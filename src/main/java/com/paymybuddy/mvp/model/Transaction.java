package com.paymybuddy.mvp.model;

import com.paymybuddy.mvp.model.internal.Money;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class Transaction {
    @Builder.Default
    @Id
    @NonNull private UUID id = UUID.randomUUID();

    @AttributeOverride(name = "uint", column = @Column(name = "amount"))
    @NonNull private Money amount;

    private String description;

    @ManyToOne
    @JoinColumn(name = "id_sender")
    @NonNull private User sender;

    @ManyToOne
    @JoinColumn(name = "id_receiver")
    @NonNull private User receiver;

    @Builder.Default
    @NonNull private LocalDateTime date = LocalDateTime.now();

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
        final var transaction = (Transaction) o;
        return getId() != null && Objects.equals(getId(), transaction.getId());
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
