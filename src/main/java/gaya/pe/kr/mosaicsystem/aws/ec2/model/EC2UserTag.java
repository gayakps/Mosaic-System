package gaya.pe.kr.mosaicsystem.aws.ec2.model;

import java.util.Base64;

public class EC2UserTag {


    StringBuilder stringBuilder = new StringBuilder();

    public EC2UserTag() {
        stringBuilder.append("#!/bin/bash\n");
    }

    public void addLine(String line) {
        stringBuilder.append(line).append("\n");
    }

    public String getValue() {
        return Base64.getEncoder().encodeToString(stringBuilder.toString().getBytes());
    }



}
