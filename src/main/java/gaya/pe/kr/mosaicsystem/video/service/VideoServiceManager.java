package gaya.pe.kr.mosaicsystem.video.service;

import gaya.pe.kr.mosaicsystem.video.thread.VideoUploadTimeOutThread;
import org.springframework.stereotype.Service;

@Service
public class VideoServiceManager {


    VideoUploadTimeOutThread videoUploadTimeOutThread;

    public VideoServiceManager() {

        videoUploadTimeOutThread = new VideoUploadTimeOutThread();

    }

    public void addUploadUser(String userId) {
        videoUploadTimeOutThread.getUserIdHashSet().add(userId);
    }

    public boolean isUploadUser(String userId) {
        return videoUploadTimeOutThread.getUserIdHashSet().contains(userId);
    }

    public void removeUploadUser(String userId) {
        videoUploadTimeOutThread.getUserIdHashSet().remove(userId);
    }


}
