# ---------- Build Stage ----------
FROM node:22.18.0-alpine as builder

WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm ci

# Copy all source files
COPY . .

# Build Angular app in production mode
RUN npm run build -- --configuration production

# ---------- NGINX Stage ----------
FROM nginx:1.27-alpine

# Remove default NGINX static files
RUN rm -rf /usr/share/nginx/html/*

# Copy Angular production build output from builder stage
COPY --from=builder /app/dist/GymWeb/browser/ /usr/share/nginx/html/

# Expose port 80 for HTTP traffic
EXPOSE 80

# Start NGINX in foreground
CMD ["nginx", "-g", "daemon off;"]
