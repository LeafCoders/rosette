package se.leafcoders.rosette.service;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentHashMap.KeySetView;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import lombok.NonNull;
import se.leafcoders.rosette.controller.dto.EventOut;

@Service
public class SseService {

    private static final Map<String, String> PING_EVENT = Collections.singletonMap("ping", "fromServer");

    private final KeySetView<SseEmitter, Boolean> sseEmitters = ConcurrentHashMap.newKeySet();
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    @Scheduled(fixedDelay = 120_1000L)
    private void pingEmitters() {
        if (sseEmitters.isEmpty()) {
            return;
        }
        pingAll();
    }

    @NonNull
    public SseEmitter createNewEmitter() {
        SseEmitter emitter = new SseEmitter(-1L); // No timeout
        emitter.onCompletion(() -> {
            sseEmitters.remove(emitter);
        });
        sseEmitters.add(emitter);
        return emitter;
    }

    public void sendEvent(@NonNull final EventOut event) {
        sendToAll("event", event);
    }

    public void pingAll() {
        sendToAll("ping", PING_EVENT);
    }

    private void sendToAll(@NonNull String eventType, @NonNull final Object eventData) {
        final Map<String, Object> event = new HashMap<>();
        event.put("type", eventType);
        event.put("data", eventData);
        executor.submit(() -> {
            sseEmitters.forEach(emitter -> {
                try {
                    emitter.send(event);
                } catch (IOException ignore) {
                    sseEmitters.remove(emitter);
                }
            });
        });
    }
}
