package com.roniantonius.resume_screening.services.impl;

import org.springframework.stereotype.Service;

import com.roniantonius.resume_screening.models.ModelSettingEntity;
import com.roniantonius.resume_screening.models.SkorAtsResponse;
import com.roniantonius.resume_screening.services.SkorAtsService;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class SkorAtsServiceImpl implements SkorAtsService{
	
	private final ModelSettingServiceImpl modelSettingServiceImpl;
	
	// instance variabel untuk sistem promp dan tugas prompt
	private static final String SYSTEM_PROMPT =  """
			You're an privilaged ivy leagues HR with ATS (Applicant Tracking System) analyzer main ability. Right now your task is to:
			1. Evaluate the provided resume text against standarrd ATS criteria or a specific Job description if its provided.
			2. Score the resume on a scale of 1-100 based on ATS compatibility.
			3. You must provide specific feedback in the following categories:
				- Format and structure of resume (20 points)
				- Keywords and relevance (30 points)
				- Quantifiable achievements (20 points)
				- Skills and qualifications (30 points)
			4. List of specific strengths and weakness of that resume
			5. Give a effective Hack with actionable recommendations to improve ATS score as an Top HR.
			
			Return your analysis/evaluation in a valid JSON format with following structure:
			{
				"skorReview": [overall score 1-100],
				"rekomendasi": "[brief of best of the best recommendation]",
				"daftarKekuatan": ["kekuatan1", "kekuatan2", ...],
				"daftarKelemahan": ["kelemahan1", "kelemahan2", ...],
				"skorKategori": {
					"format": [score 1-20],
					"kataKunci": [score 1-30],
					"pencapaian": [score 1-20],
					"skills": [score 1-30]
				}
			}
			
			As an top HR with ATS analyzer ability, make your evaluation is comprehensive but it is concise.
			""";
	
	private static final String PROMPT_USER_TANPA_PEKERJAAN = """
			Please analyze this resume for ATS compatibility as an privilaged ivy leagues HR with ATS (Applicant Tracking System) analyzer main ability:
			
			{teksResume}
			""";
	
	private static final String PROMPT_USER_PEKERJAAN = """
			Please analyze this resume, included with job description, for ATS compatibility as an privilaged ivy leagues HR with ATS (Applicant Tracking System) analyzer main ability:
			
			JOB DESCRIPTION:
			{deskripsiPekerjaan}
			
			RESUME:
			{teksResume}
			""";
	
	
	
	@Override public SkorAtsResponse hitungSkorAts(String teksResume) {
		// TODO Auto-generated method stub
		return hitungSkorAts(teksResume, null);
	}

	@Override public SkorAtsResponse hitungSkorAts(String teksResume, String deksripsiPekerjaan) {
		// TODO Auto-generated method stub
		try {
			// fungsionalitas berkomunikasi dengan ChatClient melalui sistem prompt
			ModelSettingEntity modelSetting = modelSettingServiceImpl.getModelSettings();
			// later, i am sleepy
		} catch (Exception e) {
			// TODO: handle exception
			return buatSkorError("Gagal menganalisis resume, Tolong coba layanan ini kembali nanti");
		}
	}
	
	
	public static SkorAtsResponse buatSkorError(String pesanError) {
		Map<String, Integer> skorDefault = new HashMap<>();
		skorDefault.put("format", 0);
		skorDefault.put("kataKunci", 0);
		skorDefault.put("pencapaian", 0);
		skorDefault.put("skills", 0);
		
		return SkorAtsResponse.builder()
				.skorReview(0)
				.rekomendasi(pesanError)
				.daftarKekuatan(Collections.emptyList())
				.daftarKelemahan(Collections.singletonList("Analisis terjadi error"))
				.skorKategori(skorDefault)
				.build();
	}
}
