package com.juanmatiaslopez.transaction_service.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(int status, String message, T data) {

}
