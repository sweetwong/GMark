package sweet.wong.gmark

import org.junit.Test
import sweet.wong.gmark.utils.TimeUtils

class TimeUtilsTest {

    private val time15minutesAgo = System.currentTimeMillis() - 15 * 60 * 1000L

    private val time2HoursAgo = System.currentTimeMillis() - 2 * 60 * 60 * 1000L

    private val time3DaysAgo = System.currentTimeMillis() - 3 * 24 * 60 * 60 * 1000L

    private val time1WeekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L

    private val time1MonthsAgo = System.currentTimeMillis() - 30 * 24 * 60 * 60 * 1000L

    @Test
    fun testNow() {
        println(TimeUtils.getRelativeString(System.currentTimeMillis()))
    }

    @Test
    fun test15MinutesAgo() {
        println(TimeUtils.getRelativeString(time15minutesAgo))
    }

    @Test
    fun test2HoursAgo() {
        println(TimeUtils.getRelativeString(time2HoursAgo))
    }

    @Test
    fun test3DaysAgo() {
        println(TimeUtils.getRelativeString(time3DaysAgo))
    }

    @Test
    fun test1WeekAgo() {
        println(TimeUtils.getRelativeString(time1WeekAgo))
    }

    @Test
    fun test1MonthAgo() {
        println(TimeUtils.getRelativeString(time1MonthsAgo))
    }

}