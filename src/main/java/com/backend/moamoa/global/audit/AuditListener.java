package com.backend.moamoa.global.audit;

import org.springframework.context.annotation.Configuration;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.LocalDateTime;

@Configuration
public class AuditListener {
    @PrePersist
    public void setCreateDate(Auditable auditable) {
        TimeEntity timeEntity = auditable.getTimeEntity();

        if (timeEntity == null) {
            timeEntity = new TimeEntity();
            auditable.setTimeEntity(timeEntity);
        }
        timeEntity.setCreatedDate(LocalDateTime.now());
        timeEntity.setUpdatedDate(LocalDateTime.now());
    }

    @PreUpdate
    public void setUpdateDate(Auditable auditable){
        auditable.getTimeEntity().setUpdatedDate(LocalDateTime.now());
    }
}
