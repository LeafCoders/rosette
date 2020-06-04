package se.leafcoders.rosette.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.NonNull;
import se.leafcoders.rosette.endpoint.user.User;
import se.leafcoders.rosette.endpoint.user.UserRepository;

@Service
public class CurrentUserService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public CurrentUser loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);
        if (user != null) {
            String authority = getAuthority(user);
            if (authority != null) {
                return new CurrentUser(user.getId(), user.getFullName(), user.getEmail(), user.getPassword(),
                        user.getIsActive(), authority);
            } else {
                return new CurrentUser(user.getId(), user.getFullName(), user.getEmail(), user.getPassword(),
                        user.getIsActive());
            }
        }
        throw new UsernameNotFoundException("user not found");
    }

    public CurrentUser loadUserById(Long userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            String authority = getAuthority(user);
            if (authority != null) {
                return new CurrentUser(user.getId(), user.getFullName(), user.getEmail(), "", user.getIsActive(),
                        authority);
            } else {
                return new CurrentUser(user.getId(), user.getFullName(), user.getEmail(), "", user.getIsActive());
            }
        }
        return null;
    }

    private String getAuthority(@NonNull User user) {
        if (user.getIsSuperAdmin()) {
            return RosetteAuthority.SUPER_ADMIN;
        }
        return null;
    }
}
