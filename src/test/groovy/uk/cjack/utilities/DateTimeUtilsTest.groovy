/*
 * Copyright 2021 Chris Jackson (www.cjack.uk)
 */
package uk.cjack.utilities

import org.apache.commons.lang3.time.DateUtils
import org.apache.commons.lang3.time.FastDateFormat
import org.mockito.MockedConstruction
import org.mockito.Mockito
import spock.lang.Specification
import uk.cjack.utilities.exception.InvalidDateException


/**
 * Test Class for {@link DateTimeUtils}
 *
 * @author chris 07/04/2021
 */
class DateTimeUtilsTest extends Specification {

    /* ******** *
     * Set Time *
     * ******** */

    /**
     * Test for {@link DateTimeUtils#setTime}
     */
    def "setTime should set [Hour #hour, Minute #minute] onto [Date #date] returning #expectedResult"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.setTime(date, hour, minute)) == expectedResult

        where: "The time is set onto the date"
        date                                    | hour | minute | expectedResult
        convertToDate("2021-01-01T00:00:01.00") | 12   | 15     | "2021-01-01T12:15:00"
        convertToDate("2000-01-31T00:00:59.00") | 23   | 59     | "2000-01-31T23:59:00"
        convertToDate("1999-12-31T00:00:20.00") | 00   | 01     | "1999-12-31T00:01:00"
    }

    /**
     * Test for {@link DateTimeUtils#setTime}
     */
    def "setTime should set [Hour #hour, Minute #minute, Seconds #seconds, Milliseconds #milliseconds] onto [Date #date] returning #expectedResult"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.setTime(date, hour, minute, seconds, milliseconds)) == expectedResult

        where: "The time is set onto the date"
        date                                    | hour | minute | seconds | milliseconds | expectedResult
        convertToDate("2021-01-01T00:00:01.00") | 12   | 15     | 10      | 99           | "2021-01-01T12:15:10"
        convertToDate("2000-01-31T00:00:59.00") | 23   | 59     | 25      | 60           | "2000-01-31T23:59:25"
        convertToDate("1999-12-31T00:00:20.00") | 00   | 01     | 59      | 1            | "1999-12-31T00:01:59"
    }

    /**
     * Test for {@link DateTimeUtils#setTimeOnDate}
     */
    def "setTimeOnDate should set [Hour #hour, Minute #minute] onto [Date #dateToSet] as #expectedResult"() {
        given: "I create a Date object"
        def date = dateToSet

        when: "I invoke setTimeOnDate with the date object and a time"
        DateTimeUtils.setTimeOnDate(date, hour, minute)

        then: "The date should now be modified to the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(date) == expectedResult

        where: "The time is set onto the date"
        dateToSet                               | hour | minute | expectedResult
        convertToDate("2021-01-01T00:00:01.00") | 12   | 15     | "2021-01-01T12:15:00"
        convertToDate("2000-01-31T00:00:01.00") | 23   | 59     | "2000-01-31T23:59:00"
        convertToDate("1999-12-31T00:00:01.01") | 00   | 01     | "1999-12-31T00:01:00"
    }

    /**
     * Test for {@link DateTimeUtils#setTimeOnDate}
     */
    def "setTimeOnDate should set [Hour #hour, Minute #minute, Seconds #seconds, Milliseconds #milliseconds] onto [Date #dateToSet] as #expectedResult"() {
        given: "I create a Date object"
        def date = dateToSet

        when: "I invoke setTimeOnDate with the date object and a time"
        DateTimeUtils.setTimeOnDate(date, hour, minute, seconds, milliseconds)

        then: "The date should now be modified to the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(date) == expectedResult

        where: "The time is set onto the date"
        dateToSet                               | hour | minute | seconds | milliseconds | expectedResult
        convertToDate("2021-01-01T00:00:01.00") | 12   | 15     | 8       | 50           | "2021-01-01T12:15:08"
        convertToDate("2000-01-31T00:00:01.00") | 23   | 59     | 6       | 1            | "2000-01-31T23:59:06"
        convertToDate("1999-12-31T00:00:01.01") | 00   | 01     | 24      | 12           | "1999-12-31T00:01:24"
    }

    /**
     * Test for {@link DateTimeUtils#setTime} without the seconds flag enabled
     */
    def "setTime should set the time from [#timeDate], ignoring seconds and milliseconds, onto the date of [#dateDate] as #expectedResult"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.setTime(dateDate, timeDate)) == expectedResult

        where: "The following scenarios are tested, with the seconds and milliseconds reset to zero"
        dateDate                                | timeDate                                | expectedResult
        convertToDate("2021-01-01T00:00:06.01") | convertToDate("1970-01-01T12:15:07.12") | "2021-01-01T12:15:00"
        convertToDate("2000-01-31T00:00:05.02") | convertToDate("1970-01-01T23:59:08.11") | "2000-01-31T23:59:00"
        convertToDate("1999-12-31T00:00:04.03") | convertToDate("1970-01-01T00:01:09.10") | "1999-12-31T00:01:00"
    }

    /**
     * Test for {@link DateTimeUtils#setTime} with the seconds flag enabled
     */
    def "setTime should set the time from [#timeDate], ignoring milliseconds, onto the date of [#dateDate] as #expectedResult"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.setTime(dateDate, timeDate, true)) == expectedResult

        where: "The following scenarios are tested, with the milliseconds reset to zero"
        dateDate                                | timeDate                                | expectedResult
        convertToDate("2021-01-01T00:00:06.01") | convertToDate("1970-01-01T12:15:07.12") | "2021-01-01T12:15:07"
        convertToDate("2000-01-31T00:00:05.02") | convertToDate("1970-01-01T23:59:08.11") | "2000-01-31T23:59:08"
        convertToDate("1999-12-31T00:00:04.03") | convertToDate("1970-01-01T00:01:09.10") | "1999-12-31T00:01:09"
    }


    /**
     * Test for {@link DateTimeUtils#setTimeOnDummyDate}
     */
    def "setTimeOnDummyDate should set [Hour #hour, Minute #minute] as #expectedResult"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.setTimeOnDummyDate(hour, minute)) == expectedResult

        where: "The following scenarios are tested"
        hour | minute | expectedResult
        12   | 15     | "1970-01-01T12:15:00"
        23   | 59     | "1970-01-01T23:59:00"
        00   | 01     | "1970-01-01T00:01:00"
    }

    /**
     * Test for {@link DateTimeUtils#setTimeOnDummyDate}
     */
    def "setTimeOnDummyDate should set [#date] as #expectedResult"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.setTimeOnDummyDate(date as Date)) == expectedResult

        where: "The following scenarios are tested, including NULL"
        date                                    | expectedResult
        convertToDate("2021-01-01T12:15:00.00") | "1970-01-01T12:15:00"
        convertToDate("2021-01-01T23:59:00.00") | "1970-01-01T23:59:00"
        convertToDate("2021-01-01T00:01:01.00") | "1970-01-01T00:01:00"
    }

    /**
     * Test for {@link DateTimeUtils#setTimeOnDummyDate}
     */
    def "setTimeOnDummyDate should set [#timeString] as #expectedResult"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.setTimeOnDummyDate(timeString as String)) == expectedResult

        where: "The following scenarios are tested"
        timeString | expectedResult
        "12:15"    | "1970-01-01T12:15:00"
        "23:59"    | "1970-01-01T23:59:00"
        "00:01"    | "1970-01-01T00:01:00"
    }

    /**
     * Test for {@link DateTimeUtils#setTimeEvent}
     */
    def "setTimeEvent should set the time event #timeEvent onto the date of [#dateDate] as #expectedResult"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.setTimeEvent(dateDate, timeEvent)) == expectedResult

        where: "All TimeEvent scenarios are tested"
        dateDate                                | timeEvent                             | expectedResult
        convertToDate("2021-01-01T00:00:06.01") | DateTimeUtils.TimeEvent.END_OF_DAY    | "2021-01-01T23:59:59"
        convertToDate("2021-01-31T00:00:00.00") | DateTimeUtils.TimeEvent.END_OF_DAY    | "2021-01-31T23:59:59"
        convertToDate("2021-01-01T00:00:06.01") | DateTimeUtils.TimeEvent.ONE_WEEK_AGO  | "2021-12-25T00:00:00"
        convertToDate("2021-06-06T14:56:06.28") | DateTimeUtils.TimeEvent.START_OF_DAY  | "2021-06-06T00:00:00"
        convertToDate("2021-08-08T00:00:06.01") | DateTimeUtils.TimeEvent.START_OF_YEAR | "2021-01-01T00:00:00"
        convertToDate("2021-06-06T00:00:06.01") | DateTimeUtils.TimeEvent.END_OF_DAY    | "2021-06-06T23:59:59"
        convertToDate("2021-01-01T00:00:06.01") | DateTimeUtils.TimeEvent.END_OF_YEAR   | "2021-12-31T23:59:59"
    }

    /**
     * Test for {@link DateTimeUtils#setTimeEventOnDate}
     *
     * Instead of returning a Date, this method modifies the provided Date
     */
    def "setTimeEventOnDate should set the time event #timeEvent onto the date of [#dateToSet] as #expectedResult"() {
        given: "I create a Date object"
        def date = dateToSet

        when: "I invoke setTimeEventOnDate with the date object and a TimeEvent"
        DateTimeUtils.setTimeEventOnDate(date, timeEvent)

        then: "The date should now be modified to the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(date) == expectedResult

        where: "All TimeEvent scenarios are tested"
        dateToSet                               | timeEvent                             | expectedResult
        convertToDate("2021-01-01T00:00:06.01") | DateTimeUtils.TimeEvent.END_OF_DAY    | "2021-01-01T23:59:59"
        convertToDate("2021-01-01T00:00:06.01") | DateTimeUtils.TimeEvent.ONE_WEEK_AGO  | "2021-12-25T00:00:00"
        convertToDate("2021-06-06T14:56:06.28") | DateTimeUtils.TimeEvent.START_OF_DAY  | "2021-06-06T00:00:00"
        convertToDate("2021-08-08T00:00:06.01") | DateTimeUtils.TimeEvent.START_OF_YEAR | "2021-01-01T00:00:00"
        convertToDate("2021-06-06T00:00:06.01") | DateTimeUtils.TimeEvent.END_OF_DAY    | "2021-06-06T23:59:59"
        convertToDate("2021-01-01T00:00:06.01") | DateTimeUtils.TimeEvent.END_OF_YEAR   | "2021-12-31T23:59:59"
    }

    /**
     * Test for {@link DateTimeUtils#resetTime}
     */
    def "resetTime should convert reset the time on #date to #expectedResult"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.resetTime(date)) == expectedResult

        where: "The following scenarios are tested"
        date                                    | expectedResult
        convertToDate("2021-01-01T12:34:56.00") | "2021-01-01T00:00:00"
        convertToDate("1980-02-27T00:00:00.00") | "1980-02-27T00:00:00"
        convertToDate("1999-12-31T00:00:00.00") | "1999-12-31T00:00:00"
    }

    /* ******** *
     * Set Date *
     * ******** */

    /**
     * Test for {@link DateTimeUtils#setDate}
     */
    def "setDate should convert [Year #year, Month #month, Day #day] into #expectedResult"() {
        expect: "The method should return the expected result"
        DateTimeUtils.setDate(year, month, day) == expectedResult

        where: "The following scenarios are tested"
        year | month | day | expectedResult
        2021 | 0     | 1   | convertToDate("2021-01-01T00:00:00.00")
        1980 | 1     | 27  | convertToDate("1980-02-27T00:00:00.00")
        1999 | 11    | 31  | convertToDate("1999-12-31T00:00:00.00")
    }

    /**
     * Test for {@link DateTimeUtils#setDateTime}
     */
    def "setDateTime should convert [Year #year, Month #month, Day #day, Hours #hours, Minutes #minutes] into #expectedResult"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.setDateTime(year, month, day, hours, minutes)) == expectedResult

        where: "The following scenarios are tested"
        year | month | day | hours | minutes | expectedResult
        2021 | 0     | 1   | 0     | 0       | "2021-01-01T00:00:00"
        1980 | 1     | 27  | 1     | 2       | "1980-02-27T01:02:00"
        1999 | 11    | 31  | 23    | 59      | "1999-12-31T23:59:00"
        2021 | 0     | 31  | 23    | 59      | "2021-01-31T23:59:00"
    }

    /**
     * Test for {@link DateTimeUtils#setDateTime}
     */
    def "setDateTime should convert [Year #year, Month #month, Day #day, Hours #hours, Minutes #minutes, Seconds #seconds, Milliseconds #milliseconds] into #expectedResult"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.setDateTime(year, month, day, hours, minutes, seconds, milliseconds)) == expectedResult

        where: "The following scenarios are tested"
        year | month | day | hours | minutes | seconds | milliseconds | expectedResult
        2021 | 0     | 1   | 0     | 0       | 0       | 0            | "2021-01-01T00:00:00"
        1980 | 1     | 27  | 1     | 2       | 3       | 4            | "1980-02-27T01:02:03"
        1999 | 11    | 31  | 23    | 59      | 59      | 99           | "1999-12-31T23:59:59"
    }

    /**
     * Test for {@link DateTimeUtils#setMonthBoundaries}
     */
    def "setMonthBoundaries should return #expectedStartDate and #expectedEndDate when given #monthToSet"() {
        given: "I create the start and end dates to provide to the method"
        def startOfSearch = new Date()
        def endOfSearch = new Date()

        when: "I call getYearAndMonthFromDate"
        DateTimeUtils.setMonthBoundaries(startOfSearch, endOfSearch, 2021, monthToSet)

        then: "The startOfSearch and endOfSearch dates should have been set as expected"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(startOfSearch) == expectedStartDate
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(endOfSearch) == expectedEndDate

        where: "All months are tested (Month 0 = JANUARY, Month 11 = DECEMBER)"
        monthToSet | expectedStartDate     | expectedEndDate
        0          | "2021-01-01T00:00:00" | "2021-01-31T23:59:59"
        1          | "2021-02-01T00:00:00" | "2021-02-28T23:59:59"
        2          | "2021-03-01T00:00:00" | "2021-03-31T23:59:59"
        3          | "2021-04-01T00:00:00" | "2021-04-30T23:59:59"
        4          | "2021-05-01T00:00:00" | "2021-05-31T23:59:59"
        5          | "2021-06-01T00:00:00" | "2021-06-30T23:59:59"
        6          | "2021-07-01T00:00:00" | "2021-07-31T23:59:59"
        7          | "2021-08-01T00:00:00" | "2021-08-31T23:59:59"
        8          | "2021-09-01T00:00:00" | "2021-09-30T23:59:59"
        9          | "2021-10-01T00:00:00" | "2021-10-31T23:59:59"
        10         | "2021-11-01T00:00:00" | "2021-11-30T23:59:59"
        11         | "2021-12-01T00:00:00" | "2021-12-31T23:59:59"
    }

    /**
     * Test for {@link DateTimeUtils#setMonthBoundaries} using Date arguments for month and year
     */
    def "setMonthBoundaries should return #expectedStartDate and #expectedEndDate when given dates [year: #dateWithYear, month: #dateWithMonth]"() {
        given: "I create the start and end dates to provide to the method"
        def startOfSearch = new Date()
        def endOfSearch = new Date()

        when: "I call getYearAndMonthFromDate"
        DateTimeUtils.setMonthBoundaries(startOfSearch, endOfSearch, dateWithYear, dateWithMonth)

        then: "The startOfSearch and endOfSearch dates should have been set as expected"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(startOfSearch) == expectedStartDate
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(endOfSearch) == expectedEndDate

        where: "All months are tested (Month 0 = JANUARY, Month 11 = DECEMBER)"
        dateWithMonth                           | dateWithYear                            | expectedStartDate     | expectedEndDate
        convertToDate("1970-01-10T01:02:03.45") | convertToDate("2000-01-01T00:00:00.00") | "2000-01-01T00:00:00" | "2000-01-31T23:59:59"
        convertToDate("1970-02-01T01:02:03.45") | convertToDate("2002-01-01T00:00:00.00") | "2002-02-01T00:00:00" | "2002-02-28T23:59:59"
        convertToDate("1970-03-01T01:02:03.45") | convertToDate("2004-01-01T00:00:00.00") | "2004-03-01T00:00:00" | "2004-03-31T23:59:59"
        convertToDate("1970-04-01T01:02:03.45") | convertToDate("2006-01-01T00:00:00.00") | "2006-04-01T00:00:00" | "2006-04-30T23:59:59"
        convertToDate("1970-05-01T01:02:03.45") | convertToDate("2008-01-01T00:00:00.00") | "2008-05-01T00:00:00" | "2008-05-31T23:59:59"
        convertToDate("1970-06-01T01:02:03.45") | convertToDate("2010-01-01T00:00:00.00") | "2010-06-01T00:00:00" | "2010-06-30T23:59:59"
        convertToDate("1970-07-01T01:02:03.45") | convertToDate("2012-01-01T00:00:00.00") | "2012-07-01T00:00:00" | "2012-07-31T23:59:59"
        convertToDate("1970-08-01T01:02:03.45") | convertToDate("2014-01-01T00:00:00.00") | "2014-08-01T00:00:00" | "2014-08-31T23:59:59"
        convertToDate("1970-09-01T01:02:03.45") | convertToDate("2016-01-01T00:00:00.00") | "2016-09-01T00:00:00" | "2016-09-30T23:59:59"
        convertToDate("1970-10-01T01:02:03.45") | convertToDate("2018-01-01T00:00:00.00") | "2018-10-01T00:00:00" | "2018-10-31T23:59:59"
        convertToDate("1970-11-01T01:02:03.45") | convertToDate("2020-01-01T00:00:00.00") | "2020-11-01T00:00:00" | "2020-11-30T23:59:59"
        convertToDate("1970-12-01T01:02:03.45") | convertToDate("2021-01-01T00:00:00.00") | "2021-12-01T00:00:00" | "2021-12-31T23:59:59"
    }

    /* *********** *
     * Conversions *
     * *********** */

    /**
     * Test for {@link DateTimeUtils#convertToMinutes{Date}}
     */
    def "convertToMinutes should convert the time aspect of a Date into the quantity of minutes"() {
        expect: "The method should return the expected result"
        DateTimeUtils.convertToMinutes(dateToConvert) == expectedResult

        where: "The following scenarios are tested"
        dateToConvert                           | expectedResult
        convertToDate("2021-01-01T15:00:00.00") | 900 // 15 x 60
        convertToDate("2021-01-01T15:45:00.00") | 945 // (15 x 60) + 45
        convertToDate("2021-01-01T00:12:50.00") | 12 // (0 x 60) + 12
        convertToDate("2021-01-01T00:00:59.00") | 0 // (0 x 60) + 0 [no rounding of seconds]
    }

    /**
     * Test for {@link DateTimeUtils#convertToMinutes{int, int}
     */
    def "convertToMinutes should convert hours and minutes into the quantity of minutes"() {
        expect: "The method should return the expected result"
        DateTimeUtils.convertToMinutes(hours, minutes) == expectedResult

        where: "The following scenarios are tested"
        hours | minutes | expectedResult
        15    | 0       | 900
        15    | 45      | 945 // (15 x 60) + 45
        0     | 12      | 12 // (0 x 60) + 12
        0     | 0       | 0 // (0 x 60) + 0
    }

    /**
     * Test for {@link DateTimeUtils#convertFormat{String, String, String}}
     */
    def "convertFormat should convert the format of a String date from one String format into the provided String format"() {
        expect: "The method should return the expected result"
        DateTimeUtils.convertFormat(stringDate, fromFormat, toFormat) == expectedResult

        where: "The following scenarios are tested"
        stringDate   | fromFormat                              | toFormat                     | expectedResult
        "31/12/2021" | DateTimeUtils.Format.UK_DATE            | DateTimeUtils.Format.US_DATE | "12/31/2021"
        "12/31/2021" | DateTimeUtils.Format.US_DATE            | DateTimeUtils.Format.UK_DATE | "31/12/2021"
        "2021-12-31" | DateTimeUtils.Format.ISO_8601_DATE_ONLY | DateTimeUtils.Format.US_DATE | "12/31/2021"

    }

    /**
     * Test for {@link DateTimeUtils#convertFormat{String, FastDateFormat, FastDateFormat}}
     */
    def "convertFormat should convert the format of a String date from one FastDateFormat into the provided FastDateFormat"() {
        expect: "The method should return the expected result"
        DateTimeUtils.convertFormat(stringDate, fromFormat, toFormat) == expectedResult

        where: "The following scenarios are tested"
        stringDate   | fromFormat                                 | toFormat                        | expectedResult
        "31/12/2021" | DateTimeUtils.Formatter.UK_DATE            | DateTimeUtils.Formatter.US_DATE | "12/31/2021"
        "12/31/2021" | DateTimeUtils.Formatter.US_DATE            | DateTimeUtils.Formatter.UK_DATE | "31/12/2021"
        "2021-12-31" | DateTimeUtils.Formatter.ISO_8601_DATE_ONLY | DateTimeUtils.Formatter.US_DATE | "12/31/2021"

    }

    /**
     * Test for {@link DateTimeUtils#getDatesAsList{Date, int}
     */
    def "getDatesAsList should create a list of Dates, starting from the provided Date, until the provided increment is met"() {
        when: "I call getDatesAsList"
        def list = DateTimeUtils.getDatesAsList(date, numberOfDays)

        and: "I convert the result into String (to avoid object and timezone comparison)"
        def stringDates = []
        for (final Date dateInList : list) {
            stringDates.add(DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(dateInList))
        }

        then: "The list should match the expected list of dates"
        stringDates == expectedResult

        where: "The following scenarios are tested"
        date                                    | numberOfDays | expectedResult
        convertToDate("2021-01-01T15:00:00.00") | 5            | ["2021-01-01T15:00:00", "2021-01-02T15:00:00", "2021-01-03T15:00:00", "2021-01-04T15:00:00", "2021-01-05T15:00:00"]
        convertToDate("2021-02-28T15:00:00.00") | 4            | ["2021-02-28T15:00:00", "2021-03-01T15:00:00", "2021-03-02T15:00:00", "2021-03-03T15:00:00"]
        convertToDate("2021-12-31T15:00:00.00") | 2            | ["2021-12-31T15:00:00", "2023-01-01T15:00:00"]

    }

    /* ************ *
     * Calculations *
     * ************ */

    /**
     * Test for {@link DateTimeUtils#millisecondsBetween{Date,Date}}
     */
    def "millisecondsBetween should calculate the number of milliseconds between two Dates"() {
        expect: "The method should return the expected result"
        DateTimeUtils.millisecondsBetween(date1, date2) == expectedResult

        where: "The following scenarios are tested"
        date1                                   | date2                                   | expectedResult
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:00:00.01") | 1
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:00:01.02") | 1002
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:00:59.30") | 59030
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:01:00.99") | 60099
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:59:00.00") | 3540000
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-02T15:00:00.04") | 86400004
    }

    /**
     * Test for {@link DateTimeUtils#secondsBetween{Date,Date}}
     */
    def "secondsBetween should calculate the number of seconds between two Dates"() {
        expect: "The method should return the expected result"
        DateTimeUtils.secondsBetween(date1, date2) == expectedResult

        where: "The following scenarios are tested (full seconds only)"
        date1                                   | date2                                   | expectedResult
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:00:00.01") | 0
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:00:01.02") | 1
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:00:59.30") | 59
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:01:00.99") | 60
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:59:00.00") | 3540
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-02T15:00:00.04") | 86400
    }

    /**
     * Test for {@link DateTimeUtils#minutesBetween{Date,Date}}
     */
    def "minutesBetween should calculate the number of minutes between two Dates"() {
        expect: "The method should return the expected result"
        DateTimeUtils.minutesBetween(date1, date2) == expectedResult

        where: "The following scenarios are tested"
        date1                                   | date2                                   | expectedResult
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:01:00.00") | 1
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:59:59.00") | 59
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T16:00:01.00") | 60
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-02T15:00:00.00") | 1440
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-02-01T15:00:00.00") | 44640
        convertToDate("2021-02-01T15:00:00.00") | convertToDate("2021-03-01T15:00:00.00") | 40320
    }

    /**
     * Test for {@link DateTimeUtils#hoursBetween{Date,Date}}
     */
    def "hoursBetween should calculate the number of hours between two Dates"() {
        expect: "The method should return the expected result"
        DateTimeUtils.hoursBetween(date1, date2) == expectedResult

        where: "The following scenarios are tested"
        date1                                   | date2                                   | expectedResult
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:59:00.00") | 0
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T16:00:00.00") | 1
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-02T16:00:00.00") | 25
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-02-01T15:00:00.00") | 744
        convertToDate("2021-02-01T15:00:00.00") | convertToDate("2021-03-01T15:00:00.00") | 672
    }

    /**
     * Test for {@link DateTimeUtils#daysBetween{Date,Date}}
     */
    def "daysBetween should calculate the number of days between two Dates"() {
        expect: "The method should return the expected result"
        DateTimeUtils.daysBetween(date1, date2) == expectedResult

        where: "The following scenarios are tested"
        date1                                   | date2                                   | expectedResult
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:59:00.00") | 0
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-02T16:00:00.00") | 1
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-02-01T15:00:00.00") | 31
        convertToDate("2021-02-01T15:00:00.00") | convertToDate("2021-03-01T15:00:00.00") | 28
        convertToDate("2021-02-01T15:00:00.00") | convertToDate("2023-02-01T23:00:00.00") | 365
    }

    /**
     * Test for {@link DateTimeUtils#daysBetweenExact{Date,Date}}
     */
    def "daysBetweenExact should calculate the exact number of days between two Dates, including decimal places"() {
        expect: "The method should return the expected result"
        DateTimeUtils.daysBetweenExact(date1, date2) == expectedResult

        where: "The following scenarios are tested"
        date1                                   | date2                                   | expectedResult
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:59:00.00") | 0.04097222222222222
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-02T16:00:00.00") | 1.0416666666666667
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-02-01T15:00:00.00") | 31
        convertToDate("2021-02-01T15:00:00.00") | convertToDate("2021-03-01T15:00:00.00") | 28
        convertToDate("2021-02-01T15:00:00.00") | convertToDate("2023-02-01T23:00:00.00") | 365.3333333333333
    }

    /**
     * Test for {@link DateTimeUtils#daysBetweenExact{long,long}}
     */
    def "daysBetweenExact should calculate the exact number of days between two millisecond epochs, including decimal places"() {
        expect: "The method should return the expected result"
        DateTimeUtils.daysBetweenExact(millis1, millis2) == expectedResult

        where: "The following scenarios are tested"
        millis1                                           | millis2                                           | expectedResult
        convertToDate("2021-01-01T15:00:00.00").getTime() | convertToDate("2021-01-01T15:59:00.00").getTime() | 0.04097222222222222
        convertToDate("2021-01-01T15:00:00.00").getTime() | convertToDate("2021-01-02T16:00:00.00").getTime() | 1.0416666666666667
        convertToDate("2021-01-01T15:00:00.00").getTime() | convertToDate("2021-02-01T15:00:00.00").getTime() | 31
        convertToDate("2021-02-01T15:00:00.00").getTime() | convertToDate("2021-03-01T15:00:00.00").getTime() | 28
        convertToDate("2021-02-01T15:00:00.00").getTime() | convertToDate("2023-02-01T23:00:00.00").getTime() | 365.3333333333333
    }

    /**
     * Test for {@link DateTimeUtils#daysSpanned{Date,Date}}
     */
    def "daysSpanned should calculate the number of days covered by two Dates"() {
        expect: "The method should return the expected result"
        DateTimeUtils.daysSpanned(date1, date2) == expectedResult

        where: "The following scenarios are tested"
        date1                                   | date2                                   | expectedResult
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:59:00.00") | 1
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-02T15:00:00.00") | 2
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-02T15:01:00.00") | 2
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-02-01T15:00:00.00") | 32
        convertToDate("2021-02-01T15:00:00.00") | convertToDate("2021-03-01T15:00:00.00") | 29
        convertToDate("2021-02-01T15:00:00.00") | convertToDate("2023-02-01T23:00:00.00") | 366
    }

    /**
     * Test for {@link DateTimeUtils#monthsBetween{Date,Date}}
     */
    def "daysBetween should calculate the number of months between two Dates"() {
        expect: "The method should return the expected result"
        DateTimeUtils.monthsBetween(date1, date2) == expectedResult

        where: "The following scenarios are tested"
        date1                                   | date2                                   | expectedResult
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:59:00.00") | 0
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-02-02T16:00:00.00") | 1
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2023-01-01T00:00:00.00") | 12
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2024-02-01T15:00:00.00") | 25
        convertToDate("2021-02-01T15:00:00.00") | convertToDate("2032-03-01T15:00:00.00") | 121
        convertToDate("2021-02-01T15:00:00.00") | convertToDate("2123-02-01T23:00:00.00") | 1212
    }

    /**
     * Test for {@link DateTimeUtils#yearsBetween{Date,Date}}
     */
    def "daysBetween should calculate the number of years between two Dates"() {
        expect: "The method should return the expected result"
        DateTimeUtils.yearsBetween(date1, date2) == expectedResult

        where: "The following scenarios are tested"
        date1                                   | date2                                   | expectedResult
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:59:00.00") | 0
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-12-31T23:59:59.00") | 0
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2023-01-01T00:59:00.00") | 1
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2033-01-01T00:59:00.00") | 11
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2133-01-01T00:59:00.00") | 111
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2333-01-01T00:59:00.00") | 311
    }

    /* **************** *
     * Get Unit Methods *
     * **************** */

    /**
     * Test for {@link DateTimeUtils#getDayOfWeek}
     *
     * For reference:
     * Sunday = 1
     * Monday = 2
     * Tuesday = 3
     * Wednesday = 4
     * Thursday = 5
     * Friday = 6
     * Saturday = 7
     */
    def "getDayOfWeek should return the integer day of the week from a Date"() {
        expect: "The method should return the expected result"
        DateTimeUtils.getDayOfWeek(dateToConvert) == expectedResult

        where: "The following scenarios are tested"
        dateToConvert                           | expectedResult
        convertToDate("2021-08-14T00:00:00.00") | 1 // Sun
        convertToDate("2021-08-22T00:00:00.00") | 2 // Mon
        convertToDate("2021-08-30T00:00:00.00") | 3 // Tues
        convertToDate("2021-09-07T00:00:00.00") | 4 // Weds
        convertToDate("2021-09-08T00:00:00.00") | 5 // Thurs
        convertToDate("2021-09-16T00:00:00.00") | 6 // Fri
        convertToDate("2021-09-24T00:00:00.00") | 7 // Sat
    }

    /**
     * Test for {@link DateTimeUtils#getHours}
     *
     * For reference:
     * 00:30am = 0
     * 11:15am = 11
     * 1:00pm = 13
     * 11:45pm = 23
     * etc.
     */
    def "getHours should return the integer hour of the day from a Date"() {
        expect: "The method should return the expected result"
        DateTimeUtils.getHours(dateToUse) == expectedResult

        where: "The following scenarios are tested"
        dateToUse                               | expectedResult
        convertToDate("2021-08-14T00:00:00.00") | 0
        convertToDate("2021-08-14T01:00:00.00") | 1
        convertToDate("2021-08-14T02:00:00.00") | 2
        convertToDate("2021-08-14T03:00:00.00") | 3
        convertToDate("2021-08-14T04:00:00.00") | 4
        convertToDate("2021-08-14T05:00:00.00") | 5
        convertToDate("2021-08-14T06:00:00.00") | 6
        convertToDate("2021-08-14T07:00:00.00") | 7
        convertToDate("2021-08-14T13:00:00.00") | 13
        convertToDate("2021-08-14T23:00:00.00") | 23
    }

    /**
     * Test for {@link DateTimeUtils#getMinutes}
     */
    def "getMinutes should return the integer minute of the hour from a Date"() {
        expect: "The method should return the expected result"
        DateTimeUtils.getMinutes(dateToUse) == expectedResult

        where: "The following scenarios are tested"
        dateToUse                               | expectedResult
        convertToDate("2021-08-14T00:00:00.00") | 0
        convertToDate("2021-08-14T00:59:00.00") | 59
        convertToDate("2021-08-14T00:31:00.00") | 31
        convertToDate("2021-08-14T00:01:00.00") | 1
    }

    /**
     * Test for {@link DateTimeUtils#getSeconds}
     */
    def "getSeconds should return the integer seconds of the time from a Date"() {
        expect: "The method should return the expected result"
        DateTimeUtils.getSeconds(dateToUse) == expectedResult

        where: "The following scenarios are tested"
        dateToUse                               | expectedResult
        convertToDate("2021-08-14T00:00:00.00") | 0
        convertToDate("2021-08-14T00:59:59.00") | 59
        convertToDate("2021-08-14T00:31:12.00") | 12
        convertToDate("2021-08-14T00:01:38.00") | 38
    }

    /**
     * Test for {@link DateTimeUtils#getCurrentYear}
     *
     * In this test, I've mocked the Construction of a new Date so that this test doesn't need constant updating.
     * The mock will default to the unix epoch start (e.g. 1970-01-01)
     */
    def "getCurrentYear should return the current year as an integer"() {
        expect: "The method to return 1970"
        try (MockedConstruction<Date> dateMockedConstruction = Mockito.mockConstruction(Date)) {
            DateTimeUtils.getCurrentYear() == 1970
        }
    }

    /**
     * Test for {@link DateTimeUtils#getCurrentMonth}
     *
     * In this test, I've mocked the Construction of a new Date so that this test doesn't need constant updating.
     * The mock will default to the unix epoch start (e.g. 1970-01-01)
     */
    def "getCurrentMonth should return the current month as an integer"() {
        expect: "The method to return 0 (January)"
        try (MockedConstruction<Date> dateMockedConstruction = Mockito.mockConstruction(Date)) {
            DateTimeUtils.getCurrentMonth() == 0
        }
    }

    /**
     * Test for {@link DateTimeUtils#getStartOfYear}
     */
    def "getStartOfYear should return a Date representing the start of the given integer year"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.getStartOfYear(year)) == expectedResult

        where: "The following scenarios are tested"
        year | expectedResult
        1990 | "1990-01-01T00:00:00"
        2021 | "2021-01-01T00:00:00"
        2001 | "2001-01-01T00:00:00"
        1999 | "1999-01-01T00:00:00"
    }

    /**
     * Test for {@link DateTimeUtils#getEndOfYear}
     *
     * For some reason, these dates need comparing as Strings to avoid hashCode comparisons (which fail)
     */
    def "getEndOfYear should return a Date representing the end of the given integer year"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.getEndOfYear(year)) == expectedResult

        where: "The following scenarios are tested"
        year | expectedResult
        1990 | "1990-12-31T23:59:59"
        2021 | "2021-12-31T23:59:59"
        2001 | "2001-12-31T23:59:59"
        1999 | "1999-12-31T23:59:59"
    }

    /**
     * Test for {@link DateTimeUtils#getDay}
     *
     * For reference:
     * 1st = 1
     * 2nd = 2
     * 31st = 31
     * etc.
     */
    def "getDay should return the integer day of the month from a Date"() {
        expect: "The method should return the expected result"
        DateTimeUtils.getDay(dateToUse) == expectedResult

        where: "The following scenarios are tested"
        dateToUse                               | expectedResult
        convertToDate("2021-08-14T00:00:00.00") | 14
        convertToDate("2021-08-01T00:00:00.00") | 1
        convertToDate("2021-08-30T00:00:00.00") | 30
    }

    /**
     * Test for {@link DateTimeUtils#getMonth}
     *
     * For reference:
     * January = 0
     * February = 1
     * December = 11
     * etc.
     */
    def "getMonth should return the integer month of the year from a Date"() {
        expect: "The method should return the expected result"
        DateTimeUtils.getMonth(dateToUse) == expectedResult

        where: "The following scenarios are tested"
        dateToUse                               | expectedResult
        convertToDate("2021-01-14T00:00:00.00") | 0
        convertToDate("2021-02-01T00:00:00.00") | 1
        convertToDate("2021-12-30T00:00:00.00") | 11
    }

    /**
     * Test for {@link DateTimeUtils#getYear}
     */
    def "getYear should return the integer month of the year from a Date"() {
        expect: "The method should return the expected result"
        DateTimeUtils.getYear(dateToUse) == expectedResult

        where: "The following scenarios are tested"
        dateToUse                               | expectedResult
        convertToDate("2021-01-14T00:00:00.00") | 2021
        convertToDate("2000-02-01T00:00:00.00") | 2000
        convertToDate("1999-12-30T00:00:00.00") | 1999
        convertToDate("1900-12-30T00:00:00.00") | 1900
    }

    /* ********************** *
     * To String Method Tests *
     * ********************** */

    /**
     * Test for {@link DateTimeUtils#minutesToTime}
     */
    def "minutesToTime should return a String 'hh:mm' time from the minutes provided"() {
        expect: "The method should return the expected result"
        DateTimeUtils.minutesToTime(minutes) == expectedResult

        where: "The following scenarios are tested"
        minutes | expectedResult
        0       | "00:00"
        60      | "01:00"
        260     | "04:20"
        900     | "15:00"
        1439    | "23:59"
    }

    /**
     * Test for {@link DateTimeUtils#toStringTime}
     */
    def "When toStringTime"() {
        expect: "The method should return the expected result"
        DateTimeUtils.toStringTime(dateToConvert) == expectedResult

        where: "The following scenarios are tested"
        dateToConvert                           | expectedResult
        convertToDate("2021-08-14T00:00:00.00") | "00:00"
        convertToDate("2021-08-14T01:23:45.00") | "01:23"
        convertToDate("2021-08-14T12:34:56.00") | "12:34"
    }

    /**
     * Test for {@link DateTimeUtils#toIsoStringDate}
     */
    def "When toIsoStringDate"() {
        expect: "The method should return the expected result"
        DateTimeUtils.toIsoStringDate(dateToConvert) == expectedResult

        where: "The following scenarios are tested"
        dateToConvert                           | expectedResult
        convertToDate("2021-08-14T00:00:00.00") | "2021-08-14"
        convertToDate("2021-01-31T00:00:00.00") | "2021-01-31"
        // Overflowing Date
        convertToDate("2021-02-31T00:00:00.00") | "2021-03-03"
    }

    /**
     * Test for {@link DateTimeUtils#toIsoStringDateTime}
     */
    def "When toIsoStringDateTime"() {
        expect: "The method should return the expected result"
        DateTimeUtils.toIsoStringDateTime(dateToConvert) == expectedResult

        where: "The following scenarios are tested"
        dateToConvert                           | expectedResult
        convertToDate("2021-08-14T00:00:00.00") | "2021-08-14T00:00:00"
        convertToDate("2021-01-31T15:30:20.00") | "2021-01-31T15:30:20"
        // Overflowing Date
        convertToDate("2021-02-31T01:23:45.00") | "2021-03-03T01:23:45"
    }

    /* **************** *
     * Add Method Tests *
     * **************** */

    /**
     * Test for {@link DateTimeUtils#addMinutes}
     */
    def "addMinutes should add #minutes minutes to the Date #dateTime to make #expectedDate"() {
        when: "I provide addMinutes with a DateTime and a number of minutes to add"
        DateTimeUtils.addMinutes(dateTime, minutes)

        then: "The method should adjust the provided Date to add the number of minutes"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(dateTime) == expectedDate

        where: "The following examples are tested"
        dateTime                                | minutes | expectedDate
        convertToDate("1970-01-01T00:00:00.00") | 10      | "1970-01-01T00:10:00"
        convertToDate("2021-09-16T15:39:00.00") | 10      | "2021-09-16T15:49:00"
        convertToDate("2020-01-01T10:11:12.00") | 120     | "2020-01-01T12:11:12"
        convertToDate("2020-01-01T10:11:12.00") | -10     | "2020-01-01T10:01:12"
    }

    /**
     * Test for {@link DateTimeUtils#addDays}
     */
    def "addDays should add #days days to the Date #dateTime to make #expectedDate"() {
        when: "I provide addDays with a DateTime and a number of days to add"
        DateTimeUtils.addDays(dateTime, days)

        then: "The method should adjust the provided Date to add the number of days"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(dateTime) == expectedDate

        where: "The following examples are tested"
        dateTime                                | days | expectedDate
        convertToDate("1970-01-01T00:00:00.00") | 5    | "1970-01-06T00:00:00"
        convertToDate("2021-09-16T15:39:00.00") | 10   | "2021-09-26T15:39:00"
        convertToDate("2020-01-01T10:11:12.00") | 120  | "2020-04-30T10:11:12"
        convertToDate("2020-01-01T10:11:12.00") | -10  | "2019-12-22T10:11:12"
    }

    /* ********************* *
     * Subtract Method Tests *
     * ********************* */

    /**
     * Test for {@link DateTimeUtils#minusMinutes}
     */
    def "minusMinutes should removed #minutes minutes from the Date #dateTime to make #expectedDate"() {
        when: "I provide addMinutes with a DateTime and a number of minutes to add"
        DateTimeUtils.minusMinutes(dateTime, minutes)

        then: "The method should adjust the provided Date to remove the number of minutes"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(dateTime) == expectedDate

        where: "The following examples are tested"
        dateTime                                | minutes | expectedDate
        convertToDate("1970-01-01T00:10:00.00") | 10      | "1970-01-01T00:00:00"
        convertToDate("2021-09-16T15:39:00.00") | 10      | "2021-09-16T15:29:00"
        convertToDate("2020-01-01T10:11:12.00") | 120     | "2020-01-01T08:11:12"
    }

    /* ******************** *
     * To Date Method Tests *
     * ******************** */

    /**
     * Test for {@link DateTimeUtils#iso8601StringToDate}
     */
    def "isoStringToDate should converted the String #dateTimeString to the Date #expectedDate"() {
        when: "I provide isoStringToDate with a DateTime String in the ISO 8601 format"
        def result = DateTimeUtils.iso8601StringToDate(dateTimeString)

        then: "The method should return a converted Date object"
        result == expectedDate

        where: "The following examples are tested"
        dateTimeString        | expectedDate
        "2021-09-16T15:39:00" | DateTimeUtils.Formatter.ISO_8601_DATE_TIME.parse(dateTimeString)
        "2020-01-01T10:11:12" | DateTimeUtils.Formatter.ISO_8601_DATE_TIME.parse(dateTimeString)
    }

    /* *********************** *
     * Date Check Method Tests *
     * *********************** */

    /**
     * Test for {@link DateTimeUtils#isBetween}
     */
    def "isBetween should return #expectedResult when the from date is #fromDate, date to check is #dateToCheck, and the toDate is #toDate"() {
        expect: "The method should return the expected result"
        DateTimeUtils.isBetween(dateToCheck as Date, fromDate as Date, toDate as Date) == expectedResult

        where: "The following scenarios are tested"
        dateToCheck                             | fromDate                                  | toDate                                     | expectedResult
        // Date to check is bang-on the date range == true
        convertToDate("2021-08-14T00:00:00.00") | convertToDate("2021-08-14T00:00:00.00")   | convertToDate("2021-08-14T00:00:00.00")    | true
        // Date to check is 1 millisecond before the from date == false
        convertToDate("2021-08-14T00:00:00.00") | DateUtils.addMilliseconds(dateToCheck, 1) | convertToDate("2021-08-14T00:00:00.00")    | false
        // Date to check is 1 millisecond after the to date == false
        convertToDate("2021-08-14T00:00:00.00") | convertToDate("2021-08-14T00:00:00.00")   | DateUtils.addMilliseconds(dateToCheck, -1) | false
        // Date to check is comfortably within the boundary
        convertToDate("2021-08-14T00:00:00.00") | convertToDate("2021-08-13T00:00:00.00")   | convertToDate("2021-08-15T00:00:00.00")    | true
    }

    /**
     * Test for {@link DateTimeUtils#isInFuture}
     */
    def "isInFuture should return #expectedResult when #dateToCheck is tested"() {
        expect: "The method should return the expected result"
        DateTimeUtils.isInFuture(dateToCheck as Date) == expectedResult

        where: "The following scenarios are tested"
        dateToCheck               | expectedResult
        DateTimeUtils.addDays(1)  | true
        DateTimeUtils.addDays(10) | true
        DateTimeUtils.addDays(-1) | false
        DateTimeUtils.addDays(0)  | false
    }

    /**
     * Test for {@link DateTimeUtils#isWithin}
     */
    def "isWithin should return #expectedResult when the date range to check is #startDateToCheck -> #endDateToCheck, and the range to check against is #fromDate -> #toDate"() {
        expect: "The method should return the expected result"
        DateTimeUtils.isWithin(startDateToCheck as Date, endDateToCheck as Date, fromDate as Date, toDate as Date) == expectedResult

        where: "The following scenarios are tested"
        startDateToCheck                        | endDateToCheck                          | fromDate                                       | toDate                                        | expectedResult
        // Date Range to check is bang-on the date range == true
        convertToDate("2021-08-14T00:00:00.00") | convertToDate("2021-08-15T17:00:00.00") | convertToDate("2021-08-14T00:00:00.00")        | convertToDate("2021-08-15T17:00:00.00")       | true
        // Date Range to check is 1 millisecond before the from date == false
        convertToDate("2021-08-14T00:00:00.00") | convertToDate("2021-08-15T00:00:00.00") | DateUtils.addMilliseconds(startDateToCheck, 1) | convertToDate("2021-08-15T00:00:00.00")       | false
        // Date Range to check is 1 millisecond after the to date == false
        convertToDate("2021-08-14T00:00:00.00") | convertToDate("2021-08-17T00:00:00.00") | convertToDate("2021-08-14T00:00:00.00")        | DateUtils.addMilliseconds(endDateToCheck, -1) | false
        // Date Range to check is comfortably within the boundary
        convertToDate("2021-08-10T00:00:00.00") | convertToDate("2021-08-14T00:00:00.00") | convertToDate("2021-08-01T00:00:00.00")        | convertToDate("2021-08-20T00:00:00.00")       | true
        // Date Range to check is comfortably within the boundary
        convertToDate("2021-09-19T00:00:00.00") | convertToDate("2021-09-22T00:00:00.00") | convertToDate("2021-09-15T00:00:00.00")        | convertToDate("2021-09-30T00:00:00.00")       | true
        convertToDate("2021-09-19T00:00:00.00") | convertToDate("2021-09-22T00:00:00.00") | convertToDate("2021-08-15T00:00:00.00")        | convertToDate("2021-08-30T00:00:00.00")       | false
    }

    /**
     * Test for {@link DateTimeUtils#isNotWithinDays}
     */
    def "isNotWithinDays should return #expectedResult when #date is NOT within #days days"() {
        expect: "The method should return the expected result"
        DateTimeUtils.isNotWithinDays(date, days) == expectedResult

        where: "The following scenarios are tested"
        date                              | days | expectedResult
        DateUtils.addDays(new Date(), 10) | 30   | false
        DateUtils.addDays(new Date(), 50) | 30   | true
    }

    /**
     * Test for {@link DateTimeUtils#isWithinDays}
     */
    def "isWithinDays should return #expectedResult when #date is NOT within #days days"() {
        expect: "The method should return the expected result"
        DateTimeUtils.isWithinDays(date, days) == expectedResult

        where: "The following scenarios are tested"
        date                              | days | expectedResult
        DateUtils.addDays(new Date(), 10) | 30   | true
        DateUtils.addDays(new Date(), 50) | 30   | false
    }

    /**
     * Test for {@link DateTimeUtils#isSameDate}
     */
    def "isSameDate should return #expectedResult when the dates to check are #date1 and #date2"() {
        expect: "The method should return the expected result"
        DateTimeUtils.isSameDate(date1, date2, DateTimeUtils.Formatter.UK_DATE_SHORT_YEAR) == expectedResult

        where: "The following scenarios are tested"
        date1                                   | date2                                   | expectedResult
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-01T15:59:00.00") | true
        convertToDate("2021-01-01T00:00:00.00") | convertToDate("2021-01-01T23:59:00.00") | true
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2023-01-01T15:59:00.00") | false
        convertToDate("2021-01-01T15:00:00.00") | convertToDate("2021-01-02T15:59:00.00") | false
    }

    /**
     * Test for {@link DateTimeUtils#validateDateUnit}
     */
    def "validateDateUnit should return #expectedResult when the given #dateUnitToCheck in format #format"() {
        expect: "The method should return the expected result"
        DateTimeUtils.Formatter.ISO_8601_DATE_TIME.format(DateTimeUtils.validateDateUnit(dateUnitToCheck, format, unit)) == expectedResult

        where: "The following scenarios are tested"
        dateUnitToCheck | format                                     | unit   | expectedResult
        "2021-01-01"    | DateTimeUtils.Formatter.ISO_8601_DATE_ONLY | "Date" | "2021-01-01T00:00:00"
        "2021-12-14"    | DateTimeUtils.Formatter.ISO_8601_DATE_ONLY | "Date" | "2021-12-14T00:00:00"
        "14:53"         | DateTimeUtils.Formatter.ISO_8601_TIME_ONLY | "Time" | "1970-01-01T14:53:00"
        "31/01/23"      | DateTimeUtils.Formatter.UK_DATE_SHORT_YEAR | "Date" | "2023-01-31T00:00:00"
    }

    /**
     * Test for {@link DateTimeUtils#validateDateUnit}
     */
    def "validateDateUnit should throw an exception if the date #dateUnitToCheck is not in the format #format"() {
        when: "I call validateDateUnit with a date to check that is not in the correct format"
        DateTimeUtils.validateDateUnit(dateUnitToCheck, format, unit)

        then: "An exception should be thrown, with a message detailing the error"
        def exception = thrown(InvalidDateException)
        exception.message == String.format("Invalid %s format for String: %s; should be %s", unit, dateUnitToCheck, format.getPattern())

        where: "The following scenarios are tested"
        dateUnitToCheck | format                                     | unit
        "01-01-2023"    | DateTimeUtils.Formatter.ISO_8601_DATE_ONLY | "Date"
        "2023-31-01"    | DateTimeUtils.Formatter.ISO_8601_DATE_ONLY | "Date"
        "1400"          | DateTimeUtils.Formatter.ISO_8601_TIME_ONLY | "Time"
        "31/01/2023"    | DateTimeUtils.Formatter.UK_DATE_SHORT_YEAR | "Date"
    }

    /* ************** *
     * Helper Methods *
     * ************** */

    /**
     * Converts a String date in "yyyy-MM-dd'T'HH:mm:ss.SSS" format to a {@link Date}
     * @param stringDate the String in "yyyy-MM-dd'T'HH:mm:ss.SSS" format
     * @return the converted {@link Date}
     */
    private Date convertToDate(final String stringDate) {
        FastDateFormat.getInstance("yyyy-MM-dd'T'HH:mm:ss.SSS").parse(stringDate)
    }

    /* *************** *
     * Constants Class *
     * *************** */

    /**
     * Class for constants used in the unit tests
     */
    private class TestConstants {
        public static final int ONE_HOUR_MILLIS = 3600000
        public static final int TWO_HOURS_MILLIS = 2 * ONE_HOUR_MILLIS
        public static final int MINUS_FIVE_HOURS_MILLIS = (5 * ONE_HOUR_MILLIS) * -1
        public static final int MINUS_SIX_HOURS_MILLIS = (6 * ONE_HOUR_MILLIS) * -1
    }


}

