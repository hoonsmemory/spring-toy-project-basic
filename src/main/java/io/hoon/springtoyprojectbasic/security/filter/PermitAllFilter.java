package io.hoon.springtoyprojectbasic.security.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.intercept.InterceptorStatusToken;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 권한이 필요없는 페이지에 대해서는 모든 사용자에게 보여질 수 있도록 함
 */
@Slf4j
public class PermitAllFilter extends FilterSecurityInterceptor {
    private static final String FILTER_APPLIED = "__spring_security_filterSecurityInterceptor_filterApplied";
    private List<RequestMatcher> permitAllRequestMatcher = new ArrayList<>();
    public PermitAllFilter(String... permitAllPattern) {
        createPermitAllPattern(permitAllPattern);
    }

    @Override
    protected InterceptorStatusToken beforeInvocation(Object object) {
        boolean permitAll = false;

        //URL 정보 가져오기
        HttpServletRequest request = ((FilterInvocation) object).getRequest();
        for (RequestMatcher requestMatcher : permitAllRequestMatcher) {
            //적용한 URL과 사용자가 요청한 URL이 같을 경우 권한심사를 패스할 수 있도록 null을 return 한다.
            if (requestMatcher.matches(request)) {
                permitAll = true;
                break;
            }
        }

        if (permitAll) return null;
        return super.beforeInvocation(object);
    }

    @Override
    public void invoke(FilterInvocation fi) throws IOException, ServletException {

        if ((fi.getRequest() != null) && (fi.getRequest().getAttribute(FILTER_APPLIED) != null)
                && super.isObserveOncePerRequest()) {
            // filter already applied to this request and user wants us to observe
            // once-per-request handling, so don't re-do security checking
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        } else {
            // first time this request being called, so perform security checking
            if (fi.getRequest() != null) {
                fi.getRequest().setAttribute(FILTER_APPLIED, Boolean.TRUE);
            }

            InterceptorStatusToken token = beforeInvocation(fi);

            try {
                fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
            } finally {
                super.finallyInvocation(token);
            }

            super.afterInvocation(token, null);
        }
    }

    private void createPermitAllPattern(String... permitAllPattern) {
        for (String pattern : permitAllPattern) {
            permitAllRequestMatcher.add(new AntPathRequestMatcher(pattern));
        }
    }
}
