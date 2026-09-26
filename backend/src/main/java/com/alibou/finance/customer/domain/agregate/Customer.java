package com.alibou.finance.customer.domain.agregate;

import com.alibou.finance.customer.domain.vo.Address;
import com.alibou.finance.shared.domain.vo.user.UserId;
import com.alibou.finance.customer.domain.vo.*;
import com.alibou.finance.shared.domain.exception.OperationNotPermittedException;
import com.alibou.finance.shared.domain.vo.user.CustomerId;
import com.alibou.finance.shared.domain.vo.user.Email;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

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
    private CustomerStatus customerStatus;
    private Address address;
    private Occupation occupation;
    private ImageUrl imageUrl;
    private LocalDate createdDate;
    private LocalDate lastModifiedDate;
    private UUID createdBy;
    private UUID lastModifiedBy;
    private UserId userId;

    /**
     * Règle métier : ne modifier un champ que s'il a effectivement changé.
     * Le domaine décide, la couche application ne fait qu'appeler.
     */
    public void updateProfile(
            FirstName firstName,
            LastName lastName,
            Occupation occupation,
            PhoneNumber phoneNumber,
            DateOfBirth dateOfBirth,
            Address address
    ) {
        if (!Objects.equals(this.firstName, firstName)) {
            this.firstName = firstName;
        }
        if (!Objects.equals(this.lastName, lastName)) {
            this.lastName = lastName;
        }
        if (!Objects.equals(this.occupation, occupation)) {
            this.occupation = occupation;
        }
        if (!Objects.equals(this.phoneNumber, phoneNumber)) {
            this.phoneNumber = phoneNumber;
        }
        if (!Objects.equals(this.dateOfBirth, dateOfBirth)) {
            this.dateOfBirth = dateOfBirth;
        }
        if (!Objects.equals(this.address, address)) {
            this.address = address;
        }
    }

    public void updateEmail(Email newEmail){
        if(!Objects.equals(this.email, newEmail)){
            this.email = newEmail;
        }
    }

    public void updateImageUrl(String imageUrl) {
        if (StringUtils.isNotBlank(imageUrl)) {
            this.imageUrl = new ImageUrl(imageUrl);
        }
    }


    public void initCustomer(){
        this.customerId =  CustomerId.generate();
        this.customerStatus = CustomerStatus.pending();
    }

    public void suspend(){
        if(this.customerStatus.value() == CustomerStatusEnum.ACTIVE){
            this.customerStatus = CustomerStatus.suspended();
        } else {
            throw new OperationNotPermittedException("On ne peut suspendre que les clients actives");
        }
    }

    public void active(){
        if(this.customerStatus.value() == CustomerStatusEnum.PENDING_KYC || this.customerStatus.value() == CustomerStatusEnum.SUSPENDED){
            this.customerStatus = CustomerStatus.active();
        } else {
            throw new OperationNotPermittedException("Les clients dont le status est 'CLOSED' ne sont plus activables");
        }
    }

    public void close(){
        if(this.customerStatus.value() != CustomerStatusEnum.CLOSED){
            this.customerStatus = CustomerStatus.close();
        } else {
           System.out.println("Le status est deja CLOSED");
        }
    }

}
