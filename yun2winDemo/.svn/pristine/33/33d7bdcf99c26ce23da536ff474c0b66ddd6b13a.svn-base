package com.y2w.uikit.utils;

import com.example.maa2.uikit.R;
import com.y2w.uikit.utils.StringUtil;

import java.util.Random;

/**
 * 头像文字背景提供者
 * 
 * @author Administrator
 * 
 */
public class HeadTextBgProvider {
	public static int getTextBg() {
		int i = new Random().nextInt(5);
		switch (i) {
		case 0:
			return R.drawable.circle_name_0;
		case 1:
			return R.drawable.circle_name_1;
		case 2:
			return R.drawable.circle_name_2;
		case 3:
			return R.drawable.circle_name_3;
		case 4:
			return R.drawable.circle_name_4;
		}
		return R.drawable.circle_name_0;
	}
	
	public static int getTextBg(String qunno) {
		int i=0;
		if(!StringUtil.isEmpty(qunno)){
			try{
				i =qunno.subSequence(qunno.length()-1, qunno.length()).charAt(0);
				i=i%5;
				/*i = Integer.parseInt(qunno.subSequence(qunno.length()-1, qunno.length()).toString()) % 5;*/
			}catch(Exception e){
				i = new Random().nextInt(5);
			}
		}else{
			i = new Random().nextInt(5);
		}
		switch (i) {
		case 0:
			return R.drawable.circle_name_0;
		case 1:
			return R.drawable.circle_name_1;
		case 2:
			return R.drawable.circle_name_2;
		case 3:
			return R.drawable.circle_name_3;
		case 4:
			return R.drawable.circle_name_4;
		}
		return R.drawable.circle_name_0;
	}

	public static int getTextBg(int num) {
		int i=0;
		i = num % 5;
		switch (i) {
		case 0:
			return R.drawable.circle_name_0;
		case 1:
			return R.drawable.circle_name_1;
		case 2:
			return R.drawable.circle_name_2;
		case 3:
			return R.drawable.circle_name_3;
		case 4:
			return R.drawable.circle_name_4;
		}
		return R.drawable.circle_name_0;
	}
}
