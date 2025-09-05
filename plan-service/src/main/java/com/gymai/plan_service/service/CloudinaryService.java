// package com.gymai.plan_service.service;

// import com.cloudinary.Cloudinary;
// import com.cloudinary.utils.ObjectUtils;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.beans.factory.annotation.Value;
// import org.springframework.stereotype.Service;
// import org.springframework.web.multipart.MultipartFile;

// import javax.imageio.ImageIO;
// import java.awt.image.BufferedImage;
// import java.io.IOException;
// import java.util.*;
// import java.util.concurrent.CompletableFuture;

// @Service
// @Slf4j
// public class CloudinaryService {

// @Autowired
// private Cloudinary cloudinary;

// @Value("${cloudinary.folder.exercises:exercises}")
// private String exerciseFolder;

// @Value("${cloudinary.folder.foods:foods}")
// private String foodFolder;

// @Value("${cloudinary.max-file-size:10485760}") // 10MB default
// private long maxFileSize;

// @Value("${cloudinary.allowed-formats:jpg,jpeg,png,webp,gif}")
// private String allowedFormats;

// private static final Map<String, String> IMAGE_TRANSFORMATIONS = Map.of(
// "thumbnail", "c_thumb,w_150,h_150,g_center",
// "medium", "c_fill,w_400,h_300,g_center",
// "large", "c_fit,w_800,h_600");

// public ImageUploadResult uploadExerciseImage(MultipartFile file, String
// exerciseName) {
// return uploadImage(file, exerciseFolder, generatePublicId("exercise",
// exerciseName));
// }

// public ImageUploadResult uploadFoodImage(MultipartFile file, String foodName)
// {
// return uploadImage(file, foodFolder, generatePublicId("food", foodName));
// }

// public ImageUploadResult uploadImage(MultipartFile file, String folder,
// String publicId) {
// log.info("Starting image upload to folder: {}, publicId: {}", folder,
// publicId);

// try {
// // Validate file
// validateFile(file);

// Map<String, Object> uploadParams = ObjectUtils.asMap(
// "folder", folder,
// "public_id", publicId,
// "overwrite", true,
// "resource_type", "image",
// "format", "webp", // Convert to WebP for better performance
// "quality", "auto:good",
// "fetch_format", "auto",
// "flags", "progressive",
// "transformation", Arrays.asList(
// ObjectUtils.asMap("quality", "auto:good"),
// ObjectUtils.asMap("fetch_format", "auto")));

// Map<String, Object> result = cloudinary.uploader().upload(file.getBytes(),
// uploadParams);

// String url = (String) result.get("secure_url");
// String finalPublicId = (String) result.get("public_id");

// // Generate optimized URLs
// Map<String, String> optimizedUrls = generateOptimizedUrls(finalPublicId);

// log.info("Successfully uploaded image. URL: {}, PublicId: {}", url,
// finalPublicId);

// return ImageUploadResult.builder()
// .url(url)
// .publicId(finalPublicId)
// .thumbnailUrl(optimizedUrls.get("thumbnail"))
// .mediumUrl(optimizedUrls.get("medium"))
// .largeUrl(optimizedUrls.get("large"))
// .fileSize(file.getSize())
// .format("webp")
// .success(true)
// .build();

// } catch (Exception e) {
// log.error("Failed to upload image: {}", e.getMessage(), e);
// return ImageUploadResult.builder()
// .success(false)
// .error(e.getMessage())
// .build();
// }
// }

// public CompletableFuture<List<ImageUploadResult>> uploadMultipleImages(
// List<MultipartFile> files, String folder, String baseName) {
// return CompletableFuture.supplyAsync(() -> {
// List<ImageUploadResult> results = new ArrayList<>();

// for (int i = 0; i < files.size(); i++) {
// MultipartFile file = files.get(i);
// String publicId = generatePublicId(baseName, String.valueOf(i + 1));
// ImageUploadResult result = uploadImage(file, folder, publicId);
// results.add(result);
// }

// return results;
// });
// }

// public boolean deleteImage(String publicId) {
// try {
// log.info("Deleting image with publicId: {}", publicId);

// Map<String, Object> result = cloudinary.uploader().destroy(publicId,
// ObjectUtils.emptyMap());
// String resultStatus = (String) result.get("result");

// boolean success = "ok".equals(resultStatus);
// log.info("Image deletion result: {}, success: {}", resultStatus, success);

// return success;
// } catch (Exception e) {
// log.error("Failed to delete image with publicId: {}", publicId, e);
// return false;
// }
// }

