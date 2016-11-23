package y2w.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.view.SwitchButton;
import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.StringUtil;
import com.yun2win.demo.R;

import java.util.List;

import y2w.base.AppContext;
import y2w.base.Urls;
import y2w.common.HeadImageView;
import y2w.common.ImagePool;
import y2w.manage.EnumManage;
import y2w.manage.Users;
import y2w.model.SettingTemplate;
import y2w.model.SettingType;
import y2w.ui.activity.HeadSculptureActivity;
import y2w.ui.activity.MainActivity;
import y2w.ui.activity.PersonalInfoModifyActivity;

/**
 * Created by maa2 on 2016/1/22.
 */
public class SettingsAdapter extends BaseAdapter{

    protected List<SettingTemplate> items;
    protected Activity activity;
    protected Context context;
    private int layoutID;
    protected int itemHeight;
    private SwitchButton.OnChangedListener onchangeListener;
    public SettingsAdapter(Activity activity,Context context,List<SettingTemplate> items) {
        this(activity,context, items,R.layout.setting_item_base);
    }

    public SettingsAdapter(Activity activity,Context context,List<SettingTemplate> items, int layoutID) {
        this.activity = activity;
        this.context = context;
        this.items = items;
        this.layoutID = layoutID;
        itemHeight = context.getResources().getDimensionPixelSize(R.dimen.isetting_item_height);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(layoutID, parent, false);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(viewHolder == null) {
            viewHolder = new ViewHolder();
            viewHolder.root = convertView;
            viewHolder.headImageView = (HeadImageView) convertView.findViewById(R.id.head_image);
            viewHolder.headTextView = (TextView) convertView.findViewById(R.id.tv_contact_header);
            viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.relativeLayout11);
            viewHolder.titleView = (TextView)  convertView.findViewById(R.id.title_label);
            viewHolder.detailView = (TextView) convertView.findViewById(R.id.detail_label);
            viewHolder.switchButton = (SwitchButton) convertView.findViewById(R.id.setting_item_toggle);
            viewHolder.line = convertView.findViewById(R.id.line);
            viewHolder.indicator = (ImageView) convertView.findViewById(R.id.setting_item_indicator);
            viewHolder.headTitleView = (TextView) convertView.findViewById(R.id.head_title_label);
            viewHolder.headDetailView = (TextView) convertView.findViewById(R.id.head_detail_label);
            convertView.setTag(viewHolder);
        }

        SettingTemplate item = items.get(position);
        if(item.getType() == SettingType.TYPE_TOGGLE) {
            updateToggleItem(viewHolder, item, position);
        } else if(item.getType() == SettingType.TYPE_HEAD) {
            updateHeadItem(viewHolder);
        } else if(item.getType() == SettingType.TYPE_SEPERATOR) {
            updateSeperatorItem(viewHolder);
        } else if(item.getType() == SettingType.TYPE_LINE) {
            addLineItem(viewHolder);
        }else if (item.getType() == SettingType.TYPE_NEWUI){
            updateFeatherItem(viewHolder, item, position);
        }else {
            updateDefaultItem(viewHolder, item, position);
        }

