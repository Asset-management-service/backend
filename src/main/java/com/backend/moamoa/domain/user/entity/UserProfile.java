package com.backend.moamoa.domain.user.entity;

import com.backend.moamoa.domain.user.enums.Address;
import com.backend.moamoa.domain.user.enums.Gender;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

@Getter
@Entity
@NoArgsConstructor(access = PROTECTED)
public class UserProfile {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "user_profile_id")
    private Long id;

    private String nickname;

    private LocalDateTime birthday;

    private Gender gender;

    @Enumerated(EnumType.STRING)
    private Address address;

    private String phoneNumber;

    private String provider;

    private String providerId;

    @OneToOne(mappedBy ="userProfile")
    private User user;

}
