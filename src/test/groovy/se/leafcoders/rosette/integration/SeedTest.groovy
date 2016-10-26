package se.leafcoders.rosette.integration

import org.apache.http.client.ClientProtocolException
import org.joda.time.DateTime
import org.junit.Test
import se.leafcoders.rosette.model.Booking
import se.leafcoders.rosette.model.DefaultSetting
import se.leafcoders.rosette.model.EventType
import se.leafcoders.rosette.model.Group
import se.leafcoders.rosette.model.Location
import se.leafcoders.rosette.model.Poster
import se.leafcoders.rosette.model.User
import se.leafcoders.rosette.model.education.EducationTheme
import se.leafcoders.rosette.model.education.EducationThemeRef
import se.leafcoders.rosette.model.education.EducationType
import se.leafcoders.rosette.model.education.EducationTypeRef
import se.leafcoders.rosette.model.education.EventEducation
import se.leafcoders.rosette.model.education.SimpleEducation
import se.leafcoders.rosette.model.event.Event
import se.leafcoders.rosette.model.podcast.Podcast
import se.leafcoders.rosette.model.reference.EventRef
import se.leafcoders.rosette.model.reference.LocationRefOrText
import se.leafcoders.rosette.model.reference.ObjectReferences
import se.leafcoders.rosette.model.reference.UserRef
import se.leafcoders.rosette.model.reference.UserRefsAndText
import se.leafcoders.rosette.model.resource.Resource
import se.leafcoders.rosette.model.resource.ResourceType
import se.leafcoders.rosette.model.resource.UploadResource
import se.leafcoders.rosette.model.resource.UserResource
import se.leafcoders.rosette.model.resource.UserResourceType
import se.leafcoders.rosette.model.upload.UploadFolder;
import se.leafcoders.rosette.model.upload.UploadFile
import se.leafcoders.rosette.integration.util.TestUtil

class SeedTest extends AbstractIntegrationTest {

    enum Times {
        FORRA_VECKAN_08(now().minusDays(7).withHourOfDay(8)),
        FORRA_VECKAN_09(now().minusDays(7).withHourOfDay(9)),
        IGAR_10(now().minusDays(1).withHourOfDay(10)),
        IGAR_11(now().minusDays(1).withHourOfDay(11)),
        IDAG_22(now().withHourOfDay(22)),
        IDAG_23(now().withHourOfDay(23)),
        IMORGON_18(now().plusDays(1).withHourOfDay(18)),
        IMORGON_20(now().plusDays(1).withHourOfDay(20)),
        NASTA_VECKA_13(now().plusDays(7).withHourOfDay(13)),
        NASTA_VECKA_17(now().plusDays(7).withHourOfDay(17)),

        TISDAG_18(now().withDayOfWeek(2).withHourOfDay(18)),
        TORSDAG_10(now().withDayOfWeek(4).withHourOfDay(10)),
        LORDAG_19(now().withDayOfWeek(6).withHourOfDay(19)),
        SONDAG_11(now().withDayOfWeek(7).withHourOfDay(11));

        private DateTime time

        Times(DateTime time) {
            this.time = time
        }

        public Date time() {
            return outTime(time)
        }

        public Map range(int endMinutes) {
            return [start: outTime(time), end: outTime(time.plusMinutes(endMinutes))]
        }

        public Map rangeOffsetDays(int dayOffset, int endMinutes) {
            DateTime newTime = time.plusDays(dayOffset)
            return [start: outTime(newTime), end: outTime(newTime.plusMinutes(endMinutes))]
        }

        public Map rangeOffsetWeeks(int weekOffset, int endMinutes) {
            DateTime newTime = time.plusWeeks(weekOffset)
            return [start: outTime(newTime), end: outTime(newTime.plusMinutes(endMinutes))]
        }

        private Date outTime(DateTime outTime) {
            return TestUtil.modelDate("""${ outTime.toLocalDateTime().toString("yyyy-MM-dd HH:mm") } Europe/Stockholm""")
        }

        static private DateTime now() {
            return new DateTime().withTimeAtStartOfDay()
        }
    }

