package com.skillbridgebackend.skillBridge.backend.Service.impl;

import com.skillbridgebackend.skillBridge.backend.Dto.KafkaProducerDto;
import com.skillbridgebackend.skillBridge.backend.Entity.Courses;
import com.skillbridgebackend.skillBridge.backend.Entity.ESEntities.FeedbackES;
import com.skillbridgebackend.skillBridge.backend.Entity.Feedback;
import com.skillbridgebackend.skillBridge.backend.Repository.CoursesRepository;
import com.skillbridgebackend.skillBridge.backend.Repository.ESRepository.CoursesESRepository;
import com.skillbridgebackend.skillBridge.backend.Repository.ESRepository.FeedbackESRepository;
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
    private FeedbackESRepository feedbackESRepository;

    @Autowired
    private CoursesRepository coursesRepository;

    @Autowired
    private CoursesESRepository coursesESRepository;

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
//
//        coursesESRepository.findById(kafkaProducerDto.getCourseId())
//                .orElseThrow(()-> new RuntimeException("course not found in elasticsearch"));
//
//        FeedbackES feedbackES = new FeedbackES();
//        try {
//            feedbackES.setId(kafkaProducerDto.getFeedbackDto().getId());
//            feedbackES.setEmail(kafkaProducerDto.getFeedbackDto().getEmail());
//            feedbackES.setBody(kafkaProducerDto.getFeedbackDto().getBody());
//            feedbackES.setCourses(courses);
//
//            feedbackESRepository.save(feedbackES);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//
//        System.out.println("feedback saved in elasticsearch"+feedbackES);
        System.out.println("feedback is "+feedback);
    }
}
