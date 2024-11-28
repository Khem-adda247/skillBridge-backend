package com.skillbridgebackend.skillBridge.backend.Dto;

import com.skillbridgebackend.skillBridge.backend.Entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KafkaProducerDto implements Serializable {
    Long courseId;
    FeedbackDto feedbackDto;
}
