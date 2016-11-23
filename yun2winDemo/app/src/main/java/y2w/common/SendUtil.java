package y2w.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.y2w.uikit.utils.ImageUtil;

import java.io.File;

/**
 * Created by maa2 on 2016/4/5.
 */
public class SendUtil {
    private static final int IMAGE_MAX_LENGTH = 560;

    /**
     * 压缩原图
     * @param context
     * @param filePath
     * @return
     */
    public static String compressOriginPicture(Context context,String filePath){
        String filePathTemp="";
        try {
            File file=new File(filePath);
            String filename = file.getName();
            int lastIndex = filename.lastIndexOf(".");
            String tFileName = filename.substring(0, lastIndex) + ".jpg";
            filePathTemp = Config.DEFAULT_PATH_FILE + ".origin_" + tFileName;
            Bitmap originBitmap = BitmapFactory.decodeFile(filePath);
            if(originBitmap != null){
                //确定保存目录存在
                file = new File(Config.DEFAULT_PATH_FILE);
                if(!file.exists()){
                    file.mkdirs();
                }
                ImageUtil.saveImageForSendMeg(filePathTemp, originBitmap, context);
            }

        } catch (Exception e) {
        }

        return filePathTemp;
    }

    public static String getImageThumbnail(String filePath){
        String filePathTemp="";
        try {
            File file=new File(filePath);
            String fileName = file.getName();
            int lastIndex = fileName.lastIndexOf(".");
            String tFileName = fileName.substring(0, lastIndex) + ".jpg";
            filePathTemp = Config.DEFAULT_PATH_FILE + ".thumb_" + tFileName;
            Bitmap thumbnail = ImageUtil.getImageThumbnail(filePath, IMAGE_MAX_LENGTH);
            //保存缩略图
            if(thumbnail != null){
                //确定保存目录存在
                file = new File(Config.DEFAULT_PATH_FILE);
                if(!file.exists()){
                    file.mkdirs();
                }
                ImageUtil.saveImageToSD(null, filePathTemp, thumbnail, 70);
            }
        } catch (Exception e) {
        }

        return filePathTemp;
    }

    public static String getMovieThumbnail(String filePath){
        String filePathTemp="";
        try {
            File file=new File(filePath);
            String fileName = file.getName();
            int lastIndex = fileName.lastIndexOf(".");
            String tFileName = fileName.substring(0, lastIndex) + ".jpg";
            filePathTemp = Config.DEFAULT_PATH_FILE + ".thumb_" + tFileName;
            Bitmap thumbnail = ImageUtil.getVideoThumbnail(filePath, IMAGE_MAX_LENGTH);
            //保存缩略图
            if(thumbnail != null){
                //确定保存目录存在
                file = new File(Config.DEFAULT_PATH_FILE);
                if(!file.exists()){
                    file.mkdirs();
                }
                ImageUtil.saveImageToSD(null, filePathTemp, thumbnail, 70);
            }
        } catch (Exception e) {
        }

        return filePathTemp;
    }

}
