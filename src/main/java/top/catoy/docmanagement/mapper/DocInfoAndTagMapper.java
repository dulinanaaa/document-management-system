package top.catoy.docmanagement.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.catoy.docmanagement.domain.DocInfoAndDocLabel;
import top.catoy.docmanagement.domain.DocInfoAndTag;

import java.util.List;

@Mapper
public interface DocInfoAndTagMapper {

    public int insertDocInfoAndTag(DocInfoAndTag docInfoAndTag);


    public List<DocInfoAndTag> getTagsByFileId(@Param("fileId") int fileId);


    public int insertDocInfoAndTag(@Param("doclabels") List<DocInfoAndDocLabel> docInfoAndDocLabels);



}
