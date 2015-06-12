package jp.pmw.group;

public class SeatObject {
	private String seatId;
	private String groupName;
	private String blockId;
	private String seatBlockName;
	private String seatRow;
	private String seatColumn;
	SeatObject(String sid,String gname,String bid,String bname,String srow,String sc){
		this.seatId=sid;
		this.groupName=gname;
		this.blockId=bid;
		this.seatBlockName=bname;
		this.seatRow=srow;
		this.seatColumn=sc;
	}
	public String getSeatId(){return this.seatId;}
	public String getGroupName(){return this.groupName;}
	public String getSeatBlockId(){return this.blockId;}
	public String getSeatBlockName(){return this.seatBlockName;}
	public String getSeatRow(){return this.seatRow;}
	public String getSeatColumn(){return this.seatColumn;}
}
