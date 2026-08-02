package com.alibou.finance.customer.infrastructure.out;

import com.alibou.finance.auth.domain.model.RoleEnum;
import com.alibou.finance.auth.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.alibou.finance.auth.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.projection.RegistrationStatisticProj;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.repository.CustomerJpaRepository;
import com.alibou.finance.customer.domain.model.CustomerStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CustomerJpaRepositoryTest {

    @Autowired
    private CustomerJpaRepository customerJpaRepository;
    @Autowired
    private UserJpaRepository userJpaRepository;

    @BeforeEach
    void setUp() {
        // On nettoie la base avant chaque test
        customerJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

    }

    @Test
    @DisplayName("Devrait retourner une page de client dont le nom|prénom|cin commence par un mots clés")
    void shouldFindCustomersByMatchingFirstNameLastNameOrCin() {
        UserEntity user1 = createAndSaveUserEntity("Alibaba");
        UserEntity user2 = createAndSaveUserEntity("John");
        UserEntity user3 = createAndSaveUserEntity("Robert");
        UserEntity user4 = createAndSaveUserEntity("Zorro");
        CustomerEntity customer1 = createMinimalCustomerEntity("Alibaba", "Smith", "117071001", "117", CustomerStatus.SUSPENDED, user1, LocalDate.now());
        CustomerEntity customer2 = createMinimalCustomerEntity("John", "Alaric", "228082002", "456",CustomerStatus.ACTIVE, user2, LocalDate.now());
        CustomerEntity customer3 = createMinimalCustomerEntity("Robert", "Downey", "117093003", "345", CustomerStatus.PENDING, user3, LocalDate.now());
        CustomerEntity customer4 = createMinimalCustomerEntity("Zorro", "Ronald", "999999999", "999", CustomerStatus.SUSPENDED, user4, LocalDate.now());


        customerJpaRepository.saveAndFlush(customer1);
        customerJpaRepository.saveAndFlush(customer2);
        customerJpaRepository.saveAndFlush(customer3);
        customerJpaRepository.saveAndFlush(customer4);

        Pageable pageable = PageRequest.of(0, 10);

        Page<CustomerEntity> resultName = customerJpaRepository
                .fetchAllEnableCustomerBySearchBegin("al", pageable);

        Page<CustomerEntity> resultCin = customerJpaRepository
                .fetchAllEnableCustomerBySearchBegin("117", pageable);
        
        Page<CustomerEntity> resultOfNameStart = customerJpaRepository
                .findAllByFirstNameStartingWithIgnoreCaseOrLastNameStartingWithIgnoreCaseOrCinStartingWith("ro", "ro", "ro", pageable);
        Page<CustomerEntity> resultOfCinStart = customerJpaRepository
                .findAllByFirstNameStartingWithIgnoreCaseOrLastNameStartingWithIgnoreCaseOrCinStartingWith("11", "11", "11", pageable);
        

        // 4. Then (Assertions)
        assertThat(resultName.getContent()).hasSize(2);
        assertThat(resultName.getContent())
                .extracting(CustomerEntity::getFirstName)
                .containsExactlyInAnyOrder("Alibaba", "John");

        assertThat(resultCin.getContent()).hasSize(2);
        assertThat(resultCin.getContent())
                .extracting(CustomerEntity::getCin)
                .contains("117071001", "117093003");

        assertThat(resultOfNameStart.getContent()).hasSize(2);
        assertThat(resultOfNameStart.getTotalPages()).isEqualTo(1);
        assertThat(resultOfNameStart.getContent()).extracting(CustomerEntity::getFirstName).containsExactly("Robert", "Zorro");

        assertThat(resultOfCinStart.getContent()).hasSize(2);
        assertThat(resultOfCinStart.getTotalPages()).isEqualTo(1);
        assertThat(resultOfCinStart.getContent()).extracting(CustomerEntity::getCin).containsExactly("117071001", "117093003");

    }

    @Test
    @DisplayName("Devrait modifier le status du client avec success")
    void updateCustomerStatusTest(){
        //Given
        CustomerStatus actualStatus= CustomerStatus.SUSPENDED;
        UserEntity user1 = createAndSaveUserEntity("Alibaba");
        CustomerEntity customer1 =
                createMinimalCustomerEntity("Alibaba", "Smith", "117071001","667", actualStatus, user1, LocalDate.now());

        customer1 = customerJpaRepository.saveAndFlush(customer1);

        int result = customerJpaRepository.updateCustomerStatus(customer1.getId(), CustomerStatus.ACTIVE);

        assertThat(result).isEqualTo(1);
        var customerUpdated = customerJpaRepository.findByCin("117071001").get();
        assertThat(customerUpdated.getStatus()).isEqualTo(CustomerStatus.ACTIVE);
    }

    @Test
    @DisplayName("Devrait retourner les statistiques de nouveau client par jour: Lundi jusqu'à Samedi pour le test")
    void shouldGetCustomersPerDayOfWeek(){
        LocalDate monday = LocalDate.of(2026, 6, 1);
        List<LocalDate> weeks  = monday.datesUntil(LocalDate.of(2026,6,8)).toList();
        int mondayCustomer = 5;
        int tuesdayCustomer = 4;
        int wednesdayCustomer = 6;
        int thursdayCustomer = 3;
        int fridayCustomer = 4;
        int saturdayCustomer = 2;
        for(int i=0; i < mondayCustomer; i++){
            UserEntity user = createAndSaveUserEntity("alibaba" + i);
            CustomerEntity customer =
                    createMinimalCustomerEntity("Alibaba" + i, "Smith" + i, "117071001" + i, "117" + i, CustomerStatus.ACTIVE, user, weeks.get(0));
            customerJpaRepository.save(customer);
        }
        for(int i=0; i < tuesdayCustomer; i++){
            UserEntity user = createAndSaveUserEntity("john" + i);
            CustomerEntity customer =
                    createMinimalCustomerEntity("John" + i, "Doe" + i, "1170710012" + i, "116" + i, CustomerStatus.ACTIVE, user, weeks.get(1));
            customerJpaRepository.save(customer);
        }
        for(int i=0; i < wednesdayCustomer; i++){
            UserEntity user = createAndSaveUserEntity("marry" + i);
            CustomerEntity customer =
                    createMinimalCustomerEntity("Marry" + i, "Jane" + i, "1160710012" + i, "216" + i, CustomerStatus.ACTIVE, user, weeks.get(2));
            customerJpaRepository.save(customer);
        }
        for(int i=0; i < thursdayCustomer; i++){
            UserEntity user = createAndSaveUserEntity("nestor" + i);
            CustomerEntity customer =
                    createMinimalCustomerEntity("Nestor" + i, "Conor" + i, "1360710012" + i, "516" + i, CustomerStatus.ACTIVE, user, weeks.get(3));
            customerJpaRepository.save(customer);
        }
        for(int i=0; i < fridayCustomer; i++){
            UserEntity user = createAndSaveUserEntity("wilson" + i);
            CustomerEntity customer =
                    createMinimalCustomerEntity("Wilson" + i, "James" + i, "1360718012" + i, "518" + i, CustomerStatus.ACTIVE, user, weeks.get(4));
            customerJpaRepository.save(customer);
        }
        for(int i=0; i < saturdayCustomer; i++){
            UserEntity user = createAndSaveUserEntity("jefferson" + i);
            CustomerEntity customer =
                    createMinimalCustomerEntity("Jefferson" + i, "Douglas" + i, "1360744012" + i, "918" + i, CustomerStatus.ACTIVE, user, weeks.get(5));
            customerJpaRepository.save(customer);
        }

        assertThat(customerJpaRepository.findAll().stream().filter(c -> c.getCreatedDate().getDayOfWeek() == DayOfWeek.MONDAY).toList().size()).isEqualTo(5);
        assertThat(customerJpaRepository.findAll().stream().filter(c -> c.getCreatedDate().getDayOfWeek() == DayOfWeek.TUESDAY).toList().size()).isEqualTo(4);
        assertThat(customerJpaRepository.findAll().stream().filter(c -> c.getCreatedDate().getDayOfWeek() == DayOfWeek.WEDNESDAY).toList().size()).isEqualTo(6);
        assertThat(customerJpaRepository.findAll().stream().filter(c -> c.getCreatedDate().getDayOfWeek() == DayOfWeek.THURSDAY).toList().size()).isEqualTo(3);
        assertThat(customerJpaRepository.findAll().stream().filter(c -> c.getCreatedDate().getDayOfWeek() == DayOfWeek.FRIDAY).toList().size()).isEqualTo(4);
        assertThat(customerJpaRepository.findAll().stream().filter(c -> c.getCreatedDate().getDayOfWeek() == DayOfWeek.SATURDAY).toList().size()).isEqualTo(2);

        LocalDate startWeek = monday.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endWeek = monday.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        //System.out.println(startWeek.format(DateTimeFormatter.ISO_DATE));
        //System.out.println(endWeek.format(DateTimeFormatter.ISO_DATE));
        List<RegistrationStatisticProj> statistics =
                customerJpaRepository.getCustomersPerDayOfWeek(startWeek, endWeek);

        assertThat(statistics.size()).isEqualTo(6);
        assertThat(statistics).extracting(RegistrationStatisticProj::getNbrCustomer).containsExactlyInAnyOrder(5L,4L,6L,3L, 4L, 2L);
        assertThat(statistics).extracting(stat -> stat.getCreatedDate().getDayOfWeek().name())
                .containsExactlyInAnyOrder("MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY");
        for(RegistrationStatisticProj registration : statistics){
            System.out.println("============");
            System.out.println(registration.getCreatedDate().getDayOfWeek().name().toLowerCase());
            System.out.println(registration.getNbrCustomer());
            System.out.println("============");
        }
    }

    @Test
    @DisplayName("Devrait retourner le nombre des clients dont le status n'est pas fermé")
    void shouldGetNbrTotalOfCustomerNoClosed(){
        UserEntity user1 = createAndSaveUserEntity("Alibaba");
        UserEntity user2 = createAndSaveUserEntity("John");
        UserEntity user3 = createAndSaveUserEntity("Robert");
        UserEntity user4 = createAndSaveUserEntity("Zorro");
        CustomerEntity customer1 = createMinimalCustomerEntity("Alibaba", "Smith", "117071001", "117", CustomerStatus.SUSPENDED, user1, LocalDate.now());
        CustomerEntity customer2 = createMinimalCustomerEntity("John", "Alaric", "228082002", "456",CustomerStatus.ACTIVE, user2, LocalDate.now());
        CustomerEntity customer3 = createMinimalCustomerEntity("Robert", "Downey", "117093003", "345", CustomerStatus.PENDING, user3, LocalDate.now());
        CustomerEntity customer4 = createMinimalCustomerEntity("Zorro", "Mask", "999999999", "999", CustomerStatus.CLOSED, user4, LocalDate.now());
        customerJpaRepository.saveAll(List.of(customer1, customer2, customer3, customer4));

        Long nbrCustomerNoClosed = customerJpaRepository.getNbrTotalOfCustomerNoClosed(CustomerStatus.CLOSED);

        assertThat(nbrCustomerNoClosed).isEqualTo(3);
    }

    private UserEntity createAndSaveUserEntity(String username){
       return userJpaRepository.save(UserEntity.builder()
               .id(UUID.randomUUID())
               .username(username.toLowerCase())
               .role(RoleEnum.CLIENT)
               .enable(true)
               .password("1234")
               .email(username.toLowerCase() + "@gmail.com")
               .build()
       );
    }

    private CustomerEntity createMinimalCustomerEntity(
            String firstName, String lastName, String cin, String phoneNumber, CustomerStatus status, UserEntity user, LocalDate createdDate
    ) {
        CustomerEntity entity = new CustomerEntity();
        entity.setId(UUID.randomUUID());
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setCin(cin);
        entity.setEmail(firstName.toLowerCase() + "@example.com");
        entity.setDateOfBirth(LocalDate.of(1990, 1, 1));
        entity.setImageUrl("http://");
        entity.setOccupation("test");
        entity.setAddressValue("test");
        entity.setPhoneNumber(phoneNumber);
        entity.setAddressCity("city");
        entity.setStatus(status);
        entity.setAddressCountry("country");
        entity.setAddressZipCode("102");
        entity.setUserEntity(user);
        entity.setCreatedBy(UUID.randomUUID());
        entity.setCreatedDate(createdDate);
        return entity;
    }
}