// public CompletableFuture<Boolean> deleteMultipleImages(List<String>
// publicIds) {
// return CompletableFuture.supplyAsync(() -> {
// boolean allDeleted = true;
// for (String publicId : publicIds) {
// if (!deleteImage(publicId)) {
// allDeleted = false;
// }
// }
// return allDeleted;
// });
// }

// public String generateOptimizedUrl(String publicId, String transformation) {
// return cloudinary.url()
// .transformation(transformation)
// .generate(publicId);
// }

// public Map<String, String> generateOptimizedUrls(String publicId) {
// Map<String, String> urls = new HashMap<>();

// IMAGE_TRANSFORMATIONS.forEach((key, transformation) -> {
// String url = generateOptimizedUrl(publicId, transformation);
// urls.put(key, url);
// });

// return urls;
// }

// public ImageAnalysisResult analyzeImage(String publicId) {
// try {
// Map<String, Object> result = cloudinary.api().resource(publicId,
// ObjectUtils.asMap(
// "image_metadata", true,
// "colors", true,
// "quality_analysis", true));

// return ImageAnalysisResult.builder()
// .width((Integer) result.get("width"))
// .height((Integer) result.get("height"))
// .format((String) result.get("format"))
// .bytes((Long) result.get("bytes"))
// .colors((List<Map<String, Object>>) result.get("colors"))
// .qualityScore((Double) result.get("quality_score"))
// .build();

// } catch (Exception e) {
// log.error("Failed to analyze image: {}", e.getMessage(), e);
// return null;
// }
// }

// public List<String> searchImages(String folder, String tag) {
// try {
// Map<String, Object> result = cloudinary.search()
// .expression(String.format("folder:%s AND tags:%s", folder, tag))
// .maxResults(100)
// .execute();

// List<Map<String, Object>> resources = (List<Map<String, Object>>)
// result.get("resources");

// return resources.stream()
// .map(resource -> (String) resource.get("secure_url"))
// .toList();

// } catch (Exception e) {
// log.error("Failed to search images: {}", e.getMessage(), e);
// return Collections.emptyList();
// }
// }

// private void validateFile(MultipartFile file) throws IOException {
// if (file == null || file.isEmpty()) {
// throw new IllegalArgumentException("File cannot be null or empty");
// }

// if (file.getSize() > maxFileSize) {
// throw new IllegalArgumentException("File size exceeds maximum limit of " +
// maxFileSize + " bytes");
// }

// String contentType = file.getContentType();
// if (contentType == null || !contentType.startsWith("image/")) {
// throw new IllegalArgumentException("File must be an image");
// }

// // Validate file format
// String format = getFileExtension(file.getOriginalFilename()).toLowerCase();
// List<String> allowedFormatsList = Arrays.asList(allowedFormats.split(","));

// if (!allowedFormatsList.contains(format)) {
// throw new IllegalArgumentException("Unsupported file format. Allowed formats:
// " + allowedFormats);
// }

// // Validate that it's actually an image by trying to read it
// try {
// BufferedImage image = ImageIO.read(file.getInputStream());
// if (image == null) {
// throw new IllegalArgumentException("File is not a valid image");
// }
// } catch (IOException e) {
// throw new IllegalArgumentException("Cannot read image file", e);
// }
// }

// private String generatePublicId(String prefix, String identifier) {
// String sanitizedIdentifier = identifier.replaceAll("[^a-zA-Z0-9-_]", "_");
// return String.format("%s_%s_%d", prefix, sanitizedIdentifier,
// System.currentTimeMillis());
// }

// private String getFileExtension(String filename) {
// if (filename == null)
// return "";
// int lastDotIndex = filename.lastIndexOf('.');
// return lastDotIndex > 0 ? filename.substring(lastDotIndex + 1) : "";
// }

// // Result DTOs
// @lombok.Data
// @lombok.Builder
// @lombok.NoArgsConstructor
// @lombok.AllArgsConstructor
// public static class ImageUploadResult {
// private boolean success;
// private String url;
// private String publicId;
// private String thumbnailUrl;
// private String mediumUrl;
// private String largeUrl;
// private long fileSize;
// private String format;
// private String error;
// }

// @lombok.Data
// @lombok.Builder
// @lombok.NoArgsConstructor
// @lombok.AllArgsConstructor
// public static class ImageAnalysisResult {
// private Integer width;
// private Integer height;
// private String format;
// private Long bytes;
// private List<Map<String, Object>> colors;
// private Double qualityScore;
// }
// }