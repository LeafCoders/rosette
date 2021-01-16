package se.leafcoders.rosette;

import java.util.Optional;

import org.springframework.boot.autoconfigure.flyway.FlywayMigrationStrategy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class PersistenceConfig {

    private final RosetteSettings rosetteSettings;

    // Clean database and run all migrations before running tests
    @Bean
    @Profile("test")
    public FlywayMigrationStrategy cleanMigrateStrategy() {
        return flyway -> {
            flyway.clean();
            flyway.migrate();
        };
    }

    @Bean
    @Profile("production")
    public FlywayMigrationStrategy productionMigrateStrategy() {
        return flyway -> {
            String command = Optional.ofNullable(rosetteSettings.getFlywayCommand()).orElse("migrate");
            switch (command) {
                case "migrate":
                    flyway.migrate();
                    return;
                case "info":
                    flyway.info();
                    break;
                case "validate":
                    flyway.validate();
                    break;
                case "repair":
                    flyway.repair();
                    break;
                default:
                    throw new RuntimeException("Invalid value '" + command + "' of property 'flywayCommand'.");
            }
            throw new RuntimeException("Flyway command '" + command
                    + "' has been executed with success. Stopping application. Remove property of property 'flywayCommand' and start application again.");
        };
    }

}
