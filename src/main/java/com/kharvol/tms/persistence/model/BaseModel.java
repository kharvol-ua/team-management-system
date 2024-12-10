package com.kharvol.tms.persistence.model;

import com.kharvol.tms.persistence.listener.IdGeneratorListener;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@MappedSuperclass
@EntityListeners({IdGeneratorListener.class})

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public abstract class BaseModel {

    @Id
    private String id;

    private String createdBy;

    private OffsetDateTime createdDate;

    private String modifiedBy;

    private OffsetDateTime modifiedDate;


    @PrePersist
    public void created() {
        createdDate = modifiedDate = OffsetDateTime.now();
    }

    @PreUpdate
    public void modified() {
        modifiedDate = OffsetDateTime.now();
//        lastModifiedBy = SecurityContextHolder.getContext().getAuthentication().getName();
    }

}
