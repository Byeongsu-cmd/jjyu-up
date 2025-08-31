package org.example.jjyuup.common.consts;

/**
 * 상수로 문자열을 관리하여 오류의 가능성을 줄이는 const
 */
public class Const {

    // static은 보통 대문자로 선언, 띄어쓰기는 "_"을 사용
    public static final String SESSION_KEY = "sessionKey";

    private Const() { // 외부에서 객체생성하지 못하게 방지
        throw new UnsupportedOperationException("이 클래스는 인스턴스화할 수 없어용~");
    }
}