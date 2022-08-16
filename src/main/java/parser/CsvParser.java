package parser;

import either.Result;

public interface CsvParser<TYPE> {
    Boolean isHeader(String line);
    Result<TYPE> parse(String line);
}