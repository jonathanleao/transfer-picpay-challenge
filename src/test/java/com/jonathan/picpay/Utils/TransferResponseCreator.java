package com.jonathan.picpay.Utils;

import com.jonathan.picpay.Playload.TransferRequest;
import com.jonathan.picpay.Playload.TransferResponse;

import java.math.BigDecimal;

public class TransferResponseCreator {
    public static TransferResponse buildTransferResponse() {
        return TransferResponse.builder()
                .id(1L)
                .amount(BigDecimal.valueOf(100))
                .userReceive(UserResponseCreator.buildUserResponse())
                .userSend(UserResponseCreator.buildUserResponse())
                .build();
    }
}
