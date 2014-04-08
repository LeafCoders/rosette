package se.ryttargardskyrkan.rosette.application;

import org.springframework.context.annotation.Configuration;

@Configuration
public class DevPersistenceConfig extends PersistenceConfig {

    @Override
    protected String getDatabaseName() {
        return "rosette-test";
    }
}
