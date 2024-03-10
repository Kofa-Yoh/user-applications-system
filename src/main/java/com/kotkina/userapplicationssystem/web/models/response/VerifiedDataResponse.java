package com.kotkina.userapplicationssystem.web.models.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class VerifiedDataResponse {

    private String type;
    private String phone;
    @JsonProperty("country_code")
    private Integer countryCode;
    @JsonProperty("city_code")
    private Integer cityCode;
    private String number;
    private Byte qc;
}
