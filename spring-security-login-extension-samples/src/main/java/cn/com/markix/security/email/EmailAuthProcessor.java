package cn.com.markix.security.email;

import cn.com.markix.dao.UserDao;
import cn.com.markix.entity.UserPO;
import cn.com.markix.spring.security.extension.AuthProcessor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;

/**
 * （邮箱+验证码）认证处理器
 *
 * @author markix
 */
public class EmailAuthProcessor implements AuthProcessor {

    public static final String EMAIL_KEY = "email";
    public static final String CAPTCHA_KEY = "captcha";

    private UserDao userDao;
    private EmailCodeService captchaService;

    public EmailAuthProcessor(UserDao userDao, EmailCodeService codeService) {
        this.userDao = userDao;
        this.captchaService = codeService;
    }

    @Override
    public boolean support(HttpServletRequest request) {
        String email = request.getParameter(EMAIL_KEY);
        String captcha = request.getParameter(CAPTCHA_KEY);
        return !StringUtils.isEmpty(email) && !StringUtils.isEmpty(captcha);
    }

    @Override
    public UserDetails loadUserByRequest(HttpServletRequest request) {
        String email = request.getParameter(EMAIL_KEY);
        UserPO user = userDao.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("邮箱不存在"));
        // TODO 自定义返回的用户认证信息
        return new User(user.getName(), user.getPassword(), Collections.emptyList());
    }

    @Override
    public boolean authenticate(UserDetails userDetails, HttpServletRequest request) {
        String email = request.getParameter(EMAIL_KEY);
        String captcha = request.getParameter(CAPTCHA_KEY);
        return captchaService.verifyCode(email, captcha);
    }

}