    @Test
    public void test() throws ClientProtocolException, IOException {

        /**
         * Användare och grupper
         */
        User admin = newUser("Admin", "Admin")
        givenPermissionForUser(admin, ["*"])
        createTestUploadUser()

        Group predikantGrupp = newGroup("predikanter", "Predikanter")
        User predikantPatrik = newUser("Predikant", "Patrik")
        User predikantPaula = newUser("Predikant", "Paula")
        User predikantPhilip = newUser("Predikant", "Philip")
        givenGroupMembership(predikantPatrik, predikantGrupp)
        givenGroupMembership(predikantPaula, predikantGrupp)
        givenGroupMembership(predikantPhilip, predikantGrupp)

        Group musikteamGrupp = newGroup("musikteam", "Musikteam")
        User musikteamMaria = newUser("Musikteam", "Maria")
        User musikteamMartin = newUser("Musikteam", "Martin")
        User musikteamMolly = newUser("Musikteam", "Molly")
        givenGroupMembership(musikteamMaria, musikteamGrupp)
        givenGroupMembership(musikteamMartin, musikteamGrupp)
        givenGroupMembership(musikteamMolly, musikteamGrupp)

        Group ljudteknikerGrupp = newGroup("ljudtekniker", "Ljudtekniker")
        User ljudteknikerLars = newUser("Ljudtekniker", "Lars")
        User ljudteknikerLisa = newUser("Ljudtekniker", "Lisa")
        User ljudteknikerLoffe = newUser("Ljudtekniker", "Loffe")
        givenGroupMembership(ljudteknikerLars, ljudteknikerGrupp)
        givenGroupMembership(ljudteknikerLisa, ljudteknikerGrupp)
        givenGroupMembership(ljudteknikerLoffe, ljudteknikerGrupp)

        
        /**
         * Lokaler
         */
        Location kyrksalLokal = newLocation("kyrksalen", "Kyrksalen")
        Location bokenLokal = newLocation("boken", "Boken")
        Location oasenLokal = newLocation("oasen", "Oasen")


        /**
         * Bokningar
         */
        Booking personaltraffBokning = newBooking("Musikskolan", bokenLokal, Times.IGAR_10.range(60))
        Booking inforadBokning = newBooking("Inforådet", bokenLokal, Times.IDAG_22.range(60))
        Booking musikskolanBokning = newBooking("Musikskolan", bokenLokal, Times.IMORGON_18.range(90))


        /**
         * Affischer
         */
        UploadFolder affischFilkatalog = newUploadFolder("posters", "Affischer", true, ["image/"])
        UploadFile konsertBild = givenUploadInFolder(affischFilkatalog.id, validPNGImage)
        UploadFile bibelstudieBild = givenUploadInFolder(affischFilkatalog.id, validJPEGImage)

        Poster konsertAffisch = newPoster("Konsert", konsertBild, Times.FORRA_VECKAN_08, Times.IGAR_10)
        Poster bibelstudieAffisch = newPoster("Bibelstudium", bibelstudieBild, Times.IGAR_10, Times.NASTA_VECKA_17)
        Poster bonAffisch = newPoster("Bön", konsertBild, Times.IMORGON_18, Times.NASTA_VECKA_17)


        /**
         * Resurstyper
         */
        UserResourceType predikanResurstyp = newUserResourceType("predikan", "Predikan", predikantGrupp, false, true)
        UserResourceType musikResurstyp = newUserResourceType("musik", "Musik", musikteamGrupp, true, true)
        UserResourceType ljudResurstyp = newUserResourceType("ljud", "Ljud", ljudteknikerGrupp, true, false)


        /**
         * Händelsetyper
         */
        EventType gudstjanstHandelsetyp = newEventType("gudstjanst", "Gudstjänst", [predikanResurstyp, musikResurstyp, ljudResurstyp], true, true)
        EventType bonHandelsetyp = newEventType("bon", "Bön", [], true, false)
        EventType bibelstudieHandelsetyp = newEventType("bibelstudie", "Bibelstudie", [predikanResurstyp], true, false)
        EventType konsertHandelsetyp = newEventType("konsert", "Konsert", [musikResurstyp, ljudResurstyp], true, false)
        EventType cellgrupp39Handelsetyp = newEventType("cellgrupp39", "Cellgrupp 39", [], false, false)


        /**
         * Händelser
         */
        String gudstjanstBeskrivning = "Tema:\n{Predikan: #predikan}\n{Sång & musik: #musik}"
        String bonBeskrivning = ""

        Event gudstjanstM6Handelse = newEvent(gudstjanstHandelsetyp, Times.SONDAG_11.rangeOffsetWeeks(-6, 120), "Gudstjänst", gudstjanstBeskrivning, kyrksalLokal, [
            new UserResource(type: "user", resourceType: predikanResurstyp, users: new UserRefsAndText(refs: [predikantPatrik] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: musikResurstyp, users: new UserRefsAndText(refs: [musikteamMaria] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: ljudResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>))
        ])
        Event gudstjanstM5Handelse = newEvent(gudstjanstHandelsetyp, Times.SONDAG_11.rangeOffsetWeeks(-5, 120), "Gudstjänst", gudstjanstBeskrivning, kyrksalLokal, [
            new UserResource(type: "user", resourceType: predikanResurstyp, users: new UserRefsAndText(refs: [predikantPatrik] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: musikResurstyp, users: new UserRefsAndText(refs: [musikteamMaria] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: ljudResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>))
        ])
        Event gudstjanstM4Handelse = newEvent(gudstjanstHandelsetyp, Times.SONDAG_11.rangeOffsetWeeks(-4, 120), "Gudstjänst", gudstjanstBeskrivning, kyrksalLokal, [
            new UserResource(type: "user", resourceType: predikanResurstyp, users: new UserRefsAndText(refs: [predikantPatrik] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: musikResurstyp, users: new UserRefsAndText(refs: [musikteamMaria] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: ljudResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>))
        ])
        Event gudstjanstM3Handelse = newEvent(gudstjanstHandelsetyp, Times.SONDAG_11.rangeOffsetWeeks(-3, 120), "Gudstjänst", gudstjanstBeskrivning, kyrksalLokal, [
            new UserResource(type: "user", resourceType: predikanResurstyp, users: new UserRefsAndText(refs: [predikantPaula] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: musikResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: ljudResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>))
        ])
        Event gudstjanstM2Handelse = newEvent(gudstjanstHandelsetyp, Times.SONDAG_11.rangeOffsetWeeks(-2, 120), "Gudstjänst", gudstjanstBeskrivning, kyrksalLokal, [
            new UserResource(type: "user", resourceType: predikanResurstyp, users: new UserRefsAndText(refs: [predikantPatrik] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: musikResurstyp, users: new UserRefsAndText(refs: [musikteamMartin] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: ljudResurstyp, users: new UserRefsAndText(refs: [ljudteknikerLoffe, ljudteknikerLisa] as ObjectReferences<UserRef>))
        ])
        Event gudstjanstM1Handelse = newEvent(gudstjanstHandelsetyp, Times.SONDAG_11.rangeOffsetWeeks(-1, 120), "Gudstjänst", gudstjanstBeskrivning, kyrksalLokal, [
            new UserResource(type: "user", resourceType: predikanResurstyp, users: new UserRefsAndText(refs: [predikantPhilip] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: musikResurstyp, users: new UserRefsAndText(refs: [musikteamMaria] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: ljudResurstyp, users: new UserRefsAndText(refs: [ljudteknikerLisa] as ObjectReferences<UserRef>, text: 'Pelle Praktikant'))
        ])
        Event gudstjanstP1Handelse = newEvent(gudstjanstHandelsetyp, Times.SONDAG_11.range(120), "Gudstjänst", gudstjanstBeskrivning, kyrksalLokal, [
            new UserResource(type: "user", resourceType: predikanResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>, text: 'Evert Evangelist')),
            new UserResource(type: "user", resourceType: musikResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: ljudResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>))
        ])
        Event gudstjanstP2Handelse = newEvent(gudstjanstHandelsetyp, Times.SONDAG_11.rangeOffsetWeeks(1, 120), "Gudstjänst", gudstjanstBeskrivning, kyrksalLokal, [
            new UserResource(type: "user", resourceType: predikanResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>, text: 'Evert Evangelist')),
            new UserResource(type: "user", resourceType: musikResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: ljudResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>))
        ])
        Event gudstjanstP3Handelse = newEvent(gudstjanstHandelsetyp, Times.SONDAG_11.rangeOffsetWeeks(2, 120), "Gudstjänst", gudstjanstBeskrivning, kyrksalLokal, [
            new UserResource(type: "user", resourceType: predikanResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>, text: 'Evert Evangelist')),
            new UserResource(type: "user", resourceType: musikResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>)),
            new UserResource(type: "user", resourceType: ljudResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>))
        ])


        (-4..5).each { int offset ->
            newEvent(bonHandelsetyp, Times.TISDAG_18.rangeOffsetWeeks(offset, 45), "Bön 45 minuter", bonBeskrivning, oasenLokal, [])
            newEvent(bonHandelsetyp, Times.TORSDAG_10.rangeOffsetWeeks(offset, 60), "Bön 60 minuter", bonBeskrivning, oasenLokal, [])
        }

        newEvent(konsertHandelsetyp, Times.LORDAG_19.rangeOffsetWeeks(2, 180), "Konsert med Bandet", "", kyrksalLokal, [
            new UserResource(type: "user", resourceType: ljudResurstyp, users: new UserRefsAndText(refs: [] as ObjectReferences<UserRef>))
        ])



        /**
         * Undervisningstyp
         */
        UploadFolder predikningarFilkatalog = newUploadFolder("predikningar", "Predikningar", true, ["audio/"])
        EducationType gudstjanstUtbildningstyp = newEducationType("gudstjanst", "Gudstjänst", gudstjanstHandelsetyp, predikanResurstyp, predikningarFilkatalog)

        UploadFolder bibelstudieFilkatalog = newUploadFolder("bibelstudium", "Bibelstudium", true, ["audio/"])
        EducationType bibelstudieUtbildningstyp = newEducationType("bibelstudie", "Bibelstudie", bibelstudieHandelsetyp, predikanResurstyp, bibelstudieFilkatalog)


        /**
         * Undervisningstema
         */
        UploadFolder temaFilkatalog = newUploadFolder("educationThemes", "Temabilder", true, ["image/"])
        UploadFile standardTemaBild = givenUploadInFolder(temaFilkatalog.id, validPNGImage)
        UploadFile jakobsbrevetBild = givenUploadInFolder(temaFilkatalog.id, validJPEGImage)

        String temaContent = "Detta tema handlar om det här..."

        EducationTheme ovrigtTema = newEducationTheme("Övrigt", temaContent, gudstjanstUtbildningstyp, standardTemaBild)
        EducationTheme weAreFamilyTema = newEducationTheme("We are family 2.0", temaContent, gudstjanstUtbildningstyp, standardTemaBild)
        EducationTheme jakobsbrevetTema = newEducationTheme("Jakobsbrevet", temaContent, gudstjanstUtbildningstyp, jakobsbrevetBild)
        EducationTheme profeterTema = newEducationTheme("Profeter", temaContent, gudstjanstUtbildningstyp, standardTemaBild)


        /**
         * Undervisning
         */

        UploadFile inspelning1 = givenUploadInFolder(predikningarFilkatalog.id, audioRecording1)

        (-50..-7).each { int offset ->
            newSimpleEducation(
                "Undervisning ${ offset + 51 }", "Innehåll", "Frågor",
                gudstjanstUtbildningstyp, ovrigtTema, Times.SONDAG_11.rangeOffsetWeeks(offset, 0).start,
                "Författare Fia", inspelning1
            )
        }


        String weAreFamilyContent = """Här är en liten kort beskrivning av predikan.
Den är uppdelad i tre delar:
- Del 1
- Del 2
- Del 3

Lite mer text här. Och sen lite till."""

        String weAreFamilyQuestions = """1) Första frågan
2) Andra frågan. En massa text här för att det ska bli radbryning på mobiltelefon. Lite mer text för att vara säker.
3) Tredje frågan."""

        EventEducation weAreFamily_1 = newEventEducation(
            "Familjen hemma", weAreFamilyContent, weAreFamilyQuestions,
            gudstjanstUtbildningstyp, weAreFamilyTema, gudstjanstM6Handelse,
            "Predikant Patrik", inspelning1
        )
        EventEducation weAreFamily_2 = newEventEducation(
            "Familjen här", weAreFamilyContent, weAreFamilyQuestions,
            gudstjanstUtbildningstyp, weAreFamilyTema, gudstjanstM5Handelse,
            "Predikant Patrik", inspelning1
        )
        EventEducation weAreFamily_3 = newEventEducation(
            "Familjen borta", weAreFamilyContent, null,
            gudstjanstUtbildningstyp, weAreFamilyTema, gudstjanstM4Handelse,
            "Predikant Patrik", inspelning1
        )

        String jakobsbrevetContent = "Här är en liten kort beskrivning av predikan."
        String jakobsbrevetQuestions = "Frågor här..."

        EventEducation jakobsbrevet_1 = newEventEducation(
            "Jakobsbrevet kap 1", jakobsbrevetContent, jakobsbrevetQuestions,
            gudstjanstUtbildningstyp, jakobsbrevetTema, gudstjanstM3Handelse,
            "Predikant Patrik", inspelning1
        )
        EventEducation jakobsbrevet_2 = newEventEducation(
            "Jakobsbrevet kap 2", null, null,
            gudstjanstUtbildningstyp, jakobsbrevetTema, gudstjanstM2Handelse,
            "Predikant Patrik", inspelning1
        )
        EventEducation jakobsbrevet_3 = newEventEducation(
            "Jakobsbrevet kap 3", jakobsbrevetContent, null,
            gudstjanstUtbildningstyp, jakobsbrevetTema, gudstjanstM1Handelse,
            "Predikant Patrik", inspelning1
        )
        EventEducation jakobsbrevet_4 = newEventEducation(
            "Jakobsbrevet kap 4", null, jakobsbrevetQuestions,
            gudstjanstUtbildningstyp, jakobsbrevetTema, gudstjanstP1Handelse,
            "Predikant Patrik", inspelning1
        )
        EventEducation jakobsbrevet_5 = newEventEducation(
            "Jakobsbrevet kap 5", jakobsbrevetContent, jakobsbrevetQuestions,
            gudstjanstUtbildningstyp, jakobsbrevetTema, gudstjanstP2Handelse,
            "Predikant Patrik", null
        )

        
        /**
         * Podcast
         */
        UploadFolder podcastFilkatalog = newUploadFolder("podcastbilder", "Podcastbilder", true, ["image/"])
        newPodcast("Kyrkans predikningar", gudstjanstUtbildningstyp, givenUploadInFolder(podcastFilkatalog.id, validPNGImage))



        /**
         * Rättigheter
         */
        givenPermissionForGroup(predikantGrupp, [
            "events:view",
            "events:read,update:resourceTypes:predikan",
            "resourceTypes:read:predikan",
            "locations:read"
        ])

        givenPermissionForGroup(ljudteknikerGrupp, [
            "events:view",
            "events:read,update:resourceTypes:ljud",
            "resourceTypes:read:ljud",
            "locations:read"
        ])

        User hanteraGudstjanster = newUser("Hantera", "Gudstjanster")
        givenPermissionForUser(hanteraGudstjanster, [
            "events:view",
            "events:read,update:eventTypes:gudstjanst",
            "eventTypes:read:gudstjanst",
            "resourceTypes:read",
            "locations:read",
            "users:read"
        ])

        User hanteraHandelser = newUser("Hantera", "Handelser")
        givenPermissionForUser(hanteraHandelser, [
            "events",
            "eventTypes:read",
            "resourceTypes:read",
            "locations:read",
            "users:read"
        ])

        User hanteraAffischer = newUser("Hantera", "Affischer")
        givenPermissionForUser(hanteraAffischer, [
            "posters",
            "uploads:view",
            "uploads:*:posters"
        ])

        User hanteraBokningar = newUser("Hantera", "Bokningar")
        givenPermissionForUser(hanteraBokningar, [
            "bookings",
            "locations:read"
        ])

        givenPermissionForEveryone(["public:read"])
    }

