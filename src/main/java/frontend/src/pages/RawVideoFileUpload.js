
import React, {useEffect, useState} from 'react';
import axios from 'axios';
import { v4 as uuidv4 } from 'uuid'; // uuid 라이브러리에서 UUID 생성 함수를 가져옵니다.

function RawVideoFileUpload() {

    const [file, setFile] = useState(null);
    const [uploadPercentage, setUploadPercentage] = useState(0); // 업로드 진행률을 추적하는 상태 변수
    const [cancelUpload, setCancelUpload] = useState(null); // 취소 토큰 상태

    // const userId = uuidv4();
    const userId = 'test-Kim_Seonwoo'

    useEffect(() => {
        // 컴포넌트 언마운트 시 실행될 로직
        return () => {
            if (uploadPercentage > 0 && uploadPercentage < 100) {
                // 파일 업로드가 중간에 중단됨
                if ( cancelUpload && cancelUpload() ) {
                    console.log('업로드가 취소되었습니다 언마운트 시 실행될 로직')
                    notifyServerAboutCancellation().then(r => alert('성공적으로 취소를 알립니다')); // 서버에 업로드 취소를 알림
                }
            }
        };
    }, [uploadPercentage, cancelUpload]);

    const handleFileChange = (event) => {
        setFile(event.target.files[0]); // 사용자가 선택한 파일을 설정
        setUploadPercentage(0)
    };

    const notifyServerAboutCancellation = async () => {

        if ( !file) return;

        console.log('업로드가 취소되었습니다 notifyServerAboutCancellation 에서 동작함')

        try {
            const userData = {
                userVideo: {
                    fileName: file.name,
                    userId: userId
                },
                cancel: false
            }
            // 취소 이벤트를 서버에 알림
            await axios.post('http://localhost:8080/api/notify-cancellation', userData, {
                headers: {
                    'Content-Type': 'application/json'
                }
            });
            alert("빠져나갑니다 취소합니다")
        } catch (error) {
            console.error('Error notifying the server about cancellation:', error);
        }
    };

    const uploadFile = async () => {
        if (!file) {
            alert('파일이 업로드 되지 않았습니다');
            return;
        }


        const userData = {
            userVideo: {
                fileName: file.name,
                userId: userId
            },
            cancel: false
        }


        console.log(`filename ${file.name} userId ${userId} cancel ${false}`);

        // 백엔드로부터 사전 서명된 URL 요청
        const response = await axios.post('http://localhost:8080/api/generate-resigned-url', userData,{
            headers: {
                'Content-Type': 'application/json'
            }
        });
        const presignedUrl = response.data;

        console.log(presignedUrl +" <<< URL")

        // 사전 서명된 URL을 사용하여 파일 업로드
        // 사전 서명된 URL을 사용하여 파일 업로드

        const CancelToken = axios.CancelToken;
        const source = CancelToken.source(); // CANCEL 전용

        try {
            const uploadResponse = await axios.put(presignedUrl, file, {
                headers: {
                    'Content-Type': file.type  // 실제 파일의 MIME 타입 사용
                },
                onUploadProgress: progressEvent => {
                    console.log("전체 : " + progressEvent.total +" 현재 ::: " + progressEvent.loaded)
                    const percentCompleted = Math.round((progressEvent.loaded * 100) / progressEvent.total);
                    setUploadPercentage(percentCompleted); // 업로드 진행률 업데이트
                },
                cancelToken: source.token
            });

            if (uploadResponse.status === 200) {

                const userSuccessFileData = {
                    userVideo: {
                        fileName: file.name,
                        userId: userId
                    },
                    url: presignedUrl
                }

                const response = await axios.post('http://localhost:8080/api/success-upload-file'
                    , userSuccessFileData,{
                    headers: {
                        'Content-Type': 'application/json'
                    }
                });

                alert('File uploaded successfully.');
                console.log(uploadResponse.data + " <<<<<< Datas")
            } else {
                throw new Error('File not uploaded successfully');
            }
        } catch ( error ) {
            if ( axios.isCancel(error) ) {
                alert('업로드가 캔슬 되었습니다')
            } else {
                alert('문제 발생 관리자에게 문의하세요')
                console.error(error + " ERROR OCCURED")
            }
        }

    };

    return (
        <div>
            파일을 업로드 해주세요 <br/>
            <input type="file" onChange={handleFileChange} accept=".mp4,.mov" /> <br/>
            <button onClick={uploadFile}>Upload Video</button>
            <br/>
            {file && (
                <div style={{ width: '100%', backgroundColor: '#ddd' }}>
                    <div style={{ height: '20px', backgroundColor: 'green', width: `${uploadPercentage}%` }}>
                        {uploadPercentage}%
                    </div>
                </div>
            )}
        </div>
    );
}

export default RawVideoFileUpload;
