package com.kharvol.tms.domain;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class BaseDto {

    @JsonProperty(access = Access.READ_ONLY)
    private String id;

    @JsonProperty(access = Access.READ_ONLY)
    private String createdBy;

    @JsonProperty(access = Access.READ_ONLY)
    private OffsetDateTime createdDate;

    @JsonProperty(access = Access.READ_ONLY)
    private String modifiedBy;

    @JsonProperty(access = Access.READ_ONLY)
    private OffsetDateTime modifiedDate;


}
