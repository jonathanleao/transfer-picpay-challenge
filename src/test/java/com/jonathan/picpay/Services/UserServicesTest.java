package com.jonathan.picpay.Services;

import com.jonathan.picpay.Entity.User;
import com.jonathan.picpay.Entity.UserType;
import com.jonathan.picpay.Exceptions.UserAlreadyExistsException;
import com.jonathan.picpay.Mappers.UserMapper;
import com.jonathan.picpay.Playload.UserRequest;
import com.jonathan.picpay.Playload.UserResponse;
import com.jonathan.picpay.Repositories.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;


@ExtendWith(MockitoExtension.class)
class UserServicesTest {

    @InjectMocks
    private UserServices userServices;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Test
    @DisplayName("Should return a list of UserReponse when Successful")
    void returnListOfUsersWhenListAllisSuccessful(){
        User user = User.builder()
                .id(1L)
                .firstName("Jonathan")
                .lastName("Leão")
                .userType(UserType.COMMON)
                .email("jonathan@gmail.com")
                .document("12345678")
                .balance(BigDecimal.valueOf(100.00))
                .password("senhaForte123")
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userType(user.getUserType())
                .balance(user.getBalance()).build();

        BDDMockito.when(userRepository.findAll()).thenReturn(List.of(user));
        BDDMockito.when(userMapper.toResponse(user)).thenReturn(userResponse);


        List<UserResponse> listOfUsers = userServices.listAll();

        assertThat(listOfUsers)
                .isNotEmpty()
                .hasSize(1)
                .contains(userResponse);
    }

    @Test
    @DisplayName("Should return a empty list when any user are found")
    void returnEmptyListOfUsersWhenListAllNotFoundAnyUsers(){

        BDDMockito.when(userRepository.findAll()).thenReturn(List.of());

        List<UserResponse> listOfUsers = userServices.listAll();
        assertThat(listOfUsers).isEmpty();
    }

    @Test
    @DisplayName("Should return userResponse when a user is created in database")
    void returnUserResponseWhenUserIsCreated(){
        UserRequest userRequest = UserRequest.builder()
                .firstName("Jonathan")
                .lastName("Leão")
                .document("12345678")
                .email("jonathan@gmail.com")
                .userType(UserType.COMMON)
                .balance(BigDecimal.valueOf(100.00))
                .password("senhaForte123")
                .build();

        User user = User.builder()
                .id(1L)
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .userType(userRequest.getUserType())
                .email(userRequest.getEmail())
                .document(userRequest.getDocument())
                .balance(userRequest.getBalance())
                .password(userRequest.getPassword())
                .build();

        UserResponse userResponse = UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userType(user.getUserType())
                .balance(user.getBalance()).build();

        BDDMockito.when(userMapper.toEntity(userRequest)).thenReturn(user);
        BDDMockito.when(passwordEncoder.encode(userRequest.getPassword())).thenReturn("encodedPassword");
        BDDMockito.when(userRepository.save(user)).thenReturn(user);
        BDDMockito.when(userMapper.toResponse(user)).thenReturn(userResponse);

        UserResponse userResponseReturned = userServices.create(userRequest);

        assertThat(userResponseReturned).isEqualTo(userResponse);
        BDDMockito.then(userRepository).should().save(user);

    }
    @Test
    @DisplayName("Should return a UserAlreadyExistsException, and not save in the database, when user trying " +
            "to enter with a email then other user have")
    void returnUserAlreadyExistsExceptionWhenUserWithThisEmailAlreadyExists() {

        UserRequest userRequest= UserRequest.builder()
                .firstName("Jonathan")
                .lastName("Leão")
                .document("12345678")
                .email("jonathan@gmail.com")
                .userType(UserType.COMMON)
                .balance(BigDecimal.valueOf(100.00))
                .password("senhaForte123")
                .build();

        BDDMockito.when(userRepository.existsByEmail(userRequest.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> userServices.create(userRequest))
                .isInstanceOf(UserAlreadyExistsException.class);

        BDDMockito.then(userRepository).should(BDDMockito.never()).save(any(User.class));
    }

    @Test
    @DisplayName("Should return a UserAlreadyExistsException, and not save in the database, when user trying " +
            "to enter with a Document then other user have")
    void returnUserAlreadyExistsExceptionWhenUserWithThisDocumentsAlreadyExists() {

        UserRequest userRequest= UserRequest.builder()
                .firstName("Jonathan")
                .lastName("Leão")
                .document("12345678")
                .email("jonathan@gmail.com")
                .userType(UserType.COMMON)
                .balance(BigDecimal.valueOf(100.00))
                .password("senhaForte123")
                .build();

        BDDMockito.when(userRepository.existsByDocument(userRequest.getDocument())).thenReturn(true);

        assertThatThrownBy(() -> userServices.create(userRequest))
                .isInstanceOf(UserAlreadyExistsException.class);

        BDDMockito.then(userRepository).should(BDDMockito.never()).save(any(User.class));
    }
}