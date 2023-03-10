package server.frm.pane;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import dao.AskingDAObyFile;
import dao.LogDAObyFile;
import dao.RecordDAOByFile;
import dao.UserDAObyMySQL;
import data.Asking;
import data.DAOUser;
import data.FriendUser;
import data.Log;
import data.LoginUser;
import data.Message;
import data.Record;
import data.RegUser;
import tools.DateDeal;
import tools.GetParameter;
import tools.JQCreater;
import tools.FillWidth;

public class ServicePane extends JPanel implements ActionListener,Runnable{
		private JButton startButton=new JButton("����������");
		 private JButton stopButton=new JButton("�رշ�����");
		 private JTextArea logArea=new JTextArea();
		 private ServerSocket server=null;
		 //��ϣ���ͻ������ӣ���Ӧ���ߵ��û���<�˺ţ��ͻ���������>
		 public static Hashtable<Integer, ClientLink>clientHashtable=null;
		 
		 private static boolean isServiceRun=false;//�����Ƿ�������
		 
		 private String pathString="log.txt";//������־·��
		 private PrintWriter rafPrintWriter=null;
		 
		 //��ʼ����壬�������
		 public ServicePane() {
		try {
			//��д�����־�ļ�
			rafPrintWriter=new PrintWriter(new BufferedOutputStream(new FileOutputStream(new File(pathString),true)));
		} catch (FileNotFoundException e) {
			logArea.append("�����쳣������ȷ��"+pathString+"�ļ���д!ԭ������:"+e.getMessage());
			startButton.setEnabled(false);//�����������İ�ť�޷�����
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		 //����
		 setLayout(new FlowLayout(FlowLayout.CENTER));
		 logArea.setEditable(false);
		 logArea.setLineWrap(true);
		 startButton.addActionListener(this);
		 stopButton.addActionListener(this);
		 stopButton.setEnabled(false);
		 
		 JPanel pane=new JPanel();
		 pane.setPreferredSize(new Dimension(600,70));
		 pane.setLayout(new FlowLayout(FlowLayout.CENTER));
		 pane.add(startButton);
		 pane.add(stopButton);
		 
		 setLayout(new BorderLayout());
		 add(pane,BorderLayout.NORTH);
		 add(new JScrollPane(logArea),BorderLayout.CENTER);
		add(new FillWidth(4, 4),BorderLayout.WEST);//fillwidth���Զ���հ�ռλ�����
		add(new FillWidth(4,4),BorderLayout.EAST);
		 }
		 		 	 
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				if(e.getSource()==startButton) {
				try {
					startButton.setEnabled(false);
					stopButton.setEnabled(true);
					startServer();
				} catch (Exception e1) {
					// TODO: handle exception
					writeSysLog(DateDeal.getCurrentTime()+",��������������ʱ��������ԭ������:"+e1.getMessage());
				}
				}
				if(e.getSource()==stopButton) {
					startButton.setEnabled(true);
					stopButton.setEnabled(false);
					try {
						stopServer();
					} catch (Exception e1) {
						// TODO: handle exception
						writeSysLog(DateDeal.getCurrentTime()+",������ֹͣ����ʱ��������ԭ������:"+e1.getMessage());
					}
				}
			}

//����������
		private void startServer() throws ClassNotFoundException, IOException {
				// TODO Auto-generated method stub
				isServiceRun=true;//��������������
				clientHashtable=new Hashtable<Integer,ClientLink>();
				//Class.forName("tools.GetParameter");
				int port= Integer.parseInt(new GetParameter().paramap.get("Port")) ;
				server=new ServerSocket(port);
				new Thread(this).start();//�����̣߳�ִ��run���������Ϊ����run��
				writeSysLog(DateDeal.getCurrentTime()+",���������������ɹ�!�ȴ��û�����...");
			}
		private void stopServer() {
			// TODO Auto-generated method stub
			isServiceRun=false;
			Enumeration<ClientLink>en=clientHashtable.elements();//ö���࣬�൱�ڵ�����
		while(en.hasMoreElements()) {
			//��һ�˳��ͻ��˺�
			ClientLink client=en.nextElement();
			client.updateUserState(client.acnum, 2);
			client.letClientQuit();
		}
		clientHashtable.clear();
		clientHashtable=null;
		if(server!=null)
			try {
				server.close();
				server = null;
				writeSysLog(DateDeal.getCurrentTime()+",����������ֹͣ�ɹ�!");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}
		//���̣߳�serversoket��������������
		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(isServiceRun) {
				try {
					Socket client=server.accept();
					System.out.println("come accept");
					new Thread(new ClientLink(client)).start();//���߳�
				} catch (Exception e) {
					// TODO: handle exception
					writeSysLog(DateDeal.getCurrentTime()+",���������ܿͻ���ʱ�����쳣:"+e.getMessage());
				}
				
			}
		}

