package y2w.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.y2w.uikit.utils.HeadTextBgProvider;
import com.y2w.uikit.utils.StringUtil;
import com.y2w.uikit.utils.pinyinutils.SortModel;
import com.yun2win.demo.R;
import com.yun2win.utils.Json;

import java.util.List;

import y2w.base.AppContext;
import y2w.base.Urls;
import y2w.common.HeadImageView;
import y2w.common.ImagePool;
import y2w.service.Back;

/**
 * Created by maa2 on 2016/1/22.
 */
public class WebMenuAdapter extends BaseAdapter {
    private Context context;
    private List<Json> menus;
    public WebMenuAdapter(Context context,List<Json> menus){
        this.context = context;
        this.menus = menus;
    }


    @Override
    public int getCount() {
        return menus == null ? 0 :menus.size();
    }

    @Override
    public Object getItem(int position) {
        return menus.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Json model = menus.get(position);
        HoldView holdView;
        if (null == view) {
            holdView = new HoldView();
            view = LayoutInflater.from(context).inflate(R.layout.webmenu_list_item, null);
            holdView.img_title = (ImageView) view
                    .findViewById(R.id.img_title);
            holdView.tv_title=(TextView)view.findViewById(R.id.tv_title);
            view.setTag(holdView);
        } else {
            holdView = (HoldView) view.getTag();
        }
        String menuName =model.getStr("name");
        String menuType = model.getStr("type");
        final String menuId = model.getStr("id");
        if(!StringUtil.isEmpty(menuType)){
            if(menuType.equals("text")){
                holdView.tv_title.setVisibility(View.VISIBLE);
                holdView.img_title.setVisibility(View.GONE);
                holdView.tv_title.setText(menuName);
            }else if(menuType.equals("icon")){
                String iconSrc = model.getStr("iconSrc");
                holdView.img_title.setVisibility(View.VISIBLE);
                holdView.tv_title.setVisibility(View.GONE);
                ImagePool.getInstance(AppContext.getAppContext()).load(iconSrc, "", holdView.img_title, 0);
            }else if(menuType.equals("iconText")){
                String iconSrc = model.getStr("iconSrc");
                holdView.img_title.setVisibility(View.VISIBLE);
                holdView.tv_title.setVisibility(View.VISIBLE);
                holdView.tv_title.setText(menuName);
                ImagePool.getInstance(AppContext.getAppContext()).load(iconSrc, "", holdView.img_title, 0);
            }
        }
        return view;
    }


    class HoldView {
        public TextView tv_title;
        public ImageView img_title;
    }

}
