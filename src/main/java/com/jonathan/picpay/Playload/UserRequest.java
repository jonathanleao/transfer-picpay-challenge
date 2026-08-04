package com.jonathan.picpay.Playload;

import com.jonathan.picpay.Entity.UserType;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class UserRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    private String document;
    @Email
    private String email;
    @NotNull
    private UserType userType;
    @NotNull
    private BigDecimal balance;
    @NotBlank
    private String password;
}
