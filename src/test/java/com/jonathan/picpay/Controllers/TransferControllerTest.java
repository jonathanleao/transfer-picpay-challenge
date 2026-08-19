package com.jonathan.picpay.Controllers;

import com.jonathan.picpay.Entity.Transfer;
import com.jonathan.picpay.Entity.User;
import com.jonathan.picpay.Entity.UserType;
import com.jonathan.picpay.Playload.TransferRequest;
import com.jonathan.picpay.Playload.TransferResponse;
import com.jonathan.picpay.Playload.UserRequest;
import com.jonathan.picpay.Playload.UserResponse;
import com.jonathan.picpay.Services.TransferServices;
import com.jonathan.picpay.Services.UserServices;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class TransferControllerTest {

    @InjectMocks
    private TransferController transferController;

    @Mock
    private TransferServices transferServices;

    @Test
    @DisplayName("Create transfer when successful and return userResponse and status 201 CREATED")
    void createTransferAndReturnHttpsStatus201CREATED() {
        User userSend = User.builder()
                .id(1L)
                .firstName("Jonathan").lastName("Leão")
                .userType(UserType.COMMON).email("jonathan@gmail.com")
                .document("12345678008").balance(BigDecimal.valueOf(100.00))
                .password("senhaForte123").build();

        User userReceive = User.builder()
                .id(2L)
                .firstName("Monica")
                .lastName("Hellen").userType(UserType.COMMON)
                .email("Monica@gmail.com").document("23243255500")
                .balance(BigDecimal.valueOf(100.00)).password("senhaForte123")
                .build();

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(BigDecimal.valueOf(10.00)).userSendId(userSend.getId()).userReceiveId(userReceive.getId())
                .build();

        Transfer transfer = Transfer.builder()
                .id(1L).amount(transferRequest.getAmount()).userSend(userSend).userReceive(userReceive).build();

        UserResponse userSendResponse = UserResponse.builder()
                .id(userSend.getId()).email(userSend.getEmail())
                .firstName(userSend.getFirstName()).lastName(userSend.getLastName())
                .userType(userSend.getUserType()).balance(BigDecimal.valueOf(90.00)).build();

        UserResponse userReceiveResponse = UserResponse.builder()
                .id(userReceive.getId())
                .email(userReceive.getEmail()).firstName(userReceive.getFirstName()).lastName(userReceive.getLastName())
                .userType(userReceive.getUserType()).balance(BigDecimal.valueOf(110.00)).build();

        TransferResponse transferResponse = TransferResponse.builder()
                .id(transfer.getId()).amount(transfer.getAmount())
                .userReceive(userReceiveResponse).userSend(userSendResponse).build();

        BDDMockito.when(transferServices.transfer(transferRequest)).thenReturn(transferResponse);

        ResponseEntity<TransferResponse> transferResponseReturned = transferController.createTransfer(transferRequest);

        TransferResponse body = transferResponseReturned.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(transferResponse.getId());
        assertThat(body.getAmount()).isEqualByComparingTo(transferResponse.getAmount());

        assertThat(body.getUserSend().getId()).isEqualTo(userSendResponse.getId());
        assertThat(body.getUserSend().getEmail()).isEqualTo(userSendResponse.getEmail());
        assertThat(body.getUserSend().getBalance()).isEqualByComparingTo(userSendResponse.getBalance());

        assertThat(body.getUserReceive().getId()).isEqualTo(userReceiveResponse.getId());
        assertThat(body.getUserReceive().getEmail()).isEqualTo(userReceiveResponse.getEmail());
        assertThat(body.getUserReceive().getBalance()).isEqualByComparingTo(userReceiveResponse.getBalance());

    }
}