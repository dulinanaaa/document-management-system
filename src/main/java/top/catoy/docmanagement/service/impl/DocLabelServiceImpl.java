package top.catoy.docmanagement.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.catoy.docmanagement.domain.DocLabel;
import top.catoy.docmanagement.domain.ResponseBean;
import top.catoy.docmanagement.mapper.DocLabelMapper;
import top.catoy.docmanagement.service.DocLabelService;

import java.util.List;

/**
 * @description:
 * @author: xjn
 * @create: 2019-04-28 12:44
 **/
@Service
public class DocLabelServiceImpl implements DocLabelService {
    @Autowired
    private DocLabelMapper docLabelMapper;

    /**
     * 插入标签
     * @param docLabel
     * @return
     */
    @Override
    public ResponseBean insertDocLabel(DocLabel docLabel) {
        try {
            int sum = docLabelMapper.insertLabel(docLabel);
            if(sum > 0){
                return new ResponseBean(ResponseBean.SUCCESS,"标签插入成功",null);
            }
            else {
                return new ResponseBean(ResponseBean.FAILURE,"标签插入失败",null);
            }
        }catch (RuntimeException r){
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    /**
     * 删除根据Id标签
     * @param docLabel
     * @return
     */
    @Override
    public ResponseBean delDocLabel(DocLabel docLabel) {
        try {
            int sum = docLabelMapper.delDocLabel(docLabel);
            if(sum > 0){
                return new ResponseBean(ResponseBean.SUCCESS,"标签删除成功",null);
            }
            else {
                return new ResponseBean(ResponseBean.FAILURE,"标签删除失败",null);
            }
        }catch (RuntimeException r){
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    /**
     * 根据id更新标签
     * @param docLabel
     * @return
     */
    @Override
    public ResponseBean editDocLabel(DocLabel docLabel) {
        try {
            int sum = docLabelMapper.editDocLabel(docLabel);
            if(sum > 0){
                return new ResponseBean(ResponseBean.SUCCESS,"标签修改成功",null);
            }
            else {
                return new ResponseBean(ResponseBean.FAILURE,"标签修改失败",null);
            }
        }catch (RuntimeException r){
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    /**
     * 得到所有标签
     * @return
     */
    @Override
    public ResponseBean getAllDocLabels() {
        try {
            List<DocLabel> docLabels = docLabelMapper.getAllDocLabels();
            if(docLabels != null){
                return new ResponseBean(ResponseBean.SUCCESS,"查询成功",docLabels);
            }
            else {
                return new ResponseBean(ResponseBean.FAILURE,"插叙失败",null);
            }
        }catch (RuntimeException r){
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    /**
     * 得到所有子标签
     * @param superId
     * @return
     */
    @Override
    public ResponseBean getSonDocLabels(int superId) {
      try {
          List<DocLabel> docLabels = docLabelMapper.getDocLabelBySuperId(superId);
          if(docLabels != null){
              return new ResponseBean(ResponseBean.SUCCESS,"子标签查找成功",docLabels);
          }else{
              return new ResponseBean(ResponseBean.FAILURE,"子标签查找失败",null);
          }
      }catch (RuntimeException r){
          return new ResponseBean(ResponseBean.ERROR,"错误",null);
      }
    }

    /**
     * 通过Id查询标签
     * @param id
     * @return
     */
    @Override
    public ResponseBean getDocLabelById(int id) {
        try {
            DocLabel docLabel = docLabelMapper.getDocLabelById(id);
            if(docLabel != null){
                return new ResponseBean(ResponseBean.SUCCESS,"标签查询成功",docLabel);
            }
            else {
                return new ResponseBean(ResponseBean.FAILURE,"标签查询失败",null);
            }
        }catch (RuntimeException r){
            return new ResponseBean(ResponseBean.ERROR,"错误",null);
        }
    }

    @Override
    public List<DocLabel> getLabelByName(String[] labels) {
        List<DocLabel> docLabels = docLabelMapper.getLabelsByName(labels);
        return docLabels;
    }
}
