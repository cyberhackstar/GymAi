package com.gymai.plan_service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gymai.plan_service.entity.Exercise;

// ExerciseRepository.java
@Repository
public interface ExerciseRepository extends JpaRepository<Exercise, Long> {

    List<Exercise> findByCategory(String category);

    List<Exercise> findByMuscleGroup(String muscleGroup);

    List<Exercise> findByDifficulty(String difficulty);

    List<Exercise> findByEquipment(String equipment);

    List<Exercise> findByCategoryAndDifficulty(String category, String difficulty);

    List<Exercise> findByMuscleGroupAndDifficulty(String muscleGroup, String difficulty);

    @Query("SELECT e FROM Exercise e WHERE e.equipment IN :equipmentList AND e.difficulty = :difficulty")
    List<Exercise> findByEquipmentInAndDifficulty(@Param("equipmentList") List<String> equipmentList,
            @Param("difficulty") String difficulty);

    @Query("SELECT e FROM Exercise e WHERE e.category = :category AND e.muscleGroup = :muscleGroup AND e.difficulty = :difficulty")
    List<Exercise> findByCategoryAndMuscleGroupAndDifficulty(@Param("category") String category,
            @Param("muscleGroup") String muscleGroup,
            @Param("difficulty") String difficulty);
}