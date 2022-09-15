package cn.com.markix.security;

import cn.com.markix.dao.UserDao;
import cn.com.markix.entity.UserPO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.transaction.Transactional;
import java.util.Collections;

/**
 * 用户信息查找服务，用于查找用户
 *
 * @author markix
 */
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    @Transactional(rollbackOn = Exception.class)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserPO user = userDao.findByName(username).orElseThrow(() -> new UsernameNotFoundException("用户不存在"));
        return new User(user.getName(), user.getPassword(), Collections.emptyList());
    }

}
