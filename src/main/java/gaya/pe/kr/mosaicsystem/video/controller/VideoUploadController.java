package gaya.pe.kr.mosaicsystem.video.controller;

import gaya.pe.kr.mosaicsystem.video.entities.UserUploadVideoChunk;
import gaya.pe.kr.mosaicsystem.video.service.VideoServiceManager;
import gaya.pe.kr.mosaicsystem.video.service.io.VideoFileManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/api")
public class VideoUploadController {

    VideoServiceManager videoServiceManager;
    VideoFileManager videoFileManager;

    public VideoUploadController(
            @Autowired VideoServiceManager videoServiceManager,
            @Autowired VideoFileManager videoFileManager
    ) {
        System.out.println("VideoUploadController Create");
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

        Files.copy(chunk.getInputStream(), tempFilePath);
        System.out.printf("Chunk File : %s\n", tempFilePath.toFile().getAbsolutePath());

        if ( totalChunks == chunkIndex+1 ) {
            // 만일 모든 청크에 도달했다면
            System.out.println("모든 청크에 도달했습니다 파일 저장을 시도합니다");
            videoFileManager.mergeVideoChunk(userUploadVideoChunk);
        }

        return ResponseEntity.ok(true);
    }

}
