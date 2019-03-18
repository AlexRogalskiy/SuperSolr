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
package com.wildbeeslabs.sensiblemetrics.supersolr.model.interfaces;

import java.io.Serializable;

/**
 * Persistable account model definition
 */
public interface PersistableAccount extends Serializable {

    /**
     * Default document ID
     */
    String MODEL_ID = "Account";
    /**
     * Default table name
     */
    String TABlE_NAME = "accounts";

    /**
     * Default field names
     */
    String ID_FIELD_NAME = "id";
    String ACCOUNT_ID_FIELD_NAME = "accountId";
    String USERNAME_FIELD_NAME = "username";
    String PASSWORD_FIELD_NAME = "password";
    String ENABLED_FIELD_NAME = "enabled";
    /**
     * Default reference field names
     */
    String ACCOUNTS_REF_FIELD_NAME = "accounts";
}
