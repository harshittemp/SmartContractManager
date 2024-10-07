package com.smart.Imp;

import com.cloudinary.Cloudinary;
import com.smart.service.CloudanirayImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryImageServiceImpl implements CloudanirayImageService {

    @Autowired
    private Cloudinary cloudinary;
    @Override
    public Map upload(MultipartFile file) {
        try {
          Map data=  this.cloudinary.uploader().upload(file.getBytes(),Map.of());
            return data;
        } catch (IOException e) {
           throw new RuntimeException("Image Uploading Failed");
        }

    }
}
