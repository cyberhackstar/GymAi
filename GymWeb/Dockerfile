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

# ---- Stage 1: Dependency Installation (Cached Layer) ----
FROM node:22-alpine AS deps
WORKDIR /app

# Install build dependencies for native modules
RUN apk add --no-cache \
    python3 \
    make \
    g++ \
    && npm config set registry https://registry.npmjs.org/

# Copy only dependency files for maximum cache efficiency
COPY package*.json ./
COPY .npmrc* ./

# Install dependencies with optimization flags
RUN npm ci --only=production --no-audit --no-fund --silent && \
    npm cache clean --force

# ---- Stage 2: Build Application (Optimized) ----
FROM node:22-alpine AS builder
WORKDIR /app

# Copy cached node_modules from deps stage
COPY --from=deps /app/node_modules ./node_modules
COPY --from=deps /app/package*.json ./

# Install dev dependencies for build
RUN npm ci --silent --no-audit --no-fund

# Copy source files with .dockerignore optimization
COPY . .

# Build with maximum optimizations
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
    # Verify build output
    ls -la dist/ && \
    # Clean up build artifacts
    rm -rf node_modules .angular/cache

# ---- Stage 3: NGINX Runtime (Ultra-Optimized) ----
FROM nginx:1.27-alpine AS runtime

