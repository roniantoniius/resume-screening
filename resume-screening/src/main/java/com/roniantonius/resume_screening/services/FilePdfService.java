package com.roniantonius.resume_screening.services;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface FilePdfService {
	String ekstrakTeksFromPdf(MultipartFile filePdf) throws IOException;
}
