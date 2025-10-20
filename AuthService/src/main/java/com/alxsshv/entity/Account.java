package com.alxsshv.entity;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "accounts")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class Account implements Serializable {

    @Id
    @Column(name = "id")
    private UUID id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "status", nullable = false)
    private Status status;


    @BatchSize(size = 20)
    @Enumerated(EnumType.STRING)
    @ElementCollection(targetClass = Authorities.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "authorities", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "authority", nullable = false)
    private Set<Authorities> authorities = new HashSet<>();


    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Account account)) return false;
        return Objects.equals(id, account.id) && Objects.equals(email, account.email) && status == account.status && Objects.equals(authorities, account.authorities);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email, status, authorities);
    }
}
