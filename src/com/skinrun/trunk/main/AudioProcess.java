package com.skinrun.trunk.main;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.pcmdecoder.lib.PCMDecoder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.SurfaceView;

public class AudioProcess {
	private float PI = 3.1415926f;	
	private String TAG = "AudioProcess";
	public int frequence = 0;
	public boolean isLinked = false;
	public short mnMax = 32767;
	public short mnMin = -32767;
	Handler m_handler;
	
	private int mnFileIndex = 0;
	boolean m_bSaveFile = true;					// 保存到文件以便调试
	private boolean mbRecording = false;	
	private boolean mbPause = false;
	int minBufferSize;
	static int frequency = 44100;		// 采样频率
	static final int channelConfiguration = AudioFormat.CHANNEL_CONFIGURATION_MONO;//AudioFormat.CHANNEL_CONFIGURATION_STEREO;//AudioFormat.CHANNEL_CONFIGURATION_MONO;  
	static final int audioEncodeing = AudioFormat.ENCODING_PCM_16BIT; 
	public static AudioRecord audioRecord;		// AudioRecord对象
	
	
	int mnTestEm = 0;			// 电压
	float mfMoist = 0.0f;
	float mfOil = 0.0f;
	float mfSoft = 0.0f;	
	float fTableMoist [];
	float fTableOil [];
	float fTableSoft [];
	
	// 2014217
	float fTableDCV[];					// 表格
	float fTableWater [];
//	float fTableOil[];
	float fTableTX[];
	float fTableWater_Oil[];			// Water/Oil;
	int mnDCVIndex = 0;
	float mfDCVy = 0.0f;
	float mfWater = 0.0f;
//	float mfOil = 0.0f;
	float mfTX = 0.0f;
	float mfWater_Oil = 0.0f;
	
	
	// 新加，兼容S4，调用so库解码，后期全部整合
	boolean mbSM_G9006V = false;
	PCMDecoder mDecoder;	
	
