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
package com.wildbeeslabs.sensiblemetrics.supersolr.model.constraint.validator;

import com.wildbeeslabs.sensiblemetrics.supersolr.model.BaseModel;
import com.wildbeeslabs.sensiblemetrics.supersolr.model.constraint.ChronologicalDates;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Objects;

/**
 * Chronological dates constraint validation implementation
 *
 * @version 1.0.0
 * @since 2017-08-08
 */
public class ChronologicalDatesValidator implements ConstraintValidator<ChronologicalDates, BaseModel<?>> {

    @Override
    public void initialize(final ChronologicalDates constraintAnnotation) {
    }

    @Override
    public boolean isValid(final BaseModel<?> baseEntity, final ConstraintValidatorContext context) {
        if (Objects.isNull(baseEntity.getChanged())) {
            return true;
        }
        boolean isValid = baseEntity.getCreated().getTime() <= baseEntity.getChanged().getTime();
        if (!isValid) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate(String.format("ERROR: incorrect entity chronological dates: created={%s}, changed={%s} (expected dates: created <= changed)", baseEntity.getCreated(), baseEntity.getChanged())).addConstraintViolation();
        }
        return isValid;
    }
}
