package cn.com.markix.spring.security.extension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.servlet.http.HttpServletRequest;

/**
 * 临时认证实体
 *
 * @author markix
 */
public class TempAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private HttpServletRequest request;
    private AuthProcessor processor;

    public TempAuthenticationToken(HttpServletRequest request, AuthProcessor processor) {
        super(null, null);
        this.request = request;
        this.processor = processor;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public AuthProcessor getProcessor() {
        return processor;
    }

}