		//дϵͳ���������־
		 public void writeSysLog(String log) {
				// TODO Auto-generated method stub
			    logArea.append(log+"\n");
				//�������Զ��¹�
				logArea.setCaretPosition(logArea.getDocument().getLength());
				rafPrintWriter.write(log+"\n");
				rafPrintWriter.flush();
			}
		 
		 //д���ļ�
			private  void writelog(Log log) {
				// TODO Auto-generated method stub
				try {
					LogDAObyFile logDAO=new LogDAObyFile();
					logDAO.add(log);				
				} catch (Exception e) {
					// TODO: handle exception
					writeSysLog(DateDeal.getCurrentTime()+",д�������־["+log.toString()+"]ʱ��������:"+e.getMessage());
				}
			}
		
		 //�ͻ��������࣬���մ���ͻ�����Ϣ�ͷ�����
		 private class ClientLink implements Runnable,Serializable  {
					public Socket client=null;
					public ObjectInputStream ois=null;
					public ObjectOutputStream oos=null;
					public int acnum=-1;//�˺�
					public boolean equals(ClientLink cl) {
						return (this.client.equals(cl.client));
					}
					public ClientLink(Socket client) {
						this.client=client;//���ӵ�ĳ���ͻ��˵�socket����
						writeSysLog(DateDeal.getCurrentTime()+",�ͻ���"+getClientIP()+"]���ӵ������");
						try {
							ois = new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
							oos = new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
						} catch (IOException e) {
							writeSysLog(DateDeal.getCurrentTime()+",��ȡ���ͻ���"+getClientIP()+"�����ӷ�������:"+e.getMessage());
						}
					}

					/*����ͷ���message
					 * type���ͣ�
					 * ��1��ͷ�ģ�ע�������Ϣ
					10:�ͻ��˷���ע����Ϣ�������
					11:����˻ظ�ע��ɹ����ͻ���
					12:����˻ظ�ע��ʧ�ܵ��ͻ���
					
					��2��ͷ�ģ���½�����Ϣ
					20:�ͻ��˷��͵�½��Ϣ������ˣ��жϴ��û��Ƿ��ѵ�¼��
					21:��½�ɹ�����˷��ͺ�����Ϣ���ͻ���
					22:��¼ʧ�ܷ���˷��ʹ�����Ϣ���ͻ���
					23:����˷����˺��ڱ𴦵�½
					24:�ͻ��˷����˳��������
					25:����˷��ͺ������߹���
					
					��3��ͷ�ģ����ͼ�¼�����Ϣ
					30:�ͻ��˷�����Ϣ�������
					31:����˸�����Ϣ���͵��ͻ���
					
					��4��ͷ�ģ����������û��Ӻ��������Ϣ
					40:�ͻ��˷��ͺ�������
					41:��������ͻ��˷��ͺ�������
					44:��ͻ��˷�����Ӻ��ѵ�qq��
					45:�ͻ��˷��ͻ�����Ӻ�������
					47:�ͻ��˾ܾ���Ӻ��ѣ���Ӻ���ʧ��
					49:������ͻ��˷�����Ӻ��ѳɹ�
					
					��9��ͷ�ģ�ϵͳ�����Ϣ
					90:����˷������߹��ܵ��ͻ���
					*/
			@Override
					public void run() {
				// TODO Auto-generated method stub
				try {
					while(isServiceRun&&ois!=null&&oos!=null) {
						//���ܿͻ��˷�������Ϣ
						Object object=ois.readObject();
						if(object instanceof Message) {
							Message message=(Message) object;
							int type=message.gettype();
							if(type==10) {//�ͻ���ע��
								dealRegiter(message);
								break;
							}
						switch (type) {
						case 20://�ͻ��˵�¼
							{System.out.println("ServicePane.ClientLink.run()20,267");
							DealLogin(message);
							break;}
						case 24://�ͻ����˳�
							dealQuit(message);
							break;
						case 30://��������Ϣ
							dealMessage(message);
							break;
						case 40://��������
						   dealAskAddFri(message);
							break;
						case 45://������Ӻ���
							Addeach(message);
							break;
						case 47://��Ӻ���ʧ��
						dealFailAdd(message);
							break;
						}
								}
						else
							writeSysLog("�ͻ���"+getClientIP()+"���ʹ����������Ϣ�������");
					}
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println("server recieve msg failed");
					removeClientForException(this);
					closeClient();
				}
			}
			

