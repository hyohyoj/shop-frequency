package hyo.shop.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SearchInfo {
    private int page = 1;                  // 현재 페이지 번호
    private int recordSize = 10;           // 페이지당 출력할 데이터 개수
    private int pageSize = 10;             // 화면 하단에 출력할 페이지 사이즈
    private String keyword;           // 검색 키워드
    private String searchType;        // 검색 유형
    private Pagination pagination;    // 페이지네이션 정보

}
