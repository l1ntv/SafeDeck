package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "Color")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Color extends AbstractEntity {

    @Column(name = "rgb_code", nullable = false)
    private String rgbCode;

    @OneToMany(mappedBy = "color")
    private Set<Board> boards = new HashSet<>();

    @OneToMany(mappedBy = "color")
    private Set<Card> cards = new HashSet<>();
}
