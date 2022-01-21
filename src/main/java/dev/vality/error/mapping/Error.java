package dev.vality.error.mapping;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Error {

    @Deprecated
    private String code;

    @Deprecated
    private String description;

    @Deprecated
    private String regexp;

    private String codeRegex;

    private String descriptionRegex;

    private String state;

    private String mapping;

}
