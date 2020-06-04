package se.leafcoders.rosette.endpoint.service;

import javax.transaction.Transactional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.core.exception.ApiError;
import se.leafcoders.rosette.core.exception.ForbiddenException;
import se.leafcoders.rosette.core.permission.PermissionValue;
import se.leafcoders.rosette.core.service.SecurityService;
import se.leafcoders.rosette.core.service.SseService;
import se.leafcoders.rosette.endpoint.event.EventPermissionValue;

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
        final PermissionValue permission = new EventPermissionValue().read();
        if (securityService.isPermitted(permission)) {
            return sseService.createNewEmitter();
        } else {
            throw new ForbiddenException(ApiError.MISSING_PERMISSION, permission.toString());
        }
    }

}
