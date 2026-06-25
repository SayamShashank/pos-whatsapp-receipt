package com.ina.pos.receipt.aws.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Slf4j
@Service
public class AmazonS3Service {

    @Value("${s3.bucket.name}")
    private String s3BucketName;

    @Value("${aws.region}")
    private String awsRegion;

    private final S3Client s3Client;

    public AmazonS3Service(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFileAndGetUrl(byte[] fileInBytes, String fileName) {
        log.info("Uploading file in the s3 bucket");
        String key = "receipt/pdf/" + fileName;
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(s3BucketName)
                .key(key)
                .contentType("application/pdf")
                .build();
        s3Client.putObject(
                putObjectRequest,
                RequestBody.fromBytes(fileInBytes)
        );
        log.info("File is uploaded successfully in the location : {}", key);
        return generateUrlToFetchFilesFromS3Bucket(key, s3BucketName, awsRegion);
    }

    public String generateUrlToFetchFilesFromS3Bucket(String key,String s3BucketName, String awsRegion ) {
        return "https://" + s3BucketName + ".s3." + awsRegion + ".amazonaws.com/" + key;
    }

}
