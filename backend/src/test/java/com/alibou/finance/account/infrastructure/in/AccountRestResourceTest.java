package com.alibou.finance.account.infrastructure.in;

import com.alibou.finance.account.application.port.usecase.AccountConsultationUseCase;
import com.alibou.finance.account.application.port.usecase.AccountLifeCycleUseCase;
import com.alibou.finance.account.domain.agregate.Account;
import com.alibou.finance.account.domain.agregate.AccountStatusEnum;
import com.alibou.finance.account.domain.vo.AccountStatus;
import com.alibou.finance.account.infrastructure.adapter.in.controller.AccountRestResource;
import com.alibou.finance.account.infrastructure.adapter.in.dto.CreateAccountRequest;
import com.alibou.finance.account.infrastructure.handlers.AccountExceptionHandler;
import com.alibou.finance.shared.error.domain.OperationNotPermittedException;
import com.alibou.finance.shared.error.infrastructure.GlobalExceptionHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
public class AccountRestResourceTest {

    private MockMvc mockMvc;

    @Mock
    private AccountLifeCycleUseCase accountLifeCycleService;
    @Mock
    private AccountConsultationUseCase accountConsultationUseCase;

    private Authentication mockAuthentication;

    @InjectMocks
    private AccountRestResource accountRestResource;

    private final ObjectMapper objectMapper = new ObjectMapper();

    CreateAccountRequest createAccountRequest;

    @BeforeEach
    void setUp(){
        accountRestResource = new AccountRestResource(accountLifeCycleService, accountConsultationUseCase);
        mockAuthentication = mock(Authentication.class);
        mockMvc = MockMvcBuilders
                .standaloneSetup(accountRestResource)
                .setControllerAdvice(new GlobalExceptionHandler(), new AccountExceptionHandler())
                .build();
        createAccountRequest = new CreateAccountRequest("10", "MGA", UUID.randomUUID());
    }

    @Test
    void createAccount_shouldCreateSuccessfully() throws Exception {
        var account = Account.builder()
                .accountStatus(new AccountStatus(AccountStatusEnum.PENDING))
                .build();
        when(accountLifeCycleService.create(any(Account.class))).thenReturn(account);

        mockMvc.perform(post("/accounts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createAccountRequest))
                .principal(mockAuthentication)
        ).andExpect(status().isCreated())
         .andExpect(jsonPath("$.message").value("Le compte a été crée avec success"));
    }

    @Test
    void createAccount_shouldReturnBadRequest() throws Exception {
        createAccountRequest = new CreateAccountRequest("10", "MGA", null);
        mockMvc.perform(post("/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountRequest))
                        .principal(mockAuthentication)
                ).andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Client ID est obligatoire"));

        verify(accountLifeCycleService, never()).create(any(Account.class));
    }

    @Test
    void createAccount_shouldHandleServiceException() throws Exception {
        doThrow(new OperationNotPermittedException("Some exception was happen in service"))
                .when(accountLifeCycleService).create(any(Account.class));

        mockMvc.perform(post("/accounts/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createAccountRequest))
                        .principal(mockAuthentication)
                ).andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value("Some exception was happen in service"));

    }
}
