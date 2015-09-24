/*
 * 蘑菇街 Inc.
 * Copyright (c) 2010-2015 All Rights Reserved.
 *
 * Author: guangming
 * Create Date: 2015年9月24日 下午4:24:41
 */

package com.mogujie.jarvis.core.common.util;

import java.util.Calendar;
import java.util.Date;

/**
 * @author guangming
 *
 */
public class CalendarUtil {
    /**
     * 获取当前时间(now)前几天(offset)的日期
     *
     * @param Date d
     * @param int offset
     * @return Date
     */
    public static Date getDayBefore(Date now, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE)-offset);
        return cal.getTime();
    }

    /**
     * 获取当前时间(now)后几天(offset)的日期
     *
     * @param Date d
     * @param int offset
     * @return Date
     */
    public static Date getDayAfter(Date now, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.set(Calendar.DATE, cal.get(Calendar.DATE)+offset);
        return cal.getTime();
    }

    /**
     * 获取当前时间(now)上几周(offset)星期一的日期
     *
     * @param Date d
     * @param int offset
     * @return Date
     */
    public static Date getMondayOfWeekBefore(Date now, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.WEEK_OF_MONTH, -offset);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    /**
     * 获取当前时间(now)上几周(offset)星期日的日期
     *
     * @param Date d
     * @param int offset
     * @return Date
     */
    public static Date getSundayOfWeekBefore(Date now, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.WEEK_OF_MONTH, -offset);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
        return cal.getTime();
    }

    /**
     * 获取当前时间(now)上几周(offset)第一天日期(周日)
     *
     * @param Date d
     * @param int offset
     * @return Date
     */
    public static Date getFirstDayOfWeekBefore(Date now, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.WEEK_OF_MONTH, -offset);
        cal.set(Calendar.DAY_OF_WEEK, 1);
        return cal.getTime();
    }

    /**
     * 获取当前时间(now)上几周(offset)最后一天日期(周六)
     *
     * @param Date d
     * @param int offset
     * @return Date
     */
    public static Date getLastDayOfWeekBefore(Date now, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.WEEK_OF_MONTH, -offset);
        cal.set(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    /**
     * 获取当前时间(now)上几月(offset)第一天的日期
     *
     * @param Date d
     * @param int offset
     * @return Date
     */
    public static Date getFirstDayOfMonthBefore(Date now, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MONTH, -offset);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    /**
     * 获取当前时间(now)上几月(offset)最后一天的日期
     *
     * @param Date d
     * @param int offset
     * @return Date
     */
    public static Date getLastDayOfMonthBefore(Date now, int offset) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(now);
        cal.add(Calendar.MONTH, -offset);
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        Date lastDate = cal.getTime();
        lastDate.setDate(lastDay);
        return lastDate;
    }
}
