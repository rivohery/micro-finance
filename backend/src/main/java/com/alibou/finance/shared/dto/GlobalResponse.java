package com.alibou.finance.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GlobalResponse {
    private LocalDateTime timestamps;
    private int status;
    private String message;
    private Map<String,Object> data;
}
