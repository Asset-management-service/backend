package com.backend.moamoa.domain.asset.repository;

import com.backend.moamoa.domain.asset.entity.Asset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssetRepository extends JpaRepository<Asset, Long> {
}
