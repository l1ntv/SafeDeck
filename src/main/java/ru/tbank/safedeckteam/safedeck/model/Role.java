package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Role extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "board_id", nullable = false)
    private int boardId;

    @Column(name = "is_personal_role", nullable = false)
    private boolean isPersonalRole;

    @Column(name = "is_editor", nullable = false)
    private boolean isEditor;

    @ManyToMany
    @JoinTable(
            name = "Role_to_Client",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<Client> clients = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "Role_to_Card",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "card_id")
    )
    private Set<Card> cards = new HashSet<>();
}
