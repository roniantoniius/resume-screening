package com.roniantonius.resume_screening.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.roniantonius.resume_screening.models.ModelSettingEntity;
import com.roniantonius.resume_screening.models.SkorAtsResponse;
import com.roniantonius.resume_screening.services.impl.AiChatServiceImpl;
import com.roniantonius.resume_screening.services.impl.FilePdfServiceImpl;
import com.roniantonius.resume_screening.services.impl.ModelSettingServiceImpl;
import com.roniantonius.resume_screening.services.impl.SkorAtsServiceImpl;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping(path = "/api/v1/resume")
@RequiredArgsConstructor
public class ResumeController {
	
	@Autowired
	private final FilePdfServiceImpl pdfService;
	
	@Autowired
	private final SkorAtsServiceImpl skorAtsService;
	
	@Autowired
	private final ModelSettingServiceImpl modelSettingService;
	
	@PostMapping(value = "/analisis", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<SkorAtsResponse> analisisResume(@RequestParam("file") MultipartFile files){
		try {
			validasiFile(files);
			if (validasiPerformaChatClient()) {
				SkorAtsResponse skor = SkorAtsServiceImpl.buatSkorError("Layanan AI tidak berhasil, tolong konfigurasi API");
				return ResponseEntity.ok(skor);
			}
			String teksResume = pdfService.ekstrakTeksFromPdf(files);
			SkorAtsResponse skorAtsResponse = skorAtsService.hitungSkorAts(teksResume);
			return ResponseEntity.ok(skorAtsResponse);
		} catch (IOException e) {
			// TODO: handle exception
			return ResponseEntity.badRequest().build();
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			return ResponseEntity.badRequest().build();
		}
	}
	
	@PostMapping(value = "/analisis-dengan-pekerjaan", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<SkorAtsResponse> analisisResumeDenganLowongan(
			@RequestParam("file") MultipartFile files,
			@RequestParam("deskripsiPekerjaan") String deskripsiPekerjaan
	){
		try {
			validasiFile(files);
			if (validasiPerformaChatClient()) {
				SkorAtsResponse skor = SkorAtsServiceImpl.buatSkorError("Layanan AI tidak berhasil, tolong konfigurasi API");
				return ResponseEntity.ok(skor);
			}
			String teksResume = pdfService.ekstrakTeksFromPdf(files);
			SkorAtsResponse skorAtsResponse = skorAtsService.hitungSkorAts(teksResume, deskripsiPekerjaan);
			return ResponseEntity.ok(skorAtsResponse);
		} catch (IOException e) {
			// TODO: handle exception
			return ResponseEntity.badRequest().build();
		} catch (IllegalArgumentException e) {
			// TODO: handle exception
			return ResponseEntity.badRequest().build();
		}
	}
	
	private void validasiFile(MultipartFile files) {
		if (files.isEmpty()) {
			throw new IllegalArgumentException("Fillenya kosong");
		}
		
		String tipeKonten = files.getContentType();
		if (tipeKonten == null || !tipeKonten.equals("application/pdf")) {
			throw new IllegalArgumentException("Hanya file pdf yang diperbolehkam");
		}
	}
	
	private boolean validasiPerformaChatClient() {
		ModelSettingEntity settings = modelSettingService.getModelSettings();
		if (AiChatServiceImpl.getChatClient() == null && settings == null) {
			return true;
		}
		
		if (AiChatServiceImpl.getChatClient() == null) {
			AiChatServiceImpl.getInstance(settings);
		}
		
		return false;
	}
}