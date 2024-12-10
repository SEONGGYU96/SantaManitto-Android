package org.sopt.santamanitto.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.GregorianCalendar
import java.util.Locale
import java.util.TimeZone

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
            val laterDate = utcFormat.parse(later)
            val earlyDate = utcFormat.parse(early)
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

    fun getDayDiffFromNow(utcFormatString: String): Int {
        return try {
            val utcString = convertUtcToKst(utcFormatString)
            val nowString = utcFormat.format(Date())
            getDayDiff(utcString, nowString)
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    fun isExpired(expirationDate: String): Boolean {
        return try {
            getDayDiffFromNow(expirationDate) <= 0
        } catch (e: Exception) {
            e.printStackTrace()
            true
        }
    }

    // GregorianCalendar -> Utc(KST)
    fun convertGregorianCalendarToUtc(calendar: GregorianCalendar): String {
        return utcFormat.format(Date(calendar.timeInMillis))
    }

    // Utc(KST) -> GregorianCalendar
    fun convertUtcToGregorianCalendar(utcFormatString: String): GregorianCalendar {
        return GregorianCalendar(KOREA_TIME_ZONE).apply {
            time = utcFormat.parse(utcFormatString) ?: throw IllegalArgumentException(WRONG_FORMAT)
        }
    }

    // UTC -> Calendar
    fun convertUtcToCalendar(utcFormatString: String): Calendar {
        return Calendar.getInstance(UTC_TIME_ZONE).apply {
            time = utcFormat.parse(utcFormatString) ?: throw IllegalArgumentException(WRONG_FORMAT)
        }
    }

    // Utc -> Utc(KST)
    fun convertUtcToKst(utcFormatString: String): String {
        return kstFormat.format(
            utcFormat.parse(utcFormatString) ?: throw IllegalArgumentException(WRONG_FORMAT)
        )
    }
}