package com.backend.moamoa.domain.user.entity;

import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.user.oauth.entity.ProviderType;
import com.backend.moamoa.global.audit.AuditListener;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditListener.class)
@AllArgsConstructor
@Entity
@Builder
public class User implements Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

//    @NotNull
    @Column(name = "user_provider_id")
    private String userId;

//    @Enumerated(EnumType.STRING)
//    @NotNull
    private ProviderType providerType;

    private String email;


    private String password = "";

    @NotNull
    private String nickname;

    private String birthday;

    private String birthYear;

    @Builder.Default
    private String gender = "";

    @Embedded
    private TimeEntity timeEntity;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    private List<Post> posts = new ArrayList<>();

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

}
