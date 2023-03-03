package hyo.shop.Controller;

import hyo.shop.Service.CartService;
import hyo.shop.Service.LoginService;
import hyo.shop.common.SessionConstants;
import hyo.shop.domain.Cart;
import hyo.shop.domain.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;
    private final CartService cartService;
    private final PasswordEncoder pwEncoder;

    //컨트롤러 내에서 발생하는 예외를 모두 처리해준다
    @ExceptionHandler(value = Exception.class)
    public String controllerExceptionHandler(Exception e) {
        System.out.println(new RuntimeException(e.getMessage() + " : 에러 발생"));
        return "/error";
    }

    @GetMapping(value = {"/login", "/admin/login"})
    public String loginForm(Model model, HttpServletRequest request) {
        String path = request.getRequestURI();

        model.addAttribute("loginForm", new Login());
        model.addAttribute("path", path);

        return "/login/loginForm";
    }

    @PostMapping("/loginCheck")
    @ResponseBody
    public String loginCheck(@RequestBody Login loginForm) {
        String rawPw = "";
        String encodePw = "";

        Login loginMember = loginService.getUser(loginForm);

        if(loginMember == null) {
            return "아이디가 일치하지 않습니다.";
        }

        rawPw = loginForm.getUser_pw();
        encodePw = loginMember.getUser_pw();

        if(!pwEncoder.matches(rawPw, encodePw)) {
            return "비밀번호가 일치하지 않습니다.";
        }

        return "성공";
    }

    @PostMapping("/login")
    public String login(@ModelAttribute Login loginForm,
                        @RequestParam(defaultValue = "/") String redirectURL,
                        HttpServletRequest request,
                        Model model)
    {
        Cookie cookie = WebUtils.getCookie(request, "cartCookie");
        Cart cart = new Cart();

        Login loginMember = loginService.getUser(loginForm);
        String path = request.getParameter("path");
        System.out.println("request 테스트 : " + path);

        // 관리자 페이지 주소로 접근 시
        if(path.equals("/admin/login")) {
            if(loginMember.getAuth_code().equals("admin")) {
                // 로그인 성공
                HttpSession session = request.getSession(); // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성하여 반환
                session.setAttribute(SessionConstants.LOGIN_MEMBER, loginMember);   // 세션에 로그인 회원 정보 보관

                return "redirect:/admin";
            } else {
                model.addAttribute("loginForm", loginMember);
                model.addAttribute("msg", "관리자 권한이 없습니다.");
                model.addAttribute("path", path);
                return "/login/loginForm";
            }
        }

        // 로그인 성공
        HttpSession session = request.getSession(); // 세션이 있으면 있는 세션 반환, 없으면 신규 세션을 생성하여 반환
        session.setAttribute(SessionConstants.LOGIN_MEMBER, loginMember);   // 세션에 로그인 회원 정보 보관

        // 비회원 장바구니 업데이트
        if(cookie != null) {
            cart.setCart_ckid(cookie.getValue());
            cart.setUser_id(loginMember.getUser_id());

            cartService.updateCartList(cart);
        }

        return "redirect:" + redirectURL;
    }

    @PostMapping("/logout")
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        // 해당 핸들러를 사용하면 logoutSuccessUrl(String)이 무시됨.
        new SecurityContextLogoutHandler().logout(request, response, SecurityContextHolder.getContext().getAuthentication());

        return "redirect:/";
    }

    @PostMapping("/admin/logout")
    public String adminLogout(HttpServletRequest request, HttpServletResponse response) {
        HttpSession session = request.getSession(false);

        if(session != null) {
            session.invalidate();
        }

        return "redirect:/admin";
    }

    @GetMapping("/signup")
    public String signupForm(Model model) {
        model.addAttribute("signUpForm", new Login());
        return "/login/signUpForm";
    }

    @PostMapping("/signup")
    public String signup(@ModelAttribute Login signUpForm, Model model) {
        String rawPw = "";
        String encodePw = "";
        ModelAndView mv = new ModelAndView();

        Login signUpMember = loginService.getUser(signUpForm);

        if(signUpMember != null) {  // 중복 아이디 존재 시, 다시 폼 화면으로 던짐
            model.addAttribute("signUpForm", new Login());
            return "/login/signUpForm";
        }

        rawPw = signUpForm.getUser_pw();
        encodePw = pwEncoder.encode(rawPw);
        signUpForm.setUser_pw(encodePw);

        int success = loginService.insertUser(signUpForm);

        if(success <= 0) {  // insert 실패
            model.addAttribute("signUpForm", new Login());
            return "/login/signUpForm";
        }

        return "/login/signUpSuccess";
    }

    @PostMapping("/signUpCheck")
    @ResponseBody
    public String signUpCheck(@RequestBody Login signUpForm) {

        Login loginMember = loginService.getUser(signUpForm);

        if(loginMember != null) {
            return "중복 아이디 입니다.";
        }

        return "성공";
    }
}
