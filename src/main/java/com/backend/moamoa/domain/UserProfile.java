package com.backend.moamoa.domain;

import com.backend.moamoa.domain.enums.Address;
import com.backend.moamoa.domain.enums.Gender;
import lombok.AccessLevel;
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

    private Address address;

    private String phoneNumber;

    private String provider;

    private String providerId;


}
