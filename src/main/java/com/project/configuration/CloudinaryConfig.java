package com.project.configuration;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

@Configuration
public class CloudinaryConfig {

    @Bean
    public Cloudinary cloudinary() {
        Map<String, String> config = ObjectUtils.asMap(
                "cloud_name", "dd0gbtqw9",
                "api_key", "977948938517315",
                "api_secret", "5tIa3dIcyTn2vn5_kpOOSyFAFb4",
                "secure", true
        );
        return new Cloudinary(config);  //
    }
}
