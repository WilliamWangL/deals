package com.river.module.stats.service.alert;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertRecord {

    private Long id;
    
    private AlertType type;
    
    private AlertLevel level;
    
    private String message;
    
    private String details;
    
    private Boolean resolved;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime resolvedAt;

    public enum AlertType {
        ROI_DROP,
        UNATTRIBUTED_CONVERSION,
        HIGH_FREQUENCY_CLICK,
        POSTBACK_FAILURE,
        CONVERSION_DROP,
        SYSTEM_ERROR
    }

    public enum AlertLevel {
        CRITICAL,
        WARNING,
        INFO
    }

}
