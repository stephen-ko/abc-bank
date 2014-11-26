package com.abc;

import java.util.Calendar;
import java.util.Date;

public class DateProvider {
    private static DateProvider instance = null;

    private Calendar cal = null;
    public synchronized static DateProvider getInstance() {
        if (instance == null)
            instance = new DateProvider();
        return instance;
    }

    private DateProvider()
    {
    	cal = Calendar.getInstance();
    }
    
    public synchronized Date now() {
     	return cal.getTime();
    }
}
