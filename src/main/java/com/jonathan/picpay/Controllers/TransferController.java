package com.jonathan.picpay.Controllers;

import com.jonathan.picpay.Playload.TransferRequest;
import com.jonathan.picpay.Playload.TransferResponse;
import com.jonathan.picpay.Services.TransferServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/transfers")
public class TransferController {

    private final TransferServices transferServices;

    @PostMapping
    public ResponseEntity<TransferResponse> createTransfer (@Valid @RequestBody TransferRequest transferRequest){
        return  new ResponseEntity<>(transferServices.transfer(transferRequest), HttpStatus.CREATED);
    }
}
