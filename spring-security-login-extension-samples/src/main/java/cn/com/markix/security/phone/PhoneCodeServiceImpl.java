package cn.com.markix.security.phone;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 邮件验证码服务
 *
 * @author markix
 */
public class PhoneCodeServiceImpl implements PhoneCodeService {

    private static Map<String, String> codeMap = new ConcurrentHashMap<>();

    private static Random random = new Random();

    @Override
    public void generateCode(String email) {
        // 生成 4 位数的验证码
        String code = String.format("%04d", random.nextInt(9999));
        codeMap.put(email, code);

        // TODO 对接短信平台，将验证码发送给用户
        // 测试用，直接打印到控制台，便于调试
        System.out.println(String.format("phone={%s},code={%s}", email, code));
    }

    @Override
    public boolean verifyCode(String email, String code) {
        return code.equals(codeMap.get(email));
    }


}
