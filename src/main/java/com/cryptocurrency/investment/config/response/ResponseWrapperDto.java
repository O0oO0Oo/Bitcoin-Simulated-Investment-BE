package com.cryptocurrency.investment.config.response;

import com.cryptocurrency.investment.user.dto.request.UserEmailDto;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"code","message","result"})
public record ResponseWrapperDto(
    int code,
    String message,
    Object result
){
    static public ResponseWrapperDto of(ResponseStatus status, Object result) {
        return new ResponseWrapperDto(status.getCode(), status.getMessage(), result);
    }

    static public ResponseWrapperDto of(ResponseStatus status) {
        return new ResponseWrapperDto(status.getCode(), status.getMessage(), "None");
    }

    static public ResponseWrapperDto of(String value, ResponseStatus status) {
        return new ResponseWrapperDto(status.getCode(), status.getMessage().formatted(value), "None");
    }

    static public ResponseWrapperDto of(Integer value, ResponseStatus status) {
        return new ResponseWrapperDto(status.getCode(), status.getMessage().formatted(value), "None");
    }

    static public ResponseWrapperDto of(UserEmailDto value, ResponseStatus status) {
        return new ResponseWrapperDto(status.getCode(), status.getMessage().formatted(value.email()), "None");
    }
}