			//�õ��ͻ��˵�ip
			private String getClientIP() {
				// TODO Auto-generated method stub
				return client==null?"[�ͻ����ѹر�,���ܻ�ȡ��Ϣ]":"["+client.getInetAddress().toString()+":"+client.getPort()+"]";						
			}
			
			//���ͻ��˷�����Ϣ
			public boolean writeToClient(Message message) {
				// TODO Auto-generated method stub
				System.out.println("ServicePane.ClientLink.writeToClient()304");
				new ClientWrite(this,message).start();
				return true;
			}
				
			//10������ע����Ϣ
		    private void dealRegiter(Message message) throws FileNotFoundException, IOException {
				if(message.getobject()instanceof DAOUser) {
								DAOUser user=(DAOUser)message.getobject();
								JQCreater creater = new JQCreater();
								int id = creater.createID();
								int num = creater.createJQ();
								creater.saveIDJQ(id, num);
								user.setId(id);
								user.setAcnum(num);
								user.setRegisterTime(new java.sql.Date(System.currentTimeMillis()));
								UserDAObyMySQL udbm=new UserDAObyMySQL();
								boolean b=udbm.add(user);
								Message regresultMessage=new Message();
								if(b) {
									regresultMessage.settype(11);				
									RegUser regUser=new RegUser();
									regUser.setAcnum(user.getAcnum());
									regUser.setNickname(user.getNickname());
									regUser.setPassword(user.getPassword());
									regresultMessage.setobject(regUser);
									writelog(getLog(user,"���û�ע��ɹ�!"));
								}
								else{
									regresultMessage.settype(12);
									regresultMessage.setobject(null);
									writelog(getLog(user,"�û�ע��ʧ��!"));
								}
								writeToClient(regresultMessage);
								}
							else {
								writeSysLog("�ͻ���"+getClientIP()+"���ʹ����������Ϣ�������");					
							}
						}
			
