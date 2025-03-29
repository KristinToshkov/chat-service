package app;
import app.model.Message;
import app.service.MessageService;
import app.web.MessageController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(MessageController.class)
public class MessageControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MessageService messageService;

    private Message createTestMessage() {
        return Message.builder()
                .id(UUID.randomUUID())
                .author("testUser")
                .message("Hello world")
                .sentAt(LocalDateTime.now())
                .build();
    }

    @Test
    public void getChatMessages_ReturnsMessages() throws Exception {
        // Arrange
        Message message1 = createTestMessage();
        Message message2 = createTestMessage();
        message2.setMessage("Another message");

        when(messageService.getChatMessages()).thenReturn(List.of(message1, message2));

        // Act & Assert
        mockMvc.perform(get("/api/chat")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", notNullValue()))
                .andExpect(jsonPath("$[0].author", is("testUser")))
                .andExpect(jsonPath("$[0].message", is("Hello world")))
                .andExpect(jsonPath("$[0].sentAt", notNullValue()));
    }

    @Test
    public void getChatMessages_EmptyList_ReturnsEmptyArray() throws Exception {
        // Arrange
        when(messageService.getChatMessages()).thenReturn(Collections.emptyList());

        // Act & Assert
        mockMvc.perform(get("/api/chat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", empty()));
    }

    @Test
    public void saveMessage_MissingAuthor_ReturnsBadRequest() throws Exception {
        // Arrange
        String invalidRequestBody = """
                {
                    "message": "Hello without author"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid message data"));
    }

    @Test
    public void saveMessage_MissingMessage_ReturnsBadRequest() throws Exception {
        // Arrange
        String invalidRequestBody = """
                {
                    "author": "testUser"
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid message data"));
    }

    @Test
    public void saveMessage_EmptyMessage_ReturnsNonOverwrittingRequest() throws Exception {
        // Arrange
        String invalidRequestBody = """
                {
                    "author": "testUser",
                    "message": ""
                }
                """;

        // Act & Assert
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequestBody))
                .andExpect(status().is2xxSuccessful());
    }

    @Test
    public void saveMessage_LongMessage_ReturnsCreated() throws Exception {
        // Arrange
        String longMessage = "A".repeat(1000);
        String requestBody = """
                {
                    "author": "testUser",
                    "message": "%s"
                }
                """.formatted(longMessage);

        // Act & Assert
        mockMvc.perform(post("/api/chat")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated());
    }
}
