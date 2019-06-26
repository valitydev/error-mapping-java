package com.rbkmoney.error.mapping;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;


/**
 * @author Anatoly Cherkasov
 */
@Data
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Error {

    /**
     * @deprecated
     */
    private String code;

    /**
     * @deprecated
     */
    private String description;

    /**
     * @deprecated
     */
    private String regexp;

    private String codeRegex;

    private String descriptionRegex;

    private String state;

    private String mapping;

}
