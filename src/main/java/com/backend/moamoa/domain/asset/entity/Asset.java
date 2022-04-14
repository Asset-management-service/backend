package com.backend.moamoa.domain.asset.entity;

import com.backend.moamoa.domain.user.entity.User;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;
import static javax.persistence.GenerationType.IDENTITY;
import static lombok.AccessLevel.PROTECTED;

@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Asset {

    @Id
    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "asset_setting_id")
    private Long id;

    @Column(nullable = false)
    private Integer budget;

    @OneToMany(mappedBy = "asset")
    private List<AssetCategory> assetCategories = new ArrayList<>();

    //지출 비율
    @Embedded
    private ExpenditureRatio expenditureRatio;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
