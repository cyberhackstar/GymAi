package com.gymai.plan_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class MealPlanResponse {
    private List<Meal> meals;
    private Nutrients nutrients;
}
