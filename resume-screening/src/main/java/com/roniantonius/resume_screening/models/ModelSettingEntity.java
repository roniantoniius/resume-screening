package com.roniantonius.resume_screening.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "model_settings")
@Entity
@Builder
public class ModelSettingEntity {

    @Id
    private int id = 1;

    @Column(name = "baseUrl")
    private String baseUrl;

    @Column(name = "apiKey")
    private String apiKey;

    @Column(name = "model")
    private String model;

    @Column(name = "samplingRate")
    private Double samplingRate;
}