package com.paymybuddy.mvp.model;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.paymybuddy.mvp.model.dto.UserShowTransactionDTO;
import com.paymybuddy.mvp.model.internal.Money;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;

import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
public class BankTransaction {
    public enum ESens {
        FROM_APP_TO_BANK,
        FROM_BANK_TO_APP
    }

    @Builder.Default
    @Id
    @NonNull private UUID id = UUID.randomUUID();

    @AttributeOverride(name = "uint", column = @Column(name = "amount"))
    @JsonUnwrapped
    @NonNull private Money amount;

    // @JdbcType(PostgreSQLEnumJdbcType.class)
    @Enumerated(EnumType.STRING)
    @NonNull private ESens sens;

    @ManyToOne
    @JoinColumn(name = "id_user")
    @NonNull private User user;

    @Builder.Default
    @NonNull private LocalDateTime date = LocalDateTime.now();

    public UserShowTransactionDTO toUserShowTransactionDTO() {
        final var amount =
                switch (sens) {
                    case ESens.FROM_APP_TO_BANK -> this.amount.getUint().negate();
                    case ESens.FROM_BANK_TO_APP -> this.amount.getUint();
                };
        return UserShowTransactionDTO.builder().amount(amount).date(date).build();
    }

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
        final var bankTransaction = (BankTransaction) o;
        return getId() != null && Objects.equals(getId(), bankTransaction.getId());
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
