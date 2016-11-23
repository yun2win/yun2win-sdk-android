package y2w.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.ToastUtil;
import com.y2w.uikit.utils.pinyinutils.SortModel;
import com.yun2win.demo.R;

import java.util.List;

import y2w.base.AppContext;
import y2w.base.Urls;
import y2w.common.HeadImageView;
import y2w.manage.EnumManage;
import y2w.ui.activity.SelectSoucePersonActivity;

/**
 * Created by maa2 on 2016/2/19.
 */
public class SoucePersonAdapter extends BaseAdapter {

    private List<SortModel> sortModels;
    private Context context;
    private boolean isavatar = true;
    private SelectSoucePersonActivity selectSoucePersonActivity;
    private boolean showselect = true;
    private String select_mode;
    private boolean selectFolder = false;

    public SoucePersonAdapter(SelectSoucePersonActivity selectSoucePersonActivity, Context context){
        this.context = context;
        this.selectSoucePersonActivity = selectSoucePersonActivity;
    }

    public boolean isavatar() {
        return isavatar;
    }

    public void setIsavatar(boolean isavatar) {
        this.isavatar = isavatar;
    }
    public boolean isShowselect() {
        return showselect;
    }
    public void setShowselect(boolean showselect) {
        this.showselect = showselect;
    }

    public String getSelect_mode() {
        return select_mode;
    }

    public void setSelect_mode(String select_mode) {
        this.select_mode = select_mode;
    }

    public boolean isSelectFolder() {
        return selectFolder;
    }

    public void setSelectFolder(boolean selectFolder) {
        this.selectFolder = selectFolder;
    }

    public void setListView(List<SortModel> list) {
        this.sortModels = list;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return sortModels == null ? 0 :sortModels.size();
    }

    @Override
    public Object getItem(int position) {
        return sortModels.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        if(position > sortModels.size() - 1){
            return view;
        }
        final SortModel model = sortModels.get(position);
        SouceHoldView holdView;
        if (null == view) {
            holdView = new SouceHoldView();
            view = LayoutInflater.from(context).inflate(R.layout.selectperson_list_item, null);
            holdView.img_header = (HeadImageView) view
                    .findViewById(R.id.img_contact_header);
            holdView.tv_name = (TextView) view
                    .findViewById(R.id.tv_contact_name);
            holdView.tv_header = (TextView) view
                    .findViewById(R.id.tv_contact_header);
            holdView.tv_chart = (TextView) view
                    .findViewById(R.id.textchart);
            holdView.view_line=view.findViewById(R.id.viewline);
            holdView.iv_select = (ImageView) view.findViewById(R.id.iv_contact_select);
            holdView.selectchildren= (LinearLayout) view.findViewById(R.id.selectchildren);
            holdView.imgarrow = (ImageView) view.findViewById(R.id.imgarrow);
            holdView.relativeLayouthead = (RelativeLayout) view.findViewById(R.id.relativeLayout11);

            view.setTag(holdView);
        } else {
            holdView = (SouceHoldView) view.getTag();
        }

        holdView.iv_select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectSoucePersonActivity.choicePerson(position);
            }
        });
        if(EnumManage.Select_Mode.single.toString().equals(select_mode)) {
            holdView.selectchildren.setEnabled(true);
            if (model.getChildrenPerson() != null && model.getChildrenPerson().size() > 0) {

                holdView.imgarrow.setVisibility(View.VISIBLE);
                holdView.selectchildren.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                            selectSoucePersonActivity.gotochildren(position);
                    }
                });
            } else {
                holdView.imgarrow.setVisibility(View.GONE);
                holdView.selectchildren.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectSoucePersonActivity.singleChosePerson(model);
                    }
                });
            }
        }else {
            if (model.getChildrenPerson() != null && model.getChildrenPerson().size() > 0) {
                holdView.selectchildren.setEnabled(true);
                holdView.imgarrow.setVisibility(View.VISIBLE);
                holdView.selectchildren.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (model.isChoice()) {
                            ToastUtil.ToastMessage(AppContext.getAppContext(), "已选择部门下的所有成员");
                        } else {
                            selectSoucePersonActivity.gotochildren(position);
                        }
                    }
                });
            } else {
                holdView.selectchildren.setEnabled(false);
                holdView.imgarrow.setVisibility(View.GONE);
            }
        }
        holdView.tv_name.setText(model.getName());
        if(isavatar) {
            holdView.img_header.loadBuddyAvatarbyurl(model.getAvatarUrl(), R.drawable.default_person_icon);
            holdView.tv_header.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(model.getId())));
        }else{
            holdView.relativeLayouthead.setVisibility(View.GONE);
        }
        holdView.tv_chart.setVisibility(View.GONE);
        holdView.view_line.setVisibility(View.VISIBLE);
        setIndexview(holdView,model,position);
        if(!showselect){
            holdView.iv_select.setVisibility(View.GONE);
        }else{
            if (model.getChildrenPerson() != null && model.getChildrenPerson().size() > 0) {
                if(selectFolder){
                    holdView.iv_select.setVisibility(View.VISIBLE);
                    holdView.iv_select.setEnabled(true);
                }else{
                    holdView.iv_select.setVisibility(View.INVISIBLE);
                    holdView.iv_select.setEnabled(false);
                }
            }else{
                holdView.iv_select.setVisibility(View.VISIBLE);
                holdView.iv_select.setEnabled(true);
            }
        }
        return view;
    }
    public void setIndexview(SouceHoldView holdView, SortModel model, final int position){
       if(model.isChoice()){
           holdView.iv_select.setImageResource(R.drawable.checked);
       }else{
           holdView.iv_select.setImageResource(R.drawable.unchecked);
       }
    }

  public class SouceHoldView {
        public HeadImageView img_header;
        public TextView tv_name;
        public TextView tv_header;
        public TextView tv_chart;
        public View view_line;
        public ImageView iv_select;
        public RelativeLayout relativeLayouthead;
        public LinearLayout selectchildren;
        public ImageView imgarrow;
    }
}

