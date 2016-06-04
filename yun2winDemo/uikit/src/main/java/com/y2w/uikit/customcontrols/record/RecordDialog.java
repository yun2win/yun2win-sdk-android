package com.y2w.uikit.customcontrols.record;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.y2w.uikit.utils.ToastUtil;

import java.io.File;


/**
 * 录音弹出对话框
 */
public class RecordDialog extends Dialog implements RecordButton.OnRecordListener {

	
	private OnRecrodFinishListener recordFinishListener;
	RelativeLayout rr;
	public RecordDialog(Context context, boolean cancelable,
			OnCancelListener cancelListener , OnRecrodFinishListener recordFinishListener) {
		super(context, cancelable, cancelListener);
		this.recordFinishListener = recordFinishListener;
	}

	/*public RecordDialog(Context context , OnRecrodFinishListener recordFinishListener) {
		super(context, R.style.volume_dialog);
		this.recordFinishListener = recordFinishListener;
	}*/

	public RecordDialog(Context context, int theme , OnRecrodFinishListener recordFinishListener) {
		super(context, theme);	
		this.recordFinishListener = recordFinishListener;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.voice_rcd_hint_window);
		
		//iv=(ImageView)findViewById(R.id.volume);

	}
	ImageView iv;
	
	@Override
	public void onRecordStart() {
		if(!this.isShowing()){
			show();
		}
	}

	@Override
	public void onSuccess(String path, int code) {
		//Toast.makeText(getContext(), code+"----"+path, Toast.LENGTH_SHORT).show();
		switch (code) {
			case RecordButton.RECORD_LESS_THAN_MIN:
				ToastUtil.ToastMessage(getContext(), "录音时间太短。");
				new File(path).deleteOnExit();
				break;
			case RecordButton.RECORD_TIMEOUT:
				ToastUtil.ToastMessage(getContext(), "录音达到最大长度。");
			case RecordButton.RECORD_NORMAL:
				recordFinishListener.onResualt(path);
				break;
			default:
				break;
		}
		if(this.isShowing()){
			dismiss();
		}
	}

	@Override
	public void onError(String msg) {
		Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
		if(this.isShowing()){
			dismiss();
		}
		
	}

	@Override
	public void onVolumeChange(int volumn) {
		switch (volumn) {
			case 1:
				//iv.setImageResource(R.drawable.amp1);
				break;
			case 2:
				//iv.setImageResource(R.drawable.amp2);
				break;
			case 3:
				//iv.setImageResource(R.drawable.amp3);
				break;
			case 4:
				//iv.setImageResource(R.drawable.amp4);
				break;
			case 5:
				//iv.setImageResource(R.drawable.amp5);
				break;
			case 6:
				//iv.setImageResource(R.drawable.amp6);
				break;
			case 7:
				//iv.setImageResource(R.drawable.amp7);
				break;
		}
	}
	
	@Override
	public void onCancel() {
		
	}
	
	public interface OnRecrodFinishListener{
		void onResualt(String filePath);
	}

	

	
	
	

}
