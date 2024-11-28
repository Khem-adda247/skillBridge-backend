package com.skillbridgebackend.skillBridge.backend.Service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillbridgebackend.skillBridge.backend.Dto.BuyCourseDto;
import com.skillbridgebackend.skillBridge.backend.Dto.CoursesDto;
import com.skillbridgebackend.skillBridge.backend.Dto.TopFiveCoursesDto;
import com.skillbridgebackend.skillBridge.backend.Entity.BuyCourse;
import com.skillbridgebackend.skillBridge.backend.Entity.Courses;
import com.skillbridgebackend.skillBridge.backend.Entity.User;
import com.skillbridgebackend.skillBridge.backend.Repository.BuyCourseRepository;
import com.skillbridgebackend.skillBridge.backend.Repository.CoursesRepository;
import com.skillbridgebackend.skillBridge.backend.Repository.UserRepository;
import com.skillbridgebackend.skillBridge.backend.Service.BuyCourseService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class BuyCourseImpl implements BuyCourseService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CoursesRepository coursesRepository;
    @Autowired
    private BuyCourseRepository buyCourseRepository;

    @Autowired
    private ModelMapper mapper;




    private final RedisTemplate<String, String> redisTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    public BuyCourseImpl(UserRepository userRepository,
                         CoursesRepository coursesRepository,
                         BuyCourseRepository buyCourseRepository, ModelMapper mapper
            ,RedisTemplate<String, String> redisTemplate) {
        this.userRepository = userRepository;
        this.coursesRepository = coursesRepository;
        this.buyCourseRepository = buyCourseRepository;
        this.mapper = mapper;
        this.redisTemplate = redisTemplate;
    }


    //buy course method
    @Override
    public BuyCourse buyCourse(Long userId, Long courseId) {
        Optional<User> user = userRepository.findById(userId);
        Optional<Courses> courses = coursesRepository.findById(courseId);

        if (user.isEmpty()){
            throw new RuntimeException("user with this id does not found");
        }

        if(courses.isEmpty()){
            throw new RuntimeException("course with this id does not found");
        }

        Optional<BuyCourse> alreadyBuy = buyCourseRepository.findByUserIdAndCourseId(userId, courseId);

        BuyCourse buy;
        if (alreadyBuy.isPresent()){
            throw new RuntimeException("This course is already purchased by you, you can go and watch it");
        }else {
            buy = new BuyCourse();
            buy.setCourses(courses.get());
            buy.setUser(user.get());
            buy.setCourseName(courses.get().getCourseName());
            buy.setBuyCount(1L);
        }
        return buyCourseRepository.save(buy);
    }



    @Override
    public List<BuyCourseDto> getAllPurchasedCourse() {
        List<BuyCourse> buyCourseList = new ArrayList<>(buyCourseRepository.findAll());

        List<BuyCourseDto> buyCourseDtoList = buyCourseList.stream().map(buyCourse -> mapToDto(buyCourse)).toList();

        return buyCourseDtoList;
    }



    @Override
    public List<TopFiveCoursesDto> getTopFivePurchasedCourses() throws JsonProcessingException {
        //fetching from redis
        String key = "topFiveCourses";
        String topCourses = redisTemplate.opsForValue().get(key);

        // If the cache is not empty, deserialize and return the cached data
        if (topCourses != null && !topCourses.isEmpty()) {
            List<TopFiveCoursesDto> cachedTopFiveCourses = objectMapper.readValue(topCourses, objectMapper.getTypeFactory().constructCollectionType(List.class, TopFiveCoursesDto.class));
            return cachedTopFiveCourses;
        }

        //fetching from db
        List<BuyCourse> buyCourses = buyCourseRepository.findAll();

        // Aggregate buy counts by course name
        Map<String, Long> courseBuyCounts = new HashMap<>();

        for (BuyCourse buyCourse : buyCourses) {
            courseBuyCounts.put(buyCourse.getCourseName(),
                    courseBuyCounts.getOrDefault(buyCourse.getCourseName(), 0L) + buyCourse.getBuyCount());
        }

        List<TopFiveCoursesDto> topFiveCourses = courseBuyCounts.entrySet().stream()
                .map(entry -> new TopFiveCoursesDto(entry.getKey(), entry.getValue()))
                .sorted((c1, c2) -> Long.compare(c2.getBuyCount(), c1.getBuyCount())) // Sort by buyCount DESC
                .limit(5) // Get top 5 courses
                .collect(Collectors.toList());

        redisTemplate.opsForValue().set(key, objectMapper.writeValueAsString(topFiveCourses), 120, TimeUnit.SECONDS);

        return topFiveCourses;
    }




    private BuyCourse mapToEntity(BuyCourseDto buyCourseDto){
        BuyCourse buyCourse = mapper.map(buyCourseDto, BuyCourse.class);
        return buyCourse;
    }

    //creating private method for converting entity to dto to use it again and again
    private BuyCourseDto mapToDto(BuyCourse buyCourse){
        //mapping using modelmapper maven
        BuyCourseDto buyCourseDto = mapper.map(buyCourse, BuyCourseDto.class);
        return buyCourseDto;
    }
}
