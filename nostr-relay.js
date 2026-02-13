// Minimal Nostr relay (NIP-01) for Trystero WebRTC signaling.
// Events are ephemeral (pruned after 5 min). No signature verification.

const http = require('http');
const { WebSocketServer } = require('ws');

const PORT = 7777;
const EVENT_TTL = 5 * 60 * 1000;

const clients = new Set();      // Set<WebSocket>
const subs = new Map();         // ws -> Map<subId, filters[]>
const events = [];              // { event, time }

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

function handleMessage(ws, raw) {
    let msg;
    try { msg = JSON.parse(raw); } catch { return; }

    if (msg[0] === 'EVENT') {
        const event = msg[1];
        events.push({ event, time: Date.now() });
        ws.send(JSON.stringify(['OK', event.id, true, '']));
        for (const other of clients) {
            if (other === ws || other.readyState !== 1) continue;
            const otherSubs = subs.get(other);
            if (!otherSubs) continue;
            for (const [subId, filters] of otherSubs) {
                if (matchesAny(event, filters)) {
                    other.send(JSON.stringify(['EVENT', subId, event]));
                }
            }
        }
    } else if (msg[0] === 'REQ') {
        const subId = msg[1];
        const filters = msg.slice(2);
        let mySubs = subs.get(ws);
        if (!mySubs) { mySubs = new Map(); subs.set(ws, mySubs); }
        mySubs.set(subId, filters);
        for (const { event } of events) {
            if (matchesAny(event, filters)) {
                ws.send(JSON.stringify(['EVENT', subId, event]));
            }
        }
        ws.send(JSON.stringify(['EOSE', subId]));
    } else if (msg[0] === 'CLOSE') {
        const mySubs = subs.get(ws);
        if (mySubs) mySubs.delete(msg[1]);
    }
}

function removeClient(ws) {
    clients.delete(ws);
    subs.delete(ws);
}

// --- HTTP server + WebSocket ---

const server = http.createServer((req, res) => {
    res.writeHead(200);
    res.end('nostr relay');
});

const wss = new WebSocketServer({ server });

wss.on('connection', (ws) => {
    clients.add(ws);
    ws.on('message', (data) => handleMessage(ws, data.toString()));
    ws.on('close', () => removeClient(ws));
    ws.on('error', () => removeClient(ws));
});

// Prune old events every 60s
setInterval(() => {
    const cutoff = Date.now() - EVENT_TTL;
    while (events.length && events[0].time < cutoff) events.shift();
}, 60000);

server.listen(PORT, () => {
    console.log('Nostr relay listening on port ' + PORT);
});
