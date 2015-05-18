package com.avoscloud.chat.base;

import android.content.Context;
import android.text.Spannable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.widget.EditText;

public class EmotionEditText extends EditText {

  public EmotionEditText(Context context) {
    super(context);
  }

  public EmotionEditText(Context context, AttributeSet attrs, int defStyle) {
    super(context, attrs, defStyle);
  }

  public EmotionEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override
  public void setText(CharSequence text, BufferType type) {
    if (!TextUtils.isEmpty(text)) {
    	Spannable span = SmileUtils.getSmiledText(getContext(), text.toString());
		super.setText(span, type/*BufferType.SPANNABLE*/);
		//super.setText(EmotionUtils.replace(getContext(), text.toString()), type);
    } else {
      super.setText(text, type);
    }
  }
}
