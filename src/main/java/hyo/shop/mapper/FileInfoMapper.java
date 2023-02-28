package hyo.shop.mapper;

import hyo.shop.domain.FileInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileInfoMapper {
    int insertFile(List<FileInfo> fileList);

    FileInfo selectFileDetail(Long fileNo);

    int deleteFile(Long goodsNo);

    int completeDeleteFile(Long goodsNo);

    List<FileInfo> selectFileList(Long goodsNo);

    int selectFileTotalCount(Long goodsNo);

    int undeleteFile(List<Long> fileNo);

    List<FileInfo> deleteFileList();

    int deleteFileOne(Long fileNo);
}
