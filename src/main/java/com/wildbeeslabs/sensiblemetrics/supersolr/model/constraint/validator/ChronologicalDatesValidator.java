/*
 * Copyright 2012-2013 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
