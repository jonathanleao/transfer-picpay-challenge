package com.jonathan.picpay.Services;

import com.jonathan.picpay.Entity.Transfer;
import com.jonathan.picpay.Entity.User;
import com.jonathan.picpay.Entity.UserType;
import com.jonathan.picpay.Exceptions.NoBalanceException;
import com.jonathan.picpay.Exceptions.SelfTransactionException;
import com.jonathan.picpay.Exceptions.TypeNotSupportedForTransaction;
import com.jonathan.picpay.Mappers.TransferMapper;
import com.jonathan.picpay.Playload.TransferRequest;
import com.jonathan.picpay.Playload.TransferResponse;
import com.jonathan.picpay.Playload.UserResponse;
import com.jonathan.picpay.Repositories.TransferRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
class TransferServicesTest {

    @InjectMocks
    private TransferServices transferServices;

    @Mock
    private TransferMapper transferMapper;

    @Mock
    private TransferRepository transferRepository;

    @Mock
    private UserServices userServices;

    @Test
    @DisplayName("Make transaction and return TransactionResponse when successful and balance was send'")
    void createAndMakeTransactionAndValidateIfTheBalanceWasSendWhenSuccessful(){
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

        UserResponse userSendResponse = UserResponse.builder()
                .id(userSend.getId())
                .email(userSend.getEmail())
                .firstName(userSend.getFirstName())
                .lastName(userSend.getLastName())
                .userType(userSend.getUserType())
                .balance(BigDecimal.valueOf(90.00)).build();

        UserResponse userReceiveResponse = UserResponse.builder()
                .id(userReceive.getId())
                .email(userReceive.getEmail())
                .firstName(userReceive.getFirstName())
                .lastName(userReceive.getLastName())
                .userType(userReceive.getUserType())
                .balance(BigDecimal.valueOf(110.00)).build();

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(BigDecimal.valueOf(10.00)).userSendId(userSend.getId()).userReceiveId(userReceive.getId())
                .build();

        Transfer transfer = Transfer.builder()
                .id(1L).amount(transferRequest.getAmount()).userSend(userSend).userReceive(userReceive).build();

        TransferResponse transferResponse = TransferResponse.builder()
                .id(transfer.getId()).amount(transfer.getAmount())
                .userReceive(userReceiveResponse).userSend(userSendResponse).build();

        BDDMockito.when(userServices.findById(userSend.getId())).thenReturn(userSend);
        BDDMockito.when(userServices.findById(userReceive.getId())).thenReturn(userReceive);
        BDDMockito.when(transferMapper.toEntity(transferRequest)).thenReturn(transfer);
        BDDMockito.when(transferRepository.save(any(Transfer.class))).thenReturn(transfer);
        BDDMockito.when(transferMapper.toResponse(transfer)).thenReturn(transferResponse);

        TransferResponse transferResponseReturned = transferServices.transfer(transferRequest);

        assertThat(transferResponseReturned).isEqualTo(transferResponse);

        assertThat(transferResponseReturned.getUserSend().getBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(90.00));
        assertThat(transferResponseReturned.getUserReceive().getBalance())
                .isEqualByComparingTo(BigDecimal.valueOf(110.00));
    }

    @Test
    @DisplayName("Should return SelfTransactionException if UserSend are equal to UserReceive")
    void returnSelfTransactionExceptionIfUserSendAreEqualToUserReceive(){
        User userSend = User.builder()
                .id(1L)
                .firstName("Jonathan").lastName("Leão")
                .userType(UserType.COMMON).email("jonathan@gmail.com")
                .document("12345678008").balance(BigDecimal.valueOf(100.00))
                .password("senhaForte123").build();

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(BigDecimal.valueOf(100.00)).userReceiveId(userSend.getId())
                .userSendId(userSend.getId()).build();

        Transfer transfer = Transfer.builder()
                .id(1L).amount(transferRequest.getAmount())
                .userSend(userSend)
                .userReceive(userSend).build();

        BDDMockito.when(userServices.findById(userSend.getId())).thenReturn(userSend);
        BDDMockito.when(transferMapper.toEntity(transferRequest)).thenReturn(transfer);

        assertThatThrownBy(()-> transferServices.transfer(transferRequest))
                .isInstanceOf(SelfTransactionException.class);

    }

