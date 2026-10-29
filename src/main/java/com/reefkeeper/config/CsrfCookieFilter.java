// Reef Keeper - CIS045-3 coursework.
// Source: skeleton generated with Claude (Anthropic AI), 29 Oct 2026. Complete as given.
// Based on the CsrfCookieFilter example in the Spring Security reference guide.
package com.reefkeeper.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Makes sure the XSRF-TOKEN cookie is always sent to the browser.
 *
 * Spring Security only creates the CSRF token when something asks for it. Our pages are plain
 * HTML files, so nothing would ever ask, and the cookie would never be set. This filter asks
 * for the token on every request, which makes Spring Security write the cookie.
 * Not a @Component on purpose: SecurityConfig adds it to the security filter chain itself.
 */
final class CsrfCookieFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            csrfToken.getToken(); // loading the token writes the cookie
        }
        filterChain.doFilter(request, response);
    }
}
