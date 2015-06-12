package jp.pmw.group;

public class Student {
	private String studentId;
	private String fullName;
	private DM dm;
	private Attendee attendee;
	private SeatObject seat;
	private SeatObject pastSeat;

	private int movingFlag = 0;

	Student(String id,String f,DM dm,SeatObject se){
		this.studentId=id;
		this.fullName=f;
		this.dm=dm;
		this.seat=se;
	}

	public void setStudentInfo(String studentId,String fullName,DM dm,Attendee at){
		this.studentId=studentId;
		this.fullName=fullName;
		this.dm=dm;
		this.attendee=at;
	}

	public void setAttendee(Attendee at){
		this.attendee=at;
	}

	public void setPastSeat(SeatObject p){
		this.pastSeat=p;
	}
	public void setMovingFlag(int flag){
		this.movingFlag=flag;
	}

	public void setSeatObject(SeatObject so){
		if(this.seat != null){
			this.pastSeat=seat;
		}
		this.seat=so;
	}

	public int getMovingFlag(){return this.movingFlag;}
	public String getStudentId(){return this.studentId;}
	public String getFullName(){return this.fullName;}
	public Attendee getAttendee(){return this.attendee;}
	public DM getDm(){return this.dm;}
	public SeatObject getSeatObject(){return this.seat;}
	public SeatObject getPastSeatObject(){return this.pastSeat;}
}
