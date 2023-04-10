package com.sunzy.vulfocus.model.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SysLogDTO {
    private String username;
    private String operationType;
    private String operationName;
    private String operationValue;
    private String operationArgs;
    private String ip;
    private LocalDateTime createdDate;
}
