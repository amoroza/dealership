package ingester;

import either.Result;
import parser.CsvParser;
import validator.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class CsvFileIngester<T> {

    private final CsvParser<T> parser;
    private final  Validator<T> validator;

    public CsvFileIngester(final CsvParser<T> parser,
                           final Validator<T> validator) {
        this.parser = parser;
        this.validator = validator;
    }

    public List<Result<T>> ingest(final String path) throws IOException {

        final var items = new ArrayList<Result<T>>();

        try (Stream<String> s = Files.lines(Paths.get(path))) {
            s.filter(line -> !parser.isHeader(line))
                    .forEach(line -> {
                        items.add(parseAndValidate(line));
                    });

            return items;
        }
    }

    private Result<T> parseAndValidate (final String line) {

        final var parsed = parser.parse(line);

        if ( ! parsed.isSuccess() ) {
            return parsed;
        }

        final Result<T> validated = validator.validate(parsed.getRecord());

        return validated.isSuccess()?
                parsed
                : validated;
    }
}
