package com.alibou.finance.customer.domain.agregate;

import com.alibou.finance.auth.domain.agregate.User;
import com.alibou.finance.auth.domain.vo.Address;
import com.alibou.finance.customer.domain.vo.*;
import com.alibou.finance.shared.domain.OperationNotPermittedException;
import com.alibou.finance.shared.vo.domain.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Customer {
    private CustomerId customerId;
    private FirstName firstName;
    private LastName lastName;
    private DateOfBirth dateOfBirth;
    private PhoneNumber phoneNumber;
    private Email email;
    private Cin cin;
    private Status status;
    private Address address;
    private Occupation occupation;
    private ImageUrl imageUrl;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;
    private UUID createdBy;
    private UUID lastModifiedBy;
    private User user;

    public void updateCustomerId(CustomerId customerId){
        if(Objects.isNull(customerId)){
            return;
        }
        this.customerId = customerId;
    }

    public void updateImageUrl(String filePath){
        if(filePath == null || filePath.length() == 0){
            return;
        }
        this.imageUrl = new ImageUrl(filePath);
    }
    public void updateUser(User user){
        if(Objects.isNull(user)){
            return;
        }
        this.user = user;
    }

    public void updateCin(Cin cin){
        if(Objects.isNull(cin)){
            return;
        }
        this.cin = cin;
    }

    public void updatePhoneNumber(PhoneNumber phoneNumber){
        if(Objects.isNull(phoneNumber)){
            return;
        }
        this.phoneNumber = phoneNumber;
    }

    public void updateFirstName(FirstName firstName){
        if(Objects.isNull(firstName)){
            return;
        }
        this.firstName = firstName;
    }

    public void updateLastName(LastName lastName){
        if(Objects.isNull(lastName)){
            return;
        }
        this.lastName = lastName;
    }

    public void updateAdresse(Address adresse){
        if(Objects.isNull(adresse)){
            return;
        }
        this.address = adresse;
    }
    public void updateDateOfBirth(DateOfBirth dateOfBirth){
        if(Objects.isNull(dateOfBirth)){
            return;
        }
        this.dateOfBirth = dateOfBirth;
    }

    public void updateEmail(Email email){
        if(Objects.isNull(email)){
            return;
        }
        this.email = email;
    }
    public void updateOccupation(Occupation occupation){
        if(Objects.isNull(occupation)){
            return;
        }
        this.occupation = occupation;
    }

    public void initCustomer(){
        this.customerId =  CustomerId.generate();
        this.status = Status.pending();
    }

    public void suspend(){
        if(this.status.value() == CustomerStatus.ACTIVE){
            this.status = Status.suspended();
        } else {
            throw new OperationNotPermittedException("Le status de ce client être encore 'PENDING'");
        }
    }

    public void active(){
        if(this.status.value() == CustomerStatus.PENDING || this.status.value() == CustomerStatus.SUSPENDED){
            this.status = Status.active();
        }
    }

    public void close(){
        if(this.status.value() != CustomerStatus.SUSPENDED){
            throw new OperationNotPermittedException("Clôture du compte client interrompu: soit le client est active ou encore en attente");
        }
        this.status = Status.close();
    }

}
