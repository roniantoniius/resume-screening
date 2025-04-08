package com.roniantonius.resume_screening.services;

import com.roniantonius.resume_screening.models.ModelSettingEntity;

public interface ModelSettingService {
    ModelSettingEntity getModelSettings();
    void save(ModelSettingEntity modelSetting);
}