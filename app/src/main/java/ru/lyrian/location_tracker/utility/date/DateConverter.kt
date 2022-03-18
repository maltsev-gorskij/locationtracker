package ru.lyrian.location_tracker.utility.date

import android.content.Context
import ru.lyrian.location_tracker.R
import java.time.*
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class DateConverter @Inject constructor(val context: Context) {
    fun epochMillisToLocalDate(epochMillis: Long): LocalDate = LocalDateTime
        .ofInstant(Instant.ofEpochMilli(epochMillis), ZoneId.systemDefault())
        .toLocalDate()

    fun epochSecondsToLocalDate(epochSeconds: Long): LocalDate = LocalDateTime
        .ofInstant(Instant.ofEpochSecond(epochSeconds), ZoneId.systemDefault())
        .toLocalDate()

    fun epochMillisToDayRangeSeconds(epochMillis: Long): Pair<Long, Long> {
        val timeZone = ZoneId.systemDefault()
        val localDate = epochMillisToLocalDate(epochMillis)
        val startOfDay = localDate.atTime(LocalTime.MIN).atZone(timeZone).toInstant().toEpochMilli()
        val endOfDay = localDate.atTime(LocalTime.MAX).atZone(timeZone).toInstant().toEpochMilli()

        return Pair(TimeUnit.MILLISECONDS.toSeconds(startOfDay), TimeUnit.MILLISECONDS.toSeconds(endOfDay))
    }

    fun epochSecondsToDayRangeSeconds(epochSeconds: Long): Pair<Long, Long> {
        val timeZone = ZoneId.systemDefault()
        val localDate = epochSecondsToLocalDate(epochSeconds)
        val startOfDay = localDate.atTime(LocalTime.MIN).atZone(timeZone).toInstant().toEpochMilli()
        val endOfDay = localDate.atTime(LocalTime.MAX).atZone(timeZone).toInstant().toEpochMilli()

        return Pair(TimeUnit.MILLISECONDS.toSeconds(startOfDay), TimeUnit.MILLISECONDS.toSeconds(endOfDay))
    }

    fun epochSecondsToFormattedString(epochSeconds: Long): String =
        localDateToFormattedString(epochSecondsToLocalDate(epochSeconds))

    fun localDateToFormattedString(localDate: LocalDate): String =
        localDate.format(DateTimeFormatter.ofPattern(this.context.getString(R.string.local_date_formatter_pattern)))

    fun epochMillisIsToday(epochMillis: Long) = epochMillisToLocalDate(epochMillis).isEqual(LocalDate.now())
}