			//20���û���¼
			private void DealLogin(Message message) {
				if(message.getobject() instanceof LoginUser) {
					LoginUser loginUser=(LoginUser)message.getobject();
					String inputpassword=loginUser.getPassword();
					int acnum=loginUser.getAcnum();
					UserDAObyMySQL userDAO=new UserDAObyMySQL();
					try {
						DAOUser user=userDAO.findById(acnum);//�����ݿ���Ϣ�����Ķ���
						//�����ݿ�����Ϣȡ��������
						Message loginresultMessage=new Message();
						if(user!=null)//�˺Ŵ���
						{
							if(user.getPassword().equals(inputpassword)) {
								//������ȷ���ɹ���¼
								this.acnum=acnum;//��socket
								//����û��Ƿ��¼�����Ѿ���¼��֮ǰ��¼���û�����
								if(clientHashtable.containsKey(acnum))
									letClientLogout(acnum,client.getInetAddress().toString());
								user.setState(loginUser.getState());
								userDAO.update(user);
								
								//��ȡuser������Ϣ��תΪ���Ѷ��󣬷��͸��û�
								//�û��ڿͻ���Ҫ��frienduser����н�������ȥdaouser���������Ϣ
								//��˻�Ҫ����һ����ʾ�Լ���frienduser
								Vector<FriendUser>friends=new Vector<FriendUser>() ;
									FriendUser selfUser=new FriendUser(user);
										friends.add(selfUser);								
								Vector<Integer> friendlist=user.getListFriend();
								if(friendlist!=null)
								for(Integer i:friendlist) {
									DAOUser tempDaoUser=userDAO.findById(i);
								if(tempDaoUser!=null)
						{
									FriendUser fu=new FriendUser(tempDaoUser);
								 friends.add(fu);
									}
								}	
								loginresultMessage.settype(21);
								loginresultMessage.setobject(friends);
							//	System.out.println("ServicePane.ClientLink.DealLogin()548");
								writeToClient(loginresultMessage);
									clientHashtable.put(user.getAcnum(), this);
								//֪ͨ���ѣ���������
								writelog(getLog(user, "�û���¼"));
								telfriendState(user);
								//����Ƿ��к�������
								RecordDAOByFile recordDAO=new RecordDAOByFile();
								Vector<Record> v=recordDAO.findLeaveRecord(acnum);
								try {
									if(v!=null)for(Record r:v) 
									sendRecordToClient(this, r);
									recordDAO.deleteRecordForAdmin(acnum);									
								} catch (Exception e) {
									// TODO: handle exception
									writelog(getLog(user, "�������Ը��û�ʱ��������:"+e.getMessage()));
									System.out.println("send record failed "+e);
								}
								//����Ƿ�������
								AskingDAObyFile askingDAO=new AskingDAObyFile();
								Vector<Asking> va=askingDAO.findLeaveAsking(acnum);
								try {
									if(va!=null)for(Asking a:va) 
									sendAskingToClient(this, a);
									askingDAO.deleteAskingForAdmin(acnum);
									
								} catch (Exception e) {
									// TODO: handle exception
									writelog(getLog(user, "����������û�ʱ��������:"+e.getMessage()));
									System.out.println("send asking failed "+e);
								}
							}
							else {
								loginresultMessage.settype(22);
								loginresultMessage.setobject("��½�������"+"["+loginUser.getAcnum()+"]");
								writeToClient(loginresultMessage);
								writelog(getLoginLog(loginUser, "������û�["+loginUser.getAcnum()+"]��¼����"));
								closeClient();
							}
						}
						else {//�û�������
							loginresultMessage.settype(22);
							loginresultMessage.setobject("�����ڵ��û�["+loginUser.getAcnum()+"]");
							writeToClient(loginresultMessage);
							writelog (getLoginLog(loginUser, "�����ڵ��û�["+loginUser.getAcnum()+"]��¼"));
							closeClient();						
						}
					} catch (Exception e) {
						// TODO: handle exception
						writeSysLog("����"+e.getMessage());
					}

				}	else{
					writeSysLog("�ͻ���"+getClientIP()+"���ʹ����������Ϣ�������");
					closeClient();
					}
			}	
			
			//�ͻ�������
			private void letClientQuit() {
				Message message=new Message(90,"�ͷ���˶Ͽ�����");
				writeToClient(message);
				closeClient();
			}
			//�رտͻ���
			private void closeClient() {
				// TODO Auto-generated method stub
				String ip=getClientIP();
				writeSysLog(DateDeal.getCurrentTime()+",�ͻ���"+getClientIP()+"����.");
				try {
					//�ر�stream&socket
					if(oos!=null)oos.close();oos = null;
					if(ois!=null)ois.close();ois = null;
					if(client!=null)client.close();client=null;
				} catch (IOException e) {
					writeSysLog(DateDeal.getCurrentTime()+",�رյ��ͻ���"+ip+"������ʱʱ��������:"+e.getMessage());
				}
			}			
			//24���ͻ����˳�
			private void dealQuit(Message message) {
				Object object=message.getobject();
				if(object instanceof FriendUser) {
					FriendUser user=(FriendUser)object;
					writelog(getLog(user, "�û��˳�"));
				}
				updateUserState(acnum,2);
				UserDAObyMySQL userDAO=new UserDAObyMySQL();
				try {
					telfriendState(userDAO.findById(acnum));
				} catch (Exception e) {
					// TODO: handle exception
					System.out.println(e);
				}
				clientHashtable.remove(acnum);
				closeClient();
			}
		
