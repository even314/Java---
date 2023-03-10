package client.frm;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.PrimitiveIterator.OfDouble;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import data.Asking;
import data.FriendUser;
import data.LoginUser;
import data.Message;
import data.Portrait;
import data.Record;
import tools.FillWidth;

//�����ܽ���
public class MainPane extends JFrame implements ActionListener{
private JLabel photoJLabel = new JLabel();//ͷ��
private JLabel nicknameJLabel=new JLabel();//�ǳ�
private JTextArea signitureTextArea=new JTextArea();//����ǩ��

private JList friendJList=null;
private DefaultListModel listModel=null;

private JButton quitButton=new JButton("�˳�");
private JButton findButton=new JButton("����");

private JButton btnCancleLogin = new JButton("ȡ����¼");
private JPanel fillWidth = new FillWidth(118, 120, Color.WHITE);

public Socket client=null;
private ObjectOutputStream oos=null;
private ObjectInputStream ois=null;

private HashMap<Integer, ChatPane>chatHashMap=null;//���촰����hashmapװ
private  FriendUser selfUser=null;//�û��Լ�������Ϣ��

private String serverIP;
private Integer serverPort;
private Integer acnum;
private String password;
private Integer state;

private FindWindow findWindow=null;

private Thread thread=null;

	public MainPane(String  serverIP, int serverPort, Integer acnum, String password, int state) {
	// TODO Auto-generated constructor stub
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		this.acnum = acnum;
		this.password = password;
		this.state = state;
		
		setTitle("Chatting");
		setSize(200, 550);
		setResizable(true);
		Toolkit tk = Toolkit.getDefaultToolkit();
		setLocation((tk.getScreenSize().width - getSize().width) - 10,
				(tk.getScreenSize().height - getSize().height) / 2 - 30);
		
		getContentPane().setBackground(Color.WHITE);
		initLoginPane();

		setVisible(true);		
		thread = new LoginThread();//��¼�߳�
		thread.start();
}
	//��¼�ȴ��������
	private void initLoginPane() {
		// TODO Auto-generated method stub
		btnCancleLogin.setPreferredSize(new Dimension(60, 20));
		btnCancleLogin.setMargin(new Insets(0, 0, 0, 0));
		btnCancleLogin.setFocusPainted(false);
		setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
		add(fillWidth);
		add(btnCancleLogin);
		
		btnCancleLogin.addActionListener(this);
	}
	
	//��ҳ��
	private void initMain(Vector<FriendUser> v) {
		selfUser = v.get(0);
		v.remove(0);
		setTitle("Chatting " + selfUser.getAcnum());
		
		//��ť
		quitButton.setMargin(new Insets(0, 5, 0, 5));
		findButton.setMargin(new Insets(0, 5, 0, 5));
	   quitButton.addActionListener(this);
	   findButton.addActionListener(this);
	    //��ť��壨�ײ���
			JPanel buttonJPanel=new JPanel();
			buttonJPanel.setLayout(new FlowLayout(FlowLayout.LEADING, 5, 5));
			buttonJPanel.add(findButton);
			buttonJPanel.add(quitButton);
		
	   //ͷ��
	   photoJLabel.setSize(50,50);
	   photoJLabel.setLocation(5,5);
	   photoJLabel.setOpaque(true);
	   photoJLabel.setBackground(new Color(116, 220, 253, 150));
	   photoJLabel.setHorizontalAlignment(SwingConstants.CENTER);
	   photoJLabel.setIcon(new Portrait(selfUser.getPhoto(), selfUser.getState()));
	   photoJLabel.setBorder(new LineBorder(new Color(60, 168, 206), 1, true));
	   
	   //�ǳ�
	   nicknameJLabel.setSize(130,20);
	   nicknameJLabel.setLocation(60,5);
	   nicknameJLabel.setText(selfUser.getNickName() );
	   
	   //����ǩ��
	   signitureTextArea.setText(selfUser.getSignature());
	   signitureTextArea.setEditable(false);
	   signitureTextArea.setLineWrap(true);
	   signitureTextArea.setBackground(getBackground());
	   JScrollPane signJScrollPane=new JScrollPane(signitureTextArea);
	   signJScrollPane.setSize(125, 25);
		signJScrollPane.setLocation(60, 30);
		signJScrollPane.setBorder(new EmptyBorder(new Insets(0, 0, 0, 0)));
		
		//�������
		JPanel topJPanel = new JPanel();
		topJPanel .setLayout(null);
		topJPanel .setPreferredSize(new Dimension(200, 60));
		topJPanel .add(photoJLabel);
		topJPanel .add(nicknameJLabel);
		topJPanel .add(signJScrollPane);
		
		//�����б�
		listModel=new DefaultListModel<>();
		for(FriendUser fu:v) {
			if(fu!=null) {
				System.out.println(fu.getAcnum()+":"+fu.getNickName());
			listModel.addElement(fu);
			}
		}
		friendJList=new JList<>();
		friendJList.setCellRenderer(new CompanyLogoListCellRenderer());
		friendJList.setModel(listModel);
		friendJList.setFixedCellHeight(50);
		friendJList.addMouseListener(new ListMouseAdapter());
		friendJList.addMouseMotionListener(new ListMouseAdapter());//����ƶ�����
		JScrollPane frdJScrollPane=new JScrollPane(friendJList);//������
		
		//���촰��hashmap��ʼ��
		chatHashMap=new HashMap<>();
		for (int i = 0; i < v.size(); i++)
			chatHashMap.put(v.get(i).getAcnum(), null);
		setVisible(false);
		btnCancleLogin.removeActionListener(this);
		remove(fillWidth);
		remove(btnCancleLogin);
		//validate();
		
		setLayout(new BorderLayout());
	
		add(topJPanel,BorderLayout.NORTH);
		add(frdJScrollPane,BorderLayout.CENTER);
		add(new FillWidth(5, 5), BorderLayout.EAST);
		add(new FillWidth(5, 5), BorderLayout.WEST);
		add(buttonJPanel, BorderLayout.SOUTH);
		setVisible(true);
		
		MainPane.this.addWindowListener(new MyWindowAdapter());
		findWindow=new FindWindow(this, true);
		//findWindow.setVisible(false);
	}
	
