package web;


import com.sun.net.httpserver.HttpsServer;
import model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import service.MessageService;

import java.util.List;
import java.util.UUID;

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
        List<Message> chatMessages = messageService.getChatMessages();
        return ResponseEntity.status(HttpStatus.OK).body(chatMessages);
    }

    @PostMapping
    public ResponseEntity<String> saveChatMessage(Message message) {

        messageService.save(message);
        return ResponseEntity.status(HttpStatus.CREATED).body("Saved a new message!");
    }
}
