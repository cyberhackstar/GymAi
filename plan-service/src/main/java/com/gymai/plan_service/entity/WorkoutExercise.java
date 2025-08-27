package com.gymai.plan_service.entity;

// WorkoutExercise.java
public class WorkoutExercise {
    private Exercise exercise;
    private int sets;
    private int reps;
    private int durationMinutes; // for cardio
    private double weight; // for strength training
    private int restSeconds;
    private double caloriesBurned;

    public WorkoutExercise(Exercise exercise, int sets, int reps, int durationMinutes,
            double weight, int restSeconds) {
        this.exercise = exercise;
        this.sets = sets;
        this.reps = reps;
        this.durationMinutes = durationMinutes;
        this.weight = weight;
        this.restSeconds = restSeconds;
        calculateCaloriesBurned();
    }

    private void calculateCaloriesBurned() {
        if (durationMinutes > 0) {
            // For cardio exercises
            this.caloriesBurned = exercise.getCaloriesBurnedPerMinute() * durationMinutes;
        } else {
            // For strength training, estimate based on sets and reps
            int totalTime = (sets * reps * 3) + (sets * restSeconds); // 3 seconds per rep
            this.caloriesBurned = exercise.getCaloriesBurnedPerMinute() * (totalTime / 60.0);
        }
    }

    // Getters and setters
    public Exercise getExercise() {
        return exercise;
    }

    public void setExercise(Exercise exercise) {
        this.exercise = exercise;
    }

    public int getSets() {
        return sets;
    }

    public void setSets(int sets) {
        this.sets = sets;
    }

    public int getReps() {
        return reps;
    }

    public void setReps(int reps) {
        this.reps = reps;
    }

    public int getDurationMinutes() {
        return durationMinutes;
    }

    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
        calculateCaloriesBurned();
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public int getRestSeconds() {
        return restSeconds;
    }

    public void setRestSeconds(int restSeconds) {
        this.restSeconds = restSeconds;
    }

    public double getCaloriesBurned() {
        return caloriesBurned;
    }
}
