package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "Secure_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SecureLog extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @ManyToOne
    @JoinColumn(name = "card_id", nullable = false)
    private Card card;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Client user;

    @ManyToOne
    @JoinColumn(name = "ip_id", nullable = false)
    private IP ip;

    @Column(name = "view_time", nullable = false)
    private LocalDateTime viewTime;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    private Status status;

    @Column(name = "country", nullable = false)
    private String country;

    @Column(name = "device", nullable = false)
    private String device;

    @Column(name = "provider", nullable = false)
    private String provider;
}
