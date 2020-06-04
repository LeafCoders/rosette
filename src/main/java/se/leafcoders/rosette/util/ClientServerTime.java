package se.leafcoders.rosette.util;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.TimeZone;

public class ClientServerTime {

    // Convert server time (UTC) to client time (Europe/Stockholm)
    public static LocalDateTime serverToClient(LocalDateTime serverDateTime) {
        if (serverDateTime == null) {
            return null;
        }
        final TimeZone clientTimeZone = TimeZone.getTimeZone("Europe/Stockholm");
        final ZonedDateTime utcDateTime = serverDateTime.atZone(ZoneOffset.UTC);
        final ZonedDateTime timezoneDateTime = utcDateTime.withZoneSameInstant(clientTimeZone.toZoneId());
        final LocalDateTime clientDateTime = timezoneDateTime.toLocalDateTime();
        return clientDateTime;
    }

    // Convert client time (Europe/Stockholm) to server time (UTC)
    public static LocalDateTime clientToServer(LocalDateTime clientDateTime) {
        if (clientDateTime == null) {
            return null;
        }
        final TimeZone clientTimeZone = TimeZone.getTimeZone("Europe/Stockholm");
        final ZonedDateTime timezoneDateTime = ZonedDateTime.of(clientDateTime, clientTimeZone.toZoneId());
        final ZonedDateTime utcDateTime = timezoneDateTime.withZoneSameInstant(ZoneOffset.UTC);
        return utcDateTime.toLocalDateTime();
    }

    public static LocalDateTime serverTimeNow() {
        return LocalDateTime.now(ZoneOffset.UTC);
    }
}