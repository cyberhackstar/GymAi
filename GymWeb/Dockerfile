# # ---------- Build Stage ----------
# FROM node:22.18.0-alpine as builder

# WORKDIR /app

# # Copy package files and install dependencies
# COPY package*.json ./
# RUN npm ci

# # Copy all source files
# COPY . .

# # Build Angular app in production mode
# RUN npm run build -- --configuration production

# # ---------- NGINX Stage ----------
# FROM nginx:1.27-alpine

# # Remove default NGINX static files
# RUN rm -rf /usr/share/nginx/html/*

# # Copy Angular production build output from builder stage
# COPY --from=builder /app/dist/GymWeb/browser/ /usr/share/nginx/html/

# # Expose port 80 for HTTP traffic
# EXPOSE 80

# # Start NGINX in foreground
# CMD ["nginx", "-g", "daemon off;"]

# ---------- Build Stage ----------
FROM node:22-alpine as builder

# Set npm memory limits for Pi
ENV NODE_OPTIONS="--max_old_space_size=2048"
ENV NPM_CONFIG_PROGRESS=false
ENV NPM_CONFIG_LOGLEVEL=warn

WORKDIR /app

# Copy package files and install dependencies
COPY package*.json ./
RUN npm ci --only=production --silent

# Copy source files
COPY . .

# Build Angular app with optimizations
RUN npm run build -- \
    --configuration=production \
    --optimization=true \
    --aot=true \
    --build-optimizer=true \
    --extract-licenses=false \
    --source-map=false

# ---------- NGINX Stage ----------
FROM nginx:1.27-alpine

# Install security updates
RUN apk update && apk upgrade && rm -rf /var/cache/apk/*

# Remove default NGINX static files
RUN rm -rf /usr/share/nginx/html/*

# Copy custom nginx configuration for SPA
COPY <<EOF /etc/nginx/conf.d/default.conf
server {
    listen 80;
    server_name _;
    root /usr/share/nginx/html;
    index index.html;

    # Gzip compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1000;
    gzip_proxied expired no-cache no-store private auth;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/javascript
        application/xml+rss
        application/json;

    # Security headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header X-Content-Type-Options "nosniff" always;

    # Cache static assets
    location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2)$ {
        expires 1y;
        add_header Cache-Control "public, immutable";
    }

    # Angular routing fallback
    location / {
        try_files \$uri \$uri/ /index.html;
    }
}
EOF

# Copy Angular production build output from builder stage
COPY --from=builder /app/dist/GymWeb/browser/ /usr/share/nginx/html/

# Add non-root user
RUN addgroup -g 101 -S nginx-user && adduser -S -D -H -u 101 -h /var/cache/nginx -s /sbin/nologin -G nginx-user nginx-user

# Set proper permissions
RUN chown -R nginx-user:nginx-user /usr/share/nginx/html && \
    chown -R nginx-user:nginx-user /var/cache/nginx

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=10s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:80/ || exit 1

# Switch to non-root user
USER nginx-user

# Expose port 80 for HTTP traffic
EXPOSE 80

# Start NGINX in foreground
CMD ["nginx", "-g", "daemon off;"]