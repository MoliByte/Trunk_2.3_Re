package com.yvelabs.satellitemenu;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView.ScaleType;
import android.widget.RelativeLayout;

import com.app.base.entity.Point;
import com.app.base.init.MyApplication;
import com.idongler.widgets.CircleImageView;
import com.yvelabs.satellitemenu.utils.ImageUtils;
import com.yvelabs.satellitemenu.utils.MyMathUtils;

/**
 * 
 * @author Yve
 *
 */
public class SatelliteMenu extends RelativeLayout {
	
	private int originAngle; //��ʼ�Ƕ�
	private int endAngle; //��ֹ�Ƕ�
	private int satelliteDistance; //���Ǿ���
	private int customerRadiusAdjust; //
	
	private CircleImageView planetMenu;
	private AbstractAnimation menuAnimation;
	private OnSatelliteClickedListener onSatelliteClickedListener;
	private OnPlanetClickedListener onPlanetClickedListener;
	private List<SatelliteItemModel> satelliteList = new ArrayList<SatelliteItemModel>();
	private boolean launched = false;
	private AtomicBoolean plusAnimationActive = new AtomicBoolean(false);
	
	private android.graphics.Point point;
	

	public void setPlanetImg(int planetImgResourceId) {
		new ImageUtils().setImage(planetMenu, planetImgResourceId);
	}

	public void setPlanetImg(Drawable planetImgDrawable) {
		new ImageUtils().setImage(planetMenu, planetImgDrawable);
	}

