package se.ryttargardskyrkan.rosette.security;

import org.apache.shiro.authc.credential.DefaultPasswordService;
import org.apache.shiro.crypto.hash.DefaultHashService;

public class RosettePasswordService extends DefaultPasswordService {

	public RosettePasswordService() {
		super();
		
		DefaultHashService hashService = new DefaultHashService();
        hashService.setHashAlgorithmName("SHA-256");
        hashService.setHashIterations(1);
        hashService.setGeneratePublicSalt(true);
		setHashService(hashService);
	}

}
