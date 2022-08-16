package error;

import java.util.ArrayList;
import java.util.List;

public final class Errors {

    private final List<FieldError> errors = new ArrayList<>();

    public static class FieldError {
        private static final String SEE_DESCRIPTION = "see description";
        private final String type;
        private final String attribute;
        private final String description;

        public FieldError(final String type,
                          final String attribute,
                          final String description) {
            this.type = type;
            this.attribute = attribute;
            this.description = description;
        }

        public FieldError(final String type,
                          final String description) {
            this.type = type;
            this.attribute = SEE_DESCRIPTION;
            this.description = description;
        }

        public String getType() {
            return type;
        }

        public String getAttribute() {
            return attribute;
        }

        public String getDescription() {
            return description;
        }

        @Override
        public String toString() {
            return "FieldError{" +
                    "type='" + type + '\'' +
                    ", attribute='" + attribute + '\'' +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

    public void addError(final FieldError error) {
        errors.add(error);
    }

    public boolean hasErrors () {
        return ! errors.isEmpty();
    }

    public List<FieldError> allErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return errors.toString();
    }
}
