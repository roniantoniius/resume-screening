package com.roniantonius.resume_screening.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.roniantonius.resume_screening.models.ModelSettingEntity;
import com.roniantonius.resume_screening.services.impl.AiChatServiceImpl;
import com.roniantonius.resume_screening.services.impl.ModelSettingServiceImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class AiChatController {
	private final ModelSettingServiceImpl modelSetting;
	
	@GetMapping("/")
	public String home(Model model) {
		ModelSettingEntity settings = modelSetting.getModelSettings();
		model.addAttribute("settings", settings == null ? new ModelSettingEntity():settings);
		return "index";
	}
	
	// endpoint for configured temperature settings on ai
	@PostMapping("/settings")
	public String settings(
			@ModelAttribute ModelSettingEntity settings,
			RedirectAttributes attributes
	) {
		try {
			if (settings.getSamplingRate() == null) {
				settings.setSamplingRate(0.7);
			}
			
			String testRespon = AiChatServiceImpl.getInstance(settings).test();
			if (testRespon.equals("Sukses")) {
				modelSetting.save(settings);
				attributes.addFlashAttribute("successMessage", "Pengaturan Model berhasil disimpan");
			} else {
				attributes.addFlashAttribute("errorMessage", testRespon);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			attributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
		}
		
		return "redirect:/";
	}
	
	@ModelAttribute("requestUri")
	public String requestUri(HttpServletRequest request) {
		String skema = request.getScheme();
		String namaServer = request.getServerName();
		int portServer = request.getServerPort();
		String konteksPath = request.getContextPath();
		
		// construct url base
		String urlBase = skema + "://" + namaServer;
		
		// if port used wasnt default, we construct another port
		if((skema.equals("http") && portServer != 80) || (skema.equals("https") && portServer != 443)) {
			urlBase += ":" + portServer;
		}
		
		urlBase += konteksPath;
		return urlBase;
	}
}