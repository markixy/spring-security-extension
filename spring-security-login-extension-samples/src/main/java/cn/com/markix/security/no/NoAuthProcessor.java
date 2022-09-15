package cn.com.markix.security.no;

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
 * （工号+密码）认证处理器
 *
 * @author markix
 */
public class NoAuthProcessor implements AuthProcessor {

    public static final String NO_KEY = "no";
    public static final String PASSWORD_KEY = "password";

    private UserDao userDao;
    private PasswordEncoder passwordEncoder;

    public NoAuthProcessor(UserDao userDao, PasswordEncoder passwordEncoder) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean support(HttpServletRequest request) {
        String no = request.getParameter(NO_KEY);
        String password = request.getParameter(PASSWORD_KEY);
        return !StringUtils.isEmpty(no) && !StringUtils.isEmpty(password);
    }

    @Override
    public UserDetails loadUserByRequest(HttpServletRequest request) {
        String no = request.getParameter(NO_KEY);
        UserPO user = userDao.findById(no).orElseThrow(() -> new UsernameNotFoundException("工号不存在"));
        // TODO 自定义返回的用户认证信息
        return new User(user.getName(), user.getPassword(), Collections.emptyList());
    }

    @Override
    public boolean authenticate(UserDetails userDetails, HttpServletRequest request) {
        String password = request.getParameter(PASSWORD_KEY);
        // 验证密码
        return passwordEncoder.matches(password, userDetails.getPassword());
    }

}
