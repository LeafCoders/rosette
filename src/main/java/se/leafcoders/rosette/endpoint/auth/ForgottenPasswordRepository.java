package se.leafcoders.rosette.endpoint.auth;

import org.springframework.stereotype.Repository;

import se.leafcoders.rosette.core.persistable.ModelRepository;

@Repository
public interface ForgottenPasswordRepository extends ModelRepository<ForgottenPassword> {
    
    ForgottenPassword findByToken(String token);
    
}
