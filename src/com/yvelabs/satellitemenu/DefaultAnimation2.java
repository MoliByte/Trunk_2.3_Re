package com.yvelabs.satellitemenu;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

import com.yvelabs.satellitemenu.utils.MyMathUtils;

public class DefaultAnimation2 extends AbstractAnimation {
	
	private int satelliteStartOffsetMin = 0;
	private int satelliteStartOffsetMax = 100;
	
	public int getSatelliteStartOffsetMin() {
		return satelliteStartOffsetMin;
	}
	public void setSatelliteStartOffsetMin(int satelliteStartOffsetMin) {
		this.satelliteStartOffsetMin = satelliteStartOffsetMin;
	}
	public int getSatelliteStartOffsetMax() {
		return satelliteStartOffsetMax;
	}
	public void setSatelliteStartOffsetMax(int satelliteStartOffsetMax) {
		this.satelliteStartOffsetMax = satelliteStartOffsetMax;
	}

	@Override
	public Animation createPlanetLaunchAnimation(View view) {
		RotateAnimation rotateAnimation = new RotateAnimation(0f, -135f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(300);
		rotateAnimation.setFillEnabled(true);
		rotateAnimation.setFillAfter(true);
		rotateAnimation.setFillBefore(false);

		view.startAnimation(rotateAnimation);
		return rotateAnimation;
	}

	@Override
	public Animation createPlanetDrawBackAnimation(View view) {
		RotateAnimation rotateAnimation = new RotateAnimation(-135f, 0f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setDuration(300);
		rotateAnimation.setFillEnabled(true);
		rotateAnimation.setFillAfter(true);
		rotateAnimation.setFillBefore(false);

		view.startAnimation(rotateAnimation);
		return rotateAnimation;
	}

	@Override
	public Animation createPlanetItemClickedAnimation(View view) {
		return createPlanetDrawBackAnimation(view);
	}

	/**
	 * ����  ���䶯��
	 */
	@Override
	public Animation createSatelliteLaunchAnimation(View view) {
		AnimationSet animationSet = new AnimationSet(false);
		SatelliteItemModel itemModel = (SatelliteItemModel) view.getTag();
		
		// �ƶ�����
		TranslateAnimation translateAnimation = new TranslateAnimation(
				itemModel.getOriginX() - itemModel.getStopX()
						- itemModel.getAdjustX(), 0, itemModel.getOriginY()
						- itemModel.getStopY() - itemModel.getAdjustX(), 0);
		int startOffset = MyMathUtils.getRandom(satelliteStartOffsetMin, satelliteStartOffsetMax);
		translateAnimation.setStartOffset(startOffset);
		translateAnimation.setDuration(400);
		translateAnimation.setAnimationListener(new SatelliteLaunchListener(view));
		translateAnimation.setInterpolator(new OvershootInterpolator(3F));
		animationSet.addAnimation(translateAnimation);
		
		//͸������
		AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
		alphaAnimation.setDuration(60);
		alphaAnimation.setStartOffset(startOffset);
		animationSet.addAnimation(alphaAnimation);
		
		view.startAnimation(animationSet);
		return animationSet;
	}

	/**
	 * �����ջض���
	 */
	@Override
	public Animation createSatelliteDrawBackAnimation(View view) {
		AnimationSet animationSet = new AnimationSet(false);
		SatelliteItemModel itemModel = (SatelliteItemModel) view.getTag();
		
		//�ƶ�����
		TranslateAnimation translateAnimation = new TranslateAnimation(0, itemModel.getOriginX() - itemModel.getStopX() - itemModel.getAdjustX(), 0, itemModel.getOriginY() - itemModel.getStopY() - itemModel.getAdjustX());
		int startOffset = MyMathUtils.getRandom(satelliteStartOffsetMin, satelliteStartOffsetMax);
		translateAnimation.setStartOffset(200 + startOffset);
		translateAnimation.setDuration(400);
		translateAnimation.setAnimationListener(new SatelliteDrawBackListener(view));
		translateAnimation.setInterpolator(new AnticipateInterpolator(3F));
		animationSet.addAnimation(translateAnimation);
		
		//͸������
		AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(60);
        alphaAnimation.setStartOffset(540 + startOffset);
        animationSet.addAnimation(alphaAnimation);
		
		view.startAnimation(animationSet);
		return animationSet;
	}

	@Override
	public Animation createSatelliteItemClickedAnimation(View view) {
		AnimationSet clickedAnimationSet = new AnimationSet(false);
		//�ջض���
		//�Ŵ󶯻�
		ScaleAnimation scaleAnimation = new ScaleAnimation(1, 3, 1, 3, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		scaleAnimation.setDuration(600);
		scaleAnimation.setStartOffset(100);
		clickedAnimationSet.addAnimation(scaleAnimation);
		//͸������
		AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
		alphaAnimation.setDuration(300);
		alphaAnimation.setStartOffset(250);
		clickedAnimationSet.addAnimation(alphaAnimation);
		
		clickedAnimationSet.setAnimationListener(new SatelliteItemClickedListener(view));
		
		//�ջ���������
		for (SatelliteItemModel itemModel : getSatelliteList()) {
			if (itemModel.getId() != ((SatelliteItemModel)view.getTag()).getId()) 
			createSatelliteDrawBackAnimation(itemModel.getView());
		}
		
		view.startAnimation(clickedAnimationSet);
		
		return clickedAnimationSet;
	}
	
	/**
	 * ���� ���� ��������
	 * @author Yve
	 *
	 */
	private class SatelliteLaunchListener implements Animation.AnimationListener {
		private View satellite;
		
		public SatelliteLaunchListener (View satellite) {
			this.satellite = satellite;
//			itemModel = (SatelliteItemModel) satellite.getTag();
		}
		@Override
		public void onAnimationEnd(Animation animation) { }
		@Override
		public void onAnimationRepeat(Animation animation) { }
		@Override
		public void onAnimationStart(Animation animation) {
			satellite.setVisibility(View.VISIBLE);
		}
		
	}
	
	/**
	 * ���� �ջ� ��������
	 * @author Yve
	 *
	 */
	private class SatelliteDrawBackListener implements Animation.AnimationListener {
		private View view;
		
		private SatelliteDrawBackListener (View view) {
			this.view = view;
//			itemModel = (SatelliteItemModel) view.getTag();
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			view.setVisibility(View.GONE);
		}
		@Override
		public void onAnimationRepeat(Animation animation) { }
		@Override
		public void onAnimationStart(Animation animation) { }
		
	}
	
	/**
	 * ���� ��� ����������
	 * @author Yve
	 *
	 */
	private class SatelliteItemClickedListener implements Animation.AnimationListener {
		private View view;
		
		public SatelliteItemClickedListener (View view) {
			this.view = view;
		}
		@Override
		public void onAnimationEnd(Animation animation) {
			view.setVisibility(View.GONE);
		}
		@Override
		public void onAnimationRepeat(Animation animation) { }
		@Override
		public void onAnimationStart(Animation animation) { }
	}

}
