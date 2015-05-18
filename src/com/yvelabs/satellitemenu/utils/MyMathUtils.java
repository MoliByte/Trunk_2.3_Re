package com.yvelabs.satellitemenu.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.yvelabs.satellitemenu.SatelliteItemModel;
import com.yvelabs.satellitemenu.SettingPara;

public class MyMathUtils {
	
	public static int getRandom(int min, int max) {
		return  new Random().nextInt(max) % (max - min + 1) + min;
	}
	
	public static int getAngle (int ang) {
		return ang % 360;
	}
	
	public static int getIncludedAngle (int startAngle, int endAngle) {
		return getAngle(360 - startAngle + endAngle);
	}

	public int getAverageAngle(int angle, int itemCount) {
		if (angle > 350) {
			return angle / itemCount;
		} else {
			return angle / (itemCount - 1);
		}
		
	}

	public static int getStopX(float angle, int distance) {
		return Double.valueOf(
				Math.rint(distance * Math.cos(Math.toRadians(angle)))).intValue();
	}

	public static int getStopY(float angle, int distance) {
		return Double.valueOf(
				Math.rint(-1 * distance * Math.sin(Math.toRadians(angle)))).intValue();
	}
	public void calcStopXY(List<SatelliteItemModel> satelliteList,
			int originAngle, int endAngle, int distance) {
		int aveAngle = Math.abs(getAverageAngle(getIncludedAngle(originAngle, endAngle),
				satelliteList.size()));

		int index = 0;
		for (SatelliteItemModel itemModel : satelliteList) {
			int x = getStopX(originAngle + (aveAngle * index), distance);
			int y = getStopY(originAngle + (aveAngle * index), distance);
			itemModel.setStopX(x);
			itemModel.setStopY(y);
			index++;
		}
	}

	public int getOriginQuadrant(int angle) {
		if (angle >= 0 && angle < 90) {
			return 1;
		} else if (angle >= 90 && angle < 180) {
			return 2;
		} else if (angle >= 180 && angle < 270) {
			return 3;
		} else if (angle >= 270 && angle < 360) {
			return 4;
		}

		return 0;
	}

	public int getEndQuadrant(int angle) {
		if (angle >= 0 && angle <= 90) {
			return 1;
		} else if (angle > 90 && angle <= 180) {
			return 2;
		} else if (angle > 180 && angle <= 270) {
			return 3;
		} else if (angle > 270 && angle <= 360) {
			return 4;
		}

		return 0;
	}
	
	public int getLonger(int x, int y) {
		if (x > y) {
			return x;
		} else {
			return y;
		}
	}
	
	public List<Integer> getCoverQuadrants (int startAngle, int endAngle) {
		List<Integer> resultList = new ArrayList<Integer>();

		//��ʼ����
		int quadrant = getOriginQuadrant(startAngle);
		resultList.add(quadrant);
		
		//�������
		int tempAngle = 1;
		int IncludedAngle = getIncludedAngle(startAngle, endAngle);
		
		while (tempAngle <= IncludedAngle) {
			int currentQuadrant = getEndQuadrant(getAngle(startAngle + tempAngle));
			
			if (!ObjectsUtils.containsInt(resultList, currentQuadrant)) 
				resultList.add(currentQuadrant);
			tempAngle ++;
		}
		
		return resultList;
	}
	
