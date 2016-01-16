package se.leafcoders.rosette.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.model.BaseModel;
import se.leafcoders.rosette.validator.CheckReferenceArray;
import se.leafcoders.rosette.validator.CheckReference;

/**
 * ReferenceClass Class of the document that we want to check if it's referenced
 * ReferenceId Id of the document
 *
 * DocumentClass Class of the document that we query in to find the reference
 * ModelClass Class where we find annotations in
 */

public class ReferenceUsageFinder {

    private final MongoTemplate mongoTemplate;
    private final Class<? extends BaseModel> referenceClass;
    private final String referenceId;

    private enum Extend { NOT, SUB_CLASSES, SUPER_CLASSES };
    
    public ReferenceUsageFinder(MongoTemplate mongoTemplate, Class<? extends BaseModel> referenceClass, String referenceId) {
        this.mongoTemplate = mongoTemplate;
        this.referenceClass = referenceClass;
        this.referenceId = referenceId;
    }

    /**
     * Find all classes with \@Document annotation
     * Check all fields annotated with \@ReferenceXXX 
     */
    public void checkIsReferenced() {
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(true);
        scanner.addIncludeFilter(new AnnotationTypeFilter(Document.class));
//        scanner.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile("(.*?)")));
        for (BeanDefinition bd : scanner.findCandidateComponents("se.leafcoders.rosette.model")) {
            try {
                Class <?> modelClass = Class.forName(bd.getBeanClassName());
                handleAnnotations(modelClass, modelClass, "", Extend.SUPER_CLASSES);
            } catch (ClassNotFoundException e) {
                throw new ForbiddenException("error.delete", e.getMessage());
            }
        }
    }

    private void handleAnnotations(Class<?> documentClass, Class <?> modelClass, String dbKeyPath, Extend direction) {
        try {
            // Check fields in this class
            Field[] fieldList = modelClass.getDeclaredFields();
            for (Field field : fieldList) {
                if (field.isAnnotationPresent(CheckReference.class)) {
                    handleRefereceOne(documentClass, field.getAnnotation(CheckReference.class), field, dbKeyPath);
                }
                if (field.isAnnotationPresent(CheckReferenceArray.class)) {
                    handleRefereceArray(documentClass, field.getAnnotation(CheckReferenceArray.class), field, dbKeyPath);
                }
            }
            
            if (direction == Extend.SUB_CLASSES) {
                Annotation[] annotations = modelClass.getAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation.annotationType() == JsonSubTypes.class) {
                        JsonSubTypes.Type[] jsonSubTypes = ((JsonSubTypes) annotation).value();
                        for (JsonSubTypes.Type jsonSubType : jsonSubTypes) {
                            handleAnnotations(documentClass, jsonSubType.value(), dbKeyPath, Extend.NOT);
                        }
                    }
                }
            }
            
            if (direction == Extend.SUPER_CLASSES) {
                if (modelClass.getSuperclass() != null) {
                    handleFieldSuperClass(documentClass, modelClass.getSuperclass(), dbKeyPath);
                }
            }
            
        } catch (SecurityException e) {
            throw new ForbiddenException("error.delete", e.getMessage());
        }
    }
    
    private void handleRefereceOne(Class<?> documentClass, CheckReference annotation, Field field, String dbKeyPath) {
        Class<?> fieldClass = annotation.model();
        if (fieldClass.equals(BaseModel.class)) {
            fieldClass = field.getType();
        }
        
        if (!fieldClass.equals(referenceClass)) {
            return;
        }

        String dbKey = annotation.dbKey();
        if (dbKey.isEmpty()) {
            dbKey = field.getName() + ".id";
        }

        if (fieldClass.getSuperclass() != null) {
            handleFieldSuperClass(documentClass, fieldClass.getSuperclass(), buildKey(dbKeyPath, dbKey));
        }
        
//        System.out.println("Query key: " + buildKey(dbKeyPath, dbKey) + ", Id: " + referenceId + ", ModelClass: " + documentClass.getSimpleName());

        String key = buildKey(dbKeyPath, dbKey).replaceAll(".id", "._id");
        if (mongoTemplate.exists(Query.query(Criteria.where(key).is(QueryId.get(referenceId))), documentClass)) {
            throw new ForbiddenException("error.referencedBy", documentClass.getSimpleName());
        }
    }

    private void handleRefereceArray(Class<?> documentClass, CheckReferenceArray annotation, Field field, String dbKeyPath) {
        handleAnnotations(documentClass, annotation.model(), buildKey(dbKeyPath, field.getName()), Extend.SUB_CLASSES);
    }

    private void handleFieldSuperClass(Class<?> documentClass, Class<?> superClass, String dbKeyPath) {
        handleAnnotations(documentClass, superClass, dbKeyPath, Extend.SUPER_CLASSES);
    }
    
    private String buildKey(String path, String dbKey) {
        return path.isEmpty() ? dbKey : path + "." + dbKey;
    }
}
