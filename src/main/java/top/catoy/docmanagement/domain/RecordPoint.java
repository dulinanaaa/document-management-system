package top.catoy.docmanagement.domain;

/**
 * @description: 记录点
 * @author: xjn
 * @create: 2019-06-07 18:46
 **/
public class RecordPoint {
    private int recordPointId;
    private String timestamp;
    private String sqlFileName;
    private String docDirName;
    private String backupPath;//备份目录路径

    public int getRecordPointId() {
        return recordPointId;
    }

    public void setRecordPointId(int recordPointId) {
        this.recordPointId = recordPointId;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getSqlFileName() {
        return sqlFileName;
    }

    public void setSqlFileName(String sqlFileName) {
        this.sqlFileName = sqlFileName;
    }

    public String getDocDirName() {
        return docDirName;
    }

    public void setDocDirName(String docDirName) {
        this.docDirName = docDirName;
    }

    public String getBackupPath() {
        return backupPath;
    }

    public void setBackupPath(String backupPath) {
        this.backupPath = backupPath;
    }

    @Override
    public String toString() {
        return "RecordPoint{" +
                "recordPointId=" + recordPointId +
                ", timestamp='" + timestamp + '\'' +
                ", sqlFileName='" + sqlFileName + '\'' +
                ", docDirName='" + docDirName + '\'' +
                ", backupPath='" + backupPath + '\'' +
                '}';
    }
}
