package org.example.climatica.secure;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class FilterConfig {

    @Bean
    public FilterRegistrationBean<UserIdCookieFilter> userIdCookieFilter() {
        FilterRegistrationBean<UserIdCookieFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UserIdCookieFilter());

        registrationBean.addUrlPatterns("/*");

        registrationBean.setInitParameters(new HashMap<>() {{
            put("excludedPaths", "/login,/registration");
        }});

        return registrationBean;
    }
}