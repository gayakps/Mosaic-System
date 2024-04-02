package gaya.pe.kr.mosaicsystem.video.controller;

import gaya.pe.kr.mosaicsystem.aws.manager.AWSServiceManager;
import gaya.pe.kr.mosaicsystem.aws.ec2.model.EC2UserTag;
import gaya.pe.kr.mosaicsystem.video.entities.UserSuccessUploadNotify;
import gaya.pe.kr.mosaicsystem.video.entities.UserUploadRequest;
import gaya.pe.kr.mosaicsystem.deprecated.VideoServiceManager;
import gaya.pe.kr.mosaicsystem.deprecated.VideoFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

@RestController
@RequestMapping("/api")
public class VideoUploadController {

    private static final Logger logger = LoggerFactory.getLogger(VideoUploadController.class);

    AWSServiceManager awsServiceManager;

    public VideoUploadController(
            @Autowired AWSServiceManager awsServiceManager
    ) {
        System.out.println("VideoUploadController Create");
        this.awsServiceManager = awsServiceManager;
    }

    @PostMapping("/generate-resigned-url")
    public ResponseEntity<String> generateURL(@RequestBody UserUploadRequest userUploadRequest) {
        String fileName = userUploadRequest.getUserVideo().getFileName().replace("/", "_");
        String userId = userUploadRequest.getUserVideo().getUserId();
        URL url = awsServiceManager.getAwss3Manager().generatePreSignedURL(awsServiceManager.getS3Client(), userId, fileName);
        logger.info("파일 명 : {} USER ID : {} URL : {} URL 에 대한 Object Video Id {}", fileName, userId, url, awsServiceManager.getAwss3Manager().getAwss3UserDataRepository().getValue(url));
        return ResponseEntity.ok(url.toString());
    }

    @PostMapping("/success-upload-file")
    public ResponseEntity<?> successUploadRawVideo(@RequestBody UserSuccessUploadNotify userSuccessUploadNotify) {
        URL url = userSuccessUploadNotify.getUrl();
        int videoId = awsServiceManager.getAwss3Manager().getAwss3UserDataRepository().getValue(userSuccessUploadNotify.getUrl()).orElse(-1);
        logger.info("File Name : {} User ID : {} URL {} Video Id {} ", userSuccessUploadNotify.getUserVideo().getFileName(), userSuccessUploadNotify.getUserVideo().getUserId(), url, videoId);
        awsServiceManager.getAwsec2Manager().createEC2Instance(userSuccessUploadNotify, awsServiceManager.getEc2Client(), "Test-" + new Random().nextInt(1000) + 1,videoId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notify-cancellation")
    public ResponseEntity<?> notifyCancellation(@RequestBody UserUploadRequest userUploadRequest) {
        // 취소 이벤트에 대한 로직 처리
        System.out.println("Upload cancellation notified for file: " + userUploadRequest.getUserVideo().getFileName() + ":: " + userUploadRequest.getUserVideo().getUserId());
        System.out.println(awsServiceManager.getAwss3Manager().deleteS3Object(awsServiceManager.getS3Client(), userUploadRequest).toString());
        // 필요한 처리 수행...
        return ResponseEntity.ok().build();
    }

}
