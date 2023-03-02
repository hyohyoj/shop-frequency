package hyo.shop.Service;

import hyo.shop.domain.Cart;
import hyo.shop.mapper.CartMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
