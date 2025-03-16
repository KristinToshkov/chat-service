package app.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Service
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Message {

    @Id
    private UUID id;

    @Column(nullable = false)
    private LocalDateTime sentAt;

    private String message;

    @Column(nullable = false)
    private String author;
}

