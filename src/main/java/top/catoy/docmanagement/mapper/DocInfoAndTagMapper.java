package top.catoy.docmanagement.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import top.catoy.docmanagement.domain.DocInfoAndDocLabel;
import top.catoy.docmanagement.domain.DocInfoAndTag;
import top.catoy.docmanagement.domain.Tag;

import java.util.List;

@Mapper
public interface DocInfoAndTagMapper {

    int insertDocInfoAndTag(DocInfoAndTag docInfoAndTag);


    List<DocInfoAndTag> getTagsByFileId(@Param("fileId") int fileId);


    int insertDocInfoAndTag(@Param("doclabels") List<DocInfoAndDocLabel> docInfoAndDocLabels);

    int delByDocId(@Param("docId") int docId);

    int insertDocInfoAndTags(@Param("tagList") List<Tag> docLabels,@Param("docId") int docInfoId);

    List<Integer> getDocIdByTagId(@Param("tags") List<Tag> tags);

    List<Tag> getTagsByDocId(@Param("docId") int docId);
}
