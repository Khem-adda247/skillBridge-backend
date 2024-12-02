package com.skillbridgebackend.skillBridge.backend.Entity.ESEntities;


import com.skillbridgebackend.skillBridge.backend.Entity.Courses;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "feedbacks")
public class FeedbackES {
    @Id
    private Long id;

    private String name;
    private String email;
    private String body;

    private CoursesES courseId  ;
}
