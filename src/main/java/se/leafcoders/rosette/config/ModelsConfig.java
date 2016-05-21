package se.leafcoders.rosette.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import se.leafcoders.rosette.model.ForgottenPassword;
import se.leafcoders.rosette.model.User;
import se.leafcoders.rosette.service.DbService;

@Configuration
public class ModelsConfig {

    @Bean
    public DbService<User> dbUser() {
        return new DbService<User>(User.class);
    }

    @Bean
    public DbService<ForgottenPassword> dbForgottenPassword() {
        return new DbService<ForgottenPassword>(ForgottenPassword.class);
    }
}
