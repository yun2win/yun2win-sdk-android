package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.yun2win.demo.R;

import y2w.base.AppData;
import y2w.base.Urls;
import y2w.common.CallBackUpdate;
import y2w.common.HeadImageView;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.Contact;
import y2w.model.Session;
import y2w.model.User;
import y2w.service.Back;

/**
 * Created by hejie on 2016/3/14.
 * 个人名片界面
 */
public class ContactInfoActivity extends Activity{
    private Context context;
    private String _otherId = "";//联系人ID
    private String _otheruserid ="";//用户ID
    private String avatarUrl ="";
    private String username ="";
    private String account ="";
    private Session _session;
    private TextView tv_head;
    private HeadImageView img_head;
    private TextView tv_username;
    private TextView tv_account;
    private Button bt_chat,bt_delete;
    private boolean isfriend =false;
    private int flag;
    public final static int chat = 1;
    Handler updatefriendHandler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if(msg.what==1) {//更新
                Contact contact = Users.getInstance().getCurrentUser().getContacts().getContact(_otheruserid);
                if(contact.getEntity()!=null){
                    if(contact.getEntity().getUserId().equals(_otheruserid)&&!contact.getEntity().isDelete()) {
                        isfriend = true;
                        _otherId =contact.getEntity().getId();
                    }
                }
                if (isfriend) {
                    bt_delete.setText(getResources().getString(R.string.delete_friend));
                } else {
                    bt_delete.setText(getResources().getString(R.string.add_friend));
                }
            }else if(msg.what ==2){
                if (isfriend) {
                    bt_delete.setText(getResources().getString(R.string.delete_friend));
                } else {
                    bt_delete.setText(getResources().getString(R.string.add_friend));
                }
            }else if(msg.what==0){
                tv_username.setText(username);
                tv_account.setText("账号:" + account);
                img_head.loadBuddyAvatarbyurl(avatarUrl , R.drawable.default_person_icon);
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactinfo);
        context = this;
        getExtras(this.getIntent().getExtras());
        getHttp();
        getSessionP2p();
        initActionBar();
        initUi();
        uiEvent();
        updatefriendHandler.sendEmptyMessage(1);
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
        texttitle.setText("个人名片");

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
    private void getExtras(Bundle bundle){
        if(bundle == null)
            return;
        _otheruserid = bundle.getString("otheruserid");
        avatarUrl = bundle.getString("avatarUrl");
        username= bundle.getString("username");
        account =bundle.getString("account");
        flag = bundle.getInt("flag");
    }
   private void getHttp(){
       final ProgressDialog pd = new ProgressDialog(ContactInfoActivity.this);
       pd.setCanceledOnTouchOutside(false);
       pd.setMessage(getString(R.string.loading));
       pd.show();
       Users.getInstance().getRemote().userGet(_otheruserid, new Back.Result<User>() {
           @Override
           public void onSuccess(User user) {
               account = user.getEntity().getAccount();
               username = user.getEntity().getName();
               avatarUrl =user.getEntity().getAvatarUrl();
               updatefriendHandler.sendEmptyMessage(0);
               pd.dismiss();
           }

           @Override
           public void onError(int Code, String error) {
               pd.dismiss();
           }
       });

   }
   private void initUi(){
       tv_head = (TextView) findViewById(R.id.tv_contact_header);
       tv_username = (TextView) findViewById(R.id.head_title_label);
       tv_account = (TextView) findViewById(R.id.head_detail_label);
       img_head = (HeadImageView) findViewById(R.id.head_image);
       tv_username.setText(username);
       if(!StringUtil.isEmpty(account)) {
           tv_account.setText("账号:" + account);
       }
       img_head.loadBuddyAvatarbyurl(avatarUrl , R.drawable.default_person_icon);

       tv_head.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(_otheruserid)));
       bt_chat = (Button) findViewById(R.id.bt_contactInfo_chat);
       bt_delete = (Button) findViewById(R.id.bt_contactInfo_delete);
       if(flag == 1){
           bt_delete.setVisibility(View.GONE);
       }

       if (_otheruserid.equals(Users.getInstance().getCurrentUser().getEntity().getId())){
           bt_chat.setVisibility(View.GONE);
           bt_delete.setVisibility(View.GONE);
       }

   }
    private void uiEvent(){
        tv_head.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent headSculpture1 = new Intent(context,
                        HeadSculptureActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("type", EnumManage.SessionType.p2p.toString());
                bundle1.putString("mode", "view");
                bundle1.putString("userId", _otheruserid);
                headSculpture1.putExtras(bundle1);
                startActivity(headSculpture1);
            }
        });

        bt_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (_session == null) {
                    Toast.makeText(context, "正在初始化数据", Toast.LENGTH_SHORT);
                    return;
                }
                Intent intent = new Intent(context, ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("sessionid", _session.getEntity().getId());
                bundle.putString("sessiontype", _session.getEntity().getType());
                bundle.putString("otheruserId",_session.getEntity().getOtherSideId());
                bundle.putString("name", username);
                intent.putExtras(bundle);
                startActivity(intent);
                finish();
            }
        });
        bt_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog pd = new ProgressDialog(ContactInfoActivity.this);
                pd.setCanceledOnTouchOutside(false);
                pd.setMessage(getString(R.string.operationing));
                if (isfriend) {
                    Users.getInstance().getCurrentUser().getContacts().getRemote().contactDelete(_otherId, new Back.Callback() {
                        @Override
                        public void onSuccess() {
                            ToastUtil.ToastMessage(context, "删除联系人成功");
                            AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.contact.toString()).syncDate();
                            pd.dismiss();
                            finish();
                        }

                        @Override
                        public void onError(int errorCode,String error) {
                            pd.dismiss();
                            ToastUtil.ToastMessage(context, "删除失败");
                        }
                    });
                } else {
                    Users.getInstance().getCurrentUser().getContacts().getRemote().contactAdd(_otheruserid, account, username, avatarUrl, new Back.Result<Contact>() {
                        @Override
                        public void onSuccess(Contact contact) {
                            ToastUtil.ToastMessage(context, "添加联系人成功");
                            pd.dismiss();
                            _otherId = contact.getEntity().getId();
                            AppData.getInstance().getUpdateHashMap().get(CallBackUpdate.updateType.contact.toString()).syncDate();
                            isfriend = true;
                            updatefriendHandler.sendEmptyMessage(2);
                            if (_session == null) {
                                Toast.makeText(context, "正在初始化数据", Toast.LENGTH_SHORT);
                                return;
                            }
                            Intent intent = new Intent(context, ChatActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putString("sessionid", _session.getEntity().getId());
                            bundle.putString("sessiontype", _session.getEntity().getType());
                            bundle.putString("otheruserId",_session.getEntity().getOtherSideId());
                            bundle.putString("name", username);
                            intent.putExtras(bundle);
                            startActivity(intent);
                            finish();
                        }
                        @Override
                        public void onError(int errorCode, String error) {
                            pd.dismiss();
                            ToastUtil.ToastMessage(context, "添加失败");
                        }
                    });
                }
            }
        });
    }
    private void getSessionP2p(){
        Users.getInstance().getCurrentUser().getSessions().getSessionByTargetId(_otheruserid, EnumManage.SessionType.p2p.toString(), new Back.Result<Session>() {
            @Override
            public void onSuccess(Session session) {
                _session = session;
            }

            @Override
            public void onError(int errorCode,String error) {

            }
        });
    }
}
