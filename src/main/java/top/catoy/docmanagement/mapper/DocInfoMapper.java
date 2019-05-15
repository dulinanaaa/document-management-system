package top.catoy.docmanagement.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.catoy.docmanagement.domain.DocInfo;

import java.util.List;

@Mapper
public interface DocInfoMapper {

    int insertDocInfo(DocInfo docInfo);

    List<DocInfo> getAllDocInfo();

    List<DocInfo> getDocByDepartmentId(@Param("departmentId") int departmentId);



}
