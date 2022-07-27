package io.hoon.springtoyprojectbasic.security.common;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

import javax.servlet.http.HttpServletRequest;

public class FormWebAuthenticationDetails extends WebAuthenticationDetails {

    private String securetKey;

    public FormWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        securetKey = request.getParameter("secret_key");
    }

    public String getSecuretKey() {
        return securetKey;
    }
}
