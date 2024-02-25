import React, { useState } from 'react';
import { v4 as uuidv4 } from 'uuid'; // uuid 라이브러리에서 UUID 생성 함수를 가져옵니다.
import axios from 'axios';

function FileUploader() {
    const [selectedFile, setSelectedFile] = useState(null);
    const chunkSize = 5 * 1024 * 1024; // 5MB
    const uniqueFileUUID = uuidv4(); // 파일 업로드 세션마다 고유한 UUID를 생성합니다.

    const handleFileChange = (event) => {
        setSelectedFile(event.target.files[0]);
    };

    const handleUpload = async () => {
        if (!selectedFile) {
            alert('Please select a file first!');
            return;
        }

        const chunks = chunkFile(selectedFile, chunkSize);
        const totalChunks = chunks.length;
        const filename = selectedFile.name;

        for (let index = 0; index < totalChunks; index++) {
            const formData = new FormData();
            formData.append('userId', "kim_seon_woo")
            formData.append('fileUUID', uniqueFileUUID); // 생성된 UUID를 폼 데이터에 추가합니다.
            formData.append('fileName', filename);
            formData.append('chunkSize', totalChunks);
            formData.append('nowChunkIndex', index);
            formData.append('fileChunk', chunks[index]);


            try {
                await axios.post('http://localhost:8080/api/chunk_upload', formData, {
                    headers: {
                        'Content-Type': 'multipart/form-data',
                    },
                });
                console.log(`Chunk ${index + 1} of ${totalChunks} uploaded`);
            } catch (error) {
                console.error(`Error uploading chunk ${index + 1}:`, error);
                alert(`Error uploading chunk ${index + 1}`);
                return;
            }
        }

        alert('File uploaded successfully');
    };

    // Helper function to split the file into chunks
    function chunkFile(file, size) {
        let chunks = [];
        let count = Math.ceil(file.size / size);

        for (let i = 0; i < count; i++) {
            let start = i * size;
            let end = Math.min(start + size, file.size);
            chunks.push(file.slice(start, end));
        }

        return chunks;
    }

    return (
        <div>
            <input type="file" accept=".mov" onChange={handleFileChange} />
            <button onClick={handleUpload}>Upload File</button>
        </div>
    );
}

export default FileUploader;
