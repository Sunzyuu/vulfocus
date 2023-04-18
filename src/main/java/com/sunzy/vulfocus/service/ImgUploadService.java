package com.sunzy.vulfocus.service;

import com.sunzy.vulfocus.common.Result;
import org.springframework.web.multipart.MultipartFile;

public interface ImgUploadService {
    Result upload(MultipartFile file);
}
