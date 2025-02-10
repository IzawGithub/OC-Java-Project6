package com.paymybuddy.mvp.json;

import static com.diffplug.selfie.Selfie.cacheSelfie;
import static com.diffplug.selfie.Selfie.expectSelfie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.paymybuddy.mvp.HelperTest;
import com.paymybuddy.mvp.model.internal.Money;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

@JsonTest
@TestInstance(Lifecycle.PER_CLASS)
class MoneyTest {
    @Autowired
    private JacksonTester<Money> json;

    private final Money money = HelperTest.money();
    private String moneyJson;

    @BeforeAll
    void setUp() {
        moneyJson = cacheSelfie(() -> json.write(money).getJson()).toBe("{\"balance\": 13.37}");
    }

    @Test
    void deserialize() throws IOException {
        final var expected = money;
        final var actual = json.parse(moneyJson).getObject();
        assertEquals(expected, actual);
    }

    @Test
    void serialize() {
        expectSelfie(moneyJson).toBe("{\"balance\": 13.37}");
    }
}
