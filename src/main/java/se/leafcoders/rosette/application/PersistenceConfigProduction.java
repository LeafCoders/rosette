package se.leafcoders.rosette.application;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Profile("production")
@Configuration
@EnableMongoAuditing
public class PersistenceConfigProduction extends PersistenceConfigDefault {

    @Override
    protected String getDatabaseName() {
        return "rosette";
    }
}
