package jp.pmw.group;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import jp.pmw.mysql.ConectObject;
import jp.pmw.mysql.Connect;


public class Main {
	//ダミー同一授業識別番号
	private static String DUMY_SAME_CLASS_NUMBER = "150608130000143000ROOM3050030001";
	//ダミー教室ID
	private static String DUMY_ROOM_ID = "ROOM3050030001";
	//
	private static int howManyMembers = 4;
	//MySQLコネクション
	private static Connection con = null;

	private static int minimamuMemberCount = 3;
	private static int maximumMemberCount = 4;

	public static void main(String[] args) {
		// TODO 自動生成されたメソッド・スタブ

		try {
			con = getConnection();

			SubjectConfig subjectConfig = new SubjectConfig(con);
			subjectConfig.setConfig(DUMY_ROOM_ID);
			List<SeatObject> changeSeatList = subjectConfig.getSeatChangeData(DUMY_ROOM_ID,howManyMembers);
			List<Student> attendeeList = subjectConfig.getAttendeeData(DUMY_SAME_CLASS_NUMBER);
			if(attendeeList.size() < subjectConfig.getSubjectObject().getGroupingConfig().getGroupMinimum()){
				//log.info("出席者が足りません!");
				System.out.println("出席者が足りません!");
			}else{
				/**さまざまな出席者の状態に対応できていません!**/
				//出席者にグループ情報を持たせる

				//System.out.println("aa");

				//グループの状態を変数に格納する
				subjectConfig.checkGroupAllAttendee("aa",changeSeatList,attendeeList);
				GroupingConfig gc = subjectConfig.getSubjectObject().getGroupingConfig();
				GroupBelong belong = subjectConfig.getGroupBelong();
				GroupDistribute dist = new GroupDistribute(gc,belong);

				//System.out.println("開始前");
				//dist.show();

				//グループの振り分けを行う
				dist.groupingDistribute();
				belong = dist.getGroupBelong();

				//System.out.println("開始後");
				//dist.show();
				/*
						log.info("Start MovingGroupSQL");
						MovingGroupSQL mgSQL = new MovingGroupSQL(db.getMySQLConnection(),belong);
						try {
							int count = mgSQL.updateSeatRevisionDistination();
							log.info("End MovingGroupSQL");
							resultCSV(count,belong);
						} catch (SQLException e) {
							log.error(e);
						}

					}*/
			}

			/*
				GroupingConfig gc = new GroupingConfig(maximumMemberCount,minimamuMemberCount);


				GroupBelong gb = new GroupBelong(orijinalGroupsInfo);
				System.out.println("再編成前");
				showGroupDistribute(gb);


				GroupDistribute gd = new GroupDistribute(gc,gb);
				gd.groupingDistribute();
				GroupBelong belong = gd.getGroupBelong();
				System.out.println("再編成後");
				showGroupDistribute(belong);


			 */
			int a = 0;
			a = 12;
		} catch (SQLException e) {
			System.out.println(e);
		}
	}




	private static Connection getConnection() throws SQLException{
		ConectObject object = new ConectObject();
		object.setHost("127.0.0.1");
		object.setUsr("root");
		object.setPassWord("");
		object.setDb("catalunya_ait_2015");

		Connect.getInstance().setConnection(object);

		return Connect.getInstance().getConnection();
	}



}