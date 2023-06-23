package ru.itgroup.intouch.client.exceptionHandling;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Date;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ErrorResponse {
    private String timestamp;
    private int code;
    private String status;
    @JsonProperty(value = "error_description")
    private String message;
    private String details;

    public ErrorResponse(int code, String status, String message, String details) {
        timestamp = new Date().toString();
        this.code = code;
        this.status = status;
        this.message = message;
        this.details = details;
    }

    public ErrorResponse() {

    }

    @Override
    public String toString() {
        return "ErrorResponse{" +
                "timestamp=" + timestamp +
                ", code=" + code +
                ", status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", details='" + details + '\'' +
                '}';
    }
}
