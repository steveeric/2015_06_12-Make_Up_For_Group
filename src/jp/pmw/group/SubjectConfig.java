package jp.pmw.group;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class SubjectConfig {

	private int ACTION_ID = 9;
	private int GROUP_MAXIMUM=4;
	private int GROUP_MINIMUM=3;

	private Connection con;
	private SubjectObject subjectObject;
	private GroupBelong groupBelong;

	SubjectConfig(Connection c){
		this.con=c;

	}

	public void setConfig(String roomId){
		subjectObject = new SubjectObject(null,null,null,null,roomId,null);
		subjectObject.setGroupingConfig(new GroupingConfig(GROUP_MAXIMUM,GROUP_MINIMUM));
	}

	public SubjectObject getSubjectObject(){return this.subjectObject;}
	public GroupBelong getGroupBelong(){return this.groupBelong;}

	/*public void getSyllabus(String timeTableId,String subjectId,String teacherId) throws SQLException{
		subjectObject = null;
		String sql = "SELECT SY.SCHEDULE_ID, SY.WEEK, SU.SUBJECT_ID,SU.SUBJECT_NAME,SY.ROOM_ID,SY.TIMETABLE_ID,SY.ACTION_ID "
				+ "FROM  `SYLLABUS_MST` SY, SUBJECT_MST SU "
				+ "WHERE SY.SUBJECT_ID = SU.SUBJECT_ID "
				+ "AND SY.`YEAR` =  ? "
				+ "AND SY.`MONTH` LIKE  ? "
				+ "AND SY.`DAY` LIKE  ? "
				+ "AND SY.`TIMETABLE_ID` LIKE  ? "
				+ "AND SY.`SUBJECT_ID` LIKE  ? "
				+ "AND SY.`TEACHER_ID` LIKE  ? "
				+ "AND SY.`USE_ESL` = 1 ";
		long start =System.currentTimeMillis();
		PreparedStatement ps = null;
		ResultSet rs = null;
		con.setAutoCommit(false);
		con.commit();
		try{
			ps = con.prepareStatement(sql);
			ps.setString(1, Now.getYear());
			ps.setString(2, Now.getMonth());
			ps.setString(3, Now.getDay());
			ps.setString(4, timeTableId);
			ps.setString(5, subjectId);
			ps.setString(6, teacherId);
			rs = ps.executeQuery();
			if(rs.next()){
				String sid = rs.getString("SCHEDULE_ID");
				String w = rs.getString("WEEK");
				String subId = rs.getString("SUBJECT_ID");
				String subName = rs.getString("SUBJECT_NAME");
				String roomId = rs.getString("ROOM_ID");
				String timeTable = rs.getString("TIMETABLE_ID");
				subjectObject = new SubjectObject(sid,w,subId,subName,roomId,timeTable);
				if(rs.getInt("ACTION_ID") == ACTION_ID){
					subjectObject.setGroupingConfig(new GroupingConfig(GROUP_MAXIMUM,GROUP_MINIMUM));
				}
			}
		}catch(Exception e){
			//log.error(e);
		}finally{
			//クローズ処理
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}
		long end = System.currentTimeMillis();
		//log.info("END getSyllabus:"+(end-start)+"(msec)");
	}*/

	public List<SeatObject> getSeatChangeData(String roomId,int howManyMembers) throws SQLException{
		return getSeatChange(roomId,howManyMembers);
	}
	public List<Student> getAttendeeData(String sameClassNumber) throws SQLException{
		return getAttendeeGroup(sameClassNumber);
	}

	public void checkGroupAllAttendee(String screenContentId,List<SeatObject>changeSeatList,List<Student>attendeeList) throws SQLException{
		List<SeatBlockObject> seatBlockList = getSeatBlocks();
		//java.util.Iterator<SeatObject> changeSeatIter = changeSeatList.iterator();

		for(int i=0;i<seatBlockList.size();i++){
			String seatBlockId = seatBlockList.get(i).getSeatBlockId();
			List<String> groupNameList = this.getGroupNames(seatBlockId);//getGroupName(seatBlockId);
			List<GroupObject> goupList = new ArrayList<GroupObject>();
			for(int j=0;j<groupNameList.size();j++){
				String groupName = groupNameList.get(j);
				List<Student> member = new ArrayList<Student>();
				for(int k=0;k<attendeeList.size();k++){
					if(groupName.equals(attendeeList.get(k).getSeatObject().getGroupName())){
						member.add(attendeeList.get(k));
					}
					if(member.size() == this.subjectObject.getGroupingConfig().getGroupMaximum()){
						break;
					}
				}
				if(member.size()<this.subjectObject.getGroupingConfig().getGroupMaximum()){
					//空席を追加してあげる必要がある
					//while (changeSeatIter.hasNext()) {
					for(int kk=0;kk<changeSeatList.size();kk++){
						//if(groupName.equals(changeSeatIter.next().getGroupName())){
						if(groupName.equals(changeSeatList.get(kk).getGroupName())){
							//String sid = changeSeatIter.next().getSeatId();
							String sid = changeSeatList.get(kk).getSeatId();
							boolean b = true;
							for(int ll=0;ll<member.size();ll++){
								if(member.get(ll).getSeatObject().getSeatId().equals(sid)){
									b = false;
									break;
								}
							}
							if(b == true){
								//System.out.println(sid);
								/*String gname = changeSeatIter.next().getGroupName();
								String bid = changeSeatIter.next().getSeatBlockId();
								String bname = changeSeatIter.next().getSeatBlockName();
								String c = changeSeatIter.next().getSeatColumn();
								String r = changeSeatIter.next().getSeatRow();*/
								String gname = changeSeatList.get(kk).getGroupName();
								String bid = changeSeatList.get(kk).getSeatBlockId();
								String bname = changeSeatList.get(kk).getSeatBlockName();
								String c = changeSeatList.get(kk).getSeatColumn();
								String r = changeSeatList.get(kk).getSeatRow();
								SeatObject so = new SeatObject(sid,gname,bid,bname,r,c);
								//log.info("空席 GROUP_NAME:"+gname+",SEAT_BLOCK_NAME:"+bname+",ROW:"+r+",COLUMN:"+c);
								member.add(new Student(null,null,null,so));
							}
						}
						//changeSeatIter.remove();
					}
				}
				goupList.add(new GroupObject(groupName,member));
			}
			seatBlockList.get(i).setGroupList(goupList);
		}

		//誰がどの座席ブロックのどのグループにいるかをセット
		groupBelong = new GroupBelong(seatBlockList);
	}


	private List<SeatObject> getSeatChange(String roomId,int howManyMembers) throws SQLException{
		List<SeatObject> choiceSeatList = new ArrayList<SeatObject>();

		String sql = "SELECT SM.SEAT_ID,G.GROUP_NUMBER,SM.SEAT_NAME,SM.SEAT_BLOCK_NAME,SM.SEAT_ROW,SM.SEAT_COLUMN "
				+ "FROM  `GROUPING` G,  `SEATS_MST` SM "
				+ "WHERE G.SEAT_ID = SM.SEAT_ID "
				+ "AND G.`ACTIVE_STATUS` = 1 "
				+ "AND SM.`ACTIVE_STATUS` = 1 "
				+ "AND G.`HOW_MANY_MEMBERS` = "+howManyMembers +" "
				+ "AND SM.`ROOM_ID` =  '"+roomId+"' "
				+ "ORDER BY  `G`.`GROUP_ORDER` ASC ";
		Statement stmt = (Statement) con.createStatement();
		ResultSet rs = stmt.executeQuery(sql);

		while(rs.next()){
			String id = rs.getString("SEAT_ID");
			String gname = rs.getString("GROUP_NUMBER");
			String bid = rs.getString("SEAT_BLOCK_NAME");
			String bname = rs.getString("SEAT_BLOCK_NAME");
			String sr = rs.getString("SEAT_ROW");
			String sc = rs.getString("SEAT_COLUMN");
			choiceSeatList.add(new SeatObject(id,gname,bid,bname,sr,sc));
		}
		/*String sql = "SELECT SC.SEAT_ID, SC.GROUP_NAME,B.SEAT_BLOCK_ID,B.SEAT_BLOCK_NAME, S.SEAT_ROW, S.SEAT_COLUMN "
				+ "FROM `SEAT_CHANGE_MST` SC, SEAT_MST S, SEAT_BLOCK_MST B "
				+ "WHERE SC.SEAT_ID = S.SEAT_ID "
				+ "AND S.SEAT_BLOCK_ID = B.SEAT_BLOCK_ID "
				+ "AND SC.`ROOM_ID` = ? "
				+ "AND SC.`SCREEN_CONTENT_ID` LIKE ? "
				+ "AND SC.`USING` = 1 "
				+ "ORDER BY `B`.`SEAT_BLOCK_NAME` , S.SEAT_ROW, S.SEAT_COLUMN ASC ";
		long start =System.currentTimeMillis();
		PreparedStatement ps = null;
		ResultSet rs = null;
		con.setAutoCommit(false);
		con.commit();
		try{
			ps = con.prepareStatement(sql);
			ps.setString(1, this.subjectObject.getRoomId());
			ps.setString(2, screenContentId);
			rs = ps.executeQuery();
			while(rs.next()){
				String id = rs.getString("SEAT_ID");
				String gname = rs.getString("GROUP_NAME");
				String bid = rs.getString("SEAT_BLOCK_ID");
				String bname = rs.getString("SEAT_BLOCK_NAME");
				String sr = rs.getString("SEAT_ROW");
				String sc = rs.getString("SEAT_COLUMN");
				choiceSeatList.add(new SeatObject(id,gname,bid,bname,sr,sc));
			}
		}catch(Exception e){
			//log.error(e);
		}finally{
			//クローズ処理
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}
		long end = System.currentTimeMillis();
		//log.info("END getSeatChange:"+(end-start)+"(msec)");*/

		return choiceSeatList;
	}

	private List<Student> getAttendeeGroup(String sameClassNumber) throws SQLException{
		List<Student> attendee = new ArrayList<Student>();
		/*String sql = "SELECT A.ATTEND_ID, R.DM_BARCODE_ID, ST.STUDENT_ID, ST.FULL_NAME, A.SEAT_ID, SC.GROUP_NAME,B.SEAT_BLOCK_ID,B.SEAT_BLOCK_NAME, S.SEAT_ROW, S.SEAT_COLUMN "
				+ "FROM `ATTENDEE` A, SEAT_MST S, SEAT_BLOCK_MST B, STUDENT_MST ST, REGISTER_MST R, SEAT_CHANGE_MST SC "
				+ "WHERE SC.SEAT_ID = S.SEAT_ID "
				+ "AND A.STUDENT_ID = R.STUDENT_ID "
				+ "AND A.STUDENT_ID = ST.STUDENT_ID "
				+ "AND A.SEAT_ID = S.SEAT_ID "
				+ "AND S.SEAT_BLOCK_ID = B.SEAT_BLOCK_ID "
				+ "AND A.`SCHEDULE_ID` LIKE ? "
				+ "ORDER BY `SC`.`SELECTION_ORDER` ASC ";*/

		String sql = "SELECT A.`ATTENDANCE_ID` , A.`SEAT_POSITION` , A.`SEAT_AFTER_MOVING`,"
				+ "G.`GROUP_NUMBER`,SM.`SEAT_ID` ,SM.`SEAT_BLOCK_NAME`, SM.`SEAT_ROW`, SM.`SEAT_COLUMN`,ST.`STUDENT_ID_NUMBER`,ST.`FULL_NAME` "
				+ "FROM  `ATTENDANCES` A,  `CLASSES_MST` CM, `SEATS_MST` SM,`GROUPING` G,`REGISTRATION_MST`RM,`STUDENTS_MST` ST "
				+ "WHERE A.`CLASS_ID` = CM.`CLASS_ID` "
				+ "AND A.SEAT_POSITION = SM.SEAT_ID "
				+ "AND A.REGISTRATION_ID = RM.REGISTRATION_ID "
				+ "AND RM.STUDENT_ID = ST.STUDENT_ID "
				+ "AND SM.SEAT_ID = G.SEAT_ID "
				+ "AND A.`ATTENDANCE_TIME` IS NOT NULL "
				+ "AND CM.`SAME_CLASS_NUMBER` LIKE '"+sameClassNumber+"' ";
		Statement stmt = (Statement) con.createStatement();

		/*String sql = "SELECT A.ATTEND_ID, R.DM_BARCODE_ID, ST.STUDENT_ID, ST.FULL_NAME, A.SEAT_ID, B.SEAT_BLOCK_ID, B.SEAT_BLOCK_NAME, S.SEAT_ROW, S.SEAT_COLUMN "
				+ "FROM `ATTENDEE` A, SEAT_MST S, SEAT_BLOCK_MST B, STUDENT_MST ST, REGISTER_MST R "
				+ "WHERE A.STUDENT_ID = R.STUDENT_ID "
				+ "AND A.STUDENT_ID = ST.STUDENT_ID "
				+ "AND A.SEAT_ID = S.SEAT_ID "
				+ "AND S.SEAT_BLOCK_ID = B.SEAT_BLOCK_ID "
				+ "AND A.`SCHEDULE_ID` LIKE ?";*/

		long start =System.currentTimeMillis();
		PreparedStatement ps = null;
		ResultSet rs = null;
		con.setAutoCommit(false);
		con.commit();
		try{
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while(rs.next()){
				String attendeeId = rs.getString("ATTENDANCE_ID");
				String studentId = rs.getString("STUDENT_ID_NUMBER");
				String fullName = rs.getString("FULL_NAME");
				//String dmBarcodeId = rs.getString("DM_BARCODE_ID");
				String id = rs.getString("SEAT_ID");
				String gname = rs.getString("GROUP_NUMBER");
				String bid = rs.getString("SEAT_BLOCK_NAME");
				String bname = rs.getString("SEAT_BLOCK_NAME");
				String sr = rs.getString("SEAT_ROW");
				String sc = rs.getString("SEAT_COLUMN");
				SeatObject so = new SeatObject(id,gname,bid,bname,sr,sc);
				DM d = new DM(""/*dmBarcodeId*/);
				Student st = new Student(studentId,fullName,d,so);
				st.setAttendee(new Attendee(attendeeId));
				attendee.add(st);
			}
		}catch(Exception e){
			//log.error(e);
			System.out.println(e);
		}finally{
			//クローズ処理
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}
		long end = System.currentTimeMillis();
		//log.info("END getAttendee:"+(end-start)+"(msec)");
		return attendee;
	}

	/**グループ名を取得**/
	private List<String> getGroupNames(String seatBlockId) throws SQLException{
		List<String> groupNames = new ArrayList<String>();


		String sql = "SELECT DISTINCT(`GROUP_NUMBER`) FROM `GROUPING`G,`SEATS_MST`SM WHERE G.SEAT_ID = SM.SEAT_ID AND SM.SEAT_BLOCK_NAME LIKE ? AND SM.ROOM_ID LIKE ? AND G.ACTIVE_STATUS = 1 ORDER BY `G`.`GROUP_NUMBER` ASC";

		long start =System.currentTimeMillis();
		PreparedStatement ps = null;
		ResultSet rs = null;
		con.setAutoCommit(false);
		con.commit();
		try{
			String r =this.subjectObject.getRoomId();
			//String scid =screenContentId;
			ps = con.prepareStatement(sql);
			ps.setString(1,seatBlockId);
			ps.setString(2,r);
			rs = ps.executeQuery();
			while(rs.next()){
				String gname = rs.getString("GROUP_NUMBER");
				groupNames.add(gname);
			}
		}catch(Exception e){
			//log.error(e);
			System.out.println(e);
		}finally{
			//クローズ処理
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}
		long end = System.currentTimeMillis();
		//log.info("END getGroupName:"+(end-start)+"(msec)");
		//log.info("TODAY_GROUP_COUNT:"+groupNames.size());
		return groupNames;
	}

	private List<SeatBlockObject> getSeatBlocks() throws SQLException{
		List<SeatBlockObject> object=null;
		/*String sql = "SELECT SEAT_BLOCK_ID, SEAT_BLOCK_NAME "
				+ "FROM `SEAT_BLOCK_MST` "
				+ "WHERE `ROOM_ID` = ? "
				+ "ORDER BY `SEAT_BLOCK_MST`.`SEAT_BLOCK_NAME` ASC ";*/

		String sql = "SELECT DISTINCT(`SEAT_BLOCK_NAME`) FROM `SEATS_MST` WHERE `ROOM_ID` LIKE ? AND `ACTIVE_STATUS` = 1 ORDER BY `SEAT_BLOCK_NAME` ASC";

		long start =System.currentTimeMillis();
		PreparedStatement ps = null;
		ResultSet rs = null;
		con.setAutoCommit(false);
		con.commit();
		try{
			ps = con.prepareStatement(sql);
			ps.setString(1,this.subjectObject.getRoomId());
			rs = ps.executeQuery();
			object = new ArrayList<SeatBlockObject>();
			while(rs.next()){
				String bid = rs.getString("SEAT_BLOCK_NAME");
				String name = rs.getString("SEAT_BLOCK_NAME");
				object.add(new SeatBlockObject(bid,name));
			}
		}catch(Exception e){
			//log.error(e);
			System.out.println(e);
		}finally{
			//クローズ処理
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}
		long end = System.currentTimeMillis();
		//log.info("END getSeatBlock:"+(end-start)+"(msec)");
		return object;
	}

	private List<String> getGroupName(String seatBlockId) throws SQLException{
		List<String> groupName = new ArrayList<String>();
		String sql = "SELECT DISTINCT ( "
				+ "GROUP_NAME "
				+ ") "
				+ "FROM `SEAT_CHANGE_MST` SC, SEAT_MST S, SEAT_BLOCK_MST B "
				+ "WHERE SC.SEAT_ID = S.SEAT_ID "
				+ "AND S.SEAT_BLOCK_ID = B.SEAT_BLOCK_ID "
				+ "AND B.SEAT_BLOCK_ID = ?";
		long start =System.currentTimeMillis();
		PreparedStatement ps = null;
		ResultSet rs = null;
		con.setAutoCommit(false);
		con.commit();
		try{
			ps = con.prepareStatement(sql);
			ps.setString(1,seatBlockId);
			rs = ps.executeQuery();
			while(rs.next()){
				String gname = rs.getString("GROUP_NAME");
				groupName.add(gname);
			}
		}catch(Exception e){
			//log.error(e);
			System.out.println(e);
		}finally{
			//クローズ処理
			if(ps != null) ps.close();
			if(rs != null) rs.close();
		}
		long end = System.currentTimeMillis();
		//log.info("END getGroupName:"+(end-start)+"(msec)");
		return groupName;
	}
}
