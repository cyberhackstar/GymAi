@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private RestTemplate restTemplate; // To call auth-service

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            boolean isValid = restTemplate.postForObject("http://auth-service/api/auth/validate", token, Boolean.class);
            if (!Boolean.TRUE.equals(isValid)) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return;
            }
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing Token");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
