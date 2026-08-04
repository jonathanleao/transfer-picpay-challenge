package com.jonathan.picpay.Repositories;

import com.jonathan.picpay.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByDocument (String document);
    boolean existsByEmail (String email);
    boolean existsByPassword (String password);
}
