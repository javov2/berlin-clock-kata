package org.javo.berlinclock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

class BerlinClockTest {

    private BerlinClock underTest;

    @BeforeEach
    void setUp() {
        underTest = new BerlinClock();
    }

    @ParameterizedTest(name = " {index} ==> Time ''{0}''")
    @ValueSource(strings = {"00:00:00", "23:59:59", "12:32:00", "12:34:00", "12:35:00"})
    void checkValidateFormatWithCorrectFormats(String time) {
        BerlinTime berlinTime = BerlinTime.builder().digitalTime(time).convertedDigitalTime("").build();
        BerlinTime result = underTest.validateFormat(berlinTime).await().indefinitely();
        assertEquals(berlinTime, result);
    }

    @ParameterizedTest(name = " {index} ==> Time ''{0}''")
    @ValueSource(strings = {"00", "assdas", "1asds1", "", "12:35:0"})
    void checkValidateFormatWithIncorrectFormats(String time) {
        BerlinTime berlinTime = BerlinTime.builder().digitalTime(time).convertedDigitalTime("").build();
        var result = underTest.validateFormat(berlinTime).await();
        assertThrows(IllegalArgumentException.class, result::indefinitely);
    }

    @Test
    void checkValidateFormatWithNull() {
        var result = underTest.validateFormat(null).await();
        assertThrows(IllegalArgumentException.class, result::indefinitely);
    }

    @ParameterizedTest(name = "{index} ==> Time ''{0}'' Row {1}")
    @CsvSource({"00:00:00, OOOO", "23:59:59, YYYY", "12:32:00, YYOO", "12:34:00,YYYY", "12:35:00, OOOO"})
    void checkSingleMinutesRow(String time, String row) {
        BerlinTime berlinTime = BerlinTime.builder().digitalTime(time).convertedDigitalTime("").build();
        BerlinTime result = underTest.calculateSingleMinutesRow(berlinTime).await().indefinitely();
        assertEquals(row, result.getConvertedDigitalTime());
    }

    @ParameterizedTest(name = "{index} ==> Time ''{0}'' Row {1}")
    @CsvSource({"00:00:00, OOOOOOOOOOO",
            "23:59:59, YYRYYRYYRYY",
            "12:04:00, OOOOOOOOOOO",
            "12:23:00, YYRYOOOOOOO",
            "12:35:00, YYRYYRYOOOO"})
    void checkFiveMinutesRow(String time, String row) {
        BerlinTime berlinTime = BerlinTime.builder().digitalTime(time).convertedDigitalTime("").build();
        BerlinTime result = underTest.calculateFiveMinutesRow(berlinTime).await().indefinitely();
        assertEquals(row, result.getConvertedDigitalTime());
    }

    @ParameterizedTest(name = "{index} ==> Time ''{0}'' Row {1}")
    @CsvSource({"00:00:00, OOOO",
            "23:59:59, RRRO",
            "02:04:00, RROO",
            "08:23:00, RRRO",
            "14:35:00, RRRR"})
    void checkSingleHoursRow(String time, String row) {
        BerlinTime berlinTime = BerlinTime.builder().digitalTime(time).convertedDigitalTime("").build();
        BerlinTime result = underTest.calculateSingleHoursRow(berlinTime).await().indefinitely();
        assertEquals(row, result.getConvertedDigitalTime());
    }

    @ParameterizedTest(name = "{index} ==> Time ''{0}'' Row {1}")
    @CsvSource({"00:00:00, OOOO",
            "23:59:59, RRRR",
            "02:04:00, OOOO",
            "08:23:00, ROOO",
            "16:35:00, RRRO"})
    void checkFiveHoursRow(String time, String row) {
        BerlinTime berlinTime = BerlinTime.builder().digitalTime(time).convertedDigitalTime("").build();
        BerlinTime result = underTest.calculateFiveHoursRow(berlinTime).await().indefinitely();
        assertEquals(row, result.getConvertedDigitalTime());
    }

    @ParameterizedTest(name = "{index} ==> Time ''{0}'' Row {1}")
    @CsvSource({"00:00:00, Y",
            "23:59:59, O",})
    void checkSecondsRow(String time, String row) {
        BerlinTime berlinTime = BerlinTime.builder().digitalTime(time).convertedDigitalTime("").build();
        BerlinTime result = underTest.calculateSecondsRow(berlinTime).await().indefinitely();
        assertEquals(row, result.getConvertedDigitalTime());
    }

    @ParameterizedTest(name = "{index} ==> Time ''{0}'' Row {1}")
    @CsvSource({"00:00:00, YOOOOOOOOOOOOOOOOOOOOOOO",
            "23:59:59, ORRRRRRROYYRYYRYYRYYYYYY",
            "16:50:06, YRRROROOOYYRYYRYYRYOOOOO",
            "11:37:01, ORROOROOOYYRYYRYOOOOYYOO",})
    void checkEntireBerlinClock(String time, String row) {
        String result = underTest.convertDigitalTimeToBerlinTime(time).await().indefinitely();
        assertEquals(row, result);
    }

}