			//30�����������ܴ���������Ϣ
			private void dealMessage(Message message) throws FileNotFoundException, IOException {
				if(message.getobject()instanceof Record) {
					Record record=(Record) message.getobject();
					record.setSendTime(new Date(System.currentTimeMillis()));
					int acnum=record.getToid();//���շ�
					if(clientHashtable.containsKey(acnum)) {
						//����
						ClientLink client=clientHashtable.get(acnum);
						sendRecordToClient(client,record);
					}
					else {
						record.setRead(false);
						record.setReadTime(new Date(System.currentTimeMillis()));
						record.setSendTime(new Date(System.currentTimeMillis()));
						RecordDAOByFile recordDAO = new RecordDAOByFile();
						recordDAO.add(record);
					}
				}else
					writeSysLog("�ͻ���"+getClientIP()+"���ʹ����������Ϣ["+message.getobject()+"]�������");
			}
			//����������������Ϣ�����շ�
			private void sendRecordToClient(ClientLink client, Record record) throws FileNotFoundException, IOException {
				// TODO Auto-generated method stub
				Message message=new Message(31,record);
				if(client.writeToClient(message)) {
					//�ɹ�����
					record.setRead(true);
					record.setReadTime(new Date(System.currentTimeMillis()));
				}
				else {
					record.setRead(false);//δ��
					//д���ļ�����
					RecordDAOByFile rdf=new RecordDAOByFile();
					rdf.add(record);
				}
			}
			