	public AudioProcess() {
		// 新加，兼容S4，调用so库解码，后期全部整合	
		mbSM_G9006V = android.os.Build.MODEL.equals("SM-G9006V");
		//mbSM_G9006V = android.os.Build.MODEL.equals("Nexus 5");	
		//Log.i("", android.os.Build.MODEL);
		mDecoder = new PCMDecoder();
		mDecoder.init();
		mDecoder.setDebug(true);
		Log.i("sdfsdfsdf", mDecoder.test());
		
		
		initTable();
		// 启动录音线程
        minBufferSize = AudioRecord.getMinBufferSize(frequency, 
        		AudioFormat.CHANNEL_CONFIGURATION_MONO,
        		audioEncodeing);
        if(null == audioRecord){
	        audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency,
	        		AudioFormat.CHANNEL_CONFIGURATION_MONO,
	        		audioEncodeing,
	        		minBufferSize*10);
        }
	}
	
	//测试AudioProcess 单实例
	public static AudioProcess audioProcess ;
	
	public static AudioProcess getInsProcess(){
		if(audioProcess == null){
			audioProcess = new AudioProcess();
		}
		return audioProcess ;
	}
		
	public void setHandle(Handler handler) {
		m_handler =  handler;
	}
	
	// 初始化表格
	public void initTable() {
		fTableDCV = new float[93];
		fTableDCV[0] = 0.0066f;
		fTableDCV[1] = 0.0077f;
		fTableDCV[2] = 0.022f;
		fTableDCV[3] = 0.0275f;
		fTableDCV[4] = 0.0352f;
		fTableDCV[5] = 0.0385f;
		fTableDCV[6] = 0.0418f;
		fTableDCV[7] = 0.044f;
		fTableDCV[8] = 0.0495f;
		fTableDCV[9] = 0.055f;
		fTableDCV[10] = 0.0583f;
		fTableDCV[11] = 0.0627f;
		fTableDCV[12] = 0.0671f;
		fTableDCV[13] = 0.0704f;
		fTableDCV[14] = 0.0737f;
		fTableDCV[15] = 0.077f;
		fTableDCV[16] = 0.0825f;
		fTableDCV[17] = 0.0858f;
		fTableDCV[18] = 0.0902f;
		fTableDCV[19] = 0.0935f;
		fTableDCV[20] = 0.0968f;
		fTableDCV[21] = 0.099f;
		fTableDCV[22] = 0.121f;
		fTableDCV[23] = 0.132f;
		fTableDCV[24] = 0.154f;
		fTableDCV[25] = 0.176f;
		fTableDCV[26] = 0.187f;
		fTableDCV[27] = 0.209f;
		fTableDCV[28] = 0.231f;
		fTableDCV[29] = 0.242f;
		fTableDCV[30] = 0.264f;
		fTableDCV[31] = 0.286f;
		fTableDCV[32] = 0.297f;
		fTableDCV[33] = 0.319f;
		fTableDCV[34] = 0.341f;
		fTableDCV[35] = 0.352f;
		fTableDCV[36] = 0.374f;
		fTableDCV[37] = 0.396f;
		fTableDCV[38] = 0.418f;
		fTableDCV[39] = 0.44f;
		fTableDCV[40] = 0.451f;
		fTableDCV[41] = 0.462f;
		fTableDCV[42] = 0.484f;
		fTableDCV[43] = 0.495f;
		fTableDCV[44] = 0.517f;
		fTableDCV[45] = 0.539f;
		fTableDCV[46] = 0.561f;
		fTableDCV[47] = 0.572f;
		fTableDCV[48] = 0.594f;
		fTableDCV[49] = 0.605f;
		fTableDCV[50] = 0.627f;
		fTableDCV[51] = 0.649f;
		fTableDCV[52] = 0.671f;
		fTableDCV[53] = 0.682f;
		fTableDCV[54] = 0.704f;
		fTableDCV[55] = 0.726f;
		fTableDCV[56] = 0.7326f;
		fTableDCV[57] = 0.7414f;
		fTableDCV[58] = 0.7458f;
		fTableDCV[59] = 0.7502f;
		fTableDCV[60] = 0.7546f;
		fTableDCV[61] = 0.759f;
		fTableDCV[62] = 0.7645f;
		fTableDCV[63] = 0.77f;
		fTableDCV[64] = 0.7755f;
		fTableDCV[65] = 0.781f;
		fTableDCV[66] = 0.7865f;
		fTableDCV[67] = 0.7898f;
		fTableDCV[68] = 0.792f;
		fTableDCV[69] = 0.7953f;
		fTableDCV[70] = 0.7997f;
		fTableDCV[71] = 0.803f;
		fTableDCV[72] = 0.8074f;
		fTableDCV[73] = 0.8118f;
		fTableDCV[74] = 0.8162f;
		fTableDCV[75] = 0.8195f;
		fTableDCV[76] = 0.8228f;
		fTableDCV[77] = 0.8283f;
		fTableDCV[78] = 0.8316f;
		fTableDCV[79] = 0.836f;
		fTableDCV[80] = 0.8393f;
		fTableDCV[81] = 0.8426f;
		fTableDCV[82] = 0.8448f;
		fTableDCV[83] = 0.847f;
		fTableDCV[84] = 0.8492f;
		fTableDCV[85] = 0.8514f;
		fTableDCV[86] = 0.8536f;
		fTableDCV[87] = 0.8558f;
		fTableDCV[88] = 0.858f;
		fTableDCV[89] = 0.8635f;
		fTableDCV[90] = 0.869f;
		fTableDCV[91] = 0.88f;
		fTableDCV[92] = 1.2f;
		
		fTableWater = new float[93];
		fTableWater[0] = 5;
		fTableWater[1] = 10;
		fTableWater[2] = 11;
		fTableWater[3] = 12;
		fTableWater[4] = 13;
		fTableWater[5] = 14;
		fTableWater[6] = 15;
		fTableWater[7] = 16;
		fTableWater[8] = 17;
		fTableWater[9] = 18;
		fTableWater[10] = 19;
		fTableWater[11] = 20;
		fTableWater[12] = 21;
		fTableWater[13] = 22;
		fTableWater[14] = 23;
		fTableWater[15] = 24;
		fTableWater[16] = 25;
		fTableWater[17] = 26;
		fTableWater[18] = 27;
		fTableWater[19] = 28;
		fTableWater[20] = 29;
		fTableWater[21] = 30;
		fTableWater[22] = 31;
		fTableWater[23] = 32;
		fTableWater[24] = 33;
		fTableWater[25] = 34;
		fTableWater[26] = 35;
		fTableWater[27] = 36;
		fTableWater[28] = 37;
		fTableWater[29] = 38;
		fTableWater[30] = 39;
		fTableWater[31] = 40;
		fTableWater[32] = 41;
		fTableWater[33] = 42;
		fTableWater[34] = 43;
		fTableWater[35] = 44;
		fTableWater[36] = 45;
		fTableWater[37] = 46;
		fTableWater[38] = 47;
		fTableWater[39] = 48;
		fTableWater[40] = 49;
		fTableWater[41] = 50;
		fTableWater[42] = 51;
		fTableWater[43] = 52;
		fTableWater[44] = 53;
		fTableWater[45] = 54;
		fTableWater[46] = 55;
		fTableWater[47] = 56;
		fTableWater[48] = 57;
		fTableWater[49] = 58;
		fTableWater[50] = 59;
		fTableWater[51] = 60;
		fTableWater[52] = 61;
		fTableWater[53] = 62;
		fTableWater[54] = 63;
		fTableWater[55] = 64;
		fTableWater[56] = 65;
		fTableWater[57] = 66;
		fTableWater[58] = 67;
		fTableWater[59] = 68;
		fTableWater[60] = 69;
		fTableWater[61] = 70;
		fTableWater[62] = 71;
		fTableWater[63] = 72;
		fTableWater[64] = 73;
		fTableWater[65] = 74;
		fTableWater[66] = 75;
		fTableWater[67] = 76;
		fTableWater[68] = 77;
		fTableWater[69] = 78;
		fTableWater[70] = 79;
		fTableWater[71] = 80;
		fTableWater[72] = 81;
		fTableWater[73] = 82;
		fTableWater[74] = 83;
		fTableWater[75] = 84;
		fTableWater[76] = 85;
		fTableWater[77] = 86;
		fTableWater[78] = 87;
		fTableWater[79] = 88;
		fTableWater[80] = 89;
		fTableWater[81] = 90;
		fTableWater[82] = 91;
		fTableWater[83] = 92;
		fTableWater[84] = 93;
		fTableWater[85] = 94;
		fTableWater[86] = 95;
		fTableWater[87] = 96;
		fTableWater[88] = 97;
		fTableWater[89] = 98;
		fTableWater[90] = 99;
		fTableWater[91] = 99.5f;
		fTableWater[92] = 100;
		
		fTableOil = new float[93];
		fTableOil[0] = 2.235835979f;
		fTableOil[1] = 4.471671958f;
		fTableOil[2] = 4.918839154f;
		fTableOil[3] = 5.36600635f;
		fTableOil[4] = 5.813173546f;
		fTableOil[5] = 6.260340741f;
		fTableOil[6] = 6.707507937f;
		fTableOil[7] = 7.154675133f;
		fTableOil[8] = 7.601842329f;
		fTableOil[9] = 8.049009525f;
		fTableOil[10] = 8.49617672f;
		fTableOil[11] = 8.943343916f;
		fTableOil[12] = 9.390511112f;
		fTableOil[13] = 9.837678308f;
		fTableOil[14] = 10.2848455f;
		fTableOil[15] = 10.7320127f;
		fTableOil[16] = 11.1791799f;
		fTableOil[17] = 11.62634709f;
		fTableOil[18] = 12.07351429f;
		fTableOil[19] = 12.52068148f;
		fTableOil[20] = 12.96784868f;
		fTableOil[21] = 13.41501587f;
		fTableOil[22] = 13.8982291f;
		fTableOil[23] = 14.34655907f;
		fTableOil[24] = 14.79488904f;
		fTableOil[25] = 15.24321901f;
		fTableOil[26] = 15.69154898f;
		fTableOil[27] = 16.14566982f;
		fTableOil[28] = 16.59416065f;
		fTableOil[29] = 17.04265148f;
		fTableOil[30] = 17.49114231f;
		fTableOil[31] = 17.93963313f;
		fTableOil[32] = 18.38812396f;
		fTableOil[33] = 18.85775862f;
		fTableOil[34] = 19.30675287f;
		fTableOil[35] = 19.75574713f;
		fTableOil[36] = 20.20474138f;
		fTableOil[37] = 20.65373563f;
		fTableOil[38] = 21.10746845f;
		fTableOil[39] = 21.55656352f;
		fTableOil[40] = 22.0056586f;
		fTableOil[41] = 22.45475367f;
		fTableOil[42] = 22.90384874f;
		fTableOil[43] = 23.35294382f;
		fTableOil[44] = 23.80524614f;
		fTableOil[45] = 24.25440172f;
		fTableOil[46] = 24.70355731f;
		fTableOil[47] = 25.1527129f;
		fTableOil[48] = 25.60186849f;
		fTableOil[49] = 26.05453484f;
		fTableOil[50] = 26.50375095f;
		fTableOil[51] = 26.95296707f;
		fTableOil[52] = 27.40218319f;
		fTableOil[53] = 27.85139931f;
		fTableOil[54] = 28.3069734f;
		fTableOil[55] = 28.75629044f;
		fTableOil[56] = 29.20560748f;
		fTableOil[57] = 29.65492451f;
		fTableOil[58] = 30.10424155f;
		fTableOil[59] = 30.55355859f;
		fTableOil[60] = 31.00287563f;
		fTableOil[61] = 31.45219267f;
		fTableOil[62] = 31.90150971f;
		fTableOil[63] = 32.35082674f;
		fTableOil[64] = 32.80014378f;
		fTableOil[65] = 33.24946082f;
		fTableOil[66] = 33.69877786f;
		fTableOil[67] = 34.1480949f;
		fTableOil[68] = 34.59741193f;
		fTableOil[69] = 35.04672897f;
		fTableOil[70] = 35.49604601f;
		fTableOil[71] = 35.95667221f;
		fTableOil[72] = 36.40613061f;
		fTableOil[73] = 36.85558902f;
		fTableOil[74] = 37.30504742f;
		fTableOil[75] = 37.75450582f;
		fTableOil[76] = 38.20396422f;
		fTableOil[77] = 38.65342263f;
		fTableOil[78] = 39.10288103f;
		fTableOil[79] = 39.55233943f;
		fTableOil[80] = 40.00179783f;
		fTableOil[81] = 40.45125624f;
		fTableOil[82] = 40.90071464f;
		fTableOil[83] = 41.35017304f;
		fTableOil[84] = 41.79963144f;
		fTableOil[85] = 42.24908985f;
		fTableOil[86] = 42.69854825f;
		fTableOil[87] = 43.14800665f;
		fTableOil[88] = 43.59746505f;
		fTableOil[89] = 44.04692346f;
		fTableOil[90] = 44.49638186f;
		fTableOil[91] = 44.94584026f;
		fTableOil[92] = 44.94584026f;
		
		fTableTX = new float[93];
		fTableTX[0] = 4.014164021f;
		fTableTX[1] = 7.469369047f;
		fTableTX[2] = 8.160410052f;
		fTableTX[3] = 8.851451058f;
		fTableTX[4] = 9.542492063f;
		fTableTX[5] = 10.23353307f;
		fTableTX[6] = 10.92457407f;
		fTableTX[7] = 11.61561508f;
		fTableTX[8] = 12.30665608f;
		fTableTX[9] = 12.99769709f;
		fTableTX[10] = 13.68873809f;
		fTableTX[11] = 14.3797791f;
		fTableTX[12] = 15.0708201f;
		fTableTX[13] = 15.76186111f;
		fTableTX[14] = 16.45290212f;
		fTableTX[15] = 17.14394312f;
		fTableTX[16] = 17.83498413f;
		fTableTX[17] = 18.52602513f;
		fTableTX[18] = 19.21706614f;
		fTableTX[19] = 19.90810714f;
		fTableTX[20] = 20.59914815f;
		fTableTX[21] = 21.29018915f;
		fTableTX[22] = 21.93762609f;
		fTableTX[23] = 22.62721363f;
		fTableTX[24] = 23.31680117f;
		fTableTX[25] = 24.0063887f;
		fTableTX[26] = 24.69597624f;
		fTableTX[27] = 25.37852626f;
		fTableTX[28] = 26.06791272f;
		fTableTX[29] = 26.75729919f;
		fTableTX[30] = 27.44668565f;
		fTableTX[31] = 28.13607212f;
		fTableTX[32] = 28.82545858f;
		fTableTX[33] = 29.48904454f;
		fTableTX[34] = 30.17780172f;
		fTableTX[35] = 30.86655891f;
		fTableTX[36] = 31.55531609f;
		fTableTX[37] = 32.24407328f;
		fTableTX[38] = 32.92703328f;
		fTableTX[39] = 33.61566444f;
		fTableTX[40] = 34.30429559f;
		fTableTX[41] = 34.99292675f;
		fTableTX[42] = 35.68155791f;
		fTableTX[43] = 36.37018907f;
		fTableTX[44] = 37.05488681f;
		fTableTX[45] = 37.74344233f;
		fTableTX[46] = 38.43199784f;
		fTableTX[47] = 39.12055336f;
		fTableTX[48] = 39.80910888f;
		fTableTX[49] = 40.4933516f;
		fTableTX[50] = 41.18183145f;
		fTableTX[51] = 41.87031131f;
		fTableTX[52] = 42.55879116f;
		fTableTX[53] = 43.24727101f;
		fTableTX[54] = 43.92792955f;
		fTableTX[55] = 44.61628325f;
		fTableTX[56] = 45.30463695f;
		fTableTX[57] = 45.99299065f;
		fTableTX[58] = 46.68134436f;
		fTableTX[59] = 47.36969806f;
		fTableTX[60] = 48.05805176f;
		fTableTX[61] = 48.74640546f;
		fTableTX[62] = 49.43475917f;
		fTableTX[63] = 50.12311287f;
		fTableTX[64] = 50.81146657f;
		fTableTX[65] = 51.49982027f;
		fTableTX[66] = 52.18817398f;
		fTableTX[67] = 52.87652768f;
		fTableTX[68] = 53.56488138f;
		fTableTX[69] = 54.25323508f;
		fTableTX[70] = 54.94158879f;
		fTableTX[71] = 55.61598274f;
		fTableTX[72] = 56.30415974f;
		fTableTX[73] = 56.99233673f;
		fTableTX[74] = 57.68051373f;
		fTableTX[75] = 58.36869073f;
		fTableTX[76] = 59.05686772f;
		fTableTX[77] = 59.74504472f;
		fTableTX[78] = 60.43322172f;
		fTableTX[79] = 61.12139871f;
		fTableTX[80] = 61.80957571f;
		fTableTX[81] = 62.49775271f;
		fTableTX[82] = 63.1859297f;
		fTableTX[83] = 63.8741067f;
		fTableTX[84] = 64.5622837f;
		fTableTX[85] = 65.25046069f;
		fTableTX[86] = 65.93863769f;
		fTableTX[87] = 66.62681469f;
		fTableTX[88] = 67.31499169f;
		fTableTX[89] = 68.00316868f;
		fTableTX[90] = 68.69134568f;
		fTableTX[91] = 69.37952268f;
		fTableTX[92] = 69.37952268f;
		
		fTableWater_Oil = new float[93];
		fTableWater_Oil[0] = 2.2363f;
		fTableWater_Oil[1] = 2.2363f;		
		fTableWater_Oil[2] = 2.2363f;		
		fTableWater_Oil[3] = 2.2363f;		
		fTableWater_Oil[4] = 2.2363f;		
		fTableWater_Oil[5] = 2.2363f;		
		fTableWater_Oil[6] = 2.2363f;		
		fTableWater_Oil[7] = 2.2363f;		
		fTableWater_Oil[8] = 2.2363f;		
		fTableWater_Oil[9] = 2.2363f;		
		fTableWater_Oil[10] = 2.2363f;		
		fTableWater_Oil[11] = 2.2363f;		
		fTableWater_Oil[12] = 2.2363f;		
		fTableWater_Oil[13] = 2.2363f;		
		fTableWater_Oil[14] = 2.2363f;		
		fTableWater_Oil[15] = 2.2363f;		
		fTableWater_Oil[16] = 2.2363f;		
		fTableWater_Oil[17] = 2.2363f;		
		fTableWater_Oil[18] = 2.2363f;		
		fTableWater_Oil[19] = 2.2363f;		
		fTableWater_Oil[20] = 2.2363f;		
		fTableWater_Oil[21] = 2.2363f;		
		fTableWater_Oil[22] = 2.2305f;		
		fTableWater_Oil[23] = 2.2305f;		
		fTableWater_Oil[24] = 2.2305f;		
		fTableWater_Oil[25] = 2.2305f;		
		fTableWater_Oil[26] = 2.2305f;		
		fTableWater_Oil[27] = 2.2297f;		
		fTableWater_Oil[28] = 2.2297f;		
		fTableWater_Oil[29] = 2.2297f;		
		fTableWater_Oil[30] = 2.2297f;		
		fTableWater_Oil[31] = 2.2297f;		
		fTableWater_Oil[32] = 2.2297f;		
		fTableWater_Oil[33] = 2.2272f;		
		fTableWater_Oil[34] = 2.2272f;		
		fTableWater_Oil[35] = 2.2272f;		
		fTableWater_Oil[36] = 2.2272f;		
		fTableWater_Oil[37] = 2.2272f;		
		fTableWater_Oil[38] = 2.2267f;		
		fTableWater_Oil[39] = 2.2267f;		
		fTableWater_Oil[40] = 2.2267f;		
		fTableWater_Oil[41] = 2.2267f;		
		fTableWater_Oil[42] = 2.2267f;		
		fTableWater_Oil[43] = 2.2267f;		
		fTableWater_Oil[44] = 2.2264f;			
		fTableWater_Oil[45] = 2.2264f;			
		fTableWater_Oil[46] = 2.2264f;			
		fTableWater_Oil[47] = 2.2264f;			
		fTableWater_Oil[48] = 2.2264f;			
		fTableWater_Oil[49] = 2.2261f;		
		fTableWater_Oil[50] = 2.2261f;		
		fTableWater_Oil[51] = 2.2261f;		
		fTableWater_Oil[52] = 2.2261f;		
		fTableWater_Oil[53] = 2.2261f;		
		fTableWater_Oil[54] = 2.2256f;		
		fTableWater_Oil[55] = 2.2256f;		
		fTableWater_Oil[56] = 2.2256f;		
		fTableWater_Oil[57] = 2.2256f;		
		fTableWater_Oil[58] = 2.2256f;		
		fTableWater_Oil[59] = 2.2256f;		
		fTableWater_Oil[60] = 2.2256f;		
		fTableWater_Oil[61] = 2.2256f;		
		fTableWater_Oil[62] = 2.2256f;		
		fTableWater_Oil[63] = 2.2256f;		
		fTableWater_Oil[64] = 2.2256f;		
		fTableWater_Oil[65] = 2.2256f;		
		fTableWater_Oil[66] = 2.2256f;		
		fTableWater_Oil[67] = 2.2256f;		
		fTableWater_Oil[68] = 2.2256f;		
		fTableWater_Oil[69] = 2.2256f;		
		fTableWater_Oil[70] = 2.2256f;		
		fTableWater_Oil[71] = 2.2249f;		
		fTableWater_Oil[72] = 2.2249f;		
		fTableWater_Oil[73] = 2.2249f;		
		fTableWater_Oil[74] = 2.2249f;		
		fTableWater_Oil[75] = 2.2249f;		
		fTableWater_Oil[76] = 2.2249f;		
		fTableWater_Oil[77] = 2.2249f;		
		fTableWater_Oil[78] = 2.2249f;		
		fTableWater_Oil[79] = 2.2249f;		
		fTableWater_Oil[80] = 2.2249f;		
		fTableWater_Oil[81] = 2.2249f;		
		fTableWater_Oil[82] = 2.2249f;		
		fTableWater_Oil[83] = 2.2249f;		
		fTableWater_Oil[84] = 2.2249f;		
		fTableWater_Oil[85] = 2.2249f;		
		fTableWater_Oil[86] = 2.2249f;		
		fTableWater_Oil[87] = 2.2249f;		
		fTableWater_Oil[88] = 2.2249f;		
		fTableWater_Oil[89] = 2.2249f;		
		fTableWater_Oil[90] = 2.2249f;		
		fTableWater_Oil[91] = 2.2249f;	
		fTableWater_Oil[92] = 2.2249f;			
		
		
/*		fTableMoist = new float[101];
		fTableMoist[0] = 1100.0f;
		fTableMoist[1] = 1080.4f;
		fTableMoist[2] = 1062.8f;
		fTableMoist[3] = 1045.2f;
		fTableMoist[4] = 1027.6f;
		fTableMoist[5] = 1010.0f;
		fTableMoist[6] = 992.4f;
		fTableMoist[7] = 974.8f;
		fTableMoist[8] = 957.2f;
		fTableMoist[9] = 939.6f;
		fTableMoist[10] = 922.0f;
		fTableMoist[11] = 904.4f;
		fTableMoist[12] = 886.8f;
		fTableMoist[13] = 869.2f;
		fTableMoist[14] = 851.6f;
		fTableMoist[15] = 834.0f;
		fTableMoist[16] = 816.4f;
		fTableMoist[17] = 798.8f;
		fTableMoist[18] = 781.2f;
		fTableMoist[19] = 733.9f;
		fTableMoist[20] = 687.7f;
		fTableMoist[21] = 641.5f;
		fTableMoist[22] = 595.3f;
		fTableMoist[23] = 549.1f;
		fTableMoist[24] = 502.9f;
		fTableMoist[25] = 456.7f;
		fTableMoist[26] = 410.4f;
		fTableMoist[27] = 364.1f;
		fTableMoist[28] = 349.3f;
		fTableMoist[29] = 333.8f;
		fTableMoist[30] = 318.3f;
		fTableMoist[31] = 302.8f;
		fTableMoist[32] = 287.3f;
		fTableMoist[33] = 271.8f;
		fTableMoist[34] = 256.3f;
		fTableMoist[35] = 240.8f;
		fTableMoist[36] = 225.3f;
		fTableMoist[37] = 217.7f;
		fTableMoist[38] = 211.3f;
		fTableMoist[39] = 204.9f;
		fTableMoist[40] = 198.5f;
		fTableMoist[41] = 192.1f;
		fTableMoist[42] = 185.7f;
		fTableMoist[43] = 179.3f;
		fTableMoist[44] = 172.9f;
		fTableMoist[45] = 166.5f;
		fTableMoist[46] = 160.1f;
		fTableMoist[47] = 154.12f;
		fTableMoist[48] = 148.17f;
		fTableMoist[49] = 142.22f;
		fTableMoist[50] = 136.27f;
		fTableMoist[51] = 130.32f;
		fTableMoist[52] = 124.37f;
		fTableMoist[53] = 118.42f;
		fTableMoist[54] = 112.47f;
		fTableMoist[55] = 106.52f;
		fTableMoist[56] = 101.8f;
		fTableMoist[57] = 96.5f;
		fTableMoist[58] = 91.2f;
		fTableMoist[59] = 85.9f;
		fTableMoist[60] = 80.6f;
		fTableMoist[61] = 75.3f;
		fTableMoist[62] = 70.0f;
		fTableMoist[63] = 64.7f;
		fTableMoist[64] = 59.4f;
		fTableMoist[65] = 56.38f;
		fTableMoist[66] = 53.21f;
		fTableMoist[67] = 50.04f;
		fTableMoist[68] = 46.87f;
		fTableMoist[69] = 43.72f;
		fTableMoist[70] = 40.53f;
		fTableMoist[71] = 37.36f;
		fTableMoist[72] = 34.19f;
		fTableMoist[73] = 31.02f;
		fTableMoist[74] = 27.9f;
		fTableMoist[75] = 26.1f;
		fTableMoist[76] = 24.3f;
		fTableMoist[77] = 22.5f;
		fTableMoist[78] = 20.7f;
		fTableMoist[79] = 18.9f;
		fTableMoist[80] = 17.1f;
		fTableMoist[81] = 15.3f;
		fTableMoist[82] = 13.5f;
		fTableMoist[83] = 11.84f;
		fTableMoist[84] = 10.91f;
		fTableMoist[85] = 9.98f;
		fTableMoist[86] = 9.05f;
		fTableMoist[87] = 8.12f;
		fTableMoist[88] = 7.19f;
		fTableMoist[89] = 6.26f;
		fTableMoist[90] = 5.33f;
		fTableMoist[91] = 4.4f;
		fTableMoist[92] = 4.38f;
		fTableMoist[93] = 4.22f;
		fTableMoist[94] = 4.06f;
		fTableMoist[95] = 3.9f;
		fTableMoist[96] = 3.74f;
		fTableMoist[97] = 3.58f;
		fTableMoist[98] = 3.42f;
		fTableMoist[99] = 3.26f;
		fTableMoist[100] = 3.1f;
		
		fTableOil = new float[101];		
		fTableOil[0] = 1100.0f;
		fTableOil[1] = 1077.4f;
		fTableOil[2] = 1054.2f;
		fTableOil[3] = 1031.0f;
		fTableOil[4] = 1007.8f;
		fTableOil[5] = 984.6f;
		fTableOil[6] = 961.4f;
		fTableOil[7] = 938.2f;
		fTableOil[8] = 915.0f;
		fTableOil[9] = 891.8f;
		fTableOil[10] = 868.6f;
		fTableOil[11] = 845.4f;
		fTableOil[12] = 822.2f;
		fTableOil[13] = 799.0f;
		fTableOil[14] = 775.8f;
		fTableOil[15] = 752.6f;
		fTableOil[16] = 729.4f;
		fTableOil[17] = 706.2f;
		fTableOil[18] = 683.0f;
		fTableOil[19] = 656.8f;
		fTableOil[20] = 631.4f;
		fTableOil[21] = 606.0f;
		fTableOil[22] = 580.6f;
		fTableOil[23] = 555.2f;
		fTableOil[24] = 529.8f;
		fTableOil[25] = 504.4f;
		fTableOil[26] = 479.0f;
		fTableOil[27] = 453.6f;
		fTableOil[28] = 428.2f;
		fTableOil[29] = 402.8f;
		fTableOil[30] = 377.4f;
		fTableOil[31] = 352.0f;
		fTableOil[32] = 326.6f;
		fTableOil[33] = 301.2f;
		fTableOil[34] = 275.8f;
		fTableOil[35] = 250.4f;
		fTableOil[36] = 225.0f;
		fTableOil[37] = 221.56f;
		fTableOil[38] = 218.14f;
		fTableOil[39] = 214.72f;
		fTableOil[40] = 211.3f;
		fTableOil[41] = 207.88f;
		fTableOil[42] = 204.46f;
		fTableOil[43] = 201.04f;
		fTableOil[44] = 197.62f;
		fTableOil[45] = 194.2f;
		fTableOil[46] = 190.78f;
		fTableOil[47] = 187.36f;
		fTableOil[48] = 183.94f;
		fTableOil[49] = 180.52f;
		fTableOil[50] = 177.1f;
		fTableOil[51] = 173.68f;
		fTableOil[52] = 170.26f;
		fTableOil[53] = 166.84f;
		fTableOil[54] = 163.42f;
		fTableOil[55] = 160.0f;
		fTableOil[56] = 156.99f;
		fTableOil[57] = 154.02f;
		fTableOil[58] = 151.05f;
		fTableOil[59] = 148.08f;
		fTableOil[60] = 145.11f;
		fTableOil[61] = 142.14f;
		fTableOil[62] = 139.17f;
		fTableOil[63] = 136.2f;
		fTableOil[64] = 133.23f;
		fTableOil[65] = 130.26f;
		fTableOil[66] = 127.29f;
		fTableOil[67] = 124.32f;
		fTableOil[68] = 121.35f;
		fTableOil[69] = 118.38f;
		fTableOil[70] = 115.41f;
		fTableOil[71] = 112.44f;
		fTableOil[72] = 109.47f;
		fTableOil[73] = 106.5f;
		fTableOil[74] = 89.9f;
		fTableOil[75] = 73.3f;
		fTableOil[76] = 56.7f;
		fTableOil[77] = 0.0f;
		fTableOil[78] = 0.145f;
		fTableOil[79] = 0.29f;
		fTableOil[80] = 0.435f;
		fTableOil[81] = 0.58f;
		fTableOil[82] = 0.725f;
		fTableOil[83] = 0.835f;
		fTableOil[84] = 0.945f;
		fTableOil[85] = 1.055f;
		fTableOil[86] = 1.165f;
		fTableOil[87] = 1.275f;
		fTableOil[88] = 1.385f;
		fTableOil[89] = 1.495f;
		fTableOil[90] = 1.605f;
		fTableOil[91] = 1.725f;
		fTableOil[92] = 5.975f;
		fTableOil[93] = 10.225f;
		fTableOil[94] = 14.475f;
		fTableOil[95] = 18.725f;
		fTableOil[96] = 22.975f;
		fTableOil[97] = 27.225f;
		fTableOil[98] = 31.475f;
		fTableOil[99] = 35.725f;
		fTableOil[100] = 40.0f;
		
		fTableSoft = new float[101];
		fTableSoft[0] = 1100.0f;
		fTableSoft[1] = 1081.9f;
		fTableSoft[2] = 1064.2f;
		fTableSoft[3] = 1046.5f;
		fTableSoft[4] = 1028.8f;
		fTableSoft[5] = 1011.1f;
		fTableSoft[6] = 993.4f;
		fTableSoft[7] = 975.7f;
		fTableSoft[8] = 958.0f;
		fTableSoft[9] = 940.3f;
		fTableSoft[10] = 922.6f;
		fTableSoft[11] = 904.9f;
		fTableSoft[12] = 887.2f;
		fTableSoft[13] = 869.5f;
		fTableSoft[14] = 851.8f;
		fTableSoft[15] = 834.1f;
		fTableSoft[16] = 816.4f;
		fTableSoft[17] = 798.7f;
		fTableSoft[18] = 781.0f;
		fTableSoft[19] = 734.4f;
		fTableSoft[20] = 688.1f;
		fTableSoft[21] = 641.8f;
		fTableSoft[22] = 595.5f;
		fTableSoft[23] = 549.2f;
		fTableSoft[24] = 502.9f;
		fTableSoft[25] = 456.6f;
		fTableSoft[26] = 410.3f;
		fTableSoft[27] = 364.0f;
		fTableSoft[28] = 348.2f;
		fTableSoft[29] = 332.8f;
		fTableSoft[30] = 317.4f;
		fTableSoft[31] = 302.0f;
		fTableSoft[32] = 286.6f;
		fTableSoft[33] = 271.2f;
		fTableSoft[34] = 255.8f;
		fTableSoft[35] = 240.4f;
		fTableSoft[36] = 225.0f;
		fTableSoft[37] = 218.5f;
		fTableSoft[38] = 212.0f;
		fTableSoft[39] = 205.5f;
		fTableSoft[40] = 199.0f;
		fTableSoft[41] = 192.5f;
		fTableSoft[42] = 186.0f;
		fTableSoft[43] = 179.5f;
		fTableSoft[44] = 173.0f;
		fTableSoft[45] = 166.5f;
		fTableSoft[46] = 160.0f;
		fTableSoft[47] = 154.02f;
		fTableSoft[48] = 148.08f;
		fTableSoft[49] = 142.14f;
		fTableSoft[50] = 136.2f;
		fTableSoft[51] = 130.26f;
		fTableSoft[52] = 124.32f;
		fTableSoft[53] = 118.38f;
		fTableSoft[54] = 112.44f;
		fTableSoft[55] = 106.5f;
		fTableSoft[56] = 101.1f;
		fTableSoft[57] = 95.9f;
		fTableSoft[58] = 90.7f;
		fTableSoft[59] = 85.5f;
		fTableSoft[60] = 80.3f;
		fTableSoft[61] = 75.1f;
		fTableSoft[62] = 69.9f;
		fTableSoft[63] = 64.7f;
		fTableSoft[64] = 59.5f;
		fTableSoft[65] = 56.44f;
		fTableSoft[66] = 53.26f;
		fTableSoft[67] = 50.08f;
		fTableSoft[68] = 46.9f;
		fTableSoft[69] = 43.72f;
		fTableSoft[70] = 40.54f;
		fTableSoft[71] = 37.36f;
		fTableSoft[72] = 34.18f;
		fTableSoft[73] = 31.0f;
		fTableSoft[74] = 29.0f;
		fTableSoft[75] = 27.0f;
		fTableSoft[76] = 25.0f;
		fTableSoft[77] = 23.0f;
		fTableSoft[78] = 21.0f;
		fTableSoft[79] = 19.0f;
		fTableSoft[80] = 17.0f;
		fTableSoft[81] = 15.0f;
		fTableSoft[82] = 13.0f;
		fTableSoft[83] = 12.02f;
		fTableSoft[84] = 11.08f;
		fTableSoft[85] = 10.14f;
		fTableSoft[86] = 9.2f;
		fTableSoft[87] = 8.26f;
		fTableSoft[88] = 7.32f;
		fTableSoft[89] = 6.38f;
		fTableSoft[90] = 5.44f;
		fTableSoft[91] = 4.5f;
		fTableSoft[92] = 4.36f;
		fTableSoft[93] = 4.19f;
		fTableSoft[94] = 4.02f;
		fTableSoft[95] = 3.85f;
		fTableSoft[96] = 3.68f;
		fTableSoft[97] = 3.51f;
		fTableSoft[98] = 3.34f;
		fTableSoft[99] = 3.17f;
		fTableSoft[100] = 3.0f;*/
	}
	
	//　启动录音线程
	public void start() {
		if(mbRecording) return;
		mbRecording = true;
		new RecordThread(audioRecord, minBufferSize).start();
	}
	
	// 停止
	public void stop() {
		if(!mbRecording) return;
		mbRecording = false;
	}
	
	// 暂停
	public void pause() {
		mbPause = true;
	}
	
	// 恢复
	public void resume() {
		mbPause = false;
	}
	
	public boolean isLinked() {
		return isLinked;
	}
	//¼���߳�
	class RecordThread extends Thread{
		private AudioRecord audioRecord;
		private int minBufferSize;
		
		public RecordThread(AudioRecord audioRecord,int minBufferSize){
			this.audioRecord = audioRecord;
			this.minBufferSize = minBufferSize;
		}
		
		public void run(){
			try{
				int nMaxIndex = 0;
				int nFrameCount = 0;
				int nFrameCount2 = 0;
				short[] buffer = new short[minBufferSize];
				short[] captureBuffer = new short[16384];
				int nCaptureLen = 0;
				audioRecord.startRecording();
				Log.i(TAG, "已启动录音线程");
				
				int nCaptureState = 0;		// 状态ֹ
				while(mbRecording){
					// 如果是暂停状态不录音
					if(mbPause) {
						nCaptureState = 0;
						nCaptureLen = 0;
						Thread.sleep(100);
						continue;
					}
					
					// 录音
					int res = audioRecord.read(buffer, 0, minBufferSize);

					// 新加，兼容S4，调用so库解码，后期全部整合	
					if(mbSM_G9006V) {
						if(0 == mDecoder.DecodeData(buffer, res)) {
							Log.i(TAG, "解码成功...");
							
							mfWater = mDecoder.GetWater();
							mfOil = mDecoder.GetOil();
							mfTX = mDecoder.GetTX();
							mfWater_Oil = mDecoder.GetWaterOil();
							
							Message message = new Message();
							Bundle bundle  = new Bundle(); 
							bundle.putInt("Em", mnTestEm);
							bundle.putFloat("Moist", mfMoist);
							bundle.putFloat("Oil", mfOil);
							bundle.putFloat("Soft", mfSoft);
									
							// 2014217
							bundle.putFloat("Water", mfWater);	
							bundle.putFloat("Oil", mfOil);
							bundle.putFloat("TX", mfTX);
							bundle.putFloat("Water_Oil", mfWater_Oil);							
								
							message.setData(bundle);
							message.what = 2;		
							AudioProcess.this.m_handler.sendMessage(message);
						}	
					}
					else {
						// 检测最大值
						int nMax9 = 7000;
						int nMax = 0; 
						if(nCaptureState == 0) {
							for(int i=0; i <res-20 ; i+=1){
								nMax = (short) Math.max(nMax, Math.abs(buffer[i]));
								if(nMax >= mnMax || nMax > 1000) {
									int tempcount = 0;
									for(int j=0; j<12; j++) {
										if(Math.abs(buffer[i+j]) > nMax9) tempcount++;
									}
									if(tempcount > 8) {
										nMaxIndex = i;
										nCaptureState = 1;
										break;
									}
								}
							}							
						}	
						
/*						int nMax = 0; 
						if(nCaptureState == 0) {					
							for(int i=0; i <res ; i++){
								Short short1 = buffer[i];							
								nMax = (short) Math.max(nMax, Math.abs(short1));
								if(nMax >= mnMax || nMax > 9000) {
									nMaxIndex = i;
									nCaptureState = 1;
									Log.i(TAG, "找到最大值，开始捕捉 nMaxIndex = " + nMaxIndex);
									break;
								}
							}							
						}*/	
						//if(nMax >= mnMax || nMax > 9000)
						//	Log.i(TAG, String.format("nMax %d", nMax));
						if(nCaptureState == 1) {
							int nCopyLen = Math.min(res, 16384-nCaptureLen);
							System.arraycopy(buffer, 0, captureBuffer, nCaptureLen, nCopyLen);
							nCaptureLen += nCopyLen;
						}
						else {
							// 状态
							nFrameCount++;						
							if(nMax < 2200) {
								nFrameCount2++;
							}						
	
							if(nFrameCount > 10) {
								isLinked = nFrameCount2 > 6;
								nFrameCount = 0;
								nFrameCount2 = 0;
							}
						}
	
						// 在指定误差范围内检测
					    int nFL[] = {145, 146, 144, 147, 143, 148, 142, 149, 141, 150,
			                    140, 151, 139, 152, 138, 153, 137, 154, 136, 155,
			                    135, 156, 134, 157, 133, 158, 132, 159, 131, 160,
			                    130, 161, 129, 162, 128, 163, 127, 164, 126, 165,
			                    166, 167, 168};					
						if(nCaptureLen >= 16384) {
							nCaptureState = 2;
							// 旧的解释数据
							//boolean bOk = transform(captureBuffer, nCaptureLen);
							
							
							// 保存到文件中
							if(m_bSaveFile) {
								//
								mnFileIndex ++;
								/*
								File sdCardDir = Environment.getExternalStorageDirectory();
								String strFile = String.format("%s/data%d.txt", sdCardDir.getCanonicalPath(), mnFileIndex);
								Log.i("df", strFile);
								FileOutputStream fos = new FileOutputStream(strFile);
								fos.write(String.format("nMax=%d nMaxIndex=%d\r\n", nMax, nMaxIndex).getBytes());
								for(int i=0; i<nCaptureLen; i++) {
									String strText = String.format("%d\r\n", captureBuffer[i]);
									fos.write(strText.getBytes());
								}
								fos.close();
								*/	
							}
							
							
					        for(int i=0; i<43; i++) {
					            int nFrameLen = nFL[i];      	
								boolean bOk = transform2(captureBuffer, nCaptureLen, nMaxIndex, nFrameLen);
								if(bOk) {
									Message message = new Message();
									Bundle bundle  = new Bundle(); 
									bundle.putInt("Em", mnTestEm);
									bundle.putFloat("Moist", mfMoist);
									bundle.putFloat("Oil", mfOil);
									bundle.putFloat("Soft", mfSoft);
									
									// 2014217
									bundle.putFloat("Water", mfWater);	
									bundle.putFloat("Oil", mfOil);
									bundle.putFloat("TX", mfTX);
									bundle.putFloat("Water_Oil", mfWater_Oil);							
									
									message.setData(bundle);
									message.what = 2;		
									AudioProcess.this.m_handler.sendMessage(message);
									Log.i(TAG, "执行一次分析 >> 成功");
									break;
								}
								else {
									Log.i(TAG, "执行一次分析 >> 失败");						
								}							
					        }
							nCaptureState = 0;
							nCaptureLen = 0;
						}
					}
				}
				audioRecord.stop();
				isLinked = false;
				mDecoder.term();
				Log.i(TAG, "录音线程已退出");
			}catch (Exception e) {
				// TODO: handle exception
				Log.i("Rec E",e.toString());
			}
		}
		
		// 解释数据
		public boolean transform2(short [] buffer, int len, int nMaxIndex, int nFrameLen)
				throws IOException {

			// 数字变换
			int nBits[] = new int[22];			
			float [] fOut = new float[128];
			float [] fIn = new float[256];


			for(int i=0; i<22; i++) {
				for(int j=0; j<256; j++) {
					if(j < nFrameLen) {
						fIn[j] = (float)buffer[nMaxIndex+i*nFrameLen+j];
					}
					else {
						fIn[j] = 0.0f;						
					}
				}
				FFT(fIn, fOut, 256, 8);	
				
				int nBit = getBit(fOut, 128);
				if(nBit == -1) return false;
				if(nBit != 0 && i == 0) return false;
				if(nBit != 1 && i == 1) return false;				
				if(nBit != 0 && i == 2) return false;
				if(nBit != 1 && i == 3) return false;				
				nBits[i] = nBit;
			}
			
			
			// 位数据
			Message message = new Message();
			Bundle bundle  = new Bundle(); 
			bundle.putInt("Count", 22);
			bundle.putIntArray("Bit", nBits);
			message.setData(bundle);
			message.what = 3;		
			AudioProcess.this.m_handler.sendMessage(message);
			
			if(nBits[0] != 0 || nBits[1] != 1 || nBits[2] != 0 || nBits[3] != 1
					/*|| nBits[20] != 1*/ || nBits[21] != 1) {
				Log.i(TAG, "头部不正确");
				return false;
			}		
			
			// 计算电压值并查找
			int n1, n2, n3, n4;
			n1 = nBits[4] + nBits[5]*2 + nBits[6]*4 + nBits[7]*8;
			n2 = nBits[8] + nBits[9]*2 + nBits[10]*4 + nBits[11]*8;	
			n3 = nBits[12] + nBits[13]*2 + nBits[14]*4 + nBits[15]*8;	
			n4 = nBits[16] + nBits[17]*2 + nBits[18]*4 + nBits[19]*8;
			mnTestEm = n4*16*16*16 + n3*16*16 + n2*16 + n1;
			Log.i(TAG, String.format("nRetValue = %d", mnTestEm));
			
//			float fValue = (float) (mnTestEm*100.0/1000.0);			
//			mfMoist = findFromTable(fTableMoist, fValue);
//			mfOil = findFromTable(fTableOil, fValue);
//			mfSoft = findFromTable(fTableSoft, fValue);
			
			// 2014217
			// 查表得到数据
			float fDCV = mnTestEm/4096.0f*3300.0f * 0.001f;
			findFromDCVTable(fDCV);
			mfWater = findFromTable2014217(fTableWater, mnDCVIndex, mfDCVy);
			mfOil = findFromTable2014217(fTableOil, mnDCVIndex, mfDCVy);
			mfTX = findFromTable2014217(fTableTX, mnDCVIndex, mfDCVy);
			mfWater_Oil = findFromTable2014217(fTableWater_Oil, mnDCVIndex, mfDCVy);
	
			
			return true;
		}
		
		// 分析数据(旧的方法,目前在项目中已不使用)
/*		public boolean transform(short [] buffer, int len) throws IOException {
			File sdCardDir = Environment.getExternalStorageDirectory();
			
			// 找最大值
			int nValue1 = (int)mnMax/10*3;
			int nValue2 = (int)mnMin/10*3;
			for(int i=0; i<len; i++) {
				int nCur = buffer[i];
				if(0 < nCur && nCur < nValue1)
					buffer[i] = 0;
				else if(nCur > nValue1)
					buffer[i] = mnMax;
				else if(0 > nCur && nCur > nValue2)
					buffer[i] = 0;
				else if(nCur < nValue2)
					buffer[i] = mnMin;
			}

			int nState = 0;			// 状态
			int nByteCount = 0; 
			int nWaveList [] = new int[1024];		// 波形
			int nWaveCount = 0;
			for(int i=0; i<len; i++) {
				//Log.i(TAG, String.format(">>> buffer[%d] = %d", i, buffer[i]));							
				Short nValue =  buffer[i];
				if(nState == 0) {
					if(nValue == mnMax) {
						nState = 1;
						nByteCount ++;											}
					else if(nValue == mnMin) {
						nState = 2;	
						nByteCount ++;											
					}
				}
				else if(nState == 1) {						
					if(nValue == mnMax) {
						nByteCount ++;						
					}
					else {						
						if(7 <= nByteCount && nByteCount <= 11) {
							nWaveList[nWaveCount] = 0;
							nWaveCount++;						
						}
						else if(14 <= nByteCount && nByteCount <= 20) {
							nWaveList[nWaveCount] = 1;	
							nWaveCount++;				
						}
						else {
							if(14 <= nByteCount && nByteCount <= 28) {
								nWaveList[nWaveCount] = 1;
								nWaveCount++;
							}							
							break;
						}						
						nByteCount = 0;	
						nState = 2;
					}
				}
				else if(nState == 2) {
					if(nValue == mnMin) {
						nByteCount ++;						
					}
					else {				
						if(7 <= nByteCount && nByteCount <= 11) {
							nWaveList[nWaveCount] = 0;
							nWaveCount++;						
						}
						else if(14 <= nByteCount && nByteCount <= 20) {
							nWaveList[nWaveCount] = 1;	
							nWaveCount++;				
						}
						else {
							if(14 <= nByteCount && nByteCount <= 28) {
								nWaveList[nWaveCount] = 1;
								nWaveCount++;
							}												
							break;
						}						
						nByteCount = 0;	
						nState = 1;
					}
				}
			} 
		
			if(nWaveCount < 190) {
				return false;
			}

			for(int i=0; i<nWaveCount; i++) {
				Log.i(TAG, String.format(">>>>>>> [%d]: %d", i, nWaveList[i]));
			}
			
		
			int nBitList[] = new int[32];
			int nBitCount = 0;
			int nWaveCount2 = 0;
			int nType = -1;
			for(int i=0; i<nWaveCount; i++) {
				if(nWaveList[i] == nType || i == nWaveCount-1) {
					nWaveCount2++;
				}
				if(nWaveList[i] != nType || i == nWaveCount-1) {
					if(nType == 0) {
						int nCount = (int) (nWaveCount2/15);
						for(int j=0; j<nCount; j++) {
							nBitList[nBitCount] = 0;
							nBitCount++;
						}
					}
					else if(nType == 1) {
						int nCount = (int) (nWaveCount2/8);
						for(int j=0; j<nCount; j++) {
							nBitList[nBitCount] = 1;
							nBitCount++;
						}											
					}
					nType = nWaveList[i];
					nWaveCount2 = 1;
				}				
			}
				
			if(nBitCount != 22) {
				Log.i(TAG, String.format("nBitCount[=%d] != 22", nBitCount));
				return false;
			}
			
			Message message = new Message();
			Bundle bundle  = new Bundle(); 
			bundle.putInt("Count", nBitCount);
			bundle.putIntArray("Bit", nBitList);
			message.setData(bundle);
			message.what = 3;		
			AudioProcess.this.m_handler.sendMessage(message);
			
			if(nBitList[0] != 0 || nBitList[1] != 1 || nBitList[2] != 0 || nBitList[3] != 1
					|| nBitList[20] != 1 || nBitList[21] != 1) {
				return false;
			}		

			int n1, n2, n3, n4;
			n1 = nBitList[4] + nBitList[5]*2 + nBitList[6]*4 + nBitList[7]*8;
			n2 = nBitList[8] + nBitList[9]*2 + nBitList[10]*4 + nBitList[11]*8;	
			n3 = nBitList[12] + nBitList[13]*2 + nBitList[14]*4 + nBitList[15]*8;	
			n4 = nBitList[16] + nBitList[17]*2 + nBitList[18]*4 + nBitList[19]*8;
			mnTestEm = n4*16*16*16 + n3*16*16 + n2*16 + n1;
			Log.i(TAG, String.format("nRetValue = %d", mnTestEm));
			
			float fValue = (float) (mnTestEm*100.0/1000.0);			
			mfMoist = findFromTable(fTableMoist, fValue);
			mfOil = findFromTable(fTableOil, fValue);
			mfSoft = findFromTable(fTableSoft, fValue);
			
			
			return true;
		}
		*/
		// 从表中查到电压值等级
		public void findFromDCVTable(float fDCV) {
			Log.i("ere", "findFromDCVTable");
			if(fDCV <= fTableDCV[0]) {
				mnDCVIndex = 0;
				mfDCVy = 0.0f;
				return;
			}
			if(fDCV >= fTableDCV[92]) {
				mnDCVIndex = 91;
				mfDCVy = 1.0f;
				return;
			}
			
			for(int i=1; i<92; i++) {
				if(fDCV < fTableDCV[i]) {
					mnDCVIndex = i-1;
					mfDCVy = (fDCV-fTableDCV[mnDCVIndex]) / (fTableDCV[i]-fTableDCV[mnDCVIndex]);
					return;
				}
			}
		}
		
		// 查表得到数据
		public float findFromTable2014217(float [] fTable, int nIndex, float fy) {
			return fTable[nIndex] + (fTable[nIndex+1]-fTable[nIndex]) * fy;
		}
		
		// 旧的查表方法()
		/*public float findFromTable(float [] fTable, float fScrValue) {
			int nIndex = 0;
			if(fScrValue >= fTable[0])
				return 0.0f;
			if(fScrValue <= fTable[100])
				return 100.0f;
			
			for(int i=1; i<100; i++) {
				if(fScrValue >= fTable[i]) {
					nIndex = i;
					break;
				}
			}
			if(1 <= nIndex && nIndex < 100) {
				float f = (fScrValue - fTable[nIndex])
						/ (fTable[nIndex-1] - fTable[nIndex]);
				return nIndex - f;
			}
			
			return 0.0f;
		}
		*/
		/*public float findFromTable2(float [] fTable, float fScrValue) {
			int nIndex = 0;
			if(fScrValue >= fTable[0])
				return 0.0f;
//			if(fScrValue <= fTable[100])
//				return 100.0f;
			
			if(fScrValue >= 56.7f) {
				for(int i=1; i<76; i++) {
					if(fScrValue >= fTable[i]) {
						nIndex = i;
						break;
					}
				}
				if(1 <= nIndex && nIndex < 76) {
					float f = (fScrValue - fTable[nIndex])
							/ (fTable[nIndex-1] - fTable[nIndex]);
					return nIndex - f;
				}				
			}
			else if(fScrValue <= 40.0f){
				for(int i=99; i>=77; i--) {
					if(fScrValue >= fTable[i]) {
						nIndex = i;
						break;
					}
				}
				if(77 <= nIndex && nIndex <= 99) {
					float f = (fScrValue - fTable[nIndex])
							/ (fTable[nIndex+1] - fTable[nIndex]);
					return nIndex + f;
				}						
			}
//			else {
				return 100.0f;
//			}
			
			for(int i=1; i<100; i++) {
				if(fScrValue >= fTable[i]) {
					nIndex = i;
					break;
				}
			}
			if(1 <= nIndex && nIndex < 100) {
				float f = (fScrValue - fTable[nIndex])
						/ (fTable[nIndex-1] - fTable[nIndex]);
				return nIndex - f;
			}
			
//			return 0.0f;
		}
		*/
		
		// 以下是数学变换的方法，本人也不太了解。
		// 作用就是输入一个数组，变换后得到一个数组
		public void FFT(float fDataIn[], float fDataOut[], int nCount, int M) {
			int i, j, k;
			int p, J, L, B;
			float dataR[];					// 实数
			float dataI[];					// 虚数
			float Tr,Ti,temp;	
			
			
			// 初始化数组
			dataR = new float[nCount];
			dataI = new float[nCount];
			for(i = 0; i < nCount; i++)
			{
				dataR[i] = fDataIn[i];			// 实数
				dataI[i] = 0;					// 虚数
			}


			j = nCount / 2;	
			for(i=1; i<nCount-2; i++) {
				if(i < j) {
					temp = dataR[i];
					dataR[i] = dataR[j];
					dataR[j] = temp;			
				}		
				k = nCount / 2;		
				while(true) {
					if(j < k) {
						j = j + k;
						break;
					}
					else {
						j = j - k;
						k = k / 2;
					}
				}
			}

			for(L=1; L<=M;L++)
			{		
				B = 1;
				i = L-1;
				while(i > 0) {
					B = B * 2;
					i--;
				}
				

				for(J=0;J<=B-1;J++)
				{
					p = 1;
					i = M - L;
					while(i>0) {
						p = p * 2;
						i--;
					}
					p = J * p;			

					for(k = J; k <= nCount-1; k = k+2*B) {
						Tr = dataR[k];
						Ti = dataI[k];
						temp = dataR[k+B];
				     
						dataR[k] = (float) (dataR[k] + dataR[k+B]*Math.cos(2.0*PI*p/nCount) 
		                                    + dataI[k+B]*Math.sin(2.0*PI*p/nCount));
						dataI[k] = (float) (dataI[k] + dataI[k+B]*Math.cos(2.0*PI*p/nCount) 
		                                     - dataR[k+B]*Math.sin(2.0*PI*p/nCount));
						dataR[k+B] = (float) (Tr - dataR[k+B]*Math.cos(2.0*PI*p/nCount) 
		                                     - dataI[k+B]*Math.sin(2.0*PI*p/nCount));
						dataI[k+B] = (float) (Ti - dataI[k+B]*Math.cos(2.0*PI*p/nCount) 
		                                    + temp*Math.sin(2.0*PI*p/nCount));
					}
				}
			}

			for(i=0; i<nCount/2; i++ )
			{ 		
		 		fDataOut[i] = (float) Math.sqrt(dataR[i]*dataR[i]+dataI[i]*dataI[i]);
			}		
		}
		
		// 输入数组，检测是位1还是位0
		public int getBit(float [] fIn, int length) {
			int nMaxIndex = 0;
			float fMax = 0.0f;
			float fAve = 0.0f;
			for(int i=0; i<length; i++) {
				if(fIn[i] > fMax) {
					fMax = fIn[i];
					nMaxIndex = i;
				}
				fAve += fIn[i];
			}
			fAve = fAve/length;
			if(fMax < 2*fAve) return -1;
//			Log.i(TAG, "getBit 003");			
			if(nMaxIndex == 7 || nMaxIndex == 8 || nMaxIndex == 9)
				return 1;
			if(nMaxIndex == 13 || nMaxIndex == 14 || nMaxIndex == 15 || nMaxIndex == 16)
				return 0;			
			Log.i(TAG, String.format("getBit004 fMaxIndex=%d", nMaxIndex));			
			return -1;
		}
	}	
}
