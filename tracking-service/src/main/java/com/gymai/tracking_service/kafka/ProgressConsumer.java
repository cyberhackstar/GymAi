
package com.gymai.tracking_service.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gymai.tracking_service.entity.Progress;
import com.gymai.tracking_service.service.ProgressService;

@Service
public class ProgressConsumer {

    @Autowired
    private ProgressService progressService;

    private ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(topics = "user-progress", groupId = "progress-group")
    public void consume(String message) {
        try {
            Progress progress = mapper.readValue(message, Progress.class);
            progressService.saveProgress(progress);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
