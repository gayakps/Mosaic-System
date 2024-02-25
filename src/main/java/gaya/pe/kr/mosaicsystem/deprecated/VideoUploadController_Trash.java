package gaya.pe.kr.mosaicsystem.deprecated;

import gaya.pe.kr.mosaicsystem.aws.manager.AWSServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("/api")
public class VideoUploadController_Trash {

    VideoServiceManager videoServiceManager;
    VideoFileManager videoFileManager;

    AWSServiceManager awsServiceManager;

    public VideoUploadController_Trash(
            @Autowired AWSServiceManager awsServiceManager,
            @Autowired VideoServiceManager videoServiceManager,
            @Autowired VideoFileManager videoFileManager
    ) {
        System.out.println("VideoUploadController Create");
        this.awsServiceManager = awsServiceManager;
        this.videoServiceManager = videoServiceManager;
        this.videoFileManager = videoFileManager;
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
        }

        return ResponseEntity.ok(true);

    }

}
