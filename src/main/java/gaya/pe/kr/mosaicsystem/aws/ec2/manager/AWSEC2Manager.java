package gaya.pe.kr.mosaicsystem.aws.ec2.manager;

import gaya.pe.kr.mosaicsystem.aws.ec2.configuration.EC2Configuration;
import gaya.pe.kr.mosaicsystem.aws.ec2.model.EC2UserTag;
import gaya.pe.kr.mosaicsystem.aws.s3.configuration.S3Configuration;
import gaya.pe.kr.mosaicsystem.infra.util.LogColor;
import gaya.pe.kr.mosaicsystem.video.controller.VideoUploadController;
import gaya.pe.kr.mosaicsystem.video.entities.UserSuccessUploadNotify;
import jakarta.annotation.Nullable;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.cloudtrail.model.LookupAttribute;
import software.amazon.awssdk.services.cloudtrail.model.LookupAttributeKey;
import software.amazon.awssdk.services.cloudtrail.model.LookupEventsRequest;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.*;
import software.amazon.awssdk.services.ec2.Ec2Client;
import software.amazon.awssdk.services.ec2.model.*;
import software.amazon.awssdk.services.ec2.model.Tag;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;

@Service
public class AWSEC2Manager {

    private static final Logger logger = LoggerFactory.getLogger(AWSEC2Manager.class);
    private final EC2Configuration ec2Configuration;
    private final S3Configuration s3Configuration;

    public AWSEC2Manager(@Qualifier("ec2-gaya.pe.kr.mosaicsystem.aws.ec2.configuration.EC2Configuration") @Autowired EC2Configuration ec2Configuration, @Autowired S3Configuration s3Configuration) {
        this.ec2Configuration = ec2Configuration;
        this.s3Configuration = s3Configuration;
    }

    @Nullable
    public Instance createEC2Instance(UserSuccessUploadNotify userSuccessUploadNotify, Ec2Client ec2Client, String name, int videoId) {

        EC2UserTag ec2UserTag = new EC2UserTag();

        logger.info("@@@@ TEST(S3 INFO) :: {} @@@@", s3Configuration.toString());
        logger.info("@@@@ TEST(EC2 INFO) :: {} @@@@", ec2Configuration.toString());

        ec2UserTag.addLines(ec2Configuration.getUserTag(), s3Configuration.getRawVideoContentsMosaicUserUploadBucketName(), userSuccessUploadNotify, videoId);

        IamInstanceProfileSpecification iamInstanceProfile = IamInstanceProfileSpecification.builder()
                .name(ec2Configuration.getIamRoleName())  // IAM 역할 이름 지정
                .build();

        RunInstancesRequest runRequest = RunInstancesRequest.builder()
                .imageId(ec2Configuration.getImageId())
                .instanceType(InstanceType.valueOf(ec2Configuration.getInstanceType().toUpperCase(Locale.ROOT).replace(".", "_")))
                .maxCount(ec2Configuration.getMaxCount())
                .minCount(ec2Configuration.getMinCount())
                .securityGroups(ec2Configuration.getSecurityGroup())
                .ebsOptimized(ec2Configuration.isEbsOptimized())
                .userData(ec2UserTag.getValue())
                .iamInstanceProfile(iamInstanceProfile)
                .keyName("Mosaic-Python-AMI-Key-Pair")
                .build();

        RunInstancesResponse response = ec2Client.runInstances(runRequest);

        Instance instance = response.instances().get(0);
        String instanceId = instance.instanceId();


        Tag tag = Tag.builder()
                .key("Name")
                .value(name)
                .build();

        CreateTagsRequest tagRequest = CreateTagsRequest.builder()
                .resources(instanceId)
                .tags(tag)
                .build();

        try {
            ec2Client.createTags(tagRequest);
            logger.info("Mosaic-EC2-{} Start InstanceId-{} UserId-{} File_Name-{}",
                    LogColor.addColor(name, LogColor.GREEN),
                    LogColor.addColor(instanceId, LogColor.YELLOW),
                    LogColor.addColor(userSuccessUploadNotify.getUserVideo().getUserId(), LogColor.RED),
                    LogColor.addColor(userSuccessUploadNotify.getUserVideo().getFileName(), LogColor.BLUE)
            );
            return instance;
        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
        }

        return null;

    }

    public void stopInstance(Ec2Client ec2, String instanceId) {

        StopInstancesRequest request = StopInstancesRequest.builder()
                .instanceIds(instanceId)
                .build();

        ec2.stopInstances(request);
        System.out.printf("Successfully stopped instance %s", instanceId);

    }


