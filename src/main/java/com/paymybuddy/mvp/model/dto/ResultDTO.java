package com.paymybuddy.mvp.model.dto;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.paymybuddy.mvp.errors.EErrorPayMyBuddy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import net.xyzsd.dichotomy.Result;
import net.xyzsd.dichotomy.Result.Err;
import net.xyzsd.dichotomy.Result.OK;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.IOException;

@AllArgsConstructor
@NoArgsConstructor(force = true)
@Builder
@Value
public class ResultDTO<T> {
    @JsonSerialize(using = ResultSerializer.class)
    @JsonUnwrapped
    private Result<T, EErrorPayMyBuddy> result;

    public ResponseEntity<ResultDTO<T>> toResponseEntity() {
        return switch (result) {
            case OK(T ok) -> new ResponseEntity<>(this, HttpStatus.OK);
            case Err(EErrorPayMyBuddy err) -> new ResponseEntity<>(this, HttpStatus.BAD_REQUEST);
        };
    }

    static class ResultSerializer extends JsonSerializer<Result<?, ?>> {
        @Override
        public void serialize(Result<?, ?> value, JsonGenerator jgen, SerializerProvider provider)
                throws IOException {
            switch (value) {
                case OK(Object ok) -> jgen.writePOJOField("success", ok);
                case Err(Object err) -> {
                    jgen.writePOJOField("error", err);
                    jgen.writePOJOField("message", err.toString());
                }
            }
        }

        @Override
        public boolean isUnwrappingSerializer() {
            return true;
        }
    }
}
