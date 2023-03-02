package hyo.shop.mapper;

import hyo.shop.domain.Cart;
import org.springframework.stereotype.Repository;

@Repository
public interface CartMapper {
    int cartInsert(Cart cart);
    int cartCheck(Cart cart);
}
