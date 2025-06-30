package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Board")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Board extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String name;

    // ----- Владелец доски -----
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false)
    private Client owner;

    // ----- Цвет фона доски -----
    @ManyToOne
    @JoinColumn(name = "background_color_id", nullable = false)
    private Color color;

    // ----- Другие пользователи, имеющие доступ к доске -----
    @ManyToMany
    @JoinTable(
            name = "Clients_to_Boards",
            joinColumns = @JoinColumn(name = "board_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private Set<Client> clients = new HashSet<>();

    // ----- Связанные сущности -----
    @OneToMany(mappedBy = "board")
    private Set<Card> cards = new HashSet<>();

    @OneToMany(mappedBy = "board")
    private Set<ControlQuestion> controlQuestions = new HashSet<>();
}