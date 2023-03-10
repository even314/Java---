package client.frm;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import data.DAOUser;
import data.Message;
import data.Portrait;
import data.RegUser;
import tools.GetParameter;
import tools.FillWidth;

public class RegisterPane extends JFrame implements ActionListener{
	//��ǩ
	private JLabel nickNamelaLabel = new JLabel("�û��ǳ�:");
	private JLabel emailLabel = new JLabel("E-mail:");
	private JLabel passwordLabel = new JLabel("��¼����:");
	private JLabel rePassLabel= new JLabel("�ظ�����:");
	private JLabel sexLabel = 	new JLabel("��    ��:");
	private JLabel agelaLabel = new JLabel("����:");
	private JLabel signaturelaLabel = new JLabel("����ǩ��:");
	
	//�����
	private JTextField nickNameField = new JTextField("");
	private JTextField emailField= new JTextField("");
	private JPasswordField passwordField = new JPasswordField("");
	private JPasswordField rePassField = new JPasswordField("");
	private JComboBox  sexBox = new JComboBox();
	private JTextField ageField= new JTextField("0");
	private JTextArea signatureArea = new JTextArea();
	
	//ͷ��
	private JLabel photoJLabel = new JLabel();
	private JButton changeButton = new JButton("����ͷ��");
	
	//��ť
	private JButton okButton = new JButton("ע��");
	private JButton cancleButton = new JButton("ȡ��");
	
	private ChooseProtrait chooseProtrait = null;
	
	private Socket client=null;
	private ObjectOutputStream oos=null;
	private ObjectInputStream ois=null;
	

	public RegisterPane() {
		setTitle("���û�ע��");
		setSize(330,343);
		setResizable(false);
		Toolkit tk=Toolkit.getDefaultToolkit();
		setLocation((tk.getScreenSize().width-getSize().width)/2,(tk.getScreenSize().height-getSize().height)/2);
		init();
		changeButton.addActionListener(this);
		cancleButton.addActionListener(this);
		okButton.addActionListener(this);
		
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	//��ʼ�����
	private void init() {
		// TODO Auto-generated method stub
		setLayout(null);
		//��ǩ��С��ʽ
		nickNamelaLabel.setPreferredSize(new Dimension(60,20));
		emailLabel.setPreferredSize(new Dimension(60,20));
		passwordLabel.setPreferredSize(new Dimension(60,20));
		rePassLabel.setPreferredSize(new Dimension(60,20));
		sexLabel.setPreferredSize(new Dimension(60,20));
		agelaLabel.setPreferredSize(new Dimension(30,20));
		signaturelaLabel.setPreferredSize(new Dimension(60,20));
		
		nickNameField.setPreferredSize(new Dimension(120,20));
		emailField.setPreferredSize(new Dimension(120,20));
		passwordField.setPreferredSize(new Dimension(120,20));
		rePassField.setPreferredSize(new Dimension(120,20));
		sexBox.setPreferredSize(new Dimension(40,20));
		ageField.setPreferredSize(new Dimension(40,20));
		JScrollPane sp = new JScrollPane(signatureArea);
		sp.setPreferredSize(new Dimension(220,60));
		
		photoJLabel.setOpaque(true);
		photoJLabel.setBackground(Color.WHITE);
		photoJLabel.setHorizontalAlignment(SwingConstants.CENTER);
		photoJLabel.setPreferredSize(new Dimension(50,50));
		photoJLabel.setBorder(new LineBorder(Color.DARK_GRAY));
		photoJLabel.setIcon(new Portrait());
		changeButton.setPreferredSize(new Dimension(60,20));
		changeButton.setMargin(new Insets(0,0,0,0));
		
		sexBox.addItem("��");
		sexBox.addItem("Ů");
		
		//�����
		JPanel requrePanel = new JPanel();
		requrePanel.setBorder(new TitledBorder(new LineBorder(Color.GRAY),"����ѡ��"));
		requrePanel.setSize(210,135);
		requrePanel.setLocation(10, 10);
		requrePanel.setLayout(new FlowLayout(FlowLayout.LEFT,5,6));
		requrePanel.add(nickNamelaLabel);
		requrePanel.add(nickNameField);
		requrePanel.add(emailLabel);
		requrePanel.add(emailField);
		requrePanel.add(passwordLabel);
		requrePanel.add(passwordField);
		requrePanel.add(rePassLabel);
		requrePanel.add(rePassField);
		
		//ѡ���
		JPanel unrequireJPanel = new JPanel();
		unrequireJPanel.setBorder(new TitledBorder(new LineBorder(Color.GRAY),"ѡ��ѡ��"));
		unrequireJPanel.setSize(305,125);
		unrequireJPanel.setLocation(10, 150);
		unrequireJPanel.setLayout(new FlowLayout(FlowLayout.LEFT,5,6));
		unrequireJPanel.add(sexLabel);
		unrequireJPanel.add(sexBox);
		unrequireJPanel.add(agelaLabel);
		unrequireJPanel.add(ageField);
		unrequireJPanel.add(new FillWidth(80,20));
		unrequireJPanel.add(signaturelaLabel);
		unrequireJPanel.add(sp);
		
		
		//ͷ��ѡ����
		JPanel panePhoto = new JPanel();
		panePhoto.setBorder(new TitledBorder(new LineBorder(Color.GRAY),"ͷ��"));
		panePhoto.setSize(85,135);
		panePhoto.setLocation(230, 10);
		panePhoto.setLayout(new FlowLayout(FlowLayout.CENTER,5,8));
		panePhoto.add(new FillWidth(50,4));
		panePhoto.add(photoJLabel);
		panePhoto.add(changeButton);
		
		//��ťѡ����
		JPanel buttonPanel = new JPanel();
		buttonPanel.setSize(305,30);
		buttonPanel.setLocation(10, 275);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT,2,5));
		buttonPanel.add(new FillWidth(100,20));
		buttonPanel.add(okButton);
		buttonPanel.add(new FillWidth(8,20));
		buttonPanel.add(cancleButton);
		
