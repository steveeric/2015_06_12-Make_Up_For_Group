package jp.pmw.group;

public class GroupingConfig {
	private int groupMaximum;
	private int groupMinimum;
	GroupingConfig(int maximum,int minimum){
		this.groupMaximum=maximum;
		this.groupMinimum=minimum;
	}

	public int getGroupMaximum(){return this.groupMaximum;}
	public int getGroupMinimum(){return this.groupMinimum;}
}
