package cn.com.markix.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author markix
 */
@RestController
public class CurrentController {

    /**
     * 获取当前认证信息
     */
    @GetMapping("/current")
    public Authentication authentication() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication;
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/current/principal")
    public Object principal() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        // principal 就是 UserDetails 类型
        return principal;
    }

}
