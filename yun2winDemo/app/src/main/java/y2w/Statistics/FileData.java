package y2w.Statistics;

import android.content.Context;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

/**
 * Created by SongJie on 09/13 0013.
 */
public class FileData {
    private final static String fileName = "channel";
    private Object mLockFile = new Object();

    /**
     * 保存统计的频道信息到文件
     * @param data 格式为{...},{...},{... }
     */
    public void saveDataToFile(Context mContext,String data){
        try {
            synchronized (mLockFile) {
                File filePath = new File(mContext.getFilesDir() + "/" + fileName);
                boolean fileExist = true;//默认文件存在
                if (!filePath.exists()) {
                    fileExist = false;
                }
                FileOutputStream outStream = mContext.openFileOutput(fileName, Context.MODE_APPEND);
                if (fileExist) {
                    outStream.write(",".getBytes());
                }
                outStream.write(data.getBytes());
                outStream.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 读取文件中的数据
     * @return 格式为{...},{...},{... }
     */
    public String readDataFromFile(Context mContext){
        try {
            synchronized (mLockFile) {
                File filePath = new File(mContext.getFilesDir() + "/" + fileName);
                if (filePath.exists()) {
                    String data="";
                    FileInputStream inputStream = mContext.openFileInput(fileName);
                    byte[] bytes = new byte[1024];
                    while(inputStream.read(bytes)!=-1){
                        data = data+bytes.toString();
                    }
                    inputStream.close();
                    //读取完数据删除文件
                    filePath.delete();
                    return data;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
