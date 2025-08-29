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

# Copy Angular production build output
COPY --from=builder /app/dist/GymWeb/browser/ /usr/share/nginx/html/

# Copy custom nginx.conf to support Angular routing
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Expose port 80
EXPOSE 80

# Healthcheck (optional)
HEALTHCHECK --interval=30s --timeout=10s --start-period=30s --retries=3 \
    CMD wget -q --spider http://localhost/ || exit 1

# Start NGINX in foreground
CMD ["nginx", "-g", "daemon off;"]





