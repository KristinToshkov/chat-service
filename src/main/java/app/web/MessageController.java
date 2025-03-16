package app.web;


import com.sun.net.httpserver.HttpsServer;
import app.model.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import app.service.MessageService;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/chat")
public class MessageController {
    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<List<Message>> getChat() {
        log.info("Sending messages back");
        List<Message> chatMessages = messageService.getChatMessages();
        return ResponseEntity.status(HttpStatus.OK).body(chatMessages);
    }

    @PostMapping
    public ResponseEntity<String> saveChatMessage(@RequestBody Message message) {
        log.info("Saving message:" + message.getId() + " " + message.getMessage() + " " + message.getAuthor() + " " + message.getSentAt());
        if (message.getMessage() == null || message.getAuthor() == null) {
            return ResponseEntity.badRequest().body("Invalid message data");
        }
        log.info("Received message from " + message.getAuthor());

        messageService.save(message);

        return ResponseEntity.status(HttpStatus.CREATED).body("Saved a new message!");
    }



}
