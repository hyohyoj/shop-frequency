package hyo.shop.Controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import hyo.shop.Service.CartService;
import hyo.shop.Service.ShopService;
import hyo.shop.common.FileUtils;
import hyo.shop.common.SessionConstants;
import hyo.shop.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/shop/**")
@Slf4j
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;
    private final CartService cartService;
    private final FileUtils fileUtils;

    // ModelAndView 형태로 데이터가 세팅 된 뷰를 반환
    @PostMapping("/getGoodsList")
    @ResponseBody
    public ModelAndView getGoodsList(
            @RequestBody Map<String, Object> map,      // JSON 형식으로 받아옴
            @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember,
            HttpServletResponse response)
    {
        ModelAndView mv = new ModelAndView("jsonView"); // json 형태로 데이터 전송

        ObjectMapper mapper = new ObjectMapper();

        SearchInfo searchInfo = mapper.convertValue(map.get("searchInfo"), SearchInfo.class);
        Goods goods = mapper.convertValue(map.get("goodsInfo"), Goods.class);

//        response.setContentType("text/html;charset=UTF-8");
//        Cookie pageCookie = new Cookie("page", Integer.toString(searchInfo.getPage()));
//        pageCookie.setPath("/");
//        response.addCookie(pageCookie);

        Map<String, Object> searchMap = new HashMap<>();
        DecimalFormat formatter = new DecimalFormat("###,###");

        try{
            int goodsCount = shopService.goodsCount();
            Pagination pagination = new Pagination(goodsCount, searchInfo);

            searchInfo.setPagination(pagination);

            searchMap.put("goodsVo", goods);
            searchMap.put("searchVo", searchInfo);

            List<Goods> goodsList = shopService.goodsList(searchMap);
            goodsList = fileUtils.setImageUploadPath(goodsList);    // 출력 이미지 경로 지정

            for (Goods g : goodsList) {
                int price = g.getGoods_price();
                g.setFormatPrice(formatter.format(price));
            }

            mv.setViewName("/shop/setGoodsList");
            mv.addObject("goodsList", goodsList);                   // 상품 목록
            if(loginMember != null) {
                mv.addObject("sessionId", loginMember.getUser_id());    // 세션 아이디
            }
            mv.addObject("searchInfo", searchInfo);                 // 페이징 정보
        } catch (Exception e) {
            System.out.println(e + " : 에러 발생");
            mv.setViewName("/error");
            return mv;
        }

        return mv;
    }

    @GetMapping("/goodsDetail")
    public ModelAndView goodsDetail(
            @RequestParam(value = "goods_no") Long goodsNo)
    {
        ModelAndView mv = new ModelAndView("jsonView"); // json 형태로 데이터 전송
        DecimalFormat formatter = new DecimalFormat("###,###");

        Goods goods = shopService.getGoods(goodsNo);
        goods = fileUtils.setImagePathArray(goods);     // 출력 이미지 경로 지정

        int price = goods.getGoods_price();
        goods.setFormatPrice(formatter.format(price));

        mv.setViewName("/shop/goodsDetail");
        mv.addObject("goods", goods);

        return mv;
    }

    @PostMapping("/cart")
    @ResponseBody
    public int cart(@RequestBody Cart cart,
                    @SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember,
                    HttpServletRequest request, HttpServletResponse response)
    {
        Cookie cookie = WebUtils.getCookie(request, "cartCookie");

        // 비회원 장바구니 첫 추가 시 쿠키 생성
        if(cookie == null && loginMember == null) {
            String ckid = RandomStringUtils.random(6, true, true);

            Cookie cartCookie = new Cookie("cartCookie", ckid);
            cartCookie.setPath("/");
            cartCookie.setMaxAge(60 * 60 * 24 * 1);     // 쿠키 제한시간 1일
            response.addCookie(cartCookie);

            cart.setCart_ckid(ckid);
            cartService.cartInsert(cart);
        }
        // 비회원 장바구니 쿠키 생성 후 상품 추가
        else if(cookie != null && loginMember == null) {
            String ckValue = cookie.getValue();
            cart.setCart_ckid(ckValue);

            // 장바구니 중복 확인
            if(cartService.cartCheck(cart) != 0) {
                return 2;
            }

            // 상품 추가 시 쿠키 시간 연장
            cookie.setPath("/");
            cookie.setMaxAge(60 * 60 * 24 * 1);
            response.addCookie(cookie);

            //insert
        }
        // 회원 장바구니 상품 추가
        else if(loginMember != null) {
            cart.setUser_id(loginMember.getUser_id());

            // 장바구니 중복 확인
            if(cartService.cartCheck(cart) != 0) {
                return 2;
            }

            cartService.cartInsert(cart);
        }
        return 1;
    }

//        // 프리퀀시
//        if(회원) {
//
//            int 누적금액 = 0;
//            int 구매금액 = 0;
//            int 프리퀀시개수 = 0;
//
//            // 이벤트 목록 리스트에 담음
//            List<String> 이벤트리스트 = new ArrayList<>();
//            for ( 상품 : 상품리스트) {
//                이벤트리스트.add(상품.이벤트타입);
//            }
//
//            for ( 이벤트타입 : 이벤트리스트) {
//
//                // 해당 이벤트의 프리퀀시 데이터 존재하는지 확인
//                프리퀀시 f = getFrequency(사용자정보, 이벤트타입);
//                // 구매금액 초기화
//                구매금액 = 0;
//
//                for (상품 : 상품리스트) {
//                    // 해당 이벤트의 프리퀀시 상품 가격을 구함
//                    if (frequency_yn.equals('Y') && 이벤트타입.equals(상품.이벤트타입)) {
//                        구매금액 =+ 상품.가격;
//                    }
//                }
//
//                // 첫 구매
//                if(f == null) {
//                    누적금액 = 0;
//                    누적금액 = 누적금액 + 구매금액;
//                    프리퀀시개수 = (int) Math.floor((double) 누적금액 / 10만원);
//
//                    insert(사용자정보, 이벤트타입, 누적금액, 프리퀀시개수);
//                } else {
//                    // 해당 이벤트의 프리퀀시 기존 누적 금액 불러옴
//                    누적금액 = f.getFrequncyPrice();
//                    누적금액 = 누적금액 + 구매금액;
//                    프리퀀시개수 = (int) Math.floor((double) 누적금액 / 10만원);
//
//                    update(사용자정보, 이벤트타입, 누적금액, 프리퀀시개수);
//                }
//                // Math.floor( (double) (누적금액 + 구매금액) / 10) == 최종 프리퀀시 개수
//            }
//            // end of for
//
//            // 구매 목록 insert
//            insert(사용자정보, 상품리스트);
//
//        } else if(비회원) {
//            // 프리퀀시 해당 안 됨
//            insert(사용자정보, 상품리스트);
//        }
//
//        // 환불 및 취소
//        if(회원) {
//
//            int 누적금액 = 0;
//            int 환불금액 = 0;
//            int 프리퀀시개수 = 0;
//
//            // 이벤트 목록 리스트에 담음
//            List<String> 이벤트리스트 = new ArrayList<>();
//            for ( 환불 : 환불리스트) {
//                이벤트리스트.add(환불.이벤트타입);
//            }
//
//            for ( 이벤트타입 : 이벤트리스트) {
//                // 해당 이벤트의 프리퀀시 누적 금액 불러옴
//                누적금액 = getFrequncyPrice(사용자정보, 이벤트타입);
//                // 구매금액 초기화
//                환불금액 = 0;
//
//                for (환불 : 환불리스트) {
//                    if (frequency_yn.equals('Y') && 이벤트타입.equals(환불.이벤트타입)) {
//                        환불금액 =+ 환불.가격;
//                    }
//                }
//
//                누적금액 = 누적금액 - 환불금액;
//                프리퀀시개수 = (int) Math.floor((double) 누적금액 / 10만원);
//
//                update(사용자정보, 이벤트타입, 누적금액, 프리퀀시개수);
//            }
//            // end of for
//
//            update(사용자정보, 환불리스트);    // 취소처리
//
//        } else if (비회원) {
//            // 프리퀀시 해당 안 됨
//            update(사용자정보, 환불리스트);    // 취소처리
//        }

}


