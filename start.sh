#!/bin/sh

# Start Nginx and log output
service nginx start
NGINX_STATUS=$?
if [ $NGINX_STATUS -ne 0 ]; then
  echo "Nginx failed to start."
  cat /var/log/nginx/error.log
  exit $NGINX_STATUS
fi

# Start the Nostr relay (for julianjocquecom-bill text chat signaling)
node /app/nostr-relay.js &
NOSTR_PID=$!
echo "Nostr relay started (PID: $NOSTR_PID)"

# Start the Spring Boot application
java -jar /app/app.jar