    @Nullable
    public Instance getTargetInstance(Ec2Client ec2Client, String instanceId) {

        DescribeInstancesRequest request = DescribeInstancesRequest.builder().maxResults(1).nextToken("").build();
        DescribeInstancesResponse response = ec2Client.describeInstances(request);

        for (Reservation reservation : response.reservations()) {
            for (Instance instance : reservation.instances()) {
                if (instanceId.equals(instance.instanceId())) {
                    return instance;
                }
            }
        }

        return null;

    }

    public List<Instance> getAllowAllInstance(Ec2Client ec2) {

        boolean done = false;
        String nextToken = null;

        List<Instance> instances = new ArrayList<>();

        try {

            do {
                DescribeInstancesRequest request = DescribeInstancesRequest.builder().maxResults(6).nextToken(nextToken).build();
                DescribeInstancesResponse response = ec2.describeInstances(request);

                for (Reservation reservation : response.reservations()) {
                    for (Instance instance : reservation.instances()) {
//                        System.out.println("---------------------------------------------");
//                        System.out.println("Instance Id is " + instance.instanceId());
//                        System.out.printf("Instance Name : %s\n", instance.keyName());
//                        System.out.println("Image id is "+  instance.imageId());
//                        System.out.println("Instance type is "+  instance.instanceType());
//                        System.out.println("Instance state name is "+  instance.state().name());
//                        System.out.println("monitoring information is "+  instance.monitoring().state());
//                        System.out.println("Launch Time: " + instance.launchTime());
//                        System.out.println("IP : " + instance.publicIpAddress());
//                        System.out.println("HOST : " + instance.publicDnsName());

                        String instanceId = instance.instanceId();

                        LookupAttribute resourceNameAttribute = LookupAttribute.builder()
                                .attributeKey(LookupAttributeKey.RESOURCE_NAME)
                                .attributeValue(instanceId)
                                .build();


                        LookupEventsRequest eventsRequest = LookupEventsRequest.builder()
                                .lookupAttributes(LookupAttribute.builder()
                                        .attributeValue("StopInstances")
                                        .build())
                                .build();

//                        LookupEventsResponse eventsResponse = getCloudTrailClient().lookupEvents(eventsRequest);
//
//                        for(Event event : eventsResponse.events()){
//                            System.out.println("Stopped instance: " + event.resources().get(0));
//                            System.out.println("Event time: " + event.eventTime());
//                        }


                        instances.add(instance);
                    }
                }
                nextToken = response.nextToken();
            } while (nextToken != null);

        } catch (Ec2Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }

        return instances;

    }

    public void getInstanceState(CloudWatchClient cloudWatchClient, String instanceId, String namespace, String metricName) {

        int period = 30;
        String stat = "Average";

        ZoneId ZONE_ID = ZoneId.of("Asia/Seoul");
        int dayRange = 3;
        Instant startTimeInstant = LocalDate.now(ZONE_ID).minusDays(dayRange).atStartOfDay(ZONE_ID).toInstant();
        Instant endTimeInstant = ZonedDateTime.now(ZONE_ID).toInstant();

        List<MetricDataQuery> metricDataQueryList = new ArrayList<>();
        int numbering = 0;
        Dimension dimension = Dimension.builder().name("InstanceId").value(instanceId).build();
        Metric metric = Metric.builder().namespace(namespace).dimensions(dimension).metricName(metricName).build();
        MetricStat metricStat = MetricStat.builder().metric(metric).period(period).stat(stat).build();
        MetricDataQuery metricDataQuery = MetricDataQuery.builder().metricStat(metricStat).id("m" + (numbering++)).build();
        metricDataQueryList.add(metricDataQuery);

        GetMetricDataRequest request = GetMetricDataRequest.builder().metricDataQueries(metricDataQueryList)
                .startTime(startTimeInstant)
                .endTime(endTimeInstant)
                .build();
        GetMetricDataResponse response = cloudWatchClient.getMetricData(request);

        List<Instant> timestamps = null;
        List<Double> values = null;

        for (MetricDataResult result : response.metricDataResults()) {
            timestamps = result.timestamps();
            values = result.values();
            System.out.printf("id : %s%n", result.id());
            for (int i = values.size() - 1; i >= 0; i--) {
                System.out.printf("timestamp : %s, value : %s%n", timestamps.get(i).atZone(ZONE_ID), values.get(i));
            }
        }

    }

}
