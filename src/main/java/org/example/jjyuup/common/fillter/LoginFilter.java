package org.example.jjyuup.common.fillter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.example.jjyuup.common.consts.Const;
import org.springframework.http.HttpStatus;
import org.springframework.util.PatternMatchUtils;

import java.io.IOException;

 @Slf4j // SLF4j 인터페이스 기반으로 자동으로 log 객체를 생성해준다.
// private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(LoginFilter.class);
public class LoginFilter implements Filter {
    /**
     *  상수화 변수명은 대문자와 띄어쓰기는 _ 이렇게 작성하는 것이 관례
     *  인증을 하지 않는 URL Path 배열
     */
    private static final String[] WHITE_LIST = {"/signup","/login","/restoreUsers"};

    // 항상 "왜??" 라고 생각하고 접근! 방법적으로 접근하지 말자! 방법만 알아선 응용하지 못한다!
     // 공통으로 처리해야할 사항들을 한 번에 필터링.. 어떻게 구현할지?로 접근 보단 어떤 상황에 사용해야할지? 에 대해 공부하는 것이 더 좋은 접근 방법이다.
     // 인증 여부 체크(토큰, 세션), IP를 허용/차단, 로깅 : 요청을 빠르게 걸러내거나 표준화 해야할 때! 공통적으로 처리해야할 때!
     // 언제 사용하면 안되는지, 컨트롤러의 정보가 필요한경우! (X) 도메인 정보나 로직이 필요한 경우 (X)

     // 요청을 처리할 때 어떤 순서로 어떻게 처리하는지 비교 Filter, Interceptor, AOP, ControllerAdvice
     // Controller 정보를 알아야하는 경우에는 Interceptor 공부해보기!

    @Override // 필러를 implements를 하기 때문에 필수로 구현해야한다.
    public void doFilter(
            ServletRequest request, // 클라이언트가 서버로 보낸 요청정보를 담고 있다. (url, 파라미터, 바디)
            ServletResponse response, // 서버가 클라이언트로 응답을 보낼 때 사용한다.(HTML, JSON)
            FilterChain chain // 여러개의 필터가 있을 때, 필터들을 연결해서 순차적으로 실행할 수 있도록 도와준다.
    ) throws IOException, ServletException{

        /**
         * 아래와 같이 다양한 기능을 사용하기 위해 다운 캐스팅
         * getMethod() : Http 메서드 확인
         * getHeader("Authorization") : 요청 헤더 조회
         * getCookies() : 쿠키 정보 가져오기
         * getSession() : 세션 객체 접근
         * getRequestURI() : 요청 uri 확인
         */
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();

        /**
         * 다양한 기능을 사용하기 위해 다운 캐스팅
         * setStatus(int sc) : 응답 상태 코드 설정
         * setHeader(String name, String value) : 응답 헤더 추가
         * sendRedirect(String location) : 클라이언트를 다른 url로 리다이렉트
         * setContentType(String type) : 응답의 MIME 차입 설정(application/json)
         * getWriter() : 텍스트 기반 응답 출력
         * getOutputStream() : 바이너리 응답 출력(파일, 이미지 등)
         */
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // log는 Logger 객체 info는 정보성 메세지 출력할 때 사용
        log.info("로그인 필터 실행");

        /**
         * whiteList에 포함되어 있는 url인지 검사 후
         * whiteList에 포함되어 있다면 true 반환
         * 포함되지 않았다면 false 그럼 예외 처리
         */
        if(!isWhiteList(uri)){

            /**
             * false 일 때 상황 세션이 존재한다면 세션을 가져온다.
             * 없다면 session = null
             *
             * HttpSession session = httpRequest.getSession();
             * 그래서 로그인 처리할 때 세션을 생성하니 이것을 사용한다.
             * 만약 true로 설정하면 세션이 존재한다면 세션을 가져온다.
             * 세션이 없다면 null이 아닌 새로 생성
             */
            HttpSession session = httpRequest.getSession(false);

            // 로그인하지 않은 사용자라면..
            if(session == null || session.getAttribute(Const.SESSION_KEY) == null){
                httpResponse.setStatus(HttpStatus.UNAUTHORIZED.value()); // 401 반환
                httpResponse.setContentType("application/json;charset=utf-8"); // 아! JSON이구나~ UTF-8로 일어야지~
                httpResponse.getWriter().write("로그인 해주세요!");
                return;
            }
        }

        /**
         * whiteList에 등록된 url 요청이라면 chain.doFilter 동작
         * 아니라면 예외처리
         * 필터가 더 없다면 Servlet -> Controller 호출
         */
        chain.doFilter(request, response);
    }

    // whiteList에 포함된 url인지 체크하는 메서드
    private boolean isWhiteList(String uri) {
        /**
         * url이 whistList에 포함되어 있는 지 확인 후
         * 포함이라면 true
         * 아니라면 false 반환
         */
        return PatternMatchUtils.simpleMatch(WHITE_LIST,uri);
    }
}