	public void setPlanetImg(String planetImgAssetPath) {
		try {
			new ImageUtils().setImage(planetMenu.getContext(), planetMenu, planetImgAssetPath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	

	public SatelliteMenu(Context context) {
		super(context);
		init(context);
	}

	public SatelliteMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	public SatelliteMenu(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
		
	}
	
	/**
	 * ������ǰ�ť������
	 * @author Yve
	 *
	 */
	public interface OnSatelliteClickedListener {
		public void onClick(View view);
	}
	/**
	 * �������ǰ�ť������
	 * @param onSatelliteClickedListener
	 */
	public void setOnSatelliteClickedListener(OnSatelliteClickedListener onSatelliteClickedListener) {
		this.onSatelliteClickedListener = onSatelliteClickedListener;
	}
	
	public interface OnPlanetClickedListener {
		public void onClick(View view);
	}
	
	public void setOnPlanetClickedListener (OnPlanetClickedListener onPlanetClickedListener) {
		this.onPlanetClickedListener = onPlanetClickedListener;
	}

	/**
	 * ��ʼ��
	 * @param context
	 */
	private void init(Context context) {
		planetMenu = new CircleImageView(context);
		
		point=MyApplication.screenSize;
		
		RelativeLayout.LayoutParams planetlayoutPara = new RelativeLayout.LayoutParams(point.x/6, point.x/6);
		planetMenu.setScaleType(ScaleType.CENTER);
		addView(planetMenu, planetlayoutPara);
		
		//����������
		planetMenu.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (plusAnimationActive.compareAndSet(false, true)) {
					if (onPlanetClickedListener != null) 
						onPlanetClickedListener.onClick(v);
					
					try {
						if (launched == false) {
							menuAnimation.createPlanetLaunchAnimation(planetMenu);
							//���Ƿ��䶯��
							for (SatelliteItemModel model : satelliteList) {
								menuAnimation.createSatelliteLaunchAnimation(model.getView());
							}
						} else {
							menuAnimation.createPlanetDrawBackAnimation(planetMenu);
							//�����ջض���
							for (SatelliteItemModel model : satelliteList) {
								menuAnimation.createSatelliteDrawBackAnimation(model.getView());
							}
						}
						launched = !launched;
						
					} finally {
						plusAnimationActive.set(false);
					}
				}
			}

		});
	}
	//自动散开的方法
	public Point autoExpand(){
		Log.e("autoExpand", "=======卫星菜单的坐标："+planetMenu.getLeft()+","+planetMenu.getTop());
		Point point= new Point(planetMenu.getLeft(), planetMenu.getTop());
		menuAnimation.createPlanetLaunchAnimation(planetMenu);
		//���Ƿ��䶯��
		for (SatelliteItemModel model : satelliteList) {
			menuAnimation.createSatelliteLaunchAnimation(model.getView());
		}
		launched=true;
		
		plusAnimationActive.set(false);
		return point;
		
	}
	/**
	 * ������ǰ�ť
	 * @param satelliteItemList
	 * @throws Exception
	 */
	private void addSatelliteItem (List<SatelliteItemModel> sateList, Map<String, Integer> planetPosition) throws Exception{
		this.removeView(planetMenu);
		
		//�����յ�
		new MyMathUtils().calcStopXY(satelliteList, originAngle, endAngle, satelliteDistance); 
		
		for (final SatelliteItemModel itemModel : satelliteList) {
			//����Model����
			itemModel.setOriginX(planetPosition.get("X"));
			itemModel.setOriginY(planetPosition.get("Y"));
			
			//view��������
			CircleImageView itemView = new CircleImageView(getContext());
			itemView.setTag(itemModel);
			itemView.setScaleType(ScaleType.CENTER);
			new ImageUtils().setImage(getContext(), itemView, itemModel);
			RelativeLayout.LayoutParams satellitelayoutPara = new RelativeLayout.LayoutParams(point.x/6, point.x/6);
			
			Map<String, Integer> planetWHMap = new ImageUtils().getImageWH(this.getContext(), itemView.getDrawable());
			Map<String, Integer> satelliteWHMap = new ImageUtils().getImageWH(this.getContext(), planetMenu.getDrawable());
			int adjustX = planetWHMap.get("WIDTH") / 2 - satelliteWHMap.get("WIDTH") / 2;
			int adjustY = planetWHMap.get("HEIGHT") / 2 - satelliteWHMap.get("HEIGHT") / 2;
			itemModel.setAdjustX(adjustX);
			itemModel.setAdjustY(adjustY);
			itemModel.setStopX(itemModel.getStopX() + planetPosition.get("X") - adjustX);
			itemModel.setStopY(itemModel.getStopY() + planetPosition.get("Y") - adjustY);
			
			satellitelayoutPara.leftMargin = itemModel.getStopX();
			satellitelayoutPara.topMargin = itemModel.getStopY();
			this.addView(itemView, satellitelayoutPara);
			itemView.setVisibility(View.GONE);
			itemModel.setView(itemView);
			

			//������ǰ�ť������
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					if (plusAnimationActive.compareAndSet(false, true)) {
						try {
							//���ü�����
							if (onSatelliteClickedListener != null) 
								onSatelliteClickedListener.onClick(view);
							
							//�������� ���� ����
							menuAnimation.createSatelliteItemClickedAnimation(view);
							menuAnimation.createPlanetItemClickedAnimation(planetMenu);
							
							//��Ϊ�ջ�״̬
							launched = false;
						
						} finally {
							plusAnimationActive.set(false);
						}
					}
				}
			});
		}
		menuAnimation.setSatelliteList(satelliteList);
		
		this.addView(planetMenu);
	}
	
	/**
	 * ����layout �߿�
	 */
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
		//setMeasuredDimension(layoutWidth, layoutHeight);
	}
	
	/**
	 * ����
	 * @param settingPara
	 * @throws Exception
	 */
	public void setting (SettingPara settingPara) throws Exception {
		
		this.endAngle = MyMathUtils.getAngle(settingPara.getEndAngle());
		this.originAngle = MyMathUtils.getAngle(settingPara.getOriginAngle());
		this.satelliteDistance = settingPara.getSatelliteDistance();
		this.customerRadiusAdjust = settingPara.getCustomerRadiusAdjust();
		
		
		//װ�ض���
		menuAnimation = settingPara.getMenuAnimation();
		menuAnimation.init(satelliteList, this);
		menuAnimation.setPlanetMenu(planetMenu);
		menuAnimation.setSatelliteDistance(satelliteDistance);
		
		//�������
		this.satelliteList.addAll(settingPara.getSatelliteList());
		
		//��������ͼ��
		if (settingPara.getPlanetImgResourceId() > 0) {
			new ImageUtils().setImage(planetMenu, settingPara.getPlanetImgResourceId());
		} else if (settingPara.getPlanetImgDrawable() != null) {
			new ImageUtils().setImage(planetMenu, settingPara.getPlanetImgDrawable());
		} else if (settingPara.getPlanetImgAssetPath() != null && settingPara.getPlanetImgAssetPath().length() > 0) {
			new ImageUtils().setImage(getContext(), planetMenu, settingPara.getPlanetImgAssetPath());
		}
		
		
		Map<String, String> map = null;
		int layoutWidth;
		int layoutHeight;
		try {
			//����뾶
			int satelliteLenght = new ImageUtils().getLongestImage(getContext(), satelliteList) * 2;
			int radius = satelliteDistance + satelliteLenght + customerRadiusAdjust;
			
			if (settingPara.getPlanetPosition() != null && settingPara.getPlanetPosition().length() > 0) {
				map = new MyMathUtils().getWidthHeightByPosition(settingPara.getPlanetPosition(), radius);
				layoutWidth = Integer.parseInt(map.get("WIDTH"));
				layoutHeight = Integer.parseInt(map.get("HEIGHT"));
			} else {
				map = new MyMathUtils().getWidthHeightNPosition(originAngle, endAngle, radius);
				layoutWidth = Integer.parseInt(map.get("WIDTH"));
				layoutHeight = Integer.parseInt(map.get("HEIGHT"));
				settingPara.setPlanetPosition(map.get("POSITION"));
			}
			
			//����parent layout ��, ��
			RelativeLayout.LayoutParams parentlayoutPara = (LayoutParams) getLayoutParams();
			parentlayoutPara.width = layoutWidth ;
			parentlayoutPara.height = layoutHeight;
			setLayoutParams(parentlayoutPara);
			
			//��������λ��
			Log.d("sun_yve", "position:" + settingPara.getPlanetPosition());
			Map<String, Integer> planetPosition = getPlanetPosition(settingPara.getPlanetPosition(), layoutWidth, layoutHeight);
			RelativeLayout.LayoutParams planetlayoutPara =  (LayoutParams) planetMenu.getLayoutParams();
			planetlayoutPara.leftMargin = planetPosition.get("X");
			planetlayoutPara.topMargin = planetPosition.get("Y");
			planetMenu.setLayoutParams(planetlayoutPara);
			//��������
			addSatelliteItem(satelliteList, planetPosition);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private Map<String, Integer> getPlanetPosition (String position, int layoutWidth, int layoutHeight) throws Exception {
		Map<String, Integer> hwMap = new ImageUtils().getImageWH(this.getContext(), planetMenu.getDrawable());
		
		Map<String, Integer> resultMap = new HashMap<String, Integer>();
		if (SettingPara.POSITION_TOP_LEFT.equals(position)) {
			resultMap.put("X", 0);
			resultMap.put("Y", 0);
		} else if (SettingPara.POSITION_TOP_RIGHT.equals(position)) {
			resultMap.put("X", layoutWidth - hwMap.get("WIDTH"));
			resultMap.put("Y", 0);
		} else if (SettingPara.POSITION_BOTTOM_LEFT.equals(position)) {
			resultMap.put("X", 0 );
			resultMap.put("Y", layoutHeight - hwMap.get("HEIGHT"));
		} else if (SettingPara.POSITION_BOTTOM_RIGHT.equals(position)) {
			resultMap.put("X", layoutWidth - hwMap.get("WIDTH"));
			resultMap.put("Y", layoutHeight - hwMap.get("HEIGHT"));
		} else if (SettingPara.POSITION_TOP_CENTER.equals(position)) {
			resultMap.put("X", layoutWidth / 2 - hwMap.get("WIDTH") / 2);
			resultMap.put("Y", 0);
		} else if (SettingPara.POSITION_BOTTOM_CENTER.equals(position)) {
			resultMap.put("X", layoutWidth / 2  - hwMap.get("WIDTH") / 2);
			resultMap.put("Y", layoutHeight - hwMap.get("HEIGHT"));
		} else if (SettingPara.POSITION_LEFT_CENTER.equals(position)) {
			resultMap.put("X", 0);
			resultMap.put("Y", layoutHeight / 2 - hwMap.get("HEIGHT") / 2);
		} else if (SettingPara.POSITION_RIGHT_CENTER.equals(position)) {
			resultMap.put("X", layoutWidth  - hwMap.get("WIDTH"));
			resultMap.put("Y", layoutHeight / 2 - hwMap.get("HEIGHT") / 2);
		} else if (SettingPara.POSITION_CENTER.equals(position)) {
			resultMap.put("X", layoutWidth / 2  - hwMap.get("WIDTH") / 2);
			resultMap.put("Y", layoutHeight / 2 - hwMap.get("HEIGHT") / 2);
		} else {
			throw new Exception ("(setViewPosition)there are no this planet position (" + position + ")") ;
		}
		
		return resultMap;
	}
	
	private void setViewPosition (String position, RelativeLayout.LayoutParams layoutPara) throws Exception {
		
		if (SettingPara.POSITION_TOP_LEFT.equals(position)) {
			layoutPara.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			layoutPara.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		} else if (SettingPara.POSITION_TOP_RIGHT.equals(position)) {
			layoutPara.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			layoutPara.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		} else if (SettingPara.POSITION_BOTTOM_LEFT.equals(position)) {
			layoutPara.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			layoutPara.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
		} else if (SettingPara.POSITION_BOTTOM_RIGHT.equals(position)) {
			layoutPara.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			layoutPara.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		} else if (SettingPara.POSITION_TOP_CENTER.equals(position)) {
			layoutPara.addRule(RelativeLayout.ALIGN_PARENT_TOP);
			layoutPara.addRule(RelativeLayout.CENTER_HORIZONTAL);
		} else if (SettingPara.POSITION_BOTTOM_CENTER.equals(position)) {
			layoutPara.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
			layoutPara.addRule(RelativeLayout.CENTER_HORIZONTAL);
		} else if (SettingPara.POSITION_LEFT_CENTER.equals(position)) {
			layoutPara.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			layoutPara.addRule(RelativeLayout.CENTER_VERTICAL);
		} else if (SettingPara.POSITION_RIGHT_CENTER.equals(position)) {
			layoutPara.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			layoutPara.addRule(RelativeLayout.CENTER_VERTICAL);
		} else if (SettingPara.POSITION_CENTER.equals(position)) {
			layoutPara.addRule(RelativeLayout.CENTER_IN_PARENT);
		} else {
			throw new Exception ("(setViewPosition)there are no this planet position (" + position + ")") ;
		}
		
	}
	
}
