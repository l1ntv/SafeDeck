package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Board extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Client owner;

    @ManyToOne
    @JoinColumn(name = "background_color_id", nullable = false)
    private Color color;

    @OneToMany(mappedBy = "board")
    private Set<Card> cards = new HashSet<>();

    @OneToMany(mappedBy = "board")
    private Set<ControlQuestion> controlQuestions = new HashSet<>();

    @ManyToMany(mappedBy = "boards")
    private Set<Client> clients = new HashSet<>();
}
