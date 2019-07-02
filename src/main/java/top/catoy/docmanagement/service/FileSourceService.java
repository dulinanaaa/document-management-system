package top.catoy.docmanagement.service;

import top.catoy.docmanagement.domain.FileSource;

import java.util.List;

public interface FileSourceService {

    public int insertFileSource(FileSource fileSource);


    public int deleteFileSource(FileSource fileSource);

    public List<FileSource> getAllFileSource();

    public int getFileSourceIdByName(String name);

}
