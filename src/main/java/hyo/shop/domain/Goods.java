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
    private Long goods_no;
    private String goods_title;
    private String goods_desc;
    private int goods_price;
    private int goods_stock;
    private String frequency_yn;
    private String insert_user_id;
    private LocalDateTime insert_date;

    //
    private String changeYn;
    private String imageUploadPath;
    private String thumbnailUploadPath;

}
