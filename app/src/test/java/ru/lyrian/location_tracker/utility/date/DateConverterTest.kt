package ru.lyrian.location_tracker.utility.date

import android.content.Context
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.MockitoAnnotations
import ru.lyrian.location_tracker.R
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.Month
import java.time.ZoneId

class DateConverterTest {
    private val localDateFormatterPattern = "dd.MM.yyyy"
    private val localDateOf04012254 =  LocalDate.of(2254, Month.JANUARY, 4)
    private val formattedDateStringOf04012254 = "04.01.2254"
    private val millisecondsOfDate04012254 = 8962435987623L
    private val millisecondsOfDate17121969 = -1234567890L
    private val secondsOfDate04012254 = millisecondsOfDate04012254 / 1000
    private val secondsOfDate17121969 = millisecondsOfDate17121969 / 1000

    @Mock
    private lateinit var contextMock: Context

    @InjectMocks
    private lateinit var dateConverterMock: DateConverter

    @Before
    fun setupMocks() {
        MockitoAnnotations.openMocks(this)
        setupContextMocks()
    }

    private fun setupContextMocks() {
        Mockito.`when`(contextMock.getString(R.string.local_date_formatter_pattern)).thenReturn(localDateFormatterPattern)
    }

    @Test
    fun `Convert positive epoch milliseconds to LocalDate`() {
        val localDate = this.dateConverterMock.epochMillisToLocalDate(millisecondsOfDate04012254)
        val expectedLocalDate = LocalDate.of(2254, Month.JANUARY, 4)
        assertEquals(expectedLocalDate, localDate)
    }

    @Test
    fun `Convert negative epoch milliseconds to LocalDate`() {
        val localDate = this.dateConverterMock.epochMillisToLocalDate(millisecondsOfDate17121969)
        val expectedLocalDate = LocalDate.of(1969, Month.DECEMBER, 17)
        assertEquals(expectedLocalDate, localDate)
    }

    @Test
    fun `Convert positive epoch seconds to LocalDate`() {
        val localDate = this.dateConverterMock.epochSecondsToLocalDate(secondsOfDate04012254)
        val expectedLocalDate = LocalDate.of(2254, Month.JANUARY, 4)
        assertEquals(expectedLocalDate, localDate)
    }

    @Test
    fun `Convert negative epoch seconds to LocalDate`() {
        val localDate = this.dateConverterMock.epochSecondsToLocalDate(secondsOfDate17121969)
        val expectedLocalDate = LocalDate.of(1969, Month.DECEMBER, 17)
        assertEquals(expectedLocalDate, localDate)
    }

    @Test
    fun `Convert epoch milliseconds to day range Pair of seconds`() {
        val dayRange = this.dateConverterMock.epochMillisToDayRangeSeconds(millisecondsOfDate04012254)
        val expectedPair = Pair(8962434000, 8962520399)
        assertEquals(expectedPair, dayRange)
    }

    @Test
    fun `Convert epoch seconds to day range Pair of seconds`() {
        val dayRange = this.dateConverterMock.epochSecondsToDayRangeSeconds(secondsOfDate04012254)
        val expectedPair = Pair(8962434000, 8962520399)
        assertEquals(expectedPair, dayRange)
    }

    @Test
    fun `Check if milliseconds is today`() {
        val currentLocalDateTimeMillis = LocalDateTime.now().toMillis()
        val isToday = this.dateConverterMock.epochMillisIsToday(currentLocalDateTimeMillis)
        assertTrue(isToday)
    }

    @Test
    fun `Check if milliseconds of day in the past is not today`() {
        val notToday = this.dateConverterMock.epochMillisIsToday(millisecondsOfDate17121969)
        assertEquals(false, notToday)
    }

    @Test
    fun `Convert LocalDate to formatted string`() {
        val formattedString = this.dateConverterMock.localDateToFormattedString(localDateOf04012254)
        assertEquals(formattedDateStringOf04012254, formattedString)
    }

    @Test
    fun `Convert epoch seconds to date as formatted string`() {
        val formattedString = this.dateConverterMock.epochSecondsToFormattedString(secondsOfDate04012254)
        assertEquals(formattedDateStringOf04012254, formattedString)
    }

    private fun LocalDateTime.toMillis(zoneId: ZoneId = ZoneId.systemDefault()) = atZone(zoneId).toInstant().toEpochMilli()
}