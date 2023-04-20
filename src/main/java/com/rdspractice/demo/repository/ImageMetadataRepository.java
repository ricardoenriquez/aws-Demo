package com.rdspractice.demo.repository;

import com.rdspractice.demo.entity.ImageMetadata;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ImageMetadataRepository extends JpaRepository<ImageMetadata, Long> {
    Optional<ImageMetadata> findByName(String name);
}

