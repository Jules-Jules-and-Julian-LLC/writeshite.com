// Minimal Nostr relay (NIP-01) for Trystero WebRTC signaling.
// Zero dependencies — raw WebSocket over Node's built-in http + crypto.
// Events are ephemeral (pruned after 5 min). No signature verification.

const http = require('http');
const crypto = require('crypto');

const PORT = 7777;
const EVENT_TTL = 5 * 60 * 1000;

const clients = new Set();      // Set<ws-like object>
const subs = new Map();         // client -> Map<subId, filters[]>
const events = [];              // { event, time }

// --- WebSocket frame helpers ---

function parseFrames(buffer) {
    const frames = [];
    let offset = 0;
    while (offset < buffer.length) {
        if (buffer.length - offset < 2) break;
        const byte1 = buffer[offset];
        const byte2 = buffer[offset + 1];
        const opcode = byte1 & 0x0f;
        const masked = (byte2 & 0x80) !== 0;
        let payloadLen = byte2 & 0x7f;
        offset += 2;

        if (payloadLen === 126) {
            if (buffer.length - offset < 2) break;
            payloadLen = buffer.readUInt16BE(offset);
            offset += 2;
        } else if (payloadLen === 127) {
            if (buffer.length - offset < 8) break;
            payloadLen = Number(buffer.readBigUInt64BE(offset));
            offset += 8;
        }

        let maskKey = null;
        if (masked) {
            if (buffer.length - offset < 4) break;
            maskKey = buffer.slice(offset, offset + 4);
            offset += 4;
        }

        if (buffer.length - offset < payloadLen) break;
        const payload = buffer.slice(offset, offset + payloadLen);
        offset += payloadLen;

        if (masked) {
            for (let i = 0; i < payload.length; i++) {
                payload[i] ^= maskKey[i & 3];
            }
        }

        frames.push({ opcode, payload });
    }
    return { frames, consumed: offset };
}

function sendFrame(socket, data) {
    const payload = Buffer.from(data, 'utf8');
    let header;
    if (payload.length < 126) {
        header = Buffer.alloc(2);
        header[0] = 0x81; // FIN + text
        header[1] = payload.length;
    } else if (payload.length < 65536) {
        header = Buffer.alloc(4);
        header[0] = 0x81;
        header[1] = 126;
        header.writeUInt16BE(payload.length, 2);
    } else {
        header = Buffer.alloc(10);
        header[0] = 0x81;
        header[1] = 127;
        header.writeBigUInt64BE(BigInt(payload.length), 2);
    }
    socket.write(Buffer.concat([header, payload]));
}

function sendPong(socket, payload) {
    let header;
    if (payload.length < 126) {
        header = Buffer.alloc(2);
        header[0] = 0x8a; // FIN + pong
        header[1] = payload.length;
    } else {
        header = Buffer.alloc(4);
        header[0] = 0x8a;
        header[1] = 126;
        header.writeUInt16BE(payload.length, 2);
    }
    socket.write(Buffer.concat([header, payload]));
}

// --- Nostr protocol ---

function matchesAny(event, filters) {
    return filters.some(f => matchesFilter(event, f));
}

function matchesFilter(event, f) {
    if (f.ids && !f.ids.some(id => event.id.startsWith(id))) return false;
    if (f.authors && !f.authors.some(a => event.pubkey.startsWith(a))) return false;
    if (f.kinds && !f.kinds.includes(event.kind)) return false;
    if (f.since && event.created_at < f.since) return false;
    if (f.until && event.created_at > f.until) return false;
    for (const key of Object.keys(f)) {
        if (key[0] === '#' && key.length === 2) {
            const tagName = key[1];
            const wanted = f[key];
            const have = (event.tags || []).filter(t => t[0] === tagName).map(t => t[1]);
            if (!wanted.some(v => have.includes(v))) return false;
        }
    }
    return true;
}

function handleMessage(client, raw) {
    let msg;
    try { msg = JSON.parse(raw); } catch { return; }

    if (msg[0] === 'EVENT') {
        const event = msg[1];
        events.push({ event, time: Date.now() });
        sendFrame(client.socket, JSON.stringify(['OK', event.id, true, '']));
        for (const other of clients) {
            if (other === client || other.socket.destroyed) continue;
            const otherSubs = subs.get(other);
            if (!otherSubs) continue;
            for (const [subId, filters] of otherSubs) {
                if (matchesAny(event, filters)) {
                    sendFrame(other.socket, JSON.stringify(['EVENT', subId, event]));
                }
            }
        }
    } else if (msg[0] === 'REQ') {
        const subId = msg[1];
        const filters = msg.slice(2);
        let mySubs = subs.get(client);
        if (!mySubs) { mySubs = new Map(); subs.set(client, mySubs); }
        mySubs.set(subId, filters);
        for (const { event } of events) {
            if (matchesAny(event, filters)) {
                sendFrame(client.socket, JSON.stringify(['EVENT', subId, event]));
            }
        }
        sendFrame(client.socket, JSON.stringify(['EOSE', subId]));
    } else if (msg[0] === 'CLOSE') {
        const mySubs = subs.get(client);
        if (mySubs) mySubs.delete(msg[1]);
    }
}

function removeClient(client) {
    clients.delete(client);
    subs.delete(client);
}

// --- HTTP server with WebSocket upgrade ---

const server = http.createServer((req, res) => {
    res.writeHead(200);
    res.end('nostr relay');
});

server.on('upgrade', (req, socket, head) => {
    const key = req.headers['sec-websocket-key'];
    if (!key) { socket.destroy(); return; }

    const accept = crypto.createHash('sha1')
        .update(key + '258EAFA5-E914-47DA-95CA-5AB5DC11E65A')
        .digest('base64');

    socket.write(
        'HTTP/1.1 101 Switching Protocols\r\n' +
        'Upgrade: websocket\r\n' +
        'Connection: Upgrade\r\n' +
        'Sec-WebSocket-Accept: ' + accept + '\r\n\r\n'
    );

    const client = { socket };
    clients.add(client);
    let buf = Buffer.alloc(0);

    socket.on('data', (data) => {
        buf = Buffer.concat([buf, data]);
        const { frames, consumed } = parseFrames(buf);
        buf = buf.slice(consumed);

        for (const frame of frames) {
            if (frame.opcode === 0x1) {             // text
                handleMessage(client, frame.payload.toString('utf8'));
            } else if (frame.opcode === 0x9) {      // ping
                sendPong(socket, frame.payload);
            } else if (frame.opcode === 0x8) {      // close
                removeClient(client);
                socket.end();
            }
        }
    });

    socket.on('close', () => removeClient(client));
    socket.on('error', () => removeClient(client));
});

// Prune old events every 60s
setInterval(() => {
    const cutoff = Date.now() - EVENT_TTL;
    while (events.length && events[0].time < cutoff) events.shift();
}, 60000);

server.listen(PORT, () => {
    console.log('Nostr relay listening on port ' + PORT);
});
