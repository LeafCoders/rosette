package se.leafcoders.rosette.controller;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.fileUpload;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import se.leafcoders.rosette.IdResult;
import se.leafcoders.rosette.IdResultHandler;
import se.leafcoders.rosette.RosetteSettings;
import se.leafcoders.rosette.controller.dto.AssetFolderIn;
import se.leafcoders.rosette.data.UserData;
import se.leafcoders.rosette.persistence.model.Asset;
import se.leafcoders.rosette.persistence.model.AssetFolder;
import se.leafcoders.rosette.persistence.model.Permission;
import se.leafcoders.rosette.persistence.model.Persistable;
import se.leafcoders.rosette.persistence.model.User;
import se.leafcoders.rosette.persistence.repository.AssetFolderRepository;
import se.leafcoders.rosette.persistence.repository.AssetRepository;
import se.leafcoders.rosette.persistence.repository.PermissionRepository;
import se.leafcoders.rosette.persistence.repository.UserRepository;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
@Sql("/setupBeforeEachTest.sql")
abstract class AbstractControllerTest {

    public static final MediaType CONTENT_JSON = new MediaType(
        MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8")
    );

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private RosetteSettings rosetteSettings;

    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    protected MockMvc mockMvc;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {
        this.mappingJackson2HttpMessageConverter = (MappingJackson2HttpMessageConverter) Arrays.asList(converters).stream()
            .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().orElse(null);

        assertNotNull("the JSON message converter must not be null", this.mappingJackson2HttpMessageConverter);
    }

    protected void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).apply(SecurityMockMvcConfigurers.springSecurity()).build();
        deleteAllAssetFiles();
    }

    protected String mapToJson(Function<Map<String, Object>, Map<String, Object>> func) throws JsonProcessingException {
        Map<String, Object> data = new HashMap<String, Object>();
        ObjectMapper mapper = new ObjectMapper();
        return mapper.writeValueAsString(func.apply(data));
    }

    protected String json(Object o) throws IOException {
        if (o instanceof Persistable) {
            fail("json() must be an IN dto!");
        }
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    protected String xAuthToken(User user) {
        final String jwtSecret = "developmentSimpleJwtSecretToken";
        return Jwts.builder().setSubject(user.getId().toString()).signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
    }

    protected ResultActions withUser(User user, MockHttpServletRequestBuilder builder) throws Exception {
        return mockMvc.perform(builder.header("X-AUTH-TOKEN", xAuthToken(user))).andDo(print());
    }
    
    protected ResultActions withUserPrint(User user, MockHttpServletRequestBuilder builder) throws Exception {
        return withUser(user, builder);
    }

    protected MockHttpServletRequestBuilder uploadFile(Long folderId, String fileName, String asFileName, String mimeType) throws IOException {
        File file = new File("src/test/resources/" + fileName);
        FileInputStream fis = new FileInputStream(file);
        MockMultipartFile multipartFile = new MockMultipartFile("file", file.getName(), mimeType, fis);
        return fileUpload("/api/files")
            .file(multipartFile)
            .param("folderId", folderId.toString())
            .param("fileName", asFileName);
    }

    // ---------------------------------

    @Autowired
    protected PermissionRepository permissionRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected AssetFolderRepository assetFolderRepository;

    @Autowired
    protected AssetRepository assetRepository;
    
    protected User user1 = UserData.user1();
    protected User user2 = UserData.user2();

    protected User givenUser(User user) {
        return user = userRepository.save(user);
    }

    protected Permission givenPermissionForUser(User user, String patterns) {
        return permissionRepository.save(new Permission("For user: " + user.getFullName(), Permission.LEVEL_USER, user.getId(), patterns));
    }

    protected Permission givenPermissionForAll(String patterns) {
        return permissionRepository.save(new Permission("For all", Permission.LEVEL_ALL_USERS, null, patterns));
    }

    protected Asset givenAssetInFolder(Long folderId, String fileName, String asFileName, String mimeType) throws Exception {
        createUploadUser();

        IdResult idResult = new IdResult();
        withUser(uploadUser, uploadFile(folderId, fileName, asFileName, mimeType))
            .andExpect(status().isCreated())
            .andDo(IdResultHandler.assignTo("$.id", idResult));
        return assetRepository.findOne(idResult.id);
    }

    protected AssetFolder givenAssetFolder(AssetFolderIn assetFolder) throws Exception {
        createUploadUser();
        
        IdResult idResult = new IdResult();
        withUser(uploadUser, post("/api/assetFolders").content(json(assetFolder)).contentType(AbstractControllerTest.CONTENT_JSON))
            .andExpect(status().isCreated())
            .andExpect(content().contentType(AbstractControllerTest.CONTENT_JSON))
            .andDo(IdResultHandler.assignTo("$.id", idResult));
        return assetFolderRepository.findOne(idResult.id);
    }

    // UPLOAD USER
    
    private User uploadUser = null;

    private void createUploadUser() {
        if (uploadUser == null) {
            uploadUser = userRepository.save(UserData.upload());
            givenPermissionForUser(uploadUser, "*");
        }
    }


    private void deleteAllAssetFiles() {
        String path = rosetteSettings.getFilesPath();
        if (path != null && path.length() > 10) {
            try {
                Files.walk(Paths.get(path)).filter(Files::isRegularFile).forEach((p) -> {
                    if (p.toFile().isFile()) {
                        String fn = p.getFileName().toString();
                        if (fn.contains(".java") || fn.contains(".groovy") || fn.contains(".txt") || fn.contains(".sql") || fn.contains(".zip")) {
                            fail("Will not delete folder. It contains development files.");
                        }
                    }
                });
    
                // Delete the files
                final Path directory = Paths.get(path);
                Files.walkFileTree(directory, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                        Files.delete(file);
                        return FileVisitResult.CONTINUE;
                    }
    
                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        if (dir.compareTo(directory) != 0) {
                            Files.delete(dir);
                        }
                        return FileVisitResult.CONTINUE;
                    }
                });
            } catch (IOException e) {
                fail("Will not delete folder. It contains development files.");
            }
        }
    }

}
