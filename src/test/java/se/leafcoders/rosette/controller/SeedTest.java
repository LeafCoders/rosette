package se.leafcoders.rosette.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import se.leafcoders.rosette.IdResult;
import se.leafcoders.rosette.IdResultHandler;
import se.leafcoders.rosette.TimeRange;
import se.leafcoders.rosette.data.AssetFolderData;
import se.leafcoders.rosette.data.EventData;
import se.leafcoders.rosette.data.EventTypeData;
import se.leafcoders.rosette.data.GroupData;
import se.leafcoders.rosette.data.ResourceData;
import se.leafcoders.rosette.data.ResourceTypeData;
import se.leafcoders.rosette.data.SlideData;
import se.leafcoders.rosette.data.SlideShowData;
import se.leafcoders.rosette.data.UserData;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.model.AssetFolder;
import se.leafcoders.rosette.persistence.model.User;

public class SeedTest extends AbstractControllerTest {

    private static final int MONDAY = 1;
    private static final int SUNDAY = 7;
    private static final int DURATION_60 = 60;
    private static final int DURATION_90 = 90;
    
    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @SuppressWarnings("unused")
    @Test
    public void seed() throws Exception {
        user1 = givenUser(user1);
        givenPermissionForUser(user1, "*");
        givenPermissionForAll("*:view");

        // Users
        final Long admin = post(user1, "/users", json(UserData.newActiveUser("Admin", "Admin")));
        final Long patrikPastor = post(user1, "/users", json(UserData.newUser("Patrik", "Pastor")));
        final Long pamelaPastor = post(user1, "/users", json(UserData.newUser("Pamela", "Pastor")));
        final Long pavelPastor = post(user1, "/users", json(UserData.newUser("Pavel", "Pastor")));
        final Long mariaMusiker = post(user1, "/users", json(UserData.newUser("Maria", "Musiker")));
        final Long markusMusiker = post(user1, "/users", json(UserData.newUser("Markus", "Musiker")));
        final Long millaMusiker = post(user1, "/users", json(UserData.newUser("Milla", "Musiker")));
        final Long lasseLjudtekniker = post(user1, "/users", json(UserData.newUser("Lasse", "Ljudtekniker")));
        final Long lisaLjudtekniker = post(user1, "/users", json(UserData.newUser("Lisa", "Ljudtekniker")));
        final Long ludvigLjudtekniker = post(user1, "/users", json(UserData.newUser("Ludvig", "Ljudtekniker")));

        // Groups
        final Long pastorGrupp = post(user1, "/groups", json(GroupData.newGroup("pastorer", "Pastorer")));
        final Long musikGrupp = post(user1, "/groups", json(GroupData.newGroup("musiker", "Musiker")));

        // Users in group
        connect(user1, "/groups/" + pastorGrupp + "/users/" + patrikPastor);
        connect(user1, "/groups/" + pastorGrupp + "/users/" + pamelaPastor);
        connect(user1, "/groups/" + pastorGrupp + "/users/" + pavelPastor);
        connect(user1, "/groups/" + musikGrupp + "/users/" + mariaMusiker);
        
        
        // User permissions
        givenPermissionForUser(userRepository.findOne(admin), "*");

        // Slide shows
        final AssetFolder slideShowFolder = givenAssetFolder(AssetFolderData.newAssetFolder("slideShow", "Slide show images", "image/"));
        final Long slideShow1 = post(user1, "/slideShows", json(SlideShowData.newSlideShow("tv1", "TV 1", slideShowFolder)));
        final Long slideShow2 = post(user1, "/slideShows", json(SlideShowData.newSlideShow("tv2", "TV 2", slideShowFolder)));

        // Slide show images
        Asset slideShowImage1 = givenAssetInFolder(slideShowFolder.getId(), "image.png", "yellow.png", "image/png");
        Asset slideShowImage2 = givenAssetInFolder(slideShowFolder.getId(), "image.png", "blue.png", "image/png");
        Asset slideShowImage3 = givenAssetInFolder(slideShowFolder.getId(), "image.jpg", "image.jpg", "image/jpeg");
        
        
        // Slides
        post(user1, "/slideShows/" + slideShow1 + "/slides", json(SlideData.newSlide(slideShowImage1, TimeRange.start(MONDAY, 12, 30).weekOffset(-1).endAfterDays(6))));
        post(user1, "/slideShows/" + slideShow1 + "/slides", json(SlideData.newSlide(slideShowImage2, TimeRange.start(MONDAY, 1, 0).weekOffset(0).endAfterDays(10))));
        post(user1, "/slideShows/" + slideShow1 + "/slides", json(SlideData.newSlide(slideShowImage3, TimeRange.start(SUNDAY, 22, 10).weekOffset(1).endAfterDays(5))));

        
        
        // Resource types
        final Long predikantRT = post(user1, "/resourceTypes", json(ResourceTypeData.newResourceType("predikant", "Predikant")));
        final Long musikRT = post(user1, "/resourceTypes", json(ResourceTypeData.newResourceType("musik", "Musik")));
        final Long ljudRT = post(user1, "/resourceTypes", json(ResourceTypeData.newResourceType("ljud", "Ljud")));

        // Resources
        final Long patrikPastorResurs = post(user1, "/resources", json(ResourceData.newResource("Patrik Pastor", patrikPastor)));
        connect(user1, "/resources/" + patrikPastorResurs + "/resourceTypes/" + predikantRT);
        final Long pamelaPastorResurs = post(user1, "/resources", json(ResourceData.newResource("Pamela Pastor", pamelaPastor)));
        connect(user1, "/resources/" + pamelaPastorResurs + "/resourceTypes/" + predikantRT);
        final Long pavelPastorResurs = post(user1, "/resources", json(ResourceData.newResource("Pavel Pastor", pavelPastor)));
        connect(user1, "/resources/" + pavelPastorResurs + "/resourceTypes/" + predikantRT);

        final Long mariaMusikerResurs = post(user1, "/resources", json(ResourceData.newResource("Maria Musiker", mariaMusiker)));
        connect(user1, "/resources/" + mariaMusikerResurs + "/resourceTypes/" + musikRT);
        final Long markusMusikerResurs = post(user1, "/resources", json(ResourceData.newResource("Markus Musiker", markusMusiker)));
        connect(user1, "/resources/" + markusMusikerResurs + "/resourceTypes/" + musikRT);
        final Long millaMusikerResurs = post(user1, "/resources", json(ResourceData.newResource("Milla Musiker", millaMusiker)));
        connect(user1, "/resources/" + millaMusikerResurs + "/resourceTypes/" + musikRT);

        final Long lasseLjudteknikerResurs = post(user1, "/resources", json(ResourceData.newResource("Lasse Ljudtekniker", lasseLjudtekniker)));
        connect(user1, "/resources/" + lasseLjudteknikerResurs + "/resourceTypes/" + ljudRT);
        final Long lisaLjudteknikerResurs = post(user1, "/resources", json(ResourceData.newResource("Lisa Ljudtekniker", lisaLjudtekniker)));
        connect(user1, "/resources/" + lisaLjudteknikerResurs + "/resourceTypes/" + ljudRT);
        final Long ludvigLjudteknikerResurs = post(user1, "/resources", json(ResourceData.newResource("Ludvig Ljudtekniker", ludvigLjudtekniker)));
        connect(user1, "/resources/" + ludvigLjudteknikerResurs + "/resourceTypes/" + ljudRT);

        
        // Event types
        final Long gudstjanstET = post(user1, "/eventTypes", json(EventTypeData.newEventType("gudstjanst", "Gudstjänst")));
        connect(user1, "/eventTypes/" + gudstjanstET + "/resourceTypes/" + predikantRT);
        connect(user1, "/eventTypes/" + gudstjanstET + "/resourceTypes/" + musikRT);
        connect(user1, "/eventTypes/" + gudstjanstET + "/resourceTypes/" + ljudRT);


        // Events
        final Long event1 = post(user1, "/events", json(EventData.newEvent(gudstjanstET, "Gudstjänst", TimeRange.start(SUNDAY, 10, 0).weekOffset(0).endAfterMinutes(DURATION_60))));
        final Long event2 = post(user1, "/events", json(EventData.newEvent(gudstjanstET, "Gudstjänst", TimeRange.start(SUNDAY, 11, 0).weekOffset(1).endAfterMinutes(DURATION_90))));
        final Long event3 = post(user1, "/events", json(EventData.newEvent(gudstjanstET, "Gudstjänst", TimeRange.start(SUNDAY, 11, 0).weekOffset(2).endAfterMinutes(DURATION_90))));
        
        // Event resource requirements
// Dessa tre skapas redan när eventet skapas. Koppla resurserna behöver vi ändå göra, men kanske inte nu...
/*        
        final Long rr1 = postReturnArray(0, user1, "/events/" + event1 + "/resourceRequirements", json(ResourceRequirementData.newResourceRequirement(predikantRT)));
        final Long rr2 = postReturnArray(1, user1, "/events/" + event1 + "/resourceRequirements", json(ResourceRequirementData.newResourceRequirement(musikRT)));
        final Long rr3 = postReturnArray(2, user1, "/events/" + event1 + "/resourceRequirements", json(ResourceRequirementData.newResourceRequirement(ljudRT)));
        connect(user1, "/events/" + event1 + "/resourceRequirements/" + rr1 + "/resources?resourceId=" + patrikPastorResurs);
        connect(user1, "/events/" + event1 + "/resourceRequirements/" + rr1 + "/resources?resourceId=" + pamelaPastorResurs);
        connect(user1, "/events/" + event1 + "/resourceRequirements/" + rr2 + "/resources?resourceId=" + mariaMusikerResurs);
        connect(user1, "/events/" + event1 + "/resourceRequirements/" + rr3 + "/resources?resourceId=" + lasseLjudteknikerResurs);
        connect(user1, "/events/" + event1 + "/resourceRequirements/" + rr3 + "/resources?resourceId=" + lisaLjudteknikerResurs);
        connect(user1, "/events/" + event1 + "/resourceRequirements/" + rr3 + "/resources?resourceId=" + ludvigLjudteknikerResurs);

        postReturnArray(0, user1, "/events/" + event2 + "/resourceRequirements", json(ResourceRequirementData.newResourceRequirement(predikantRT)));
        postReturnArray(0, user1, "/events/" + event3 + "/resourceRequirements", json(ResourceRequirementData.newResourceRequirement(predikantRT)));
*/
    }

    public Long post(User authUser, String controllerUrl, String jsonString) throws Exception {
        IdResult idResult = new IdResult();
        withUser(authUser, MockMvcRequestBuilders.post("/api" + controllerUrl).content(jsonString).contentType(AbstractControllerTest.CONTENT_JSON))
            .andExpect(status().isCreated())
            .andDo(IdResultHandler.assignTo("$.id", idResult));
        return idResult.id;
    }

    public Long postReturnArray(int index, User authUser, String controllerUrl, String jsonString) throws Exception {
        IdResult idResult = new IdResult();
        withUser(authUser, MockMvcRequestBuilders.post("/api" + controllerUrl).content(jsonString).contentType(AbstractControllerTest.CONTENT_JSON))
            .andExpect(status().isOk())
            .andDo(IdResultHandler.assignTo("$[" + index + "].id", idResult));
        return idResult.id;
    }
    
    public void connect(User authUser, String controllerUrl) throws Exception {
        postReturnArray(0, authUser, controllerUrl, "");
    }
    
}
