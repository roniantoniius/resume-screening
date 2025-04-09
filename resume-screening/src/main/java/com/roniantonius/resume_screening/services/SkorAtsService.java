package com.roniantonius.resume_screening.services;

import com.roniantonius.resume_screening.models.SkorAtsResponse;

public interface SkorAtsService {
	SkorAtsResponse hitungSkorAts(String teksResume);
	SkorAtsResponse hitungSkorAts(String teksResume, String deksripsiPekerjaan);
}