		add(requrePanel);
		add(unrequireJPanel);
		add(panePhoto);
		add(buttonPanel);
	}
	
	//��ť�¼� ע�ᡢȡ��
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		if(e.getSource()==cancleButton) {
			dispose();
			new LoginPane();
		}
		if(e.getSource()==changeButton) {//��ͷ��(����������Դ)
			if(chooseProtrait==null)
				new ChooseProtrait();
			else
				chooseProtrait.setVisible(true);//��ʾ��ͷ��Ի���			
		}
		if(e.getSource()==okButton) {
			if(nickNameField.getText().toString().equals("")||emailField.getText().toString().equals(""))return;
			//ע�ᰴť ��֤��ʽ���������룩����ע����Ϣ��������
			try {
				//���������Ƿ�һ��
				String pwdString= new String(passwordField.getPassword());
				String repwdsString=new String(rePassField.getPassword());
				if(pwdString.equals("")||pwdString.equals(""))return;
				if(pwdString.equals(repwdsString)) 
				{	
					okButton.setEnabled(false);
					//System.out.println("RegisterPane.actionPerformed()207");
				new RegNewUser().start();
				}
				else {
					JOptionPane.showMessageDialog(null,"�������벻һ�£�");
				}
			} catch (Exception e2) {
				// TODO: handle exception
				System.out.println("RegisterPane.actionPerformed()"+e2);
			}
		}
	}
	//ע�����û����߳� ��������ύ��Ϣ
	private class RegNewUser extends Thread {
		   public RegNewUser() throws IOException {			  
			try {
				client=new Socket("127.0.0.1",Integer.parseInt(new GetParameter().paramap.get("Port")));
			} catch (NumberFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (UnknownHostException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			oos=new ObjectOutputStream(new BufferedOutputStream(client.getOutputStream()));
			DAOUser user=new DAOUser(nickNameField.getText(),emailField.getText(),new String(passwordField.getPassword()) );
			user.setSex(sexBox.getSelectedItem().toString());
			try {
				user.setAge(Integer.parseInt(ageField.getText()));
			} catch (Exception e) {
				// TODO: handle exception
				user.setAge(0);
			}
			user.setSignature(signatureArea.getText());
			user.setPhoto(((Portrait)photoJLabel.getIcon()).getNum());
			Message message=new Message(10,user);
			//����������
			oos.writeObject(message);
			oos.flush();
			//���շ�������Ӧ
			ois=new ObjectInputStream(new BufferedInputStream(client.getInputStream()));
		}
		   //���շ�������Ӧ��������߳�
		   @Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				while(ois!=null) {
					Object object=ois.readObject();
					if(object instanceof Message) {
						Message message=(Message)object;
						int type=message.gettype();
						switch (type) {
						case 11://ע��ɹ�
							//����������reguser������������ɵ��˺ź��û�����
							RegUser xuser=(RegUser)message.getobject();		
							new RegSuccess(xuser,RegisterPane.this,true);							
							closeClient();
							break;
						case 12:
							JOptionPane.showMessageDialog(null, "ע��ʧ��!������ע��!");
							okButton.setEnabled(true);
							closeClient();
							break;
						case 90:
							JOptionPane.showMessageDialog(null, message.getobject().toString());
							closeClient();
							System.exit(0);
							break;
						}						
					}	break;				
				}
			} catch (Exception e) {
				// TODO: handle exception
				System.out.println("RegisterPane.RegNewUser.run()"+e);
				JOptionPane.showMessageDialog(null, "��������,ԭ��:"+e.getMessage());
				closeClient();
			}
		}
		private void closeClient() {
			// TODO Auto-generated method stub
			//�ر����������
		try {
			if(oos!=null)oos.close();oos=null;
			if(ois!=null)ois.close();ois=null;
			if(client!=null)client.close();client=null;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("RegisterPane.RegNewUser.closeClient()"+e);
		}
		}
	}
	
	//ע��ɹ�����
	private class RegSuccess extends JDialog implements ActionListener{
		private JTextArea infoArea=new JTextArea();
		private JButton loginButton=new JButton("ֱ�ӵ�¼");
		private JButton returnButton=new JButton("���ص�¼����");
		private RegUser user;
		
		//owner:�����ڣ�modal:�Ƿ�ģ̬
		public RegSuccess(RegUser user,Frame owner,boolean modal) {
		super(owner,modal);
		this.user=user;
		setSize(250,200);
		setResizable(false);
		Toolkit tk=Toolkit.getDefaultToolkit();
		setLocation((tk.getScreenSize().width-getSize().width)/2,(tk.getScreenSize().height-getSize().height)/2);
		setTitle("ע����Ϣ");
		
		infoArea.setText("��ϲ!,ע��ɹ���\n"+
				"�û��ǳ�:"+user.getNickname()+"\n"+
				"��½����:"+user.getAcnum()+"\n"+
				"��½����:"+user.getPassword()+"\n"+
				"�����Ʊ������ĺ��������!");
		infoArea.setEditable(false);
		infoArea.setOpaque(true);
		infoArea.setBackground(this.getBackground());
		infoArea.setPreferredSize(new Dimension(200,100));
		infoArea.setBorder(new TitledBorder(new LineBorder(Color.DARK_GRAY),"ע����Ϣ"));
		setLayout(new FlowLayout(FlowLayout.CENTER,10,10));
		
		loginButton.addActionListener(this);
		returnButton.addActionListener(this);
		add(infoArea);
		add(loginButton);
		add(returnButton);
		
		setVisible(true);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if(e.getSource()==loginButton) {
				dispose();
				RegisterPane.this.dispose();//�ر�ע��ҳ registerpane��this������
			try {
				new MainPane("127.0.0.1",Integer.parseInt(new GetParameter().paramap.get("Port")),
						user.getAcnum(),user.getPassword(),1);
			} catch (Exception e2) {
				System.out.println("RegisterPane.RegSuccess.actionPerformed()"+e);
				// TODO: handle exception
			}
			}
			if(e.getSource()==returnButton) {
				dispose();
				RegisterPane.this.dispose();
				new LoginPane();
			}
		}
	}
	//ͷ��ѡ�񴰿�(����������Դ)
	private class ChooseProtrait extends JDialog implements ActionListener{
		private JButton[] btnPortrait = new JButton[158];
		private Portrait[] portraits = new Portrait[158];
		
		private JLabel lblBoys = new JLabel("��ʿͷ��(��30��)");
		private JLabel lblGirls = new JLabel("Ůʿͷ��(��29��)");
		private JLabel lblAnimals = new JLabel("����ͷ��(��36��)");
		private JLabel lblOthers = new JLabel("����ͷ��(��63��)");
		
		private JLabel lblViewInfo = new JLabel("Ԥ��:");
		private JLabel lblPhotoView = new JLabel();
		
		private JButton btnP_Ok = new JButton("ȷ��");
		private JButton btnP_Cancle = new JButton("ȡ��");
		
		public ChooseProtrait() {
			setTitle("ѡ��ͷ��");
			setSize(500,440);
			setResizable(false);
			Toolkit tk=Toolkit.getDefaultToolkit();
			setLocation((tk.getScreenSize().width-getSize().width)/2,(tk.getScreenSize().height-getSize().height)/2);
			
			btnP_Ok.setSize(80,20);
			btnP_Ok.setLocation(300, 375);
			btnP_Ok.addActionListener(this);
			btnP_Cancle.setSize(80,20);
			btnP_Cancle.setLocation(400, 375);
			btnP_Cancle.addActionListener(this);
			
			//��ʼ����ť������ͷ����ʾ�İ�ť��
			for(int i=0;i<btnPortrait.length;i++){
				btnPortrait[i] = new JButton();
				btnPortrait[i].setMargin(new Insets(0,0,0,0));
				btnPortrait[i].setPreferredSize(new Dimension(50,50));
				btnPortrait[i].addActionListener(this);
				btnPortrait[i].setOpaque(true);
				btnPortrait[i].setBackground(Color.WHITE);
			}
			//��ʼ��Ԥ��ͷ��
			lblPhotoView.setOpaque(true);
			lblPhotoView.setBackground(Color.WHITE);
			lblPhotoView.setHorizontalAlignment(SwingConstants.CENTER);
			lblPhotoView.setPreferredSize(new Dimension(50,50));
			lblPhotoView.setBorder(new LineBorder(Color.DARK_GRAY));
			lblPhotoView.setIcon(photoJLabel.getIcon());
			lblPhotoView.setSize(50,50);
			lblPhotoView.setLocation(420, 40);
			lblViewInfo.setSize(60,20);
			lblViewInfo.setLocation(425, 10);
			
			initJLabel(lblBoys);
			initJLabel(lblGirls);
			initJLabel(lblAnimals);
			initJLabel(lblOthers);
			
			JPanel paneBoys = getPane(0, 30);
			JPanel paneGirls = getPane(30, 60);
			JPanel paneAnimals = getPane(60, 96);
			JPanel paneOthers = getPane(96, 158);
			
			JPanel panePortrait = new JPanel();
			panePortrait.setPreferredSize(new Dimension(380,1500));
			panePortrait.setOpaque(true);
			panePortrait.setBackground(Color.WHITE);
			panePortrait.add(lblBoys);
			panePortrait.add(paneBoys);
			panePortrait.add(lblGirls);
			panePortrait.add(paneGirls);
			panePortrait.add(lblAnimals);
			panePortrait.add(paneAnimals);
			panePortrait.add(lblOthers);
			panePortrait.add(paneOthers);
			
			JScrollPane sp = new JScrollPane(panePortrait);
			sp.setSize(400,350);
			sp.setLocation(10,5);
			setLayout(null);
			
			JPanel paneAll = new JPanel();
			paneAll.setSize(480,365);
			paneAll.setLocation(5, 0);
			paneAll.setOpaque(true);
			paneAll.setBackground(Color.WHITE);
			paneAll.setBorder(new LineBorder(Color.BLACK));
			paneAll.setLayout(null);
			paneAll.add(sp);
			paneAll.add(lblViewInfo);
			paneAll.add(lblPhotoView);
			
			add(paneAll);
			add(btnP_Ok);
			add(btnP_Cancle);
			
			//�������̼߳���ͷ�񵽰�ť�ϣ��ӿ�Ի������ʾʱ��
			new Thread(){
				public void run() {
					for(int i=0;i<btnPortrait.length;i++){
						portraits[i] = new Portrait(i+1);
						btnPortrait[i].setIcon(portraits[i]);
					}
				}
			}.start();
			
			
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			setVisible(true);
		}
		
		/**
		 * ��ʼ��һЩ��壬����Ϊ��㷽����
		 * @param pane
		 */
		private void initJLabel(JLabel pane){
			pane.setOpaque(true);
			pane.setBackground(new Color(226,247,254));
			pane.setPreferredSize(new Dimension(380,25));
			pane.setBorder(new LineBorder(Color.BLACK));
		}
		
		/**
		 * ���ݿ�ʼ�ͽ��������塣
		 * @param begin ��ʼ��ͷ��λ�á�
		 * @param end ������ͷ��λ�á�
		 * @return ��Ӻ�ͷ�����塣
		 */
		private JPanel getPane(int begin,int end){
			JPanel pane = new JPanel();
			pane.setOpaque(true);
			pane.setBackground(Color.WHITE);
			pane.setLayout(new GridLayout(0,7,5,5));
			for(int i = begin;i<end;i++)
				pane.add(btnPortrait[i]);
			
			return pane;
		}
		
		public void actionPerformed(ActionEvent e) {
			//���ȷ��ʱ����ѡ���ͼ��
			if(e.getSource()==btnP_Ok){
				photoJLabel.setIcon(lblPhotoView.getIcon());
				dispose();
				return;
			}
			//���ȷ��ʱ�ر�ѡ��ͼ��Ĵ���
			if(e.getSource()==btnP_Cancle){
				dispose();
				return;
			}
			//�����ͼ��İ�ťʱ����ʾͼ�뵽���ͷ�������
			int i = -1;
			for(i=0;i<btnPortrait.length;i++){
				if(e.getSource()==btnPortrait[i])
					break;
			}
			if(i<btnPortrait.length){
				lblPhotoView.setIcon(portraits[i]);
			}
				
		}
	}
	public static void main(String[] args) {
		RegisterPane r=new RegisterPane();		
	}
}
