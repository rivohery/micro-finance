package com.alibou.finance.customer.infrastructure.in;

import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.customer.infrastructure.adapter.in.controller.AdminCustomerRestResource;
import com.alibou.finance.customer.infrastructure.handler.CustomerExceptionHandler;
import com.alibou.finance.customer.infrastructure.transactional.CreateCustomerUseCaseProxy;
import com.alibou.finance.customer.infrastructure.transactional.CustomerConsultationUseCaseProxy;
import com.alibou.finance.customer.infrastructure.transactional.CustomerLifeCycleUseCaseProxy;
import com.alibou.finance.customer.infrastructure.transactional.UpdateCustomerUseCaseProxy;
import com.alibou.finance.shared.domain.IllegalArgumentException;
import com.alibou.finance.shared.domain.OperationNotPermittedException;
import com.alibou.finance.shared.infrastructure.error.GlobalExceptionHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AdminCustomerRestResourceTest {

    private MockMvc mockMvc;

    @Mock
    private CustomerConsultationUseCaseProxy customerConsultationUseCase;
    @Mock
    private CreateCustomerUseCaseProxy createCustomerService;
    @Mock
    private UpdateCustomerUseCaseProxy updateCustomerUseCase;
    @Mock
    private CustomerLifeCycleUseCaseProxy customerLifeCycleUseCase;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule()); // Important pour LocalDate

    @InjectMocks
    private AdminCustomerRestResource customerController;

    MockMultipartFile mockFile = new MockMultipartFile("file", "image.jpg", "image/jpeg", "content".getBytes());

    @BeforeEach
    void setUp() {
        customerController = new AdminCustomerRestResource(
                customerConsultationUseCase,
                createCustomerService,
                updateCustomerUseCase,
                customerLifeCycleUseCase,
                objectMapper
        );
        mockMvc = MockMvcBuilders
                .standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler(), new CustomerExceptionHandler()) // On ajoute le handler ici !
                .build();
    }

    @Test
    @DisplayName("Devrait échouer quand l'utilisateur est mineur (validation DateOfBirth)")
    void createCustomer_ShouldThrowException_WhenUserIsMinor() throws Exception {
        // GIVEN : Un JSON avec une date de naissance trop récente (2020)
        String jsonInfo = """
                {
                    "firstName": "Jean",
                    "lastName": "Dupont",
                    "username": "jdupont",
                    "dateOfBirth": "2020-01-01",
                    "phoneNumber": "0347366212",
                    "email": "jean@test.com",
                    "cin": "117071009630",
                    "occupation": "Étudiant",
                    "addressValue": "1 rue de Paris",
                    "addressCity": "Paris",
                    "addressZipCode": "75001",
                    "addressCountry": "France"
                }
                """;

        // WHEN & THEN
        mockMvc.perform(multipart("/admin/clients/create")
                        .file(mockFile)
                        .param("customerInfo", jsonInfo))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andExpect(result -> {
                    // On vérifie que la cause racine est bien notre IllegalArgumentException
                    Throwable cause = result.getResolvedException();
                    if (cause.getCause() != null) cause = cause.getCause();

                    assertThat(cause).isInstanceOf(IllegalArgumentException.class);
                    assertThat(cause.getMessage()).contains("Nos services sont réservés uniquement pour le majeur");
                });

        Mockito.verify(createCustomerService, Mockito.never()).execute(any(Customer.class), eq(mockFile.getBytes()), eq(mockFile.getOriginalFilename()));
    }

    @Test
    void createCustomer_ShouldThrowCustomerServiceException() throws Exception {
        // GIVEN : Un JSON valide
        String jsonInfo = """
                {
                    "firstName": "Jean",
                    "lastName": "Dupont",
                    "username": "jdupont",
                    "dateOfBirth": "2000-01-01",
                    "phoneNumber": "0347366212",
                    "email": "jean@test.com",
                    "cin": "117071009630",
                    "occupation": "Étudiant",
                    "addressValue": "1 rue de Paris",
                    "addressCity": "Paris",
                    "addressZipCode": "75001",
                    "addressCountry": "France"
                }
                """;

        Mockito.doThrow(new OperationNotPermittedException("Une exception est levée au niveau du service"))
                .when(createCustomerService).execute(any(Customer.class), eq(mockFile.getBytes()), eq(mockFile.getOriginalFilename()));

        // WHEN & THEN
        mockMvc.perform(multipart("/admin/clients/create")
                             .file(mockFile)
                             .param("customerInfo", jsonInfo)
        ).andExpect(MockMvcResultMatchers.status().isNotAcceptable())
         .andExpect(MockMvcResultMatchers.jsonPath("$.message").value("Une exception est levée au niveau du service"));

        Mockito.verify(createCustomerService, Mockito.times(1)).execute(
                any(Customer.class), eq(mockFile.getBytes()), eq(mockFile.getOriginalFilename())
        );

    }


}