			//40���������ѣ��Ӻ�������
			private void dealAskAddFri(Message message) {
				// TODO Auto-generated method stub
				Object object=message.getobject();
				if(object instanceof Integer) {
					Integer toacnum=(Integer)object;
					UserDAObyMySQL udao=new UserDAObyMySQL();
				   DAOUser u= udao.findById(toacnum);
				   Message remsg=new Message();
				 if(u==null) {
					 System.out.println("ServicePane.ClientLink.dealAskAddFri()");
					remsg.settype(46);
					remsg.setobject("�û������ڣ����ʧ�ܣ�");
					writeToClient(remsg);
					return;
				 }
				 else {
					 //this:������ frmuser
					FriendUser frmUser=new FriendUser(udao.findById(acnum));
					FriendUser toUser=new FriendUser(u);
					Asking asking=new Asking(toUser,frmUser,false);
					if(clientHashtable.containsKey(toacnum)) {
						//�Է�����
						ClientLink client=clientHashtable.get(toacnum);
						sendAskingToClient(client, asking);
					}
					else {
						//�Է������ߣ�д���ļ�
						AskingDAObyFile adao=new AskingDAObyFile();
						try {
							adao.add(asking);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
				}
			}
			private void sendAskingToClient(ClientLink client, Asking asking) {
				Message remsg=new Message();
				remsg.settype(41);
				remsg.setobject(asking);
				client.writeToClient(remsg);
			}
			
			//45��������Ӻ���
			private void Addeach(Message message) {
				// TODO Auto-generated method stub
				//this:���շ� touser
				Object object=message.getobject();
				if(object instanceof Vector) {
					Vector<Integer> eachothernum=(Vector<Integer>) object;
				//0:from,1:to
				Message msg1=new Message();
				Message msg2=new Message();
				msg1.settype(49);
				msg2.settype(49);
				Integer frmnum=eachothernum.get(0);
				UserDAObyMySQL udao=new UserDAObyMySQL();
				DAOUser user1=udao.findById(frmnum);
				user1.getListFriend().add(acnum);
				DAOUser user2=udao.findById(acnum);
				user2.getListFriend().add(frmnum);
				try {
					udao.update(user1);
					udao.update(user2);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				FriendUser fuser1=new FriendUser(user1);
				FriendUser fuser2=new FriendUser(user2);
				msg2.setobject(fuser1);
				msg1.setobject(fuser2);
				if(clientHashtable.containsKey(frmnum)) {
				ClientLink clientLink=	clientHashtable.get(frmnum);
				clientLink.writeToClient(msg1);
				}
				writeToClient(msg2);
				}
			}
			
			//�Ӻ���ʧ�ܣ��и����⣺ֻ�������û����յ���Ϣ
		    private void dealFailAdd(Message message) {
				// TODO Auto-generated method stub
				if(message.getobject() instanceof FriendUser) {
					FriendUser frmUser=(FriendUser) message.getobject();
					if(clientHashtable.containsKey(frmUser.getAcnum())) {
						ClientLink clientLink=clientHashtable.get(frmUser.getAcnum());
						clientLink.writeToClient(new Message(48,"�Է� "+acnum+" �ܾ���������"));
					}
				}
			}
		
		  //�����û�״̬
			private void updateUserState(int acnum, int state) {
				// TODO Auto-generated method stub
				UserDAObyMySQL userdao=new UserDAObyMySQL();
				DAOUser user=userdao.findById(acnum);
				if(user!=null) {
					try {
						user.setState(state);
						userdao.update(user);
					} catch (Exception e) {
						// TODO: handle exception
						writelog(getLog(user, "�����û�״̬ʱ��������:"+e.getMessage()));
						System.out.println("update state failed"+e);
					}
					
				}
			}
			//���ߺ�����(user)��״̬�����߻����ߣ�
			private void telfriendState(DAOUser user) {
				FriendUser selfUser=new FriendUser(user);
				Message message=new Message(25,selfUser);
				Vector<Integer> friendlist=user.getListFriend();
				for(Integer i:friendlist) {
					if(clientHashtable.containsKey(i)) {
						ClientLink client=clientHashtable.get(i);
						client.writeToClient(message);
					}
				}
			}
			
			//������־����
			private Log getLog(DAOUser user, String what) {
				// TODO Auto-generated method stub
				Log log = new Log();
				log.setUserid(user.getAcnum());
				log.setIp(client.getLocalAddress().toString());
				log.setNickname(user.getNickname());
				log.setTime(new Date(System.currentTimeMillis()));
				log.setWhat(what);
				return log;
			}
			private Log getLog(FriendUser user, String what) {
				// TODO Auto-generated method stub
				Log log=new Log();
				log.setUserid(user.getAcnum());
				log.setIp(client.getLocalAddress().toString());
				log.setNickname(user.getNickName());
				log.setTime(new Date(System.currentTimeMillis()));
				log.setWhat(what);
				return log;
			}	
          private Log getLoginLog(LoginUser loginUser, String what) {
				// TODO Auto-generated method stub
    	  Log log=new Log();
    		log.setNickname("δ֪�û�");
			log.setUserid(loginUser.getAcnum());
			log.setIp(client.getLocalAddress().toString());
			log.setTime(new Date(System.currentTimeMillis()));
			log.setWhat(what);
			return log;
			}
		 }
	 
	   //��������ĳ���ͻ��˴�����Ϣ����
		 private class ClientWrite extends Thread{
				private ClientLink clientLink;
				private Message message;
				public ClientWrite(ClientLink clientLink,Message message) {
				this.clientLink=clientLink;
				this.message=message;
				}
				public void run() {
					if(clientLink.oos!=null) {
						try {
							clientLink.oos.writeObject(message);
							clientLink.oos.flush();
							System.out.println("ServicePane.ClientWrite.run()621");
						} catch (Exception e) {
							// TODO: handle exception
							System.out.println("ServicePane.ClientWrite.run()"+e);
							e.getStackTrace();
							writeSysLog("��ͻ���"+clientLink.getClientIP()+"��������ʧ��!");
						}
						
					}
				}
				}
		//ĳip���˺ŶϿ��������������
			private void letClientLogout(int acnum, String ip) {
				// TODO Auto-generated method stub
				if(clientHashtable.containsKey(acnum)) {
					ClientLink clientLink=clientHashtable.get(acnum);
					Message msg=new Message(23,"�����˺��ڱ�[IP:"+ip+"]��¼,�����˳�!");
					clientLink.writeToClient(msg);
					clientHashtable.remove(acnum);
					clientLink.closeClient();
				}
			}			 
			
			//�ͻ��˷����쳣�ͷ������Ͽ����ӣ����clienthashtable�жϿ��Ŀͻ�����Ϣ
			private void removeClientForException(ClientLink client) {
				if(client!=null&&clientHashtable!=null&&clientHashtable.contains(client)) {
					Enumeration<Integer> enumeration=clientHashtable.keys();
					while (enumeration.hasMoreElements()) {
						Integer acnum = (Integer) enumeration.nextElement();
						if(clientHashtable.get(acnum).equals(client)) {
							clientHashtable.remove(acnum);
							client.updateUserState(acnum, 2);//2:����
							break;
						}
					}										
				}
			}
}
		 

		 
