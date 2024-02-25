package gaya.pe.kr.mosaicsystem.deprecated;

import lombok.Getter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

public class VideoUploadTimeOutThread extends Thread {


    final int TIME_OUT_SEC = 5;

    VideoFileManager videoFileManager;

    public VideoUploadTimeOutThread(VideoFileManager videoFileManager) {
        this.videoFileManager = videoFileManager;
    }

    @Getter
    private final HashMap<String, UserUploadVideoChunk> userUploadVideoChunkHashMap = new HashMap<>();
    @Override
    public void run() {

        while ( true ) {
            try {
                Iterator<UserUploadVideoChunk> iterator = userUploadVideoChunkHashMap.values().iterator();

                while (iterator.hasNext()) {
                    UserUploadVideoChunk value = iterator.next();

                    System.out.println(value + " <<< 검사중 : " + value.getUpdateDate().toString());

                    if (value.isUpdateDateAfterSec(TIME_OUT_SEC)) {
                        try {
                            System.out.printf("%d 초가 지나 %s 유저의 %s 파일은 자동 병합 합니다\n", TIME_OUT_SEC, value.getUserId(), value.getFileName());
                            videoFileManager.mergeVideoChunk(value);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                Thread.sleep(1000);
            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }
        }


    }



}