    private User newUser(String firstName, String lastName) {
        User user = new User(
            email: "${ firstName.toLowerCase() }@${ lastName.toLowerCase() }.se",
            firstName: firstName,
            lastName: lastName,
            hashedPassword : "${ hashedPassword }"
        )
        givenUser(user)
        return user
    }

    private Group newGroup(String id, String name) {
        Group group = new Group(id: id, name: name, description: "Detta är en grupp som heter ${ name }.")
        givenGroup(group)
        return group
    }

    private Location newLocation(String id, String name) {
        Location location = new Location(id: id, name: name, description: "Detta är en lokal som heter ${ name }.")
        givenLocation(location)
        return location
    }

    private UploadFolder newUploadFolder(String id, String name, boolean isPublic, List<String> mimeTypes) {
        UploadFolder uploadFolder = new UploadFolder(id: id, name: name, isPublic: isPublic, mimeTypes: mimeTypes)
        givenUploadFolder(uploadFolder)
        return uploadFolder
    }

    private Booking newBooking(String customerName, Location location, Map time) {
        Booking booking = new Booking(
            id : getObjectId(),
            customerName : customerName,
            startTime : time.start,
            endTime : time.end,
            location : new LocationRefOrText(ref: location)
        )
        givenBooking(booking)
        return booking
    }

