package com.skillbridgebackend.skillBridge.backend.Entity.ESEntities;
import com.skillbridgebackend.skillBridge.backend.Entity.Feedback;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(indexName = "courses")
public class CoursesES {

    @Id
    private Long id;

    private String courseName;

    private double coursePrice;

    private String description;

    private String courseContent;

    private Set<FeedbackES> feedbacks = new HashSet<>();
}
