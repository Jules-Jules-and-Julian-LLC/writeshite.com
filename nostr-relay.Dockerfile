FROM node:18-alpine
WORKDIR /app
COPY nostr-relay-package.json package.json
RUN npm install --production
COPY nostr-relay.js .
EXPOSE 7777
CMD ["node", "nostr-relay.js"]
