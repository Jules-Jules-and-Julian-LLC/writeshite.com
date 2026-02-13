FROM node:18-alpine
WORKDIR /app
COPY nostr-relay.js .
EXPOSE 7777
CMD ["node", "nostr-relay.js"]
