package se.leafcoders.rosette.controller;

import javax.transaction.Transactional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.exception.ApiError;
import se.leafcoders.rosette.exception.ForbiddenException;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.permission.PermissionValue;
import se.leafcoders.rosette.service.SecurityService;
import se.leafcoders.rosette.service.SseService;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/sse", produces = "text/event-stream")
public class SseController {

    private final SseService sseService;
    private final SecurityService securityService;

    @GetMapping(value = "/events")
    public SseEmitter events() {
        // User is required to have full read permission to events
        final PermissionValue permission = PermissionType.events().read();
        if (securityService.isPermitted(permission)) {
            return sseService.createNewEmitter();
        } else {
            throw new ForbiddenException(ApiError.MISSING_PERMISSION, permission.toString());
        }
    }

}
