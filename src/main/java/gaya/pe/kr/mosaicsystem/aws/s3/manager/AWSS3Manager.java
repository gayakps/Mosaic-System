package gaya.pe.kr.mosaicsystem.aws.s3.manager;

import gaya.pe.kr.mosaicsystem.aws.s3.configuration.S3Configuration;
import gaya.pe.kr.mosaicsystem.video.entities.UserUploadRequest;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.List;

@Service
@Getter
public class AWSS3Manager {

    S3Configuration s3Configuration;
    private static final Logger logger = LoggerFactory.getLogger(AWSS3Manager.class);

    public AWSS3Manager(@Autowired S3Configuration s3Configuration) {
        this.s3Configuration = s3Configuration;
    }

    public URL generatePreSignedURL(String userId, String fileName) {

        String objectKey = userId + "/" + fileName;

        // S3 클라이언트 생성
        try (S3Presigner presigner = S3Presigner.builder().region(Region.AP_NORTHEAST_2).build() ) {
            // 사전 서명된 PUT 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(s3Configuration.getRawVideoContentsMosaicUserUploadBucketName())
                    .key(objectKey)
                    .build();

            // 사전 서명된 요청 설정
            PresignedPutObjectRequest presignedPutObjectRequest =
                    presigner.presignPutObject(z -> z.signatureDuration(Duration.ofMinutes(s3Configuration.getPresignedURLValidMinutes()))
                            .putObjectRequest(putObjectRequest));

            // 사전 서명된 URL 반환
            return presignedPutObjectRequest.url();
        }
    }

    public List<S3Object> getAllS3Objects(S3Client s3Client) {
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(s3Configuration.getRawVideoContentsMosaicUserUploadBucketName()).build();
        ListObjectsResponse listObjectsResponse = s3Client.listObjects(listObjectsRequest);
        return listObjectsResponse.contents();
    }


    public DeleteObjectResponse deleteS3Object(S3Client s3Client, UserUploadRequest userUploadRequest) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(s3Configuration.getRawVideoContentsMosaicUserUploadBucketName())
                .key(userUploadRequest.getUserVideo().getUserId()+"/"+userUploadRequest.getUserVideo().getFileName())
                .build();
        return s3Client.deleteObject(deleteObjectRequest);
    }

}
