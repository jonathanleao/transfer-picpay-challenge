package com.jonathan.picpay.Playload;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class TransferResponse {
    long id;
    private BigDecimal amount;
    private UserResponse userReceive;
    private UserResponse userSend;
}
