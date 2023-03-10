package hyo.shop.Service;

import hyo.shop.domain.Cart;
import hyo.shop.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartMapper cartMapper;

    public int cartInsert(Cart cart) {
        return cartMapper.cartInsert(cart);
    }

    public int cartCheck(Cart cart) {
        return cartMapper.cartCheck(cart);
    }

    public int cartDelete(Cart cart) {
        return cartMapper.cartDelete(cart);
    }

    public int cartSelectDelete(List<Cart> cartlist) {
        return cartMapper.cartSelectDelete(cartlist);
    }

    public List<Map<String, Object>> selectCartList(Cart cart) {
        return cartMapper.selectCartList(cart);
    }

    public int updateCartList(Cart cart) {
        return cartMapper.updateCartList(cart);
    }

    public int updateCookieLimit(Cart cart) {
        return cartMapper.updateCookieLimit(cart);
    }

    public int updateQuantity(Cart cart) {
        return cartMapper.updateQuantity(cart);
    }

    public int updateSelectYn(Cart cart) {
        return cartMapper.updateSelectYn(cart);
    }

    public int updateSelectYnAll(Cart cart) {
        return cartMapper.updateSelectYnAll(cart);
    }
}
