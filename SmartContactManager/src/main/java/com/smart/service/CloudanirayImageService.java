package com.smart.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface CloudanirayImageService {

    public Map upload(MultipartFile file);
}
