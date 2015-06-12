package jp.pmw.group;

import java.util.ArrayList;
import java.util.List;


public class GroupDistribute {

	private GroupingConfig config;
	private GroupBelong groupBelong;
	GroupDistribute(GroupingConfig config,GroupBelong belong){
		this.config=config;
		this.groupBelong=belong;
	}

	public GroupBelong getGroupBelong(){return this.groupBelong;}

	public void groupingDistribute(){

		int maximum = this.config.getGroupMaximum();
		int minimum = this.config.getGroupMinimum();

		int numberInGroupCount = 1;
		boolean processFlag = false;
		if(minimum == numberInGroupCount){
			processFlag = true;
		}
		while(!processFlag){
			int applyCount = applyGroupCount(numberInGroupCount);
			//現在何人の学生がグループに属せず宙ぶらりんかを数える
			int unGroupStudentCount = applyCount*numberInGroupCount;
			if(applyCount > 0){
				for(int i=0;i<=maximum-minimum;i++){
					int serchCount = maximum - i;
					if(unGroupStudentCount >= serchCount){
						//グループが作れるので宙ぶらりん学生だけでグループを作る
						reorganizationGroup(serchCount,numberInGroupCount);
						applyCount = applyGroupCount(numberInGroupCount);
						//現在何人の学生がグループに属せず宙ぶらりんかを数える
						unGroupStudentCount = applyCount*numberInGroupCount;
					}
				}

				if(applyCount > 0){
					//log.info("まだ宙ぶらりんの学生が"+unGroupStudentCount+"人います.");
					System.out.println("まだ宙ぶらりんの学生が"+unGroupStudentCount+"人います.");
					List<Student> stList = leaksGroupingGetPutMember(numberInGroupCount,unGroupStudentCount);
					int danglingCount = 0;
					for(int i=0;i<stList.size();i++){
						if(stList.get(i)!=null){
							++danglingCount;
						}
					}

					if(danglingCount == 0){
						//log.info("宙ぶらりんを正常に処理しました.");
						System.out.println("宙ぶらりんを正常に処理しました.");
					}else{
						boolean b = reMakeGroupingMember(stList,numberInGroupCount);
						if(b == true){
							//log.info("宙ぶらりんを正常に処理しました.");
							System.out.println("宙ぶらりんを正常に処理しました.");
						}else{
							//log.info("宙ぶらりん異常!!");
							System.out.println("宙ぶらりん異常!!");
						}
					}
				}
			}
			showChangeSeat();
			++numberInGroupCount;
			if(minimum == numberInGroupCount){
				processFlag = true;
			}
		}
	}

	/**
	 *
	 * 再グルーピングからもれてしまった学生を対応する
	 * メンバーに入れてもらう入れてもらう
	 *
	 * **/
	private List<Student> leaksGroupingGetPutMember(int nowCount,int unGroupStudentCount){
		boolean getPutMemberFlag = false;
		//int putMemberCount = 0;
		int count = this.config.getGroupMaximum()-1;
		boolean addFlag = false;
		int addMemberCount = 0;
		List<Student> isolatedList = isolatedStudents(nowCount);

		for(int i=0;i<(this.config.getGroupMaximum()-this.config.getGroupMinimum()+1);i++){
			int serch = count-i;
			System.out.println(serch+"人のグループを検索");
			//for(int k=0;k<isolatedList.size();k++){
			for(int k=isolatedList.size()-1;k>=0;k--){
				if(isolatedList.get(k)!=null){
					addFlag = false;
					if(isolatedList.get(k).getAttendee()!=null){
						addFlag =addMemberInGroup(serch,isolatedList.get(k));
					}
					if(addFlag == true){
						++addMemberCount;
						isolatedList.remove(k);
						if(addMemberCount >= unGroupStudentCount){
							break;
						}
					}
				}
			}
		}

		if(addMemberCount >= unGroupStudentCount){
			getPutMemberFlag = true;;
		}
		return isolatedList;
	}

