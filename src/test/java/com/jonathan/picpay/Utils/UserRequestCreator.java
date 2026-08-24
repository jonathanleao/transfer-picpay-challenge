package com.jonathan.picpay.Utils;

import com.jonathan.picpay.Entity.UserType;
import com.jonathan.picpay.Playload.UserRequest;

import java.math.BigDecimal;

public class UserRequestCreator {
    public static UserRequest buildUserRequest() {
        return UserRequest.builder()
                .firstName("Jonas")
                .lastName("Leão")
                .document("12345678900")
                .email("jonas@gmail.com")
                .userType(UserType.COMMON)
                .balance(BigDecimal.valueOf(100))
                .password("senha123")
                .build();
    }
}
