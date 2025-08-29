
package com.gymai.tracking_service.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gymai.tracking_service.entity.Progress;
import com.gymai.tracking_service.repository.ProgressRepository;

import java.util.List;

@Service
public class ProgressService {

    @Autowired
    private ProgressRepository repository;

    public void saveProgress(Progress progress) {
        repository.save(progress);
    }

    public List<Progress> getProgressByUserId(String userId) {
        return repository.findByUserId(userId);
    }
}
