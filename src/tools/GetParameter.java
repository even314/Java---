package tools;

import java.util.HashMap;

public class GetParameter {


	/**
	 * Port:�����������������Ķ˿�
	 * MinDIgit:��������˺ŵ���С����
	 * MaxDigit:..��󳤶�
	 * ShieldAc:���ε��˺�
	 * IsBak���Զ�������־
	 * Driver�����ݿ�����
	 * LinkParameter�����ݿ����Ӳ���
	 * UserName��password��dbname��charset:�������ݿ�
	 */
	public String[] keys ;
	/**
	 * �����ļ���ȱʡֵ��
	 */
	public  String[] values;
	
	public HashMap<String,String> paramap;

	
	public GetParameter() {
		keys =new String[]  { "Port", "MinDigit", "MaxDigit", "ShieldAc",
				"IsBak", "Driver", "LinkParameter",
				"UserName", "Password", "DBName", "Charset" ,"TableName"};
		values =new String[] { "3608", "5", "9", "10000;88888", "1",
				 "com.mysql.jdbc.Driver", "jdbc:mysql://127.0.0.1:3306/",
					"12345user", "12345", "chatting", "gb2312","chatting.user" };
		paramap=new HashMap<>()
		{
			{
				for(int i=0;i<keys.length;i++)
				put(keys[i],values[i]);
			}
		};
	}
}
