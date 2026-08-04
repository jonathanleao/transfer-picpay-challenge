package com.jonathan.picpay.Controllers;

import com.jonathan.picpay.Playload.UserRequest;
import com.jonathan.picpay.Playload.UserResponse;
import com.jonathan.picpay.Services.UserServices;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserServices userServices;

    @GetMapping
    public ResponseEntity<List<UserResponse>> listAll(){
        return  new ResponseEntity<>(userServices.listAll(),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<UserResponse> create(@Valid @RequestBody UserRequest userRequest){
        return new ResponseEntity<>(userServices.create(userRequest), HttpStatus.CREATED);
    }
}


