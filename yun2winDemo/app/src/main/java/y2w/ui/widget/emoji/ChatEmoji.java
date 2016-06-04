package y2w.ui.widget.emoji;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import y2w.model.Session;
import y2w.ui.activity.ChatActivity;
import y2w.ui.activity.MainActivity;
import y2w.ui.adapter.MessageExpressionIndexAdapter;
import y2w.ui.adapter.MessageExpressionMenuAdapter;
import y2w.ui.adapter.MessageFragementPagerAdapter;
import y2w.ui.fragment.MessageFragment;

/**
 * Created by maa2 on 2016/4/11.
 */
public class ChatEmoji {

    private Context _context;
    private Activity _activity;
    private Session _session;
    private FragmentManager _fragmentManager;
    private ViewPager vp_emoji;
    private GridView gv_emoji_index;
    private GridView gv_emoji_menu;

    private List<Boolean> indexBooleans;
    private List<Expression.ExprMenu> exprmenus;

    private MessageFragementPagerAdapter pagerAdapter;
    private MessageExpressionIndexAdapter expressionIndexAdapter;
    private MessageExpressionMenuAdapter expressionMenuAdapter;

    public static int pageIndex = 0;
    private int titleIndex = 0;

    public ChatEmoji(Context context,Activity activity,Session session,FragmentManager fragmentManager){
        this._context = context;
        this._activity = activity;
        this._session = session;
        this._fragmentManager = fragmentManager;
    }

    public void initView(ViewPager vp_emoji,GridView gv_emoji_menu, GridView gv_emoji_index){
        this.vp_emoji = vp_emoji;
        this.gv_emoji_menu = gv_emoji_menu;
        this.gv_emoji_index = gv_emoji_index;
    }


    public void initExpressionViewPager() {
        List<Fragment> fragmentList = new ArrayList<Fragment>();
        MessageFragment firstFragment = MessageFragment.newInstance(_activity,_context,_session,
                "base");
        firstFragment.setChatEmoji(this);
        fragmentList.add(firstFragment);
        pagerAdapter = new MessageFragementPagerAdapter(
                _fragmentManager, fragmentList);
        vp_emoji.setAdapter(pagerAdapter);
        vp_emoji.setCurrentItem(0);
        vp_emoji
                .setOnPageChangeListener(new MyOnPageChangeListener());
        setPagerIndex(2, 0);
        setMenuDatas();
        expressionMenuAdapter = new MessageExpressionMenuAdapter(_context,
                exprmenus);
        gv_emoji_menu.setAdapter(expressionMenuAdapter);
        gv_emoji_menu.setNumColumns(6);
    }


    public void setPagerIndex(int number, int keybetrue) {
        if (indexBooleans == null) {
            indexBooleans = new ArrayList<Boolean>();
        } else {
            indexBooleans.clear();
        }
        for (int i = 0; i < number; i++) {
            if (i != keybetrue) {
                indexBooleans.add(false);
            } else {
                indexBooleans.add(true);
            }

        }
        pageIndex = keybetrue;
        expressionIndexAdapter = new MessageExpressionIndexAdapter(_context,
                indexBooleans);
        gv_emoji_index.setAdapter(expressionIndexAdapter);
    }

    private void setMenuDatas() {
        exprmenus = new ArrayList<Expression.ExprMenu>();

        Expression.ExprMenu exprMenu1 = new Expression.ExprMenu();
        exprMenu1.setName("经典");
        exprMenu1.setChoosebool(false);
        Expression.ExprMenu exprMenu2 = new Expression.ExprMenu();
        exprMenu2.setName("贴图");
        exprMenu2.setChoosebool(false);

        exprmenus.add(exprMenu1);
        exprmenus.add(exprMenu2);

    }

    private class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        @Override
        public void onPageSelected(int arg0) {
            if (titleIndex != arg0) {
                if (titleIndex > arg0) {
                    setPagerIndex(2, 1);
                } else {
                    setPagerIndex(2, 0);
                }
            }
            titleIndex = arg0;
            if (expressionMenuAdapter != null)
                expressionMenuAdapter.notifyDataSetChanged();
        }
    }
}
