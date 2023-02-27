package hyo.shop.mapper;

import hyo.shop.domain.Goods;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface ShopMapper {
    int goodsCount();
    int insert(Goods goods);
    List<Goods> goodsList(Map<String, Object> map);
}
