package se.leafcoders.rosette.endpoint.user;

import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import javax.servlet.http.HttpServletRequest;

import com.fasterxml.jackson.databind.JsonNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import se.leafcoders.rosette.core.exception.ApiString;
import se.leafcoders.rosette.core.exception.SingleValidationException;
import se.leafcoders.rosette.core.exception.ValidationError;
import se.leafcoders.rosette.core.persistable.PersistenceService;
import se.leafcoders.rosette.core.service.EmailTemplateService;
import se.leafcoders.rosette.endpoint.auth.Consent;
import se.leafcoders.rosette.endpoint.auth.ConsentRepository;
import se.leafcoders.rosette.endpoint.auth.SignupUserIn;
import se.leafcoders.rosette.util.ClientServerTime;

@Service
public class UserService extends PersistenceService<User, UserIn, UserOut> {

    static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private ConsentRepository consentRepository;

    @Autowired
    private EmailTemplateService emailTemplateService;

    public UserService(UserRepository repository) {
        super(User.class, UserPermissionValue::new, repository);
    }

    @Override
    protected User convertFromInDTO(UserIn dto, JsonNode rawIn, User item) {
        checkChangeOfEmail(dto);
        checkChangeOfIsActive(dto);

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
            user.setLastLoginTime(LocalDateTime.now(ZoneOffset.UTC));
        });
    }

    @Override
    public User update(Long id, Class<UserIn> inClass, HttpServletRequest request, boolean checkPermissions) {
        boolean isActive = repo().isActive(id);
        User updatedUser = super.update(id, inClass, request, checkPermissions);
        if (!isActive && updatedUser.getIsActive()) {
            emailTemplateService.sendActivatedUserEmail(updatedUser);
        }
        return updatedUser;
    }

    private void checkChangeOfIsActive(UserIn userIn) {
        if (userIn.getIsActive() != null) {
            checkPermission(new UserPermissionValue().activate());
        }
    }

    private void checkChangeOfEmail(UserIn userIn) {
        if (userIn.getEmail() != null && repo().findByEmail(userIn.getEmail()) != null) {
            throw new SingleValidationException(new ValidationError("email", ApiString.EMAIL_NOT_UNIQUE));
        }
    }

    public User changePassword(Long id, String password) {
        User user = read(id, false);
        String hashedPassword = new BCryptPasswordEncoder().encode(password);
        user.setPassword(hashedPassword);
        securityService.validate(user, null);
        return user;
    }

    public User createSignupUser(SignupUserIn signupUserIn) {
        UserIn userIn = new UserIn(signupUserIn);
        User user = create(userIn, false);

        // Save signup consent
        try {
            Consent signupConsent = new Consent();
            signupConsent.setType(Consent.Type.SIGNUP);
            signupConsent.setSource(Consent.Source.WEBPAGE);
            signupConsent.setTime(ClientServerTime.serverTimeNow());
            signupConsent.setUserId(user.getId());
            signupConsent.setConsentText(signupUserIn.getConsentText());
            consentRepository.save(signupConsent);
        } catch (Exception exception) {
            logger.error(MessageFormat.format("Failed to save signup consent due to: {0}", exception.getMessage()),
                    exception);
        }

        // Send welcome email and activate email
        emailTemplateService.sendWelcomeEmail(user);
        emailTemplateService.toAdminSendActivateUserEmail(signupUserIn);
        return user;
    }

    public boolean isOkToSignupUser() {
        return repo().countRecentSignups(ClientServerTime.serverTimeNow().minusHours(1)) <= 60;
    }
}
