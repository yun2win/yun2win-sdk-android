package y2w.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.example.maa2.uikit.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.y2w.uikit.customcontrols.imageview.CircleImageView;

import y2w.base.AppContext;
import y2w.base.Urls;
import y2w.manage.Users;

/**
 * Created by hejie on 2015/11/13.
 */
public class HeadImageView extends CircleImageView {

    public static final int DEFAULT_THUMB_SIZE = 100;

    private DisplayImageOptions options = createImageOptions();

    private static final DisplayImageOptions createImageOptions() {
        int defaultIcon = R.drawable.default_contact;
        return new DisplayImageOptions.Builder()
                .showImageOnLoading(defaultIcon)
                .showImageOnFail(defaultIcon)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }

    public HeadImageView(Context context) {
        super(context);
    }

    public HeadImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HeadImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    /**
     * 加载用户头像
     *
     * @param uid
     */
    public void loadBuddyAvatarbyuid(final String uid,final int defaulticon) {
        // 先显示默认头像
         String fileToken = "?access_token=" + Users.getInstance().getCurrentUser().getToken();
        ImagePool.getInstance(AppContext.getAppContext()).load(
                uid,fileToken,
                this, defaulticon);
    }

    /**
     * 加载用户头像
     *
     * @param url
     */
    public void loadBuddyAvatarbyurl(final String url,final int defaulticon) {
        // 先显示默认头像

        if(url!=null&&url.contains("http")){
            ImagePool.getInstance(AppContext.getAppContext()).load(
                    url,null,
                    this, defaulticon);
        }else{
            String fileToken = "?access_token=" + Users.getInstance().getCurrentUser().getToken();
            ImagePool.getInstance(AppContext.getAppContext()).load(
                    Urls.User_Messages_File_DownLoad+url,fileToken,
                    this, defaulticon);
        }
    }
    /**
     * 解决ViewHolder复用问题
     */
    public void resetImageView() {
        setImageBitmap(null);
    }
}
