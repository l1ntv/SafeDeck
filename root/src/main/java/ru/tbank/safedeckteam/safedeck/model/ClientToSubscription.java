package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "Client_to_subscription")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ClientToSubscription extends AbstractEntity {

    @ManyToOne
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    @Column(name = "subscription_date_start", nullable = false)
    private LocalDate subscriptionDateStart;

    @Column(name = "subscription_date_end")
    private LocalDate subscriptionDateEnd;

    @ManyToOne
    @JoinColumn(name = "subscription_plan_id", nullable = false)
    private SubscriptionPlan subscriptionPlan;

    @ManyToOne
    @JoinColumn(name = "sale_id")
    private Sale sale;
}
