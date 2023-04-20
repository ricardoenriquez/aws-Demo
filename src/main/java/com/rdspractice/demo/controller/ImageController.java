package com.rdspractice.demo.controller;

import com.rdspractice.demo.entity.ImageMetadata;
import com.rdspractice.demo.repository.ImageMetadataRepository;
import com.rdspractice.demo.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    @Autowired
    private ImageMetadataRepository imageMetadataRepository;

    @Autowired
    private ImageService imageService;

    @PostMapping
    public ResponseEntity<ImageMetadata> uploadImage(@RequestParam("image") MultipartFile file) throws IOException {
        String imageName = file.getOriginalFilename();
        imageService.uploadFile(imageName, file);
        ImageMetadata metadata = imageMetadataRepository.findByName(imageName).orElse(null);
        return new ResponseEntity<>(metadata, HttpStatus.CREATED);
    }

    @GetMapping("/{name}")
    public ResponseEntity<File> downloadImage(@PathVariable("name") String imageName) {
        File file = imageService.downloadFile(imageName);
        return new ResponseEntity<>(file, HttpStatus.OK);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteImage(@PathVariable("name") String imageName) {
        imageService.deleteFile(imageName);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<ImageMetadata>> getAllImageMetadata() {
        List<ImageMetadata> metadataList = imageMetadataRepository.findAll();
        return new ResponseEntity<>(metadataList, HttpStatus.OK);
    }
}