    private Poster newPoster(String title, UploadFile image, Times start, Times end) {
        Poster poster = new Poster(
            id : getObjectId(),
            title : title,
            startTime : start.time(),
            endTime : end.time(),
            duration : 15
        )
        givenPoster(poster, image)
        return poster
    }

    private UserResourceType newUserResourceType(String id, String name, Group group, boolean multiSelect, boolean allowText) {
        UserResourceType resourceType = new UserResourceType(
            type : "user",
            id : id,
            section : "persons",
            name : name,
            description : "Detta är en resurstyp med namn ${ name }.\n${ multiSelect ? 'Flera personer' : 'En person' } kan tilldelas.\nPerson kan ${ allowText ? '' : 'inte ' }anges med fritext.",
            multiSelect : multiSelect,
            allowText : allowText,
            group : group
        )
        givenResourceType(resourceType)
        return resourceType
    }

    private EventType newEventType(String id, String name, List<ResourceType> resourceTypes, boolean publicEvents, boolean allowChangePublic) {
        EventType eventType = new EventType(
            id : id,
            name : name,
            description : "Detta är en händelse med namn ${ name }.\nHändelserna är ${ publicEvents ? '' : 'inte ' }publika.\nPublik-statusen kan ${ allowChangePublic ? '' : 'inte ' }ändras.",
            hasPublicEvents : new DefaultSetting<Boolean>(value: publicEvents, allowChange: allowChangePublic),
            resourceTypes : resourceTypes
        )
        givenEventType(eventType)
        return eventType
    }

