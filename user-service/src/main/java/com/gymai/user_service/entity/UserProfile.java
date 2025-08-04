package com.gymai.user_service.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfile {

  




    @Id
    private Long userId; // ID from auth-service

    private String name;
    private String email; 
    private int age;
    private double height;
    private double weight;
    private String gender;
    private String goal;         // e.g., "Lose Weight"
    private String activityLevel; // e.g., "Moderate"
    private String preference;    // e.g., "Vegetarian"


}