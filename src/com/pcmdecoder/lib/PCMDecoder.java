package com.pcmdecoder.lib;

import android.util.Log;

public class PCMDecoder {
	private static String DBGTag = "GPComAir5Wrapper";
	
	// Native Library Start -------
	static {
		try {
			Log.i(DBGTag, "------------Trying to load PCM5Lib.so ...");
			System.loadLibrary("pcmdecoderlibrary");
		} catch (UnsatisfiedLinkError Ule) {
			Log.e(DBGTag, "----------Cannot load PCM5Lib.so ...");
			Ule.printStackTrace();
		} finally {
		}
	}
	
	private native int PCMLibInit();
	private native int PCMLibTerm();	
	private native int PCMLibSetDegug(boolean bDebug);
	private native int PCMLibDecode(short[] shBuffer, int len);	
	private native float PCMLibGetWater();
	private native float PCMLibGetOil();	
	private native float PCMLibGetTX();
	private native float PCMLibGetWaterOil();	
	private native String stringFromJNI();	
	
	public void Testso() {
		
	}
	
	public void init() {
		PCMLibInit();
	}
	
	public void term() {
		PCMLibTerm();
	}
	
	public void setDebug(boolean bDebug) {
		PCMLibSetDegug(bDebug);
	}
	
	public String test() {
		return stringFromJNI();
	}
	
	public int DecodeData(short[] shBuffer, int len) {
		return PCMLibDecode(shBuffer, len);
	}
	
	public float GetWater() {
		return PCMLibGetWater();
	}
	
	public float GetOil() {
		return PCMLibGetOil();
	}
	
	public float GetTX() {
		return PCMLibGetTX();
	}	
	
	public float GetWaterOil() {
		return PCMLibGetWaterOil();
	}
}
