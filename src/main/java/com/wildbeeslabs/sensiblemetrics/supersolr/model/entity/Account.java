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
package com.wildbeeslabs.sensiblemetrics.supersolr.model.entity;

import com.wildbeeslabs.sensiblemetrics.supersolr.model.iface.PersistableAccount;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.iface.PersistableBaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.BatchSize;

import javax.persistence.*;
import java.util.*;

/**
 * Account model {@link BaseModel}
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity(name = PersistableAccount.MODEL_ID)
@BatchSize(size = 10)
@Table(name = PersistableAccount.TABlE_NAME, catalog = "auth")
@AttributeOverrides({
    @AttributeOverride(name = PersistableBaseModel.ID_FIELD_NAME, column = @Column(name = PersistableAccount.ID_FIELD_NAME, unique = true, nullable = false))
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Account extends BaseModel<Long> implements PersistableAccount {

    /**
     * Default explicit serialVersionUID for interoperability
     */
    private static final long serialVersionUID = -7537326168433942579L;

    @Column(name = USERNAME_FIELD_NAME)
    private String username;

    @Column(name = PASSWORD_FIELD_NAME)
    private String password;

    @Column(name = ENABLED_FIELD_NAME)
    private boolean enabled;

    @ManyToMany(mappedBy = ACCOUNTS_REF_FIELD_NAME, fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private final Set<Role> roles = new HashSet<>();

    public void setRoles(final Collection<? extends Role> roles) {
        this.getRoles().clear();
        Optional.ofNullable(roles)
            .orElseGet(Collections::emptyList)
            .forEach(this::addRole);
    }

    public void addRole(final Role role) {
        if (Objects.nonNull(role)) {
            this.getRoles().add(role);
        }
    }
}
