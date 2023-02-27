package hyo.shop.Service;

import hyo.shop.domain.Goods;
import hyo.shop.mapper.ShopMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class ShopService {

    private final ShopMapper shopMapper;

    public int insert(Goods goods) {
        return shopMapper.insert(goods);
    }
}
