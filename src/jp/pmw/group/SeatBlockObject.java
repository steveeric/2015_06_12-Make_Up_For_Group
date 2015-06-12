package jp.pmw.group;

import java.util.List;

public class SeatBlockObject {
	private String seatBlockId;
	private String seatBlockName;
	private List<GroupObject> groupList;
	SeatBlockObject(String bid,String sbn){
		this.seatBlockId=bid;
		this.seatBlockName=sbn;
	}
	
	public void setGroupList(List<GroupObject> go){
		this.groupList=go;
	}
	
	public String getSeatBlockId(){return this.seatBlockId;}
	public String getSeatBlockName(){return this.seatBlockName;}
	public List<GroupObject> getGroup(){return this.groupList;}
}
