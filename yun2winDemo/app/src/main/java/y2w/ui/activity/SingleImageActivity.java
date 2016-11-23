package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
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
 * Created by maa46 on 2016/9/20.
 */
public class SingleImageActivity extends Activity{
    RelativeLayout rl_preview;
    PhotoView iv_preview;
    TextView rotateButton;
    RoundProgressBar progressBar;
    private int degree = 0;
    private Bitmap bitmap =null;
    String imgurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_browse_pager_item);
        initActionBar();
         imgurl = getIntent().getStringExtra("imgurl");
        rl_preview = (RelativeLayout) findViewById(R.id.rl_preview);
        iv_preview = (PhotoView) findViewById(R.id.iv_preview);
        rotateButton = (TextView) findViewById(R.id.message_image_rotate);
        progressBar = (RoundProgressBar) findViewById(R.id.rb_image);
        preview(imgurl,iv_preview,rotateButton,progressBar);
        rl_preview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              //  finish();
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
                Y2wDialog dialog = new Y2wDialog(SingleImageActivity.this);
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
                    finish();
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
    /*
***自定义aciontbar
*/
    private void initActionBar(){
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setCustomView(R.layout.actionbar_chat);
        TextView texttitle = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
        texttitle.setText("图片展示");

        ImageButton imageButtonclose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
        ImageButton imageButtonright = (ImageButton) actionbar.getCustomView().findViewById(R.id.right_add);
        ImageButton imageButtonSearch = (ImageButton) actionbar.getCustomView().findViewById(R.id.right_other_third);
        imageButtonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageButtonright.setVisibility(View.GONE);
        imageButtonSearch.setVisibility(View.GONE);
    }
}
