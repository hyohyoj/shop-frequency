package hyo.shop.mapper;

import hyo.shop.domain.FileInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileInfoMapper {
    int insertFile(List<FileInfo> fileList);

    FileInfo selectFileDetail(Long fileNo);

    int deleteFile(Long boardNo);

    int completeDeleteFile(Long boardNo);

    List<FileInfo> selectFileList(Integer boardNo);

    int selectFileTotalCount(Long boardNo);

    int undeleteFile(List<Long> fileNo);

    List<FileInfo> deleteFileList();

    int deleteFileOne(Long fileNo);
}
