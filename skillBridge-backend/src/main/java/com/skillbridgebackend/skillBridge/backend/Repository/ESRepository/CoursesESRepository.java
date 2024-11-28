package com.skillbridgebackend.skillBridge.backend.Repository.ESRepository;

import com.skillbridgebackend.skillBridge.backend.Entity.ESEntities.CoursesES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface CoursesESRepository extends ElasticsearchRepository<CoursesES, Long> {
}
