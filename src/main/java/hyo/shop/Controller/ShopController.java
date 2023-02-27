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
        String uploadPath = Paths.get("C:", "develop", "upload", uploaddate, imagename).toString();

        InputStream imageStream = new FileInputStream(uploadPath);
        byte[] imageByteArray = IOUtils.toByteArray(imageStream);
        imageStream.close();

        return new ResponseEntity<byte[]>(imageByteArray, HttpStatus.OK);
    }
}
