package com.trackswiftly.client_service.dtos;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PoiResponse {

    private Long id;
    private String name;
    private GroupResponse group;
    private PoiTypeResponse poiTypeResponse;
    private String address;
    private BigDecimal latitude;
    private BigDecimal longitude;
    private Map<String, Object> payload;
    private Instant createdAt;
    private Instant updatedAt;
}
