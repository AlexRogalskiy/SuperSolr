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
package com.wildbeeslabs.sensiblemetrics.supersolr.service.impl;

import com.wildbeeslabs.sensiblemetrics.supersolr.model.Account;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.Role;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.AccountService;
import com.wildbeeslabs.sensiblemetrics.supersolr.service.AuthUserService;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.wildbeeslabs.sensiblemetrics.supersolr.utility.StringUtils.getStringByDelimiter;

/**
 * {@link User} service implementation
 */
@Slf4j
@EqualsAndHashCode
@ToString
@RequiredArgsConstructor
@Service(AuthUserService.SERVICE_ID)
@Transactional
public class AuthUserServiceImpl implements AuthUserService {

    @Autowired
    private final AccountService userService;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {
        final Account account = this.userService.findByUsername(username).get();
        if (Objects.isNull(account)) {
            throw new UsernameNotFoundException("Username not found");
        }
        return new User(account.getUsername(), account.getPassword(), account.isEnabled(), true, true, true, getGrantedAuthorities(account));
    }

    private List<GrantedAuthority> getGrantedAuthorities(final Account account) {
        final List<GrantedAuthority> authorities = new ArrayList<>();
        for (final Role role : account.getRoles()) {
            if (role.isEnabled()) {
                authorities.add(new SimpleGrantedAuthority(getStringByDelimiter(DEFAULT_ROLE_DELIMITER, DEFAULT_ROLE_PREFIX, role.getCode())));
            }
        }
        return authorities;
    }
}
