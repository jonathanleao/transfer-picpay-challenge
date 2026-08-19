package com.jonathan.picpay.Controllers;

import com.jonathan.picpay.Entity.User;
import com.jonathan.picpay.Entity.UserType;
import com.jonathan.picpay.Playload.UserRequest;
import com.jonathan.picpay.Playload.UserResponse;
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
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    @InjectMocks
    private UserController userController;

    @Mock
    private  UserServices userServices;

    @Test
    @DisplayName("list all users and return a list of userResponse and http status 200 OK")
    void returnListOfUSerResponseAndHttpsStatus200() {
        UserRequest userRequest = UserRequest.builder()
                .firstName("Jonathan").lastName("Leão")
                .document("12345678").email("jonathan@gmail.com").userType(UserType.COMMON).balance(BigDecimal.valueOf(100.00))
                .password("senhaForte123")
                .build();

        User user = User.builder()
                .id(1L).firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName()).userType(userRequest.getUserType())
                .email(userRequest.getEmail()).document(userRequest.getDocument())
                .balance(userRequest.getBalance()).password(userRequest.getPassword())
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId()).email(user.getEmail()).firstName(user.getFirstName()).lastName(user.getLastName()).userType(user.getUserType())
                .balance(user.getBalance()).build();

        BDDMockito.when(userServices.listAll()).thenReturn(List.of(userResponse));

        ResponseEntity<List<UserResponse>> response = userController.listAll();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);

        List<UserResponse> body = response.getBody();

        assertThat(body).isNotNull();
        assertThat(body).hasSize(1);

        UserResponse returned = body.getFirst();
        assertThat(returned.getId()).isEqualTo(userResponse.getId());
        assertThat(returned.getEmail()).isEqualTo(userResponse.getEmail());
        assertThat(returned.getFirstName()).isEqualTo(userResponse.getFirstName());
        assertThat(returned.getLastName()).isEqualTo(userResponse.getLastName());
        assertThat(returned.getUserType()).isEqualTo(userResponse.getUserType());
        assertThat(returned.getBalance()).isEqualByComparingTo(userResponse.getBalance());


    }

    @Test
    @DisplayName("create user and return userResponse and http status 201 CREATED")
    void createUserAndReturnUserResponseAndHttpStatus201() {
        UserRequest userRequest = UserRequest.builder()
                .firstName("Jonathan").lastName("Leão")
                .document("12345678").email("jonathan@gmail.com").userType(UserType.COMMON).balance(BigDecimal.valueOf(100.00))
                .password("senhaForte123")
                .build();

        User user = User.builder()
                .id(1L).firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName()).userType(userRequest.getUserType())
                .email(userRequest.getEmail()).document(userRequest.getDocument())
                .balance(userRequest.getBalance()).password(userRequest.getPassword())
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId()).email(user.getEmail()).firstName(user.getFirstName()).lastName(user.getLastName()).userType(user.getUserType())
                .balance(user.getBalance()).build();

        BDDMockito.when(userServices.create(userRequest)).thenReturn(userResponse);

        ResponseEntity<UserResponse> response = userController.create(userRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        UserResponse body = response.getBody();
        assertThat(body).isNotNull();
        assertThat(body.getId()).isEqualTo(userResponse.getId());
        assertThat(body.getEmail()).isEqualTo(userResponse.getEmail());
        assertThat(body.getFirstName()).isEqualTo(userResponse.getFirstName());
        assertThat(body.getLastName()).isEqualTo(userResponse.getLastName());
        assertThat(body.getUserType()).isEqualTo(userResponse.getUserType());
        assertThat(body.getBalance()).isEqualByComparingTo(userResponse.getBalance());

    }
}