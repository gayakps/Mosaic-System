package gaya.pe.kr.mosaicsystem.video.service;

import gaya.pe.kr.mosaicsystem.video.entities.UserUploadVideoChunk;
import gaya.pe.kr.mosaicsystem.video.service.io.VideoFileManager;
import gaya.pe.kr.mosaicsystem.video.thread.VideoUploadTimeOutThread;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VideoServiceManager {


    VideoUploadTimeOutThread videoUploadTimeOutThread;

    VideoFileManager videoFileManager;

    public VideoServiceManager(@Autowired VideoFileManager videoFileManager) {
        this.videoFileManager = videoFileManager;
        this.videoFileManager.init(this);
        videoUploadTimeOutThread = new VideoUploadTimeOutThread(videoFileManager);
        videoUploadTimeOutThread.start();
    }

    public void addUploadUser(UserUploadVideoChunk userUploadVideoChunk) {
        String userId = userUploadVideoChunk.getUserId();
        videoFileManager.getChunkFolder(userUploadVideoChunk).mkdirs(); // Chunk 폴더를 만들어줍니다
        videoFileManager.getResultFolder(userUploadVideoChunk).mkdirs(); // Result Folder 를 만들어 줍니다
        videoUploadTimeOutThread.getUserUploadVideoChunkHashMap().put(userId, userUploadVideoChunk);
    }

    public boolean isUploadUser(String userId) {
        return videoUploadTimeOutThread.getUserUploadVideoChunkHashMap().containsKey(userId);
    }

    public void removeUploadUser(String userId) {
        videoUploadTimeOutThread.getUserUploadVideoChunkHashMap().remove(userId);
    }

    public UserUploadVideoChunk getUserUploadVideoChunk(String userId) {
        return videoUploadTimeOutThread.getUserUploadVideoChunkHashMap().get(userId);
    }

}
