package org.example.jjyuup.common.consts;

/**
 * 상수로 문자열을 관리하여 오류의 가능성을 줄이는 const
 */
public class Const {

    // static은 보통 대문자로 선언, 띄어쓰기는 "_"을 사용
    public static final String SESSION_KEY = "sessionKey";

    /**
     * 외부에서 객체생성하지 못하게 방지
     * 값이 노출되거나 탈취되면, 공격자가 그 세션 키를 이용해서 인증된 사용자처럼 행동할 수 있다는 게 핵심 위험
     */
    private Const() {
        throw new UnsupportedOperationException("이 클래스는 인스턴스화할 수 없어용~");
    }
}