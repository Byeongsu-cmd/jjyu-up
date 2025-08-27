package org.example.jjyuup.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Getter
@MappedSuperclass // jpa entity 클래스들이 이 클래스를 상속할 경우 createDate, modifiedDate를 컬럼으로 인식
@EntityListeners(AuditingEntityListener.class) // 해당 클래스에 Auditing 기능을 포함한다.
public class BaseEntity {

    @CreatedDate
    @Column(nullable = false, updatable = false) // null이 가능하지않다. // 업데이트를 할 수 없다.
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;
}
