// package com.gymai.plan_service.controller;

// import com.gymai.plan_service.dto.*;
// import com.gymai.plan_service.entity.Exercise;
// import com.gymai.plan_service.service.AdminExerciseService;
// import com.gymai.plan_service.service.CloudinaryService;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Page;
// import org.springframework.data.domain.Pageable;
// import org.springframework.http.HttpStatus;
// import org.springframework.http.MediaType;
// import org.springframework.http.ResponseEntity;
// import org.springframework.validation.annotation.Validated;
// import org.springframework.web.bind.annotation.*;
// import org.springframework.web.multipart.MultipartFile;

// import jakarta.validation.Valid;
// import java.util.List;
// import java.util.concurrent.CompletableFuture;

// @RestController
// @RequestMapping("/api/admin/exercises")
// @CrossOrigin(origins = "*")
// @Slf4j
// @Validated
// public class AdminExerciseController {

// @Autowired
// private AdminExerciseService adminExerciseService;

// @Autowired
// private CloudinaryService cloudinaryService;

// // Get all exercises with pagination, filtering, and sorting
// @GetMapping
// public ResponseEntity<Page<ExerciseDTO>> getAllExercises(
// @RequestParam(defaultValue = "0") int page,
// @RequestParam(defaultValue = "20") int size,
// @RequestParam(defaultValue = "name") String sortBy,
// @RequestParam(defaultValue = "asc") String sortDirection,
// @RequestParam(required = false) String category,
// @RequestParam(required = false) String muscleGroup,
// @RequestParam(required = false) String difficulty,
// @RequestParam(required = false) String equipment,
// @RequestParam(required = false) String search,
// @RequestParam(defaultValue = "true") boolean activeOnly) {

// log.info("Fetching exercises - page: {}, size: {}, sortBy: {}, filters:
// category={}, muscleGroup={}, difficulty={}",
// page, size, sortBy, category, muscleGroup, difficulty);

// try {
// ExerciseFilterDTO filter = ExerciseFilterDTO.builder()
// .category(category)
// .muscleGroup(muscleGroup)
// .difficulty(difficulty)
// .equipment(equipment)
// .search(search)
// .activeOnly(activeOnly)
// .build();

// Page<ExerciseDTO> exercises = adminExerciseService.getAllExercises(
// page, size, sortBy, sortDirection, filter);

// return ResponseEntity.ok(exercises);
// } catch (Exception e) {
// log.error("Error fetching exercises", e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// }

// // Get exercise by ID
// @GetMapping("/{id}")
// public ResponseEntity<ExerciseDTO> getExerciseById(@PathVariable Long id) {
// log.info("Fetching exercise with ID: {}", id);

// try {
// ExerciseDTO exercise = adminExerciseService.getExerciseById(id);
// return ResponseEntity.ok(exercise);
// } catch (RuntimeException e) {
// log.error("Exercise not found with ID: {}", id);
// return ResponseEntity.notFound().build();
// } catch (Exception e) {
// log.error("Error fetching exercise with ID: {}", id, e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// }

// // Create new exercise
// @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
// public ResponseEntity<ExerciseDTO> createExercise(
// @Valid @RequestBody CreateExerciseDTO createExerciseDTO) {

// log.info("Creating new exercise: {}", createExerciseDTO.getName());

// try {
// ExerciseDTO createdExercise =
// adminExerciseService.createExercise(createExerciseDTO);
// return ResponseEntity.status(HttpStatus.CREATED).body(createdExercise);
// } catch (IllegalArgumentException e) {
// log.error("Invalid exercise data: {}", e.getMessage());
// return ResponseEntity.badRequest().build();
// } catch (Exception e) {
// log.error("Error creating exercise", e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// }

// // Update exercise
// @PutMapping("/{id}")
// public ResponseEntity<ExerciseDTO> updateExercise(
// @PathVariable Long id,
// @Valid @RequestBody UpdateExerciseDTO updateExerciseDTO) {

// log.info("Updating exercise with ID: {}", id);

// try {
// ExerciseDTO updatedExercise = adminExerciseService.updateExercise(id,
// updateExerciseDTO);
// return ResponseEntity.ok(updatedExercise);
// } catch (RuntimeException e) {
// log.error("Exercise not found with ID: {}", id);
// return ResponseEntity.notFound().build();
// } catch (Exception e) {
// log.error("Error updating exercise with ID: {}", id, e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// }

