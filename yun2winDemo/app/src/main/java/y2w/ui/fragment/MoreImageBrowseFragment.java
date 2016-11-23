package y2w.ui.fragment;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.photoview.PhotoView;
import com.y2w.uikit.customcontrols.photoview.PhotoViewAttacher;
import com.y2w.uikit.customcontrols.view.RoundProgressBar;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import java.io.File;

import y2w.base.AppContext;
import y2w.common.AsyncMultiPartGet;
import y2w.common.Config;
import y2w.common.ImagePool;
import y2w.common.FileUtil;
import y2w.ui.dialog.Y2wDialog;


/**
 * Created by maa2 on 2015/12/11.
 */
public class MoreImageBrowseFragment extends Fragment {

    private static Activity activity;
    private static AppContext appContext;
    private String imgurl;
    private String DEFAULT = "default";

    private String selected = "";
    //private AsyncDownload downloadImagePost = null;
    RelativeLayout rl_preview;
    PhotoView iv_preview;
    TextView rotateButton;
    RoundProgressBar progressBar;
    private int degree = 0;
    private Bitmap bitmap =null;
    public static MoreImageBrowseFragment newInstance(Activity _activity,
                                                      AppContext _appContext, String imgurl) {
        activity = _activity;
        appContext = _appContext;
        MoreImageBrowseFragment newFragment = new MoreImageBrowseFragment();
        Bundle bundle = new Bundle();
        bundle.putString("imgurl", imgurl);
        newFragment.setArguments(bundle);
        return newFragment;

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();

        imgurl = args != null ? args.getString("imgurl") : "";
        View view = null;
        view = inflater.inflate(R.layout.image_browse_pager_item, container,
                false);

        rl_preview = (RelativeLayout) view.findViewById(R.id.rl_preview);
        iv_preview = (PhotoView) view.findViewById(R.id.iv_preview);
        rotateButton = (TextView) view.findViewById(R.id.message_image_rotate);
        progressBar = (RoundProgressBar) view.findViewById(R.id.rb_image);
            try {
                    preview(imgurl,iv_preview,rotateButton,progressBar);
            } catch (Exception e) {
                e.printStackTrace();
            }
        rl_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        iv_preview.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String fileName=null;
                String imgPath = null;
                try {
                    fileName = imgurl.substring(imgurl.lastIndexOf("/") + 1);
                    if (com.y2w.uikit.utils.FileUtil.checkFilePathExists(Config.DEFAULT_PATH_FILE + fileName)) {
                        imgPath = Config.DEFAULT_PATH_FILE + fileName;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if(imgPath==null){
                    return true;
                }
                Y2wDialog dialog = new Y2wDialog(activity);
                dialog.addOption("保存到手机");
                dialog.show();
                final String finalImgPath = imgPath;
                final String finalFileName = fileName;
                dialog.setOnOptionClickListener(new Y2wDialog.onOptionClickListener() {
                    @Override
                    public void onOptionClick(String option, int position) {
                        if(FileUtil.CopyFile(finalImgPath,Config.CACHE_PATH_IMAGE+ finalFileName)==0){
                            ToastUtil.ToastMessage(AppContext.getAppContext(),Config.CACHE_PATH_IMAGE+ finalFileName);
                        }
                    }
                });
                return true;
            }
        });
        return view;
    }

    private void preview(final String imgurl, final PhotoView iv_preview, final TextView rotateButton, final RoundProgressBar progressBar){
        final String fileName = imgurl.substring(imgurl.lastIndexOf("/") + 1);
        if (!com.y2w.uikit.utils.FileUtil.checkFilePathExists(Config.DEFAULT_PATH_FILE + fileName)) {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            File file = new File(Config.DEFAULT_PATH_FILE);
            if (!file.exists()) {
                file.mkdirs();
            }
            AsyncMultiPartGet get = new AsyncMultiPartGet("", imgurl, Config.DEFAULT_PATH_FILE, fileName);
            get.execute();
            get.setCallBack(new AsyncMultiPartGet.CallBack() {
                @Override
                public void update(Integer i) {
                    progressBar.setProgress(i);
                }
            });
            get.setCallBackMsg(new AsyncMultiPartGet.CallBackMsg() {
                @Override
                public void msg(String result) {
                    progressBar.setVisibility(View.GONE);
                    ImagePool.getInstance(AppContext.getAppContext()).load("file://"+Config.DEFAULT_PATH_FILE + fileName,null, iv_preview, 0);
                }
            });

        }else {
            ImagePool.getInstance(AppContext.getAppContext()).load("file://"+Config.DEFAULT_PATH_FILE + fileName,null, iv_preview, 0);
        }
        iv_preview.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                activity.finish();
            }
        });

        rotateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Matrix matrix = new Matrix();
                degree += 90;
                degree = degree % 360;
                matrix.postRotate(degree);
                if (bitmap == null) {
                    bitmap = iv_preview.getDrawingCache();
                }
                if (bitmap != null) {
                    Bitmap bm = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                            bitmap.getHeight(), matrix, true);
                    iv_preview.setImageBitmap(bm);
                }
            }
        });
    }

}
