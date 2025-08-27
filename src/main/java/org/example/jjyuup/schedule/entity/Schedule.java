package org.example.jjyuup.schedule.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.jjyuup.common.entity.BaseEntity;
import org.example.jjyuup.user.entity.User;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 외부에서 직접적으로 엔티티를 생성하는 것을 제한하는 용도
@Table(name = "schedules") // 복수명 사용하는 이유는 데이터베이스의 특성과 Java 객체의 특성을 구분한다.
public class Schedule extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY) // 일단 데이터 안가져올께 즉, 필요할 때만 가져올께
    @JoinColumn(name = "user_id", nullable = false) // null이 가능하지 않다.
    private User user;

    public Schedule(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user = user;
    }
}
