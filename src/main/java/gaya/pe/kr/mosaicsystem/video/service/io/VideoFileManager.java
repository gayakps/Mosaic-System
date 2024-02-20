package gaya.pe.kr.mosaicsystem.video.service.io;

import gaya.pe.kr.mosaicsystem.video.entities.UserUploadVideoChunk;
import gaya.pe.kr.mosaicsystem.video.service.VideoServiceManager;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Getter
@Service
public final class VideoFileManager {

    final String DEFAULT_PATH = "/Users/seonwoo/Desktop/";

    final Path TEMP_PATH = Path.of(DEFAULT_PATH+"/temp");

    final Path RESULT_PATH = Path.of(DEFAULT_PATH+"/result");

    VideoServiceManager videoServiceManager;

    public void init(VideoServiceManager videoServiceManager) {
        this.videoServiceManager = videoServiceManager;
    }

    public void mergeVideoChunk(UserUploadVideoChunk userUploadVideoChunk) throws IOException {

        int totalChunks = userUploadVideoChunk.getChunkSize();
        String userId = userUploadVideoChunk.getUserId();
        String fileUUID = userUploadVideoChunk.getFileUUID();
        String fileName = userUploadVideoChunk.getFileName();

        // All chunks are uploaded, start merging
        File resultFolder = getResultFolder(userUploadVideoChunk);
        File resultFile = new File(resultFolder + "/" + userUploadVideoChunk.getFileName() +"_Result.mov");

        try (FileOutputStream fos = new FileOutputStream(resultFile, true)) {

            File chunkFolder = getChunkFolder(userUploadVideoChunk);

            for (int i = 0; i < totalChunks; i++) {
                File chunkFile = new File(String.format("%s/%s/%s/%s.part%d", TEMP_PATH, userId, fileUUID, fileName, i));

                if ( !chunkFile.exists() ) {
                    System.out.printf("File not exist : %s\n", chunkFile.getAbsolutePath());
                    break;
                }

                Files.copy(chunkFile.toPath(), fos);
                chunkFile.delete(); // Delete the part file after merging
//                System.out.printf("Delete File : %s\n", chunkFile.getAbsolutePath());
            }

            if ( chunkFolder.exists() ) {
                chunkFolder.delete();
                System.out.printf("Chunk Folder Delete\n");
            }

        }

        System.out.printf("Path : %s 에 영상을 저장했습니다\n", resultFile.getAbsolutePath());
        videoServiceManager.removeUploadUser(userUploadVideoChunk.getUserId());

    }

    public File getChunkFolder(UserUploadVideoChunk userUploadVideoChunk) {
        return new File(String.format("%s/%s/%s", TEMP_PATH, userUploadVideoChunk.getUserId(), userUploadVideoChunk.getFileUUID()));
    }

    public File getResultFolder(UserUploadVideoChunk userUploadVideoChunk) {
        return new File(String.format("%s/%s/%s",RESULT_PATH, userUploadVideoChunk.getUserId(), userUploadVideoChunk.getFileUUID()));
    }

    public Path getUserUploadTempFilePath(UserUploadVideoChunk userUploadVideoChunk) {
        return Path.of(getChunkFolder(userUploadVideoChunk) + "/" + userUploadVideoChunk.getFileName() + ".part" + userUploadVideoChunk.getNowChunkIndex());
    }


}
