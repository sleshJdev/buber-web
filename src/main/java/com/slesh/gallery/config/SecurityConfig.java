package com.slesh.gallery.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.slesh.gallery.auth.SignInRequest;
import com.slesh.gallery.persistence.model.ApplicationUser;
import com.slesh.gallery.persistence.repository.ApplicationUserRepository;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import org.springframework.web.accept.HeaderContentNegotiationStrategy;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.stream.Collectors;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final ApplicationUserRepository applicationUserRepository;
    private final ObjectMapper objectMapper;

    public SecurityConfig(ApplicationUserRepository applicationUserRepository,
                          ObjectMapper objectMapper) {
        this.applicationUserRepository = applicationUserRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    protected UserDetailsService userDetailsService() {
        return key/*id/username/email*/ -> {
            Example<ApplicationUser> example = Example.of(
                new ApplicationUser(key, key, key, null, null),
                ExampleMatcher.matchingAny()
                    .withStringMatcher(ExampleMatcher.StringMatcher.EXACT)
                    .withIgnoreNullValues());
            return applicationUserRepository.findOne(example)
                .map(user -> new User(
                    user.getUsername(),
                    user.getPassword(),
                    user.getRoles().stream()
                        .map(Enum::toString)
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList())))
                .orElseThrow(() -> new UsernameNotFoundException("User with key" + key + "not found"));
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(11);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
            .cors().disable()
            .authorizeRequests()
            .antMatchers("/api/**").authenticated().and()
            .addFilterAt(new TokenAuthFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    private class TokenAuthFilter extends AbstractAuthenticationProcessingFilter {
        TokenAuthFilter() throws Exception {
            super(new AndRequestMatcher(
                new AntPathRequestMatcher("/api/auth/login", HttpMethod.POST.name()),
                new MediaTypeRequestMatcher(new HeaderContentNegotiationStrategy(), MediaType.APPLICATION_JSON_UTF8)
            ));
            setAuthenticationManager(SecurityConfig.this.authenticationManager());
        }

        @Override
        public Authentication attemptAuthentication(HttpServletRequest request,
                                                    HttpServletResponse response)
            throws AuthenticationException, IOException, ServletException {
            SignInRequest signInRequest = objectMapper.readValue(request.getInputStream(), SignInRequest.class);
            UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                signInRequest.getUsername(), signInRequest.getPassword());

            // Allow subclasses to set the "details" property
            authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

            return this.getAuthenticationManager().authenticate(authRequest);
        }

        @Override
        protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
            super.successfulAuthentication(request, response, chain, authResult);
            objectMapper.writeValue(response.getOutputStream(), "Ok!!!!");
        }
    }
}
