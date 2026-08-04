package com.jonathan.picpay.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@Entity
@Table (name = "Transfers")
@NoArgsConstructor
@AllArgsConstructor
public class Transfer{

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    private BigDecimal amount;
    @ManyToOne
    @JoinColumn(name = "user_send_id")
    private User userSend;
    @ManyToOne
    @JoinColumn(name = "user_receive_id")
    private User userReceive;
}