	private FriendUser getFriendUser(Integer acnum) {
	// TODO Auto-generated method stub
		for(int i=0;i<listModel.getSize();i++) {
			FriendUser user=(FriendUser)listModel.getElementAt(i);
			if(user.getAcnum().equals(acnum))return user;
		}
	return null;
}
	//�򿪺������촰��
	private void chatWithFriend() {
		// TODO Auto-generated method stub
		Object object=friendJList.getSelectedValue();
		if(object instanceof FriendUser) {
			FriendUser friendUser=(FriendUser)object;
			int acnum=friendUser.getAcnum();
			ChatPane chatPane=chatHashMap.get(acnum);
			if(chatPane==null) {
				chatPane=new ChatPane(oos, friendUser, selfUser, true);
				chatHashMap.put(acnum, chatPane);
			}else {
				chatPane.setVisible(true);
			}			
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if (e.getSource() == btnCancleLogin) {
			closeClient();
			System.exit(0);
			return;
		}
		if(e.getSource()==quitButton) {
			quit();
			closeClient();
			System.exit(0);
		}
		if(e.getSource()==findButton) {
			if (findWindow == null)
				findWindow = new FindWindow(this, false);
			else {
				findWindow.setVisible(true);//���Һ��Ѵ��ڿɼ�
			}
			return;			
		}
	}
	
	private void quit() {
	closeClient();
		thread.interrupt();//�жϽ���
		System.exit(0);
	}
	private void closeClient() {
		// TODO Auto-generated method stub
		try {
			if (oos != null)
				oos.close();
			oos = null;
			if (ois != null)
				ois.close();
			ois = null;
			if (client != null)
				client.close();
			client = null;
		} catch (IOException e) {
	    System.out.println("MainPane.WriteThread.closeClient()");
	      e.printStackTrace();
		}
	}
	
	//�رմ���ʱ�����������������״̬
	private class MyWindowAdapter extends WindowAdapter{
		public void windowClosing(WindowEvent e) {
			quit();
		}	
	}
	
	//��¼�̣߳�ͬʱ�����������������Ϣ
private class LoginThread extends Thread{
	public LoginThread(){
	try {
		client=new Socket(serverIP,serverPort);
	} catch (Exception e) {
		// TODO: handle exception
		System.out.println("MainPane.LoginThread.LoginThread()"+e);
	}

	MainPane.this.addWindowListener(new MyWindowAdapter());

	//���͵�������
	try {
		oos=new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
		LoginUser user=new LoginUser(acnum,password);
		user.setState(1);
		Message message=new Message(20,user);
		//System.out.println("MainPane.LoginThread.LoginThread()298");
		new WriteThread(message).start();
		ois=new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
	} catch (Exception e) {
		// TODO Auto-generated catch block
		closeClient();
		JOptionPane.showMessageDialog(null,
				"��ȷ������ķ�����IP�Ͷ˿���ȷ!" + e.getMessage());
		dispose();
		new LoginPane();
		System.out.println("MainPane.LoginThread.LoginThread()");
		e.printStackTrace();
	}	
	}
	//�߳̽��ܷ�������Ϣ
@Override
public void run() {
	// TODO Auto-generated method stub
	try {
	//	System.out.println("MainPane.LoginThread.run()345");
		Message message=null;
		while(ois!=null) {
			Object object=ois.readObject();
			if(object instanceof Message) {
			//	System.out.println("MainPane.LoginThread.run()350");
				message=(Message)object;
				int type=message.gettype();
				System.out.println("type="+type);
				switch (type) {
				case 21://��¼�ɹ������غ����б�
					//System.out.println("MainPane.LoginThread.run()��½�ɹ�");
					if(message.getobject() instanceof Vector) {
					Vector<FriendUser> friendlist=(Vector)message.getobject();
					//System.out.println("MainPane.LoginThread.run()362getvec"+friendlist.size());
					initMain(friendlist);
					}					
					break;
				case 22://��½ʧ��
					closeClient();
					JOptionPane.showMessageDialog(null, message.getobject().toString());
					dispose();
					new LoginPane();
					break;
				case 23://�˺��ڱ𴦵�¼
					causeLetClientQuit(message);
					break;
				case 31:// ���յ���Ϣ
				//	System.out.println("MainPane.LoginThread.run() 31 381");
					dealRecord(message.getobject(), type);
					break;
				case 90:// ������˳�
					causeLetClientQuit(message);
					break;
				case 25:// ����״̬�ı䣬���Ѷ����ɿ���̨��,ͷ���ı���ɫ,listҪ������Ⱦ
					dealFriendUserLogin(message);
					break;
				case 41:// �ӵ���������
					dealAsking(message);
					break;
				case 46:case 48://��Ӻ���ʧ��
					System.out.println("MainPane.LoginThread.run()");
					if(message.getobject() instanceof String)
						JOptionPane.showMessageDialog(null,(String)message.getobject());
					break;
				case 49://��Ӻ��ѳɹ�
					RefreshFriendlist(message);
					break;
				}
			}
		}
	} catch (Exception e) {
		// TODO: handle exception
		closeClient();
		JOptionPane.showMessageDialog(null,
				"�ͷ�������ӷ�������:" + e.getMessage() + ",�����µ�¼!");
		e.printStackTrace();
		System.exit(0);
	}
}

}
private void dealRecord(Object object, int type) {
	// TODO Auto-generated method stub
	//System.out.println("MainPane.dealRecord()413");
	if(object instanceof Record) {
		Record record=(Record) object;
		FriendUser friendUser=getFriendUser(record.getFromid());
		if(friendUser!=null) {
		ChatPane chatPane=chatHashMap.get(record.getFromid());
		if(chatPane==null) {
			chatPane=new ChatPane(oos, friendUser, selfUser, true);
			chatHashMap.put(record.getFromid(), chatPane);
		}
		else if (chatPane.isDisplayable()) {
			chatPane.setFocusable(true);
		}else {
			chatPane.setVisible(true);
		}
		chatPane.showRecord(friendUser.getNickName(), record, 
				Color.BLUE);
		//chatPane.historylogQueue.add(record);
		}
	}
}
public void RefreshFriendlist(Message message) {
	// TODO Auto-generated method stub
	Object object=message.getobject();
	if(object instanceof FriendUser) {
		FriendUser friend=(FriendUser)object;
		if(getFriendUser(friend.getAcnum())!=null)return;
		listModel.addElement(friend);
		friendJList.repaint();
	}
}
//���պ���������Ϣ
private void dealAsking(Message message) {
	// TODO Auto-generated method stub
	Object object=message.getobject();
	if(object instanceof Asking) {
		Asking asking=(Asking) object;
		asking.setOos(oos);
		asking.setVisible(true);
	}
}
//�������ߣ�������Ⱦ
public void dealFriendUserLogin(Message message) {
	// TODO Auto-generated method stub
	if(message.getobject() instanceof FriendUser) {
		FriendUser friendUser=(FriendUser)message.getobject();
		if(listModel.contains(friendUser)) {
			//�ҵ�Listmodel�е�frienduser
			for(int i=0;i<listModel.getSize();i++) {
			try {
				FriendUser friendinlist=(FriendUser)listModel.get(i);
				if(friendinlist.equals(friendUser)) {
					friendinlist.setState(friendUser.getState());
					friendinlist.setPhoto(friendUser.getPhoto());
					friendinlist.setNickName(friendUser.getNickName());
					friendinlist.setSignature(friendUser.getSignature());
					friendJList.repaint();//���»���jlist
					break;
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.print("MainPane.dealFriendUserLogin()");
				e.printStackTrace();
				return;
			} 
			}
		}
	}
}
//������˳�
private void causeLetClientQuit(Message message){
	closeClient();
	JOptionPane.showMessageDialog(null, message.getobject().toString());
	System.exit(0);
}

private class WriteThread extends Thread{
	Message message;
	public WriteThread(Message message) {
		this.message=message;
	//	System.out.println("MainPane.WriteThread.WriteThread()472");
	}
	@Override
	public void run() {
		// TODO Auto-generated method stub
			try {
				if(oos!=null)
				{
//				System.out.println("MainPane.WriteThread.run()479");	
				oos.writeObject(message);
				oos.flush();				
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("MainPane.WriteThread.run()");
				e.printStackTrace();
				//�޷���¼����Ҫ�ر�socket����(closeclient���)
				closeClient();
				JOptionPane.showMessageDialog(null,
						"�ͷ�������ӷ�������:" + e.getMessage() + ",�����µ�¼!");
				System.exit(0);
			}
	}

	
}

private class FindWindow extends JDialog implements ActionListener {
	private JButton okButton=new JButton("ȷ��");
	private JButton quitButton=new JButton("�˳�");
	private JLabel searchJLabel=new JLabel("������Է��˺�");
	private  JTextArea searchJTextArea=new JTextArea();	
	
	
	public FindWindow(MainPane owner, boolean modal) {
		// TODO Auto-generated constructor stub
		//ĸ���ڣ��Ƿ�Ϊģ̬
		super(owner,modal);
		setTitle("���Һ���");
		setSize(400, 200);
		setResizable(false);
		Toolkit tk = Toolkit.getDefaultToolkit();
		setLocation((tk.getScreenSize().width - getSize().width) / 2,
				(tk.getScreenSize().height - getSize().height) / 2);
		init();
		okButton.addActionListener(this);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setVisible(false);
	}

	private void init() {
		// TODO Auto-generated method stub
		okButton.setPreferredSize(new Dimension(100,20));
		quitButton.setPreferredSize(new Dimension(100,20));
		searchJLabel.setPreferredSize(new Dimension(100,20));
		searchJTextArea.setPreferredSize(new Dimension(250,20));
		setLayout(new BorderLayout());
		JPanel centerJPanel=new JPanel();
		centerJPanel.setPreferredSize(new Dimension(400,20));
		//centerJPanel.setLayout(new FlowLayout());
		centerJPanel.add(searchJLabel);
		centerJPanel.add(searchJTextArea);
		JPanel btnJPanel=new JPanel(new FlowLayout());
		btnJPanel.setPreferredSize(new Dimension(400,50));
	btnJPanel.add(okButton);
	btnJPanel.add(quitButton);
	//	add(new FillWidth(400, 10),BorderLayout.SOUTH);
	add(new FillWidth(400, 50),BorderLayout.NORTH);
	add(centerJPanel,BorderLayout.CENTER);
	add(btnJPanel,BorderLayout.SOUTH);
		quitButton.addActionListener(this);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==okButton) {
		if(	searchJTextArea.getText().toString().equals(""))return;
			try {			
				Integer friacnum=Integer.valueOf(searchJTextArea.getText());
				Message message=new Message(40,friacnum);
				new WriteThread(message).start();
			   searchJTextArea.setText("");
			} catch (Exception e2) {
				// TODO: handle exception
				//e2.printStackTrace();
				System.out.println(searchJTextArea.getText());
				return;
			}					
		}
		if(e.getSource()==quitButton) {
			searchJTextArea.setText("");
			setVisible(false);
		}
	}
	
}

//����¼���˫�����룬adapter��д
private class ListMouseAdapter extends MouseAdapter{
	public void mouseMoved(MouseEvent e) {//����ƶ����б���λ��itemΪѡ��״̬
		if (e.getSource() == friendJList) {
			friendJList.clearSelection();
			int index = friendJList.locationToIndex(e.getPoint());//���������������ڵ�itemλ��
			friendJList.setSelectedIndex(index);//����ѡ��״̬
		}
	}
	public void  mouseClicked(MouseEvent e) {//˫������������
		if (e.getSource() == friendJList) {
			if (e.getClickCount() == 2) {
				chatWithFriend();
			}
	}
}
}

/**
 * �Լ����Ƶĺ������(����������Դ)
 */
private class CompanyLogoListCellRenderer extends DefaultListCellRenderer {
	public Component getListCellRendererComponent(JList list, Object value,
			int index, boolean isSelected, boolean cellHasFocus) {
		Component retValue = super.getListCellRendererComponent(list,
				value, index, isSelected, cellHasFocus);
		if (value instanceof FriendUser) {
			FriendUser user = (FriendUser) value;
			setIcon(new Portrait(user.getPhoto(), user.getState()));
			setText("<html>" + user.getNickName() + "["
					+ user.getAcnum() + "]" + "<br><font color='red'>"
					+ user.getSignature() + "</font></html>");		
		}
		return retValue;
	}
}
}