// // Delete exercise (soft delete)
// @DeleteMapping("/{id}")
// public ResponseEntity<Void> deleteExercise(@PathVariable Long id) {
// log.info("Deleting exercise with ID: {}", id);

// try {
// adminExerciseService.deleteExercise(id);
// return ResponseEntity.noContent().build();
// } catch (RuntimeException e) {
// log.error("Exercise not found with ID: {}", id);
// return ResponseEntity.notFound().build();
// } catch (Exception e) {
// log.error("Error deleting exercise with ID: {}", id, e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// }

// // Hard delete exercise (permanently remove from database)
// @DeleteMapping("/{id}/permanent")
// public ResponseEntity<Void> permanentDeleteExercise(@PathVariable Long id) {
// log.info("Permanently deleting exercise with ID: {}", id);

// try {
// adminExerciseService.permanentDeleteExercise(id);
// return ResponseEntity.noContent().build();
// } catch (RuntimeException e) {
// log.error("Exercise not found with ID: {}", id);
// return ResponseEntity.notFound().build();
// } catch (Exception e) {
// log.error("Error permanently deleting exercise with ID: {}", id, e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// }

// // Upload exercise image
// @PostMapping(value = "/{id}/image", consumes =
// MediaType.MULTIPART_FORM_DATA_VALUE)
// public ResponseEntity<ImageUploadResponseDTO> uploadExerciseImage(
// @PathVariable Long id,
// @RequestParam("image") MultipartFile image) {

// log.info("Uploading image for exercise with ID: {}", id);

// try {
// ExerciseDTO exercise = adminExerciseService.getExerciseById(id);
// CloudinaryService.ImageUploadResult result =
// cloudinaryService.uploadExerciseImage(image, exercise.getName());

// if (result.isSuccess()) {
// // Update exercise with image URL
// adminExerciseService.updateExerciseImage(id, result.getUrl(),
// result.getPublicId());

// ImageUploadResponseDTO response = ImageUploadResponseDTO.builder()
// .success(true)
// .imageUrl(result.getUrl())
// .thumbnailUrl(result.getThumbnailUrl())
// .mediumUrl(result.getMediumUrl())
// .largeUrl(result.getLargeUrl())
// .publicId(result.getPublicId())
// .fileSize(result.getFileSize())
// .format(result.getFormat())
// .build();

// return ResponseEntity.ok(response);
// } else {
// return ResponseEntity.badRequest()
// .body(ImageUploadResponseDTO.builder()
// .success(false)
// .error(result.getError())
// .build());
// }
// } catch (RuntimeException e) {
// log.error("Exercise not found with ID: {}", id);
// return ResponseEntity.notFound().build();
// } catch (Exception e) {
// log.error("Error uploading image for exercise with ID: {}", id, e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
// .body(ImageUploadResponseDTO.builder()
// .success(false)
// .error("Internal server error")
// .build());
// }
// }

// // Upload multiple images for exercise
// @PostMapping(value = "/{id}/images", consumes =
// MediaType.MULTIPART_FORM_DATA_VALUE)
// public CompletableFuture<ResponseEntity<List<ImageUploadResponseDTO>>>
// uploadExerciseImages(
// @PathVariable Long id,
// @RequestParam("images") List<MultipartFile> images) {

// log.info("Uploading {} images for exercise with ID: {}", images.size(), id);

// return CompletableFuture.supplyAsync(() -> {
// try {
// ExerciseDTO exercise = adminExerciseService.getExerciseById(id);
// CompletableFuture<List<CloudinaryService.ImageUploadResult>> uploadFuture =
// cloudinaryService
// .uploadMultipleImages(images, "exercises", exercise.getName());

// List<CloudinaryService.ImageUploadResult> results = uploadFuture.join();

// List<ImageUploadResponseDTO> responses = results.stream()
// .map(result -> ImageUploadResponseDTO.builder()
// .success(result.isSuccess())
// .imageUrl(result.getUrl())
// .thumbnailUrl(result.getThumbnailUrl())
// .mediumUrl(result.getMediumUrl())
// .largeUrl(result.getLargeUrl())
// .publicId(result.getPublicId())
// .fileSize(result.getFileSize())
// .format(result.getFormat())
// .error(result.getError())
// .build())
// .toList();

