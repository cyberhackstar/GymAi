package com.gymai.plan_service.dto;

import lombok.Data;

@Data
public class Meal {
    private int id;
    private String title;
    private int readyInMinutes;
    private int servings;
    private String sourceUrl;
    private String image;
    private String imageType;
}