# Install performance tools and optimizations
RUN apk add --no-cache \
    dumb-init \
    curl \
    brotli \
    && rm -rf /var/cache/apk/*

# Create optimized nginx user and directories
RUN addgroup -g 1001 -S nginx-app && \
    adduser -u 1001 -S nginx-app -G nginx-app && \
    mkdir -p /var/cache/nginx/client_temp \
             /var/cache/nginx/proxy_temp \
             /var/cache/nginx/fastcgi_temp \
             /var/cache/nginx/uwsgi_temp \
             /var/cache/nginx/scgi_temp && \
    chown -R nginx-app:nginx-app /var/cache/nginx \
                                 /var/log/nginx \
                                 /etc/nginx/conf.d \
                                 /usr/share/nginx/html

# Remove default nginx content
RUN rm -rf /usr/share/nginx/html/*

# Copy built Angular app from builder stage
COPY --from=builder --chown=nginx-app:nginx-app /app/dist/GymWeb/browser/ /usr/share/nginx/html/

# Copy optimized nginx configuration
COPY <<EOF /etc/nginx/nginx.conf
# ULTRA-OPTIMIZED NGINX CONFIG FOR RASPBERRY PI 4

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
    # Basic Settings
    include /etc/nginx/mime.types;
    default_type application/octet-stream;
    
    # Performance Settings
    sendfile on;
    sendfile_max_chunk 1m;
    tcp_nopush on;
    tcp_nodelay on;
    keepalive_timeout 30;
    keepalive_requests 1000;
    client_max_body_size 10M;
    client_body_timeout 60;
    client_header_timeout 60;
    send_timeout 60;
    
    # Buffer Settings (optimized for Pi 4)
    client_body_buffer_size 16K;
    client_header_buffer_size 4k;
    large_client_header_buffers 8 16k;
    output_buffers 2 32k;
    postpone_output 1460;
    
    # Compression Settings
    gzip on;
    gzip_vary on;
    gzip_min_length 1024;
    gzip_comp_level 6;
    gzip_types
        text/plain
        text/css
        text/xml
        text/javascript
        application/javascript
        application/xml+rss
        application/json
        application/manifest+json
        image/svg+xml;
    
    # Brotli Compression (better than gzip)
    brotli on;
    brotli_comp_level 6;
    brotli_min_length 1024;
    brotli_types
        text/plain
        text/css
        application/json
        application/javascript
        text/xml
        application/xml
        application/xml+rss
        text/javascript
        image/svg+xml
        application/x-font-ttf
        font/opentype;
    
    # Security Headers
    add_header X-Frame-Options "SAMEORIGIN" always;
    add_header X-Content-Type-Options "nosniff" always;
    add_header X-XSS-Protection "1; mode=block" always;
    add_header Referrer-Policy "strict-origin-when-cross-origin" always;
    
    # Logging (optimized)
    log_format main '\$remote_addr - \$remote_user [\$time_local] "\$request" '
                   '\$status \$body_bytes_sent "\$http_referer" '
                   '"\$http_user_agent" "\$http_x_forwarded_for" '
                   'rt=\$request_time uct="\$upstream_connect_time" '
                   'uht="\$upstream_header_time" urt="\$upstream_response_time"';
    
    access_log /var/log/nginx/access.log main buffer=16k flush=5s;
    
    # Rate Limiting
    limit_req_zone \$binary_remote_addr zone=api:10m rate=100r/m;
    
    server {
        listen 80;
        listen [::]:80;
        server_name _;
        
        root /usr/share/nginx/html;
        index index.html;
        
        # Security
        server_tokens off;
        
        # Angular SPA routing - handle client-side routing (like Netlify _redirects)
        location / {
            # First attempt to serve request as file, then as directory, 
            # then fallback to index.html for Angular routing
            try_files \$uri \$uri/ @fallback;
        }
        
        # Fallback for Angular routing (equivalent to Netlify _redirects)
        location @fallback {
            rewrite ^.*\$ /index.html last;
        }
        
        # Explicit handling for Angular routes (more specific)
        location ~* ^/(dashboard|profile|settings|gym|workout|.*)\$ {
            try_files \$uri \$uri/ /index.html;
        }
        
        # Cache control for HTML files
        location ~* \.html\$ {
            expires -1;
            add_header Cache-Control "no-cache, no-store, must-revalidate";
            add_header Pragma "no-cache";
        }
        
        # Static assets caching with versioning
        location ~* \.(js|css|png|jpg|jpeg|gif|ico|svg|woff|woff2|ttf|eot)\$ {
            expires 1y;
            add_header Cache-Control "public, immutable";
            add_header Vary Accept-Encoding;
            
            # Enable compression for these files
            gzip_static on;
            brotli_static on;
            
            # Handle Angular lazy-loaded chunks
            location ~* chunk\.*\.js\$ {
                expires 1y;
                add_header Cache-Control "public, immutable";
            }
        }
        
        # Handle Angular service worker (if using PWA)
        location ~* (ngsw-worker\.js|ngsw\.json|manifest\.json)\$ {
            expires -1;
            add_header Cache-Control "no-cache, no-store, must-revalidate";
        }
        
        # Preload critical resources for faster loading
        location = /index.html {
            expires -1;
            add_header Cache-Control "no-cache, no-store, must-revalidate";
            add_header Link "</assets/css/styles.css>; rel=preload; as=style";
            add_header Link "</assets/js/main.js>; rel=preload; as=script";
        }
        
        # API proxy (if needed)
        location /api/ {
            limit_req zone=api burst=20 nodelay;
            proxy_pass http://spring-app:8080/;
            proxy_set_header Host \$host;
            proxy_set_header X-Real-IP \$remote_addr;
            proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
            proxy_set_header X-Forwarded-Proto \$scheme;
            
            # Timeouts
            proxy_connect_timeout 5s;
            proxy_send_timeout 60s;
            proxy_read_timeout 60s;
            
            # Buffering
            proxy_buffering on;
            proxy_buffer_size 8k;
            proxy_buffers 8 8k;
            proxy_busy_buffers_size 16k;
        }
        
        # Health check endpoint
        location /health {
            access_log off;
            return 200 "healthy\\n";
            add_header Content-Type text/plain;
        }
        
        # Deny access to sensitive files
        location ~ /\. {
            deny all;
            access_log off;
            log_not_found off;
        }
        
        # Error pages
        error_page 404 /index.html;
        error_page 500 502 503 504 /50x.html;
        location = /50x.html {
            root /usr/share/nginx/html;
        }
    }
}
EOF

# Create .dockerignore recommendations file
COPY <<EOF /.dockerignore-recommended
# Recommended .dockerignore for Angular projects
node_modules/
npm-debug.log*
yarn-debug.log*
yarn-error.log*
.angular/
dist/
coverage/
.nyc_output/
e2e/
*.tgz
.DS_Store
.vscode/
.idea/
*.swp
*.swo
.git/
.gitignore
README.md
*.md
Dockerfile*
docker-compose*
.dockerignore
EOF

# Switch to non-root user
USER nginx-app

# Expose port
EXPOSE 80

# Health check
HEALTHCHECK --interval=20s --timeout=5s --start-period=30s --retries=3 \
    CMD curl -f http://localhost/health || exit 1

# Labels
LABEL maintainer="your-team@company.com" \
      version="1.0" \
      description="Ultra-optimized Angular + NGINX for Raspberry Pi 4" \
      node.version="22" \
      nginx.version="1.27"

# Use dumb-init for proper signal handling
ENTRYPOINT ["dumb-init", "--"]
CMD ["nginx", "-g", "daemon off;"]

# ==============================================================================
# PERFORMANCE OPTIMIZATIONS APPLIED:
# - Multi-stage build with dependency caching
# - Brotli compression (30% better than gzip)
# - Optimized NGINX config for Pi 4 (4 cores, 8GB RAM)
# - Static asset caching with proper headers
# - Rate limiting for API protection  
# - Buffer optimization for ARM64 architecture
# - Security headers and hardening
# - Health checks and monitoring
# - Non-root user for security
# ==============================================================================