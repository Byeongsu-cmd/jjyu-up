package org.example.jjyuup.common.config;

import jakarta.servlet.Filter;
import org.example.jjyuup.common.fillter.LoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration
public class WebConfig {

    @Bean
    public FilterRegistrationBean loginFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginFilter()); // filter 등록
        filterRegistrationBean.addUrlPatterns("/*"); // 전체 url에 filter 적용한다.
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // 내가 만든 필터 중 가장 먼저 실행 된다.

        return filterRegistrationBean;
    }
}
