package com.jonathan.picpay.Playload;

import com.jonathan.picpay.Entity.UserType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class UserResponse {


    private Long id;
    private String firstName;
    private String lastName;
    private String email;
    private UserType userType;
    private BigDecimal balance;
}
