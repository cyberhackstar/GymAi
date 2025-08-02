
package com.gym.progress.service;

import com.gym.progress.entity.Progress;
import com.gym.progress.repository.ProgressRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
