package com.skillbridgebackend.skillBridge.backend.Dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TopFiveCoursesDto {
    private String courseName;
    private Long buyCount;
}
