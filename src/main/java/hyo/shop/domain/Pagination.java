package hyo.shop.domain;

import lombok.Getter;

@Getter
public class Pagination {
    private int totalRecordCount;       // 전체 데이터 수
    private int totalPageCount;         // 전체 페이지 수
    private int pageGroup;              // 페이지 그룹
    private int startPage;              // 시작 페이지 번호
    private int endPage;                // 끝 페이지 번호
    private int limitStart;         // LIMIT 시작 위치
    private boolean existPrevPage;      // 이전 페이지 존재 여부
    private boolean existNextPage;      // 다음 페이지 존재 여부

    public Pagination(int totalRecordCount, SearchInfo searchInfo) {
        if(totalRecordCount > 0) {
            this.totalRecordCount = totalRecordCount;
            this.calculation(searchInfo);
        }
    }

    private void calculation(SearchInfo searchInfo) {

        // 전체 페이지 수 계산
        totalPageCount = (int) Math.ceil((double) totalRecordCount / 10);

        if(totalPageCount < searchInfo.getPageSize()) {
            searchInfo.setPageSize(totalPageCount);
        }

        // 페이지 그룹
        pageGroup = (int) Math.ceil((double) searchInfo.getPage() / searchInfo.getPageSize());
        // 화면에 보여질 마지막 페이지 번호
        endPage = pageGroup * searchInfo.getPageSize();

        // 끝 페이지가 전체 페이지 수보다 큰 경우, 끝 페이지 전체 페이지 수 저장
        if(endPage > totalPageCount) {
            endPage = totalPageCount;
        }

        // 화면에 보여질 첫번째 페이지 번호
        startPage = endPage - (searchInfo.getPageSize() - 1);

        // LIMIT 시작 위치 계산
        limitStart = (searchInfo.getPage() - 1) * 10;

        // 이전 페이지 존재 여부 확인
        existPrevPage = (startPage - 1) > 0;

        // 다음 페이지 존재 여부 확인
        existNextPage = endPage < totalPageCount;

        System.out.println("=========================================================");
        System.out.println("전체 데이터 수 : " + totalRecordCount);
        System.out.println("전체 페이지 수 : " + totalPageCount);
        System.out.println("현재 페이지 : " + searchInfo.getPage());
        System.out.println("1 페이지 당 게시글 수 : " + searchInfo.getRecordSize());
        System.out.println("보여질 페이지 사이즈 : " + searchInfo.getPageSize());
        System.out.println("LIMIT 시작 위치 : " + limitStart);
        System.out.println("페이지 그룹 : " + pageGroup);
        System.out.println("첫번째 페이지 번호 : " + startPage);
        System.out.println("마지막 페이지 번호 : " + endPage);
        System.out.println("이전 페이지가 존재 하는 지 : " + existPrevPage);
        System.out.println("다음 페이지가 존재 하는 지 : " + existNextPage);
        System.out.println("=========================================================");

    }

}
