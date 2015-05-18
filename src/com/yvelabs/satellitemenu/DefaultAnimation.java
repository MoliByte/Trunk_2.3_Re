package com.yvelabs.satellitemenu;

import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnticipateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 * 
 * @author Yve
 *
 */
public class DefaultAnimation extends AbstractAnimation {

	/**
	 * ����  ���䶯��
	 */
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

	/**
	 * ���� �ջض���
	 */
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

	/**
	 * ���� �������
	 */
	public Animation createPlanetItemClickedAnimation(View view) {

		return createPlanetDrawBackAnimation(view);
	}

	/**
	 * ���� ���䶯��
	 * @throws Exception 
	 */
	public Animation createSatelliteLaunchAnimation(View satellite) {
		AnimationSet animationSet = new AnimationSet(false);
		SatelliteItemModel itemModel = (SatelliteItemModel) satellite.getTag();

		// ͸������
		AlphaAnimation alphaAnimation = new AlphaAnimation(0f, 1f);
		alphaAnimation.setDuration(60);
		alphaAnimation.setStartOffset(0);
		animationSet.addAnimation(alphaAnimation);

		// �ƶ�����
		TranslateAnimation translateAnimation = new TranslateAnimation(
				itemModel.getOriginX() - itemModel.getStopX()
						- itemModel.getAdjustX(), 0, itemModel.getOriginY()
						- itemModel.getStopY() - itemModel.getAdjustX(), 0);
		translateAnimation.setStartOffset(0);
		translateAnimation.setDuration(400);
		translateAnimation.setAnimationListener(new SatelliteLaunchListener(
				satellite));
		translateAnimation.setInterpolator(new OvershootInterpolator(3F));
		animationSet.addAnimation(translateAnimation);

		// ��ת����
		RotateAnimation rotateAnimation = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		rotateAnimation.setInterpolator(new AccelerateInterpolator(1.0F));
		rotateAnimation.setDuration(200);
		rotateAnimation.setStartOffset(400);
//		animationSet.addAnimation(rotateAnimation);

		//
		animationSet.setStartOffset(30 * itemModel.getId());

		satellite.startAnimation(animationSet);
		return animationSet;
	}

	/**
	 * ���� �ջض���
	 */
	public Animation createSatelliteDrawBackAnimation(View view) {
		AnimationSet animationSet = new AnimationSet(false);
		SatelliteItemModel itemModel = (SatelliteItemModel) view.getTag();
		
		//��ת����
		RotateAnimation rotateAnimation = new RotateAnimation(720, 0, 
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
		 rotateAnimation.setInterpolator(new AccelerateInterpolator(1.2F));
		 rotateAnimation.setDuration(400);
		 animationSet.addAnimation(rotateAnimation);

		//�ƶ�����
		TranslateAnimation translateAnimation = new TranslateAnimation(0, itemModel.getOriginX() - itemModel.getStopX() - itemModel.getAdjustX(), 0, itemModel.getOriginY() - itemModel.getStopY() - itemModel.getAdjustX());
		translateAnimation.setStartOffset(250);
		translateAnimation.setDuration(400);
		translateAnimation.setAnimationListener(new SatelliteDrawBackListener(view));
		translateAnimation.setInterpolator(new AnticipateInterpolator(3F));
		animationSet.addAnimation(translateAnimation);
		
		//͸������
		AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        alphaAnimation.setDuration(10);
        alphaAnimation.setStartOffset(640);
        animationSet.addAnimation(alphaAnimation);
        
        //
        animationSet.setStartOffset(30 * itemModel.getId());
		
		view.startAnimation(animationSet);
		return animationSet;
	}

	/**
	 * ���� �������
	 */
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
		private SatelliteItemModel itemModel;
		
		public SatelliteLaunchListener (View satellite) {
			this.satellite = satellite;
//			itemModel = (SatelliteItemModel) satellite.getTag();
		}

		@Override
		public void onAnimationEnd(Animation animation) {
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

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
		private SatelliteItemModel itemModel;
		
		private SatelliteDrawBackListener (View view) {
			this.view = view;
//			itemModel = (SatelliteItemModel) view.getTag();
		}

		@Override
		public void onAnimationEnd(Animation animation) {
			
			view.setVisibility(View.GONE);
		}

		@Override
		public void onAnimationRepeat(Animation animation) {
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			
		}
		
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
		public void onAnimationRepeat(Animation animation) {
			
		}

		@Override
		public void onAnimationStart(Animation animation) {
			
		}
	}
}
