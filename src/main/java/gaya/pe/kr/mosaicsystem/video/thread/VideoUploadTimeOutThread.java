package gaya.pe.kr.mosaicsystem.video.thread;

import gaya.pe.kr.mosaicsystem.video.entities.UserUploadVideoChunk;
import lombok.Getter;

import java.util.HashMap;
import java.util.HashSet;

public class VideoUploadTimeOutThread extends Thread {


    @Getter
    private final HashMap<String, UserUploadVideoChunk> userUploadVideoChunkHashMap = new HashMap<>();


    @Override
    public void run() {

    }



}
