# Stage 1: Build the application
FROM node:20-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm install
COPY . .
RUN npm run build

# Stage 2: Run the application
FROM node:20-alpine
WORKDIR /app
COPY --from=builder /app ./
EXPOSE 5173
ENTRYPOINT ["npm", "run", "preview", "--", "--host"]