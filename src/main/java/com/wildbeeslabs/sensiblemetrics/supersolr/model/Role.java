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
package com.wildbeeslabs.sensiblemetrics.supersolr.model;

import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.PersistableAccount;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.PersistableBaseInfoModel;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces.PersistableRole;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.*;
import java.util.*;

/**
 * Role model {@link BaseInfoModel}
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Entity(name = PersistableRole.MODEL_ID)
@BatchSize(size = 10)
@Table(name = PersistableRole.TABlE_NAME, catalog = "auth")
@AttributeOverrides({
    @AttributeOverride(name = PersistableBaseInfoModel.ID_FIELD_NAME, column = @Column(name = PersistableRole.ID_FIELD_NAME, unique = true, nullable = false))
})
@Inheritance(strategy = InheritanceType.JOINED)
public class Role extends BaseInfoModel<Long> implements PersistableRole {

    @Column(name = CODE_FIELD_NAME)
    private String code;

    @Column(name = ENABLED_FIELD_NAME)
    private boolean enabled;

    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
        name = ROLE_ACCOUNT_TABLE_NAME,
        joinColumns = {
            @JoinColumn(name = PersistableRole.ROLE_ID_FIELD_NAME, referencedColumnName = PersistableRole.ID_FIELD_NAME)
        },
        inverseJoinColumns = {
            @JoinColumn(name = PersistableAccount.ACCOUNT_ID_FIELD_NAME, referencedColumnName = PersistableAccount.ID_FIELD_NAME)
        }
    )
    @Fetch(FetchMode.SELECT)
    @OnDelete(action = OnDeleteAction.NO_ACTION)
    private final Set<Account> accounts = new HashSet<>();

    public void setAccounts(final Collection<? extends Account> accounts) {
        this.getAccounts().clear();
        Optional.ofNullable(accounts)
            .orElseGet(Collections::emptyList)
            .forEach(this::addAccount);
    }

    public void addAccount(final Account account) {
        if (Objects.nonNull(account)) {
            this.getAccounts().add(account);
        }
    }
}
