package gaya.pe.kr.mosaicsystem.video.service;

import gaya.pe.kr.mosaicsystem.video.entities.UserUploadVideoChunk;
import gaya.pe.kr.mosaicsystem.video.thread.VideoUploadTimeOutThread;
import org.springframework.stereotype.Service;

@Service
public class VideoServiceManager {


    VideoUploadTimeOutThread videoUploadTimeOutThread;

    public VideoServiceManager() {

        UserUploadVideoChunk userUploadVideoChunk = new UserUploadVideoChunk("", 0);
        videoUploadTimeOutThread = new VideoUploadTimeOutThread();

    }

    public void addUploadUser(String userId, int chunkSize) {
        videoUploadTimeOutThread.getUserUploadVideoChunkHashMap().put(userId, new UserUploadVideoChunk(userId, chunkSize));
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
