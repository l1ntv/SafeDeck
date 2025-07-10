package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "Subscription_plan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SubscriptionPlan extends AbstractEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "max_employee_count")
    private int maxEmployeeCount;

    @Column(name = "max_price_per_month")
    private int maxPricePerMonth;
}
