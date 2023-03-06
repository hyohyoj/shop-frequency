package hyo.shop.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long event_type;
    private int price;
    private int quantity;
    private String frequency_yn;
    private String user_id;
}
