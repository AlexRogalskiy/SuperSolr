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
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

    public static Object getPrincipal() {
        final Authentication authentication = getCurrentAuthentication();
        if (Objects.isNull(authentication)) {
            throw new IllegalArgumentException(String.format("ERROR: cannot read authentication params: {%s}", authentication));
        }
        return authentication.getPrincipal();
    }

    public static String getUsername() {
        final Object principal = getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return principal.toString();
    }

    public static Authentication getCurrentAuthentication() {
        final SecurityContext securityContext = SecurityContextHolder.getContext();
        if (Objects.isNull(securityContext)) {
            return null;
        }
        return securityContext.getAuthentication();
    }

    /**
     * Check if current user has specified role.
     *
     * @param privilege the role to check if user has.
     * @return true if user has specified role, otherwise false.
     */
    public static boolean hasPrivilege(final String privilege) {
        final UserDetails userDetails = (UserDetails) getPrincipal();
        if (Objects.nonNull(userDetails)) {
            return userDetails.getAuthorities().stream().anyMatch(item -> item.getAuthority().equalsIgnoreCase(privilege));
        }
        return false;
    }

    /**
     * Check if current user has any role of specified
     *
     * @param privileges the array of roles
     * @return true if has any role, otherwise false
     */
    public static boolean hasAnyPrivilege(final String... privileges) {
        final UserDetails userDetails = (UserDetails) getPrincipal();
        if (Objects.nonNull(userDetails)) {
            final Set<String> rolesSet = new HashSet<>();
            rolesSet.addAll(Arrays.asList(privileges));
            return userDetails.getAuthorities().stream().anyMatch(item -> rolesSet.contains(item.getAuthority()));
        }
        return false;
    }

    /**
     * Calculates an authorization {@link String} key for user
     *
     * @param userDetails the user details {@link UserDetails}
     * @return calculated authorization key {@link String}
     */
    public static String encode(final UserDetails userDetails) {
        Objects.requireNonNull(userDetails);
        return encode(userDetails.getUsername(), userDetails.getPassword());
    }

    /**
     * Returns formatted string value with encoded input username / password parameters
     *
     * @param username initial input username {@link String}
     * @param password - initial input password {@link String}
     * @return encoded username/password string
     */
    public static String encode(final String username, final String password) {
        final String authorizationString = String.format("%s:%s", username, password);
        try {
            return new String(Base64.getEncoder().encode(authorizationString.getBytes(StandardCharsets.UTF_8.name())));
        } catch (UnsupportedEncodingException e) {
            log.error(String.format("ERROR: cannot process authorisation parameters username: {%s}, message: {%s}", username, e.getMessage()));
        }
        return null;
    }

    /**
     * Returns coupled pair of username / password parameters {@link Pair} by input basic authorisation string
     *
     * @param basicAuthValue - initial input authorization value
     * @return coupled pair of username / password parameters {@link Pair}
     */
    public static Pair<String, String> decode(final String basicAuthValue) {
        if (Objects.isNull(basicAuthValue)) {
            return null;
        }
        try {
            final byte[] decodeBytes = Base64.getDecoder().decode(basicAuthValue.substring(basicAuthValue.indexOf(StringUtils.SPACE) + 1).getBytes(StandardCharsets.UTF_8.name()));
            final String decoded = new String(decodeBytes, StandardCharsets.UTF_8.name());
            final int indexOfDelimiter = decoded.indexOf(':');
            return new ImmutablePair<>(decoded.substring(0, indexOfDelimiter), decoded.substring(indexOfDelimiter + 1));
        } catch (UnsupportedEncodingException e) {
            log.error(String.format("ERROR: cannot process authorisation parameter: {%s}, message: {%s}", basicAuthValue, e.getMessage()));
        }
        return ImmutablePair.nullPair();
    }
}
