package abanstudio.command;
/*

 */
public class CommandData
{
	String regex;
	String comm;
	String desc_sh;
	String desc;
	int adminLevel;

	public CommandData(String regex, String comm, String desc_sh){
		setDefaults();
		this.regex = regex;
		this.comm = comm;
		this.desc_sh = desc_sh;
	}
	private void setDefaults(){
		desc = "null";
		adminLevel = 0;
	}
	public String getRegex(){
		return regex;
	}

	public String getComm(){
		return comm;
	}
	public String getDescS()
	{
		return desc_sh;
	}
	public String getDesc()
	{
		return desc;
	}

	public CommandData setDesc(String desc)
	{
		this.desc = desc;
		return this;
	}

	public int getAdminLevel()
	{
		return adminLevel;
	}

	public CommandData setAdminLevel(int adminLevel)
	{
		this.adminLevel = adminLevel;
		return this;
	}
}
