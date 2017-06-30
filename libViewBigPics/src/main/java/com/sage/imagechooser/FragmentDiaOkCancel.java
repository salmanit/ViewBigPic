package com.sage.imagechooser;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sage.bigscalephotoviewanim.R;


public class FragmentDiaOkCancel extends DialogFragment implements OnClickListener{

	
	public static FragmentDiaOkCancel create(String title,String content){
		FragmentDiaOkCancel dialog=new FragmentDiaOkCancel();
		Bundle bundle=new Bundle();
		bundle.putString(tag_title, title);
		bundle.putString(tag_content, content);
		dialog.setArguments(bundle);
		return dialog;
	}
	public static FragmentDiaOkCancel create(String title,String content,String ok_show,String cancel_show){
		FragmentDiaOkCancel dialog=new FragmentDiaOkCancel();
		Bundle bundle=new Bundle();
		bundle.putString(tag_title, title);
		bundle.putString(tag_content, content);
		bundle.putString(tag_ok, ok_show);
		bundle.putString(tag_cancel, cancel_show);
		dialog.setArguments(bundle);
		return dialog;
	}
	public FragmentDiaOkCancel() {
		setStyle(STYLE_NO_TITLE, 0);//取消标题
	}
	TextView tv_title;
	TextView tv_content;
	TextView tv_ok;
	TextView tv_cancel;
	private static String tag_title="title";
	private static String tag_content="content";
	private static String tag_ok="ok";
	private static String tag_cancel="cancel";
	private int colorTitle;
	private int colorContent;
	private int colorOK;
	private int colorCancel;

	public FragmentDiaOkCancel setColorTitle(int colorTitle) {
		this.colorTitle = colorTitle;
		return this;
	}

	public FragmentDiaOkCancel setColorContent(int colorContent) {
		this.colorContent = colorContent;
		return this;
	}

	public FragmentDiaOkCancel setColorOK(int colorOK) {
		this.colorOK = colorOK;
		return this;
	}

	public FragmentDiaOkCancel setColorCancel(int colorCancel) {
		this.colorCancel = colorCancel;
		return this;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.okcancel_dialog_fragment, container,false);
		initView(view);
		if(getArguments()!=null){
			if(!TextUtils.isEmpty(getArguments().getString(tag_title))){
				tv_title.setText(getArguments().getString(tag_title));
				tv_title.setVisibility(View.VISIBLE);
			}
			if(!TextUtils.isEmpty(getArguments().getString(tag_content))){
				tv_content.setText(getArguments().getString(tag_content));
				tv_content.setVisibility(View.VISIBLE);
			}
			if(!TextUtils.isEmpty(getArguments().getString(tag_ok))){
				tv_ok.setText(getArguments().getString(tag_ok));
			}
			if(!TextUtils.isEmpty(getArguments().getString(tag_cancel))){
				tv_cancel.setText(getArguments().getString(tag_cancel));
			}
		}
		if(colorTitle>0){
			tv_title.setTextColor(colorTitle);
		}
		if(colorContent>0){
			tv_content.setTextColor(colorContent);
		}
		if(colorCancel>0){
			tv_cancel.setTextColor(colorCancel);
		}
		if(colorOK>0){
			tv_ok.setTextColor(colorOK);
		}
		return view;
	}

	private void initView(View view) {
		tv_title= (TextView) view.findViewById(R.id.tv_title);
		tv_content= (TextView) view.findViewById(R.id.tv_content);
		tv_ok= (TextView) view.findViewById(R.id.tv_ok);
		tv_cancel= (TextView) view.findViewById(R.id.tv_cancel);
		tv_ok.setOnClickListener(this);
		tv_cancel.setOnClickListener(this);
	}

	private boolean cancel=true;
	private boolean cancelOutside=true;
	
	public FragmentDiaOkCancel setCancel(boolean cancel) {
		this.cancel = cancel;
		return this;
	}
	public FragmentDiaOkCancel setCancelOutside(boolean cancelOutside) {
		this.cancelOutside = cancelOutside;
		return this;
	}
	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		Window window=getDialog().getWindow();
		 WindowManager.LayoutParams wl = window.getAttributes();
	        wl.x = 0;
	        wl.y = 0;
	        wl.gravity=Gravity.CENTER;
	        wl.width = getResources().getDisplayMetrics().widthPixels*4/5;
	        wl.height = ViewGroup.LayoutParams.WRAP_CONTENT;
	        wl.dimAmount=0.6f;
	        getDialog().onWindowAttributesChanged(wl);
		window.setBackgroundDrawable(new ColorDrawable());
		window.setFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND,   
				 WindowManager.LayoutParams.FLAG_DIM_BEHIND); 

		getDialog().setCanceledOnTouchOutside(cancelOutside);
		getDialog().setCancelable(cancel);
	}
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.tv_ok){
			if(listener!=null){
				listener.onClick(v);
			}
			if(okCancelListener!=null){
				okCancelListener.okClick();
			}
		}else if(v.getId()==R.id.tv_cancel){
			if(okCancelListener!=null){
				okCancelListener.cancelClick();
			}
		}
		dismiss();
	}

	private OnClickListener  listener;
	public FragmentDiaOkCancel setListener(OnClickListener listener) {
		this.listener = listener;
		return this;
	}
	
	private OkCancelListener  okCancelListener;
	
	public FragmentDiaOkCancel setOkCancelListener(OkCancelListener okCancelListener) {
		this.okCancelListener = okCancelListener;
		return this;
	}

	public interface OkCancelListener{
		 void okClick();
		 void  cancelClick();
	}
	
}
