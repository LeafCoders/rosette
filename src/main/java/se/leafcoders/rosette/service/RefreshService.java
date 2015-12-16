package se.leafcoders.rosette.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import se.leafcoders.rosette.model.Refresh;

@Service
public class RefreshService {

    @Autowired protected MongoTemplate mongoTemplate;
    @Autowired protected BookingService bookingService;
    @Autowired protected EducationService educationService;
    @Autowired protected EducationThemeService educationThemeService;
    @Autowired protected EducationTypeService educationTypeService;
    @Autowired protected EventService eventService;
    @Autowired protected EventTypeService eventTypeService;
    @Autowired protected GroupMembershipService groupMembershipService;
    @Autowired protected GroupService groupService;
    @Autowired protected LocationService locationService;
    @Autowired protected PermissionService permissionService;
    @Autowired protected PodcastService podcastService;
    @Autowired protected ResourceTypeService resourceTypeService;
    @Autowired protected UploadFolderService uploadFolderService;
    @Autowired protected UserService userService;

    public RefreshService() {
    }

    public void setNeedRefresh(String collectionClassName) {
        Refresh data = new Refresh();
        data.setCollectionClassName(collectionClassName);
        mongoTemplate.insert(data);
    }

    @Scheduled(initialDelay = 1*60*1000, fixedRate = 1*60*60*1000)
    public void refresh() {
        List<Refresh> items = mongoTemplate.find(new Query(), Refresh.class);
        Set<String> changedCollections = new HashSet<String>();
        items.forEach((Refresh item) -> {
            changedCollections.add(item.getCollectionClassName());
        });

        if (changedCollections.size() > 0) {
            uploadFolderService.refresh(changedCollections);
            locationService.refresh(changedCollections);
            bookingService.refresh(changedCollections);

            groupService.refresh(changedCollections);
            userService.refresh(changedCollections);
            groupMembershipService.refresh(changedCollections);
            permissionService.refresh(changedCollections);

            resourceTypeService.refresh(changedCollections);

            educationThemeService.refresh(changedCollections);
            educationTypeService.refresh(changedCollections);
            eventTypeService.refresh(changedCollections);

            eventService.refresh(changedCollections);
            educationService.refresh(changedCollections);

            podcastService.refresh(changedCollections);

            mongoTemplate.dropCollection(Refresh.class);
        }
    }
}