// // Update exercise with additional images
// List<String> successfulUrls = responses.stream()
// .filter(ImageUploadResponseDTO::isSuccess)
// .map(ImageUploadResponseDTO::getImageUrl)
// .toList();

// if (!successfulUrls.isEmpty()) {
// adminExerciseService.addExerciseImages(id, successfulUrls);
// }

// return ResponseEntity.ok(responses);
// } catch (Exception e) {
// log.error("Error uploading images for exercise with ID: {}", id, e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// });
// }

// // Delete exercise image
// @DeleteMapping("/{id}/image")
// public ResponseEntity<Void> deleteExerciseImage(@PathVariable Long id) {
// log.info("Deleting image for exercise with ID: {}", id);

// try {
// adminExerciseService.deleteExerciseImage(id);
// return ResponseEntity.noContent().build();
// } catch (RuntimeException e) {
// log.error("Exercise not found with ID: {}", id);
// return ResponseEntity.notFound().build();
// } catch (Exception e) {
// log.error("Error deleting image for exercise with ID: {}", id, e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// }

// // Bulk operations
// @PostMapping("/bulk/activate")
// public ResponseEntity<Void> bulkActivateExercises(@RequestBody List<Long>
// exerciseIds) {
// log.info("Bulk activating {} exercises", exerciseIds.size());

// try {
// adminExerciseService.bulkUpdateStatus(exerciseIds, true);
// return ResponseEntity.ok().build();
// } catch (Exception e) {
// log.error("Error bulk activating exercises", e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// }

// @PostMapping("/bulk/deactivate")
// public ResponseEntity<Void> bulkDeactivateExercises(@RequestBody List<Long>
// exerciseIds) {
// log.info("Bulk deactivating {} exercises", exerciseIds.size());

// try {
// adminExerciseService.bulkUpdateStatus(exerciseIds, false);
// return ResponseEntity.ok().build();
// } catch (Exception e) {
// log.error("Error bulk deactivating exercises", e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// }

// @DeleteMapping("/bulk")
// public ResponseEntity<Void> bulkDeleteExercises(@RequestBody List<Long>
// exerciseIds) {
// log.info("Bulk deleting {} exercises", exerciseIds.size());

// try {
// adminExerciseService.bulkDelete(exerciseIds);
// return ResponseEntity.noContent().build();
// } catch (Exception e) {
// log.error("Error bulk deleting exercises", e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// }

// // Get exercise statistics
// @GetMapping("/stats")
// public ResponseEntity<ExerciseStatsDTO> getExerciseStats() {
// try {
// ExerciseStatsDTO stats = adminExerciseService.getExerciseStats();
// return ResponseEntity.ok(stats);
// } catch (Exception e) {
// log.error("Error fetching exercise statistics", e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// }

// // Search exercises with advanced filters
// @PostMapping("/search")
// public ResponseEntity<Page<ExerciseDTO>> searchExercises(
// @RequestBody ExerciseSearchDTO searchDTO,
// Pageable pageable) {

// log.info("Advanced exercise search with criteria: {}", searchDTO);

// try {
// Page<ExerciseDTO> exercises = adminExerciseService.searchExercises(searchDTO,
// pageable);
// return ResponseEntity.ok(exercises);
// } catch (Exception e) {
// log.error("Error searching exercises", e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// }

// // Export exercises to CSV
// @GetMapping("/export")
// public ResponseEntity<byte[]> exportExercises(
// @RequestParam(required = false) String category,
// @RequestParam(required = false) String muscleGroup,
// @RequestParam(required = false) String difficulty) {

// log.info("Exporting exercises to CSV with filters: category={},
// muscleGroup={}, difficulty={}",
// category, muscleGroup, difficulty);

// try {
// ExerciseFilterDTO filter = ExerciseFilterDTO.builder()
// .category(category)
// .muscleGroup(muscleGroup)
// .difficulty(difficulty)
// .activeOnly(true)
// .build();

// byte[] csvData = adminExerciseService.exportExercisesToCSV(filter);

// return ResponseEntity.ok()
// .header("Content-Disposition", "attachment; filename=exercises.csv")
// .header("Content-Type", "text/csv")
// .body(csvData);
// } catch (Exception e) {
// log.error("Error exporting exercises", e);
// return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
// }
// }
// }