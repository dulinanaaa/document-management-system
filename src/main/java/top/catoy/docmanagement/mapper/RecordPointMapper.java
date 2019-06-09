package top.catoy.docmanagement.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.catoy.docmanagement.domain.RecordPoint;

import java.util.List;

@Mapper
public interface RecordPointMapper {
    int addRecordPoint(RecordPoint recordPoint);
    int delRecordPoint(RecordPoint recordPoint);
    List<RecordPoint> getAllRecordPoints();
}
