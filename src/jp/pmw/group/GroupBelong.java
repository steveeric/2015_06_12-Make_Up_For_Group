package jp.pmw.group;

import java.util.List;


public class GroupBelong {
	private List<SeatBlockObject> seatBlockList;
	GroupBelong(List<SeatBlockObject> sbo){
		this.seatBlockList=sbo;
	}
	public List<SeatBlockObject> getSeatBlockList(){return this.seatBlockList;}
}
