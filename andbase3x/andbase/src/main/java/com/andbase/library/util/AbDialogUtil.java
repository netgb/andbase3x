
package com.andbase.library.util;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.andbase.library.R;
import com.andbase.library.view.dialog.AbAlertDialogFragment;
import com.andbase.library.view.dialog.AbProgressDialogFragment;
import com.andbase.library.view.dialog.AbSampleDialogFragment;


/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info Dialog工具类
 */

public class AbDialogUtil {
	
	/** dialog 标记*/
	public static String dialogTag = "dialog";
	
	public static int ThemeHoloLightDialog = android.R.style.Theme_Holo_Light_Dialog;
	
	public static int ThemeLightPanel = android.R.style.Theme_Light_Panel;
	
	/**
	 * 显示一个全屏对话框.
	 * @param view
	 * @return
	 */
	public static AbSampleDialogFragment showFullScreenDialog(View view) {
		AbSampleDialogFragment dialogFragment = null;
		try{
			FragmentActivity activity = (FragmentActivity)view.getContext();
			// Create and show the dialog.
			dialogFragment = AbSampleDialogFragment.newInstance(DialogFragment.STYLE_NORMAL,android.R.style.Theme_Black_NoTitleBar_Fullscreen,Gravity.CENTER);
			dialogFragment.setContentView(view);
			FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
			// 指定一个系统转场动画
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			dialogFragment.show(ft, dialogTag);
		}catch(Exception e){
			e.printStackTrace();
		}
        return dialogFragment;
    }
	
	/**
	 * 显示一个居中的对话框.
	 * @param view
	 */
	public static AbSampleDialogFragment showDialog(View view) {
		return showDialog(view,Gravity.CENTER);
	}

	/**
	 * 显示一个居中的面板.
	 * @param view
	 */
	public static AbSampleDialogFragment showPanel(View view) {
		return showPanel(view,Gravity.CENTER);
	}

	/**
	 *
	 * 显示一个指定位置对话框.
	 * @param view
	 * @param gravity 位置
	 * @return
	 */
	public static AbSampleDialogFragment showDialog(View view,int gravity) {
		return showDialogOrPanel(view,gravity,dialogTag,ThemeHoloLightDialog);
    }

	public static AbSampleDialogFragment showDialog(View view,int gravity,String tag) {
		return showDialogOrPanel(view,gravity,tag,ThemeHoloLightDialog);
	}

	/**
	 *
	 * 显示一个指定位置的Panel.
	 * @param view
	 * @param gravity 位置
	 * @return
	 */
	public static AbSampleDialogFragment showPanel(View view,int gravity) {
		return showDialogOrPanel(view,gravity,dialogTag,ThemeLightPanel);
	}

	public static AbSampleDialogFragment showPanel(View view,int gravity,String tag) {
		return showDialogOrPanel(view,gravity,tag,ThemeLightPanel);
	}
	
	/**
	 * 
	 * 自定义的对话框面板.
	 * @param view    View
	 * @param gravity 位置
	 * @param style   样式 ThemeHoloLightDialog  ThemeLightPanel
	 * @return
	 */
	private static AbSampleDialogFragment showDialogOrPanel(View view,int gravity,String tag,int style) {
		AbSampleDialogFragment dialogFragment = null;
		try{
			FragmentActivity activity = (FragmentActivity)view.getContext();
			dialogFragment = AbSampleDialogFragment.newInstance(DialogFragment.STYLE_NO_TITLE,style,gravity);
			dialogFragment.setContentView(view);
			FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
			// 指定一个系统转场动画
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			dialogFragment.show(ft, tag);
		}catch(Exception e){
			e.printStackTrace();
		}
		return dialogFragment;
    }

	/**
	 * 显示一个普通对话框.
	 * @param view 对话框View
	 */
	public static AbAlertDialogFragment showAlertDialog(View view) {
		AbAlertDialogFragment dialogFragment = null;
		try{
			FragmentActivity activity = (FragmentActivity)view.getContext();
			dialogFragment = new AbAlertDialogFragment();
			dialogFragment.setContentView(view);
			FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
			// 指定一个系统转场动画
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			dialogFragment.show(ft, dialogTag);
		}catch(Exception e){
			e.printStackTrace();
		}
		return dialogFragment;
	}
	
	/**
	 * 显示系统样式进度框.
	 * @param context the context
	 * @param indeterminateDrawable 用默认请写0
	 * @param message the message
	 */
	public static AbProgressDialogFragment showProgressDialog(Context context, int indeterminateDrawable, String message) {
		AbProgressDialogFragment dialogFragment = null;
		try{
			FragmentActivity activity = (FragmentActivity)context;
			dialogFragment = AbProgressDialogFragment.newInstance(indeterminateDrawable,message);
			FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
			// 指定一个系统转场动画
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			dialogFragment.show(ft, dialogTag);
		}catch(Exception e){
			e.printStackTrace();
		}
		return dialogFragment;
    }

    /**
     * 显示推荐样式进度框.
     * @param context the context
     */
    public static AbSampleDialogFragment showLoadingDialog(Context context,int indeterminateDrawable,String message) {
        View view = View.inflate(context,R.layout.view_loading_dialog,null);
        TextView textView = (TextView)view.findViewById(R.id.loading_text);
        ProgressBar progressBar = (ProgressBar)view.findViewById(R.id.loading_progress);
        if(message != null){
            textView.setText(message);
        }
        if(indeterminateDrawable > 0){
            progressBar.setIndeterminateDrawable(context.getResources().getDrawable(indeterminateDrawable));
        }
        return showLoadingDialog(context,view);
    }

