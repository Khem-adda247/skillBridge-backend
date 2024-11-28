package com.skillbridgebackend.skillBridge.backend.Dto;

import lombok.Data;

@Data
public class BuyCourseDto {

    private Long id;
    private String courseName;
    private Long buyCount;
    private Long courseId;
    private Long userId;

}
