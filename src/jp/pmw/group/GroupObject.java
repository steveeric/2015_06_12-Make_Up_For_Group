package jp.pmw.group;

import java.util.List;

public class GroupObject {
	private String groupName;
	private List<Student> member;
	GroupObject(String gn,List<Student> mem){
		this.groupName=gn;
		this.member=mem;
	}

	public void addMember(Student st){
		boolean b = false;
		for(int i=0;i<this.member.size();i++){
			if(this.member.get(i).getAttendee()==null && b==false){
				String studentId = st.getStudentId();
				String fullName = st.getFullName();
				DM dm = st.getDm();
				Attendee at = st.getAttendee();
				SeatObject so = this.member.get(i).getSeatObject();
				SeatObject past = st.getSeatObject();
				this.member.get(i).setStudentInfo(studentId, fullName, dm, at);
				this.member.get(i).setPastSeat(past);
				//this.member.get(i).setSeatObject(so);
				String gn = so.getGroupName();
				String bn = so.getSeatBlockName();
				String r = so.getSeatRow();
				String c = so.getSeatColumn();
				System.out.println("MOVING STUDENT_ID:"+studentId+"GROPU_NAME:"+gn+",SEAT_BLOCK_NAME:"+bn+",SEAT_ROW:"+r+",SEAT_COLUMN:"+c);
				b=true;
			}
		}
	}
	public void addMembers(List<Student> students){
		this.member.addAll(students);
	}

	public String getGroupName(){return this.groupName;}
	public List<Student> getMemeber(){return this.member;}

	public int getSitingSeatCount(/*int maxSize*/){
		int count = 0;
		for(int i=0;i<this.member.size();i++){
			if(this.member.get(i).getAttendee()!=null){
				++count;
			}
		}
		return count;
	}
	
	public int getSeatCount(){
		int count = 0;
		if(this.member!=null){
			count = this.member.size();
		}
		return count;
	}

	public void crearAttendeeInfo(String studentId){
		for(int i=0;i<this.member.size();i++){
			if(this.member.get(i).getAttendee()!=null){
				if(this.member.get(i).getStudentId().equals(studentId)){
					this.member.get(i).setStudentInfo(null, null, null, null);
				}
			}
		}
	}
}
