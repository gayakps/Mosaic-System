package gaya.pe.kr.mosaicsystem.video.thread;

import java.util.HashMap;
import java.util.HashSet;

public class VideoUploadTimeOutThread extends Thread {

    private final HashSet<String> userIdHashSet = new HashSet<>();

    @Override
    public void run() {

    }

    public HashSet<String> getUserIdHashSet() {
        return userIdHashSet;
    }
}
