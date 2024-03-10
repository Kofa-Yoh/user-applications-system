package com.kotkina.userapplicationssystem.services;

import com.kotkina.userapplicationssystem.web.models.response.VerifiedDataResponse;

import java.util.List;

public interface VerificationService {

    List<VerifiedDataResponse> getVerifiedPhone(String phone);
}
