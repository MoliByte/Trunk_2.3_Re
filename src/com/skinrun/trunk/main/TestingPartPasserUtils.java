package com.skinrun.trunk.main;

public class TestingPartPasserUtils {
	private TestingPartInterface tp;

	public void setTp(TestingPartInterface tp) {
		this.tp = tp;
	}
	private static TestingPartPasserUtils instance;
	private TestingPartPasserUtils(){};
	public static TestingPartPasserUtils getInstance(){
		if(instance==null){
			instance=new TestingPartPasserUtils();
		}
		return instance;
	}
	public void passPart(int part){
		tp.passTestPart(part);
	}
}
