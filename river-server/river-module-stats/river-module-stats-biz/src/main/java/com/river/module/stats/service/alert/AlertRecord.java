package com.river.module.stats.service.alert;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AlertRecord {

    private Long id;
    
    private AlertService.AlertType type;
    
    private AlertService.AlertLevel level;
    
    private String message;
    
    private String details;
    
    private Boolean resolved;
    
    private LocalDateTime createdAt;
    
    private LocalDateTime resolvedAt;

}
