package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Send_secure")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SendSecure extends AbstractEntity {

    @Column(name = "token", nullable = false)
    private String token;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;
}
