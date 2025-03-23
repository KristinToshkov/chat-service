package app;

import app.model.Message;
import app.repository.MessageRepository;
import app.service.MessageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    private MessageRepository messageRepository;

    @InjectMocks
    private MessageService messageService;

    @Test
    void shouldSaveMessageWithGeneratedUUID() {
        // Given
        Message message = new Message();
        message.setSentAt(LocalDateTime.now());
        message.setAuthor("John Doe");
        message.setMessage("Hello, world!");

        // When
        messageService.save(message);

        // Then
        assertThat(message.getId()).isNotNull(); // Ensure UUID was set
        verify(messageRepository).save(message); // Ensure it was saved
    }

    @Test
    void shouldRetrieveOnlyRecentMessages() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        Message oldMessage = Message.builder()
                .id(UUID.randomUUID())
                .sentAt(now.minusDays(2))
                .message("Old message")
                .author("Alice")
                .build();

        Message recentMessage = Message.builder()
                .id(UUID.randomUUID())
                .sentAt(now.minusHours(10))
                .message("Recent message")
                .author("Bob")
                .build();

        List<Message> mockMessages = List.of(recentMessage);

        when(messageRepository.findBySentAtAfterOrderBySentAtAsc(any()))
                .thenReturn(mockMessages);

        // When
        List<Message> messages = messageService.getChatMessages();

        // Then
        assertThat(messages).isNotEmpty();
        assertThat(messages).hasSize(1);
        assertThat(messages.get(0)).isEqualTo(recentMessage);
        verify(messageRepository).findBySentAtAfterOrderBySentAtAsc(any());
    }

    @Test
    void shouldReturnEmptyListWhenNoRecentMessages() {
        // Given
        when(messageRepository.findBySentAtAfterOrderBySentAtAsc(any()))
                .thenReturn(Collections.emptyList());

        // When
        List<Message> messages = messageService.getChatMessages();

        // Then
        assertThat(messages).isEmpty();
        verify(messageRepository).findBySentAtAfterOrderBySentAtAsc(any());
    }
}
