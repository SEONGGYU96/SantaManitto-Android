package org.sopt.santamanitto.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

/**
 * 클라이언트에서 사용하는 모든 시간 : KST(UTC +9) 기준
 *
 * 서버에서 사용하는 모든 시간 : UTC(UTC +0) 기준
 */

object TimeUtil {

    private const val UTC_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"
    private const val WRONG_FORMAT = "날짜 형식이 잘못되었습니다."

    private val UTC_TIME_ZONE: TimeZone = TimeZone.getTimeZone("UTC")
    private val KOREA_TIME_ZONE: TimeZone = TimeZone.getTimeZone("Asia/Seoul")

    private val utcFormat = SimpleDateFormat(UTC_DATE_FORMAT, Locale.KOREA).apply {
        timeZone = UTC_TIME_ZONE
    }
    private val kstFormat = SimpleDateFormat(UTC_DATE_FORMAT, Locale.KOREA).apply {
        timeZone = KOREA_TIME_ZONE
    }

    fun getDayDiff(later: String, early: String): Int {
        return try {
            val laterDate = kstFormat.parse(later)
            val earlyDate = kstFormat.parse(early)
            if (laterDate != null && earlyDate != null) {
                val diffInMillis = laterDate.time - earlyDate.time
                val diffInDays = (diffInMillis / (1000 * 60 * 60 * 24)).toInt()
                diffInDays
            } else {
                throw IllegalArgumentException(WRONG_FORMAT)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun getDayDiffFromNow(kstFormatString: String): Int {
        return getDayDiff(kstFormatString, kstFormat.format(Date()))
    }

    fun isExpired(utcFormatString: String): Boolean {
        return try {
            getDayDiffFromNow(convertUtcToKst(utcFormatString)) <= 0
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }

    // GregorianCalendar -> Utc(+0)
    fun convertGregorianCalendarToUtc(calendar: GregorianCalendar): String {
        return utcFormat.format(Date(calendar.timeInMillis))
    }

    // Utc(+0) -> Utc(KST)
    fun convertUtcToKst(utcFormatString: String): String {
        return kstFormat.format(
            utcFormat.parse(utcFormatString) ?: throw IllegalArgumentException(WRONG_FORMAT)
        )
    }

    // Utc(KST) -> GregorianCalendar
    fun convertKstToGregorianCalendar(kstFormatString: String): GregorianCalendar {
        return GregorianCalendar(KOREA_TIME_ZONE).apply {
            time = kstFormat.parse(kstFormatString) ?: throw IllegalArgumentException(WRONG_FORMAT)
        }
    }

    // Utc(KST) -> Calendar
    fun convertKstToCalendar(kstFormatString: String): Calendar {
        return Calendar.getInstance(KOREA_TIME_ZONE).apply {
            time = kstFormat.parse(kstFormatString) ?: throw IllegalArgumentException(WRONG_FORMAT)
        }
    }
}