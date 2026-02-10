package com.utkarsh.journalApp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmailLog {
    private LocalDateTime timestamp;
    private String sentiment;
    private String emailAddress;
}
