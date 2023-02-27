package hyo.shop.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Goods {
    private Long goodsNo;
    private String goodsTitle;
    private String goodsDesc;
    private int goodsPrice;
    private int goodsStock;
    private String frequencyYn;
    private String insertUserId;
    private LocalDateTime insertDate;

    //
    private String changeYn;
    private String imageUploadPath;
    private String thumbnailUploadPath;

}
