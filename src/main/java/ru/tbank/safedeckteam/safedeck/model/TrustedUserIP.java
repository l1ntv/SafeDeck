package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "Trusted_user_IP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrustedUserIP extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private Client user;

    @ManyToOne
    @JoinColumn(name = "ip_id", nullable = false)
    private IP ip;
}
