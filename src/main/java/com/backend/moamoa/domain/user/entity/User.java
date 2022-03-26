package com.backend.moamoa.domain.user.entity;

import com.backend.moamoa.domain.comment.entity.Comment;
import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.global.audit.AuditListener;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
@EntityListeners(AuditListener.class)
public class User implements Auditable {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    @Embedded
    private TimeEntity timeEntity;

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Post> posts = new ArrayList<>();

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL)
    private List<Comment> comments = new ArrayList<>();


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userProfile_id")
    private UserProfile userProfile;


}
