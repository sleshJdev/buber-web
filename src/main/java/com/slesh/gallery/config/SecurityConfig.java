package com.slesh.gallery.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slesh.gallery.auth.AuthRequest;
import com.slesh.gallery.persistence.model.ApplicationUser;
import com.slesh.gallery.persistence.repository.ApplicationUserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final long tokenExpirationTime;
    private final String tokenHeader;
    private final String tokenPrefix;
    private final String secret;
    private final ApplicationUserRepository applicationUserRepository;
    private final ObjectMapper objectMapper;

    public SecurityConfig(@Value("${app.security.token-expiration-time}") long tokenExpirationTime,
                          @Value("${app.security.token-header}") String tokenHeader,
                          @Value("${app.security.token-prefix}") String tokenPrefix,
                          @Value("${app.security.secret-key}") String secret,
                          ApplicationUserRepository applicationUserRepository,
                          ObjectMapper objectMapper) {
        this.tokenExpirationTime = tokenExpirationTime;
        this.tokenHeader = tokenHeader;
        this.tokenPrefix = tokenPrefix;
        this.secret = secret;
        this.applicationUserRepository = applicationUserRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return key/*id/username/email*/ -> {
            Example<ApplicationUser> example = Example.of(
                new ApplicationUser(key, key, null, null),
                ExampleMatcher.matchingAny());
            return applicationUserRepository.findOne(example)
                .map(user -> new User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getRoles().stream()
                        .map(Enum::toString)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())))
                .orElseThrow(() -> new UsernameNotFoundException("User with key " + key + " not found"));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.cors().and()
            .csrf().disable()
            .authorizeRequests()
            .antMatchers(HttpMethod.POST, "/api/auth/sign-up").permitAll()
            .antMatchers(HttpMethod.GET, "/api/ads/cities").permitAll()
            .antMatchers(HttpMethod.GET, "/api/ads/picture/*").permitAll()
            .regexMatchers(HttpMethod.GET, "/api/ads/[^/]+$").permitAll()
            .antMatchers(HttpMethod.GET, "/api/ads").permitAll()
            .antMatchers("/api/**").authenticated().and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .addFilterAt(new TokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
            .addFilterAt(new TokenAuthorizationFilter(), BasicAuthenticationFilter.class);
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", new CorsConfiguration().applyPermitDefaultValues());
        return source;
    }

    private class TokenAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
        TokenAuthenticationFilter() throws Exception {
            super(new AndRequestMatcher(
                new AntPathRequestMatcher("/api/auth/sign-in", HttpMethod.POST.name()),
                new MediaTypeRequestMatcher(new HeaderContentNegotiationStrategy(), MediaType.APPLICATION_JSON_UTF8)
            ));
            setAuthenticationManager(authenticationManager());
        }

        @Override
        public Authentication attemptAuthentication(HttpServletRequest request,
                                                    HttpServletResponse response)
            throws AuthenticationException, IOException {
            AuthRequest signInRequest = objectMapper.readValue(request.getInputStream(), AuthRequest.class);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                signInRequest.getUsername(), signInRequest.getPassword());

            authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

            return this.getAuthenticationManager().authenticate(authRequest);
        }

        @Override
        protected void successfulAuthentication(HttpServletRequest req,
                                                HttpServletResponse res,
                                                FilterChain chain,
                                                Authentication auth) {
            String token = Jwts.builder()
                .setSubject(((User) auth.getPrincipal()).getUsername())
                .setExpiration(Date.from(Instant.now().plusMillis(tokenExpirationTime)))
                .signWith(SignatureAlgorithm.HS512, secret.getBytes())
                .compact();
            res.addHeader(tokenHeader, tokenPrefix + token);
        }
    }

    private class TokenAuthorizationFilter extends OncePerRequestFilter {
        @Override
        protected void doFilterInternal(HttpServletRequest req,
                                        HttpServletResponse res,
                                        FilterChain chain)
            throws ServletException, IOException {

            String header = req.getHeader(tokenHeader);
            if (header == null || !header.startsWith(tokenPrefix)) {
                chain.doFilter(req, res);
                return;
            }

            UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            chain.doFilter(req, res);
        }

        private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest req) {
            return Optional.ofNullable(req.getHeader(tokenHeader))
                .map(token -> Jwts.parser()
                    .setSigningKey(secret.getBytes())
                    .parseClaimsJws(token.replace(tokenPrefix, ""))
                    .getBody()
                    .getSubject())
                .map(user -> new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>()))
                .orElse(null);
        }
    }
}
