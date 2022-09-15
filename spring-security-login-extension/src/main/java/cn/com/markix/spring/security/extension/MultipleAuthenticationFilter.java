package cn.com.markix.spring.security.extension;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 认证过滤器
 * <p>通过维护一个认证处理器列表，支持多种认证方式</p>
 *
 * @author markix
 */
public class MultipleAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * 认证处理器列表
     */
    private List<AuthProcessor> processors = new ArrayList<>();

    public MultipleAuthenticationFilter() {
        super(new AntPathRequestMatcher("/multi-login", "POST"));
    }


    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        AuthProcessor usedProcessor = null;
        for (AuthProcessor processor : this.processors) {
            if (processor.support(request)) {
                usedProcessor = processor;
                // TIPS 短路判断，一个满足则跳出
                break;
            }
        }
        Assert.notNull(usedProcessor, "暂不支持此认证请求！");

        TempAuthenticationToken authRequest = new TempAuthenticationToken(request, usedProcessor);
        authRequest.setDetails(authenticationDetailsSource.buildDetails(request));

        return this.getAuthenticationManager().authenticate(authRequest);
    }


    @Override
    public void afterPropertiesSet() {
        super.afterPropertiesSet();
        if (processors.isEmpty()) {
            throw new IllegalArgumentException("AuthProcessor is required");
        }
    }

    public void addAuthProcessor(AuthProcessor processor) {
        this.processors.add(processor);
    }

}
