package com.roniantonius.resume_screening.services.impl;

import java.io.IOException;

import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.roniantonius.resume_screening.services.FilePdfService;

import groovy.util.logging.Slf4j;

@Service
@Slf4j
public class FilePdfServiceImpl implements FilePdfService{
	
	// functionality to extract document into string that contain several rows of string

	@Override public String ekstrakTeksFromPdf(MultipartFile filePdf) throws IOException {
		// TODO Auto-generated method stub
		PagePdfDocumentReader dokumen = new PagePdfDocumentReader(filePdf.getResource(), 
				PdfDocumentReaderConfig
					.builder()
					.withPageTopMargin(0)
					.withPageExtractedTextFormatter(
							ExtractedTextFormatter
								.builder()
								.withNumberOfTopTextLinesToDelete(0)
								.build()
					)
					.withPagesPerDocument(1)
					.build()
		);
		return dokumen.read().toString();
	}
}
