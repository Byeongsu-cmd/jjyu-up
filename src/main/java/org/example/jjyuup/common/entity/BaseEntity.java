package org.example.jjyuup.common.entity;    // 공통(common)으로 사용하는 엔티티
/** Entity
 * 정의: DB 테이블과 1:1로 매핑되는 클래스
 *      DB 행(row) 하나 = 엔티티 객체 하나
 *      즉 엔티티는 DB 데이터를 객체로 표현한 것
 * 역할: 1. 데이터 저장과 조회: 엔티티 객체를 통해 데이터를 JPA로 저장 또는 조회 가능
 *      2. JPA 영속성 관리
 *      : 영속성 컨텍스트에 의한 객체의 상태 관리
 *        더티 체킹(JPA가 객체의 변경을 감지해서 자동으로 DB를 업데이트)하여 DB에 자동 반영
 *        즉 JPA가 엔티티를 이용해 DB와 객체 간 가교 역할
 *      3. 상속을 통한 공통 속성 재사용
 * 영속 컨텍스트: JPA가 영속 상태의 엔티티 객체를 감시하는 저장소
 *            JPA는 객체와 스냅샷(조회 시 객체 상태 복제) 관리
 * 더티 체킹: 더티 = 변경된 데이터
 *          트랜잭션이 끝날 때(commit 또는 flush 시점) 영속 컨텍스트 안의 스냅샷과 현재 엔티티의 필드값 비교
 *          객체의 값 변경 시 -> 자동으로 UPDATE SQL을 만들어 DB에 반영
 *          영속 상태 엔티티 + 트랜잭션 내에서만 동작
 */

import jakarta.persistence.*;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
/** @CreatedDate,@LastModifiedDate
 * JAP의 감사(Auditing) 기능에서 날짜를 자동으로 기록
 */
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;


@Getter
/** @Getter
 * 클래스 내 모든 필드에 getter 메서드 자동 생성
 * 해당 어노테이션 덕분에
 * public LocalDateTime getCreatedAt() {
 *     return createdAt;
 * }
 * cf) Setter 안 쓰는 이유
 * 엔티티의 필드를 어디서든 수정 가능
 * -> 데이터 무결성 위반 & JPA 불변성 위반
 * -> 값은 엔티티를 생성하는 시점 또는 비즈니스 로직 메서드를 통해서만 변경 가능
 *    (생성자에서만 초기화)
 */
@MappedSuperclass
/** @MappedSuperclass
 * 공통 필드만 정의하는 엔티티의 부모 클래스 지정
 * 해당 클래스는 DB에서 독립적인 엔티티가 아니므로
 * -> DB에 BaseEntity라는 테이블 생성은 아님
 * => 하지만 이 클래스의 필드는 이를 상속받는 엔티티의 컬럼으로 매핑
 *    즉 상속받는 엔티티의 테이블에 컬럼으로 합쳐짐
 * 해당 어노테이션이 아닌 @Entity 사용 시 자체 테이블이 생기나 상속 관계 및 JOIN이 필요해짐
 */
@EntityListeners(AuditingEntityListener.class)
/**
 * 엔티티의 생성 및 수정 이벤트를 감지 및 자동 호출되는 리스너(AuditingEntityListener.class) 지정
 * JPA Auditing 기능을 활성화시켜 @CreatedDate, @LastModifiedDate가 작동하도록 함
 */
public class BaseEntity {

    @CreatedDate
    /** @CreatedDate
     * 엔티티가 처음 저장 시 자동으로 현재 날짜 및 시간 저장
     * persist() 또는 save() 호출 시 값이 할당
     */
    @Column(nullable = false, updatable = false)
    /** @Column
     * 생략 시 기본값으로 컬럼 생성 즉 제약 설정
     * nullable 기본값: true
     * updatable 기본값: true
     */
    /** updatable
     *  updatable = true 기본값 -> 엔티티 업데이트 시 자동으로 갱신
     *  false 시 한번 저장되면 수정 불가 즉 고정
     */
    /** @Temporal(TemporalType.TIMESTAMP)
     * LocalDateTime은 자바 8부터 도입된 java.time 패키지 내 새로운 날짜/시간 API 클래스
     * @Temporal 없이 DB의 TIMESTAMP 타입에 직접 매핑 가능
     * 따라서 위 어노테이션은 java.util.Date 타입과 java.util.Calendar 타입에서 사용
     */
    private LocalDateTime createdAt;    // DB에서 TIMESTAMP 타입으로 매핑

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime modifiedAt;

    /** JPA 기본 생성자
     * 컴파일러가 자동으로 기본 생성자 생성하므로 코드상 생략
     * (파리미터가 있는)커스텀 생성자 추가 시, 기본 생성자 따로 정의해야 함
     * protected BaseEntity() {    // 외부에서 마음대로 생성 못 하도록 제한(JPA는 protected나 public 생성자만 필요)
     *    }    // JPA가 리플렉션을 통해 객체 생성 시 내부 필드를 채워줌 -> 그래서 new ...() 안 해도 됨
     */

    // Soft Delete
    @Column(name = "deleted",nullable = false)
    private boolean deleted = false;

    public void softDelete() {
        this.deleted = true;
    }

    public void restore() { // 복원용
        this.deleted = false;
    }
}