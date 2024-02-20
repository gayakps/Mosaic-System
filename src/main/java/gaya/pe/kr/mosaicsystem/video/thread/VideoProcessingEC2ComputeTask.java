package gaya.pe.kr.mosaicsystem.video.thread;

import gaya.pe.kr.mosaicsystem.video.entities.UserUploadVideoChunk;
import software.amazon.awssdk.services.ec2.model.Instance;

/**
 * 해당 클래스는 모든 데이터가 수신된 후 EC2 가 켜지길 기다렸다가 켜지면 작업을 진행하도록 하는 Thread 입니다
 */
public class VideoProcessingEC2ComputeTask implements Runnable {

    UserUploadVideoChunk userUploadVideoChunk;
    Instance instance;

    @Override
    public void run() {

        while ( true ) {

            try {


                Thread.sleep(500); // 0.5 초 마다 작동


            } catch ( InterruptedException e ) {
                e.printStackTrace();
            }

        }

    }
}
