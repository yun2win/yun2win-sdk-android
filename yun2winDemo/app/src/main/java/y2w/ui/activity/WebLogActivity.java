package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.view.SwitchButton;
import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.NetUtil;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;
import com.yun2win.imlib.IMClient;
import com.yun2win.imlib.IMSession;

import java.util.ArrayList;
import java.util.List;

import y2w.base.AppData;
import y2w.base.Urls;
import y2w.common.HeadImageView;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Session;
import y2w.model.SessionMember;
import y2w.model.UserSession;
import y2w.service.Back;
import y2w.ui.adapter.GroupInfoAdapter;

/**
 * Created by hejie on 2016/3/14.
 * 个人名片界面
 */
public class WebLogActivity extends Activity{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weblog);
        initActionBar();
       TextView textview = (TextView) findViewById(R.id.textview);
        textview.setText(AppData.getInstance().getLogDate());
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
        texttitle.setText("web日志");

        ImageButton imageButtonclose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
        ImageButton imageButtonright = (ImageButton) actionbar.getCustomView().findViewById(R.id.right_add);
        imageButtonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageButtonright.setVisibility(View.GONE);
    }
}
