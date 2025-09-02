package org.example.jjyuup.common.config;

import jakarta.servlet.Filter;
import org.example.jjyuup.common.fillter.LoginFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

@Configuration // spring의 자바 기반 설정 클래스다.
public class WebConfig {

    @Bean /* 인스턴스화 된 객체, 스프링 컨테이너에 등록된 객체 new 키워드 대신에 사용한다.
            사용하는 이유는 스프링 간 객체가 의존관계를 관리하도록 하는 것이 가장 큰 이유
           */
    public FilterRegistrationBean loginFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LoginFilter()); // filter 등록
        filterRegistrationBean.addUrlPatterns("/*"); // 전체 url에 filter 적용한다.
        filterRegistrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE + 1); // 내가 만든 필터 중 가장 먼저 실행 된다.

        return filterRegistrationBean;
    }
}
