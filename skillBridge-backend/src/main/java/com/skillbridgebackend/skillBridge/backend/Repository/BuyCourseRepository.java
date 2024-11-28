package com.skillbridgebackend.skillBridge.backend.Repository;

import com.skillbridgebackend.skillBridge.backend.Dto.BuyCourseDto;
import com.skillbridgebackend.skillBridge.backend.Dto.TopFiveCoursesDto;
import com.skillbridgebackend.skillBridge.backend.Entity.BuyCourse;
import com.skillbridgebackend.skillBridge.backend.Entity.Courses;
import com.skillbridgebackend.skillBridge.backend.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface BuyCourseRepository extends JpaRepository<BuyCourse, Long> {
    @Query(value = "select * from purchased_courses bc where bc.user_id =:userId AND bc.course_id =:courseId", nativeQuery = true)
    Optional<BuyCourse> findByUserIdAndCourseId(Long userId, Long courseId);

}
