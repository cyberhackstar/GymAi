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

# ==============================================================================
# ULTRA-OPTIMIZED ANGULAR + NGINX DOCKERFILE FOR RASPBERRY PI 4
# Optimizations: Multi-stage caching, compression, performance tuning
# ==============================================================================

# ==============================================================================
# ULTRA-OPTIMIZED ANGULAR + NGINX DOCKERFILE FOR RASPBERRY PI 4
# ==============================================================================

# ---- Stage 1: Build Application (Optimized) ----
FROM node:22-alpine AS builder

# Set the working directory
WORKDIR /app

# Install build dependencies for native modules and optimize npm
RUN apk add --no-cache python3 make g++ && \
    npm config set registry https://registry.npmjs.org/ && \
    npm config set unsafe-perm true

# Copy only dependency files for caching
COPY package*.json ./
COPY .npmrc* ./

# Install dependencies, including devDependencies for the build step
RUN npm ci

# Copy all source files
COPY . .

# Build with maximum optimizations and output verification
RUN npm run build -- \
    --configuration=production \
    --optimization=true \
    --aot=true \
    --build-optimizer=true \
    --common-chunk=true \
    --vendor-chunk=true \
    --extract-licenses=false \
    --source-map=false \
    --stats-json=false \
    --progress=false \
    --verbose=false && \
    # Clean up build artifacts to save space
    rm -rf node_modules .angular/cache

# ---- Stage 2: NGINX Runtime (Ultra-Optimized) ----
FROM nginx:1.27-alpine AS runtime

# Install performance tools and optimizations
# Note: Alpine's nginx package doesn't include Brotli.
# We must install the separate module package.
RUN apk add --no-cache dumb-init curl nginx-mod-http-brotli && \
    rm -rf /var/cache/apk/*

# Create optimized nginx user and directories
RUN adduser -D -g 'nginx-app' nginx-app && \
    mkdir -p /var/cache/nginx/client_temp /var/cache/nginx/proxy_temp && \
    chown -R nginx-app:nginx-app /var/cache/nginx /var/log/nginx /usr/share/nginx

# Remove default nginx content
RUN rm -rf /usr/share/nginx/html/*

# Copy built Angular app from builder stage
COPY --from=builder --chown=nginx-app:nginx-app /app/dist/GymWeb/browser/ /usr/share/nginx/html/

# Copy optimized nginx configuration
COPY <<EOF /etc/nginx/nginx.conf
# ULTRA-OPTIMIZED NGINX CONFIG FOR RASPBERRY PI 4

# Load the Brotli module. This is required because it's a separate package on Alpine.
load_module modules/ngx_http_brotli_static_module.so;
load_module modules/ngx_http_brotli_filter_module.so;

user nginx-app;
worker_processes auto;
worker_rlimit_nofile 65535;
error_log /var/log/nginx/error.log warn;
pid /var/run/nginx.pid;

# Performance optimizations
worker_cpu_affinity auto;
pcre_jit on;

events {
    worker_connections 4096;
    use epoll;
    multi_accept on;
    accept_mutex off;
}

http {
    include /etc/nginx/mime.types;
    default_type application/octet-stream;
    sendfile on;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 30;
    keepalive_requests 1000;
    client_max_body_size 10M;
    client_body_timeout 60;
    client_header_timeout 60;
    send_timeout 60;

    # Brotli Compression
    brotli on;
    brotli_comp_level 6;
    brotli_static on;
    brotli_min_length 1024;
    brotli_types
      text/plain
      text/css
      application/json
      application/javascript
      text/xml
      application/xml
      image/svg+xml;

    # Gzip fallback compression
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_comp_level 6;
    gzip_types
      text/plain
      text/css
      application/javascript
      application/json
      image/svg+xml;

    # Caching Headers
    add_header Cache-Control "no-cache, no-store, must-revalidate" always;
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    
    server {
        listen 80;
        listen [::]:80;
        server_name _;
        root /usr/share/nginx/html;
        index index.html;
        
        # Angular SPA routing
        location / {
            try_files $uri $uri/ /index.html;
        }

        # Static assets caching
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)\$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
            add_header Vary Accept-Encoding;
        }

        # Health check endpoint
        location /health {
            access_log off;
            return 200 "healthy\n";
            add_header Content-Type text/plain;
        }

        # Deny access to sensitive files
        location ~ /\. {
            deny all;
            access_log off;
            log_not_found off;
        }
    }
}
EOF

# Switch to non-root user
USER nginx-app

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=20s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -f http://localhost/health || exit 1

# Use dumb-init for proper signal handling
ENTRYPOINT ["dumb-init", "--"]
CMD ["nginx", "-g", "daemon off;"]