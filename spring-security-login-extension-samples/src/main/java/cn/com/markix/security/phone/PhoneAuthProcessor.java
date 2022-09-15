package cn.com.markix.security.phone;

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
public class PhoneAuthProcessor implements AuthProcessor {

    public static final String PHONE_KEY = "phone";
    public static final String CAPTCHA_KEY = "captcha";

    private UserDao userDao;
    private PhoneCodeService captchaService;

    public PhoneAuthProcessor(UserDao userDao, PhoneCodeService codeService) {
        this.userDao = userDao;
        this.captchaService = codeService;
    }

    @Override
    public boolean support(HttpServletRequest request) {
        String phone = request.getParameter(PHONE_KEY);
        String captcha = request.getParameter(CAPTCHA_KEY);
        return !StringUtils.isEmpty(phone) && !StringUtils.isEmpty(captcha);
    }

    @Override
    public UserDetails loadUserByRequest(HttpServletRequest request) {
        String phone = request.getParameter(PHONE_KEY);
        UserPO user = userDao.findByPhone(phone).orElseThrow(() -> new UsernameNotFoundException("手机号不存在"));
        // TODO 自定义返回的用户认证信息
        return new User(user.getName(), user.getPassword(), Collections.emptyList());
    }

    @Override
    public boolean authenticate(UserDetails userDetails, HttpServletRequest request) {
        String phone = request.getParameter(PHONE_KEY);
        String captcha = request.getParameter(CAPTCHA_KEY);
        return captchaService.verifyCode(phone, captcha);
    }

}
