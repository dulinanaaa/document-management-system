package top.catoy.docmanagement.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.catoy.docmanagement.domain.RecordPoint;

@Mapper
public interface RecordPointMapper {
    int addRecordPoint(RecordPoint recordPoint);
    int delRecordPoint(RecordPoint recordPoint);
}
