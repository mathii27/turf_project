    package com.example.Trufbooking.config;

    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.security.authentication.AuthenticationProvider;
    import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
    import org.springframework.security.config.Customizer;
    import org.springframework.security.config.annotation.web.builders.HttpSecurity;
    import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
    import org.springframework.security.core.userdetails.UserDetailsService;
    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
    import org.springframework.security.web.SecurityFilterChain;
    import org.springframework.web.cors.CorsConfiguration;
    import org.springframework.web.cors.CorsConfigurationSource;
    import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
    import org.springframework.web.servlet.config.annotation.CorsRegistry;
    import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

    import java.util.List;


    @Configuration
    public class securityconfig {
        @Bean
        public BCryptPasswordEncoder bCryptPasswordEncoder() {
            return new BCryptPasswordEncoder();
        }

        @Autowired
        private UserDetailsService userDetailsService;

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
            http.csrf(AbstractHttpConfigurer::disable)
                    .cors(Customizer.withDefaults())
                    .authorizeHttpRequests(auth -> auth
                            .requestMatchers("/home/login","/home/signup","/home/locations","/home/sports","/api/google-login").permitAll()
                            .anyRequest().authenticated()
                    )
                    .oauth2Login(oauth -> oauth
                            .loginPage("/home/login")
                            .defaultSuccessUrl("/location", true)
                            //.userInfoEndpoint(userInfo -> userInfo.oidcUserService(oidcUserService()))
                            //.failureUrl("/home/login?error=true")
                    )
                    .httpBasic(Customizer.withDefaults());
                  return  http.build();
        }

        //authentication provider.

        @Bean
        public AuthenticationProvider authenticationProvider(){
            DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
            authProvider.setUserDetailsService(userDetailsService);
            authProvider.setPasswordEncoder(bCryptPasswordEncoder());
            return authProvider;
        }

        @Bean
        CorsConfigurationSource corsConfigurationSource() {
            CorsConfiguration configuration = new CorsConfiguration();
            configuration.setAllowedOrigins(List.of("http://localhost:5173"));
            configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
            configuration.setAllowCredentials(true);
            configuration.addAllowedHeader("*");
            UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
            source.registerCorsConfiguration("/**", configuration);
            return source;
        }

        @Bean
        public OidcUserService oidcUserService() {
            return new OidcUserService();
        }

        @Bean
        public WebMvcConfigurer webMvcConfigurer() {
            return new WebMvcConfigurer() {
                @Override
                public void addCorsMappings(CorsRegistry registry) {
                    registry.addMapping("/**")
                            .allowedOrigins("http://localhost:5173")
                            .allowedMethods("GET", "POST", "PUT", "DELETE")
                            .allowedHeaders("*")
                            .allowCredentials(true);
                }
            };
        }
    }


    /*
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable) // Updated method to disable CSRF
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()) // Allow all requests
                .cors(cors -> {}); // Use WebMvcConfigurer CORS settings
        return http.build();
    }
    */

    /*
    public PasswordEncoder passwordEncoder() {
            return new BCryptPasswordEncoder();
        }
     */