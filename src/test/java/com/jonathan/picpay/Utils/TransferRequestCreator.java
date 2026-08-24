package com.jonathan.picpay.Utils;

import com.jonathan.picpay.Playload.TransferRequest;

import java.math.BigDecimal;

public class TransferRequestCreator {
    public static TransferRequest buildTransferRequest() {
        return TransferRequest.builder()
                .amount(BigDecimal.valueOf(100))
                .userReceiveId(2L)
                .userSendId(1L)
                .build();
    }
}
