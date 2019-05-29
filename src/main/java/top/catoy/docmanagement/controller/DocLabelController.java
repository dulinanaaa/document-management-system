package top.catoy.docmanagement.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.catoy.docmanagement.domain.DocLabel;
import top.catoy.docmanagement.domain.ResponseBean;
import top.catoy.docmanagement.service.DocLabelService;

import java.util.List;

/**
 * @description:
 * @author: xjn
 * @create: 2019-05-18 17:16
 **/
@RestController
public class DocLabelController {
    @Autowired
    private DocLabelService docLabelService;

    @GetMapping("/public/getDocLabelsTree")
    public ResponseBean getAllDocLabels(){
        ResponseBean result = docLabelService.getDocLabelsTree();
        return result;
    }

    @RequestMapping(value = "/public/dragLabel",method = RequestMethod.POST)
    public ResponseBean dragLabel(@RequestParam("oldNode")String oldLabel,@RequestParam("newNode")String newlabel,
                                  @RequestParam("event")String event){
        System.out.println(oldLabel+","+newlabel+","+event);
        DocLabel docLabel = docLabelService.getDocLabelByName(oldLabel);
        if(event.equals("before") || event.equals("after")){
            int superId = docLabelService.getSuperIdByName(newlabel);
            docLabel.setSuperId(superId);
            ResponseBean responseBean = docLabelService.editDocLabel(docLabel);
            if(responseBean.getCode() == ResponseBean.SUCCESS){
                return new ResponseBean(ResponseBean.SUCCESS,"移动成功!",null);
            }else {
                return new ResponseBean(ResponseBean.FAILURE,"移动失败!",null);
            }
        }else if(event.equals("inner")){
            int superId = docLabelService.getIdByName(newlabel);
            docLabel.setSuperId(superId);
            ResponseBean responseBean = docLabelService.editDocLabel(docLabel);
            if(responseBean.getCode() == ResponseBean.SUCCESS){
                return new ResponseBean(ResponseBean.SUCCESS,"移动成功!",null);
            }else {
                return new ResponseBean(ResponseBean.FAILURE,"移动失败!",null);
            }
        }
        return null;
    }

    @RequestMapping(value = "/public/addLabels",method = RequestMethod.POST)
    public ResponseBean addLabel(@RequestBody DocLabel docLabel){
        System.out.println(docLabel);
        ResponseBean responseBean = docLabelService.getAllDocLabels();
        List<DocLabel> docLabels = (List<DocLabel>) responseBean.getData();
        boolean flag = false;
        for(int i = 0;i < docLabels.size();i++){
            if(docLabels.get(i).getDocLabelName().equals(docLabel.getDocLabelName())){
                flag = true;
            }
        }
        if(flag == true){
            return new ResponseBean(ResponseBean.FAILURE,"分类名已存在",null);
        }else {
            ResponseBean res = docLabelService.insertDocLabel(docLabel);
            if(res.getCode() == ResponseBean.SUCCESS){
                return new ResponseBean(ResponseBean.SUCCESS,"分类创建成功!",null);
            }else {
                return new ResponseBean(ResponseBean.FAILURE,"分类创建失败",null);
            }
        }
    }

    @RequestMapping(value = "/public/deleteLabels",method = RequestMethod.POST)
    public ResponseBean deleteLabel(@RequestBody DocLabel docLabel){
        System.out.println(docLabel);
        ResponseBean responseBean = docLabelService.delDocLabel(docLabel);
        if(responseBean.getCode() == ResponseBean.SUCCESS){
            return new ResponseBean(ResponseBean.SUCCESS,"删除成功",null);
        }else {
            return new ResponseBean(ResponseBean.FAILURE,"删除失败",null);
        }
    }

    @RequestMapping(value = "/public/updateLabels",method = RequestMethod.POST)
    public ResponseBean updateLabel(@RequestBody DocLabel docLabel){
        System.out.println(docLabel);
        ResponseBean responseBean = docLabelService.getAllDocLabels();
        List<DocLabel> docLabels = (List<DocLabel>) responseBean.getData();
        boolean flag = false;
        for(int i = 0;i < docLabels.size();i++){
            if(docLabels.get(i).getDocLabelName().equals(docLabel.getDocLabelName())){
                flag = true;
            }
        }
        if(flag == true) {
            return new ResponseBean(ResponseBean.FAILURE, "分类名已存在", null);
        }else{
            ResponseBean respon = docLabelService.editDocLabel(docLabel);
            if(respon.getCode() == ResponseBean.SUCCESS){
                return new ResponseBean(ResponseBean.SUCCESS,"修改分类成功",null);
            }else {
                return new ResponseBean(ResponseBean.FAILURE,"修改分类失败",null);
            }
        }
    }
}
