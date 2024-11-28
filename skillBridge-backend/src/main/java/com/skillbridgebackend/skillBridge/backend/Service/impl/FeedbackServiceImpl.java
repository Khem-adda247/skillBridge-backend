package com.skillbridgebackend.skillBridge.backend.Service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridgebackend.skillBridge.backend.Dto.FeedbackDto;
import com.skillbridgebackend.skillBridge.backend.Dto.KafkaProducerDto;
import com.skillbridgebackend.skillBridge.backend.Entity.Courses;
import com.skillbridgebackend.skillBridge.backend.Entity.Feedback;
import com.skillbridgebackend.skillBridge.backend.Exception.ResourceNotFoundException;
import com.skillbridgebackend.skillBridge.backend.Exception.SkillBridgeAPIException;
import com.skillbridgebackend.skillBridge.backend.Repository.CoursesRepository;
import com.skillbridgebackend.skillBridge.backend.Repository.FeedbackRepository;
import com.skillbridgebackend.skillBridge.backend.Service.FeedbackService;
import com.skillbridgebackend.skillBridge.backend.Utils.AppConstants;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class FeedbackServiceImpl implements FeedbackService {

    private FeedbackRepository feedbackRepository;
    private CoursesRepository coursesRepository;

    @Autowired
    private KafkaTemplate<String, KafkaProducerDto> kafkaTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    private ModelMapper mapper;

    public FeedbackServiceImpl(FeedbackRepository feedbackRepository, CoursesRepository coursesRepository, ModelMapper mapper) {
        this.feedbackRepository = feedbackRepository;
        this.coursesRepository = coursesRepository;
        this.mapper = mapper;
    }

    @Override
    public FeedbackDto addFeedback(long courseId, FeedbackDto feedbackDto) {
//
//        //convert dto to entity
//        Feedback feedback = mapToEntity(feedbackDto);
//        //retrieve course entity by id so that we can set that course to the feeback
//        Courses courses = coursesRepository.findById(courseId)
//                .orElseThrow(()->new ResourceNotFoundException("Courses", "id", courseId));
//        //set course to feedback
//        feedback.setCourses(courses);
////
////        //save feedbacks into db as an entity
//        Feedback newFeedback = feedbackRepository.save(feedback);
////
////        //convert entity to dto as we want the response as dto
//        return mapToDto(newFeedback);

        KafkaProducerDto kafkaProducerDto = new KafkaProducerDto(courseId, feedbackDto);
//        KafkaProducerDto kafkaProducerDto = new KafkaProducerDto();
//        kafkaProducerDto.setCourseId(courseId);
//        kafkaProducerDto.setFeedbackDto(feedbackDto);
        try {
            kafkaTemplate.send("course-feedback", kafkaProducerDto);
        }catch (Exception e){
            System.out.println("error while sending the object to kafka"+e);
        }

        return null;

    }

    @Override
    public List<FeedbackDto> getFeedbackByCourseId(long courseId) {
        Courses course = coursesRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Courses", "id", courseId));
        List<Feedback> feedbacks = feedbackRepository.findByCourses(course);

        return feedbacks.stream().map(feedback -> mapToDto(feedback)).collect(Collectors.toList());
    }

    @Override
    public FeedbackDto getFeedbackById(long courseId, long feedbackId) {
        Courses courses = coursesRepository.findById(courseId)
                .orElseThrow(()->new ResourceNotFoundException("Courses", "id", courseId));

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(()-> new ResourceNotFoundException("Feedback", "id", feedbackId));

        if(!feedback.getCourses().getId().equals(courses.getId())){
            throw new SkillBridgeAPIException(HttpStatus.BAD_REQUEST, "feedback does not belong to the course");
        }

        return mapToDto(feedback);
    }

    @Override
    public FeedbackDto updateFeedback(long courseId, long feedbackId, FeedbackDto feedbackDto) {

        Courses courses = coursesRepository.findById(courseId)
                .orElseThrow(()->new ResourceNotFoundException("Courses", "id", courseId));

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() ->new ResourceNotFoundException("Feedback", "id", feedbackId));

        if(!feedback.getCourses().getId().equals(courses.getId())){
            throw new SkillBridgeAPIException(HttpStatus.BAD_REQUEST, "feedback does not belong to the course");
        }

        feedback.setName(feedbackDto.getName());
        feedback.setEmail(feedbackDto.getEmail());
        feedback.setBody(feedbackDto.getBody());

        Feedback updatedFeedback = feedbackRepository.save(feedback);

        return mapToDto(updatedFeedback);
    }

    @Override
    public void deleteFeedback(long courseId, long feedbackId) {
        Courses courses = coursesRepository.findById(courseId)
                .orElseThrow(()-> new ResourceNotFoundException("Courses", "id", courseId));

        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new ResourceNotFoundException("Feedback", "id", feedbackId));

        if (!feedback.getCourses().getId().equals(courses.getId())){
            throw new SkillBridgeAPIException(HttpStatus.BAD_REQUEST, "feedback does not belong to the Course");
        }

        feedbackRepository.delete(feedback);
    }


    private Feedback mapToEntity(FeedbackDto feedbackDto){

        Feedback feedback = mapper.map(feedbackDto, Feedback.class);

//        Feedback feedback = new Feedback();
//        feedback.setName(feedbackDto.getName());
//        feedback.setEmail(feedbackDto.getEmail());
//        feedback.setBody(feedbackDto.getBody());

        return feedback;
    }
    private FeedbackDto mapToDto(Feedback feedback){
        FeedbackDto feedbackDto = mapper.map(feedback, FeedbackDto.class);
//        FeedbackDto feedbackDto = new FeedbackDto();
//        feedbackDto.setId(feedback.getId());
//        feedbackDto.setName(feedback.getName());
//        feedbackDto.setEmail(feedback.getEmail());
//        feedbackDto.setBody(feedback.getBody());

        return feedbackDto;
    }
}
