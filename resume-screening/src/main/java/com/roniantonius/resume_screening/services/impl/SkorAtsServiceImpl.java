package com.roniantonius.resume_screening.services.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

import com.roniantonius.resume_screening.models.ModelSettingEntity;
import com.roniantonius.resume_screening.models.SkorAtsResponse;
import com.roniantonius.resume_screening.services.SkorAtsService;

import groovy.util.logging.Slf4j;
import lombok.RequiredArgsConstructor;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			if (AiChatServiceImpl.getChatClient() == null && modelSetting!=null) {
				AiChatServiceImpl.getInstance(modelSetting);
			}
			
			ChatClient chatClient = AiChatServiceImpl.getChatClient();
			if(chatClient == null) {
				return buatSkorError("Servis AI belum dikonfigurasi, perhatikan API AI lebih lanjut");
			}
			
			String promptUserTemplate;
			if (deksripsiPekerjaan != null && !deksripsiPekerjaan.isEmpty()) {
				promptUserTemplate = PROMPT_USER_PEKERJAAN;
				promptUserTemplate = promptUserTemplate.replace("{deskripsiPekerjaan}", deksripsiPekerjaan);
			} else {
				promptUserTemplate = PROMPT_USER_TANPA_PEKERJAAN;
			}
			
			promptUserTemplate.replace("{teksResume}", teksResume);
			
			String promptUserFinal = promptUserTemplate;
			String konten = chatClient.prompt()
					.user(pengguna -> pengguna.text(promptUserFinal))
					.system(SYSTEM_PROMPT)
					.stream()
					.content()
					.collectList()
					.map(daftar -> String.join("" , daftar))
					.block();
			return olahRespons(konten);
			
		} catch (Exception e) {
			// TODO: handle exception
			return buatSkorError("Gagal menganalisis resume, Tolong coba layanan ini kembali nanti");
		}
	}
	
	
	private SkorAtsResponse olahRespons(String kontenJsonResponse) {
		// menggunakan string manip to return an Ats skor reponse obj
		try {
			// but obviously we should json parser built in like jackson
			// why? cuz its not that efficient, we need to convert into string first, more object
			int skor = ekstrakNilaiInt(kontenJsonResponse, "skorReview");
			
			String rekomendasi = ekstrakRekomendasi(kontenJsonResponse, "rekomendasi");
			
			List<String> kekuatans = ekstrakListString(kontenJsonResponse, "daftarKekuatan");
			List<String> kelemahans = ekstrakListString(kontenJsonResponse, "daftarKelemahan");
			
			Map<String, Integer> skorKategori = new HashMap<>();
			skorKategori.put("format", ekstrakIntLapisanOutput(kontenJsonResponse, "skorKategori", "format"));
			skorKategori.put("kataKunci", ekstrakIntLapisanOutput(kontenJsonResponse, "skorKategori", "kataKunci"));
			skorKategori.put("pencapaian", ekstrakIntLapisanOutput(kontenJsonResponse, "skorKategori", "pencapaian"));
			skorKategori.put("skills", ekstrakIntLapisanOutput(kontenJsonResponse, "skorKategori", "skills"));
			
			// return build
			return SkorAtsResponse.builder()
					.skorReview(skor)
					.rekomendasi(rekomendasi)
					.daftarKekuatan(kekuatans)
					.daftarKelemahan(kelemahans)
					.skorKategori(skorKategori)
					.build();
			
		} catch (Exception e) {
			// TODO: handle exception
			return buatSkorError("Tidak bisa mengolah hasil dari resume, tolong coba lain waktu");
		}
	}
	
	private int ekstrakNilaiInt(String jsonResponse, String keyVar) {
		String pola = "\"" + keyVar + "\"\\s*:\\s*(\\d+)";
		Pattern polaRegex = Pattern.compile(pola);
		Matcher matcher = polaRegex.matcher(jsonResponse);
		if (matcher.find()) {
			return Integer.parseInt(matcher.group(1)); // ubah str dari var ke int
		}
		return 0;
	}
	
	private String ekstrakRekomendasi(String jsonResponse, String keyVar) {
		String pola = "\"" + keyVar + "\"\\s*:\\s*\"([^\"]*)\"";
		Pattern polaRegex = Pattern.compile(pola);
		Matcher matcher = polaRegex.matcher(jsonResponse);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return "";
	}
	
	private List<String> ekstrakListString(String jsonResponse, String keyVar) {
		List<String> hasils = new ArrayList<>();
		String polaDaftar = "\"" + keyVar + "\"\\s*:\\s*\\[(.*?)\\]";
		Pattern polaRegex = Pattern.compile(polaDaftar, Pattern.DOTALL);
		Matcher matcherRegex = polaRegex.matcher(jsonResponse);
		
		if (matcherRegex.find()) {
			String daftarKonten = matcherRegex.group(1);
			String polaKontenRegex = "\"([^\"]*)\"";
			Pattern pola = Pattern.compile(polaKontenRegex);
			Matcher matcher = pola.matcher(daftarKonten);
			
			while (matcher.find()) {
				hasils.add(matcher.group(1));
			}
		}
		return hasils;
	}
	
	private int ekstrakIntLapisanOutput(String jsonResponse, String keyParent, String keyChild) {
		String polaBerkelanjutan = "\"" + keyParent + "\"\\s*:\\s*\\{[^}]*\"" + keyChild + "\"\\s*:\\s*(\\d+)";
		Pattern pola = Pattern.compile(polaBerkelanjutan);
		Matcher matcher = pola.matcher(jsonResponse);
		if (matcher.find()) {
			return Integer.parseInt(matcher.group(1));
		}
		return 0;
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