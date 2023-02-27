package hyo.shop.common;

import hyo.shop.Service.FileInfoService;
import hyo.shop.domain.FileInfo;
import hyo.shop.domain.Goods;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.commons.io.FilenameUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

// @Bean과 달리 직접 작성한 클래스를 스프링 컨테이너에 등록하는 데 사용됨.
@Component
@RequiredArgsConstructor
public class FileUtils {

    private final FileInfoService fileInfoService;

    private final String today = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));

    /** 업로드 경로 */
    private final String uploadPath = Paths.get("C:", "develop", "shopImage", today).toString();

    /**
     * 서버에 생성할 파일명을 처리할 랜덤 문자열 반환
     * @return 랜덤 문자열
     */
    private final String getRandomString() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 서버에 첨부 파일을 생성하고, 업로드 파일 목록 반환
     * @param files    - 파일 Array
     * @param goodsNo - 게시글 번호
     * @return 업로드 파일 목록
     */
    public List<FileInfo> uploadFiles(MultipartFile[] files, Long goodsNo) {

        int count = 0;
        // 파일이 비어있을 경우 비어있는 리스트 반환
        for (MultipartFile file : files) {
            if(file.getSize() < 1) {
                count++;
            }
        }
        if(count == files.length) {
            return Collections.emptyList();
        }

        List<FileInfo> fileList = new ArrayList<>();

        // uploadPath에 해당하는 디렉터리가 존재하지 않으면, 부모 디렉터리를 포함한 모든 디렉터리를 생성
        File dir = new File(uploadPath);
        if(!dir.exists()) {
            dir.mkdirs();
        }

        for(MultipartFile file : files) {
            try {
                // 파일 확장자
                final String extension = FilenameUtils.getExtension(file.getOriginalFilename());

                // 서버에 저장할 파일명 (랜덤 문자열 + 확장자)
                final String saveName = getRandomString() + "." + extension;

                // 업로드 경로에 파일 생성
                File target = new File(uploadPath, saveName);
                file.transferTo(target);

                // 썸네일 생성
                createThumbnailFile(target, saveName);

                FileInfo fileInfo = new FileInfo();
                fileInfo.setGoods_no(goodsNo);
                fileInfo.setOriginal_name(file.getOriginalFilename());
                fileInfo.setSave_name(saveName);
                fileInfo.setSize(file.getSize());
                fileInfo.setThumbnail_file("s_" + saveName);
                fileInfo.setFileExtension(extension);

                if(!file.getOriginalFilename().equals("")) {    // 비어있는 파일명 제외
                    fileList.add(fileInfo);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } // end of for

        return fileList;
    }

    /**
     * 파일 확장자 반환
     * @param files    - 파일 Array
     * @return 업로드 파일 목록의 확장자
     */
    public List<FileInfo> checkFileExtention(MultipartFile[] files) {

        int count = 0;
        // 파일이 비어있을 경우 비어있는 리스트 반환
        for (MultipartFile file : files) {
            if(file.getSize() < 1) {
                count++;
            }
        }
        if(count == files.length) {
            return Collections.emptyList();
        }

        List<FileInfo> fileList = new ArrayList<>();

        for(MultipartFile file : files) {
            try {
                // 파일 확장자
                final String extension = FilenameUtils.getExtension(file.getOriginalFilename());

                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileExtension(extension);

                if(!file.getOriginalFilename().equals("")) {    // 비어있는 파일명 제외
                    fileList.add(fileInfo);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } // end of for

        return fileList;
    }

    /**
     * 이미지 출력 경로 지정해주는 함수
     * @param goodsList    - 상품 리스트
     * @return 상품 리스트
     */
    public List<Goods> setImageUploadPath(List<Goods> goodsList) {
        List<FileInfo> fileList = null;

        String extension = "";
        String uploadDate = "";
        String uploadPath = "";
        String thumbnailPath = "";

        for (Goods goods : goodsList) {
            // 게시글의 첨부 파일 모두 가져옴
            fileList = fileInfoService.selectFileList(goods.getGoodsNo().intValue());

            for (FileInfo file : fileList) {
                // 파일 확장자 체크
                extension = FilenameUtils.getExtension(file.getOriginal_name());

                // 파일이 이미지인 경우
                if(extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg")) {
                    // 파일 저장 경로 찾기
                    uploadDate = file.getInsert_time().format(DateTimeFormatter.ofPattern("yyMMdd"));
                    uploadPath = Paths.get("shop","image", uploadDate, file.getSave_name()).toString();
                    thumbnailPath = Paths.get("shop","image", uploadDate, file.getThumbnail_file()).toString();

                    goods.setImageUploadPath(uploadPath);
                    goods.setThumbnailUploadPath(thumbnailPath);
                    break;
                } else {
                    // 이미지가 없는 경우 임시 이미지 출력
                    uploadPath = "../images/thumbnail.png";
                    thumbnailPath = "../images/small_thumbnail.png";
                    goods.setImageUploadPath(uploadPath);
                    goods.setThumbnailUploadPath(thumbnailPath);
                }
            }
        }   // end of for

        return goodsList;
    }

    public void createThumbnailFile(File saveFile, String saveName) throws IOException {

        try{
            // ImageIO를 통한 썸네일 파일 생성
//            File thumbnailFile = new File(uploadPath, "s_" + saveName);
//
//            BufferedImage bo_image = ImageIO.read(saveFile);
//
//            /* 비율 */
//            double ratio = 3;
//            /*넓이 높이*/
//            int width = (int) (bo_image.getWidth() / ratio);
//            int height = (int) (bo_image.getHeight() / ratio);
//
//            BufferedImage bt_image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
//
//            Graphics2D graphic = bt_image.createGraphics();
//            graphic.drawImage(bo_image, 0, 0, width, height, null);
//
//            ImageIO.write(bt_image, "jpg", thumbnailFile);


            // thumbnailaotor 라이브러리 사용
            File thumbnailFile = new File(uploadPath, "s_" + saveName);

            BufferedImage bo_image = ImageIO.read(saveFile);

            /* 비율 */
            double ratio = 3;
            /*넓이 높이*/
            int width = (int) (bo_image.getWidth() / ratio);
            int height = (int) (bo_image.getHeight() / ratio);

            Thumbnails.of(saveFile)
                    .size(width, height)
                    .toFile(thumbnailFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteFiles() {
        //현재 게시판에 존재하는 파일객체를 만듬
        File file = new File(uploadPath + "\\" + "저장된 파일 이름");

        if(file.exists()) { // 파일이 존재하면
            file.delete(); // 파일 삭제
        }
    }
    
}
