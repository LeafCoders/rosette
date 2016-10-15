package se.leafcoders.rosette.controller;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import se.leafcoders.rosette.config.RosetteSettings;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.service.SecurityService;

@Profile("development")
@RestController
public class DevelopmentController extends ApiV1Controller {
    @Autowired
    private SecurityService security;
    @Autowired
    private RosetteSettings rosetteSettings;

    @RequestMapping(value = "development/resetPermissionCache", method = RequestMethod.DELETE, produces = "application/json")
    public void resetPermissionCache(HttpServletResponse response) {
        security.resetPermissionCache();
    }

    @RequestMapping(value = "development/deleteAllUploads", method = RequestMethod.DELETE, produces = "application/json")
    public void deleteAllUploads(HttpServletResponse response) {
        String path = rosetteSettings.getUploadsPath();
        if (path != null && path.length() > 10) {
            try {
                Files.walk(Paths.get(path)).filter(Files::isRegularFile).forEach((p) -> {
                    if (p.toFile().isFile()) {
                        String fn = p.getFileName().toString();
                        if (fn.contains(".java") || fn.contains(".groovy") || fn.contains(".txt")
                                || fn.contains(".zip")) {
                            throw new ForbiddenException("Will not delete folder. It contains development files.");
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
                throw new ForbiddenException("Will not delete folder. It contains development files.");
            }
        }
    }
}
