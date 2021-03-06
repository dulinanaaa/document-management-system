package top.catoy.docmanagement.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.catoy.docmanagement.domain.FileSource;

import java.io.File;
import java.util.List;

@Mapper
public interface FileSourceMapper {

    public int insertFileSource(FileSource fileSource);

    public int deleteFileSouce(FileSource fileSource);

    public List<FileSource> getAllFileSouce(FileSource fileSource);

    public int getFileSourceById(@Param("fileSourceName")String fileSourceName);

    public String getFileSourceNameById(@Param("fileSourceId")int fileSourceId);
}
