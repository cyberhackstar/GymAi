package com.gymai.plan_service.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gymai.plan_service.dto.DayPlanDto;
import com.gymai.plan_service.dto.DietRequest;
import com.gymai.plan_service.service.SpoonacularService;

@RestController
@RequestMapping("/api/diet")
public class DietController {

    @Autowired
    private SpoonacularService spoonacularService;

    @PostMapping("/plan")
    public List<DayPlanDto> getDietPlan(@RequestBody DietRequest request) {

        int targetCalories = spoonacularService.calculateTargetCalories(request);
        // Here you can generate a week plan, using the same calories/preference, or
        // vary days
        List<DayPlanDto> weekPlan = new ArrayList<>();
        String[] days = { "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday" };
        for (String day : days) {
            DayPlanDto dayPlan = new DayPlanDto();
            dayPlan.setDay(day);
            dayPlan.setDailyCalories(targetCalories);
            dayPlan.setMeals(spoonacularService.fetchMeals(request, targetCalories, day));
            weekPlan.add(dayPlan);
        }
        return weekPlan;
    }
}
