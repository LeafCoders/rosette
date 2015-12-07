package se.leafcoders.rosette.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.exception.NotFoundException;
import se.leafcoders.rosette.model.education.EducationTheme;

@Service
public class PublicEducationThemeService {
	@Autowired
	protected MongoTemplate mongoTemplate;

    public EducationTheme read(String id) {
        EducationTheme educationTheme = mongoTemplate.findById(id, EducationTheme.class);
        if (educationTheme == null) {
            throw new NotFoundException();
        }
        return educationTheme;
    }
}
