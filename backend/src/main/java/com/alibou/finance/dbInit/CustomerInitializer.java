package com.alibou.finance.dbInit;

import com.alibou.finance.auth.domain.agregate.RoleEnum;
import com.alibou.finance.auth.infrastructure.adapter.out.persistence.entity.UserEntity;
import com.alibou.finance.auth.infrastructure.adapter.out.persistence.repository.UserJpaRepository;
import com.alibou.finance.customer.domain.agregate.CustomerStatus;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.entity.CustomerEntity;
import com.alibou.finance.customer.infrastructure.adapter.out.persistence.repository.CustomerJpaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.UUID;

//@Profile({"dev"})
//@Order(value = 4)
//@Component
@RequiredArgsConstructor
public class CustomerInitializer implements CommandLineRunner {

    private final UserJpaRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final CustomerJpaRepository customerRepository;

    @Override
    public void run(String... args) throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate startWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        System.out.println(startWeek.format(DateTimeFormatter.ISO_DATE));

        int mondayRegistration = 5;
        for(int i = 0;  i < mondayRegistration; i++){
            boolean existbyUsername = userRepository.existsByUsername("johndoe" + i);
            if(!existbyUsername){
                UserEntity user = createAndSaveUserEntity("johndoe" + i);
                CustomerEntity customer = createMinimalCustomerEntity(
                        "John" + i,"Doe" + i,"11707100963" + i,"034736621" + i, CustomerStatus.ACTIVE, user, startWeek
                );
                boolean existByCin = customerRepository.existsByCin("11707100963" + i);
                if(!existByCin){
                    customerRepository.save(customer);
                }
            }
        }
        int tuesdayRegistration = 3;
        for(int i = 0;  i < tuesdayRegistration; i++){
            boolean existByUsername = userRepository.existsByUsername("mohamedali" + i);
            if(!existByUsername){
                UserEntity user = createAndSaveUserEntity("mohamedali" + i);
                CustomerEntity customer = createMinimalCustomerEntity(
                        "Mohamed" + i,"Ali" + i,"11709100964" + i,"033736621" + i, CustomerStatus.ACTIVE, user, startWeek.plusDays(1)
                );
                boolean existByCin = customerRepository.existsByCin("11709100964" + i);
                if(!existByCin){
                    customerRepository.save(customer);
                }
            }
        }
        int wednesdayRegistration = 4;
        for(int i = 0;  i < wednesdayRegistration; i++){
            boolean existByUsername = userRepository.existsByUsername("sarahconor" + i);
            if(!existByUsername){
                UserEntity user = createAndSaveUserEntity("sarahconor" + i);
                CustomerEntity customer = createMinimalCustomerEntity(
                        "Sarah" + i,"Conor" + i,"11708100984" + i,"032736621" + i, CustomerStatus.ACTIVE, user, startWeek.plusDays(2)
                );
                boolean existByCin = customerRepository.existsByCin("11708100984" + i);
                if(!existByCin){
                    customerRepository.save(customer);
                }
            }
        }
        int thursdayRegistration = 2;
        for(int i = 0;  i < thursdayRegistration; i++){
            boolean existByUsername = userRepository.existsByUsername("peterparker" + i);
            if(!existByUsername){
                UserEntity user = createAndSaveUserEntity("peterparker" + i);
                CustomerEntity customer = createMinimalCustomerEntity(
                        "Peter" + i,"Parker" + i,"11702100934" + i,"038736621" + i, CustomerStatus.ACTIVE, user, startWeek.plusDays(3)
                );
                boolean existByCin = customerRepository.existsByCin("11702100934" + i);
                if(!existByCin){
                    customerRepository.save(customer);
                }
            }
        }
        int fridayRegistration = 5;
        for(int i = 0;  i < fridayRegistration; i++){
            boolean existByUsername = userRepository.existsByUsername("smithdoe" + i);
            if(!existByUsername){
                UserEntity user = createAndSaveUserEntity("smithdoe" + i);
                CustomerEntity customer = createMinimalCustomerEntity(
                        "Smith" + i,"Doe" + i,"11705100974" + i,"037736681" + i, CustomerStatus.ACTIVE, user, startWeek.plusDays(4)
                );
                boolean existByCin = customerRepository.existsByCin("11705100974" + i);
                if(!existByCin){
                    customerRepository.save(customer);
                }
            }
        }
        int saturdayRegistration = 1;
        for(int i = 0;  i < saturdayRegistration; i++){
            boolean existByUsername = userRepository.existsByUsername("mickaeljackson" + i);
            if(!existByUsername){
                UserEntity user = createAndSaveUserEntity("mickaeljackson" + i);
                CustomerEntity customer = createMinimalCustomerEntity(
                        "Mickael" + i,"Jackson" + i,"11708170974" + i,"039736481" + i, CustomerStatus.ACTIVE, user, startWeek.plusDays(5)
                );
                boolean existByCin = customerRepository.existsByCin("11708170974" + i);
                if(!existByCin){
                    customerRepository.save(customer);
                }
            }
        }
    }

    private UserEntity createAndSaveUserEntity(String username){
        return userRepository.save(UserEntity.builder()
                .id(UUID.randomUUID())
                .username(username.toLowerCase())
                .role(RoleEnum.CLIENT)
                .enable(true)
                .password(bCryptPasswordEncoder.encode("0000"))
                .email(username.toLowerCase() + "@gmail.com")
                .build()
        );
    }

    private CustomerEntity createMinimalCustomerEntity(
            String firstName, String lastName, String cin, String phoneNumber, CustomerStatus status, UserEntity user, LocalDate createdDate
    ) {
        UserEntity admin = userRepository.findByUsername("alibou").orElseThrow(
                ()-> new EntityNotFoundException("Admin not found")
        );
        CustomerEntity entity = new CustomerEntity();
        entity.setId(UUID.randomUUID());
        entity.setFirstName(firstName);
        entity.setLastName(lastName);
        entity.setCin(cin);
        entity.setEmail(firstName.toLowerCase() + "@example.com");
        entity.setDateOfBirth(LocalDate.of(1990, 1, 1));
        entity.setPhoneNumber(phoneNumber);
        entity.setUserEntity(user);
        entity.setCreatedBy(admin.getId());
        entity.setCreatedDate(createdDate);
        entity.setImageUrl("http://");
        entity.setOccupation("test");
        entity.setAddressValue("test");
        entity.setAddressCity("city");
        entity.setStatus(status);
        entity.setAddressCountry("country");
        entity.setAddressZipCode("102");
        entity.setImageUrl("./uploads\\clients\\2130cc08-1a17-4803-acaf-2c299913e5a5.jpg");
        return entity;
    }
}


