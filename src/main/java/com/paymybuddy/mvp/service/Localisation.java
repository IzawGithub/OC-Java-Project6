package com.paymybuddy.mvp.service;

import fluent.bundle.FluentBundle;
import fluent.bundle.FluentResource;
import fluent.functions.icu.ICUFunctionFactory;
import fluent.syntax.parser.FTLParser;
import fluent.syntax.parser.FTLStream;

import lombok.Getter;

import net.xyzsd.dichotomy.trying.Try;

import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public final class Localisation {
    private @Getter FluentBundle bundle;
    private static final Locale CURRENT_LOCALE = Locale.US;

    private static class SingletonHelper {
        private static final Localisation INSTANCE = new Localisation();
    }

    private Localisation() {
        var fluentBuilder = FluentBundle.builder(CURRENT_LOCALE, ICUFunctionFactory.INSTANCE);
        for (var ftl : readAllLocalisationFile().getOrThrow(RuntimeException::new)) {
            fluentBuilder.addResource(ftl);
        }

        bundle = fluentBuilder.build();
    }

    public static Try<List<FluentResource>> readAllLocalisationFile() {
        return Try.wrap(() -> new PathMatchingResourcePatternResolver()
                        .getResources("localisation/en/*.ftl"))
                .map(localisations -> Arrays.stream(localisations)
                        .map(ftlFile -> {
                            try {
                                return ftlFile.getContentAsString(StandardCharsets.UTF_8);
                            } catch (IOException e) {
                                throw new UncheckedIOException(e);
                            }
                        })
                        .map(FTLStream::of)
                        .map(FTLParser::parse)
                        .toList());
    }

    public static Localisation getInstance() {
        return SingletonHelper.INSTANCE;
    }
}
