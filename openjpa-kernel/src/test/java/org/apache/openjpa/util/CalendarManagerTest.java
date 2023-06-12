package org.apache.openjpa.util;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.*;

import static org.mockito.ArgumentMatchers.any;
@RunWith(value = Parameterized.class)
public class CalendarManagerTest {
    private ProxyManager proxyManager;
    private Class calendar;
    private TimeZone timeZone;
    private boolean expectedException;



    @Before
    public void setUp() {
        proxyManager = new ProxyManagerImpl();
    }

    @After
    public void tearDown() throws Exception {
        proxyManager = null;
    }

    public CalendarManagerTest(CalendarProxyPartition calendarProxyPartition){
        this.calendar = calendarProxyPartition.calendar;
        this.timeZone = calendarProxyPartition.timeZone;
        this.expectedException = calendarProxyPartition.expectedException;
    }


    private static final class CalendarProxyPartition {
        private final Class calendar;
        private final TimeZone timeZone;
        private final boolean expectedException;

        private CalendarProxyPartition(Class calendar, TimeZone timeZone, boolean expectedException){
            this.calendar = calendar;
            this.timeZone = timeZone;
            this.expectedException = expectedException;
        }
    }


    @Parameterized.Parameters
    public static List<CalendarProxyPartition> getObjectType(){
        List<CalendarProxyPartition> calendarProxyParameters = new ArrayList<>();


        calendarProxyParameters.add(new CalendarProxyPartition(Calendar.class,TimeZone.getDefault(),false));
        calendarProxyParameters.add(new CalendarProxyPartition(Calendar.class,TimeZone.getTimeZone("UTC+0"),false));
        calendarProxyParameters.add(new CalendarProxyPartition(Calendar.class,TimeZone.getTimeZone("UTC+30"),false));
        calendarProxyParameters.add(new CalendarProxyPartition(Calendar.class,null,false));


        calendarProxyParameters.add(new CalendarProxyPartition(List.class,TimeZone.getDefault(),true));
        calendarProxyParameters.add(new CalendarProxyPartition(List.class,TimeZone.getTimeZone("UTC+0"),true));
        calendarProxyParameters.add(new CalendarProxyPartition(List.class,TimeZone.getTimeZone("UTC+30"),true));
        calendarProxyParameters.add(new CalendarProxyPartition(List.class,null,true));

        calendarProxyParameters.add(new CalendarProxyPartition(null,TimeZone.getDefault(),true));
        calendarProxyParameters.add(new CalendarProxyPartition(null,TimeZone.getTimeZone("UTC+0"),true));
        calendarProxyParameters.add(new CalendarProxyPartition(null,TimeZone.getTimeZone("UTC+30"),true));
        calendarProxyParameters.add(new CalendarProxyPartition(null,null,true));

        calendarProxyParameters.add(new CalendarProxyPartition(Integer.class,TimeZone.getDefault(),true));
        calendarProxyParameters.add(new CalendarProxyPartition(Integer.class,TimeZone.getTimeZone("UTC+0"),true));
        calendarProxyParameters.add(new CalendarProxyPartition(Integer.class,TimeZone.getTimeZone("UTC+30"),true));
        calendarProxyParameters.add(new CalendarProxyPartition(Integer.class,null,true));


        return calendarProxyParameters;
    }



    @Test
    public void addProxyCalendarMethodTest() {
        try {
            ProxyCalendar proxyCalendar = (ProxyCalendar) proxyManager.newCalendarProxy(calendar, timeZone);



            //Check on copy calendar to trigger addProxyCalendarMethods
            Calendar proxyCalendar1 = proxyManager.copyCalendar((Calendar) proxyCalendar);





            if(calendar == null){
                Assert.assertNull(proxyCalendar);
            }
            if(proxyCalendar != null){
                Assert.assertEquals(0,
                        ((Calendar) proxyCalendar).getCalendarType().compareTo(proxyCalendar1.getCalendarType()));
            }

            Assert.assertFalse(expectedException);
        }catch (Exception e){
            Assert.assertTrue(expectedException);
        }

    }
}
