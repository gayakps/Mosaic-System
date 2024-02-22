package gaya.pe.kr.mosaicsystem.aws.manager.s3;

import gaya.pe.kr.mosaicsystem.video.entities.UserUploadRequest;
import lombok.Getter;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;

import java.net.URL;
import java.time.Duration;
import java.util.Date;
import java.util.List;

@Service
@Getter
public class AWSS3Manager {

    final String RAW_VIDEO_MOSAIC_USER_UPLOAD_BUCKET = "mosaic-user-upload";

    public URL generatePreSignedURL(String userId, String fileName) {


        String objectKey = userId + "/" + fileName;

        // S3 클라이언트 생성
        try (S3Presigner presigner = S3Presigner.builder().region(Region.AP_NORTHEAST_2).build() ) {
            // 사전 서명된 PUT 요청 생성
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(RAW_VIDEO_MOSAIC_USER_UPLOAD_BUCKET)
                    .key(objectKey)
                    .build();

            // 사전 서명된 요청 설정
            PresignedPutObjectRequest presignedPutObjectRequest =
                    presigner.presignPutObject(z -> z.signatureDuration(Duration.ofHours(1))
                            .putObjectRequest(putObjectRequest));

            // 사전 서명된 URL 반환
            return presignedPutObjectRequest.url();
        }
    }

    public List<S3Object> getAllS3Objects(S3Client s3Client) {
        ListObjectsRequest listObjectsRequest = ListObjectsRequest.builder().bucket(RAW_VIDEO_MOSAIC_USER_UPLOAD_BUCKET).build();
        ListObjectsResponse listObjectsResponse = s3Client.listObjects(listObjectsRequest);
        return listObjectsResponse.contents();
    }

    public DeleteObjectResponse deleteS3Object(S3Client s3Client, UserUploadRequest userUploadRequest) {
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(RAW_VIDEO_MOSAIC_USER_UPLOAD_BUCKET)
                .key(userUploadRequest.getUserVideo().getUserId()+"/"+userUploadRequest.getUserVideo().getFileName())
                .build();

        return s3Client.deleteObject(deleteObjectRequest);
    }

}
