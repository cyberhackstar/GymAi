// package com.gymai.plan_service.feign;

// import com.gymai.plan_service.dto.PlanRequest;
// import com.gymai.plan_service.dto.PlanResponse;
// import org.springframework.cloud.openfeign.FeignClient;
// import org.springframework.web.bind.annotation.PostMapping;

// @FeignClient(name = "ai-service", url = "${ai.service.url}")
// public interface AiServiceClient {

// @PostMapping("/api/ai/generate-plan")
// PlanResponse generatePlan(PlanRequest request);
// }
