package se.leafcoders.rosette.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;

public interface ServerTime {

    default LocalDateTime serverTimeNow() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}
