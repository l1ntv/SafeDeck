package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

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
    private List<Board> boards = new ArrayList<>();

    @OneToMany(mappedBy = "color")
    private List<Card> cards = new ArrayList<>();
}
