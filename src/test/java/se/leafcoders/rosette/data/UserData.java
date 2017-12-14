package se.leafcoders.rosette.data;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import se.leafcoders.rosette.controller.dto.UserIn;
import se.leafcoders.rosette.persistence.model.User;

public class UserData {

    public static User admin() {
        User user = new User();
        user.setEmail("admin@admin.se");
        user.setFirstName("Admin");
        user.setLastName("Admin");
        user.setPassword(cryptPasword("password"));
        user.setIsActive(true);
        return user;
    }
    
    public static User user1() {
        User user = new User();
        user.setEmail("u1@ser.se");
        user.setFirstName("User");
        user.setLastName("One");
        user.setPassword(cryptPasword("password"));
        user.setIsActive(true);
        return user;
    }
    
    public static User upload() {
        User user = new User();
        user.setEmail("test@uploaduser.se");
        user.setFirstName("Upload");
        user.setLastName("User");
        user.setPassword(cryptPasword("password"));
        user.setIsActive(true);
        return user;
    }
    
    public static User user2() {
        User user = new User();
        user.setEmail("u2@ser.se");
        user.setFirstName("User");
        user.setLastName("Two");
        user.setPassword(cryptPasword("password"));
        user.setIsActive(true);
        return user;
    }

    public static UserIn missingAllProperties() {
        return new UserIn();
    }

    public static UserIn invalidProperties() {
        UserIn user = new UserIn();
        user.setEmail("noAtCharacter");
        user.setFirstName("");
        user.setLastName("");
        user.setPassword("");
        return user;
    }

    public static UserIn newUser() {
        UserIn user = new UserIn();
        user.setEmail("u2@ser.se");
        user.setFirstName("User");
        user.setLastName("Two");
        user.setPassword("password");
        return user;
    }
    
    public static UserIn newUser(String firstName, String lastName) {
        UserIn user = new UserIn();
        user.setEmail((firstName + "@" + lastName + ".se").toLowerCase());
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setPassword("password");
        return user;
    }
    
    public static UserIn newActiveUser(String firstName, String lastName) {
        UserIn user = newUser(firstName, lastName);
        user.setIsActive(true);
        return user;
    }
    
    private static String cryptPasword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }
}
