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

    public Uni<String> convertDigitalTimeToBerlinTime(String digitalTime){
        return Uni.createFrom().item(() -> digitalTime)
                .onItem().invoke(this::validateFormat)
                .onItem().invoke(this::calculateSingleMinutesRow)
                .onFailure(AssertionError.class).transform(throwable -> new RuntimeException("Time format isn't valid"));
    }

    public String calculateSingleMinutesRow(String time){
        return "OOOO";
    }

    public Uni<String> validateFormat(String digitalTime){
        return Uni.createFrom().item(digitalTime)
                .runSubscriptionOn(Infrastructure.getDefaultWorkerPool())
                .onItem().invoke(s -> {
                    String regex = "([01]?[0-9]|2[0-3]):[0-5][0-9]:[0-5][0-9]";
                    Pattern pattern = Pattern.compile(regex);
                    log.info(regex);
                    Matcher matcher = pattern.matcher(digitalTime);
                    if (!matcher.matches())
                        throw new AssertionError("The format is not valid");
                });

    }

}
