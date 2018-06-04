package se.leafcoders.rosette.persistence.service;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.JsonNode;
import se.leafcoders.rosette.controller.dto.MessageIn;
import se.leafcoders.rosette.controller.dto.MessageOut;
import se.leafcoders.rosette.permission.PermissionType;
import se.leafcoders.rosette.persistence.model.Message;
import se.leafcoders.rosette.persistence.repository.MessageRepository;

@Service
public class MessageService extends PersistenceService<Message, MessageIn, MessageOut> {

    private ConcurrentHashMap<String, String> messageCache = new ConcurrentHashMap<>();

    public MessageService(MessageRepository repository) {
        super(Message.class, PermissionType.MESSAGES, repository);
    }

    private String getMessage(String key) {
        if (messageCache.isEmpty()) {
            // NOTE: For now only read with messages without specified language
            messageCache.putAll(readMany(false).stream().filter(message -> message.getLanguage() == null)
                    .collect(Collectors.toMap(Message::getKey, Message::getMessage)));
        }
        return messageCache.get(key);
    }

    @Override
    public Message update(Long id, Class<MessageIn> inClass, HttpServletRequest request, boolean checkPermissions) {
        Message message = super.update(id, inClass, request, checkPermissions);
        messageCache.clear();
        return message;
    }

    public String lookup(String key, Object... args) {
        String message = getMessage(key);
        if (message != null && !message.isEmpty()) {
            MessageFormat format = new MessageFormat(message, Locale.getDefault());
            return format.format(args);
        }
        return "[" + key + "]";
    }

    @Override
    protected Message convertFromInDTO(MessageIn dto, JsonNode rawIn, Message item) {
        if (rawIn == null || rawIn.has("key")) {
            // Only allow update of message for now
            // item.setKey(dto.getKey());
        }
        if (rawIn == null || rawIn.has("language")) {
            // Only allow update of message for now
            // item.setLanguage(dto.getLanguage());
        }
        if (rawIn == null || rawIn.has("message")) {
            item.setMessage(dto.getMessage());
        }
        return item;
    }

    @Override
    protected MessageOut convertToOutDTO(Message item) {
        MessageOut dto = new MessageOut();
        dto.setId(item.getId());
        dto.setKey(item.getKey());
        dto.setLanguage(item.getLanguage());
        dto.setMessage(item.getMessage());
        return dto;
    }

}
