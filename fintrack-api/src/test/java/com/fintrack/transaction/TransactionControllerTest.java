package com.fintrack.transaction;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class TransactionControllerTest {

    private MockMvc mvc;
    private ObjectMapper mapper = new ObjectMapper();

    @Test
    void post_creates_for_principal() throws Exception {
        CreateTransactionDto dto = new CreateTransactionDto(new BigDecimal("12.34"), "coffee");
        Transaction saved = new Transaction("user-x", new BigDecimal("12.34"), "coffee", java.time.LocalDateTime.now());

        TransactionService service = new TransactionService(null) {
            @Override
            public Transaction createTransaction(CreateTransactionDto d, String userId) {
                return saved;
            }
        };

        TransactionController controller = new TransactionController(service);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter())
                .build();

        Principal p = () -> "user-x";

        mvc.perform(post("/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(dto))
                .principal(p))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value("user-x"));
    }

    @Test
    void get_returns_user_transactions() throws Exception {
        Transaction t = new Transaction("user-a", new BigDecimal("5.00"), "note", LocalDateTime.now());

        TransactionService service = new TransactionService(null) {
            @Override
            public java.util.List<Transaction> getTransactionsByUser(String requestingUserId, String targetUserId) {
                return List.of(t);
            }
        };

        TransactionController controller = new TransactionController(service);
        mvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new org.springframework.http.converter.json.MappingJackson2HttpMessageConverter())
                .build();

        Principal p = () -> "user-a";

        mvc.perform(get("/transactions/user-a").principal(p))
            .andExpect(status().isOk())
            .andExpect(jsonPath("[0].userId").value("user-a"));
    }
    
}
