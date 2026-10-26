package com.alibou.finance.customer.infrastructure.in;

import com.alibou.finance.BaseControllerIT;
import com.alibou.finance.customer.domain.agregate.Customer;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.customer.infrastructure.transactional.CreateCustomerUseCaseProxy;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.security.test.context.support.WithMockUser;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pour tester le droit d'accès
 */
public class AdminCustomerRestResourceIT extends BaseControllerIT {

    private static final String CREATE_URL = "/admin/clients/create";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CreateCustomerUseCaseProxy createCustomerUseCase;


    @Test
    @DisplayName("Sans authentification : accès refusé (401/403)")
    void createCustomer_withoutAuthentication_shouldBeRejected() throws Exception {
        mockMvc.perform(buildCreateRequest())
                .andExpect(result -> {
                    int s = result.getResponse().getStatus();
                    if (s != 401 && s != 403) {
                        throw new AssertionError("Attendu 401 ou 403, obtenu " + s);
                    }
                });
    }

    @Test
    @DisplayName("Utilisateur authentifié sans authority ADMIN : 401 Forbidden")
    @WithMockUser(username = "simple-user", authorities = {"EMPLOYE"})
    void createCustomer_withoutAdminAuthority_shouldReturn401() throws Exception {
        mockMvc.perform(buildCreateRequest())
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Utilisateur avec authority ADMIN : accès autorisé (201 Created)")
    @WithMockUser(username = "admin-user", authorities = {"ADMIN"})
    void createCustomer_withAdminAuthority_shouldSucceed() throws Exception {
        // ----- ARRANGE -----
        Customer mockedCustomer = mock(Customer.class);
        UUID generatedId = UUID.randomUUID();
        when(mockedCustomer.getCustomerId()).thenReturn(CustomerId.from(generatedId));
        when(createCustomerUseCase.execute(any(), any(), anyString()))
                .thenReturn(mockedCustomer);

        // ----- ACT & ASSERT -----
        mockMvc.perform(buildCreateRequest())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.data.clientId").value(generatedId.toString()));
    }

    /**
     * Construit une requête multipart valide vers /create.
     */
    private MockHttpServletRequestBuilder
    buildCreateRequest() {
        // Un JSON minimal valide — adapte les champs selon ton CustomerRequest
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
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "photo.png",
                "image/png",
                "fake-image-content".getBytes(StandardCharsets.UTF_8)
        );

        MockMultipartFile customerInfo = new MockMultipartFile(
                "customerInfo",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                jsonInfo.getBytes(StandardCharsets.UTF_8)
        );

        return multipart(CREATE_URL)
                .file(file)
                .file(customerInfo)
                .with(csrf());
    }


}
