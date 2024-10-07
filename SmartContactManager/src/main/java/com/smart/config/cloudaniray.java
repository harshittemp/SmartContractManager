package com.smart.config;

import com.cloudinary.Cloudinary;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class cloudaniray {

    @Bean
    public Cloudinary getCloudanary()
    {
        Map map=new HashMap();
        map.put("cloud_name","dgjf3kzzw");
        map.put("api_key","922493925298335");
        map.put("api_secret","-U3CHXw6YeMWuhiMzt00sgei_Kc");
        return new Cloudinary(map);
    }
}
