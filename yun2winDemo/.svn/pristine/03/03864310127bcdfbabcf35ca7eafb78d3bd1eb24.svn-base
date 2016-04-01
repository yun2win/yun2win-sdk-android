package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import java.util.List;

import y2w.manage.Users;
import y2w.entities.ContactEntity;
import y2w.service.Back;
import y2w.service.ErrorCode;
import y2w.ui.adapter.ContactSearchAdapter;

/**
 * Created by hejie on 2016/3/14.
 * 添加联系人界面
 */
public class AddContactActivity extends Activity{

    private EditText searchText;
    private Context context;
    private ListView lv_contacts;
    private ImageView searchImage;
    private List<ContactEntity> contactEntities;
    private ContactSearchAdapter contactSearchAdapter;
    Handler updatelistHandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1){//更新
                contactEntities = (List<ContactEntity>) msg.obj;
                contactSearchAdapter.updateListView(contactEntities);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_contact);
        context = this;
        initUi();
        clickEvent();
        initActionBar();
    }
   private void initUi(){
       lv_contacts = (ListView) findViewById(R.id.lv_contacts_search);
       searchText = (EditText) findViewById(R.id.et_contact_search);
       searchImage = (ImageView) findViewById(R.id.iv_add_contactor_search);
       contactSearchAdapter = new ContactSearchAdapter(context);
       lv_contacts.setAdapter(contactSearchAdapter);
   }
    private void clickEvent(){
        //选择键盘搜索按钮
        searchText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                try {
                    if (keyCode == KeyEvent.KEYCODE_ENTER
                            && event.getAction() == event.ACTION_UP) {
                        String keywordTemp = searchText.getText().toString()
                                .trim();
                        if (StringUtil.isEmpty(keywordTemp)) {
                            return true;
                        }

                        // 先隐藏键盘
                        ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                                .hideSoftInputFromWindow(
                                        AddContactActivity.this
                                                .getCurrentFocus()
                                                .getWindowToken(),
                                        InputMethodManager.HIDE_NOT_ALWAYS);
                        // 开始搜索
                        contactSearch(keywordTemp);
                    }
                } catch (Exception e) {

                }
                return false;
            }
        });
        //选择界面搜索按钮
        searchImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String keywordTemp = searchText.getText().toString()
                        .trim();
                if (StringUtil.isEmpty(keywordTemp)) {
                    return;
                }
                // 先隐藏键盘
                ((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(
                                AddContactActivity.this
                                        .getCurrentFocus()
                                        .getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                // 开始搜索
                contactSearch(keywordTemp);
            }
        });
        //listview点击
        lv_contacts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ContactEntity entity = contactEntities.get(position);
                Intent intent = new Intent(context,ContactInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("otheruserid",entity.getId());
                bundle.putString("avatarUrl",entity.getAvatarUrl());
                bundle.putString("username",entity.getName());
                bundle.putString("account",entity.getEmail());
                intent.putExtras(bundle);
                startActivity(intent);
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
        texttitle.setText("添加好友");

        ImageButton imageButtonclose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
        ImageButton imageButtonright = (ImageButton) actionbar.getCustomView().findViewById(R.id.right_add);
        imageButtonright.setVisibility(View.GONE);
        imageButtonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void contactSearch(String keyword){
        final ProgressDialog pd = new ProgressDialog(AddContactActivity.this);
        pd.setCanceledOnTouchOutside(false);
        pd.setMessage(getString(R.string.searching));
        Users.getInstance().getRemote().search(keyword, new Back.Result<List<ContactEntity>>() {
            @Override
            public void onSuccess(List<ContactEntity> entities) {
                pd.dismiss();
                if (entities==null||entities.size()<=0) {
                    ToastUtil.ToastMessage(AddContactActivity.this, "抱歉,没有查询到任何用户");
                    return;
                }
                Message msg = new Message();
                msg.what = 1;
                msg.obj = entities;
                updatelistHandler.sendMessage(msg);
            }

            @Override
            public void onError(int errorCode, String error) {
                pd.dismiss();
                ToastUtil.ToastMessage(AddContactActivity.this, "抱歉,没有查询到任何用户");
            }
        });
        pd.show();
       //TODO
    }

}
