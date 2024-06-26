package gaya.pe.kr.mosaicsystem.aws.ec2.configuration;

import gaya.pe.kr.mosaicsystem.aws.ec2.manager.AWSEC2Manager;
import gaya.pe.kr.mosaicsystem.infra.factory.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "ec2")
@PropertySource( value = "classpath:aws/application-aws-ec2-config.yml", factory = YamlPropertySourceFactory.class)
@ToString
@Getter
@Setter

public class EC2Configuration {

   private static final Logger logger = LoggerFactory.getLogger(EC2Configuration.class);

   private String imageId;
   private String instanceType;
   private int maxCount;
   private int minCount;
   private String iamRoleName;
   private String securityGroup;
   private boolean ebsOptimized;
   private List<String> userTag;

}
