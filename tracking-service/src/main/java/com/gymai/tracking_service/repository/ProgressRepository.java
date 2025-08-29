
package com.gymai.tracking_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gymai.tracking_service.entity.Progress;

import java.util.List;

public interface ProgressRepository extends JpaRepository<Progress, Long> {
    List<Progress> findByUserId(String userId);
}
