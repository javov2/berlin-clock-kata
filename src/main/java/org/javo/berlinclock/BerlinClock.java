package org.javo.berlinclock;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ApplicationScoped
@Slf4j
public class BerlinClock {


    public Uni<String> convertDigitalTimeToBerlinTime(String digitalTime) {
        return Uni.createFrom().item(() -> digitalTime)
                .onItem().invoke(this::validateFormat)
                .onItem().transform(s -> calculateSecondsRow(s).await().indefinitely() +
                        calculateFiveHoursRow(s).await().indefinitely() +
                        calculateSingleHoursRow(s).await().indefinitely() +
                        calculateFiveMinutesRow(s).await().indefinitely() +
                        calculateSingleMinutesRow(s).await().indefinitely()
                );
    }

    public Uni<String> validateFormat(String digitalTime) {
        return Uni.createFrom().item(digitalTime)
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .onItem().invoke(this::verifyDigitalTimeRegex)
                .onFailure(AssertionError.class).transform(
                        t -> new IllegalArgumentException(t.getMessage()))
                .onFailure(NullPointerException.class).transform(
                        throwable -> new IllegalArgumentException("La hora en formato digital no puede ser nula."));

    }

    public Uni<String> calculateSingleMinutesRow(String digitalTime) {
        return Uni.createFrom().item(() -> splitDigitalTime(digitalTime, 2))
                .onItem().transform(this::computeNumberOfLampsInSingleMinutesRow)
                .onItem().transform(number -> generateSingleMinutesRowPattern(number, 4));
    }

    public Uni<String> calculateFiveMinutesRow(String digitalTime) {
        return Uni.createFrom().item(() -> splitDigitalTime(digitalTime, 2))
                .onItem().transform(this::computeNumberOfLampsInFiveMinutesRow)
                .onItem().transform(number -> generateFiveMinutesRowPattern(number, 11));
    }

    public Uni<String> calculateSingleHoursRow(String digitalTime) {
        return Uni.createFrom().item(() -> splitDigitalTime(digitalTime, 1))
                .onItem().transform(this::computeNumberOfLampsInSingleHoursRow)
                .onItem().transform(number -> generateSingleHoursRowPattern(number, 4));
    }

    public Uni<String> calculateFiveHoursRow(String digitalTime) {
        return Uni.createFrom().item(() -> splitDigitalTime(digitalTime, 1))
                .onItem().transform(this::computeNumberOfLampsInFiveHoursRow)
                .onItem().transform(number -> generateFiveHoursRowPattern(number, 4));
    }

    public Uni<String> calculateSecondsRow(String digitalTime) {
        return Uni.createFrom().item(() -> splitDigitalTime(digitalTime, 3))
                .onItem().transform(this::generateSecondsRowPattern);
    }


    private Integer computeNumberOfLampsInSingleMinutesRow(String timeSegment) {
        return calculateModule(convertTimeSegmentToInteger(timeSegment), 5);
    }

    private Integer computeNumberOfLampsInFiveMinutesRow(String timeSegment) {
        return calculateQuotient(convertTimeSegmentToInteger(timeSegment), 5);
    }

    private Integer computeNumberOfLampsInSingleHoursRow(String timeSegment) {
        return calculateModule(convertTimeSegmentToInteger(timeSegment), 5);
    }

    private Integer computeNumberOfLampsInFiveHoursRow(String timeSegment) {
        return calculateQuotient(convertTimeSegmentToInteger(timeSegment), 5);
    }

    private String generateSingleMinutesRowPattern(int numberOfY, int rowLength) {
        return yCharGenerator(numberOfY) + oCharGenerator(rowLength - numberOfY);
    }

    private String generateFiveMinutesRowPattern(int numberOfY, int rowLength) {
        String partialResult = yCharGenerator(numberOfY);
        return replaceYCharsForRChars(partialResult, 3, 2) + oCharGenerator(rowLength - numberOfY);
    }

    private String generateSingleHoursRowPattern(int numberOfR, int rowLength) {
        return rCharGenerator(numberOfR) + oCharGenerator(rowLength - numberOfR);
    }

    private String generateFiveHoursRowPattern(int numberOfR, int rowLength) {
        return rCharGenerator(numberOfR) + oCharGenerator(rowLength - numberOfR);
    }

    private String generateSecondsRowPattern(String digitalTime) {
        String result;
        if (convertTimeSegmentToInteger(digitalTime) % 2 == 0)
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

    private String splitDigitalTime(String digitalTime, int segment) {
        return digitalTime.split(":")[segment - 1];
    }

    private void verifyDigitalTimeRegex(String digitalTime) {
        String DIGITAL_TIME_REGEX = "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]";
        Pattern pattern = Pattern.compile(DIGITAL_TIME_REGEX);
        Matcher matcher = pattern.matcher(digitalTime);
        if (!matcher.matches())
            throw new AssertionError("The format is not valid");
    }

    private Integer convertTimeSegmentToInteger(String timeSegment) {
        return Integer.parseInt(timeSegment);
    }

    private Integer calculateModule(int dividend, int divisor) {
        return dividend % divisor;
    }

    private Integer calculateQuotient(int dividend, int divisor) {
        return dividend / divisor;
    }

}
