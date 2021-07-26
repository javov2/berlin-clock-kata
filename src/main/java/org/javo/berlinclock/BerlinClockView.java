package org.javo.berlinclock;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javo.berlinclock.utils.BerlinClockUtils;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Slf4j
@RequiredArgsConstructor
public class BerlinClockView {

    private final BerlinClockUtils berlinClockUtils;

    public String generateSingleMinutesRowPattern(int numberOfY, int rowLength) {
        return yCharGenerator(numberOfY) + oCharGenerator(rowLength - numberOfY);
    }

    public String generateFiveMinutesRowPattern(int numberOfY, int rowLength) {
        String partialResult = yCharGenerator(numberOfY);
        return replaceYCharsForRChars(partialResult, 3, 2) + oCharGenerator(rowLength - numberOfY);
    }

    public String generateSingleHoursRowPattern(int numberOfR, int rowLength) {
        return rCharGenerator(numberOfR) + oCharGenerator(rowLength - numberOfR);
    }

    public String generateFiveHoursRowPattern(int numberOfR, int rowLength) {
        return rCharGenerator(numberOfR) + oCharGenerator(rowLength - numberOfR);
    }

    public String generateSecondsRowPattern(String digitalTime) {
        String result;
        if (berlinClockUtils.convertTimeSegmentToInteger(digitalTime) % 2 == 0)
            result = yCharGenerator(1);
        else
            result = oCharGenerator(1);
        return result;
    }

    private String replaceYCharsForRChars(String partialResult, Integer positionMultiplier, Integer positionOffset) {
        char[] yCharArray = partialResult.toCharArray();
        int numberOfYChars = partialResult.length() / 3;
        char[] rCharArray = rCharGenerator(numberOfYChars).toCharArray();
        for (int rCharCounter = 0; rCharCounter < rCharArray.length; rCharCounter++) {
            yCharArray[(rCharCounter * positionMultiplier) + positionOffset] = rCharArray[rCharCounter];
        }
        return String.valueOf(yCharArray);
    }

    private String oCharGenerator(int numberOfcharsO) {
        StringBuilder stringBuilder = new StringBuilder(numberOfcharsO);
        for (int charOCounter = 0; charOCounter < numberOfcharsO; charOCounter++) {
            stringBuilder.insert(charOCounter, "O");
        }
        return stringBuilder.toString();
    }

    private String yCharGenerator(int numberOfcharsY) {
        StringBuilder stringBuilder = new StringBuilder(numberOfcharsY);
        for (int charOCounter = 0; charOCounter < numberOfcharsY; charOCounter++) {
            stringBuilder.insert(charOCounter, "Y");
        }
        return stringBuilder.toString();
    }

    private String rCharGenerator(int numberOfcharsR) {
        StringBuilder stringBuilder = new StringBuilder(numberOfcharsR);
        for (int charOCounter = 0; charOCounter < numberOfcharsR; charOCounter++) {
            stringBuilder.insert(charOCounter, "R");
        }
        return stringBuilder.toString();
    }

}
