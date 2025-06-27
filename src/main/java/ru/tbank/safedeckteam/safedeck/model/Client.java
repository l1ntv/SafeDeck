package ru.tbank.safedeckteam.safedeck.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Client")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client extends AbstractEntity implements UserDetails {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "public_name")
    private String publicName;

    @Column(name = "country")
    private String country;

    @Column(name = "provider")
    private String provider;

    @Column(name = "device")
    private String device;

    @ManyToOne
    @JoinColumn(name = "register_IP_id", nullable = false)
    private IP registerIp;

    @Column(name = "is_subscriber")
    private Boolean isSubscriber;

    @ManyToMany(mappedBy = "clients")
    private Set<Role> roles = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "Clients_to_Boards",
            joinColumns = @JoinColumn(name = "client_id"),
            inverseJoinColumns = @JoinColumn(name = "board_id")
    )
    private Set<Board> boards = new HashSet<>();

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<ClientToSubscription> subscriptions = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<TrustedUserIP> trustedUserIps = new HashSet<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return email;
    }
}
