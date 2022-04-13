package com.backend.moamoa.domain.post.entity;

import com.backend.moamoa.global.audit.AuditListener;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditListener.class)
public class PostImage implements Auditable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "post_image_id")
    private Long id;

    private String imageUrl;

    private String storeFilename;

    @Embedded
    private TimeEntity timeEntity;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Builder
    public PostImage(String imageUrl, String storeFilename, Post post) {
        this.imageUrl = imageUrl;
        this.storeFilename = storeFilename;
        this.post = post;
    }

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }
}
