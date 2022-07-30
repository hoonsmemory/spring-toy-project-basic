package io.hoon.springtoyprojectbasic.security.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private String errorPage;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        log.error("Access Denied Error occurred : {}", accessDeniedException.getMessage());

        String deniedUrl = errorPage + "?exception=" + accessDeniedException.getMessage();
        redirectStrategy.sendRedirect(request, response, deniedUrl);
    }

    public void setErrorPage(String errorPage) {
        if ((errorPage != null) && !errorPage.startsWith("/")) {
            throw new IllegalArgumentException("errorPage must begin with '/'");
        }

        this.errorPage = errorPage;
    }
}
