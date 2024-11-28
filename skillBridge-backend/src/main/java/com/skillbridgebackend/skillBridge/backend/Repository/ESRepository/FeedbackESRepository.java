package com.skillbridgebackend.skillBridge.backend.Repository.ESRepository;

import com.skillbridgebackend.skillBridge.backend.Entity.ESEntities.FeedbackES;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface FeedbackESRepository extends ElasticsearchRepository<FeedbackES, Long> {
}
