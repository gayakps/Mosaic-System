package gaya.pe.kr.mosaicsystem.aws;

import gaya.pe.kr.mosaicsystem.aws.ec2.manager.AWSEC2Manager;
import gaya.pe.kr.mosaicsystem.aws.manager.AWSServiceManager;
import gaya.pe.kr.mosaicsystem.video.entities.UserSuccessUploadNotify;
import gaya.pe.kr.mosaicsystem.video.entities.UserVideo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.services.ec2.Ec2Client;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Random;

@SpringBootTest

public class AWSInstanceCreateTest {

    @Autowired
    AWSServiceManager awsServiceManager;
    @Test
    public void createEC2FromUserUpload() throws MalformedURLException {

        AWSEC2Manager awsec2Manager = awsServiceManager.getAwsec2Manager();

        Ec2Client ec2Client = awsServiceManager.getEc2Client();

        UserSuccessUploadNotify successUploadNotify = new UserSuccessUploadNotify();

        URL url = new URL("https://mosaic-user-upload.s3.ap-northeast-2.amazonaws.com/test-Kim_Seonwoo/0_Aiport.mov");
        successUploadNotify.setUrl(url);
        UserVideo userVideo = new UserVideo("test-Kim_Seonwoo", "Aiport.mov");
        successUploadNotify.setUserVideo(userVideo);

        awsec2Manager.createEC2Instance(successUploadNotify, ec2Client, "Test-"+ new Random().nextInt(1000)+1, 0);

    }


}
