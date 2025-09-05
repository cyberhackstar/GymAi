// package com.gymai.auth_service.config;

// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.CorsConfigurationSource;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.filter.CorsFilter;
// import org.springframework.web.servlet.config.annotation.CorsRegistry;
// import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// import lombok.extern.slf4j.Slf4j;

// import java.util.Arrays;

// @Configuration
// @Slf4j
// public class CorsConfig implements WebMvcConfigurer {

// @Override
// public void addCorsMappings(CorsRegistry registry) {
// log.info("Adding CORS mappings");
// registry.addMapping("/**")
// .allowedOriginPatterns(
// "https://*.neelahouse.cloud",
// "https://*.netlify.app",
// "https://*.vercel.app",
// "http://localhost:*",
// "http://127.0.0.1:*")
// .allowedOrigins(
// "http://localhost:4200",
// "http://localhost:3000",
// "https://gymaibybhawesh.netlify.app",
// "https://gymai.neelahouse.cloud",
// "https://gym-ai.vercel.app")
// .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH", "HEAD")
// .allowedHeaders("*")
// .exposedHeaders("Authorization", "Content-Type", "X-Total-Count")
// .allowCredentials(true)
// .maxAge(3600);
// }

// @Bean
// public CorsFilter corsFilter() {
// log.info("Creating global CORS filter");
// CorsConfiguration config = new CorsConfiguration();

// // Allow specific origins
// config.setAllowedOrigins(Arrays.asList(
// "http://localhost:4200",
// "http://localhost:3000",
// "https://gymaibybhawesh.netlify.app",
// "https://gymai.neelahouse.cloud",
// "https://gym-ai.vercel.app",
// "https://auth-service-gymai.neelahouse.cloud"));

// // Allow origin patterns for development and subdomains
// config.setAllowedOriginPatterns(Arrays.asList(
// "https://*.neelahouse.cloud",
// "https://*.netlify.app",
// "https://*.vercel.app",
// "http://localhost:*",
// "http://127.0.0.1:*"));

// // Allow all methods
// config.setAllowedMethods(Arrays.asList("*"));

// // Allow all headers
// config.setAllowedHeaders(Arrays.asList("*"));

// // Expose useful headers
// config.setExposedHeaders(Arrays.asList("Authorization", "Content-Type",
// "X-Total-Count"));

// // Allow credentials
// config.setAllowCredentials(true);

// // Cache preflight for 1 hour
// config.setMaxAge(3600L);

// UrlBasedCorsConfigurationSource source = new
// UrlBasedCorsConfigurationSource();
// source.registerCorsConfiguration("/**", config);

// return new CorsFilter(source);
// }

// @Bean
// public CorsConfigurationSource corsConfigurationSource() {
// log.info("Creating CORS configuration source");
// CorsConfiguration configuration = new CorsConfiguration();

// // Set allowed origins
// configuration.setAllowedOrigins(Arrays.asList(
// "http://localhost:4200",
// "http://localhost:3000",
// "https://gymaibybhawesh.netlify.app",
// "https://gymai.neelahouse.cloud",
// "https://gym-ai.vercel.app",
// "https://auth-service-gymai.neelahouse.cloud"));

// // Set allowed origin patterns for flexible matching
// configuration.setAllowedOriginPatterns(Arrays.asList(
// "https://*.neelahouse.cloud",
// "https://*.netlify.app",
// "https://*.vercel.app",
// "http://localhost:*",
// "http://127.0.0.1:*"));

// // Set allowed methods
// configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE",
// "OPTIONS", "PATCH", "HEAD"));

// // Set allowed headers
// configuration.setAllowedHeaders(Arrays.asList("*"));

// // Set exposed headers
// configuration.setExposedHeaders(Arrays.asList("Authorization",
// "Content-Type", "X-Total-Count"));

// // Allow credentials
// configuration.setAllowCredentials(true);

// // Set max age for preflight
// configuration.setMaxAge(3600L);

// UrlBasedCorsConfigurationSource source = new
// UrlBasedCorsConfigurationSource();
// source.registerCorsConfiguration("/**", configuration);

// return source;
// }
// }