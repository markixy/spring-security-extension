package cn.com.markix.dao;

import cn.com.markix.entity.UserPO;
import org.springframework.data.jpa.repository.support.JpaRepositoryImplementation;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author markix
 */
@Repository
public interface UserDao extends JpaRepositoryImplementation<UserPO, String> {

    Optional<UserPO> findByName(String name);

    Optional<UserPO> findByPhone(String phone);

    Optional<UserPO> findByEmail(String email);


}
