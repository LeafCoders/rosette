package se.leafcoders.rosette;

import javax.servlet.Filter;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.filter.CharacterEncodingFilter;

@SpringBootApplication
public class RosetteApplication {

	/*
	 * 
	 * - Turn of CSRF?
	 * 
	 */
	
    public static void main(String[] args) {
        SpringApplication.run(RosetteApplication.class, args);
    }

    /**
     * Example code to setup stuff when application starts
     */
	@Bean
	public InitializingBean insertDefaultUsers() {
		return new InitializingBean() {
//			@Autowired
//			private UserRepository userRepository;

			public void afterPropertiesSet() {
//				addUser("admin", "admin");
//				addUser("user", "user");
			}
/*
			private void addUser(String username, String password) {
				User user = new User();
				user.setUsername(username);
				user.setPassword(new BCryptPasswordEncoder().encode(password));
				user.grantRole(username.equals("admin") ? UserRole.ADMIN : UserRole.USER);
				userRepository.save(user);
			}
 */
		};
	}

    /**
     * All requests must have URF-8 encoding 
     */
    @Bean
	public Filter characterEncodingFilter() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		characterEncodingFilter.setForceEncoding(true);
		return characterEncodingFilter;
	}    
}
