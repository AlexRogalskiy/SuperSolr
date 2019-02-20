/*
 * The MIT License
 *
 * Copyright 2019 WildBees Labs, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.wildbeeslabs.sensiblemetrics.supersolr.utility;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Objects;

/**
 * Custom security utilities implementation
 */
@Slf4j
@UtilityClass
public class SecurityUtils {

    /**
     * Configures the Spring Security {@link SecurityContext} to be authenticated as the user with the given username and
     * password as well as the given granted authorities.
     *
     * @param username must not be {@literal null} or empty.
     * @param password must not be {@literal null} or empty.
     * @param roles
     */
    public static void runAs(final String username, final String password, final String... roles) {
        Objects.requireNonNull(username, "Username must not be null!");
        Objects.requireNonNull(password, "Password must not be null!");
        Objects.requireNonNull(roles, "Roles must not be null!");

        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(username, password, AuthorityUtils.createAuthorityList(roles))
        );
    }
}
