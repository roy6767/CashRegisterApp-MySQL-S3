package se.systementor.CloudStorage;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;

public class CloudStorage {
    private String bucketName = "biplobsbucketjensenyh-01";
    private S3Client s3Client;
    private String accessKey = "todo";
    private String secretKey = "todo";

    public CloudStorage() {
        AwsCredentialsProvider credentialsProvider = StaticCredentialsProvider.create(
                AwsBasicCredentials.create(accessKey, secretKey)
        );

        s3Client = S3Client.builder()
                .credentialsProvider(credentialsProvider)
                .region(Region.EU_NORTH_1)
                .build();
    }

    public void storeInS3(String fileName) {
        try {
            File file = new File(fileName);

            if (!file.exists()) {
                System.out.println("File not found: " + fileName);
                return;
            }
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            s3Client.putObject(objectRequest, RequestBody.fromFile(file));
            System.out.println("File uploaded successfully to S3.");

        } catch (S3Exception e) {
            System.err.println("Error uploading file to S3: " + e.awsErrorDetails().errorMessage());
        } finally {
            if (s3Client != null) {
                s3Client.close();
            }
        }
    }
}
