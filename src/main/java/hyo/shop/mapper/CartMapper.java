package hyo.shop.mapper;

import hyo.shop.domain.Cart;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CartMapper {
    int cartInsert(Cart cart);
    int cartCheck(Cart cart);
    int cartDelete(Cart cart);
    int cartSelectDelete(List<Cart> cartlist);
    List<Map<String, Object>> selectCartList(Cart cart);
    int updateCartList(Cart cart);
    int updateCookieLimit(Cart cart);
    int updateQuantity(Cart cart);
    int updateSelectYn(Cart cart);
    int updateSelectYnAll(Cart cart);
}
