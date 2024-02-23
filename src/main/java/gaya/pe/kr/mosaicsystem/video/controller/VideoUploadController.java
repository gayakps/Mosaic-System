package gaya.pe.kr.mosaicsystem.video.controller;

import gaya.pe.kr.mosaicsystem.aws.manager.AWSServiceManager;
import gaya.pe.kr.mosaicsystem.aws.manager.ec2.model.EC2UserTag;
import gaya.pe.kr.mosaicsystem.video.entities.UserSuccessUploadNotify;
import gaya.pe.kr.mosaicsystem.video.entities.UserUploadRequest;
import gaya.pe.kr.mosaicsystem.video.entities.UserUploadVideoChunk;
import gaya.pe.kr.mosaicsystem.video.service.VideoServiceManager;
import gaya.pe.kr.mosaicsystem.video.service.io.VideoFileManager;
import gaya.pe.kr.mosaicsystem.video.thread.VideoProcessingEC2ComputeTask;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.services.ec2.model.Instance;
import software.amazon.awssdk.services.ec2.model.InstanceNetworkInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/api")
public class VideoUploadController {

    VideoServiceManager videoServiceManager;
    VideoFileManager videoFileManager;

    AWSServiceManager awsServiceManager;

    public VideoUploadController(
            @Autowired AWSServiceManager awsServiceManager,
            @Autowired VideoServiceManager videoServiceManager,
            @Autowired VideoFileManager videoFileManager
    ) {
        System.out.println("VideoUploadController Create");
        this.awsServiceManager = awsServiceManager;
        this.videoServiceManager = videoServiceManager;
        this.videoFileManager = videoFileManager;
    }

    @PostMapping("/generate-resigned-url")
            public ResponseEntity<String> generateURL(@RequestBody UserUploadRequest userUploadRequest) {
        String fileName = userUploadRequest.getUserVideo().getFileName().replace("/", "_");
        String userId = userUploadRequest.getUserVideo().getUserId();
        URL url = awsServiceManager.getAwss3Manager().generatePreSignedURL(userId, fileName);

        System.out.printf("파일 명 : %s 반환 URL : %s [ USER ID : %s ]\n", fileName, url, userUploadRequest.getUserVideo().getUserId());

        return ResponseEntity.ok(url.toString());
    }

    @PostMapping("/success-upload-file")
    public ResponseEntity<?> successUploadRawVideo(@RequestBody UserSuccessUploadNotify userSuccessUploadNotify) {
        System.out.printf("파일 명 : %s [ USER ID : %s ] Success\n", userSuccessUploadNotify.getUserVideo().getFileName(), userSuccessUploadNotify.getUserVideo().getUserId());
        EC2UserTag ec2UserTag = new EC2UserTag();
        ec2UserTag.addLine("sudo apt update");
        ec2UserTag.addLine("sudo apt install -y awscli");
        ec2UserTag.addLine("echo @@@@ 김선우 테스트 입니다 @@@@");
        ec2UserTag.addLine(String.format("echo %s - %s - %s", userSuccessUploadNotify.getUserVideo().getUserId(), userSuccessUploadNotify.getUserVideo().getFileName(), new Date().toString()));
        ec2UserTag.addLine(String.format("aws s3 cp s3://%s/%s/'%s' /home/ubuntu/'%s'", awsServiceManager.getAwss3Manager().getRAW_VIDEO_MOSAIC_USER_UPLOAD_BUCKET(), userSuccessUploadNotify.getUserVideo().getUserId(), userSuccessUploadNotify.getUserVideo().getFileName(), userSuccessUploadNotify.getUserVideo().getFileName()));
        awsServiceManager.getAwsec2Manager().createEC2Instance(awsServiceManager.getEc2Client(), "Test-"+new Random().nextInt(1000)+1, ec2UserTag);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/notify-cancellation")
    public ResponseEntity<?> notifyCancellation(@RequestBody UserUploadRequest userUploadRequest) {
        // 취소 이벤트에 대한 로직 처리
        System.out.println("Upload cancellation notified for file: " + userUploadRequest.getUserVideo().getFileName() + ":: " +userUploadRequest.getUserVideo().getUserId());
        System.out.println(awsServiceManager.getAwss3Manager().deleteS3Object(awsServiceManager.getS3Client(), userUploadRequest).toString());
        // 필요한 처리 수행...
        return ResponseEntity.ok().build();
    }


    @Deprecated( since = "release 1.0", forRemoval = true )
    @PostMapping("/upload")
    public ResponseEntity<Boolean> handleFileUpload(@RequestParam("file") MultipartFile file) throws IOException {
        long size = file.getSize();
        System.out.printf("Content Type : %s Name : [%s] Size : %dMB\n", file.getContentType(), file.getOriginalFilename(), size/1024/1024);
        return ResponseEntity.ok(true);
    }

    /**
     * React 로 부터 파일을 Stream 형태로 수신하여 해당 서버에서 처리하는 것인데 현재 S3 경로를 직접 반환해서 업로드 하는 방식이기 때문에
     * 해당 방식을 사용하지 않음
     */
    @Deprecated ( since = "release 1.1", forRemoval = true )
    @PostMapping("/chunk_upload")
    public ResponseEntity<Boolean> uploadFileChunk(@RequestParam("fileChunk") MultipartFile chunk,
            @ModelAttribute UserUploadVideoChunk userUploadVideoChunk) throws IOException {

        int totalChunks = userUploadVideoChunk.getChunkSize();
        int chunkIndex = userUploadVideoChunk.getNowChunkIndex();

        System.out.printf("(%s) Bytes Chunk(%d/%d)\n", Thread.currentThread().getName(),totalChunks, chunkIndex);

        Path tempFilePath = videoFileManager.getUserUploadTempFilePath(userUploadVideoChunk);

        String userId = userUploadVideoChunk.getUserId();

        if ( videoServiceManager.isUploadUser(userId) ) {
            videoServiceManager.getUserUploadVideoChunk(userId).addChunkIndex(chunkIndex);
            System.out.println("Update Upload User");
        } else {
            videoServiceManager.addUploadUser(userUploadVideoChunk);
            System.out.println("ADD Upload User");
        }

        Files.copy(chunk.getInputStream(), tempFilePath); // Byte 로 전달된 데이터

        System.out.printf("Chunk File : %s\n", tempFilePath.toFile().getAbsolutePath());

        if ( totalChunks == chunkIndex+1 ) {
            // 만일 모든 청크에 도달했다면
            System.out.println("모든 청크에 도달했습니다 파일 저장을 시도합니다");
            videoFileManager.mergeVideoChunk(userUploadVideoChunk);


            // 이용자를 다른 곳으로 보낸다 그 이후 무언가 작업이 처리되도록 설정
//            Instance instance = awsServiceManager.createEC2Instance(String.format("%s-Mosaic-Compute", userId)); // TODO 해당 작업을 Async 로 던져야함
//
//            VideoProcessingEC2ComputeTask videoProcessingEC2ComputeTask = new VideoProcessingEC2ComputeTask(userUploadVideoChunk, instance);
//
//            instance.state().name();

        }

        return ResponseEntity.ok(true);

    }

}
