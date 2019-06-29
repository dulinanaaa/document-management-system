package top.catoy.docmanagement.domain;

public class FileSource {

    private int fileSourceId;

    private String source_name;

    public int getFileSourceId() {
        return fileSourceId;
    }

    public void setFileSourceId(int fileSourceId) {
        this.fileSourceId = fileSourceId;
    }


    public String getSource_name() {
        return source_name;
    }

    public void setSource_name(String source_name) {
        this.source_name = source_name;
    }

    @Override
    public String toString() {
        return "FileSource{" +
                "fileSourceId=" + fileSourceId +
                ", source_name='" + source_name + '\'' +
                '}';
    }
}
