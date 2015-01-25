package se.leafcoders.rosette.application;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Profile("!production")
@Configuration
@EnableMongoAuditing
public class PersistenceConfigDefault extends AbstractMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "rosette-test";
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient("127.0.0.1", 27017);
    }

    @Bean
    public GridFsTemplate gridFsTemplate() throws Exception {
        return new GridFsTemplate(mongoDbFactory(), mappingMongoConverter());
    }
}
