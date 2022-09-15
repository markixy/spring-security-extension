package cn.com.markix.spring.security.extension;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 认证提供者
 *
 * @author markix
 * @see org.springframework.security.authentication.dao.DaoAuthenticationProvider
 */
public class MultipleAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        TempAuthenticationToken tempAuth = (TempAuthenticationToken) authentication;

        UserDetails loadedUser = tempAuth.getProcessor().loadUserByRequest(tempAuth.getRequest());
        if (loadedUser == null) {
            throw new InternalAuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
        }
        return loadedUser;
    }

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        TempAuthenticationToken tempAuth = (TempAuthenticationToken) authentication;

        if (!tempAuth.getProcessor().authenticate(userDetails, tempAuth.getRequest())) {
            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
    }

    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        // TIPS 不管何种认证方式，最终返回的是 UsernamePasswordAuthenticationToken
        return super.createSuccessAuthentication(principal, authentication, user);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TempAuthenticationToken.class.isAssignableFrom(authentication);
    }

}
