package top.catoy.docmanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.catoy.docmanagement.domain.FileSource;
import top.catoy.docmanagement.mapper.FileSourceMapper;
import top.catoy.docmanagement.service.FileSourceService;

import java.util.List;

@Service
public class FileSourceServiceImpl implements FileSourceService {


    @Autowired
    private FileSourceMapper fileSourceMapper;

    @Override
    public int insertFileSource(FileSource fileSource) {
        return fileSourceMapper.insertFileSource(fileSource);
    }

    @Override
    public int deleteFileSource(FileSource fileSource) {
        return fileSourceMapper.deleteFileSouce(fileSource);
    }

    @Override
    public List<FileSource> getAllFileSource() {
        return fileSourceMapper.getAllFileSouce(null);
    }

    @Override
    public int getFileSourceIdByName(String name) {
        return fileSourceMapper.getFileSourceById(name);
    }


}
