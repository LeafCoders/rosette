package se.leafcoders.rosette.persistence.service;

import java.time.LocalDateTime;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.UserIn;
import se.leafcoders.rosette.controller.dto.UserOut;
import se.leafcoders.rosette.exception.ApiString;
import se.leafcoders.rosette.exception.SingleValidationException;
import se.leafcoders.rosette.exception.ValidationError;
import se.leafcoders.rosette.permission.PermissionAction;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.User;
import se.leafcoders.rosette.persistence.repository.UserRepository;

@Service
public class UserService extends PersistenceService<User, UserIn, UserOut> {

    // @Autowired
    // private SecurityService securityService;

    public UserService(UserRepository repository) {
        super(User.class, PermissionType.USERS, repository);
    }

    @Override
    protected User convertFromInDTO(UserIn dto, JsonNode rawIn, User item) {
        if (rawIn == null || rawIn.has("email")) {
            item.setEmail(dto.getEmail());
        }
        if (rawIn == null || rawIn.has("firstName")) {
            item.setFirstName(dto.getFirstName());
        }
        if (rawIn == null || rawIn.has("lastName")) {
            item.setLastName(dto.getLastName());
        }
        if (rawIn == null || rawIn.has("password")) {
            if (dto.getPassword() != null) {
                String hashedPassword = new BCryptPasswordEncoder().encode(dto.getPassword());
                item.setPassword(hashedPassword);
            }
        }
        if (rawIn == null || rawIn.has("isActive")) {
            item.setIsActive(dto.getIsActive());
        }
        return item;
    }

    @Override
    protected UserOut convertToOutDTO(User item) {
        UserOut dto = new UserOut();
        dto.setId(item.getId());
        dto.setEmail(item.getEmail());
        dto.setFirstName(item.getFirstName());
        dto.setLastName(item.getLastName());
        dto.setIsActive(item.getIsActive());
        dto.setLastLoginTime(item.getLastLoginTime());
        return dto;
    }

    private UserRepository repo() {
        return (UserRepository) repository;
    }

    @Override
    public User create(UserIn userIn, boolean checkPermissions) {
        return super.create(userIn, checkPermissions, (user) -> {
            if (user.getIsActive() == null) {
                user.setIsActive(false);
            }
            user.setLastLoginTime(LocalDateTime.now());
        });
    }

    @Override
    protected void extraValidation(User user, UserIn userIn) {
        checkChangeOfEmail(userIn);
        checkChangeOfIsActive(userIn);
    }

    private void checkChangeOfIsActive(UserIn userIn) {
        if (userIn.getIsActive() != null) {
            checkPermission(permissionValue(PermissionAction.ADMIN));
        }
    }

    private void checkChangeOfEmail(UserIn userIn) {
        if (userIn.getEmail() != null && repo().findByEmail(userIn.getEmail()) != null) {
            throw new SingleValidationException(new ValidationError("email", ApiString.EMAIL_NOT_UNIQUE));
        }
    }

    /*
     * @Override public List<User> readMany(final ManyQuery manyQuery) {
     * List<User> users = super.readMany(manyQuery); for (User user : users) {
     * user.setHashedPassword(null); } return users; }
     * 
     * @Override public void delete(String id, HttpServletResponse response) {
     * super.delete(id, response); securityService.resetPermissionCache(); }
     * 
     * public boolean changePassword(String id, String password) { User user =
     * read(id, false); user.setAllPasswords(password); security.validate(user);
     * mongoTemplate.save(user); return true; }
     */
}
