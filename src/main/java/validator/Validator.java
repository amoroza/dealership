package validator;

import either.Result;

public interface Validator<T> {
    Result<T> validate(T record);
}