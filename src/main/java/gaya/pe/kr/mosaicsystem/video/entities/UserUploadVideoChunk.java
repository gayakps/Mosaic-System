package gaya.pe.kr.mosaicsystem.video.entities;


import gaya.pe.kr.mosaicsystem.infra.util.TimeUtil;
import lombok.*;

import java.util.Date;

@Getter // 모든 필드에 대해 getter를 생성
@RequiredArgsConstructor // final 또는 @NonNull 필드에 대해 생성자를 생성
@ToString
@EqualsAndHashCode
public class UserUploadVideoChunk {

    @NonNull
    private final String userId;
    @NonNull
    private final String fileUUID;
    @NonNull
    private final String fileName;

    private final int chunkSize;

    @Setter
    private int nowChunkIndex; // 변경 가능하도록 setter 제공

    @Setter
    private Date updateDate = new Date(); // 필요에 따라 변경 가능

    @Synchronized
    public boolean addChunkIndex(int nowChunkIndex) {
        setUpdateDate(new Date());
        nowChunkIndex++;
        return nowChunkIndex >= chunkSize - 1;
    }


    public boolean isUpdateDateAfterSec(int sec) {
        return TimeUtil.getTimeDiffSec(getUpdateDate()) >= sec;
    }

}
