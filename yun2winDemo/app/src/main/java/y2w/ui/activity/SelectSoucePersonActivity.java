package y2w.ui.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.listview.ListViewUtil;
import com.y2w.uikit.utils.pinyinutils.SortModel;
import com.yun2win.demo.R;

import java.util.ArrayList;
import java.util.List;

import y2w.manage.EnumManage;
import y2w.ui.adapter.SessionStartAdapter;
import y2w.ui.adapter.SoucePersonAdapter;

/**
 * Created by hejie on 2016/3/14.
 * 选择联系人
 */
public class SelectSoucePersonActivity extends Activity{

    private ListView lv_souceperson;
    private SoucePersonAdapter soucePersonAdapter;
    private ArrayList<SortModel> SourceDataList,currenSourceDataList;

    private Context context;
    private ArrayList<SortModel> choicePersons = new ArrayList<SortModel>();
    private boolean isavatar= false;
    private boolean selectFolder =false;
    private String select_mode;
    private String userIds[];
    private String title;
    private LinearLayout layout_person_folder;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_souceperson);
        isavatar = getIntent().getExtras().getBoolean("avatar");
        selectFolder= getIntent().getExtras().getBoolean("selectFolder");
        select_mode = getIntent().getExtras().getString("mode");
        title = getIntent().getExtras().getString("title");
        String users = getIntent().getExtras().getString("userIds");
        userIds = users.split(";");
        SourceDataList = (ArrayList<SortModel>) getIntent().getExtras().getSerializable("dataSource");
        choicePersons= (ArrayList<SortModel>) getIntent().getExtras().getSerializable("selectdataSource");
        if(SourceDataList==null){
            SourceDataList = new ArrayList<SortModel>();
        }
        if(choicePersons==null){
            choicePersons = new ArrayList<SortModel>();
        }
        if (EnumManage.Select_Mode.single.toString().equals(select_mode)) {
            if(choicePersons!=null)
                choicePersons.clear();
        }
        context = this;
        soucePersonInit();
        initActionBar();
    }
    /*
	***自定义aciontbar
	*/
    TextView textRight;
    private void initActionBar(){
        ActionBar actionbar = getActionBar();
        actionbar.setDisplayShowTitleEnabled(false);
        actionbar.setDisplayShowHomeEnabled(false);
        actionbar.setDisplayShowCustomEnabled(true);
        actionbar.setCustomView(R.layout.actionbar_chat);
        TextView texttitle = (TextView) actionbar.getCustomView().findViewById(R.id.text_title);
        texttitle.setText(title);
        ImageButton imageButtonclose = (ImageButton) actionbar.getCustomView().findViewById(R.id.left_close);
        ImageButton imageButtonright = (ImageButton) actionbar.getCustomView().findViewById(R.id.right_add);
        imageButtonright.setVisibility(View.GONE);
        textRight = (TextView) actionbar.getCustomView().findViewById(R.id.tv_right_oper);
        textRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("choiceperson",choicePersons);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        imageButtonclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    public void soucePersonInit(){
        layout_person_folder = (LinearLayout) findViewById(R.id.layout_person_folder);
        lv_souceperson = (ListView) findViewById(R.id.lv_contact);
        soucePersonAdapter = new SoucePersonAdapter(this,context);
        soucePersonAdapter.setIsavatar(isavatar);
        if(EnumManage.Select_Mode.single.toString().equals(select_mode)) {
            soucePersonAdapter.setShowselect(false);
        }
        soucePersonAdapter.setSelect_mode(select_mode);
        lv_souceperson.setAdapter(soucePersonAdapter);
        SortModel allSortModel = new SortModel();
        allSortModel.setName("全部");
        allSortModel.setId("00");
        allSortModel.setHightSortModel(null);
        allSortModel.setChildrenPerson(SourceDataList);
        setcurrendate(SourceDataList,allSortModel);
    }

     public void choicePerson(int position){
         if(currenSourceDataList!=null&&currenSourceDataList.size()>position) {
             SortModel model = currenSourceDataList.get(position);
             if (textRight.getVisibility() == View.GONE) {
                 textRight.setVisibility(View.VISIBLE);
             }
             if (EnumManage.Select_Mode.single.toString().equals(select_mode)) {
                 if (model.isChoice()) {
                     model.setIsChoice(false);
                     for(int x =0;x<choicePersons.size();x++){
                         if(choicePersons.get(x).getId().equals(model.getId())){
                             choicePersons.remove(choicePersons.get(x));
                             break;
                         }
                     }
                 } else {
                     if (choicePersons.size() > 0) {
                         for (int i = 0; i < currenSourceDataList.size(); i++) {
                             if (currenSourceDataList.get(i).getId().equals(choicePersons.get(0).getId())) {
                                 currenSourceDataList.get(i).setIsChoice(false);
                                 for(int x =0;x<choicePersons.size();x++){
                                     if(choicePersons.get(x).getId().equals(currenSourceDataList.get(i).getId())){
                                         choicePersons.remove(choicePersons.get(x));
                                         break;
                                     }
                                 }
                                 refreshViewHolderByIndex(i, currenSourceDataList.get(i));
                                 break;
                             }
                         }
                     }
                     model.setIsChoice(true);

                     if(choicePersons.size()>0){
                         boolean find = false;
                         for(int x =0;x<choicePersons.size();x++){
                             if(choicePersons.get(x).getId().equals(model.getId())){
                                 find = true;
                                 break;
                             }
                         }
                         if(!find) {
                             choicePersons.add(model);
                         }
                     }else{
                         choicePersons.add(model);
                     }
                     refreshViewHolderByIndex(position, model);
                 }

                 textRight.setText("确认");
             } else {
                 if (model.isChoice()) {
                     model.setIsChoice(false);
                     for(int x =0;x<choicePersons.size();x++){
                         if(choicePersons.get(x).getId().equals(model.getId())){
                             choicePersons.remove(choicePersons.get(x));
                             break;
                         }
                     }

                 } else {
                     model.setIsChoice(true);
                     if(choicePersons.size()>0){
                         boolean find = false;
                         for(int x =0;x<choicePersons.size();x++){
                             if(choicePersons.get(x).getId().equals(model.getId())){
                                 find = true;
                                 break;
                             }
                         }
                         if(!find) {
                             choicePersons.add(model);
                         }
                     }else{
                         choicePersons.add(model);
                     }
                 }
                 textRight.setText("确认(" + choicePersons.size() + ")");
                 refreshViewHolderByIndex(position, model);
             }
         }
     }
    public void gotochildren(int position){
        if(currenSourceDataList!=null&&currenSourceDataList.size()>position) {
            SortModel model = currenSourceDataList.get(position);
             if(model.getChildrenPerson()!=null&&model.getChildrenPerson().size()>0){
                 setcurrendate(model.getChildrenPerson(),model);
             }
        }
    }
     private void setcurrendate(ArrayList<SortModel> list,SortModel goto_model){
         currenSourceDataList = list;
         if(choicePersons.size()>0){
             for(int i =0;i<currenSourceDataList.size();i++){
                 boolean find = false;
                 for(int j =0;j<choicePersons.size();j++){
                     if(choicePersons.get(j).getId().equals(currenSourceDataList.get(i).getId())){
                         currenSourceDataList.get(i).setIsChoice(true);
                         find= true;
                         break;
                     }
                 }
                 if(!find){
                     currenSourceDataList.get(i).setIsChoice(false);
                 }
             }
         }

         changeTileView(goto_model);
         soucePersonAdapter.setListView(currenSourceDataList);
     }
    /**
     * 刷新单条消息
     *
     * @param index
     */
    private void refreshViewHolderByIndex(final int index,final SortModel model) {
        this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (index < 0) {
                    return;
                }
                Object tag = ListViewUtil.getViewHolderByIndex(lv_souceperson, index);
                if (tag instanceof SoucePersonAdapter.SouceHoldView) {
                    SoucePersonAdapter.SouceHoldView viewHolder = (SoucePersonAdapter.SouceHoldView) tag;
                    soucePersonAdapter.setIndexview(viewHolder, model, index);
                }
            }
        });
    }
    /**
     * 单选时确认是否选择
     *
     */
    public void singleChosePerson(final SortModel model){
        if(model==null)
            return;
        new AlertDialog.Builder(context).setTitle("确认选择：").setMessage(model.getName()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                choicePersons.clear();
                choicePersons.add(model);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("choiceperson",choicePersons);
                intent.putExtras(bundle);
                setResult(RESULT_OK,intent);
                finish();

            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }
    /**
     * 头部目录导航标题
     * @param item
     * @return
     */
    private SortModel current_item;
    private List<View> viewList = new ArrayList<View>();// 头部目录导航
    private void changeTileView(final SortModel item) {
        if (current_item==null || !current_item.equals(item)) {
            TextView view=(TextView) LayoutInflater.from(this).inflate(R.layout.item_souce_title, null);
            view.setText(item.getName());
            view.setTag(item);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (viewList.lastIndexOf(v) == viewList.size() - 1) {
                        return;
                    }
                    List<View> tmpList = new ArrayList<View>();
                    int index = viewList.indexOf(v);
                    for (int i = 0; i <= index; i++) {//把这个标题和他的父目录标题加入临时列表
                        tmpList.add(viewList.get(i));
                    }
                    layout_person_folder.removeAllViews();//移除所有标题
                    for (View view : tmpList) {//把这个标题和他的父目录标题加入加入目录栏
                        layout_person_folder.addView(view);
                    }
                    viewList = tmpList;
                    SortModel item = (SortModel)viewList.get(viewList.size()-1).getTag();
                    currenSourceDataList = item.getChildrenPerson();
                    if(choicePersons.size()>0){
                        for(int i =0;i<currenSourceDataList.size();i++){
                            boolean find = false;
                            for(int j =0;j<choicePersons.size();j++){
                                if(choicePersons.get(j).getId().equals(currenSourceDataList.get(i).getId())){
                                    currenSourceDataList.get(i).setIsChoice(true);
                                    find= true;
                                    break;
                                }
                            }
                            if(!find){
                                currenSourceDataList.get(i).setIsChoice(false);
                            }
                        }
                    }
                    soucePersonAdapter.setListView(currenSourceDataList);
                    current_item = item;
                }
            });
            viewList.add(view);
            layout_person_folder.addView(view);
        }
        current_item = item;
    }

    @Override
    public void onBackPressed() {
        if(viewList.size()>1){
            layout_person_folder.removeViewAt(viewList.size()-1);
            viewList.remove(viewList.size()-1);
            SortModel item = (SortModel)viewList.get(viewList.size()-1).getTag();
            currenSourceDataList = item.getChildrenPerson();
            if(choicePersons.size()>0){
                for(int i =0;i<currenSourceDataList.size();i++){
                    boolean find = false;
                    for(int j =0;j<choicePersons.size();j++){
                        if(choicePersons.get(j).getId().equals(currenSourceDataList.get(i).getId())){
                            currenSourceDataList.get(i).setIsChoice(true);
                            find= true;
                            break;
                        }
                    }
                    if(!find){
                        currenSourceDataList.get(i).setIsChoice(false);
                    }
                }
            }
            soucePersonAdapter.setListView(currenSourceDataList);
            current_item = item;
        }else{
            finish();
        }
    }
}
