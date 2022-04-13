package com.backend.moamoa.domain.asset.entity;

import com.backend.moamoa.domain.user.entity.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.*;
import static javax.persistence.GenerationType.*;
import static lombok.AccessLevel.*;

//@Entity
@Getter
@NoArgsConstructor(access = PROTECTED)
public class Asset {

//    @Id
//    @GeneratedValue(strategy = IDENTITY)
    @Column(name = "asset_setting_id")
    private Long id;

//    @Column(nullable = false)
    private Integer budget;

//    @OneToMany(mappedBy = "assetSetting")
    private List<AssetCategory> assetCategories = new ArrayList<>();

//    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "user_id")
    private User user;

}
