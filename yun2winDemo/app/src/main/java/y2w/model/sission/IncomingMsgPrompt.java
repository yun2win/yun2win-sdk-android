package y2w.model.sission;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.y2w.uikit.customcontrols.listview.PullToRefreshListView;
import com.y2w.uikit.utils.MoonUtil;
import com.yun2win.demo.R;

import y2w.common.HeadImageView;
import y2w.model.MessageModel;

/**
 * 新消息提醒模块
 * Created by hejie on 2016/3/14.
 */
public class IncomingMsgPrompt {
    // 底部新消息提示条
    private View newMessageTipLayout;
    private TextView newMessageTipTextView;
    private HeadImageView newMessageTipHeadImageView;

    private Context context;
    private View view;
    private PullToRefreshListView messageListView;
    private Handler uiHandler;

    public IncomingMsgPrompt(Context context, View view, PullToRefreshListView messageListView, Handler uiHandler) {
        this.context = context;
        this.view = view;
        this.messageListView = messageListView;
        this.uiHandler = uiHandler;
    }

    // 显示底部新信息提示条
    public void show(MessageModel newMessage) {
        if (newMessageTipLayout == null) {
            init();
        }
        if (!TextUtils.isEmpty(newMessage.getEntity().getSender())) {
            newMessageTipHeadImageView.loadBuddyAvatarbyuid(newMessage.getEntity().getSender(), R.drawable.circle_image_transparent);
        } else {
            newMessageTipHeadImageView.resetImageView();
        }

        MoonUtil.identifyFaceExpression(context, newMessageTipTextView, newMessage.getEntity().getContent(),
                ImageSpan.ALIGN_BOTTOM);
        newMessageTipLayout.setVisibility(View.VISIBLE);
        uiHandler.removeCallbacks(showNewMessageTipLayoutRunnable);
        uiHandler.postDelayed(showNewMessageTipLayoutRunnable, 5 * 1000);
    }

    public void onBackPressed() {
        removeHandlerCallback();
    }

    // 初始化底部新信息提示条
    private void init() {
        ViewGroup containerView = (ViewGroup) view.findViewById(R.id.message_activity_list_view_container);
        View.inflate(context, R.layout.lyy_new_message_tip_layout, containerView);
        newMessageTipLayout = containerView.findViewById(R.id.new_message_tip_layout);
        newMessageTipLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {


            }
        });
        newMessageTipTextView = (TextView) newMessageTipLayout.findViewById(R.id.new_message_tip_text_view);
        newMessageTipHeadImageView = (HeadImageView) newMessageTipLayout.findViewById(R.id.new_message_tip_head_image_view);
    }

    private Runnable showNewMessageTipLayoutRunnable = new Runnable() {

        @Override
        public void run() {
            newMessageTipLayout.setVisibility(View.GONE);
        }
    };

    private void removeHandlerCallback() {
        if (showNewMessageTipLayoutRunnable != null) {
            uiHandler.removeCallbacks(showNewMessageTipLayoutRunnable);
        }
    }
}
