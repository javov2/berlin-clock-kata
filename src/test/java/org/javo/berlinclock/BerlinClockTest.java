package org.javo.berlinclock;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import javax.inject.Inject;

import static org.junit.jupiter.api.Assertions.*;

public class BerlinClockTest {

    private BerlinClock underTest;

    @BeforeEach
    void setUp() {
        underTest = new BerlinClock();
    }

    @ParameterizedTest(name = " {index} ==> Time ''{0}''")
    @ValueSource(strings = { "00:00:00", "23:59:59", "12:32:00", "12:34:00", "12:35:00" })
    void checkValidateFormatWithCorrectFormats(String time) {
        String result = underTest.validateFormat(time).await().indefinitely();
        assertEquals(result, time);
    }

    @ParameterizedTest(name = " {index} ==> Time ''{0}''")
    @ValueSource(strings = { "00", "assdas", "1asds1", "", "12:35:0" })
    void checkValidateFormatWithIncorrectFormats(String time) {
        assertThrows(IllegalArgumentException.class,
                () -> underTest.validateFormat(time).await().indefinitely());
    }

    @Test
    void checkValidateFormatWithNull() {
        assertThrows(IllegalArgumentException.class,
                () -> underTest.validateFormat(null).await().indefinitely());
    }

    @ParameterizedTest(name = "{index} ==> Time ''{0}'' Row {1}")
    @CsvSource({ "00:00:00, OOOO", "23:59:59, YYYY", "12:32:00, YYOO", "12:34:00,YYYY","12:35:00, OOOO" })
    public void checkSingleMinutesRow(String time, String row){
        String result = underTest.calculateSingleMinutesRow(time).await().indefinitely();
        assertEquals(row, result);
    }

    @ParameterizedTest(name = "{index} ==> Time ''{0}'' Row {1}")
    @CsvSource({    "00:00:00, OOOOOOOOOOO",
                    "23:59:59, YYRYYRYYRYY",
                    "12:04:00, OOOOOOOOOOO",
                    "12:23:00, YYRYOOOOOOO",
                    "12:35:00, YYRYYRYOOOO" })
    public void checkFiveMinutesRow(String time, String row){
        String result = underTest.calculateFiveMinutesRow(time).await().indefinitely();
        assertEquals(row, result);
    }

    @ParameterizedTest(name = "{index} ==> Time ''{0}'' Row {1}")
    @CsvSource({    "00:00:00, OOOO",
            "23:59:59, RRRO",
            "02:04:00, RROO",
            "08:23:00, RRRO",
            "14:35:00, RRRR" })
    public void checkSingleHoursRow(String time, String row){
        String result = underTest.calculateSingleHoursRow(time).await().indefinitely();
        assertEquals(row, result);
    }

}