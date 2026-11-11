// Reef Keeper - CIS045-3 coursework.
// Assisted by claude AI
package com.reefkeeper.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Set;

/**
 * Who may see what, and how players prove who they are.
 *
 * Every request passes through Spring Security's filter chain before it reaches a controller.
 * This class configures that chain:
 *   1. Pages anyone may open (login, register, their CSS/JS). Everything else needs a login.
 *   2. Form login: the login page POSTs username + password to /login; Spring Security checks
 *      them (using AccountUserDetailsService and the PasswordEncoder), creates a session and
 *      sends back the JSESSIONID cookie. From then on the cookie identifies the player.
 *   3. Logout: POST /logout ends the session and deletes the cookie.
 *   4. CSRF protection: every POST must carry a secret token that only our own pages can read,
 *      so another website can't make a logged-in player's browser send requests to us.
 *   5. API calls without a login get "401 Unauthorized" instead of the HTML login page, so the
 *      JavaScript can notice and go to the login page itself.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    /** Pages and files that work without logging in. */
    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/login.html", "/register.html", "/auth.css", "/style.css", "/account.js",
            "/api/register", "/favicon.ico", "/error");

    /** bcrypt: a slow, salted hash made for passwords. Used both to store and to check them. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        RequestMatcher publicPaths = request -> PUBLIC_PATHS.contains(request.getRequestURI());
        RequestMatcher apiCalls = request -> request.getRequestURI().startsWith("/api/");
        RequestMatcher h2Console = request -> request.getRequestURI().startsWith("/h2-console");

        http
            // 1. Access rules
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(publicPaths).permitAll()
                    .anyRequest().authenticated())

            // 2. Login with our own page
            .formLogin(form -> form
                    .loginPage("/login.html")
                    .loginProcessingUrl("/login")
                    .defaultSuccessUrl("/", true)
                    .failureUrl("/login.html?error")
                    .permitAll())

            // 3. Logout
            .logout(logout -> logout
                    .logoutUrl("/logout")
                    .logoutSuccessUrl("/login.html?logout")
                    .deleteCookies("JSESSIONID"))

            // 4. CSRF: the token travels in a cookie (XSRF-TOKEN) that our JavaScript reads and sends
            //    back in a form field (_csrf) or a header (X-XSRF-TOKEN). Another site can't read it.
            .csrf(csrf -> csrf
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                    .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                    .ignoringRequestMatchers(h2Console))
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class)

            // The H2 database console runs inside frames; allow that for our own pages only.
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

            // 5. Not logged in + API call = 401, not a redirect to the HTML login page
            .exceptionHandling(ex -> ex
                    .defaultAuthenticationEntryPointFor(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED), apiCalls));

        return http.build();
    }
}
