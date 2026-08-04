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
import com.jonathan.picpay.Repositories.TransferRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class TransferServices {

    private final TransferMapper transferMapper;
    private final TransferRepository transferRepository;
    private final UserServices userServices;


    @Transactional
    public TransferResponse transfer(TransferRequest transferRequest){
        User userSend = userServices.findById(transferRequest.getUserSendId());
        User userReceived = userServices.findById(transferRequest.getUserReceiveId());
        Transfer entity = transferMapper.toEntity(transferRequest);

        entity.setUserSend(userSend);
        entity.setUserReceive(userReceived);

        validateTransfer(userSend,userReceived,entity.getAmount());

        userReceived.setBalance(userReceived.getBalance().add(entity.getAmount()));
        userSend.setBalance(userSend.getBalance().subtract(entity.getAmount()));


        transferRepository.save(entity);

        return transferMapper.toResponse(entity);
    }

    private void validateTransfer(User userSend, User userReceive, BigDecimal amount){

        if (userSend.getUserType().equals(UserType.SHOPKEEPER))
            throw  new TypeNotSupportedForTransaction("UserType Not Supported to make this Transaction");

        if (userSend.getId().equals(userReceive.getId()))
            throw new SelfTransactionException("you cant not send balance from your account for yourself");

        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new NoBalanceException("Balance must be Positive");

        if (userSend.getBalance().compareTo(BigDecimal.ZERO) <= 0) {
            throw new NoBalanceException("No balance in your account");
        }

        if (amount.compareTo(userSend.getBalance())> 0) {
            throw new NoBalanceException("No suficient balance for this transaction");
        }


    }
}
