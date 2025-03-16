package app.service;


import app.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;
import app.repository.MessageRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class MessageService {
    private final MessageRepository messageRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    public List<Message> getChatMessages() {
        LocalDateTime now = LocalDateTime.now();
        return messageRepository.findBySentAtAfterOrderBySentAtAsc(now.minusHours(24));
    }

    public void save(Message message) {
        UUID uuid = UUID.randomUUID();
        message.setId(uuid);
        messageRepository.save(message);
    }
}
