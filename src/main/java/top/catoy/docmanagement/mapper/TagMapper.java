package top.catoy.docmanagement.mapper;

import org.apache.ibatis.annotations.Mapper;
import top.catoy.docmanagement.domain.Tag;


@Mapper
public interface TagMapper {

    public int insertTag(Tag tag);


}
