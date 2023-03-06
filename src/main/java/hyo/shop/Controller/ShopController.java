package hyo.shop.Controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hyo.shop.Service.CartService;
import hyo.shop.Service.FrequencyService;
import hyo.shop.Service.ShopService;
import hyo.shop.common.FileUtils;
import hyo.shop.common.SessionConstants;
import hyo.shop.domain.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.json.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.WebUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
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
    private final FrequencyService frequencyService;
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

        mv.setViewName("/shop/goodsDetail");
        mv.addObject("goods", goods);

        return mv;
    }

    @PostMapping("/cartInsert")
    @ResponseBody
    public int cartInsert(@RequestBody Cart cart,
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

            cartService.cartInsert(cart);
            cartService.updateCookieLimit(cart);
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

    @GetMapping("/cartSelect")
    @ResponseBody
    public ModelAndView cartSelect(@SessionAttribute(name = SessionConstants.LOGIN_MEMBER, required = false) Login loginMember,
                                   HttpServletRequest request)
    {
        Cookie cookie = WebUtils.getCookie(request, "cartCookie");

        ModelAndView mv = new ModelAndView("jsonView");

        List<Goods> goodsNoList = new ArrayList<>();
        List<Goods> goodsList = null;

        List<Map<String, Object>> cartList = new ArrayList<>();

        Cart cart = new Cart();

        // 비회원
        if(loginMember == null && cookie != null) {
            cart.setCart_ckid(cookie.getValue());

            cartList = cartService.selectCartList(cart);
        }
        // 회원
        else if(loginMember != null) {
            cart.setUser_id(loginMember.getUser_id());

            cartList = cartService.selectCartList(cart);
        }

        if(!cartList.isEmpty()) {
            for (Map<String, Object> map : cartList) {
                Goods goods = new Goods();
                goods.setGoods_no(Long.valueOf(String.valueOf(map.get("goods_no"))));

                goodsNoList.add(goods);
            }
            goodsList = shopService.getCartGoodsList(goodsNoList);
            goodsList = fileUtils.setImageUploadPath(goodsList);    // 출력 이미지 경로 지정
        }

        mv.setViewName("/shop/cartDetail");
        mv.addObject("cartList", cartList);
        mv.addObject("goodsList", goodsList);

        return mv;
    }

    @PostMapping("/cartQuantityUpdate")
    @ResponseBody
    public String cartQuantityUpdate(@RequestBody Cart cart) {
        String result = "fail";

        int success = cartService.updateQuantity(cart);

        if(success > 0) {
            result = "success";
        }

        return result;
    }

    @PostMapping("/cartSelectYnUpdate")
    @ResponseBody
    public String cartSelectYnUpdate(@RequestBody Cart cart) {
        String result = "fail";

        int success = cartService.updateSelectYn(cart);

        if(success > 0) {
            result = "success";
        }

        return result;
    }

    @PostMapping("/cartSelectYnUpdateAll")
    @ResponseBody
    public String cartSelectYnUpdateAll(@RequestBody Cart cart, HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, "cartCookie");

        String result = "fail";

        // 비회원
        if(cart.getUser_id() == null && cookie != null) {
            cart.setCart_ckid(cookie.getValue());
        }

        int success = cartService.updateSelectYnAll(cart);

        if(success > 0) {
            result = "success";
        }

        return result;
    }

    @PostMapping("/cartDelete")
    @ResponseBody
    public String cartDelete(@RequestBody Cart cart) {
        String result = "fail";

        int success = cartService.cartDelete(cart);

        if(success > 0) {
            result = "success";
        }

        return result;
    }

    @PostMapping("/cartSelectDelete")
    @ResponseBody
    public String cartSelectDelete(@RequestBody List<Cart> cartList) {
        String result = "fail";

        int success = cartService.cartSelectDelete(cartList);

        if(success > 0) {
            result = "success";
        }

        return result;
    }

    // 프리퀀시 적립
    @PostMapping("/order")
    @ResponseBody
    public String order(@RequestBody Map<String, Object> map) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        // JSON 데이터 받아옴
        String json = mapper.writeValueAsString(map.get("items"));
        String sessionId = map.get("sessionId").toString();
        List<Order> orderList = mapper.readValue(json, new TypeReference<ArrayList<Order>>(){});

        int totalPrice = 0;
        int price = 0;
        int frequencyCount = 0;

        Map<Long, Integer> priceMap = new HashMap<>();  // Map<이벤트타입, 구매금액>

        int success = 0;
        String result = "fail";

        // 이벤트 별 프리퀀시 상품 구매 금액 저장
        for (Order order : orderList) {
            if(order.getEvent_type() != null) {
                Long typeNo = order.getEvent_type();
                String frequencyYn = order.getFrequency_yn();

                // 구매금액 초기화
                price = 0;

                if(priceMap.containsKey(typeNo)) {
                    price = priceMap.get(typeNo);
                }
                if(frequencyYn.equals("Y")) {
                    price += (order.getPrice() * order.getQuantity());
                }

                priceMap.put(typeNo, price);
            }
        }

        for(Long typeNo : priceMap.keySet()) {
            Frequency f = new Frequency();
            f.setType_no(typeNo);
            f.setUser_id(sessionId);    // 구매자 아이디 -> 세션 아이디로 사용

            // 해당 이벤트의 프리퀀시 데이터 존재하는지 확인
            Frequency frequency = frequencyService.getFrequency(f);

            price = priceMap.get(typeNo);

            // 첫 구매
            if(frequency == null) {
                totalPrice = 0;
                totalPrice += price;

                // 프리퀀시 최대 금액은 30만원 (총 3개)
                if(totalPrice > 300000) {
                    totalPrice = 300000;
                }

                frequencyCount = (int) Math.floor((double) totalPrice / 100000);   // 누적금액 10만원 당 프리퀀시 1개 적립

                f.setFreq_count(frequencyCount);
                f.setFreq_amount(totalPrice);

                success += frequencyService.insertFrequency(f);
            }
            else {
                // 해당 이벤트의 프리퀀시 기존 누적 금액 불러옴
                totalPrice = frequency.getFreq_amount();
                totalPrice += price;

                // 프리퀀시 최대 금액은 30만원 (총 3개)
                if(totalPrice > 300000) {
                    totalPrice = 300000;
                }

                frequencyCount = (int) Math.floor((double) totalPrice / 100000);   // 누적금액 10만원 당 프리퀀시 1개 적립

                f.setFreq_count(frequencyCount);
                f.setFreq_amount(totalPrice);

                success += frequencyService.updateFrequency(f);
            }

        }   // end of for

        Frequency f2 = new Frequency();
        f2.setUser_id(sessionId);
        List<Frequency> fList = frequencyService.getFrequencyList(f2);

        if(success == priceMap.size()) {
            result = "success";
        }

        JSONObject jo = new JSONObject();
        jo.put("frequencyList", fList);
        jo.put("result", result);

        return jo.toString();
    }

    // 환불 및 취소
    @PostMapping("/refund")
    @ResponseBody
    public String refund(@RequestBody Map<String, Object> map) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();

        // JSON 데이터 받아옴
        String json = mapper.writeValueAsString(map.get("items"));
        String sessionId = map.get("sessionId").toString();
        List<Order> orderList = mapper.readValue(json, new TypeReference<ArrayList<Order>>(){});

        int totalPrice = 0;
        int price = 0;
        int frequencyCount = 0;

        Map<Long, Integer> priceMap = new HashMap<>();  // Map<이벤트타입, 구매금액>

        int success = 0;
        String result = "fail";

        // 이벤트 별 프리퀀시 상품 환불 금액 저장
        for (Order order : orderList) {
            if(order.getEvent_type() != null) {
                Long typeNo = order.getEvent_type();
                String frequencyYn = order.getFrequency_yn();

                // 환불금액 초기화
                price = 0;

                if (priceMap.containsKey(typeNo)) {
                    price = priceMap.get(typeNo);
                }
                if (frequencyYn.equals("Y")) {
                    price += (order.getPrice() * order.getQuantity());
                }

                priceMap.put(typeNo, price);
            }
        }

        for(Long typeNo : priceMap.keySet()) {
            Frequency f = new Frequency();
            f.setType_no(typeNo);
            f.setUser_id(sessionId);    // 구매자 아이디 -> 세션 아이디로 사용

            Frequency frequency = frequencyService.getFrequency(f);

            price = priceMap.get(typeNo);

            // 해당 이벤트의 프리퀀시 기존 누적 금액 불러옴
            totalPrice = frequency.getFreq_amount();
            totalPrice -= price;

            if(totalPrice < 0) {
                totalPrice = 0;
            }

            frequencyCount = (int) Math.floor((double) totalPrice / 100000);

            f.setFreq_count(frequencyCount);
            f.setFreq_amount(totalPrice);

            success += frequencyService.updateFrequency(f);

        }   // end of for

        Frequency f2 = new Frequency();
        f2.setUser_id(sessionId);
        List<Frequency> fList = frequencyService.getFrequencyList(f2);

        if(success == priceMap.size()) {
            result = "success";
        }

        JSONObject jo = new JSONObject();
        jo.put("frequencyList", fList);
        jo.put("result", result);

        return jo.toString();
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


