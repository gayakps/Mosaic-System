package gaya.pe.kr.mosaicsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

import java.util.Set;

@SpringBootApplication
@ConfigurationPropertiesScan // class path 존재하는 모든 ConfigurationProperties Scan(필수❗️)

public class MosaicSystemApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(MosaicSystemApplication.class);
        springApplication.run(args);
    }

}