    private Event newEvent(EventType eventType, Map time, String title, String description, Location location, List<Resource> resources) {
        Event event = new Event(
            id: getObjectId(),
            eventType : eventType,
            title : title,
            description : description,
            startTime : time.start,
            endTime : time.end,
            location : new LocationRefOrText(ref: location),
            isPublic : eventType.hasPublicEvents.value,
            resources : resources
        )
        givenEvent(event)
        return event
    }

    private EducationType newEducationType(String id, String name, EventType eventType, ResourceType resourceType, UploadFolder uploadFolder) {
        EducationType educationType = new EducationType(
            id : id,
            name : name,
            description : "Detta är en undervisningstyp med namn ${ name }.",
            eventType : eventType,
            authorResourceType : resourceType,
            uploadFolder : uploadFolder
        )
        givenEducationType(educationType)
        return educationType
    }

    private EducationTheme newEducationTheme(String title, String content, EducationType educationType, UploadFile image) {
        EducationTheme educationTheme = new EducationTheme(
            id : getObjectId(),
            educationType : new EducationTypeRef(educationType),
            title : title,
            content : content
        )
        givenEducationTheme(educationTheme, image)
        return educationTheme
    }

    private EventEducation newEventEducation(String title, String content, String questions, EducationType educationType, EducationTheme educationTheme, Event event, String author, UploadFile recording) {
        EventEducation eventEducation = new EventEducation(
            type : 'event',
            id : getObjectId(),
            educationType : new EducationTypeRef(educationType),
            educationTheme : new EducationThemeRef(educationTheme),
            event : new EventRef(event),
            title : title,
            content : content,
            questions : questions,
            time: event.startTime,
            authorName : author
        )
        givenEducation(eventEducation, recording)
        return eventEducation
    }

    private SimpleEducation newSimpleEducation(String title, String content, String questions, EducationType educationType, EducationTheme educationTheme, Date time, String author, UploadFile recording) {
        SimpleEducation simpleEducation = new SimpleEducation(
            type : 'simple',
            id : getObjectId(),
            educationType : new EducationTypeRef(educationType),
            educationTheme : new EducationThemeRef(educationTheme),
            title : title,
            content : content,
            questions : questions,
            time: time,
            authorName : author
        )
        givenEducation(simpleEducation, recording)
        return simpleEducation
    }

    private Podcast newPodcast(String title, EducationType educationType, UploadFile image) {
        Podcast podcast = new Podcast(
            id : getObjectId(),
            educationType : new EducationTypeRef(educationType),
            title : title,
            subTitle : "${ title } - underrubrik",
            description : "Allt om: ${ title }",
            authorName : "Kalle Karlsson",
            copyright : "Kalle & Co",
            mainCategory : "Religion & Spirituality",
            subCategory : "Christianity",
            language : "sv-se",
            link : "http://www.hompage.se",
        )
        givenPodcast(podcast, image)
        return podcast
    }
}
