package top.catoy.docmanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.catoy.docmanagement.domain.FileSource;
import top.catoy.docmanagement.service.FileSourceService;

import java.util.List;

@Service
public class FileSourceServiceImpl implements FileSourceService {


    @Autowired
    private FileSourceService fileSourceService;

    @Override
    public int insertFileSource(FileSource fileSource) {
        return fileSourceService.insertFileSource(fileSource);
    }

    @Override
    public int deleteFileSource(FileSource fileSource) {
        return fileSourceService.deleteFileSource(fileSource);
    }

    @Override
    public List<FileSource> getAllFileSource() {
        return fileSourceService.getAllFileSource();
    }


}
