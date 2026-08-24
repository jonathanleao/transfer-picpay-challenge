package com.jonathan.picpay.Controllers;

import com.jonathan.picpay.ExceptionHandler.ExceptionsHandler;
import com.jonathan.picpay.Exceptions.NoBalanceException;
import com.jonathan.picpay.Exceptions.SelfTransactionException;
import com.jonathan.picpay.Exceptions.TypeNotSupportedForTransaction;
import com.jonathan.picpay.Playload.TransferRequest;
import com.jonathan.picpay.Playload.TransferResponse;
import com.jonathan.picpay.Services.TransferServices;
import com.jonathan.picpay.Utils.TransferRequestCreator;
import com.jonathan.picpay.Utils.TransferResponseCreator;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TransferController.class)
@Import(ExceptionsHandler.class)
@AutoConfigureMockMvc(addFilters = false)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransferServices transferServices;

    @Test
    @DisplayName("Should create a transfer and return a transfer response")
    void shouldCreateTransferAndReturnATransferResponse() throws Exception {
        TransferRequest transferRequest = TransferRequestCreator.buildTransferRequest();
        TransferResponse transferResponse = TransferResponseCreator.buildTransferResponse();

        BDDMockito.when(transferServices.transfer(transferRequest)).thenReturn(transferResponse);

        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(transferResponse.getId()))
                .andExpect(jsonPath("$.amount").value(transferResponse.getAmount().doubleValue()))
                .andExpect(jsonPath("$.userReceive.id").value(transferResponse.getUserReceive().getId()))
                .andExpect(jsonPath("$.userSend.id").value(transferResponse.getUserSend().getId()));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when NoBalanceException is thrown")
    void shouldReturn400WhenNoBalanceException() throws Exception {
        TransferRequest transferRequest = TransferRequestCreator.buildTransferRequest();

        BDDMockito.when(transferServices.transfer(transferRequest))
                .thenThrow(new NoBalanceException("No suficient balance for this transaction"));

        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("No suficient balance for this transaction"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when SelfTransactionException is thrown")
    void shouldReturn400WhenSelfTransactionException() throws Exception {
        TransferRequest transferRequest = TransferRequestCreator.buildTransferRequest();

        BDDMockito.when(transferServices.transfer(transferRequest))
                .thenThrow(new SelfTransactionException("you cant not send balance from your account for yourself"));

        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("you cant not send balance from your account for yourself"));
    }

    @Test
    @DisplayName("Should return 400 Bad Request when TypeNotSupportedForTransaction is thrown")
    void shouldReturn400WhenTypeNotSupportedForTransaction() throws Exception {
        TransferRequest transferRequest = TransferRequestCreator.buildTransferRequest();

        BDDMockito.when(transferServices.transfer(transferRequest))
                .thenThrow(new TypeNotSupportedForTransaction("UserType Not Supported to make this Transaction"));

        mockMvc.perform(post("/transfers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transferRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("UserType Not Supported to make this Transaction"));
    }

}