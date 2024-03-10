package com.kotkina.userapplicationssystem.services.external;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kotkina.userapplicationssystem.exceptions.PhoneNotVerifiedException;
import com.kotkina.userapplicationssystem.services.VerificationService;
import com.kotkina.userapplicationssystem.web.models.response.VerifiedDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DadataVerificationServiceImpl implements VerificationService {

    @Value("${dadata.client.token}")
    private String token;

    @Value("${dadata.client.secret}")
    private String secret;

    private final DadataFeignClient dadataFeignClient;

    ObjectMapper mapper = new ObjectMapper();

    public List<VerifiedDataResponse> getVerifiedPhone(String phone) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", MediaType.APPLICATION_JSON_VALUE);
        headers.put("Accept", MediaType.APPLICATION_JSON_VALUE);
        headers.put("Authorization", "Token " + token);
        headers.put("X-Secret", secret);

        String response = dadataFeignClient.checkPhone(headers, MessageFormat.format("[\"{0}\"]", phone));
        List<VerifiedDataResponse> dadataResponse;

        try {
            dadataResponse = mapper.readValue(response, new TypeReference<>() {
            });
        } catch (JsonProcessingException e) {
            throw new PhoneNotVerifiedException("Ошибка верификации номера телефона: " + e.getMessage());
        }

        return dadataResponse.stream()
                .filter(r -> "Мобильный".equals(r.getType()) && (r.getQc() == 0 || r.getQc() == 7))
                .toList();
    }
}
