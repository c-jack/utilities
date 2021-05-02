/*
 * Copyright 2022 Chris Jackson (www.cjack.uk)
 */
package uk.cjack.utilities;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import uk.cjack.utilities.exception.InvalidDateException;

/**
 * Utility Methods for Date/Time purposes to reduce boilerplate/replication in implementations
 */
@SuppressWarnings( { "unused", "deprecation" } )
public abstract class DateTimeUtils {

    /**
     * Logger instance
     */
    public static final Logger LOGGER = Logger.getLogger( DateTimeUtils.class.getName() );


    /* ******** *
     * Set Time *
     * ******** */

    /**
     * Sets the provided hours, minutes, seconds, and milliseconds onto a {@link Date} object, using the given date as
     * a base
     *
     * @param date         the {@link Date} object to use as the base of the date
     * @param hours        the hours to set
     * @param minutes      the minutes to set
     * @param seconds      the seconds to set
     * @param milliseconds the milliseconds to add (NOTE: Be careful, as this ADDS rather than sets!)
     * @return {@link Date} object with the given datetime set
     */
    public static Date setTime( final Date date,
                                final int hours,
                                final int minutes,
                                final int seconds,
                                final int milliseconds ) {
        final Calendar calendar = toCalendar( date );
        return setDateTime( calendar.get( Calendar.YEAR ),
                calendar.get( Calendar.MONTH ),
                calendar.get( Calendar.DAY_OF_MONTH ),
                hours,
                minutes,
                seconds,
                milliseconds );
    }

    /**
     * Sets the provided hours and minutes onto a {@link Date} object, using the given date as a base
     *
     * @param date    the {@link Date} object to use as the base of the date
     * @param hours   the hours to set
     * @param minutes the minutes to set
     * @return {@link Date} object with the given datetime set
     */
    public static Date setTime( final Date date,
                                final int hours,
                                final int minutes ) {
        return setTime( date, hours, minutes, 0, 0 );
    }

    /**
     * Sets the provided time onto the given {@link Date} object, using its existing day, month, and year values
     *
     * @param date         the {@link Date} object to use as the base of the date
     * @param hours        the hours to set
     * @param minutes      the minutes to set
     * @param seconds      the seconds to set
     * @param milliseconds the milliseconds to set
     */
    public static void setTimeOnDate( final Date date,
                                      final int hours,
                                      final int minutes,
                                      final int seconds,
                                      final int milliseconds ) {
        final Calendar calendar = toCalendar( date );
        date.setTime( setDateTime( calendar.get( Calendar.YEAR ),
                calendar.get( Calendar.MONTH ),
                calendar.get( Calendar.DAY_OF_MONTH ),
                hours,
                minutes,
                seconds,
                milliseconds ).getTime() );
    }

    /**
     * Sets the provided time onto the given {@link Date} object, using its existing day, month, and year values
     *
     * @param date    the {@link Date} object to use as the base of the date
     * @param hours   the hours to set
     * @param minutes the minutes to set
     */
    public static void setTimeOnDate( final Date date,
                                      final int hours,
                                      final int minutes ) {
        setTimeOnDate( date, hours, minutes, 0, 0 );
    }

    /**
     * Sets the given TimeEvent onto the given {@link Date} object, using its existing day, month, and year values
     *
     * @param date      the {@link Date} object to use as the base of the date
     * @param timeEvent the {@link TimeEvent} to set
     */
    public static void setTimeEventOnDate( final Date date, final TimeEvent timeEvent ) {
        date.setTime( toMilliseconds( setTimeEvent( date, timeEvent ) ) );
    }

    /**
     * Sets the given TimeEvent onto a {@link Date} object, using the given date as a base
     *
     * @param date      the {@link Date} object to use as the base of the date
     * @param timeEvent the {@link TimeEvent} to set
     * @return {@link Date} object with the given datetime set
     */
    public static Date setTimeEvent( final Date date, final TimeEvent timeEvent ) {
        Date result = null;
        switch ( timeEvent )
        {
            case START_OF_YEAR:
                result = setDate( getYear( date ), 0, 1 );
                break;
            case START_OF_DAY:
                result = setTime( date, 0, 0, 0, 0 );
                break;
            case END_OF_DAY:
                // -1 milliseconds takes us to the last millisecond of the previous day, which is the end of today
                result = setTime( addDays( toCalendar( date ), 1 ), 0, 0, 0, -1 );
                break;
            case END_OF_YEAR:
                // -1 milliseconds takes us to a millisecond before Jan 1st, which is the last millisecond of the year
                result = setTime( setDate( getYear( date ) + 1, 0, 1 ), 0, 0, 0, -1 );
                break;
            case ONE_WEEK_AGO:
                final Date startOfDay = setTime( date, 0, 0, 0, 0 );
                addDays( startOfDay, -7 );
                result = startOfDay;
                break;
        }
        if ( result == null )
        {
            result = date;
        }
        return result;
    }

