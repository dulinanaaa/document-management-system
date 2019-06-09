package top.catoy.docmanagement.controller;



import ch.qos.logback.classic.pattern.SyslogStartConverter;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.StreamOpenOfficeDocumentConverter;
//import com.spire.pdf.PdfDocument;
//import com.spire.pdf.PdfPageBase;
//import com.spire.pdf.graphics.PdfImage;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfImage;
import com.itextpdf.text.pdf.PdfWriter;
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
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ConnectException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @Autowired
    private LogService logService;

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
        if(subject.isPermitted("上传")){
            String token = (String) subject.getPrincipal();
            User user = JWTUtil.getUserInfo(token);
            upload = getPath();
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
                System.out.println(tags.equals("")+"??????????????????????????????????????????????????????????");
                if(tags.equals("") == false){
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
        }else {
            return new ResponseBean(ResponseBean.FAILURE,"你没有权限进行此操作",null);
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
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("上传")){
          upload = getPath();
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
            String token = (String) subject.getPrincipal();
            User user = JWTUtil.getUserInfo(token);
            docInfo.setDepartmentId(user.getDepartmentId());
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
        }else {
            return new ResponseBean(ResponseBean.FAILURE,"你没有该权限",null);
        }
    }

    @RequestMapping(value = "/getName",method = RequestMethod.GET)
    public ResponseBean getDocName(@RequestParam("name") String name,@RequestParam("token")String token,HttpServletResponse response){

        download(name,response);
        return new ResponseBean(ResponseBean.SUCCESS,"下载成功!",null);
    }

    public void download(String name,HttpServletResponse response){
//        System.out.println(name+"----------------------"+token);
        String download = null;
        download = getPath();
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


    public String getPath(){
        String path = null;
        if(System.getProperty("os.name").indexOf("Windows") != -1){
            path = windowsuploadPath;
        }else {
            path = linuxuploadPath;
        }
        return path;
    }

    public boolean imgToPdf(String imgFilePath, String pdfFilePath)throws IOException {
        File file=new File(imgFilePath);
        if(file.exists()){
            Document document = new Document();
            FileOutputStream fos = null;
            try {
                fos = new FileOutputStream(pdfFilePath);
                PdfWriter.getInstance(document, fos);

// 添加PDF文档的某些信息，比如作者，主题等等
                document.addAuthor("arui");
                document.addSubject("test pdf.");
// 设置文档的大小
                document.setPageSize(PageSize.A4);
// 打开文档
                document.open();
// 写入一段文字
//document.add(new Paragraph("JUST TEST ..."));
// 读取一个图片
                Image image = Image.getInstance(imgFilePath);
                float imageHeight=image.getScaledHeight();
                float imageWidth=image.getScaledWidth();
                int i=0;
                while(imageHeight>500||imageWidth>500){
                    image.scalePercent(100-i);
                    i++;
                    imageHeight=image.getScaledHeight();
                    imageWidth=image.getScaledWidth();
                    System.out.println("imageHeight->"+imageHeight);
                    System.out.println("imageWidth->"+imageWidth);
                }

                image.setAlignment(Image.ALIGN_CENTER);
//     //设置图片的绝对位置
// image.setAbsolutePosition(0, 0);
// image.scaleAbsolute(500, 400);
// 插入一个图片
                document.add(image);
            } catch (DocumentException de) {
                System.out.println(de.getMessage());
            } catch (IOException ioe) {
                System.out.println(ioe.getMessage());
            }
            document.close();
            fos.flush();
            fos.close();
            return true;
        }else{
            return false;
        }
    }



    @RequestMapping(value = "/public/preViewFile",method = RequestMethod.POST)
    public ResponseBean PreViewFile(@RequestParam("FilePath") String filePath){
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("预览")){
            try {
                System.out.println(filePath);
                int last = filePath.lastIndexOf('/');
                int lastpot = filePath.lastIndexOf('.');
                String suffix = filePath.substring(filePath.lastIndexOf('.')+1);
                if(suffix.equals("jpg") || suffix.equals("png")){
                    String name = filePath.substring(last+1,lastpot);
                    try {
                        String destFile = "G:\\session_data\\PHPTutorial\\WWW\\"+name+".pdf";
                        boolean flag = imgToPdf(filePath,destFile);
                        if(flag == true){
                            return new ResponseBean(ResponseBean.SUCCESS,"",destFile);
                        }else {
                            return new ResponseBean(ResponseBean.FAILURE,"",null);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                String name = filePath.substring(last+1,lastpot);
                System.out.println(name);
                String destFile = "G:\\session_data\\PHPTutorial\\WWW\\"+name+".pdf";
                boolean flag = officeToPDF(filePath,destFile);
                if(flag == true){
                    return new ResponseBean(ResponseBean.SUCCESS,"",destFile);
                }else {
                    return new ResponseBean(ResponseBean.FAILURE,"",null);
                }
            }catch (Exception e){
                return new ResponseBean(ResponseBean.FAILURE,"该文件无法预览",null);
            }
        }else {
            return new ResponseBean(ResponseBean.FAILURE,"你没有该权限!",null);
        }
    }

    @RequestMapping("/public/deleteAnnex")
    public ResponseBean deleteAnnex(@RequestBody Annex annex){
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("删除")){
            int result = annexService.deleteAnnex(annex);
            if(result > 0 ){
                return new ResponseBean(ResponseBean.SUCCESS,"删除成功！",null);
            }else {
                return new ResponseBean(ResponseBean.FAILURE,"删除失败！",null);
            }
        }else {
            return new ResponseBean(ResponseBean.FAILURE,"你没有该权限",null);
        }

//        return null;
    }




    public boolean officeToPDF(String sourceFile,String destFile) throws Exception{
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
            System.out.println(444);
            System.err.println("openOffice连接失败！请检查IP,端口");
        } catch (Exception e) {
            System.out.println(444);

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


    @RequestMapping(value = "/public/downloadZip",method = RequestMethod.GET)
    public void downLoadZips(@RequestParam("name") String fileName,HttpServletResponse response) throws Exception{
        System.out.println(fileName);

        DocInfo docInfo = docInfoService.getDocInfoByName(fileName);
        List<Annex> docInfos = annexService.getAnnexListByDocId(docInfo.getDocId());
        List files = new ArrayList();
        File f = new File(docInfo.getDocSavePath());
        files.add(f);
        for(int i = 0;i < docInfos.size();i++){
            File file = new File(docInfos.get(i).getAnnexPath());
            files.add(file);
        }
        downLoadFiles(files,response);

//        return "成功";
    }


    public HttpServletResponse downLoadFiles(List<File> files,HttpServletResponse response){
        try {
            String zipFilename = "D:/tempFile.zip";
            File file = new File(zipFilename);
            file.createNewFile();
            if (!file.exists()) {
                file.createNewFile();
            }
            response.reset();
            FileOutputStream fous = new FileOutputStream(file);
            ZipOutputStream zipOut = new ZipOutputStream(fous);
            zipFile(files, zipOut);
            zipOut.close();
            fous.close();
            return downloadZip(file, response);
        }catch (Exception e){
            e.printStackTrace();
        }
        return response;
    }

    public void zipFile(List files,ZipOutputStream outputStream){
        int size = files.size();
        for(int i = 0;i < size;i++){
            File file = (File) files.get(i);
            zipFile(file,outputStream);
        }
    }


    public static void zipFile(File inputFile, ZipOutputStream ouputStream) {
        try {
            if (inputFile.exists()) {
                if (inputFile.isFile()) {
                    FileInputStream IN = new FileInputStream(inputFile);
                    BufferedInputStream bins = new BufferedInputStream(IN, 512);
                    ZipEntry entry = new ZipEntry(inputFile.getName());
                    ouputStream.putNextEntry(entry);
                    // 向压缩文件中输出数据
                    int nNumber;
                    byte[] buffer = new byte[512];
                    while ((nNumber = bins.read(buffer)) != -1) {
                        ouputStream.write(buffer, 0, nNumber);
                    }
                    // 关闭创建的流对象
                    bins.close();
                    IN.close();
                } else {
                    try {
                        File[] files = inputFile.listFiles();
                        for (int i = 0; i < files.length; i++) {
                            zipFile(files[i], ouputStream);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static HttpServletResponse downloadZip(File file, HttpServletResponse response) {
        if (file.exists() == false) {
            System.out.println("待压缩的文件目录：" + file + "不存在.");
        } else {
            try {
                // 以流的形式下载文件。
                InputStream fis = new BufferedInputStream(new FileInputStream(file.getPath()));
                byte[] buffer = new byte[fis.available()];
                fis.read(buffer);
                fis.close();
                // 清空response
                response.reset();

                OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
                response.setContentType("application/octet-stream");

                // 如果输出的是中文名的文件，在此处就要用URLEncoder.encode方法进行处理
                response.setHeader("Content-Disposition",
                        "attachment;filename=" + new String(file.getName().getBytes("GB2312"), "ISO8859-1"));
                toClient.write(buffer);
                toClient.flush();
                toClient.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                try {
                    File f = new File(file.getPath());
                    f.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return response;
    }


    @RequestMapping(value = "/deleteFile",method = RequestMethod.POST)
    public ResponseBean deleteFile(@RequestBody DocInfo docInfo){
        Subject subject = SecurityUtils.getSubject();
        if(subject.isPermitted("删除")){
            System.out.println(docInfo);
            int result = docInfoService.deleteDocInfo(docInfo);
            File file = new File(docInfo.getDocSavePath());

            List<Annex> annexes = annexService.getAnnexListByDocId(docInfo.getDocId());
//            int res =
            if(file.exists() && file.isFile()){
                if(result > 0 && file.delete()){
                    User u = JWTUtil.getUserInfo((String) SecurityUtils.getSubject().getPrincipal());
                    logService.insertLog(u.getUserId(), "删除文件-"+docInfo.getDocName(), "文件管理");
                    return new ResponseBean(ResponseBean.SUCCESS,"删除成功!",null);
                }else {
                    return new ResponseBean(ResponseBean.FAILURE,"删除失败!",null);
                }
            }else {
                return new ResponseBean(ResponseBean.FAILURE,"删除失败!",null);
            }
        }else {
            return new ResponseBean(ResponseBean.FAILURE,"你没有该权限",null);
        }
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
