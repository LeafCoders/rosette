package se.leafcoders.rosette.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.persistence.model.User;
import se.leafcoders.rosette.persistence.repository.UserRepository;

@Service
public class CurrentUserService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public final CurrentUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user != null) {
            return new CurrentUser(user.getId(), user.getFullName(), user.getEmail(), user.getPassword(), user.getIsActive());
        }
        throw new UsernameNotFoundException("user not found");
    }

    public CurrentUser loadUserById(Long userId) {
        User user = userRepository.findOne(userId);
        if (user != null) {
            return new CurrentUser(userId, user.getFullName(), user.getEmail(), "", user.getIsActive());
        }
        return null;
    }
}
