package entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString          // ← добавьте эту аннотацию
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private Integer age;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void createdData() {
        createdAt = LocalDateTime.now();
    }
}