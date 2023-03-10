package dao;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;
import java.util.Queue;
import java.util.Vector;

import data.Record;

public class RecordDAOByFile implements DAO<Record, Integer>{
	private String suffixName = ".dat";
	private String pathAdmin = "records";
	@Override
	public Vector<Record> findAll() throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	

	@Override
//��ӹ���Ա��¼ (���Լ�¼���¼�¼��
	public boolean add(Record record) throws FileNotFoundException, IOException {
		File category=new File(pathAdmin);//records
		if(!category.exists())category.mkdir();
		File file=new File(pathAdmin+File.separator+record.getToid()+suffixName);
		ObjectOutputStream oos=new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
		oos.writeObject(record);
		oos.flush();
		oos.close();
		oos = null;		
		return true;
	}
	
	@Override
	public boolean update(Record obj) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Record findById(Integer id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
	
//���Һ��ѷ��͵����Լ�¼,����һ����¼����
	public Vector<Record>findLeaveRecord(int acnum) throws FileNotFoundException, IOException{
		File file=new File(pathAdmin+File.separator+acnum+suffixName);
		if(!file.exists())return null;
		Vector<Record> v=new Vector<>();
		ObjectInputStream ois=new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
		while(true) {//���������ļ�
			Object object=null;
			try {
				object=ois.readObject();
			} catch (Exception e) {
				// TODO: handle exception
				break;
			}
			if(object==null)break;
			if(object instanceof Record) {
				Record record=(Record)object;
				record.setRead(true);
				record.setReadTime(new Date());
				v.add(record);
			}
		}
		ois.close();
		ois=null;
		return v;
	}

	@Override
	public boolean delete(Record obj) throws Exception {
		// TODO Auto-generated method stub
		
		return false;
	}
	//ɾ���������Ϣ���Ѷ�����Ϣɾ����
	public boolean deleteRecordForAdmin(int acnum){
		File file = new File(pathAdmin+File.separator+acnum+suffixName);
		if(file.exists())
			return file.delete();
		else
			return false;
	}
}
