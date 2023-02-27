package hyo.shop.Service;

import hyo.shop.domain.Goods;
import hyo.shop.mapper.ShopMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopMapper shopMapper;

    public int goodsCount() {
        return shopMapper.goodsCount();
    }

    public int insert(Goods goods) {
        return shopMapper.insert(goods);
    }

    public List<Goods> goodsList(Map<String, Object> map) {
        return shopMapper.goodsList(map);
    }
}
