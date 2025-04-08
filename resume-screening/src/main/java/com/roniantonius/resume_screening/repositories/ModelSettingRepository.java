package com.roniantonius.resume_screening.repositories;

import com.roniantonius.resume_screening.models.ModelSettingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModelSettingRepository extends JpaRepository<ModelSettingEntity, Integer> {
    ModelSettingEntity findById(int id);
}