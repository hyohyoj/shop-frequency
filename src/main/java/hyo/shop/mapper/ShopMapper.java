package hyo.shop.mapper;

import hyo.shop.domain.Goods;
import org.springframework.stereotype.Repository;

@Repository
public interface ShopMapper {
    int insert(Goods goods);
}
