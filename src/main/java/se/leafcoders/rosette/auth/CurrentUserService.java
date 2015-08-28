package se.leafcoders.rosette.auth;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.model.User;

@Service
public class CurrentUserService implements org.springframework.security.core.userdetails.UserDetailsService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public final CurrentUser loadUserByUsername(String username) throws UsernameNotFoundException {
        Query query = Query.query(new Criteria().orOperator(Criteria.where("email").is(username),
                Criteria.where("username").is(username)));
        List<User> users = mongoTemplate.find(query, User.class);
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("user not found");
        }
        User user = users.get(0);
        return new CurrentUser(user.getId(), user.getEmail(), user.getHashedPassword());
    }
    
    public CurrentUser loadUserById(String userId) {
        User user = mongoTemplate.findById(userId, User.class);
        if (user != null) {
            return new CurrentUser(userId, user.getEmail(), "");
        } else {
            return null;
        }
    }
}