    @Test
    @DisplayName("Return NoBalanceException if UserSend no have zero balance")
    void ReturnNoBalanceExceptionIfUserHaveZeroSendBalance(){
            User userSend = User.builder()
                    .id(1L)
                    .firstName("Jonathan").lastName("Leão")
                    .userType(UserType.COMMON).email("jonathan@gmail.com")
                    .document("12345678008").balance(BigDecimal.valueOf(0.00))
                    .password("senhaForte123").build();

        User userReceive = User.builder()
                .id(2L)
                .firstName("Jonas").lastName("Leão")
                .userType(UserType.COMMON).email("jonas@gmail.com")
                .document("12345679008").balance(BigDecimal.valueOf(100.00))
                .password("senhaForte123").build();

            TransferRequest transferRequest = TransferRequest.builder()
                    .amount(BigDecimal.valueOf(100.00)).userReceiveId(userReceive.getId())
                    .userSendId(userSend.getId()).build();

            Transfer transfer = Transfer.builder()
                    .id(1L).amount(transferRequest.getAmount())
                    .userSend(userSend)
                    .userReceive(userReceive).build();


        BDDMockito.when(userServices.findById(userSend.getId())).thenReturn(userSend);
        BDDMockito.when(userServices.findById(userReceive.getId())).thenReturn(userReceive);
        BDDMockito.when(transferMapper.toEntity(transferRequest)).thenReturn(transfer);

        assertThatThrownBy(()-> transferServices.transfer(transferRequest))
                .isInstanceOf(NoBalanceException.class);

        assertThat(userSend.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(0.00));

        BDDMockito.verify(transferRepository, BDDMockito.never()).save(any(Transfer.class));

    }

    @Test
    @DisplayName("Return NoBalanceException if UserSend no have suficient balance")
    void ReturnNoBalanceExceptionIfUserSendNoHaveSuficientBalance(){
        User userSend = User.builder()
                .id(1L)
                .firstName("Jonathan").lastName("Leão")
                .userType(UserType.COMMON).email("jonathan@gmail.com")
                .document("12345678008").balance(BigDecimal.valueOf(90.00))
                .password("senhaForte123").build();

        User userReceive = User.builder()
                .id(2L)
                .firstName("Jonas").lastName("Leão")
                .userType(UserType.COMMON).email("jonas@gmail.com")
                .document("12345679008").balance(BigDecimal.valueOf(100.00))
                .password("senhaForte123").build();

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(BigDecimal.valueOf(100.00)).userReceiveId(userReceive.getId())
                .userSendId(userSend.getId()).build();

        Transfer transfer = Transfer.builder()
                .id(1L).amount(transferRequest.getAmount())
                .userSend(userSend)
                .userReceive(userReceive).build();


        BDDMockito.when(userServices.findById(userSend.getId())).thenReturn(userSend);
        BDDMockito.when(userServices.findById(userReceive.getId())).thenReturn(userReceive);
        BDDMockito.when(transferMapper.toEntity(transferRequest)).thenReturn(transfer);

        assertThatThrownBy(()-> transferServices.transfer(transferRequest))
                .isInstanceOf(NoBalanceException.class);

        assertThat(userSend.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(90.00));

        BDDMockito.verify(transferRepository, BDDMockito.never()).save(any(Transfer.class));

    }
    @Test
    @DisplayName("Return TypeNotSupportedForTransaction if UserSend Are shopkeeper")
    void ReturnNoTypeNotSupportedForTransctionIfUserSendAreShopKeeper(){
        User userSend = User.builder()
                .id(1L)
                .firstName("Jonathan").lastName("Leão")
                .userType(UserType.SHOPKEEPER).email("jonathan@gmail.com")
                .document("12345678008").balance(BigDecimal.valueOf(100.00))
                .password("senhaForte123").build();

        User userReceive = User.builder()
                .id(2L)
                .firstName("Jonas").lastName("Leão")
                .userType(UserType.COMMON).email("jonas@gmail.com")
                .document("12345679008").balance(BigDecimal.valueOf(100.00))
                .password("senhaForte123").build();

        TransferRequest transferRequest = TransferRequest.builder()
                .amount(BigDecimal.valueOf(100.00)).userReceiveId(userReceive.getId())
                .userSendId(userSend.getId()).build();

        Transfer transfer = Transfer.builder()
                .id(1L).amount(transferRequest.getAmount())
                .userSend(userSend)
                .userReceive(userReceive).build();


        BDDMockito.when(userServices.findById(userSend.getId())).thenReturn(userSend);
        BDDMockito.when(userServices.findById(userReceive.getId())).thenReturn(userReceive);
        BDDMockito.when(transferMapper.toEntity(transferRequest)).thenReturn(transfer);

        assertThatThrownBy(()-> transferServices.transfer(transferRequest))
                .isInstanceOf(TypeNotSupportedForTransaction.class);

        BDDMockito.verify(transferRepository, BDDMockito.never()).save(any(Transfer.class));

    }
}
