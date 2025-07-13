package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Sale")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Sale extends AbstractEntity {

    @Column(name = "month_left_bound", nullable = false)
    private int monthLeftBound;

    @Column(name = "discount_percent", nullable = false)
    private int discountPercent;
}
