package top.catoy.docmanagement.mapper;


import java.util.List;

@Mapper
public interface DocInfoAndTagMapper {

    public int insertDocInfoAndTag(DocInfoAndTag docInfoAndTag);


    public List<DocInfoAndTag> getTagsByFileId(@Param("fileId") int fileId);


    public int insertDocInfoAndTag(@Param("doclabels") List<DocInfoAndDocLabel> docInfoAndDocLabels);



    List<Integer> getDocIdByTagId(@Param("tags") List<Tag> tags);
}
