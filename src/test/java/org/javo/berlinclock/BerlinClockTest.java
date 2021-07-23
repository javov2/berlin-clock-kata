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
        String result = underTest.validateFormat(time).subscribe().asCompletionStage().join();
        assertEquals(result, time);
    }

    @DisplayName("Berlin Clock Tests")
    @ParameterizedTest(name = "{index} ==> Time ''{0}'' Row {1}")
    @CsvSource({ "00:00:00, OOOO", "23:59:59, YYYY", "12:32:00, YYOO", "12:34:00,YYYY","12:35:00, OOOO" })
    public void checkSingleMinutesRow(String time, String row){
        String result = underTest.calculateSingleMinutesRow(time);
        assertEquals(time, result);
    }

}