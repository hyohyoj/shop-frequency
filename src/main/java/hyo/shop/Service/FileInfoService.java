package hyo.shop.Service;

import hyo.shop.domain.FileInfo;
import hyo.shop.mapper.FileInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class FileInfoService {
    private final FileInfoMapper fileInfoMapper;

    public int insertFiles(List<FileInfo> fileList) {
        return fileInfoMapper.insertFile(fileList);
    }

    public List<FileInfo> selectFileList(Long goodsNo) {
        return fileInfoMapper.selectFileList(goodsNo);
    }

    public FileInfo selectFileDetail(Long file_no) { return fileInfoMapper.selectFileDetail(file_no); }

    public int deleteFile(Long goodsNo) {
        return fileInfoMapper.deleteFile(goodsNo);
    }

    public int completeDeleteFile(Long goodsNo) {
        return fileInfoMapper.completeDeleteFile(goodsNo);
    }

    public int undeleteFile(List<Long> file_no) {
        return fileInfoMapper.undeleteFile(file_no);
    }

    public List<FileInfo> deleteFileList() {
        return fileInfoMapper.deleteFileList();
    }

    public int deleteFileOne(Long file_no) {
        return fileInfoMapper.deleteFileOne(file_no);
    }

}
