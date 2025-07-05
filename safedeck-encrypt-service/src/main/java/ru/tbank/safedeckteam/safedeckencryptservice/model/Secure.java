package ru.tbank.safedeckteam.safedeckencryptservice.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "secure", schema = "public")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Secure {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "default_seq", allocationSize = 50)
    private Long id;

    @Column(name = "card_id", nullable = false, unique = true)
    private Long cardId;

    @Column(name = "data", nullable = false, columnDefinition = "TEXT")
    private String data;
}
