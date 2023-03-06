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
public class Frequency {
    private Long freq_no;
    private Long type_no;
    private String user_id;
    private int freq_count;
    private int freq_amount;
    private LocalDateTime insert_time;
    private LocalDateTime update_time;
    private String delete_yn;

}
