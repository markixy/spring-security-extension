package cn.com.markix.security.username;

import cn.com.markix.dao.UserDao;
import cn.com.markix.entity.UserPO;
import cn.com.markix.spring.security.extension.AuthProcessor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * （用户名+密码）认证处理器
 *
 * @author markix
 */
public class NameAuthProcessor implements AuthProcessor {

    public static final String IDENTITY_PARAMETER = "username";
    public static final String CREDENTIALS_PARAMETER = "password";

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;

    public NameAuthProcessor(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean support(HttpServletRequest request) {
        String identity = request.getParameter(IDENTITY_PARAMETER);
        String credentials = request.getParameter(CREDENTIALS_PARAMETER);
        return !StringUtils.isEmpty(identity) && !StringUtils.isEmpty(credentials);
    }

    @Override
    public UserDetails loadUserByRequest(HttpServletRequest request) {
        String identity = request.getParameter(IDENTITY_PARAMETER);
        UserPO user = userDao.findByName(identity).orElseThrow(() -> new UsernameNotFoundException("用户名不存在"));
        // TODO 自定义返回的用户认证信息
        return new User(user.getName(), user.getPassword(), Collections.emptyList());
    }

    @Override
    public boolean authenticate(UserDetails userDetails, HttpServletRequest request) {
        String credentials = request.getParameter(CREDENTIALS_PARAMETER);
        // 验证密码
        return passwordEncoder.matches(credentials, userDetails.getPassword());
    }

}