    /**
     * Resets the provided {@link Date}'s hours, minutes, and seconds to 0
     *
     * @param date the {@link Date} to reset the time on
     * @return {@link Date} object with the given time reset to 00:00:00
     */
    public static Date resetTime( final Date date ) {
        return DateTimeUtils.setTimeEvent( date, TimeEvent.START_OF_DAY );
    }

    /**
     * Sets the provided {@link Date}'s hours and minutes onto a {@link Date} object's date
     *
     * @param date         the {@link Date} object to use as the base of the date
     * @param dateWithTime the {@link Date} object to use for the hours and minutes of the time
     * @return {@link Date} object with the given datetime set
     */
    public static Date setTime( final Date date, final Date dateWithTime ) {
        return setTime( date, dateWithTime, false );
    }

    /**
     * Sets the provided {@link Date}'s hours and minutes onto a {@link Date} object's date
     *
     * @param date         the {@link Date} object to use as the base of the date
     * @param dateWithTime the {@link Date} object to use for the hours and minutes of the time
     * @param copySeconds  flag to set whether the seconds should be copited from dateWithTime, or reset (false)
     * @return {@link Date} object with the given datetime set
     */
    public static Date setTime( final Date date, final Date dateWithTime, final boolean copySeconds ) {
        final int seconds = copySeconds ? getSeconds( dateWithTime ) : 0;
        return setTime( date, getHours( dateWithTime ), getMinutes( dateWithTime ), seconds, 0 );
    }

    /**
     * Sets the provided hours and minutes onto a dummy {@link Date} object, set at the unix epoch start (1/1/1970)
     * Only hours and minutes are set - not seconds
     *
     * @param hours   the hours to set
     * @param minutes the minutes to set
     * @return dummy {@link Date} object with the given time set
     */
    public static Date setTimeOnDummyDate( final int hours, final int minutes ) {
        return setTime( new Date( 0 ), hours, minutes, 0, 0 );
    }

    /**
     * Sets the provided Date's time onto a dummy {@link Date} object, set at the unix epoch start (1/1/1970).
     * Only hours and minutes are set - not seconds or milliseconds
     *
     * @param dateWithTime the Date with the time to use
     * @return dummy {@link Date} object with the given time set
     */
    public static Date setTimeOnDummyDate( final Date dateWithTime ) {
        if ( dateWithTime != null )
        {
            return setTime( new Date( 0 ), getHours( dateWithTime ), getMinutes( dateWithTime ), 0, 0 );
        }
        return null;
    }

    /**
     * Sets the provided String's time onto a dummy {@link Date} object, set at the unix epoch start (1/1/1970).
     * Only hours and minutes are set - not seconds
     *
     * @param timeString the String with the time in format HH:mm
     * @return dummy {@link Date} object with the given time set
     * @throws ParseException if the provided date cannot be parsed
     */
    public static Date setTimeOnDummyDate( final String timeString ) throws ParseException {
        if ( timeString != null )
        {
            return setTimeOnDummyDate( Formatter.ISO_8601_TIME_ONLY.parse( timeString ) );
        }
        return null;
    }

    /* ******** *
     * Set Date *
     * ******** */

    /**
     * Sets the provided year, month, and day as a {@link Date}
     *
     * @param year  the year to set
     * @param month the month to set (January = 0, February = 1... December = 11)
     * @param day   the day to set
     * @return {@link Date} object with the given date set
     */
    public static Date setDate( final int year, final int month, final int day ) {
        return setDateTime( year, month, day, 0, 0, 0, 0 );
    }

    /**
     * Sets the provided year, month, and day as a {@link Date}
     *
     * @param year   the year to set
     * @param month  the month to set (January = 0, February = 1... December = 11)
     * @param day    the day to set
     * @param hour   the hour to set
     * @param minute the minute to set
     * @return {@link Date} object with the given date set
     */
    public static Date setDateTime( final int year, final int month, final int day, final int hour, final int minute ) {
        return setDateTime( year, month, day, hour, minute, 0, 0 );
    }

    /**
     * Sets the provided year, month, and day as a {@link Date}
     *
     * @param year         the year to set
     * @param month        the month to set (January = 0, February = 1... December = 11)
     * @param day          the day to set
     * @param hour         the hour to set
     * @param minute       the minute to set
     * @param seconds      the seconds to set
     * @param milliseconds the milliseconds to set
     * @return {@link Date} object with the given date set
     */
    public static Date setDateTime( final int year,
                                    final int month,
                                    final int day,
                                    final int hour,
                                    final int minute,
                                    final int seconds,
                                    final int milliseconds ) {
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();
        calendar.set( year, month, day, hour, minute, seconds );
        calendar.set( Calendar.MILLISECOND, milliseconds );
        return calendar.getTime();

    }

