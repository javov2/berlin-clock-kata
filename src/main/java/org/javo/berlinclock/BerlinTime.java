package org.javo.berlinclock;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(toBuilder = true)
public class BerlinTime {
    private final String digitalTime;
    private final String convertedDigitalTime;
}
