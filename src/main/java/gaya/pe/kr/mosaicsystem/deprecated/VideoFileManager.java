package gaya.pe.kr.mosaicsystem.deprecated;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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

    public void test(UserUploadVideoChunk userUploadVideoChunk) throws IOException {

        int totalChunks = userUploadVideoChunk.getChunkSize();
        String userId = userUploadVideoChunk.getUserId();
        String fileUUID = userUploadVideoChunk.getFileUUID();
        String fileName = userUploadVideoChunk.getFileName();

        Path resultFolderPath = Paths.get(TEMP_PATH.toString(), userId, fileUUID);
        Path resultFilePath = resultFolderPath.resolve(fileName + "_Result.mov");

        // 결과 파일을 위한 비동기 파일 채널 열기
        try (AsynchronousFileChannel resultFileChannel = AsynchronousFileChannel.open(resultFilePath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
            long position = 0; // 파일 쓰기 시작 위치

            for (int i = 0; i < totalChunks; i++) {
                Path chunkFilePath = Paths.get(TEMP_PATH.toString(), userId, fileUUID, fileName + ".part" + i);

                if (!Files.exists(chunkFilePath)) {
                    System.out.printf("File not exist : %s\n", chunkFilePath);
                    break;
                }

                // 청크 파일을 비동기적으로 읽기
                try (AsynchronousFileChannel chunkFileChannel = AsynchronousFileChannel.open(chunkFilePath, StandardOpenOption.READ)) {
                    ByteBuffer buffer = ByteBuffer.allocate(1024); // 버퍼 크기는 필요에 따라 조정
                    Future<Integer> operation; // Future
                    int bytesRead;

                    while ((bytesRead = chunkFileChannel.read(buffer, 0).get()) > 0) {
                        buffer.flip();
                        operation = resultFileChannel.write(buffer, position);
                        operation.get(); // 쓰기 작업 완료 대기
                        position += bytesRead;
                        buffer.clear();
                    }
                } catch (ExecutionException | InterruptedException e) {
                    throw new RuntimeException(e);
                }

                Files.delete(chunkFilePath); // 청크 파일 삭제
                System.out.printf("Delete File : %s\n", chunkFilePath);
            }

            // 모든 청크 처리 후 청크 폴더 삭제
            Files.deleteIfExists(Paths.get(TEMP_PATH.toString(), userId, fileUUID));
            System.out.println("Chunk Folder Delete");
            
        }
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
