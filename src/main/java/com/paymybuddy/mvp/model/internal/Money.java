package com.paymybuddy.mvp.model.internal;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.paymybuddy.mvp.errors.EErrorMoney;
import com.paymybuddy.mvp.errors.EErrorPayMyBuddy;

import jakarta.persistence.Embeddable;

import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import net.xyzsd.dichotomy.Conversion;
import net.xyzsd.dichotomy.Result;
import net.xyzsd.dichotomy.trying.Try;

import org.springframework.lang.NonNull;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;

/**
 * Represent money in a safe manner.
 *
 * <p>This is a {@link <a href=
 * "https://doc.rust-lang.org/rust-by-example/generics/new_types.html">newtype</a>} around a
 * {@link BigDecimal}.
 *
 * <p>This type make sure that the value given is not negative, and format it to take into account
 * cents.
 */
@NoArgsConstructor(force = true)
@Builder
@Embeddable
@Value
public class Money {
    @NonNull @JsonSerialize(using = MoneySerializer.class)
    @JsonProperty("balance")
    private BigDecimal uint;

    /**
     * @param change
     * @return New money that is equal to self + change
     */
    public @NonNull Money add(@NonNull final Money change) {
        final var newBalance = this.getUint().add(change.getUint());
        // This will never error, Money only error if negative, and we're not going to overflow
        return Money.builder().uint(newBalance).tryBuild().expect();
    }

    /**
     * @param change
     * @return Ok(Money) if change is high enough, Err(ErrorMoney) otherwise.
     */
    public @NonNull Result<Money, EErrorMoney> substract(@NonNull final Money change) {
        final var newBalance = this.getUint().subtract(change.getUint());
        return Money.builder().uint(newBalance).tryBuild();
    }

    /**
     * @param change
     * @return New money that is equal to self * change
     */
    public @NonNull Money multiply(@NonNull final Money change) {
        final var newBalance = this.getUint().multiply(change.getUint());
        // This will never error, we can't multiply by a negative because Money will never be
        // negative
        return Money.builder().uint(newBalance).tryBuild().expect();
    }

    @Override
    public @NonNull String toString() {
        return uint.toString();
    }

    public Money(@NonNull final String maybeUint) throws EErrorMoney {
        this.uint = Money.builder().uint(new BigDecimal(maybeUint)).build().getUint();
    }

    public Money(@NonNull final Double maybeUint) throws EErrorMoney {
        this.uint = Money.builder().uint(BigDecimal.valueOf(maybeUint)).build().getUint();
    }

    public Money(@NonNull final BigDecimal maybeUint) throws EErrorMoney {
        this.uint = Money.builder().uint(maybeUint).build().getUint();
    }

    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

    /**
     * Same hack as `Email` class
     *
     * @see Email
     */
    private Money(@NonNull final BigDecimal uint, boolean privateValue) {
        this.uint = uint;
    }

    public static class MoneyBuilder {
        public @NonNull Result<Money, EErrorMoney> tryBuild() {
            return Conversion.toResult(Try.wrap(this::build)).mapErr(EErrorMoney.class::cast);
        }

        public @NonNull Money build() throws EErrorMoney {
            return Result.ofNullable(this.uint)
                    .mapErr(error -> (EErrorMoney) new EErrorMoney().new Null())
                    .flatMap(uint -> {
                        if (uint.compareTo(BigDecimal.ZERO) < 0) {
                            return Result.ofErr(
                                    (EErrorMoney) new EErrorMoney().new NoNegative(uint));
                        }
                        return Result.ofOK(new Money(uint, true));
                    })
                    .getOrThrow(error -> error);
        }
    }

    static class MoneySerializer extends JsonSerializer<BigDecimal> {
        @Override
        public void serialize(BigDecimal value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {
            jgen.writeNumber(Money.builder()
                    .uint(value)
                    .tryBuild()
                    .mapErr(EErrorPayMyBuddy.class::cast)
                    .map(Money::getUint)
                    .map(decimal -> decimal.setScale(2, RoundingMode.UP))
                    .map(FORMAT::format)
                    .expect());
        }
    }
}
