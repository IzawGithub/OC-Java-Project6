package com.paymybuddy.mvp.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.paymybuddy.mvp.model.internal.Email;
import com.paymybuddy.mvp.model.internal.Money;
import com.paymybuddy.mvp.model.internal.Secret;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.proxy.HibernateProxy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Builder
@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class User {
    @Builder.Default
    @Id
    @NonNull private UUID id = UUID.randomUUID();

    @NonNull private String username;

    @Embedded
    @JsonUnwrapped
    @NonNull private Email email;

    @AttributeOverride(name = "secret", column = @Column(name = "password"))
    @Embedded
    @JsonUnwrapped
    @NonNull private Secret password;

    @Builder.Default
    @NonNull private String role = "USER";

    @Builder.Default
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "many_users_knows_many_users",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_friend"))
    @NonNull private List<User> buddies = new ArrayList<>();

    @Builder.Default
    @AttributeOverride(name = "uint", column = @Column(name = "balance"))
    @JsonUnwrapped
    @NonNull private Money balance = Money.builder().uint(new BigDecimal(0)).tryBuild().expect();

    // Can't use lombok.Data with JPA entity
    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        final var oEffectiveClass =
                switch (o) {
                    case HibernateProxy proxy -> proxy.getHibernateLazyInitializer()
                            .getPersistentClass();
                    default -> o.getClass();
                };
        final var thisEffectiveClass =
                switch (this) {
                    case HibernateProxy proxy -> proxy.getHibernateLazyInitializer()
                            .getPersistentClass();
                    default -> this.getClass();
                };
        if (thisEffectiveClass != oEffectiveClass) {
            return false;
        }
        final var user = (User) o;
        return this.getId() != null && Objects.equals(this.getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return switch (this) {
            case HibernateProxy proxy -> proxy.getHibernateLazyInitializer()
                    .getPersistentClass()
                    .hashCode();
            default -> this.getClass().hashCode();
        };
    }
}
