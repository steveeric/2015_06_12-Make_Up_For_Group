package jp.pmw.group;

import java.util.List;


public class SubjectObject {
	//シラバスID
	private String scheduleId;
	//週
	private String week;
	//授業ID
	private String subjectId;
	//授業名
	private String subjectName;
	//部屋識別ID
	private String roomId;
	//
	private String timeTableId;

	private GroupingConfig groupingConfig;

	SubjectObject(String scheduleId,String week,String subjectId,String subjectName,String roomId,String timeTableId){
		this.scheduleId=scheduleId;
		this.week=week;
		this.subjectId=subjectId;
		this.subjectName=subjectName;
		this.roomId=roomId;
		this.timeTableId=timeTableId;
	}

	public void setGroupingConfig(GroupingConfig config){
		this.groupingConfig=config;
	}

	public String getScheduleId(){return this.scheduleId;}
	public String getWeek(){return this.week;}
	public String getSubjectId(){return this.subjectId;}
	public String getSubjectName(){return this.subjectName;}
	public String getRoomId(){return this.roomId;}
	public String getTimeTableId(){return this.timeTableId;}
	public GroupingConfig getGroupingConfig(){return this.groupingConfig;}

}
