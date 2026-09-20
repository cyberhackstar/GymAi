# Cloudflare Tunnel

This stack deliberately does not terminate TLS inside Docker. Cloudflare terminates public HTTPS and the tunnel forwards plain HTTP to the loopback gateway port.

Add/keep this ingress rule in the native cloudflared configuration on the Oracle VM:

```yaml
- hostname: gymai.neelastack.com
  service: http://localhost:4001
```

The host Nginx used by the other applications is not involved because the gateway binds only to `127.0.0.1:4001`.
