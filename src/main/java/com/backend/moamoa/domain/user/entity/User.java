package com.backend.moamoa.domain.user.entity;

import com.backend.moamoa.domain.post.entity.Post;
import com.backend.moamoa.domain.user.dto.request.UserUpdateRequest;
import com.backend.moamoa.domain.user.entity.enums.Gender;
import com.backend.moamoa.domain.user.oauth.entity.enums.ProviderType;
import com.backend.moamoa.global.audit.AuditListener;
import com.backend.moamoa.global.audit.Auditable;
import com.backend.moamoa.global.audit.TimeEntity;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.EnumType.STRING;

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

    @NotNull
    @Column(name = "user_provider_id")
    private String userId;

    @OneToMany(mappedBy = "user")
    private List<Post> posts = new ArrayList<>();

    @Enumerated(STRING)
    @NotNull
    private ProviderType providerType;

    private String email;

    @Builder.Default
    private boolean emailCheck = false;

    @NotNull
    private String nickname;

    private String phoneNum;

    private String birthday;

    private String birthYear;

    @Enumerated(STRING)
    private Gender gender;

    @Builder.Default
    private boolean deleted = false;

    @Embedded
    private TimeEntity timeEntity;

    @Override
    public void setTimeEntity(TimeEntity timeEntity) {
        this.timeEntity = timeEntity;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public void update(UserUpdateRequest userUpdateRequest) {
        this.email = userUpdateRequest.getEmail();
        this.nickname = userUpdateRequest.getNickname();
        this.phoneNum = userUpdateRequest.getPhoneNum();
        this.gender = userUpdateRequest.getGender();
    }

    public void updateEmail(String email) {
        this.email = email;
        emailCheck = true;
    }

}
