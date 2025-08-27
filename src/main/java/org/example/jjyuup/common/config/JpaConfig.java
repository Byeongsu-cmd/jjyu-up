package org.example.jjyuup.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration // 수동으로 빈 등록
@EnableJpaAuditing // entity의 생성 및 수정 시간을 자동으로 관리하는 기능
public class JpaConfig {
}
