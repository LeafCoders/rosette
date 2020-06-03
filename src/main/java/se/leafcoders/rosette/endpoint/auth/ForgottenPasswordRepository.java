package se.leafcoders.rosette.persistence.repository;

import org.springframework.stereotype.Repository;
import se.leafcoders.rosette.persistence.model.ForgottenPassword;

@Repository
public interface ForgottenPasswordRepository extends ModelRepository<ForgottenPassword> {
    
    ForgottenPassword findByToken(String token);
    
}
