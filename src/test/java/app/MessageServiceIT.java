package app;

import app.model.Message;
import app.repository.MessageRepository;
import app.service.MessageService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Transactional
class MessageServiceIT {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MessageRepository messageRepository;

    @Test
    void shouldSaveMessageWithGeneratedUUID() {
        // Given
        Message message = new Message();
        message.setSentAt(LocalDateTime.now());
        message.setAuthor("John Doe");
        message.setMessage("Hello, world!");

        // When
        messageService.save(message);
        Optional<Message> savedMessage = messageRepository.findById(message.getId());

        // Then
        assertThat(savedMessage).isPresent();
        assertThat(savedMessage.get().getId()).isNotNull();
        assertThat(savedMessage.get().getMessage()).isEqualTo("Hello, world!");
    }

    @Test
    void shouldRetrieveOnlyRecentMessages() {
        // Given
        LocalDateTime now = LocalDateTime.now();

        Message oldMessage = Message.builder()
                .id(UUID.randomUUID())
                .sentAt(now.minusDays(2))  // Older than 24 hours
                .message("Old message")
                .author("Alice")
                .build();

        Message recentMessage = Message.builder()
                .id(UUID.randomUUID())
                .sentAt(now.minusHours(10))  // Within 24 hours
                .message("Recent message")
                .author("Bob")
                .build();

        messageRepository.saveAll(List.of(oldMessage, recentMessage));

        // When
        List<Message> messages = messageService.getChatMessages();

        // Then
        assertThat(messages).isNotEmpty();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0).getMessage()).isEqualTo("Recent message");
    }

    @Test
    void shouldReturnEmptyListWhenNoRecentMessages() {
        // Given
        messageRepository.deleteAll();

        // When
        List<Message> messages = messageService.getChatMessages();

        // Then
        assertThat(messages).isEmpty();
    }
}