        return convertView;
    }

    /**
     * 设置默认格式item
     * @param viewHolder
     * @param item
     * @param position
     */
    private void updateDefaultItem(ViewHolder viewHolder, SettingTemplate item, int position) {

        viewHolder.relativeLayout.setVisibility(View.GONE);
        setTextView(viewHolder.titleView, item.getTitle());
        setTextView(viewHolder.detailView, item.getDetail());
    }

    /**
     * 设置右边有箭头item
     * @param viewHolder
     * @param item
     * @param position
     */
    private void updateFeatherItem(ViewHolder viewHolder, SettingTemplate item, int position) {

        viewHolder.relativeLayout.setVisibility(View.GONE);
        setTextView(viewHolder.titleView, item.getTitle());
        setTextView(viewHolder.detailView, item.getDetail());
        viewHolder.indicator.setImageResource(R.drawable.nim_arrow_right);
        viewHolder.indicator.setVisibility(View.VISIBLE);
    }
    /**
     * 设置带toggle button item
     * @param viewHolder
     * @param item
     * @param position
     */
    private void updateToggleItem(ViewHolder viewHolder, SettingTemplate item, int position) {
        setTextView(viewHolder.titleView, item.getTitle());
        setToggleView(viewHolder, item);
    }

    /**
     * 设置头像和名字item
     * @param viewHolder
     */
    private void updateHeadItem(ViewHolder viewHolder) {

        viewHolder.relativeLayout.setVisibility(View.VISIBLE);
        viewHolder.headTitleView.setVisibility(View.VISIBLE);
        viewHolder.headTitleView.setText(Users.getInstance().getCurrentUser().getEntity().getName());
        viewHolder.headDetailView.setVisibility(View.VISIBLE);
        viewHolder.headDetailView.setText(String.format("帐号:%s", Users.getInstance().getCurrentUser().getEntity().getAccount()));
        viewHolder.titleView.setVisibility(View.GONE);

        viewHolder.headImageView.loadBuddyAvatarbyurl(Users.getInstance().getCurrentUser().getEntity().getAvatarUrl(), R.drawable.default_person_icon);
        viewHolder.headTextView.setBackgroundResource(HeadTextBgProvider.getTextBg(StringUtil.parseAscii(Users.getInstance().getCurrentUser().getEntity().getId())));
        viewHolder.indicator.setImageResource(R.drawable.nim_arrow_right);
        viewHolder.indicator.setVisibility(View.VISIBLE);
        viewHolder.headImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent headSculpture1 = new Intent(context,
                        HeadSculptureActivity.class);
                Bundle bundle1 = new Bundle();
                bundle1.putString("type", EnumManage.SessionType.p2p.toString());
                headSculpture1.putExtras(bundle1);
                activity.startActivityForResult(headSculpture1, MainActivity.MainResultCode.CODE_PERSON_INFO_CHANGE);

            }
        });
    }

    /**
     * 设置空的item
     */
    private void updateSeperatorItem(ViewHolder viewHolder) {
        ViewGroup.LayoutParams lp = viewHolder.root.getLayoutParams();
        if(lp != null) {
            lp.height = dip2px(context, 20);
            viewHolder.root.setLayoutParams(lp);
            viewHolder.root.setBackgroundColor(Color.TRANSPARENT);
        }
        viewHolder.relativeLayout.setVisibility(View.GONE);
        viewHolder.titleView.setVisibility(View.GONE);
        viewHolder.detailView.setVisibility(View.GONE);
        viewHolder.switchButton.setVisibility(View.GONE);
    }

    /**
     * 添加分割线
     * @param viewHolder
     */
    private void addLineItem(ViewHolder viewHolder) {
        ViewGroup.LayoutParams lp = viewHolder.root.getLayoutParams();
        if(lp != null) {
           lp.height = dip2px(context, 1);
            viewHolder.root.setLayoutParams(lp);
        }
        viewHolder.relativeLayout.setVisibility(View.GONE);
        viewHolder.titleView.setVisibility(View.GONE);
        viewHolder.detailView.setVisibility(View.GONE);
        viewHolder.switchButton.setVisibility(View.GONE);
        viewHolder.line.setVisibility(View.VISIBLE);
    }

    private void setToggleView(ViewHolder viewHolder, SettingTemplate item) {
        if(viewHolder.switchButton != null) {
            viewHolder.switchButton.setVisibility(View.VISIBLE);
            viewHolder.switchButton.setCheck(item.getChekced());
            createSwitchListener(item);
            viewHolder.switchButton.setOnChangedListener(onchangeListener);
        }
    }

    private void setTextView(TextView textView, String value) {
        if(textView == null || TextUtils.isEmpty(value)) {
            return;
        }

        if(textView.getVisibility() != View.VISIBLE) {
            textView.setVisibility(View.VISIBLE);
        }
        textView.setText(value);
    }

    private void createSwitchListener(final SettingTemplate item) {
        onchangeListener = new SwitchButton.OnChangedListener() {
            @Override
            public void OnChanged(View v, boolean checkState) {
               // switchChangeListener.onSwitchChange(item, checkState);
            }
        };
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        private View root;
        private HeadImageView headImageView;
        private TextView headTextView;
        private RelativeLayout relativeLayout;
        private TextView titleView;
        private TextView detailView;
        private SwitchButton switchButton;
        private View line;
        private ImageView indicator;
        private TextView headTitleView;
        private TextView headDetailView;
    }
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
