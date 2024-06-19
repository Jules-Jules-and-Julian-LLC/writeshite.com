#!/bin/bash

# Build and push backend image
./gradlew jib --image=julesjulesandjulian/writeshite-backend:latest

# Build frontend
(cd src/frontend || exit;
npm install
npm run build)

# Build and push frontend image
docker build -t julesjulesandjulian/writeshite-frontend:latest .
docker push julesjulesandjulian/writeshite-frontend:latest

ssh root@julian-server.local

git clone https://github.com/Jules-Jules-and-Julian-LLC/writeshite.com.git writeshite
cd writeshite/docker || exit
docker-compose pull
docker-compose up -d
