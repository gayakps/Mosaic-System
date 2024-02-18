package gaya.pe.kr.mosaicsystem.video.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;

@Controller
@RequestMapping("/api")
public class VideoUploadController {
    public VideoUploadController() {
        System.out.println("VideoUploadController Create");
    }

    @PostMapping("/upload")
    public ResponseEntity<Boolean> handleFileUpload(@RequestParam("file")MultipartFile file) throws IOException {
        long size = file.getSize();
        System.out.printf("Content Type : %s Name : [%s] Size : %dMB\n", file.getContentType(), file.getOriginalFilename(), size/1024/1024);
        return ResponseEntity.ok(true);
    }


    @PostMapping("/chunk")
    public ResponseEntity<Boolean> uploadFileChunk(
            @RequestParam("fileChunk") MultipartFile chunk,
            @RequestParam("filename") String filename,
            @RequestParam("chunkIndex") Integer chunkIndex,
            @RequestParam("totalChunks") Integer totalChunks) throws IOException {

        System.out.printf("Bytes Chunk(%d/%d)\n",totalChunks, chunkIndex);

        Path tempPath = Path.of("/Users/seonwoo/Desktop/temp/"+filename + ".part" + chunkIndex);
        File tempFile = tempPath.toFile();
        Files.copy(chunk.getInputStream(), tempPath);
        System.out.printf("Chunk File : %s\n", tempFile.getAbsolutePath());

        if ( totalChunks == chunkIndex+1 ) {
            // 만일 모든 청크에 도달했다면
            System.out.println("모든 청크에 도달했습니다 파일 저장을 시도합니다");

            // All chunks are uploaded, start merging
            File mergedFile = new File(tempPath.getParent().resolve(filename+"_After_Save.mov").toString());
            try (FileOutputStream fos = new FileOutputStream(mergedFile, true)) {
                for (int i = 0; i < totalChunks; i++) {
                    File partFile = new File(tempPath.getParent().resolve(filename + ".part" + i).toString());
                    Files.copy(partFile.toPath(), fos);
                    partFile.delete(); // Delete the part file after merging
                    System.out.printf("Delete File : %s\n", partFile.getAbsolutePath());
                }
            }

//            // After merging all chunks, delete the temp directory
//            Files.walk(tempPath)
//                    .sorted(Comparator.reverseOrder())
//                    .map(Path::toFile)
//                    .forEach(File::delete);


            System.out.printf("Path : %s 에 영상을 저장했습니다\n", mergedFile.getAbsolutePath());

        }

        return ResponseEntity.ok(true);
    }

}
