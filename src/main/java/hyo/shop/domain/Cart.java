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
public class Cart {
    private Long cart_no;
    private String user_id;
    private Long goods_no;
    private Long option_no;
    private LocalDateTime cart_cklimit;
    private String cart_ckid;

}