    /**
     * Sets the boundaries of the given year and month onto the provided start/end dates
     *
     * @param startDateToSet the {@link Date} to set as the start of the month
     * @param endDateToSet   the {@link Date} to set as the end of the month
     * @param year           the year to set
     * @param month          the month to set the boundary for
     */
    public static void setMonthBoundaries( final Date startDateToSet,
                                           final Date endDateToSet,
                                           final int year,
                                           final int month ) {
        LOGGER.log( Level.INFO, "Setting Month Boundary for month {0} of year {1}",
                new Object[]{ month, String.valueOf( year ) } );

        // Set the date as the start time and day of the month
        final Date startOfMonth = setTimeEvent( setDate( year, month, 1 ), TimeEvent.START_OF_DAY );
        startDateToSet.setTime( toMilliseconds( startOfMonth ) );

        /*
         * Day 0 works as -1 days, setting the day to the end of the previous month
         * (without having to work with leap years etc.)
         * Sets the time as the end of the day (23:59:59.999999999)
         */
        final Date endOfMonth = setTimeEvent( setDate( year, month + 1, 0 ), TimeEvent.END_OF_DAY );
        endDateToSet.setTime( toMilliseconds( endOfMonth ) );

        LOGGER.log( Level.INFO, "Month Start [{0}] Month End [{1}]", new Object[]{ startDateToSet, endDateToSet } );
    }

    /**
     * Sets the boundaries of the given year and month onto the provided start/end dates
     *
     * @param startDateToSet the {@link Date} to set as the start of the month
     * @param endDateToSet   the {@link Date} to set as the end of the month
     * @param dateWithYear   the {@link Date}with the year to set
     * @param dateWithMonth  the {@link Date} with the month to set the boundary for
     */
    public static void setMonthBoundaries( final Date startDateToSet,
                                           final Date endDateToSet,
                                           final Date dateWithYear,
                                           final Date dateWithMonth ) {
        setMonthBoundaries( startDateToSet, endDateToSet, getYear( dateWithYear ), getMonth( dateWithMonth ) );
    }

    /* *********** *
     * Conversions *
     * *********** */

