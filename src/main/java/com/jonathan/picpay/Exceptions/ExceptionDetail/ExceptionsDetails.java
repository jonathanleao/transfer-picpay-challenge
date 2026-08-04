package com.jonathan.picpay.Exceptions.ExceptionDetail;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ExceptionsDetails {
    private String title;
    private Integer status;
    private LocalDateTime timestamp;
    private String message;
}
