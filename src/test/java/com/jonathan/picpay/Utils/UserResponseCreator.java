package com.jonathan.picpay.Utils;

import com.jonathan.picpay.Playload.UserResponse;

public class UserResponseCreator {

    public static UserResponse buildUserResponse() {
        return UserResponse.builder()
                .id(1L)
                .firstName(UserRequestCreator.buildUserRequest().getFirstName())
                .lastName(UserRequestCreator.buildUserRequest().getLastName())
                .email(UserRequestCreator.buildUserRequest().getEmail())
                .userType(UserRequestCreator.buildUserRequest().getUserType())
                .balance(UserRequestCreator.buildUserRequest().getBalance())
                .build();
    }
}
