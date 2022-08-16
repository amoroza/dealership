package either;

import error.Errors;

import java.util.List;

public class Result<T> {

    private Errors errors = new Errors();
    private T record;

    private Result(final Errors.FieldError error) {
        this.errors.addError(error);
    }
    private Result() {}

    public Result(final T record) {
        this.record = record;
    }

    private Result(final Errors errors) {
        this.errors = errors;
    }

    public static Result success() {
        return new Result<>();
    }

    public static Result error(final Errors.FieldError err) {
        return new Result<>(err);
    }

    public static Result errors(final Errors errors) {
        return new Result(errors);
    }

    /**
     * takes many {@link either.Result}
     */
    public static Result withErrorsFromResults(final List<Result> results) {
        var errors = new Errors();
        results.forEach(result ->
                result.getErrors().allErrors().forEach(errors::addError));

        return new Result(errors);
    }

    public Boolean isSuccess() {
        return ! this.errors.hasErrors();
    }

    public Errors getErrors() {
        return this.errors;
    }

    public T getRecord() { return this.record; }
}
