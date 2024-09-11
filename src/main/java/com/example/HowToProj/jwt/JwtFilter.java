package com.example.HowToProj.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;


public class JwtFilter extends GenericFilterBean {

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);
    public static final String AUTHORIZATION_HEADER = "Authorization";
    private TokenProvider tokenProvider;
    public JwtFilter(TokenProvider tokenProvider) {
        this.tokenProvider = tokenProvider;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String requestURI = httpServletRequest.getRequestURI();

        // 공개 엔드포인트는 JWT 필터를 적용하지 않도록 설정
        if (requestURI.startsWith("/api/signup")
                || requestURI.startsWith("/api/login")
                || requestURI.startsWith("/api/authenticate")
                || requestURI.startsWith("/swagger-ui/index.html")) {
            filterChain.doFilter(servletRequest, servletResponse);
            logger.debug("requestURI : {}", requestURI);
            logger.debug("servletRequest : {}", servletRequest);
            logger.debug("servletResponse : {}", servletResponse);
            return;
        }

        String jwt = resolveToken(httpServletRequest);
        logger.debug("Resolved JWT: {}", jwt);

        if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            Authentication authentication = tokenProvider.getAuthentication(jwt);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            logger.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
        } else {
            logger.debug("유효한 JWT 토큰이 없습니다, uri: {}", requestURI);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        logger.debug("Authorization 헤더: {}", bearerToken);
        if (StringUtils.hasText(bearerToken)) {
            // 'Bearer '를 체크하지 않고 바로 반환
            return bearerToken.startsWith("Bearer ") ? bearerToken.substring(7) : bearerToken;
        }
        return null;
    }
}
