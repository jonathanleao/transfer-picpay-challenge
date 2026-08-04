package com.jonathan.picpay.Playload;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class TransferRequest {

    @NotNull
    private BigDecimal amount;
    @NotNull
    private Long userReceiveId;
    @NotNull
    private Long userSendId;
}
