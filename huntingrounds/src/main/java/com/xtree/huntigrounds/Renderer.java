package com.xtree.huntigrounds;

import sun.util.calendar.ZoneInfo;

import java.sql.Timestamp;

import static org.apache.commons.lang3.time.DateFormatUtils.format;

public class Renderer {
    public static String getTime(Timestamp t, ZoneInfo z){
        return format(t.getTime(), "yyyy-MM-dd HH:mm zzz", z);
    }
}