	/**
	 *
	 * どこっかのグループをくずして人を取得してくる
	 * **/
	private boolean reMakeGroupingMember(List<Student> stList,int nowCount){
		boolean b = false;
		//log.info(stList.size()+"人がまだグループに入れていません.なので、人グループで"+this.config.getGroupMaximum()+"以上のところを崩します.");

		//必要な人数を割りだす
		int shortageCount = this.config.getGroupMinimum()*nowCount - nowCount;
		//log.info("後,"+shortageCount+"人必要です!");

		int serchCount = 0;
		for(int i=0;i<stList.size();i++){
			List<Student> makeUpMember = null;
			int needCount = this.config.getGroupMinimum();
			String gn = stList.get(i).getSeatObject().getGroupName();
			while(!b){
				int serchMemberCount = this.config.getGroupMaximum() - serchCount;
				//グループの人数が,MAXIMUM人いるところから一人ずつ取得する
				//makeUpMember = sercheGroupingOneStudentPickUp(gn,serchMemberCount);
				makeUpMember = sercheGroupingOneStudentPickUp(gn,needCount,serchMemberCount);
				if(makeUpMember.size() == 0){
					//log.error("入れてもらえるところがあるかチェックします.");
					if(this.config.getGroupMinimum() >= serchMemberCount){
						//入れてもらえるところがあるかをチェックする.
						List<Student> amaliStudents = leaksGroupingGetPutMember(serchMemberCount,serchMemberCount);
						b=true;
					}
				}else{
					for(int j=0;j<makeUpMember.size();j++){
						//新しいグループに移動する
						boolean movingFlag = addMemberSameGroupNameInGroup(gn,makeUpMember.get(i));
						if(movingFlag!=true){
							//log.error("移動できませんでした.");
						}
						b = movingFlag;
					}
				}
				++serchCount;
			}
		}
		return b;
	}


