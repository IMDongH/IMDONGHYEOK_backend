package com.practice.core.api.config;

import com.practice.core.support.user.UserInfo;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    static {
        SpringDocUtils.getConfig().addRequestWrapperToIgnore(UserInfo.class);
    }
}
