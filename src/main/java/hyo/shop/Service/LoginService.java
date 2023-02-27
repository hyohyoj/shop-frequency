package hyo.shop.Service;

import hyo.shop.domain.Login;
import hyo.shop.mapper.LoginMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginService {

    private final LoginMapper loginMapper;

    public Login getUser(Login loginForm) { return loginMapper.getUser(loginForm); }

    public int insertUser(Login signUpForm) { return loginMapper.insertUser(signUpForm); }

    public List<Login> getUserList() {
        return loginMapper.getUserList();
    }

    public int updateUser(Login login) {
        return loginMapper.updateUser(login);
    }

}
