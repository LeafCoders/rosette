package se.leafcoders.rosette.application;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;

@Configuration
@EnableMongoAuditing
public class DevPersistenceConfig extends PersistenceConfig {

    @Override
    protected String getDatabaseName() {
        return "rosette-test";
    }
}
