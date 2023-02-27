package hyo.shop.Controller;

import hyo.shop.Service.FileInfoService;
import hyo.shop.Service.ShopService;
import hyo.shop.common.FileUtils;
import hyo.shop.common.SessionConstants;
import hyo.shop.domain.FileInfo;
import hyo.shop.domain.Goods;
import hyo.shop.domain.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/admin/**")
@RequiredArgsConstructor
public class AdminController {

    private final ShopService shopService;
    private final FileInfoService fileInfoService;
    private final FileUtils fileUtils;

//    @ExceptionHandler(value = Exception.class)
//    public String controllerExceptionHandler(Exception e) {
//        System.out.println(new RuntimeException(e.getMessage() + " : 에러 발생"));
//        return "/error";
//    }

    // 관리자 페이지 내부 기능 권한 체크
    public String adminSessionRedirect(Login loginMember, String redirect, HttpServletRequest request) {
        if(!loginMember.getAuth_code().equals("admin")) {
            HttpSession session = request.getSession();
            session.invalidate();

            request.setAttribute("msg", "관리자 권한이 없습니다.");
            request.setAttribute("url", "/admin");
            return "/alert";
        } else {
            return redirect;
        }
    }

    /* 갤러리 게시판 사진 첨부 필수 체크 */
    @PostMapping("/imageCheck")
    @ResponseBody
    public String imageCheck(@RequestPart(value="files", required = false) MultipartFile[] files,
                             @RequestPart(value="goods") Goods goods) throws IOException
    {
        String result = "실패";

        // 첨부파일에 변화가 없을 경우
        if(goods.getChangeYn().equals("N")) {
            result = "성공";
        }

        // 해당 게시글 사진파일 첨부 여부 체크
        List<FileInfo> fileList = fileUtils.checkFileExtention(files);
        // 첨부한 파일이 존재하며
        if(!CollectionUtils.isEmpty(fileList)) {
            for (FileInfo file : fileList) {
                // 파일이 이미지일 경우 성공
                if(file.getFileExtension().equals("jpg") || file.getFileExtension().equals("png") || file.getFileExtension().equals("jpeg")) {
                    result = "성공";
                }
            }
        }
        return result;
    }

//    @GetMapping("/modifyForm")
//    public String modifyForm(
//            Model model,
//            @RequestParam(value = "num") Long typeNo,
//            @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember,
//            HttpServletRequest request)
//    {
//        String url = adminSessionRedirect(loginMember, request.getRequestURI(), request);
//
//        BoardType boardType = boardTypeService.getBoardType(typeNo);
//
//        model.addAttribute("boardTypeForm", boardType);
//        return url;
//    }
//
//    @PostMapping("/boardType/modify")
//    @ResponseBody
//    public int modifyBoardType(@ModelAttribute BoardType boardType) {
//        Board board = new Board();
//        board.setType_no(boardType.getType_no());
//        board.setDelete_yn(boardType.getDelete_yn());
//
//        // 게시판 활성화 및 비활성화 시, 해당 게시글도 같이 변경
//        boardService.modifyBoardYn(board);
//        return boardTypeService.modifyBoardType(boardType);
//    }
//
    @GetMapping("/insertForm")
    public String insertForm(
            Model model,
            @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember,
            HttpServletRequest request)
    {
        String url = adminSessionRedirect(loginMember, request.getRequestURI(), request);

        model.addAttribute("goodsForm", new Goods());

        return url;
    }

    @PostMapping("/goods/insert")
    @ResponseBody
    public String insert(
            @RequestPart(value="files", required = false) MultipartFile[] files,
            @RequestPart(value="goods") Goods goods) throws IOException
    {
        String result = "실패";

        int success = shopService.insert(goods);

        if(success >= 1) {
            result = "성공";

            // insert한 게시글의 goods_no 받아옴
            Long goodsNo = goods.getGoodsNo();
            System.out.println("goodsNo : " +  goodsNo);

            // 해당 게시글 첨부 파일 업로드
            List<FileInfo> fileList = fileUtils.uploadFiles(files, goodsNo);
            if(!CollectionUtils.isEmpty(fileList)) {
                success = fileInfoService.insertFiles(fileList);
                if(success < 1) {
                    result = "실패";
                }
            }
        }

        return result;
    }
//
//    @PostMapping("/boardType/insert")
//    @ResponseBody
//    public int insertBoardType(@ModelAttribute BoardType boardType) {
//        return boardTypeService.insertBoardType(boardType);
//    }
//
//    @GetMapping("/getUserList")
//    @ResponseBody
//    public ModelAndView getUserList() {
//        List<Login> userList = null;
//        ModelAndView mv = new ModelAndView();
//
//        try {
//            userList = loginService.getUserList();
//
//            mv.setViewName("/admin/setUserList");
//            mv.addObject("userList", userList);
//        } catch (Exception e) {
//            mv.setViewName("/error");
//            return mv;
//        }
//
//        return mv;
//    }
//
//    @GetMapping("/userDetailForm")
//    public String userDetailForm(
//            Model model,
//            @RequestParam(value = "user_id") String userId,
//            @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember,
//            HttpServletRequest request)
//    {
//        String url = adminSessionRedirect(loginMember, request.getRequestURI(), request);
//
//        Login login = new Login();
//        login.setUser_id(userId);
//
//        UserAuth userAuth = new UserAuth();
//        userAuth.setUser_id(userId);
//
//        model.addAttribute("userInfo", loginService.getUser(login));
//        model.addAttribute("authList", userAuthService.getUserAuthList(userAuth));
//
//        return url;
//    }
//
//    @PostMapping("/loginAuth/update")
//    @ResponseBody
//    public int loginAuthUpdate(@ModelAttribute Login login) {
//        return loginService.updateUser(login);
//    }
//
//    @PostMapping("/userAuth/delete")
//    @ResponseBody
//    public int deleteUserAuth(@ModelAttribute UserAuth userAuth) {
//        return userAuthService.deleteUserAuth(userAuth);
//    }
//
//    @GetMapping("/popup/authPopup")
//    public String authPopup(
//            Model model,
//            @RequestParam(value = "user_id") String userId,
//            @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember,
//            HttpServletRequest request)
//    {
//        String url = adminSessionRedirect(loginMember, request.getRequestURI(), request);
//
//        List<BoardType>boardTypeList = boardTypeService.getBoardTypeList("admin");
//
//        model.addAttribute("userId", userId);
//        model.addAttribute("boardTypeList", boardTypeList);
//        return url;
//    }
//
//    @PostMapping("/userAuth/insert")
//    @ResponseBody
//    public int insertUserAuth(@ModelAttribute UserAuth userAuth) {
//        return userAuthService.insertUserAuth(userAuth);
//    }
//
//    @PostMapping("/userAuth/check")
//    @ResponseBody
//    public int checkUserAuth(@ModelAttribute UserAuth userAuth) {
//        return userAuthService.checkUserAuth(userAuth);
//    }

}

