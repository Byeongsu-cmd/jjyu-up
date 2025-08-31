package org.example.jjyuup.common.config;

import jakarta.servlet.Filter;
import org.example.jjyuup.common.fillter.LoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean loginFilter(){
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginFilter()); // filter 등록
        filterRegistrationBean.addUrlPatterns("/*"); // 전체 url에 filter 적용한다.

        return filterRegistrationBean;
    }
}
