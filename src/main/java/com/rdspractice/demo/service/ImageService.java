package com.rdspractice.demo.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.rdspractice.demo.entity.ImageMetadata;
import com.rdspractice.demo.repository.ImageMetadataRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.Optional;

@Service
public class ImageService {

    @Autowired
    private AmazonS3 s3Client;

    @Autowired
    private ImageMetadataRepository imageMetadataRepository;

    @Value("${app.awsServices.bucketName}")
    private String bucketName;

    public ImageService(AmazonS3 s3Client, ImageMetadataRepository imageMetadataRepository) {
        this.s3Client = s3Client;
        this.imageMetadataRepository = imageMetadataRepository;
    }

    public void uploadFile(String keyName, MultipartFile file) throws IOException {
        File tempFile = File.createTempFile("temp", keyName);
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(file.getBytes());
            s3Client.putObject(new PutObjectRequest(bucketName, keyName, tempFile));
        } finally {
            tempFile.delete();
        }

        // Guarda la metadatos en la base de datos
        ImageMetadata metadata = new ImageMetadata();
        metadata.setName(keyName);
        metadata.setSize(file.getSize());
        metadata.setLastUpdateDate(new Date());
        metadata.setFileExtension(getFileExtension(file.getOriginalFilename()));

        imageMetadataRepository.save(metadata);
    }

    public File downloadFile(String keyName) {
        File tempFile = new File(keyName);
        s3Client.getObject(new GetObjectRequest(bucketName, keyName), tempFile);
        return tempFile;
    }

    public void deleteFile(String keyName) {
        // Elimina el objeto de S3
        s3Client.deleteObject(new DeleteObjectRequest(bucketName, keyName));

        // Encuentra y elimina el registro de metadatos en la base de datos
        Optional<ImageMetadata> imageMetadata = imageMetadataRepository.findByName(keyName);
        imageMetadata.ifPresent(metadata -> imageMetadataRepository.delete(metadata));
    }

    private String getFileExtension(String fileName) {
        int lastIndexOfDot = fileName.lastIndexOf(".");
        return lastIndexOfDot == -1 ? "" : fileName.substring(lastIndexOfDot + 1);
    }
}
