package org.javo.berlinclock;

import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.infrastructure.Infrastructure;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javo.berlinclock.utils.BerlinClockUtils;

import javax.enterprise.context.ApplicationScoped;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Slf4j
@ApplicationScoped
@RequiredArgsConstructor
public class BerlinClock {

    private final BerlinClockUtils berlinClockUtils;
    private final BerlinClockView berlinClockView;

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
                .onItem().transform(number -> berlinClockView.generateSingleMinutesRowPattern(number, 4));
    }

    public Uni<String> calculateFiveMinutesRow(String digitalTime) {
        return Uni.createFrom().item(() -> splitDigitalTime(digitalTime, 2))
                .onItem().transform(this::computeNumberOfLampsInFiveMinutesRow)
                .onItem().transform(number -> berlinClockView.generateFiveMinutesRowPattern(number, 11));
    }

    public Uni<String> calculateSingleHoursRow(String digitalTime) {
        return Uni.createFrom().item(() -> splitDigitalTime(digitalTime, 1))
                .onItem().transform(this::computeNumberOfLampsInSingleHoursRow)
                .onItem().transform(number -> berlinClockView.generateSingleHoursRowPattern(number, 4));
    }

    public Uni<String> calculateFiveHoursRow(String digitalTime) {
        return Uni.createFrom().item(() -> splitDigitalTime(digitalTime, 1))
                .onItem().transform(this::computeNumberOfLampsInFiveHoursRow)
                .onItem().transform(number -> berlinClockView.generateFiveHoursRowPattern(number, 4));
    }

    public Uni<String> calculateSecondsRow(String digitalTime) {
        return Uni.createFrom().item(() -> splitDigitalTime(digitalTime, 3))
                .onItem().transform(berlinClockView::generateSecondsRowPattern);
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

    private Integer computeNumberOfLampsInSingleMinutesRow(String timeSegment) {
        return berlinClockUtils.calculateModule(berlinClockUtils.convertTimeSegmentToInteger(timeSegment), 5);
    }

    private Integer computeNumberOfLampsInFiveMinutesRow(String timeSegment) {
        return berlinClockUtils.calculateQuotient(berlinClockUtils.convertTimeSegmentToInteger(timeSegment), 5);
    }

    private Integer computeNumberOfLampsInSingleHoursRow(String timeSegment) {
        return berlinClockUtils.calculateModule(berlinClockUtils.convertTimeSegmentToInteger(timeSegment), 5);
    }

    private Integer computeNumberOfLampsInFiveHoursRow(String timeSegment) {
        return berlinClockUtils.calculateQuotient(berlinClockUtils.convertTimeSegmentToInteger(timeSegment), 5);
    }

}
