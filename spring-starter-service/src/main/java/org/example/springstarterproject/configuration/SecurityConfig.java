package org.example.springstarterproject.configuration;

import org.example.springstarterproject.model.User;
import org.example.springstarterproject.service.AuthenticationService;
import org.example.springstarterproject.service.TokenService;
import org.example.springstarterproject.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(RsaKeyProperties.class)
public class SecurityConfig {

    private final UserService userService;
    private final TokenService tokenService;
    private final AuthenticationService authenticationService;
    private final String redirectURL;

    public SecurityConfig(UserService userService, TokenService tokenService,
                          AuthenticationService authenticationService,
                          @Value("${app.oauth2.redirect-url:http://localhost:8080}") String redirectURL) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.authenticationService = authenticationService;
        this.redirectURL = redirectURL;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.oauth2Login(
                oauth2 -> oauth2.successHandler(oAuth2AuthenticationSuccessHandler())
        );

        http.csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/v1/auth/**").anonymous()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth -> oauth.jwt(Customizer.withDefaults()))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler() {
        return ((request, response, authentication) -> {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = oAuth2User.getAttribute("email");

            User user = userService.processOAuthLogin(email);

            String accessToken = tokenService.generateAccessToken(authentication);
            String refreshToken = tokenService.generateRefreshToken(authentication);

            response.addHeader(HttpHeaders.SET_COOKIE, authenticationService.createRefreshTokenCookie(refreshToken).toString());

            //Here we should redirect the user to the success url of the frontend
            response.sendRedirect(redirectURL + "/login-success?token=" + accessToken);
        });
    }

}
