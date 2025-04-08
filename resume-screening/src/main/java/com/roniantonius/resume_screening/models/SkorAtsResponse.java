package com.roniantonius.resume_screening.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SkorAtsResponse {
    private int skorReview;
    private String rekomendasi;
    private List<String> daftarKekuatan;
    private List<String> daftarKelemahan;
    private Map<String, Integer> skorKategori;
}