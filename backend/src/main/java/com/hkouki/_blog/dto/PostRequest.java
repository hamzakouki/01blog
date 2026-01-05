package com.hkouki._blog.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class PostRequest {

    @NotBlank(message = "Content is required")
    private String content;

    // Optional media file (image or video)
    private MultipartFile media;
}
