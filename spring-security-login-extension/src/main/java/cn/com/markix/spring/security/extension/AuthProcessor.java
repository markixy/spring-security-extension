package cn.com.markix.spring.security.extension;

import org.springframework.security.core.userdetails.UserDetails;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义认证处理器接口
 * <p>自定义登录认证需要实现此接口，比如 手机号+验证码登录，邮箱+验证码登录，工号+密码登录，帐号+密码登录...</p>
 *
 * @author markix
 */
public interface AuthProcessor {

    /**
     * 判断此认证器是否可以处理该认证请求
     * <p>可以通过 querystring、form-data、body... 中携带的字段等进行判定</p>
     */
    boolean support(HttpServletRequest request);

    /**
     * 获取用户信息
     */
    UserDetails loadUserByRequest(HttpServletRequest request);

    /**
     * 认证的核心逻辑
     */
    boolean authenticate(UserDetails userDetails, HttpServletRequest request);

}
