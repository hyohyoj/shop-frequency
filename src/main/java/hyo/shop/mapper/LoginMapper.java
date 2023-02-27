package hyo.shop.mapper;

import hyo.shop.domain.Login;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LoginMapper {
    Login getUser(Login loginForm);
    int insertUser(Login signUpForm);
    List<Login> getUserList();
    int updateUser(Login login);
}
