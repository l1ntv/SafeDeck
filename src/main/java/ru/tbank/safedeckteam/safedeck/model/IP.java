package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "IP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IP extends AbstractEntity {

    @Column(name = "ip", nullable = false, unique = true)
    private String ip;
}
