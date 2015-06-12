package jp.pmw.group;

public class DM {
	private String dmBarocdeId;
	//private CSVLayoutObject object;
	DM(String dm){
		this.dmBarocdeId=dm;
	}

	/*public void setCSVLayoutObject(CSVLayoutObject csv){
		this.object=csv;
	}*/

	public String getDmBarcodeId(){return this.dmBarocdeId;}
	//public CSVLayoutObject getCSVLayoutObject(){return this.object;}
}
