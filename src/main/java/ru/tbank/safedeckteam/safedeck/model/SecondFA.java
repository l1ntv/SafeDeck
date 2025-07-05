package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "second_fa", schema = "public")@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecondFA {

    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "default_seq")
    @SequenceGenerator(name = "default_seq", sequenceName = "default_seq", allocationSize = 50)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "generated_code", nullable = false, length = 5)
    private String generatedCode;
}
