package com.roniantonius.resume_screening.services.impl;

import com.roniantonius.resume_screening.models.ModelSettingEntity;
import com.roniantonius.resume_screening.repositories.ModelSettingRepository;
import com.roniantonius.resume_screening.services.ModelSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ModelSettingServiceImpl implements ModelSettingService {

    private final ModelSettingRepository modelSettingRepository;

    @Override
    public ModelSettingEntity getModelSettings() {
        return modelSettingRepository.findById(1);
    }

    @Override
    public void save(ModelSettingEntity modelSetting) {
        modelSetting.setId(1); // idk why this method wont work in intelij
        modelSettingRepository.save(modelSetting);
    }
}