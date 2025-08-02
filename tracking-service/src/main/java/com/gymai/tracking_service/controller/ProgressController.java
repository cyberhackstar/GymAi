
package com.gym.progress.controller;

import com.gym.progress.entity.Progress;
import com.gym.progress.service.ProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/progress")
public class ProgressController {

    @Autowired
    private ProgressService progressService;

    @GetMapping("/{userId}")
    public List<Progress> getUserProgress(@PathVariable String userId) {
        return progressService.getProgressByUserId(userId);
    }
}
