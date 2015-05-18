package com.base.app.utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

public class GetAdressUtil {
	private Context context;
	
	public GetAdressUtil(Context context) {
		super();
		this.context = context;
	}

	public String requestLocation() {
		try {
			Location location = null;
			LocationManager locationManager = (LocationManager) context
					.getSystemService(Context.LOCATION_SERVICE);
			if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
				location = locationManager
						.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location != null) {
					return getLocationInfo(location);
				}
			} else {
				LocationListener locationListener = new LocationListener() {

					// Provider被enable时触发此函数，比如GPS被打开
					@Override
					public void onProviderEnabled(String arg0) {

					}

					// Provider被disable时触发此函数，比如GPS被关闭
					@Override
					public void onProviderDisabled(String arg0) {

					}

					// 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
					@Override
					public void onLocationChanged(Location arg0) {

					}

					// Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
					@Override
					public void onStatusChanged(String arg0, int arg1,
							Bundle arg2) {

					}
				};
				locationManager.requestLocationUpdates(
						LocationManager.NETWORK_PROVIDER, 1000, 0,
						locationListener);
				location = locationManager
						.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
				if (location != null) {
					
					return getLocationInfo(location);
				}
			}

		} catch (Exception e) {
			
		}
		return "北京";

	}

	private String getLocationInfo(Location location) {
		Geocoder geocoder = new Geocoder(context, Locale.getDefault());
		try {
			List<Address> addresses = geocoder.getFromLocation(
					location.getLatitude(), location.getLongitude(), 1);
			if (addresses.size() > 0) {
				Address address = addresses.get(0);
				Log.e("位置信息11：", "=============城市名："+address.getAdminArea());
				return address.getAdminArea();
			}
			return "北京";

		} catch (IOException e) {
			e.printStackTrace();
			return "北京";
		}
	}
}
