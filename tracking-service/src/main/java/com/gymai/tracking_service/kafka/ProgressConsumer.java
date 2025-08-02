
package com.gym.progress.kafka;

import com.gym.progress.entity.Progress;
import com.gym.progress.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

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
