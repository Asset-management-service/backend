package com.backend.moamoa.domain.scrap.entity;

import com.backend.moamoa.global.audit.AuditListener;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditListener.class)
public class Scrap implements Auditable {

    @Id
    @Column(name = "scrap_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Embedded
    private TimeEntity timeEntity;

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }
}
