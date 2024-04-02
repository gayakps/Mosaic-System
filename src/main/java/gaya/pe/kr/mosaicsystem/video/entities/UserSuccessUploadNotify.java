package gaya.pe.kr.mosaicsystem.video.entities;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.io.UnsupportedEncodingException;
import java.net.URL;

@Getter // 모든 필드에 대해 getter를 생성
@RequiredArgsConstructor // final 또는 @NonNull 필드에 대해 생성자를 생성
@ToString
@EqualsAndHashCode
public class UserSuccessUploadNotify {

    private UserVideo userVideo;
    private URL url;


}
