package hyo.shop.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hyo.shop.Service.FileInfoService;
import hyo.shop.Service.ShopService;
import hyo.shop.common.FileUtils;
import hyo.shop.common.SessionConstants;
import hyo.shop.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/**")
@RequiredArgsConstructor
public class AdminController {

    private final ShopService shopService;
    private final FileInfoService fileInfoService;
    private final FileUtils fileUtils;

    @ExceptionHandler(value = Exception.class)
    public String controllerExceptionHandler(Exception e) {
        System.out.println(new RuntimeException(e.getMessage() + " : 에러 발생"));
        return "/error";
    }

    /**
     * 관리자 페이지 내부 기능 권한 체크
     * @param loginMember   - 로그인 정보
     * @param redirect      - 리다이렉트 주소
     * @param request       - request
     * @return 리다이렉트 주소
     */
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

    /* 사진 첨부 필수 체크 */
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

    @GetMapping("/modifyForm")
    public String modifyForm(
            Model model,
            @RequestParam(value = "goods_no") Long goodsNo,
            @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember,
            HttpServletRequest request)
    {
        String url = adminSessionRedirect(loginMember, request.getRequestURI(), request);

        Goods goods = shopService.getGoods(goodsNo);
        List<FileInfo> fileList = fileInfoService.selectFileList(goodsNo);

        model.addAttribute("goodsForm", goods);
        model.addAttribute("fileList", fileList);       // 게시글 첨부 파일 목록

        return url;
    }

    @PostMapping("/goods/modify")
    @ResponseBody
    public int modify(@RequestPart(value="files", required = false) MultipartFile[] files,
                      @RequestPart(value="goods") Goods goods)
    {
        List<Long> fileNumList = goods.getFileNumList();

        int result = 0;
        int success = 0;

        success += shopService.update(goods);

        if(success >= 1) {
            result++;

            // 파일이 추가, 삭제, 변경된 경우
            if("Y".equals(goods.getChangeYn())) {
                // 전체 삭제 처리
                fileInfoService.deleteFile(goods.getGoods_no());

                // 변경되지 않은 기존 파일의 삭제여부를 N으로 변경
                if(!CollectionUtils.isEmpty(fileNumList)) {
                    success = fileInfoService.undeleteFile(fileNumList);
                    System.out.println(success);
                    if(success >= 1) {
                        result++;
                    }
                }

                // 해당 게시글 첨부 파일 업로드
                List<FileInfo> fileList = fileUtils.uploadFiles(files, goods.getGoods_no());
                if(!CollectionUtils.isEmpty(fileList)) {
                    success = fileInfoService.insertFiles(fileList);
                    if(success >= 1) {
                        result++;
                    }
                }
            }
        }
        return result;
    }

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

            // insert 한 게시글의 goods_no 받아옴
            Long goodsNo = goods.getGoods_no();
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

    // ModelAndView 형태로 데이터가 세팅 된 뷰를 반환
    @PostMapping("/getGoodsList")
    public ModelAndView getGoodsList(
            @RequestBody Map<String, Object> map,      // JSON 형식으로 받아옴
            @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember)
    {

        ModelAndView mv = new ModelAndView("jsonView"); // json 형태로 데이터 전송

        ObjectMapper mapper = new ObjectMapper();

        SearchInfo searchInfo = mapper.convertValue(map.get("searchInfo"), SearchInfo.class);
        Goods goods = mapper.convertValue(map.get("goodsInfo"), Goods.class);

        Map<String, Object> searchMap = new HashMap<>();

        try{
            int goodsCount = shopService.goodsCount();
            Pagination pagination = new Pagination(goodsCount, searchInfo);

            searchInfo.setPagination(pagination);

            searchMap.put("goodsVo", goods);
            searchMap.put("searchVo", searchInfo);

            List<Goods> goodsList = shopService.goodsList(searchMap);
            goodsList = fileUtils.setImageUploadPath(goodsList);    // 출력 이미지 경로 지정

            mv.setViewName("/admin/setGoodsList");
            mv.addObject("goodsList", goodsList);                   // 상품 목록
            mv.addObject("sessionId", loginMember.getUser_id());    // 세션 아이디
            mv.addObject("searchInfo", searchInfo);                 // 페이징 정보
        } catch (Exception e) {
            System.out.println(e + " : 에러 발생");
            mv.setViewName("/error");
            return mv;
        }
        return mv;
    }

}

