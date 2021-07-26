package org.javo.berlinclock.utils;

import lombok.extern.slf4j.Slf4j;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
@Slf4j
public class BerlinClockUtils {

    public Integer convertTimeSegmentToInteger(String timeSegment) {
        return Integer.parseInt(timeSegment);
    }

    public Integer calculateModule(int dividend, int divisor) {
        return dividend % divisor;
    }

    public Integer calculateQuotient(int dividend, int divisor) {
        return dividend / divisor;
    }

}
