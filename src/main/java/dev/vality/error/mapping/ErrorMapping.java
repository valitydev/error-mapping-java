package dev.vality.error.mapping;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.vality.damsel.domain.Failure;
import dev.vality.geck.serializer.kit.tbase.TErrorUtil;
import dev.vality.woody.api.flow.error.WUnavailableResultException;
import dev.vality.woody.api.flow.error.WUndefinedResultException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static dev.vality.geck.serializer.kit.tbase.TErrorUtil.toGeneral;

public class ErrorMapping {

    private static final String DEFAULT_PATTERN_REASON = "'%s' - '%s'";

    /**
     * Pattern for reason failure
     */
    private final String patternReason;

    private final List<Error> errors;

    public ErrorMapping(InputStream inputStream) {
        this(inputStream, DEFAULT_PATTERN_REASON);
    }

    public ErrorMapping(InputStream inputStream, String patternReason) {
        this(inputStream, patternReason, new ObjectMapper());
    }

    public ErrorMapping(InputStream inputStream, String patternReason, ObjectMapper objectMapper) {
        this(patternReason, initErrorList(inputStream, objectMapper));
    }

    public ErrorMapping(String patternReason, List<Error> errors) {
        this.patternReason = patternReason;
        this.errors = errors;
    }


    public static List<Error> initErrorList(InputStream inputStream, ObjectMapper objectMapper) {
        try {
            return objectMapper.readValue(
                    inputStream,
                    new TypeReference<List<Error>>() {
                    }
            );
        } catch (JsonParseException e) {
            throw new ErrorMappingException("Json can't parse data from file", e);
        } catch (JsonMappingException e) {
            throw new ErrorMappingException("Json can't mapping data from file", e);
        } catch (IOException e) {
            throw new ErrorMappingException("Failed to initErrorList", e);
        }
    }

    /**
     * Get failure by code and description
     * if code is null check only description and if description is null check only code
     *
     * @param code        String
     * @param description String
     * @return Failure
     * @throws IllegalArgumentException if code and description null together.
     */
    @Deprecated
    public Failure getFailureByCodeAndDescription(String code, String description) {
        Error error = findMatchWithPattern(errors, code, description);

        checkWoodyError(error, code, description);

        Failure failure = toGeneral(error.getMapping());
        failure.setReason(prepareReason(code, description));
        return failure;
    }

    /**
     * Find by regexp
     *
     * @param filter String
     * @return Failure
     */
    @Deprecated
    public Failure getFailureByRegexp(String filter) {
        Error error = findMatchWithPattern(errors, filter);

        checkWoodyError(error, null,null);

        Failure failure = toGeneral(error.getMapping());
        failure.setReason(prepareReason(error.getCode(), error.getDescription()));
        return failure;
    }

    public Failure mapFailure(String code) {
        return mapFailure(code, null, null);
    }

    public Failure mapFailure(String code, String description) {
        return mapFailure(code, description, null);
    }

    public Failure mapFailure(String code, String description, String state) {
        Error error = findError(code, description, state);

        checkWoodyError(error, code, description);

        Failure failure = TErrorUtil.toGeneral(error.getMapping());
        failure.setReason(prepareReason(code, description));
        return failure;
    }

    public boolean errorMappingIsExistAndNotWoody(String code, String description) {
        Optional<Error> error = findErrorInConfig(code, description, null);
        return error.isPresent()
                && !StandardError.RESULT_UNDEFINED.getError().equals(error.get().getMapping())
                && !StandardError.RESULT_UNEXPECTED.getError().equals(error.get().getMapping())
                && !StandardError.RESULT_UNAVAILABLE.getError().equals(error.get().getMapping());
    }

    private Error findError(String code, String description, String state) {
        return findErrorInConfig(code, description, state)
                .orElseThrow(() -> new ErrorMappingException(
                        String.format("Error not found. Code %s, description %s, state %s", code, description, state))
                );
    }

    private Optional<Error> findErrorInConfig(String code, String description, String state) {
        Objects.requireNonNull(code, "Code must be set");
        return errors.stream()
                .filter(e -> matchError(e, code, description, state))
                .findFirst();
    }

    private boolean matchNullableStrings(String str, String regex) {
        if (str == null || regex == null) {
            return true;
        }
        return str.matches(regex);
    }

    private boolean equalsNullableStrings(String str1, String str2) {
        if (str1 == null || str2 == null) {
            return true;
        }
        return str1.equals(str2);
    }

    private boolean matchError(Error error, String code, String description, String state) {
        return code.matches(error.getCodeRegex())
                && matchNullableStrings(description, error.getDescriptionRegex())
                && equalsNullableStrings(state, error.getState());
    }

    /**
     * @deprecated Validate mapping formate
     */
    public void validateMappingFormat() {
        errors.forEach(error -> StandardError.findByValue(error.getMapping()));
    }

    public void validateMapping() {
        errors.forEach(error -> {
            Objects.requireNonNull(error.getCodeRegex(), "Field 'codeRegex' must be set");
            Objects.requireNonNull(error.getMapping(), "Field 'mapping' must be set");
            StandardError.findByValue(error.getMapping());
        });
    }

    /**
     * Find match code or description by pattern
     *
     * @param errors      List of Errors
     * @param code        String
     * @param description String
     * @return com.rbkmoney.proxy.mocketbank.utils.model.Error
     */
    private Error findMatchWithPattern(List<Error> errors, String code, String description) {
        return errors.stream()
                .filter(error -> checkError(code, description, error))
                .findFirst()
                .orElseThrow(() -> new ErrorMappingException(
                        String.format("Unexpected error. code %s, description %s", code, description))
                );
    }

    private Error findMatchWithPattern(List<Error> errors, String filter) {
        Objects.requireNonNull(filter);

        return errors.stream()
                .filter(error -> (filter.matches(error.getRegexp())))
                .findFirst()
                .orElseThrow(() -> new ErrorMappingException(
                        String.format("Unexpected error. regexp %s", filter))
                );
    }

    private boolean checkError(String code, String description, Error error) {
        if (code != null && description != null) {
            return code.matches(error.getRegexp()) && description.matches(error.getRegexp());
        } else if (code != null) {
            return code.matches(error.getRegexp());
        } else if (description != null) {
            return description.matches(error.getRegexp());
        }
        throw new IllegalArgumentException();
    }

    /**
     * Prepare reason for {@link Failure}
     *
     * @param code        String
     * @param description String
     * @return String
     */
    private String prepareReason(String code, String description) {
        return String.format(this.patternReason, code, description);
    }

    private void checkWoodyError(Error error, String code, String description) {

        if (error == null) {
            throw new IllegalArgumentException("Error not found");
        }

        if (StandardError.RESULT_UNDEFINED.getError().equals(error.getMapping())) {
            throw new WUndefinedResultException(
                    String.format("%s, code = %s, description = %s", error, code, description)
            );
        }

        if (StandardError.RESULT_UNEXPECTED.getError().equals(error.getMapping())) {
            throw new RuntimeException(
                    String.format("Unexpected error %s, code = %s, description = %s", error, code, description)
            );
        }

        if (StandardError.RESULT_UNAVAILABLE.getError().equals(error.getMapping())) {
            throw new WUnavailableResultException(
                    String.format("%s, code = %s, description = %s", error, code, description)
            );
        }
    }
}