    /**
     * Converts a {@link Date} into a {@link Calendar}
     *
     * @param date the {@link Date} to convert
     * @return a {@link Calendar} instance of the provided Date
     */
    public static Calendar toCalendar( final Date date ) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTime( date );
        return calendar;
    }

    /**
     * Converts a {@link Date} into a {@link LocalDate}
     *
     * @param date the {@link Date} to convert
     * @return a {@link LocalDate} instance of the provided Date
     */
    public static LocalDate toLocalDate( final Date date ) {
        return new java.sql.Date( date.getTime() ).toLocalDate();
    }

    /**
     * Converts the time part of a {@link Date} into the number of minutes
     *
     * @param dateWithTime the {@link Date} with time to convert
     * @return the number of minutes into the day that time represents
     */
    public static int convertToMinutes( final Date dateWithTime ) {
        final Calendar calendar = toCalendar( dateWithTime );
        final int hours = calendar.get( Calendar.HOUR_OF_DAY );
        final int minutes = calendar.get( Calendar.MINUTE );

        return ( ( hours * UnitConstants.MINUTES_IN_AN_HOUR ) + minutes );
    }

    /**
     * Converts an hour and minute value into the number of minutes
     *
     * @param hours   the hour part of the time
     * @param minutes the minutes part of the time
     * @return the number of minutes into the day that time represents
     */
    public static int convertToMinutes( final int hours, final int minutes ) {
        return ( hours * UnitConstants.MINUTES_IN_AN_HOUR ) + minutes;
    }

    /**
     * Converts a Date to milliseconds
     * <p>
     * A method already exists for this, but it might make it more readable
     *
     * @param date the {@link Date} to convert
     * @return the Date in milliseconds
     */
    private static long toMilliseconds( final Date date ) {
        return date.getTime();
    }

    /**
     * Converts a String date into another String date with a different format
     *
     * @param stringToConvert the String date to convert
     * @param fromFormat      the format of the stringToConvert
     * @param toFormat        the format to covert into
     * @return the provided dateString in the 'toFormat' format
     * @throws ParseException if the provided date cannot be parsed
     */
    public static String convertFormat( final String stringToConvert, final String fromFormat, final String toFormat )
            throws ParseException {
        return convertFormat( stringToConvert,
                FastDateFormat.getInstance( fromFormat ),
                FastDateFormat.getInstance( toFormat ) );
    }

    /**
     * Converts a String date into another String date with a different format
     *
     * @param stringToConvert the String date to convert
     * @param fromFormat      the {@link FastDateFormat} to convert the stringToConvert from
     * @param toFormat        the {@link FastDateFormat} to convert the stringToConvert to
     * @return the provided dateString in the 'toFormat' format
     * @throws ParseException if the provided date cannot be parsed
     */
    public static String convertFormat( final String stringToConvert,
                                        final FastDateFormat fromFormat,
                                        final FastDateFormat toFormat ) throws ParseException {
        return toFormat.format( fromFormat.parse( stringToConvert ) );
    }


    /**
     * Creates a list of dates starting from the date provided, and incrementing by the given number of days
     *
     * @param startDate    the first {@link Date} to start with
     * @param numberOfDays the number of days to add, including the start date
     * @return a list of Dates
     */
    public static LinkedList<Date> getDatesAsList( final Date startDate, final int numberOfDays ) {
        final LinkedList<Date> requestedDates = new LinkedList<>();

        int i = 0;
        while ( i < numberOfDays )
        {
            requestedDates.add( addDays( toCalendar( startDate ), i ) );
            i++;
        }

        return requestedDates;
    }

    /* ************ *
     * Calculations *
     * ************ */

    /**
     * Calculates the number of milliseconds between two dates
     *
     * @param startDate the start {@link Date}
     * @param endDate   the end {@link Date}
     * @return the number of milliseconds between the two dates
     */
    public static long millisecondsBetween( final Date startDate, final Date endDate ) {
        return endDate.getTime() - startDate.getTime();
    }

    /**
     * Calculates the number of seconds between two dates
     *
     * @param startDate the start {@link Date}
     * @param endDate   the end {@link Date}
     * @return the number seconds between the two dates
     */
    public static long secondsBetween( final Date startDate, final Date endDate ) {
        return millisecondsBetween( startDate, endDate ) / 1000;
    }

    /**
     * Calculates the number of minutes between two dates
     *
     * @param startDate the start {@link Date}
     * @param endDate   the end {@link Date}
     * @return the number of minutes between the two dates
     */
    public static int minutesBetween( final Date startDate, final Date endDate ) {
        return ( int ) ( millisecondsBetween( startDate, endDate ) / UnitConstants.MILLISECONDS_IN_A_MINUTE );
    }

    /**
     * Calculates the number of hours between two dates
     *
     * @param startDate the start {@link Date}
     * @param endDate   the end {@link Date}
     * @return the number of hours between the two dates
     */
    public static int hoursBetween( final Date startDate, final Date endDate ) {
        return minutesBetween( startDate, endDate ) / UnitConstants.MINUTES_IN_AN_HOUR;
    }

    /**
     * Calculates the number of days between two dates
     *
     * @param startDate the start {@link Date}
     * @param endDate   the end {@link Date}
     * @return the number of days between the two dates
     */
    public static int daysBetween( final Date startDate, final Date endDate ) {
        final LocalDate fromLocalDate = toLocalDate( startDate );
        final LocalDate toLocalDate = toLocalDate( endDate );

        return ( int ) ChronoUnit.DAYS.between( fromLocalDate, toLocalDate );
    }

    /**
     * Calculates the number of days between two dates exactly without rounding and including decimals
     *
     * @param startDate the start {@link Date}
     * @param endDate   the end {@link Date}
     * @return the exact number of days, including decimals, between the two dates
     */
    public static double daysBetweenExact( final Date startDate, final Date endDate ) {
        final double millisecondsBetween = millisecondsBetween( startDate, endDate );
        return millisecondsBetween / 1000 / 60 / 60 / 24;
    }

    /**
     * Calculates the number of days between two dates exactly without rounding and including decimals
     *
     * @param startMillis the start {@link Date}
     * @param endMillis   the end {@link Date}
     * @return the exact number of days, including decimals, between the two dates
     */
    public static double daysBetweenExact( final long startMillis, final long endMillis ) {
        return ( double ) ( endMillis - startMillis ) / 1000 / 60 / 60 / 24;
    }

    /**
     * Calculate requested duration from request or elapsed time in days (number of days concerned by reservation)
     *
     * @param startDate the start {@link Date}
     * @param endDate   the end {@link Date}
     * @return the number of days spanned by the given date
     */
    public static int daysSpanned( final Date startDate, final Date endDate ) {
        final LocalDate fromLocalDate = toLocalDate( startDate );
        final LocalDate toLocalDate = toLocalDate( endDate );

        return ( int ) ChronoUnit.DAYS.between( fromLocalDate, toLocalDate.plusDays( 1 ).atStartOfDay().toLocalDate() );
    }

    /**
     * Calculates the number of months between two dates
     *
     * @param startDate the start {@link Date}
     * @param endDate   the end {@link Date}
     * @return the number of months between the two dates
     */
    public static int monthsBetween( final Date startDate, final Date endDate ) {
        final LocalDate fromLocalDate = toLocalDate( startDate );
        final LocalDate toLocalDate = toLocalDate( endDate );

        return ( int ) ChronoUnit.MONTHS.between( fromLocalDate, toLocalDate );
    }

    /**
     * Calculates the number of years between two dates
     *
     * @param startDate the start {@link Date}
     * @param endDate   the end {@link Date}
     * @return the number of years between the two dates
     */
    public static int yearsBetween( final Date startDate, final Date endDate ) {
        final LocalDate fromLocalDate = toLocalDate( startDate );
        final LocalDate toLocalDate = toLocalDate( endDate );

        return ( int ) ChronoUnit.YEARS.between( fromLocalDate, toLocalDate );
    }

    /* **************** *
     * Get Unit Methods *
     * **************** */

    /**
     * Retrieves the day of the week, where:
     * Sunday = 1
     * Monday = 2
     * Tuesday = 3
     * Wednesday = 4
     * Thursday = 5
     * Friday = 6
     * Saturday = 7
     * <p>
     * Replaces the deprecated {@link Date#getDay()} method.
     * WARNING - getDay() sets Sunday as '0', but with Calendar it is '1'
     *
     * @param date the {@link Date} to get the day of the week from
     * @return the integer day of the week
     */
    public static int getDayOfWeek( final Date date ) {
        final Calendar calendar = toCalendar( date );
        return calendar.get( Calendar.DAY_OF_WEEK );
    }

    /**
     * Retrieves the hour of the day in 24-hour format, where:
     * 00:30am = 0
     * 11:15am = 11
     * 1:00pm = 13
     * 11:45pm = 23
     * Replaces the deprecated {@link Date#getHours()} method
     *
     * @param date the {@link Date} to get the hour of the day from
     * @return integer hour, in 24-hour format
     */
    public static int getHours( final Date date ) {
        final Calendar calendar = toCalendar( date );
        return calendar.get( Calendar.HOUR_OF_DAY );
    }

    /**
     * Retrieves the day of the month, where:
     * 1st = 1
     * 2nd = 2
     * 31st = 31
     * etc.
     * Replaces the deprecated {@link Date#getDate()} method
     *
     * @param date the {@link Date} to get the day of the month from
     * @return integer day
     */
    public static int getDay( final Date date ) {
        final Calendar calendar = toCalendar( date );
        return calendar.get( Calendar.DAY_OF_MONTH );
    }

    /**
     * Retrieves the month from the date, where:
     * January = 0
     * February = 1
     * December = 11
     * etc.
     * Replaces the deprecated {@link Date#getMonth()} method
     *
     * @param date the {@link Date} to get the month from
     * @return integer month
     */
    public static int getMonth( final Date date ) {
        final Calendar calendar = toCalendar( date );
        return calendar.get( Calendar.MONTH );
    }

    /**
     * Retrieves the year from the date
     * <p>
     * Replaces the deprecated {@link Date#getYear()} method
     *
     * @param date the {@link Date} to get the year from
     * @return integer year
     */
    public static int getYear( final Date date ) {
        final Calendar calendar = toCalendar( date );
        return calendar.get( Calendar.YEAR );
    }

    /**
     * Retrieves the minutes of the time
     * 00:30am = 30
     * 11:15am = 15
     * 1:00pm = 0
     * 11:45pm = 45
     * etc.
     * <p>
     * Replaces the deprecated {@link Date#getMinutes()} method
     *
     * @param date the {@link Date} to get the minutes from
     * @return integer minutes
     */
    public static int getMinutes( final Date date ) {
        final Calendar calendar = toCalendar( date );
        return calendar.get( Calendar.MINUTE );
    }

    /**
     * Retrieves the seconds of the time
     * <p>
     * Replaces the deprecated {@link Date#getSeconds()} ()} method
     *
     * @param date the {@link Date} to get the seconds from
     * @return integer seconds
     */
    public static int getSeconds( final Date date ) {
        final Calendar calendar = toCalendar( date );
        return calendar.get( Calendar.SECOND );
    }

    /**
     * get the current year according to the server.
     *
     * @return the current year in 4-digit format
     */
    public static int getCurrentYear() {
        return getYear( new Date() );
    }

    /**
     * get the current month according to the server.
     *
     * @return the current month in 2-digit format
     */
    public static int getCurrentMonth() {
        return getMonth( new Date() );
    }

    /**
     * Get the start of the given year as a Date.
     *
     * @param year the year to use for the date
     * @return the start Date for the given year
     */
    public static Date getStartOfYear( final int year ) {
        return setTimeEvent( setDate( year, 0, 1 ), TimeEvent.START_OF_YEAR );
    }

    /**
     * Get the start of today's date as a Date.
     *
     * @return the today's Date with the time reset to the start of the day
     */
    public static Date getStartOfToday() {
        return DateTimeUtils.setTimeEvent( new Date(), TimeEvent.START_OF_DAY );
    }

    /**
     * Get the end of today's date as a Date.
     *
     * @return the today's Date with the time reset to the end of the day
     */
    public static Date getEndOfToday() {
        return DateTimeUtils.setTimeEvent( new Date(), TimeEvent.END_OF_DAY );
    }

    /**
     * Get the end of the given year as a Date.
     *
     * @param year the year to use for the date
     * @return the end Date for the given year
     */
    public static Date getEndOfYear( final int year ) {
        return setTimeEvent( setDate( year, 11, 31 ), TimeEvent.END_OF_DAY );
    }

    /**
     * Returns the amount of millis to add to UTC to get local time
     *
     * @param shopTimeZone the String TimeZone of the shop
     * @return the offset in milliseconds
     */
    public static long getTimeZoneOffsetMillis( final String shopTimeZone ) {
        final TimeZone zone;

        if ( StringUtils.isEmpty( shopTimeZone ) )
        {
            zone = TimeZone.getDefault();
        }
        else
        {
            zone = TimeZone.getTimeZone( shopTimeZone );
        }
        return zone.getOffset( System.currentTimeMillis() );
    }

    /* ***************** *
     * To String Methods *
     * ***************** */

    /**
     * Formats the provided minutes into a store time by dividing into hours and minutes and calling
     * {@link #toStringTime(int, int)}
     *
     * @param minutes the minutes to format
     * @return the string formatted store time as HH:mm
     */
    public static String minutesToTime( final int minutes ) {
        return toStringTime( ( minutes / UnitConstants.MINUTES_IN_AN_HOUR ),
                ( minutes % UnitConstants.MINUTES_IN_AN_HOUR ) );
    }

    /**
     * Formats the provided minutes into a store time by converting the values into a HH:mm String format
     * <p>
     * This could also probably be done with a date formatter (i.e. FastDateFormat) but this is consistent and Java-lib
     * friendly.
     *
     * @param date the date to format
     * @return the string formatted store time as HH:mm
     */
    public static String toStringTime( final Date date ) {
        final Calendar calendar = toCalendar( date );

        return String.format( Format.TIME_24H,
                calendar.get( Calendar.HOUR_OF_DAY ),
                calendar.get( Calendar.MINUTE ) );
    }

    /**
     * Formats the provided Date by converting it into a 'yyyy-MM-dd' String ISO Date format
     *
     * @param date the date to format
     * @return the string formatted store date as yyyy-MM-dd
     */
    public static String toIsoStringDate( final Date date ) {
        if ( date != null )
        {
            return FastDateFormat.getInstance( Format.ISO_8601_DATE_ONLY ).format( date );
        }
        return null;
    }

    /**
     * Formats the provided Date by converting it into a 'yyyy-MM-dd'T'HH:mm:ss' String ISO DateTime format
     *
     * @param date the date to format
     * @return the string formatted store date as yyyy-MM-dd'T'HH:mm:ss
     */
    public static String toIsoStringDateTime( final Date date ) {
        if ( date != null )
        {
            return FastDateFormat.getInstance( Format.ISO_8601_DATE_TIME ).format( date );
        }
        return null;
    }

    /**
     * Formats the provided minutes into a store time by converting the values into a HH:mm String format
     *
     * @param hours   the hours to format
     * @param minutes the minutes to format
     * @return the string formatted store time as HH:mm
     */
    public static String toStringTime( final int hours, final int minutes ) {
        return String.format( Format.TIME_24H, hours, minutes );
    }

    /* *********** *
     * Add Methods *
     * *********** */

    /**
     * Adds the given number of days to the {@link Date} provided
     *
     * @param dateToAdjust the Date to adjust the minutes on
     * @param daysToAdd    the number of days to add to the Date
     */
    public static void addDays( final Date dateToAdjust, final long daysToAdd ) {
        final Calendar calendar = toCalendar( dateToAdjust );
        calendar.add( Calendar.DAY_OF_MONTH, Math.toIntExact( daysToAdd ) );
        dateToAdjust.setTime( calendar.getTime().getTime() );
    }

    /**
     * Adds the given number of days to the {@link Calendar} provided
     *
     * @param dateToAdjust the Date to adjust the minutes on
     * @param daysToAdd    the number of days to add to the Date
     * @return the {@link Date} equivalent
     */
    public static Date addDays( final Calendar dateToAdjust, final long daysToAdd ) {
        dateToAdjust.add( Calendar.DAY_OF_MONTH, Math.toIntExact( daysToAdd ) );
        return dateToAdjust.getTime();
    }

    /**
     * Adds the given number of days to the current date
     *
     * @param daysToAdd the number of days to add to the Date
     * @return the {@link Date} with the number of days added
     */
    public static Date addDays( final long daysToAdd ) {
        final Calendar dateToAdjust = Calendar.getInstance();
        dateToAdjust.add( Calendar.DAY_OF_MONTH, Math.toIntExact( daysToAdd ) );
        return dateToAdjust.getTime();
    }

    /**
     * Adds the given number of minutes to the {@link Date} provided
     *
     * @param dateToAdjust the Date to adjust the minutes on
     * @param minutesToAdd the number of minutes to add to the Date
     */
    public static void addMinutes( final Date dateToAdjust, final long minutesToAdd ) {
        final Calendar calendar = toCalendar( dateToAdjust );
        calendar.add( Calendar.MINUTE, Math.toIntExact( minutesToAdd ) );
        dateToAdjust.setTime( calendar.getTime().getTime() );
    }


    /**
     * Adds the given number of minutes to a new {@link Date}
     *
     * @param minutesToAdd the number of minutes to add to the Date
     * @return the {@link Date} equivalent
     */
    public static Date addMinutes( final long minutesToAdd ) {
        final Calendar calendar = Calendar.getInstance();
        calendar.add( Calendar.MINUTE, Math.toIntExact( minutesToAdd ) );
        return calendar.getTime();
    }

    /* **************** *
     * Subtract Methods *
     * **************** */

    /**
     * Subtracts the given number of minutes from the {@link Date} provided
     *
     * @param dateToAdjust      the Date to adjust the minutes on
     * @param minutesToSubtract the number of minutes to subtract from the Date
     */
    public static void minusMinutes( final Date dateToAdjust, final long minutesToSubtract ) {
        addMinutes( dateToAdjust, -minutesToSubtract );
    }

    /* *************** *
     * To Date Methods *
     * *************** */

    /**
     * Attempts to parse the provided ISO 8601 String into a Date
     *
     * @param dateTimeString the String representation to parse
     *                       (format: {@link Format#ISO_8601_DATE_TIME}
     * @return the {@link Date} equivalent of the String
     * @throws ParseException if the date cannot be parsed (left to implementing methods to handle)
     */
    public static Date iso8601StringToDate( final String dateTimeString ) throws ParseException {
        return Formatter.ISO_8601_DATE_TIME.parse( dateTimeString );
    }

    /* ****************** *
     * Date Check Methods *
     * ****************** */

    /**
     * Checks if the provided date is after the current date
     *
     * @param dateToCheck the date to compare to
     * @return TRUE if the provided date is in the future, or FALSE if the date is either NULL or in the past
     */
    public static boolean isInFuture( final Date dateToCheck ) {
        if ( dateToCheck != null )
        {
            return new Date().before( dateToCheck );
        }
        return false;
    }

    /**
     * Checks if the provided dateToCheck is within the fromDate and toDate (inclusive).
     * This is done by checking that the date isn't after the fromDate and isn't
     *
     * @param dateToCheck the date to check
     * @param fromDate    the Start Date to set as the lower threshold
     * @param toDate      the End Date to set as the upper threshold
     * @return if the dateToCheck is after the start date and before the end date
     */
    public static boolean isBetween( final Date dateToCheck, final Date fromDate, final Date toDate ) {
        return !dateToCheck.before( fromDate ) && !dateToCheck.after( toDate );
    }

    /**
     * Checks if either of the provided start/end dates are within the provided start/end range
     *
     * @param startDateToCheck the Start Date to check (must be within the range)
     * @param endDateToCheck   the End Date to check (must be within the range)
     * @param startDateRange   the Start Date to set as the lower threshold
     * @param endDateRange     the End Date to set as the upper threshold
     * @return TRUE if the startDateToCheck and endDateToCheck overlaps within startDateRange and endDateRange
     */
    public static boolean isWithin( final Date startDateToCheck,
                                    final Date endDateToCheck,
                                    final Date startDateRange,
                                    final Date endDateRange ) {
        return DateTimeUtils.isBetween( startDateToCheck, startDateRange, endDateRange ) &&
                DateTimeUtils.isBetween( endDateToCheck, startDateRange, endDateRange );
    }

    /**
     * Checks if the provided date is within the number of days specified
     *
     * @param dateToCheck the Date to check
     * @param days        the number of days to check within
     * @return TRUE if the date is within the number of days specified
     */
    public static boolean isNotWithinDays( final Date dateToCheck, final int days ) {
        return !isWithinDays( dateToCheck, days );
    }

    /**
     * Checks if the provided date is within the number of days specified
     *
     * @param dateToCheck the Date to check
     * @param days        the number of days to check within
     * @return TRUE if the date is within the number of days specified
     */
    public static boolean isWithinDays( final Date dateToCheck, final int days ) {
        return dateToCheck.before( addDays( days ) );
    }

    /**
     * Parses the dates and confirms if they are the same
     *
     * @param date1     the first date to parse
     * @param date2     the second date to parse
     * @param formatter the {@link FastDateFormat} to use
     * @return TRUE, if the String dates are the same
     */
    public static boolean isSameDate( final Date date1, final Date date2, final FastDateFormat formatter ) {
        return formatter.format( date1 ).equals( formatter.format( date2 ) );
    }

    /**
     * Checks if the provided date is 'today'.  Time element is ignored
     *
     * @param date the date to check
     * @return TRUE if the date provided is the same as today's date
     */
    public static boolean isToday( final Date date ) {
        return isSameDate( date, new Date(), Formatter.ISO_8601_DATE_ONLY );
    }

    /**
     * Validates a String Date/Time unit and returns the parsed Date object
     *
     * @param valueToParse   the String date or time unit in the given format
     * @param fastDateFormat the {@link FastDateFormat} to use to parse the date or time unit
     * @param unit           the type of Date/Time unit (for error logging output)
     * @return the parsed Date, or an exception is thrown if invalid
     */
    public static Date validateDateUnit( final String valueToParse,
                                         final FastDateFormat fastDateFormat,
                                         final String unit ) throws InvalidDateException {
        try
        {
            final Date parsedDate = fastDateFormat.parse( valueToParse );
            if ( fastDateFormat.format( parsedDate ).equals( valueToParse ) )
            {
                return parsedDate;
            }
        }
        catch ( final ParseException parseException )
        {
            // Drop through
        }
        final String error = String.format( "Invalid " + unit + " format for String: %s; should be %s",
                valueToParse,
                fastDateFormat.getPattern() );
        LOGGER.severe( error );
        throw new InvalidDateException( error );
    }

    /**
     * Checks if the provided timeZone is currently observing DST.
     * Means we can make the tests more robust
     *
     * @param timeZone the timeZone to check
     * @return true, if the timeZone is currently within DST
     */
    public static boolean isCurrentlyDST( final String timeZone ) {
        return TimeZone.getTimeZone( timeZone ).inDaylightTime( new Date( System.currentTimeMillis() ) );
    }

    /* ***************** *
     * Constants Classes *
     * ***************** */

    /**
     * Date Time Format constants
     * TODO hopefully move the locale-specific ones into an enum at some point (with country codes etc)
     */
    public abstract static class Format {
        /*
         * ISO 8601
         */
        public static final String ISO_8601_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss";
        public static final String ISO_8601_DATE_ONLY = "yyyy-MM-dd";
        public static final String ISO_8601_TIME_ONLY = "HH:mm";

        /*
         * Units
         */
        public static final String YEAR_ONLY = "yyyy";
        public static final String MONTH_ONLY = "MMM";
        public static final String MONTH_NAME = "MMMM";
        public static final String TIME_24H = "%02d:%02d";

        /*
         * Locale-specific
         */
        public static final String UK_DATE = "dd/MM/yyyy";
        public static final String UK_DATE_SHORT_YEAR = "dd/MM/yy";
        public static final String UK_DATE_COMPACT = "dd/MM";
        public static final String UK_TIME_STAMP = "dd/MM/yy @ HH:mm";

        public static final String US_DATE = "MM/dd/yyyy";
        public static final String US_DATE_SHORT_YEAR = "MM/dd/yy";
        public static final String US_DATE_COMPACT = "MM/dd";
        public static final String US_TIME = "hh:mm a";

        public static final String FR_DATE = "dd.MM.yyyy";
        public static final String JP_DATE = "yyyy/MM/dd";

        /**
         * No instantiation
         */
        private Format() {
        }

    }

    /**
     * Date format formatters
     * TODO hopefully move the locale-specific ones into an enum at some point (with country codes etc)
     */
    public abstract static class Formatter {

        /*
         * ISO 8601
         */
        public static final FastDateFormat ISO_8601_DATE_ONLY = FastDateFormat.getInstance( Format.ISO_8601_DATE_ONLY );
        public static final FastDateFormat ISO_8601_DATE_TIME = FastDateFormat.getInstance( Format.ISO_8601_DATE_TIME );
        public static final FastDateFormat ISO_8601_TIME_ONLY = FastDateFormat.getInstance( Format.ISO_8601_TIME_ONLY );

        /*
         * Units
         */
        public static final FastDateFormat MONTH_NAME = FastDateFormat.getInstance( Format.MONTH_NAME );
        public static final FastDateFormat MONTH_ONLY = FastDateFormat.getInstance( Format.MONTH_ONLY );
        public static final FastDateFormat YEAR_ONLY = FastDateFormat.getInstance( Format.YEAR_ONLY );
        public static final FastDateFormat TIME_24H = FastDateFormat.getInstance( Format.TIME_24H );

        /*
         * Locale-specific
         */
        public static final FastDateFormat UK_DATE = FastDateFormat.getInstance( Format.UK_DATE );
        public static final FastDateFormat UK_DATE_SHORT_YEAR = FastDateFormat.getInstance( Format.UK_DATE_SHORT_YEAR );
        public static final FastDateFormat UK_TIME_STAMP = FastDateFormat.getInstance( Format.UK_TIME_STAMP );
        public static final FastDateFormat UK_DATE_COMPACT = FastDateFormat.getInstance( Format.UK_DATE_COMPACT );

        public static final FastDateFormat US_DATE = FastDateFormat.getInstance( Format.US_DATE );
        public static final FastDateFormat US_DATE_SHORT_YEAR = FastDateFormat.getInstance( Format.US_DATE_SHORT_YEAR );
        public static final FastDateFormat US_DATE_COMPACT = FastDateFormat.getInstance( Format.US_DATE_COMPACT );
        public static final FastDateFormat US_TIME = FastDateFormat.getInstance( Format.US_TIME );

        public static final FastDateFormat FR_DATE = FastDateFormat.getInstance( Format.FR_DATE );
        public static final FastDateFormat JP_DATE = FastDateFormat.getInstance( Format.JP_DATE );

        /**
         * No instantiation
         */
        private Formatter() {
        }

    }

    /**
     * DateTime Unit Constants
     */
    private abstract static class UnitConstants {

        private static final int MILLISECONDS_IN_A_MINUTE = 60000;
        private static final int MINUTES_IN_AN_HOUR = 60;


        /**
         * No instantiation
         */
        private UnitConstants() {
        }

    }

    /* ************ *
     * Enum Classes *
     * ************ */

    public enum TimeEvent {
        START_OF_YEAR,
        START_OF_DAY,
        END_OF_DAY,
        END_OF_YEAR,
        ONE_WEEK_AGO
    }


}
