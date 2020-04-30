package se.leafcoders.rosette.controller;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.NotSupportedException;
import javax.transaction.Transactional;

import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import se.leafcoders.rosette.controller.dto.MessageIn;
import se.leafcoders.rosette.controller.dto.MessageOut;
import se.leafcoders.rosette.persistence.service.MessageService;

@RequiredArgsConstructor
@Transactional
@RestController
@RequestMapping(value = "api/messages", produces = "application/json")
public class MessagesController {

    private final MessageService messageService;

    @GetMapping(value = "/{id}")
    public MessageOut getMessage(@PathVariable Long id) {
        return messageService.toOut(messageService.read(id, true));
    }

    @GetMapping
    public Collection<MessageOut> getMessages(HttpServletRequest request) {
        Sort sort = Sort.by("key").ascending();
        return messageService.toOut(messageService.readMany(sort, true));
    }

    @PostMapping(consumes = "application/json")
    public ResponseEntity<MessageOut> postMessage(@RequestBody MessageIn message) throws NotSupportedException {
        throw new NotSupportedException();
        // TODO: return new
        // ResponseEntity<MessageOut>(messageService.toOut(messageService.create(message,
        // true)), HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = "application/json")
    public MessageOut putMessage(@PathVariable Long id, HttpServletRequest request) {
        return messageService.toOut(messageService.update(id, MessageIn.class, request, true));
    }

    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) throws NotSupportedException {
        throw new NotSupportedException();
        // TODO: return messageService.delete(id, true);
    }
}
