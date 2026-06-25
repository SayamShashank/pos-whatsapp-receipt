package com.ina.pos.receipt.aws.service;

import com.ina.pos.receipt.aws.config.AmazonS3Config;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.s3.S3Client;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.util.ReflectionTestUtils.setField;

@ExtendWith(MockitoExtension.class)
class AmazonS3ServiceTest {

    @Mock
    private S3Client s3Client;

    private final AmazonS3Config amazonS3Config = new AmazonS3Config();

    @InjectMocks
    private AmazonS3Service amazonS3Service;

    @Test
    void testUploadFileAndGetUrl() throws IOException {
        setField(amazonS3Service,"s3BucketName", "test-s3-bucket");
        setField(amazonS3Service,"awsRegion", "test-aws-region");
        MultipartFile mockPdfFile = new MockMultipartFile("file", "file.pdf", "videos/pdf", "file-content".getBytes());

        setField(amazonS3Config, "region","test-region");
        setField(amazonS3Config, "accessKey","test-access-key");
        setField(amazonS3Config, "secretKey","test-secret-key");
        amazonS3Config.s3Client(amazonS3Config.awsCredentialsProvider());
        String pdfUrl = amazonS3Service.uploadFileAndGetUrl(mockPdfFile.getBytes(), "test-file");
        assertNotNull(pdfUrl);
    }

}
