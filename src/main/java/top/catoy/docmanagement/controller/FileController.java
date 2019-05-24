package top.catoy.docmanagement.controller;



import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
import jdk.nashorn.internal.ir.ReturnNode;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.catoy.docmanagement.domain.*;
import top.catoy.docmanagement.service.*;
import top.catoy.docmanagement.utils.JWTUtil;

import org.apache.shiro.subject.Subject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@RestController
public class FileController {

    @Value("${com.sxito.custom.windows-path}")
    private String windowsuploadPath;

    @Value("${com.sxito.custom.linux-path}")
    private String linuxuploadPath;

    @Autowired
    private DocInfoService docInfoService;

    @Autowired
    private TagService tagService;

    @Autowired
    private AnnexService annexService;

    @Autowired
    private DocInfoAndTagService docInfoAndTagService;

    @Autowired
    private DocLabelService docLabelService;

    @Autowired
    private DocInfoAndDocLabelService docInfoAndDocLabelService;

    /***
     * 单文件上传
     * @param file
     * @return
     */
    @RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
    public ResponseBean uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("department")String department
    , @RequestParam("region") String region, @RequestParam("type")String type, @RequestParam("date")String date, @RequestParam("number")String number,
                                   @RequestParam("tags")String tags,HttpServletRequest request) {
        String upload = null;
        Subject subject = SecurityUtils.getSubject();
        String token = (String) subject.getPrincipal();
        User user = JWTUtil.getUserInfo(token);
        if (System.getProperty("os.name").equals("Windows 10")) {
            upload = windowsuploadPath;
        } else if (System.getProperty("os.name").equals("Linux")) {
            upload = linuxuploadPath;
        }
        else if (System.getProperty("os.name").equals("Windows 7")) {
            upload = windowsuploadPath;
        }
        if (Objects.isNull(file) || file.isEmpty()) {
            return new ResponseBean(ResponseBean.FAILURE, "文件为空,请重新上传", null);
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(upload + file.getOriginalFilename());
            if (!Files.isWritable(path)) {
                Files.createDirectories(Paths.get(upload));
            }
            Files.write(path, bytes);
            DocInfo docInfo = new DocInfo();
            docInfo.setDocSavePath(upload + file.getOriginalFilename());
            docInfo.setDocName(file.getOriginalFilename());
            String fileName = file.getOriginalFilename();
            int pos = fileName.lastIndexOf('.');
            String suffix = fileName.substring(pos);
            docInfo.setSuffixName(suffix);
            docInfo.setUserId(user.getUserId());
            docInfo.setDepartmentId(user.getDepartmentId());
            docInfoService.insertDocInfo(docInfo);
            int docId = docInfoService.getDocId(docInfo);
            String tag[] = tags.split(",");
            for(int i = 0;i < tag.length;i++){
                Tag t = tagService.getTagByName(tag[i]);
                if(t == null){
                    Tag tg = new Tag();
                    tg.setTagName(tag[i]);
                    tg.setIsuse(1);
                    tagService.insertTags(tg);
                    DocInfoAndTag docInfoAndTag = new DocInfoAndTag();
                    docInfoAndTag.setDocInfo_id(docId);
                    int tagId = tagService.getIdByTagName(tag[i]);
                    docInfoAndTag.setTag_id(tagId);
                    docInfoAndTagService.insertDocInfoAndTag(docInfoAndTag);
                }else {
                    DocInfoAndTag docInfoAndTag = new DocInfoAndTag();
                    docInfoAndTag.setDocInfo_id(docId);
                    int tagId = tagService.getIdByTagName(tag[i]);
                    docInfoAndTag.setTag_id(tagId);
                    docInfoAndTagService.insertDocInfoAndTag(docInfoAndTag);
                }
            }
            String types[] = type.split(",");
            List<DocLabel> docLabels = docLabelService.getLabelByName(types);
            for(int i = 0;i < docLabels.size();i++){
                System.out.println("type"+types[i]);
                DocInfoAndDocLabel docInfoAndDocLabel = new DocInfoAndDocLabel();
                System.out.println("id:"+docLabels.get(i).getDocLabelId());
                docInfoAndDocLabel.setLabelId(docLabels.get(i).getDocLabelId());
                docInfoAndDocLabel.setDocId(docId);
                docInfoAndDocLabelService.insertDocInfoAndDocLabel(docInfoAndDocLabel);
            }
            return new ResponseBean(ResponseBean.SUCCESS, "上传成功", file.getOriginalFilename());
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseBean(ResponseBean.FAILURE, "上传失败", null);
        }
    }


    @RequestMapping(value = "/public/getTagById",method = RequestMethod.POST)
    public ResponseBean getTagByFileId(@RequestParam("fileId") int id){
        List<DocInfoAndTag> list = docInfoAndTagService.getTagsByFileId(id);
        List<Tag> tags = new ArrayList<>();
        for(int i = 0;i < list.size();i++){
            Tag tag = tagService.getTagById(list.get(i).getTag_id());
            tags.add(tag);
        }
        if(tags != null){
            return new ResponseBean(ResponseBean.SUCCESS,"查找成功",tags);
        }else {
            return new ResponseBean(ResponseBean.FAILURE,"查找失败",null);
        }
    }



    @RequestMapping(value = "/uploadannex",method = RequestMethod.POST)
    public ResponseBean uploadannex(@RequestParam("file")MultipartFile file,@RequestParam("filename")String name){
        String upload = null;
        if (System.getProperty("os.name").equals("Windows 10")) {
            upload = windowsuploadPath;
        } else if (System.getProperty("os.name").equals("Linux")) {
            upload = linuxuploadPath;
        }
        else if (System.getProperty("os.name").equals("Windows 7")) {
            upload = windowsuploadPath;
        }
        try {
            byte[] bytes = file.getBytes();
            Path path = Paths.get(upload + file.getOriginalFilename());
            if(Files.isWritable(path)){
                Files.createDirectories(Paths.get(upload));
            }
            Files.write(path, bytes);
        }catch (Exception e){

        }
        DocInfo docInfo = new DocInfo();
        docInfo.setDocName(name);
        int docId = docInfoService.getDocId(docInfo);
        Annex annex = new Annex();
        annex.setAnnexName(file.getOriginalFilename());
        annex.setAnnexPath(upload+file.getOriginalFilename());
        annex.setDocId(docId);
        int result = annexService.insertAnnex(annex);
        if(result > 0){
            return new ResponseBean(ResponseBean.SUCCESS,"添加成功",null);
        }else {
            return new ResponseBean(ResponseBean.FAILURE,"添加失败",null);
        }
    }

    @RequestMapping(value = "/getName",method = RequestMethod.GET)
    public void getDocName(@RequestParam("name") String name,@RequestParam("token")String token,HttpServletResponse response){
        System.out.println(name+"----------------------"+token);
        String download = null;
        if (System.getProperty("os.name").equals("Windows 7")) {
            download = windowsuploadPath;
        } else if (System.getProperty("os.name").equals("Linux")) {
            download = linuxuploadPath;
        }else if(System.getProperty("os.name").equals("Windows 10")){
            download = windowsuploadPath;
        }
        String fileName = null;
        try {
            fileName = new String(name.getBytes("utf-8"),"ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String file = name;
        response.setHeader("content-type","application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] bytes = new byte[1024];
        BufferedInputStream bufferedInputStream = null;
        OutputStream os = null;
        try {
            os = response.getOutputStream();
            bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(download
                    + file)));
            int k = bufferedInputStream.read(bytes);
            while (k != -1){
                os.write(bytes, 0, bytes.length);
                os.flush();
                k = bufferedInputStream.read(bytes);
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (bufferedInputStream != null) {
                try {
                    bufferedInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @RequestMapping(value = "/public/preViewFile",method = RequestMethod.POST)
    public void PreViewFile(@RequestParam("FileName") String filename){



    }

    public boolean officeToPDF(String sourceFile,String destFile){
        try {

            File inputFile = new File(sourceFile);
            if (!inputFile.exists()) {
                // 找不到源文件, 则返回false
                return false;
            }
            // 如果目标路径不存在, 则新建该路径
            File outputFile = new File(destFile);
            if (!outputFile.getParentFile().exists()) {
                outputFile.getParentFile().mkdirs();
            }
            //如果目标文件存在，则删除
            if (outputFile.exists()) {
                outputFile.delete();
            }
            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            OpenOfficeConnection connection = new SocketOpenOfficeConnection("127.0.0.1", 8100);
            connection.connect();
            //用于测试openOffice连接时间
            System.out.println("连接时间:" + df.format(new Date()));
            DocumentConverter converter = new StreamOpenOfficeDocumentConverter(
                    connection);
            converter.convert(inputFile, outputFile);
            //测试word转PDF的转换时间
            System.out.println("转换时间:" + df.format(new Date()));
            connection.disconnect();
            return true;
        } catch (ConnectException e) {
            e.printStackTrace();
            System.err.println("openOffice连接失败！请检查IP,端口");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    @RequestMapping(value = "/public/downloadFileAndAnnex",method = RequestMethod.POST)
    public void DownloadFileAndAnnex(@RequestBody DocInfo docInfo,HttpServletResponse response){
        System.out.println(docInfo);
        String download = null;
        if (System.getProperty("os.name").equals("Windows 7")) {
            download = windowsuploadPath;
        } else if (System.getProperty("os.name").equals("Linux")) {
            download = linuxuploadPath;
        }else if(System.getProperty("os.name").equals("Windows 10")){
            download = windowsuploadPath;
        }
        String fileName = docInfo.getDocName();
        response.setHeader("content-type","application/octet-stream");
        response.setContentType("application/octet-stream");
        response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
        byte[] bytes = new byte[1024];
        BufferedInputStream bufferedInputStream = null;
        OutputStream os = null;
//        try {
//            os = response.getOutputStream();
//            bufferedInputStream = new BufferedInputStream(new FileInputStream(new File(download
//                    + fileName)));
//            int k = bufferedInputStream.read(bytes);
//            while (k != -1){
//                os.write(bytes, 0, bytes.length);
//                os.flush();
//                k = bufferedInputStream.read(bytes);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        } finally {
//            if (bufferedInputStream != null) {
//                try {
//                    bufferedInputStream.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
    }


    /***
     *
     * @return
     */
    @RequestMapping(value = "/downLoadFile", method = RequestMethod.POST)
    public void downLoadFife(@RequestParam("docs") String filelist, HttpServletResponse response) {
        String download = null;
        System.out.println(filelist);
        JSONArray json = JSONArray.fromObject(filelist ); // 首先把字符串转成 JSONArray  对象
        String fileName = "";
        for(int i =0 ;i < json.size();i++){
            JSONObject job = json.getJSONObject(i);
            fileName = (String) job.get("fileName") +"."+ (String)job.get("type");
            if (System.getProperty("os.name").equals("Windows 10")) {
                download = windowsuploadPath;
            } else if (System.getProperty("os.name").equals("Linux")) {
                download = linuxuploadPath;
            }
            System.out.println(fileName);
            response.setHeader("content-type","application/octet-stream");
            response.setContentType("application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
            byte[] buff = new byte[1024];
            BufferedInputStream bis = null;
            OutputStream os = null;
            try {
                os = response.getOutputStream();
                bis = new BufferedInputStream(new FileInputStream(new File(download
                        + fileName)));
                int k = bis.read(buff);
                while (k != -1) {
                    os.write(buff, 0, buff.length);
                    os.flush();
                    k = bis.read(buff);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (bis != null) {
                    try {
                        bis.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }



        }


    }
}
