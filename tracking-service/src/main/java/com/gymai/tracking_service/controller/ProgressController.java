
package com.gymai.tracking_service.controller;

import com.gymai.*;
import com.gymai.tracking_service.entity.Progress;
import com.gymai.tracking_service.service.ProgressService;

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
