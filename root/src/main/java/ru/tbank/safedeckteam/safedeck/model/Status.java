package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Status")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Status extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @OneToMany(mappedBy = "status")
    private List<SecureLog> secureLogs = new ArrayList<>();
}
