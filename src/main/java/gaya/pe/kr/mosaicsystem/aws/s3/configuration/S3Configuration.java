package gaya.pe.kr.mosaicsystem.aws.s3.configuration;

import gaya.pe.kr.mosaicsystem.infra.factory.YamlPropertySourceFactory;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@ConfigurationProperties(prefix = "s3")
@Getter
@Setter
@ToString
@PropertySource(value = "classpath:aws/aws-s3-config.yml", factory = YamlPropertySourceFactory.class)
public class S3Configuration {

    private static final Logger logger = LoggerFactory.getLogger(S3Configuration.class);

    private String rawVideoContentsMosaicUserUploadBucketName;
    private int presignedURLValidMinutes;

    public S3Configuration() {
        logger.info(this.toString() + " Created");
    }
}
