package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "IP")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class IP extends AbstractEntity {

    @Column(name = "ip", nullable = false, unique = true)
    private String ip;
}
