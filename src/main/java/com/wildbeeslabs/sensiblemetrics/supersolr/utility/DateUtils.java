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
package com.wildbeeslabs.sensiblemetrics.supersolr.utility;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.WeekFields;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Date utilities implementation
 */
@Slf4j
@UtilityClass
public class DateUtils {

    /**
     * Default date format pattern
     */
    public static final String DEFAULT_DATE_FORMAT_PATTERN_EXT = "yyyy-MM-dd HH:mm:ssZ";
    /**
     * Default date format pattern
     */
    public static final String DEFAULT_DATE_FORMAT_PATTERN = "yyyy-MM-dd HH:mm:ss";
    /**
     * Default date pattern
     */
    public static final String DEFAULT_DATE_PATTERN = "EEEEE, dd/MM/yyyy";
    /**
     * Default time pattern
     */
    public static final String DEFAULT_TIME_PATTERN = "h:mm a";

    public static Integer getDaysDiff(final Date startDate, final Date endDate) {
        if (Objects.isNull(startDate) || Objects.isNull(endDate)) {
            return null;
        }
        return Period.between(convertToLocalDate(startDate), convertToLocalDate(endDate)).getDays();
    }

    public static java.sql.Date convertToSqlDate(final Date dateToConvert) {
        return new java.sql.Date(dateToConvert.getTime());
    }

    public static LocalDate convertToLocalDate(final Date dateToConvert) {
        return convertToSqlDate(dateToConvert).toLocalDate();
    }

    public static Date asDate(final LocalDate localDate) {
        return Date.from(localDate.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Date asDate(final LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static LocalDate asLocalDate(final Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static LocalDateTime asLocalDateTime(final Date date) {
        return Instant.ofEpochMilli(date.getTime()).atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    public static Date now() {
        return asDate(LocalDate.now());
    }

    public static Date yesterday() {
        return asDate(LocalDate.now().minus(Period.ofDays(1)));
    }

    public static Date firstDayOfMonth() {
        return asDate(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()));
    }

    public static Date lastDayOfMonth() {
        return asDate(LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()));
    }

    public static int getCurrentWeek() {
        final WeekFields weekFields = WeekFields.of(Locale.getDefault());
        return LocalDate.now().get(weekFields.weekOfWeekBasedYear());
    }

    public static Date firstDayOfWeek() {
        final DateTime dateTime = new DateTime();
        return dateTime.withDayOfWeek(1).toDate();
    }

    public static Date lastDayOfWeek() {
        final DateTime dateTime = new DateTime();
        return dateTime.withDayOfWeek(7).toDate();
    }

    public static Date firstMinuteOfHour() {
        final DateTime dateTime = new DateTime();
        return dateTime.withMinuteOfHour(0).withSecondOfMinute(0).toDate();
    }

    public static Date lastMinuteOfHour() {
        final DateTime dateTime = new DateTime();
        return dateTime.withMinuteOfHour(59).withSecondOfMinute(59).toDate();
    }

    public static boolean isDateBeforeNowByPeriodInDays(final Date date, final Integer periodInDays) {
        return (Objects.nonNull(date) && periodInDays.equals(DateUtils.getDaysDiff(DateUtils.now(), date)));
    }

    public static SimpleDateFormat dateFormat() {
        return new SimpleDateFormat(DEFAULT_DATE_PATTERN, Locale.getDefault());
    }

    public static SimpleDateFormat timeFormat() {
        return new SimpleDateFormat(DEFAULT_TIME_PATTERN, Locale.getDefault());
    }

    public static boolean isInRange(final Date start, final Date end) {
        return (isInPast(start) || isInFuture(end));
    }

    public static boolean isInFuture(final Date date) {
        return (Objects.nonNull(date) && date.after(DateUtils.now()));
    }

    public static boolean isInPast(final Date date) {
        return (Objects.nonNull(date) && date.before(DateUtils.now()));
    }

    public static Date toDate(final String date, final String dateFormat, final Locale locale, final TimeZone timeZone) {
        Objects.requireNonNull(dateFormat);
        try {
            final SimpleDateFormat formatter = new SimpleDateFormat(dateFormat, locale);
            formatter.setTimeZone(timeZone);
            return formatter.parse(date);
        } catch (ParseException e) {
            log.error(String.format("ERROR: cannot parse date by format={%s}", dateFormat));
        }
        return null;
    }

    public static Date toUTCDate(final String date, final String dateFormat) {
        return toDate(date, dateFormat, Locale.getDefault(), TimeZone.getTimeZone("UTC"));
    }
}
