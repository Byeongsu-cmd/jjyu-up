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

@Slf4j
public class LoginFilter implements Filter {

    /**
     *  상수화 변수명은 대문자와 띄어쓰기는 _ 이렇게 작성하는 것이 관례
     *  인증을 하지 않는 URL Path 배열
     */
    private static final String[] WHITE_LIST = {"/signup","/login","/restoreUsers"};

    @Override // 필러를 implements를 하기 때문에 필수로 구현해야한다.
    public void doFilter(
            ServletRequest request,
            ServletResponse response,
            FilterChain chain
    ) throws IOException, ServletException{

        /**
         * 다양한 기능을 사용하기 위해 다운 캐스팅
         */
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        String uri = httpRequest.getRequestURI();

        /**
         * 다양한 기능을 사용하기 위해 다운 캐스팅
         */
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        log.info("로그인 필터 실행");

        /**
         * whiteList에 포함되어 있는 url인지 검사 후
         * whiteList에 포함되어 있다면 true 반환
         * 포함되지 않았다면 false 그럼 예외 처리
         */
        if(!isWhiteList(uri)){

            /**
             * 세션이 존재한다면 세션을 가져온다.
             * 없다면 session = null
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