	public Map<String, String> getWidthHeightNPosition (int startAngle, int endAngle, int radius) {
		Map<String, String> resultMap = new HashMap<String, String>();
		List<Integer> quadrants = getCoverQuadrants(startAngle, endAngle);

		if (quadrants.size() >= 3) {
			resultMap.put("WIDTH", String.valueOf(radius * 2));
			resultMap.put("HEIGHT", String.valueOf(radius * 2));
			resultMap.put("POSITION", SettingPara.POSITION_CENTER);
		} else if (quadrants.size() == 2) {
			int firstQua = quadrants.get(0);
			int secondQua = quadrants.get(1);
			
			if (firstQua == 1 && secondQua == 2) {
				resultMap.put("WIDTH", String.valueOf(radius * 2));
				resultMap.put("HEIGHT", String.valueOf(radius));
				resultMap.put("POSITION", SettingPara.POSITION_BOTTOM_CENTER);
			} else if (firstQua == 2 && secondQua == 3) {
				resultMap.put("WIDTH", String.valueOf(radius));
				resultMap.put("HEIGHT", String.valueOf(radius * 2));
				resultMap.put("POSITION", SettingPara.POSITION_RIGHT_CENTER);
			} else if (firstQua == 3 && secondQua == 4) {
				resultMap.put("WIDTH", String.valueOf(radius * 2));
				resultMap.put("HEIGHT", String.valueOf(radius));
				resultMap.put("POSITION", SettingPara.POSITION_TOP_CENTER);
			} else if (firstQua == 4 && secondQua == 1) {
				resultMap.put("WIDTH", String.valueOf(radius));
				resultMap.put("HEIGHT", String.valueOf(radius * 2));
				resultMap.put("POSITION", SettingPara.POSITION_LEFT_CENTER);
			}
			
		} else if (quadrants.size() == 1) {
			resultMap.put("WIDTH", String.valueOf(radius));
			resultMap.put("HEIGHT", String.valueOf(radius));
			
			int qua = quadrants.get(0);
			if (qua == 1) {
				resultMap.put("POSITION", SettingPara.POSITION_BOTTOM_LEFT);
			} else if (qua == 2) {
				resultMap.put("POSITION", SettingPara.POSITION_BOTTOM_RIGHT);
			} else if (qua == 3) {
				resultMap.put("POSITION", SettingPara.POSITION_TOP_RIGHT);
			} else if (qua == 4) {
				resultMap.put("POSITION", SettingPara.POSITION_TOP_LEFT);
			}
		}
		
		return resultMap;
	}
	
	/**
	 * ���� parent layout �Ŀ��
	 * @param position
	 * @param radius
	 * @return
	 * @throws Exception
	 */
	public Map<String, String> getWidthHeightByPosition (String position, int radius) throws Exception {
		Map<String, String> resultMap = new HashMap<String, String>();
		if (SettingPara.POSITION_TOP_LEFT.equals(position)) {
			resultMap.put("WIDTH", String.valueOf(radius));
			resultMap.put("HEIGHT", String.valueOf(radius));
		} else if (SettingPara.POSITION_TOP_RIGHT.equals(position)) {
			resultMap.put("WIDTH", String.valueOf(radius));
			resultMap.put("HEIGHT", String.valueOf(radius));
		} else if (SettingPara.POSITION_BOTTOM_LEFT.equals(position)) {
			resultMap.put("WIDTH", String.valueOf(radius));
			resultMap.put("HEIGHT", String.valueOf(radius));
		} else if (SettingPara.POSITION_BOTTOM_RIGHT.equals(position)) {
			resultMap.put("WIDTH", String.valueOf(radius));
			resultMap.put("HEIGHT", String.valueOf(radius));
		} else if (SettingPara.POSITION_TOP_CENTER.equals(position)) {
			resultMap.put("WIDTH", String.valueOf(radius * 2));
			resultMap.put("HEIGHT", String.valueOf(radius));
		} else if (SettingPara.POSITION_BOTTOM_CENTER.equals(position)) {
			resultMap.put("WIDTH", String.valueOf(radius * 2));
			resultMap.put("HEIGHT", String.valueOf(radius));
		} else if (SettingPara.POSITION_LEFT_CENTER.equals(position)) {
			resultMap.put("WIDTH", String.valueOf(radius));
			resultMap.put("HEIGHT", String.valueOf(radius * 2));
		} else if (SettingPara.POSITION_RIGHT_CENTER.equals(position)) {
			resultMap.put("WIDTH", String.valueOf(radius));
			resultMap.put("HEIGHT", String.valueOf(radius * 2));
		} else if (SettingPara.POSITION_CENTER.equals(position)) {
			resultMap.put("WIDTH", String.valueOf(radius * 2));
			resultMap.put("HEIGHT", String.valueOf(radius * 2));
		} else {
			throw new Exception ("(getWidthHeightByPosition)there are no this planet position (" + position + ")") ;
		}
		
		return resultMap;
	}
}
