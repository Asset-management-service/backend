package com.backend.moamoa.domain.post.entity;

import com.backend.moamoa.global.audit.AuditListener;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import lombok.Getter;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.*;

@Entity
@Getter
@EntityListeners(AuditListener.class)
public class PostCategory implements Auditable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    private String name;

    @Embedded
    private TimeEntity timeEntity;

    @OneToMany(mappedBy = "postCategory")
    private List<Post> posts = new ArrayList<>();

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }
}
