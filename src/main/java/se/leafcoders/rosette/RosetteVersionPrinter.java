package se.leafcoders.rosette;

import org.springframework.context.annotation.Configuration;

import lombok.extern.slf4j.Slf4j;

@Configuration
@Slf4j
public class RosetteVersionPrinter {

    public RosetteVersionPrinter(final RosetteSettings rosetteSettings) {
        log.info("Rosette " + rosetteSettings.getAppVersion() + " by LeafCoders");
    }
}
