package com.roniantonius.resume_screening.services.impl;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.stereotype.Service;

import com.roniantonius.resume_screening.models.ModelSettingEntity;
import com.roniantonius.resume_screening.services.AiChatService;


@Service
public class AiChatServiceImpl implements AiChatService{
	private static ChatClient chatClient;
	private static AiChatServiceImpl aiChatService;

	private AiChatServiceImpl() {}
	
	public static AiChatService getInstance(ModelSettingEntity modelSetting) {
		// TODO Auto-generated method stub
		if (aiChatService == null) {
			aiChatService = new AiChatServiceImpl();
		}
		chatClient = aiChatService.updateAiChat(modelSetting);
		return aiChatService;
	}
	
	@Override
	public String test() {
		// TODO Auto-generated method stub
		return null;
	}
	
	private ChatClient updateAiChat(ModelSettingEntity modelSetting) {
		try {
			OpenAiApi openAiApi = OpenAiApi.builder()
					.baseUrl(modelSetting.getBaseUrl())
					.apiKey(modelSetting.getApiKey())
					.build();
			
			OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
					.model(modelSetting.getModel())
					.temperature(modelSetting.getSamplingRate())
					.build();
			
			OpenAiChatModel openAiChatModel = OpenAiChatModel.builder()
					.openAiApi(openAiApi)
					.defaultOptions(openAiChatOptions)
					.build();
			return ChatClient.create(openAiChatModel);
		} catch (Exception e) {
			// TODO: handle exception
			throw new IllegalStateException("Gagal membuat ChatClient objek: " + e.getMessage());
		}
	}
}
