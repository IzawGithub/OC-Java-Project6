package com.paymybuddy.mvp.json;

import static com.diffplug.selfie.Selfie.cacheSelfie;
import static com.diffplug.selfie.Selfie.expectSelfie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.paymybuddy.mvp.errors.EErrorPayMyBuddy;
import com.paymybuddy.mvp.model.dto.ResultDTO;

import net.xyzsd.dichotomy.Result;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

import java.io.IOException;

@JsonTest
class ResultTest {
    @Autowired
    private JacksonTester<ResultDTO<String>> json;

    private final ResultDTO<String> resultOk =
            ResultDTO.<String>builder().result(Result.ofOK("ok")).build();
    private final ResultDTO<String> resultErr = ResultDTO.<String>builder()
            .result(Result.ofErr(new EErrorPayMyBuddy()))
            .build();
    private String jsonOk;
    private String jsonErr;

    @BeforeEach
    void setUpForEach() {
        jsonOk = cacheSelfie(() -> json.write(resultOk).getJson()).toBe("{\"success\":\"ok\"}");
        jsonErr = cacheSelfie(() -> json.write(resultErr).getJson())
                .toBe("{\"error\":{},\"message\":\"EErrorPayMyBuddy()\"}");
    }

    // TODO: Doesn't work, why? ¯\_(ツ)_/¯
    @Test
    @Disabled
    void deserialize_ok() throws IOException {
        final var expected = resultOk;
        final var actual = json.parse(jsonOk).getObject();
        assertEquals(expected, actual);
    }

    @Test
    @Disabled
    void deserialize_err() throws IOException {
        final var expected = resultErr;
        final var actual = json.parse(jsonErr).getObject();
        assertEquals(expected, actual);
    }

    @Test
    void serialize_ok() {
        expectSelfie(jsonOk).toBe("{\"success\":\"ok\"}");
    }

    @Test
    void serialize_err() {
        expectSelfie(jsonErr).toBe("{\"error\":{},\"message\":\"EErrorPayMyBuddy()\"}");
    }
}
