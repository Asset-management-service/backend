package com.backend.moamoa.domain.scrap.repository;

import com.backend.moamoa.domain.scrap.entity.Scrap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ScrapRepository extends JpaRepository<Scrap,Long> {
}
