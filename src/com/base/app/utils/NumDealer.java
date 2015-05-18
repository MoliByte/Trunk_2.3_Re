package com.base.app.utils;

import java.text.DecimalFormat;

public class NumDealer {
	//首页精选数据格式处理
	public static String getTestNum(String n){
		try{
			String tem=n;
			if(n.endsWith("%")){
				tem=n.replace("%", "");
			}
			float n1=Math.abs(Float.parseFloat(tem));
			
			if(n1==0){
				return "0";
			}
			
			
			
			DecimalFormat df = new DecimalFormat("#.0");
			String ns=df.format(n1);
			
			float r=Float.parseFloat(ns);
			
			if(r<1){
				return "0"+ns;
			}
			
			return ns;
		}catch(Exception e){
			e.printStackTrace();
			return 0+"";
		}
	}
	//首页动态弹性数据处理
	public static  String dealFlexibleNum(String n){
		try{
			String tem=n;
			if(n.endsWith("%")){
				tem=n.replace("%", "");
			}
			float n1=Float.parseFloat(tem);
			int n2=(int)n1;
			if(n1>n1+0.5){
				return n2+1+"";
			}
			return n2+"";
		}catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}
	//首页动态水油数据格式处理
	public static String dealNum(String n1){
		try{
			String tem=n1;
			if(n1.endsWith("%")){
				tem=n1.replace("%", "");
			}
			
			float f=Float.parseFloat(tem);
			
			DecimalFormat df = new DecimalFormat("#.0");
			String ns=df.format(f);
			return ns;
			
		}catch(Exception e){
			e.printStackTrace();
			return "0";
		}
	}
}
