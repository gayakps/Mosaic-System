package gaya.pe.kr.mosaicsystem.aws.manager;

import gaya.pe.kr.mosaicsystem.aws.ec2.manager.AWSEC2Manager;
import gaya.pe.kr.mosaicsystem.aws.s3.manager.AWSS3Manager;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.s3.S3Client;

@Service
@Getter
public class AWSServiceManager {


    Ec2Client ec2Client;
    CloudWatchClient cloudWatchClient;
    S3Client s3Client;

    AWSEC2Manager awsec2Manager;
    AWSS3Manager awss3Manager;

    public AWSServiceManager(
            @Autowired AWSEC2Manager awsec2Manager,
            @Autowired AWSS3Manager awss3Manager
    ) {
        this.ec2Client = Ec2Client.builder()
                .region(Region.AP_NORTHEAST_2)
                //.credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        this.cloudWatchClient = CloudWatchClient
                .builder()
                .region(Region.AP_NORTHEAST_2)
                //.credentialsProvider(ProfileCredentialsProvider.create())
                .build();
        this.s3Client = S3Client.builder()
                .region(Region.AP_NORTHEAST_2)
                .build();

        this.awsec2Manager = awsec2Manager;
        this.awss3Manager = awss3Manager;


    }







}
