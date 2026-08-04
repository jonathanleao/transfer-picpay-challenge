package com.jonathan.picpay.Services;

import com.jonathan.picpay.Entity.User;
import com.jonathan.picpay.Exceptions.NotFoundException;
import com.jonathan.picpay.Exceptions.UserAlreadyExistsException;
import com.jonathan.picpay.Mappers.UserMapper;
import com.jonathan.picpay.Playload.UserRequest;
import com.jonathan.picpay.Playload.UserResponse;
import com.jonathan.picpay.Repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServices {

    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserRepository userRepository;


    public User findById(Long id) {
       return userRepository.findById(id)
               .orElseThrow(() -> new NotFoundException("user Not found"));
    }

    public List<UserResponse> listAll() {
        List<User> all = userRepository.findAll();
        return all.stream().map(userMapper::toResponse)
                .toList();
    }

    @Transactional
    public UserResponse create(UserRequest userRequest) {

        if (userRepository.existsByEmail(userRequest.getEmail())){
            throw new UserAlreadyExistsException("user with this data already exists");
        }
        if (userRepository.existsByDocument(userRequest.getDocument())){
            throw new UserAlreadyExistsException("user with this data already exists");
        }

        User entity = userMapper.toEntity(userRequest);
        entity.setPassword(passwordEncoder.encode(entity.getPassword()));
        User save = userRepository.save(entity);
        return userMapper.toResponse(save);
    }
}
