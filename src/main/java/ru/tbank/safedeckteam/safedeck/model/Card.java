package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Card")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Card extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "background_color_id", nullable = false)
    private Color color;

    @ManyToOne
    @JoinColumn(name = "board_id", nullable = false)
    private Board board;

    @Column(name = "field", nullable = false)
    private Boolean field;

    @ManyToMany(mappedBy = "cards")
    private Set<Role> roles = new HashSet<>();

    @OneToMany(mappedBy = "card")
    private Set<SendSecure> sendSecures = new HashSet<>();
}
