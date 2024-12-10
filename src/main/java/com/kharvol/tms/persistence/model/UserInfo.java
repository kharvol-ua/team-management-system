package com.kharvol.tms.persistence.model;

import jakarta.persistence.Entity;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class UserInfo extends BaseModel {
    
    private String username;

    private String password;

    private String firstName;

    private String lastName;

    private String nickname;

    private String status;

    private LocalDate dateOfBirth;

}
