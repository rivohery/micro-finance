package com.alibou.finance.customer.infrastructure.in;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(MockitoExtension.class)
class AdminCustomerRestResourceTest {
/*
    private MockMvc mockMvc;

    @Mock
    private CustomerConsultationUseCaseProxy customerConsultationUseCase;
    @Mock
    private CreateCustomerUseCaseProxy createCustomerUseCase;
    @Mock
    private UpdateCustomerUseCaseProxy updateCustomerUseCase;
    @Mock
    private CustomerLifeCycleUseCaseProxy customerLifeCycleUseCase;


    @InjectMocks
    private AdminCustomerRestResource customerController;

    MockMultipartFile file = new MockMultipartFile(
            "file",
            "photo.jpg",
            MediaType.IMAGE_JPEG_VALUE,
            "fake-image".getBytes(StandardCharsets.UTF_8)
    );


    @BeforeEach
    void setUp() {
        customerController = new AdminCustomerRestResource(
                customerConsultationUseCase,
                createCustomerUseCase,
                updateCustomerUseCase,
                customerLifeCycleUseCase
        );
        mockMvc = MockMvcBuilders
                .standaloneSetup(customerController)
                .setControllerAdvice(new GlobalExceptionHandler(), new CustomerExceptionHandler()) // On ajoute le handler ici !
                .build();

    }

    @Test
    void createCustomer_shouldRaiseMethodArgumentNotValidException_whenUsernameIsBlank() throws Exception {
        String customerInfoJson = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "userId": null,
                  "username": "",
                  "dateOfBirth": "1990-01-01",
                  "phoneNumber": "0612345678",
                  "email": "john.doe@example.com",
                  "cin": "CIN123",
                  "occupation": "Engineer",
                  "addressValue": "1 rue de Paris",
                  "addressCity": "Paris",
                  "addressZipCode": "75000",
                  "addressCountry": "France"
                }
                """;

        MockMultipartFile customerInfo = new MockMultipartFile(
                "customerInfo",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                customerInfoJson.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(buildMultipart(customerInfo))
                .andExpect(status().isBadRequest())
                .andExpect(result -> {
                    Exception resolvedException = result.getResolvedException();


                    assertInstanceOf(MethodArgumentNotValidException.class, resolvedException);

                    MethodArgumentNotValidException ex = (MethodArgumentNotValidException) resolvedException;

                    assertTrue(ex.getBindingResult().hasFieldErrors("username"));
                    assertThat(ex.getMessage()).contains("Le pseudo ne doit pas être vide");
                });

        verifyNoInteractions(createCustomerUseCase);
    }
    @Test
    void createCustomer_ShouldThrowServiceException() throws Exception {
        // GIVEN : Un JSON valide
        String customerInfoJson = """
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

        MockMultipartFile customerInfo = new MockMultipartFile(
                "customerInfo",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                customerInfoJson.getBytes(StandardCharsets.UTF_8)
        );


        Mockito.doThrow(new OperationNotPermittedException("Une exception est levée au niveau du service"))
                .when(createCustomerUseCase).execute(any(CreateCustomerCommand.class), eq(file.getBytes()), eq(file.getOriginalFilename()));

        // WHEN & THEN
        mockMvc.perform(buildMultipart(customerInfo))
                .andExpect(status().isNotAcceptable())
                .andExpect(jsonPath("$.message").value("Une exception est levée au niveau du service"));

        Mockito.verify(createCustomerUseCase).execute(any(CreateCustomerCommand.class), eq(file.getBytes()), eq(file.getOriginalFilename()));

    }

    @Test
    void createCustomer_shouldRaiseIllegalArgumentException_when_customer_is_minor() throws Exception{
        String customerInfoJson = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "username": "johndoe",
                  "dateOfBirth": "2016-01-01",
                  "phoneNumber": "0349563704",
                  "email": "john.doe@example.com",
                  "cin": "117071009630",
                  "occupation": "Engineer",
                  "addressValue": "1 rue de Paris",
                  "addressCity": "Paris",
                  "addressZipCode": "75000",
                  "addressCountry": "France"
                }
                """;

        MockMultipartFile customerInfo = new MockMultipartFile(
                "customerInfo",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                customerInfoJson.getBytes(StandardCharsets.UTF_8)
        );

        mockMvc.perform(buildMultipart(customerInfo))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Nos services sont réservés uniquement pour le majeur"));
        verifyNoInteractions(createCustomerUseCase);
    }

    @Test
    void createCustomer_shouldReturnCreated_whenRequestIsValid() throws Exception {
        UUID customerId = UUID.randomUUID();

        Customer created = mock(Customer.class);
        when(created.getCustomerId()).thenReturn(CustomerId.from(customerId));

        when(createCustomerUseCase.execute(any(CreateCustomerCommand.class), any(byte[].class), eq("photo.jpg")))
                .thenReturn(created);

        String customerInfoJson = """
                {
                  "firstName": "John",
                  "lastName": "Doe",
                  "userId": null,
                  "username": "johndoe",
                  "dateOfBirth": "1990-01-01",
                  "phoneNumber": "0349563704",
                  "email": "john.doe@example.com",
                  "cin": "117071009630",
                  "occupation": "Engineer",
                  "addressValue": "1 rue de Paris",
                  "addressCity": "Paris",
                  "addressZipCode": "75000",
                  "addressCountry": "France"
                }
                """;

        MockMultipartFile customerInfo = new MockMultipartFile(
                "customerInfo",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                customerInfoJson.getBytes(StandardCharsets.UTF_8)
        );
        mockMvc.perform(buildMultipart(customerInfo))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("Le client a été bien ajouté dans la base de donnée"))
                .andExpect(jsonPath("$.data.clientId").value(customerId.toString()));

        verify(createCustomerUseCase).execute(any(CreateCustomerCommand.class), any(byte[].class), eq("photo.jpg"));
    }

    private MockMultipartHttpServletRequestBuilder buildMultipart(MockMultipartFile customerInfo){
        return multipart("/admin/clients/create")
                .file(customerInfo)
                .file(file);
    }

    @Test
    void updateCustomer_shouldDeserializeJsonIntoCustomerRequest_andReturnOk() throws Exception {
        UUID customerId = UUID.randomUUID();
        UUID userId     = UUID.randomUUID();
        String customerInfoJson = """
            {
              "firstName": "Amine",
              "lastName": "Benali",
              "userId": "%s",
              "username": "amine.benali",
              "dateOfBirth": "1990-05-15",
              "phoneNumber": "0332186310",
              "email": "amine.benali@example.com",
              "cin": "117071009630",
              "occupation": "Ingénieur",
              "addressValue": "12 Rue de la Paix",
              "addressCity": "Casablanca",
              "addressZipCode": "20250",
              "addressCountry": "Maroc"
            }
            """.formatted(userId);

        MockMultipartFile customerInfo = new MockMultipartFile(
                "customerInfo",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                customerInfoJson.getBytes(StandardCharsets.UTF_8)
        );
        Customer updated = mock(Customer.class);
        UpdateCustomerCommand command = mock(UpdateCustomerCommand.class);

        when(updateCustomerUseCase.execute(any(UpdateCustomerCommand.class), any(byte[].class), anyString())).thenReturn(updated);
        when(updated.getCustomerId()).thenReturn(new CustomerId(customerId));

        // ⚠️ MockMvcRequestBuilders.multipart(...) génère un POST par défaut :
        //    on force la méthode PUT via .with(...)
        mockMvc.perform(
                    multipart("/admin/clients/update/{customerId}", customerId)
                            .file(customerInfo)
                            .file(file)
                            .with(request -> {
                                request.setMethod("PUT");
                                return request;
                            })
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("Modification des informations clients réussie"))
                .andExpect(jsonPath("$.data.clientId").value(customerId.toString()));

        ArgumentCaptor<UpdateCustomerCommand> updateCustomerCommandCaptor = ArgumentCaptor.forClass(UpdateCustomerCommand.class);
        verify(updateCustomerUseCase).execute(updateCustomerCommandCaptor.capture(), any(byte[].class), anyString());
        UpdateCustomerCommand ucd = updateCustomerCommandCaptor.getValue();

        //On teste si on arrive à avoir l'objet UpdateCustomerCommand => deserialization succès
        assertThat(ucd.firstName().value()).isEqualTo("Amine");
        assertThat(ucd.lastName().value()).isEqualTo("Benali");
        assertThat(ucd.userId().value()).isEqualTo(userId);
        assertThat(ucd.customerId().value()).isEqualTo(customerId);
        assertThat(ucd.phoneNumber().value()).isEqualTo("0332186310");
    }

*/
}
