FROM node:18-alpine
WORKDIR /app
COPY build /app/build
RUN npm install -g http-server
CMD ["http-server", "build", "-p", "3000"]