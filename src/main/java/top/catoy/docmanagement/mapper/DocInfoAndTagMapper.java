package top.catoy.docmanagement.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.catoy.docmanagement.domain.Tag;

import java.util.List;

@Mapper
public interface DocInfoAndTagMapper {
    List<Integer> getDocIdByTagId(@Param("tags") List<Tag> tags);
}
