package com.example.crazy_chat.controller;

import com.example.crazy_chat.dto.file.input.CompleteMultipartRequest;
import com.example.crazy_chat.dto.file.input.InitMultipartUploadRequest;
import com.example.crazy_chat.dto.file.output.MultipartUploadResponse;
import com.example.crazy_chat.service.S3FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Service
@Controller
@RequiredArgsConstructor
public class S3FileController {

    private final S3FileService s3FileService;

    @PostMapping("initiate")
    public MultipartUploadResponse initiateUpload(@RequestBody InitMultipartUploadRequest request) {
        return null;
    }


    @PostMapping("/complete")
    public void completeUpload(@RequestBody CompleteMultipartRequest request) {

    }


    @GetMapping("/download")
    public String getDownloadLink(@RequestParam String fileName) {
        return null;
    }

}
