package hyo.shop.Controller;

import hyo.shop.Service.FileInfoService;
import hyo.shop.domain.FileInfo;
import hyo.shop.domain.Goods;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/shop/**")
@Slf4j
@RequiredArgsConstructor
public class ShopController {

    private final FileInfoService fileInfoService;

    // 이미지 출력
    @GetMapping(value = "/image/{uploaddate}/{imagename}", produces = {MediaType.IMAGE_PNG_VALUE, MediaType.IMAGE_JPEG_VALUE})
    public ResponseEntity<byte[]> imageSearch(
            @PathVariable("imagename") String imagename,
            @PathVariable("uploaddate") String uploaddate) throws IOException
    {
        String uploadPath = Paths.get("C:", "develop", "shopImage", uploaddate, imagename).toString();

        InputStream imageStream = new FileInputStream(uploadPath);
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();

        return new ResponseEntity<byte[]>(imageByteArray, HttpStatus.OK);

    }

//        // 프리퀀시
//        if(회원) {
//
//            int 누적금액 = 0;
//            int 구매금액 = 0;
//            int 프리퀀시개수 = 0;
//
//            // 이벤트 목록 리스트에 담음
//            List<String> 이벤트리스트 = new ArrayList<>();
//            for ( 상품 : 상품리스트) {
//                이벤트리스트.add(상품.이벤트타입);
//            }
//
//            for ( 이벤트타입 : 이벤트리스트) {
//
//                // 해당 이벤트의 프리퀀시 데이터 존재하는지 확인
//                프리퀀시 f = getFrequency(사용자정보, 이벤트타입);
//                // 구매금액 초기화
//                구매금액 = 0;
//
//                for (상품 : 상품리스트) {
//                    // 해당 이벤트의 프리퀀시 상품 가격을 구함
//                    if (frequency_yn.equals('Y') && 이벤트타입.equals(상품.이벤트타입)) {
//                        구매금액 =+ 상품.가격;
//                    }
//                }
//
//                // 첫 구매
//                if(f == null) {
//                    누적금액 = 0;
//                    누적금액 = 누적금액 + 구매금액;
//                    프리퀀시개수 = (int) Math.floor((double) 누적금액 / 10만원);
//
//                    insert(사용자정보, 이벤트타입, 누적금액, 프리퀀시개수);
//                } else {
//                    // 해당 이벤트의 프리퀀시 기존 누적 금액 불러옴
//                    누적금액 = f.getFrequncyPrice();
//                    누적금액 = 누적금액 + 구매금액;
//                    프리퀀시개수 = (int) Math.floor((double) 누적금액 / 10만원);
//
//                    update(사용자정보, 이벤트타입, 누적금액, 프리퀀시개수);
//                }
//                // Math.floor( (double) (누적금액 + 구매금액) / 10) == 최종 프리퀀시 개수
//            }
//            // end of for
//
//            // 구매 목록 insert
//            insert(사용자정보, 상품리스트);
//
//        } else if(비회원) {
//            // 프리퀀시 해당 안 됨
//            insert(사용자정보, 상품리스트);
//        }
//
//        // 환불 및 취소
//        if(회원) {
//
//            int 누적금액 = 0;
//            int 환불금액 = 0;
//            int 프리퀀시개수 = 0;
//
//            // 이벤트 목록 리스트에 담음
//            List<String> 이벤트리스트 = new ArrayList<>();
//            for ( 환불 : 환불리스트) {
//                이벤트리스트.add(환불.이벤트타입);
//            }
//
//            for ( 이벤트타입 : 이벤트리스트) {
//                // 해당 이벤트의 프리퀀시 누적 금액 불러옴
//                누적금액 = getFrequncyPrice(사용자정보, 이벤트타입);
//                // 구매금액 초기화
//                환불금액 = 0;
//
//                for (환불 : 환불리스트) {
//                    if (frequency_yn.equals('Y') && 이벤트타입.equals(환불.이벤트타입)) {
//                        환불금액 =+ 환불.가격;
//                    }
//                }
//
//                누적금액 = 누적금액 - 환불금액;
//                프리퀀시개수 = (int) Math.floor((double) 누적금액 / 10만원);
//
//                update(사용자정보, 이벤트타입, 누적금액, 프리퀀시개수);
//            }
//            // end of for
//
//            update(사용자정보, 환불리스트);    // 취소처리
//
//        } else if (비회원) {
//            // 프리퀀시 해당 안 됨
//            update(사용자정보, 환불리스트);    // 취소처리
//        }

}


