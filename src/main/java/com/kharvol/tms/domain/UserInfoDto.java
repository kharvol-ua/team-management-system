package com.kharvol.tms.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.kharvol.tms.validation.group.OnCreate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
@EqualsAndHashCode(callSuper = true)
public class UserInfoDto extends BaseDto {

    @NotBlank(message = "{userInfo.username.blank}")
    private String username;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "{userInfo.password.blank}", groups = OnCreate.class)
    @Size(min = 8, message = "{userInfo.password.size}", groups = OnCreate.class)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$", message = "{userInfo.password.pattern}", groups = OnCreate.class)
    private String password;

    @NotBlank(message = "{userInfo.firstName.blank}")
    private String firstName;

    private String lastName;

    private String nickname;

    @NotBlank(message = "{userInfo.status.blank}")
    private String status;

    private LocalDate dateOfBirth;


}