	/**
	 *
	 * 一つのグループneedCountを満たし,
	 * groupNameでないグループから一人ずつ取得する
	 *
	 * **/
	private List<Student> sercheGroupingOneStudentPickUp(String groupName,int needCount,int groupInCount){
		List<Student> stList = new ArrayList<Student>();
		boolean stopFlag = false;
		boolean nextGroupFlag = false;
		for(int i=0;i<this.groupBelong.getSeatBlockList().size();i++){
			for(int j=0;j<this.groupBelong.getSeatBlockList().get(i).getGroup().size();j++){
				if(groupName.equals(this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getGroupName())){
				}else{
					if(this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getSitingSeatCount() == groupInCount){
						nextGroupFlag = false;
						for(int k=0;k<this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().size();k++){
							if( this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k).getAttendee()!=null
									&& nextGroupFlag == false
									&& stopFlag == false){
								nextGroupFlag = true;
								Student ss =this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k);
								Student st = new Student(ss.getStudentId(),ss.getFullName(),ss.getDm(),ss.getSeatObject());
								st.setAttendee(ss.getAttendee());
								stList.add(st);
								if(stList.size() == needCount){
									stopFlag = true;
								}
								break;
							}
						}
					}
				}
			}
			if(stopFlag==true){
				break;
			}
		}
		return stList;
	}

	private void reorganizationGroup(int groupMemberMaxCount,int nowCount){
		boolean seatShortageFlag = false;
		for(int i=0;i<this.groupBelong.getSeatBlockList().size();i++){
			for(int j=0;j<this.groupBelong.getSeatBlockList().get(i).getGroup().size();j++){
				if(groupMemberMaxCount == this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getSeatCount()){
					if(nowCount == this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getSitingSeatCount(/*this.config.getGroupMaximum()*/)){
						String groupName = this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getGroupName();
						//System.out.println(groupName);
						//List<Student> stList = new ArrayList<Student>();
						int needCount = groupMemberMaxCount-nowCount;
						List<Student>stList = sercheGroupingStudents(groupName,needCount);
						for(int k=0;k<stList.size();k++){
							if(stList.get(k)!=null){
								String gn = stList.get(k).getSeatObject().getGroupName();
								String bn = stList.get(k).getSeatObject().getSeatBlockName();
								String r = stList.get(k).getSeatObject().getSeatRow();
								String c = stList.get(k).getSeatObject().getSeatColumn();
								//log.info("NOW STUDENT_ID:"+stList.get(k).getStudentId()+"GROPU_NAME:"+gn+",SEAT_BLOCK_NAME:"+bn+",SEAT_ROW:"+r+",SEAT_COLUMN:"+c);
								creatPastSeatInfo(gn,stList.get(k).getStudentId());
								this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).addMember(stList.get(k));
							}
						}
					}
				}else{
					seatShortageFlag = true;
				}
			}
		}
	}
	/**
	 *
	 * 孤立グループにいる学生を探し出す
	 *
	 * **/
	private List<Student> isolatedStudents(int nowCount){
		List<Student> stList = new ArrayList<Student>();
		for(int i=0;i<this.groupBelong.getSeatBlockList().size();i++){
			for(int j=0;j<this.groupBelong.getSeatBlockList().get(i).getGroup().size();j++){
				for(int k=0;k<this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().size();k++){
					if(nowCount == this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getSitingSeatCount()){
						if(this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k).getAttendee()!=null){
							Student s = this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k);
							stList.add(s);
						}
					}
				}
			}
		}
		return stList;
	}

	/**
	 *
	 * グループのトータル人数がmemberCountを満たすグループに入れ込む
	 * true : 入れ込むことができた
	 * false : 入れなかった
	 * **/
	private boolean addMemberInGroup(int memberCount, Student st){
		boolean b = false;
		for(int i=0;i<this.groupBelong.getSeatBlockList().size();i++){
			for(int j=0;j<this.groupBelong.getSeatBlockList().get(i).getGroup().size();j++){
				String gn = this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getGroupName();
				for(int k=0;k<this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().size();k++){
					if(memberCount == this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getSitingSeatCount() && b==false){
						b=true;
						creatPastSeatInfo(gn,st.getStudentId());
						this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).addMember(st);
						if(b==true){
							break;
						}
					}
				}
				if(b==true){
					break;
				}
			}
			if(b==true){
				break;
			}
		}
		return b;
	}
	/**
	 *
	 * グループ名が同じグループに入れ込む
	 * true : 入れ込むことができた
	 * false : 入れなかった
	 * **/
	private boolean addMemberSameGroupNameInGroup(String groupName, Student st){
		boolean b = false;
		for(int i=0;i<this.groupBelong.getSeatBlockList().size();i++){
			for(int j=0;j<this.groupBelong.getSeatBlockList().get(i).getGroup().size();j++){
				String gn = this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getGroupName();
				if(groupName.equals(gn) && b==false){
					creatPastSeatInfo(gn,st.getStudentId());
					this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).addMember(st);
					if(b==true){
						break;
					}
				}
				if(b==true){
					break;
				}
			}
			if(b==true){
				break;
			}
		}
		return b;
	}
	/**
	 * groupMemberMaxCountを満たすグループから、
	 * 一人ずつピックアップしneedCount人集める
	 *
	 * **/
	private void reorganizationBreakInGroup(int groupMemberMaxCount,int nowCount,int needCount){
		for(int i=0;i<this.groupBelong.getSeatBlockList().size();i++){
			for(int j=0;j<this.groupBelong.getSeatBlockList().get(i).getGroup().size();j++){
				if(nowCount == this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getSitingSeatCount(/*this.config.getGroupMaximum()*/)){
					String groupName = this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getGroupName();
					/*List<Student> stList = new ArrayList<Student>();
					for(int k=0;k<needCount;k++){
						stList.add(sercheGroupingStudent(groupName,groupMemberMaxCount));
					}*/
					List<Student>stList = sercheGroupingOneStudentPickUp(groupName,needCount);
					for(int k=0;k<stList.size();k++){
						if(stList.get(k)!=null){
							String gn = stList.get(k).getSeatObject().getGroupName();
							String bn = stList.get(k).getSeatObject().getSeatBlockName();
							String r = stList.get(k).getSeatObject().getSeatRow();
							String c = stList.get(k).getSeatObject().getSeatColumn();
							creatPastSeatInfo(gn,stList.get(k).getStudentId());
							//log.info("NOW STUDENT_ID:"+stList.get(k).getStudentId()+"GROPU_NAME:"+gn+",SEAT_BLOCK_NAME:"+bn+",SEAT_ROW:"+r+",SEAT_COLUMN:"+c);
							this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).addMember(stList.get(k));
						}
					}
				}
			}
		}
	}

	private Student sercheGroupingStudent(String groupName,int maxSitting){
		Student st = null;
		boolean stopFlag = false;
		for(int i=0;i<this.groupBelong.getSeatBlockList().size();i++){
			for(int j=0;j<this.groupBelong.getSeatBlockList().get(i).getGroup().size();j++){
				if(groupName.equals(this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getGroupName())){
				}else{
					if(this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getSitingSeatCount() == maxSitting){
						for(int k=0;k<this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().size();k++){
							if( this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k).getAttendee()!=null){
								Student ss =this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k);
								st = new Student(ss.getStudentId(),ss.getFullName(),ss.getDm(),ss.getSeatObject());
								st.setAttendee(ss.getAttendee());
								this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k).setStudentInfo(null, null, null, null);
								stopFlag = true;
								break;
							}
						}
					}
				}
			}
			if(stopFlag==true){
				break;
			}
		}
		return st;
	}
	private List<Student> sercheGroupingStudents(String groupName,int needCount){
		List<Student> stList = new ArrayList<Student>();
		boolean stopFlag = false;
		for(int i=0;i<this.groupBelong.getSeatBlockList().size();i++){
			for(int j=0;j<this.groupBelong.getSeatBlockList().get(i).getGroup().size();j++){
				if(groupName.equals(this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getGroupName())){
				}else{
					if(this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getSitingSeatCount() == needCount && stopFlag == false){
						for(int k=0;k<this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().size();k++){
							if( this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k).getAttendee()!=null){
								Student ss =this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k);
								Student st = new Student(ss.getStudentId(),ss.getFullName(),ss.getDm(),ss.getSeatObject());
								st.setAttendee(ss.getAttendee());
								stList.add(st);
								//this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k).setStudentInfo(null, null, null, null);
								if(stList.size() == needCount){
									stopFlag = true;
									break;
								}
							}
						}
					}
				}
			}
			if(stopFlag==true){
				break;
			}
		}
		return stList;
	}

	/**
	 *
	 * 一つのグループneedCountを満たし,
	 * groupNameでないグループから一人ずつ取得する
	 *
	 * **/
	private List<Student> sercheGroupingOneStudentPickUp(String groupName,int needCount){
		List<Student> stList = new ArrayList<Student>();
		boolean stopFlag = false;
		boolean nextGroupFlag = false;
		for(int i=0;i<this.groupBelong.getSeatBlockList().size();i++){
			for(int j=0;j<this.groupBelong.getSeatBlockList().get(i).getGroup().size();j++){
				if(groupName.equals(this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getGroupName())){
				}else{
					if(this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getSitingSeatCount() == needCount){
						nextGroupFlag = false;
						for(int k=0;k<this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().size();k++){
							if( this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k).getAttendee()!=null
									&& nextGroupFlag == false
									&& stopFlag == false){
								nextGroupFlag = true;
								Student ss =this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k);
								Student st = new Student(ss.getStudentId(),ss.getFullName(),ss.getDm(),ss.getSeatObject());
								st.setAttendee(ss.getAttendee());
								stList.add(st);
								if(stList.size() == needCount){
									stopFlag = true;
								}
								break;
							}
						}
					}
				}
			}
			if(stopFlag==true){
				break;
			}
		}
		return stList;
	}

	private void creatPastSeatInfo(String groupName,String studentId){
		for(int i=0;i<this.groupBelong.getSeatBlockList().size();i++){
			for(int j=0;j<this.groupBelong.getSeatBlockList().get(i).getGroup().size();j++){
				if(this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getGroupName().equals(groupName)){
					this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).crearAttendeeInfo(studentId);
				}
			}
		}
	}

	/**
	 *
	 * nowCountを満たすグループがいくあるかを調べる
	 *
	 * **/
	private int applyGroupCount(int nowCount){
		int applyCount=0;
		for(int i=0;i<this.groupBelong.getSeatBlockList().size();i++){
			for(int j=0;j<this.groupBelong.getSeatBlockList().get(i).getGroup().size();j++){
				int count = this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getSitingSeatCount(/*this.config.getGroupMaximum()*/);
				if(count == nowCount){
					++applyCount;
					//log.info(this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getGroupName()+"グループ , COUNT:"+nowCount);
				}

			}
		}
		return applyCount;
	}


	private void showChangeSeat(){
		boolean b = false;
		for(int i=0;i<this.groupBelong.getSeatBlockList().size();i++){
			for(int j=0;j<this.groupBelong.getSeatBlockList().get(i).getGroup().size();j++){
				for(int k=0;k<this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().size();k++){
					if(this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k).getPastSeatObject()!=null){
						b = true;
						SeatObject p = this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k).getPastSeatObject();
						SeatObject s = this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k).getSeatObject();
						System.out.println(p.getGroupName()+"から,"+s.getGroupName());
					}
				}
			}
		}
		if(b==false){
			System.out.println("移動した学生はいません.");
		}
	}


	public void show(){
		for(int i=0;i<this.groupBelong.getSeatBlockList().size();i++){
			for(int j=0;j<this.groupBelong.getSeatBlockList().get(i).getGroup().size();j++){
				for(int k=0;k<this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().size();k++){
					String groupName = this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getGroupName();
					String studentIdNumber = this.groupBelong.getSeatBlockList().get(i).getGroup().get(j).getMemeber().get(k).getStudentId();
					System.out.println(groupName+","+studentIdNumber);
				}
			}
		}
	}

}
