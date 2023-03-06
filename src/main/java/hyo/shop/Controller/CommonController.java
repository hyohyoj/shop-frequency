package hyo.shop.Controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Paths;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CommonController {

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
}
