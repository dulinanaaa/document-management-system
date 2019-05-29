package top.catoy.docmanagement.utils;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter;

import java.awt.image.BufferedImage;
import java.io.*;

//import com.spire.pdf.PdfDocument;
//import com.spire.pdf.PdfPageBase;
//import com.spire.pdf.graphics.PdfImage;


public class ImageToPDF {
//    public boolean imgToPdf(String imgFilePath, String pdfFilePath)throws IOException {
//        File file=new File(imgFilePath);
//        if(file.exists()){
//            Document document = new Document();
//            FileOutputStream fos = null;
//            try {
//                fos = new FileOutputStream(pdfFilePath);
//                PdfWriter.getInstance(document, fos);
//
//// 添加PDF文档的某些信息，比如作者，主题等等
//                document.addAuthor("arui");
//                document.addSubject("test pdf.");
//// 设置文档的大小
//                document.setPageSize(PageSize.A4);
//// 打开文档
//                document.open();
//// 写入一段文字
////document.add(new Paragraph("JUST TEST ..."));
//// 读取一个图片
//                Image image = Image.getInstance(imgFilePath);
//                float imageHeight=image.getScaledHeight();
//                float imageWidth=image.getScaledWidth();
//                int i=0;
//                while(imageHeight>500||imageWidth>500){
//                    image.scalePercent(100-i);
//                    i++;
//                    imageHeight=image.getScaledHeight();
//                    imageWidth=image.getScaledWidth();
//                    System.out.println("imageHeight->"+imageHeight);
//                    System.out.println("imageWidth->"+imageWidth);
//                }
//
//                image.setAlignment(Image.ALIGN_CENTER);
////     //设置图片的绝对位置
//// image.setAbsolutePosition(0, 0);
//// image.scaleAbsolute(500, 400);
//// 插入一个图片
//                document.add(image);
//            } catch (DocumentException de) {
//                System.out.println(de.getMessage());
//            } catch (IOException ioe) {
//                System.out.println(ioe.getMessage());
//            }
//            document.close();
//            fos.flush();
//            fos.close();
//            return true;
//        }else{
//            return false;
//        }
 //   }
    public static void main(String[] args){
//        try {
//            new ImageToPDF().imgToPdf("D:\\img\\imooc.jpg","E:\\demo.pdf");
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        PdfDocument doc = new PdfDocument();
//        PdfPageBase page = doc.getPages().add();
////        doc.getPages().remove(page);
////        doc.getPages().removeAt(0);
//        PdfImage image = PdfImage.fromFile("D:\\img\\imooc.jpg");
//        float widthFitRate = (float) (getImgWidth(new File("D:\\img\\imooc.jpg"))/ page.getActualBounds(true).getWidth());
//        float heightFitRate = (float) (getImgHeight(new File("D:\\img\\imooc.jpg"))/ page.getActualBounds(true).getHeight());
//
//        float fitRate = Math.max(widthFitRate, heightFitRate);
//        float fitWidth = getImgWidth(new File("D:\\img\\imooc.jpg")) / fitRate*0.8f;
//        float fitHeight = getImgHeight(new File("D:\\img\\imooc.jpg"))/ fitRate*0.8f;
//
//        page.getCanvas().drawImage(image, 50, 30, fitWidth, fitHeight);
//
//        //保存并关闭
//        doc.saveToFile("D:\\ImageToPDF.pdf");
//        doc.close();

    }

//    public static int getImgWidth(File ImageFile){
//        InputStream is = null;
//        BufferedImage src = null;
//        int ret = -1;
//        try{
//            is = new FileInputStream(ImageFile);
//            src = javax.imageio.ImageIO.read(is);
//            ret = src.getWidth(null);
//            is.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return ret;
//    }
//
//
//    public static int getImgHeight(File ImageFile){
//        InputStream is = null;
//        BufferedImage src = null;
//        int ret = -1;
//        try{
//            is = new FileInputStream(ImageFile);
//            src = javax.imageio.ImageIO.read(is);
//            ret = src.getHeight(null);
//            is.close();
//        }catch (Exception e){
//            e.printStackTrace();
//        }
//        return ret;
//    }
}
