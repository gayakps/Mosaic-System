package gaya.pe.kr.mosaicsystem.aws.manager.ec2;

import gaya.pe.kr.mosaicsystem.aws.manager.ec2.model.EC2UserTag;
import jakarta.annotation.Nullable;
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

@Service
public class AWSEC2Manager {


    @Nullable
    public Instance createEC2Instance(Ec2Client ec2Client, String name, EC2UserTag ec2UserTag) {

        IamInstanceProfileSpecification iamInstanceProfile = IamInstanceProfileSpecification.builder()
                .name("Mosaic_EC2_S3_Full_Access")  // IAM 역할 이름 지정
                .build();

        RunInstancesRequest runRequest = RunInstancesRequest.builder()
                .imageId("ami-0f3a440bbcff3d043")
                .instanceType(InstanceType.T2_MICRO)
                .maxCount(1)
                .minCount(1)
                .securityGroups("MosaicUserAdd")
                .ebsOptimized(false)
                .userData(ec2UserTag.getValue())
                .iamInstanceProfile(iamInstanceProfile)
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
            System.out.printf("START EC2 Name : %s Tag : %s\n", name, new String(Base64.getDecoder().decode(ec2UserTag.getValue())) );
            System.out.printf("Successfully started EC2 Instance %s based on AMI %s", instanceId, "none");
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
