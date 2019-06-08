package top.catoy.docmanagement.service;

import top.catoy.docmanagement.domain.RecordPoint;
import top.catoy.docmanagement.domain.ResponseBean;

public interface BackupService {
    ResponseBean backupDatabase();
    ResponseBean recover(RecordPoint recordPoint);
}