	/**
	 * 显示推荐样式进度框.
	 * @param context the context
	 * @param view View
	 */
	public static AbSampleDialogFragment showLoadingDialog(Context context,View view) {
		AbSampleDialogFragment dialogFragment = null;
		try{
			FragmentActivity activity = (FragmentActivity)view.getContext();
			dialogFragment = AbSampleDialogFragment.newInstance(DialogFragment.STYLE_NORMAL,android.R.style.Theme_Black_NoTitleBar_Fullscreen,Gravity.CENTER);
			dialogFragment.setContentView(view);
			FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			dialogFragment.show(ft, dialogTag);
		}catch(Exception e){
			e.printStackTrace();
		}
		return dialogFragment;
	}

	/**
	 * 显示一个隐藏的的对话框.
	 * @param context
	 * @param fragment
     */
	public static void showDialog(Context context,DialogFragment fragment) {
		FragmentActivity activity = (FragmentActivity)context;
		fragment.show(activity.getFragmentManager(), dialogTag);
	}

	
	/**
	 * 移除Fragment.
	 * @param context the context
	 */
	public static void removeDialog(final Context context){
		removeDialog(context,dialogTag);
	}

	public static void removeDialog(final Context context,String tag){
		try {
			FragmentActivity activity = (FragmentActivity)context;
			FragmentTransaction ft = activity.getFragmentManager().beginTransaction();
			// 指定一个系统转场动画
			ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_CLOSE);
			Fragment prev = activity.getFragmentManager().findFragmentByTag(tag);
			if (prev != null) {
				ft.remove(prev);
			}
			//不能加入到back栈
			//ft.addToBackStack(null);
			ft.commit();
		} catch (Exception e) {
			//可能有Activity已经被销毁的异常
			e.printStackTrace();
		}
	}

	/**
	 * 移除Fragment和View
	 * @param view
	 */
	public static void removeDialog(View view){
		removeDialog(view.getContext());
		AbViewUtil.removeSelfFromParent(view);
	}

	public static void removeDialog(View view,String tag){
		removeDialog(view.getContext(),tag);
		AbViewUtil.removeSelfFromParent(view);
	}

	public static void showAlertDialog(final Context context,String message,String  button1Text,String button2Text,final View.OnClickListener onClickListener,final View.OnClickListener onClickListener2){
		View view = View.inflate(context, R.layout.view_confirm_dialog,null);
		Button button1 =  (Button)view.findViewById(R.id.dialog_button1);
		Button button2 =  (Button)view.findViewById(R.id.dialog_button2);
		button1.setText(button1Text);
		button2.setText(button2Text);
		TextView messageText =  (TextView)view.findViewById(R.id.dialog_message);
		messageText.setText(message);
		AbDialogUtil.showAlertDialog(view);
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickListener.onClick(v);
				AbDialogUtil.removeDialog(context);

			}
		});
		button2.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onClickListener2.onClick(v);
				AbDialogUtil.removeDialog(context);
			}
		});
	}

	public static void showMessageDialog(final Context context,String title,String message){
		View view = View.inflate(context, R.layout.view_confirm_dialog,null);
		Button button1 =  (Button)view.findViewById(R.id.dialog_button1);
		Button button2 =  (Button)view.findViewById(R.id.dialog_button2);
		button1.setText("确定");
		button2.setVisibility(View.GONE);
		TextView titleText =  (TextView)view.findViewById(R.id.dialog_title);
		TextView messageText =  (TextView)view.findViewById(R.id.dialog_message);
		titleText.setText(title);
		messageText.setText(message);
		AbDialogUtil.showAlertDialog(view);
		button1.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AbDialogUtil.removeDialog(context);

			}
		});

	}

	public static void showConfirmDialog(final Context context,String title, String message,View.OnClickListener onClickListener){
		View view = View.inflate(context, R.layout.view_confirm_dialog,null);
		Button button1 =  (Button)view.findViewById(R.id.dialog_button1);
		Button button2 =  (Button)view.findViewById(R.id.dialog_button2);
		button1.setText("确定");
		button2.setVisibility(View.GONE);
        TextView titleText =  (TextView)view.findViewById(R.id.dialog_title);
		TextView messageText = (TextView)view.findViewById(R.id.dialog_message);
        titleText.setText(title);
		messageText.setText(message);
		AbAlertDialogFragment fragment = AbDialogUtil.showAlertDialog(view);
		fragment.setCancelable(false);
		button1.setOnClickListener(onClickListener);

	}

	public static void showListDialog(final Context context,String[] list,int defaultPosition,final AdapterView.OnItemClickListener onItemClickListener){
		View view = View.inflate(context, R.layout.view_list_dialog,null);
		final ListView listView = (ListView)view.findViewById(R.id.list);
		listView.setAdapter(new ArrayAdapter<String>(context,R.layout.view_list_item_checked, list));
		AbDialogUtil.showAlertDialog(view);

		listView.setItemChecked(defaultPosition,true);

		Button buttonOk = (Button)view.findViewById(R.id.dialog_button2);
		Button buttonCancel = (Button)view.findViewById(R.id.dialog_button1);

		buttonOk.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int position = listView .getCheckedItemPosition();
				onItemClickListener.onItemClick(null,v,position,position);
				AbDialogUtil.removeDialog(context);

			}
		});
		buttonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				AbDialogUtil.removeDialog(context);
			}
		});

	}

}
