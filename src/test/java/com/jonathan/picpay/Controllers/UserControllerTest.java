package com.jonathan.picpay.Controllers;

import com.jonathan.picpay.ExceptionHandler.ExceptionsHandler;
import com.jonathan.picpay.Exceptions.UserAlreadyExistsException;
import com.jonathan.picpay.Playload.UserRequest;
import com.jonathan.picpay.Playload.UserResponse;
import com.jonathan.picpay.Services.UserServices;
import com.jonathan.picpay.Utils.UserRequestCreator;
import com.jonathan.picpay.Utils.UserResponseCreator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@Import(ExceptionsHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserServices userServices;

    @Test
    @DisplayName("should return a mapped list of userResponse")
    void shouldReturnMappedUserResponseList() throws Exception {
        UserResponse userResponse = UserResponseCreator.buildUserResponse();

        BDDMockito.when(userServices.listAll()).thenReturn(List.of(userResponse));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].firstName").value(userResponse.getFirstName()))
                .andExpect(jsonPath("$[0].email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$[0].id").value(userResponse.getId()))
                .andExpect(jsonPath("$[0].userType").value(userResponse.getUserType().toString()))
                .andExpect(jsonPath("$[0].balance").value(userResponse.getBalance()));
    }

    @Test
    @DisplayName("Should create a user and return a user response ")
    void shouldCreateUserAndReturnAUserResponse() throws Exception {
        UserRequest userRequest = UserRequestCreator.buildUserRequest();
        UserResponse userResponse = UserResponseCreator.buildUserResponse();

        BDDMockito.when(userServices.create(userRequest)).thenReturn(userResponse);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(userResponse.getId()))
                .andExpect(jsonPath("$.firstName").value(userResponse.getFirstName()))
                .andExpect(jsonPath("$.lastName").value(userResponse.getLastName()))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.userType").value(userResponse.getUserType().toString()))
                .andExpect(jsonPath("$.balance").value(userResponse.getBalance().doubleValue()));
    }
    @Test
    @DisplayName("Should return 409 Conflict when user already exists")
    void shouldReturn409WhenUserAlreadyExists() throws Exception {
        UserRequest userRequest = UserRequestCreator.buildUserRequest();

        BDDMockito.when(userServices.create(userRequest))
                .thenThrow(new UserAlreadyExistsException("user with this data already exists"));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("user with this data already exists"));
    }
}