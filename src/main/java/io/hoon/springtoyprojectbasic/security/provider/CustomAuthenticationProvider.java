package io.hoon.springtoyprojectbasic.security.provider;

import io.hoon.springtoyprojectbasic.security.common.FormWebAuthenticationDetails;
import io.hoon.springtoyprojectbasic.security.service.AccountContext;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;

    private final PasswordEncoder passwordEncoder;

    /*
        authentication에는 사용자가 입력한 ID, Password 등 입력한 정보가 담겨있다.
     */
    @Override
    @Transactional
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        //추가적인 검증을 위한 정보를 가져온다.
        AccountContext accountContext = (AccountContext) userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, accountContext.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        String secretKey = ((FormWebAuthenticationDetails) authentication.getDetails()).getSecretKey();
        if (secretKey == null || !secretKey.equals("secret")) {
            throw new IllegalArgumentException("Invalid Secret");
        }


        //최종 인증 성공 후 정보를 전달한다. (권한정보, Account 객체, 패스워드 정보, Authenticated : true)
        return new UsernamePasswordAuthenticationToken(accountContext.getAccount(), null, accountContext.getAuthorities());
    }

    /*
        authentication에서 전달된 클래스 토큰과 UsernamePasswordAuthenticationToken이 일치할 때만
        CustomAuthenticationProvider가 인증을 처리하도록 적용
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
