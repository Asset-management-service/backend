package com.backend.moamoa.domain.user.entity;

import com.backend.moamoa.global.audit.AuditListener;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Builder
public class UserMailAuth {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "mail_auth_id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    private String mail;

    private String authKey;

    // 만료 기간
    private LocalDateTime expirationDate;

    // 만료 여부
    @Builder.Default
    private boolean expired = false;

    // 토큰 사용으로 인한 만료
    public void useToken() {
        expired = true;
    }

}
