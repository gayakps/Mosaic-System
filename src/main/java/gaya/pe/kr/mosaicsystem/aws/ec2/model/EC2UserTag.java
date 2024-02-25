package gaya.pe.kr.mosaicsystem.aws.ec2.model;

import gaya.pe.kr.mosaicsystem.infra.util.TimeUtil;
import gaya.pe.kr.mosaicsystem.video.entities.UserSuccessUploadNotify;

import java.util.Base64;
import java.util.List;

public class EC2UserTag {

    StringBuilder stringBuilder = new StringBuilder();

    public EC2UserTag() {
        stringBuilder.append("#!/bin/bash\n");
    }

    public void addLine(String line) {
        stringBuilder.append(line).append("\n");
    }

    public void addLines(String... lines) {
        for (String line : lines) {
            addLine(line);
        }
    }

    //    - "(%time%) echo %user_id% 님의 새로운 파일인 %file_name% 을 다운로드 합니다"
    //    - "aws s3 cp s3://%bucket_name%/%user_id%/'%file_name%' /home/ubuntu/'%file_name%'"
    //    - "다운로드 완료"
    public void addLines(List<String> list, String bucketName, UserSuccessUploadNotify userSuccessUploadNotify) {
        for (String s : list) {
            s = s
                    .replace("%time%", TimeUtil.getNow())
                    .replace("%file_name%", userSuccessUploadNotify.getUserVideo().getFileName())
                    .replace("%bucket_name%", bucketName)
                    .replace("%user_id%", userSuccessUploadNotify.getUserVideo().getUserId());

            addLine(s);
        }
    }


    public String getValue() {
        return Base64.getEncoder().encodeToString(stringBuilder.toString().getBytes());
    }



}
