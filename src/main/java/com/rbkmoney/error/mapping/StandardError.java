package com.rbkmoney.error.mapping;

import java.util.Arrays;


/**
 * @author Anatoly Cherkasov
 */
public enum StandardError {

    RESULT_UNAVAILABLE("ResourceUnavailable"),
    RESULT_UNDEFINED("ResultUnknown"),
    RESULT_UNEXPECTED("ResultUnexpected"),

    PRE_AUTHORIZATION_FAILED("preauthorization_failed"),
    REJECTED_BY_INSPECTOR("rejected_by_inspector"),

    AUTH_FAILED_UNKNOWN("authorization_failed:unknown"),
    AUTH_FAILED_MERCHANT_BLOCKED("authorization_failed:merchant_blocked"),
    AUTH_FAILED_OPERATION_BLOCKED("authorization_failed:operation_blocked"),
    AUTH_FAILED_ACCOUNT_NOT_FOUND("authorization_failed:account_not_found"),
    AUTH_FAILED_ACCOUNT_BLOCKED("authorization_failed:account_blocked"),
    AUTH_FAILED_ACCOUNT_STOLEN("authorization_failed:account_stolen"),
    AUTH_FAILED_INSUFFICIENT_FUNDS("authorization_failed:insufficient_funds"),

    AUTH_FAILED_SECURITY_POLICY_VIOLATED("authorization_failed:security_policy_violated"),
    AUTH_FAILED_TEMPORARILY_UNAVAILABLE("authorization_failed:temporarily_unavailable"),
    AUTH_FAILED_REJECTED_BY_ISSUER("authorization_failed:rejected_by_issuer"),

    AUTH_FAILED_ACCOUNT_EXCEEDED_AMOUNT("authorization_failed:account_limit_exceeded:amount"),
    AUTH_FAILED_ACCOUNT_EXCEEDED_NUMBER("authorization_failed:account_limit_exceeded:number"),
    AUTH_FAILED_ACCOUNT_EXCEEDED_UNKNOWN("authorization_failed:account_limit_exceeded:unknown"),


    AUTH_FAILED_PROVIDER_EXCEEDED_AMOUNT("authorization_failed:provider_limit_exceeded:amount"),
    AUTH_FAILED_PROVIDER_EXCEEDED_NUMBER("authorization_failed:provider_limit_exceeded:number"),
    AUTH_FAILED_PROVIDER_EXCEEDED_UNKNOWN("authorization_failed:provider_limit_exceeded:unknown"),

    AUTH_FAILED_PAYMENT_TOOL_UNKNOWN("authorization_failed:payment_tool_rejected:unknown"),
    AUTH_FAILED_PAYMENT_TOOL_BANK_CARD_CARD_EXPIRED("authorization_failed:payment_tool_rejected:bank_card_rejected:card_expired"),
    AUTH_FAILED_PAYMENT_TOOL_BANK_CARD_CARD_NUMBER_INVALID("authorization_failed:payment_tool_rejected:bank_card_rejected:card_number_invalid"),
    AUTH_FAILED_PAYMENT_TOOL_BANK_CARD_CARD_HOLDER_INVALID("authorization_failed:payment_tool_rejected:bank_card_rejected:card_holder_invalid"),
    AUTH_FAILED_PAYMENT_TOOL_BANK_CARD_CVV_INVALID("authorization_failed:payment_tool_rejected:bank_card_rejected:cvv_invalid"),
    //AUTH_FAILED_PAYMENT_TOOL_BANK_CARD_CARD_UNSUPPORTED("authorization_failed:payment_tool_rejected:bank_card_rejected:card_unsupported"),
    AUTH_FAILED_PAYMENT_TOOL_BANK_CARD_ISSUER_NOT_FOUND("authorization_failed:payment_tool_rejected:bank_card_rejected:issuer_not_found");


    private final String error;

    StandardError(String action) {
        this.error = action;
    }

    public String getError() {
        return error;
    }

    public static StandardError findByValue(String value) {
        return Arrays.stream(StandardError.values()).filter((error) -> error.getError().equals(value))
                .findFirst()
                .orElseThrow(()->new IllegalStateException(String.format("Unsupported error '%s' does not match standard", value)));
    }

}
