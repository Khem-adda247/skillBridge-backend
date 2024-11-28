package com.skillbridgebackend.skillBridge.backend.Service.impl;

import com.skillbridgebackend.skillBridge.backend.Dto.KafkaProducerDto;
import com.skillbridgebackend.skillBridge.backend.Entity.Courses;
import com.skillbridgebackend.skillBridge.backend.Entity.Feedback;
import com.skillbridgebackend.skillBridge.backend.Repository.CoursesRepository;
import com.skillbridgebackend.skillBridge.backend.Repository.FeedbackRepository;
import com.skillbridgebackend.skillBridge.backend.Utils.AppConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class FeedbackConsumer {
    @Autowired
    private FeedbackRepository feedbackRepository;

    @Autowired
    private CoursesRepository coursesRepository;

    @KafkaListener(containerFactory = "kafkaListenerContainerFactory", topics = "course-feedback", groupId = AppConstants.Group_Id)
    public void feedbackListener(KafkaProducerDto kafkaProducerDto){

        Courses courses = coursesRepository.findById(kafkaProducerDto.getCourseId())
                .orElseThrow(()-> new RuntimeException("course not found"));

        Feedback feedback = new Feedback();
        feedback.setName(kafkaProducerDto.getFeedbackDto().getName());
        feedback.setEmail(kafkaProducerDto.getFeedbackDto().getEmail());
        feedback.setBody(kafkaProducerDto.getFeedbackDto().getBody());
        feedback.setCourses(courses);

        feedbackRepository.save(feedback);

        System.out.println("feedback is "+feedback);
    }
}
