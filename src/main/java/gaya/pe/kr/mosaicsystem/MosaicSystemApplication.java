package gaya.pe.kr.mosaicsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Set;

@SpringBootApplication
public class MosaicSystemApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(MosaicSystemApplication.class);
        springApplication.run(args);
    }

}
