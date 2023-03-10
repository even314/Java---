package server;

import java.awt.Font;
import java.io.BufferedInputStream;

import dao.UserDAObyMySQL;
import server.frm.Server;
import tools.SetFont;

public class CServer {
	
	public static void main(String[] args) {
		try {
			//��������
			Font font = Font.createFont(Font.TRUETYPE_FONT,new BufferedInputStream(CServer.class.getResourceAsStream("/tools/simsun.ttc")));
			font = font.deriveFont(Font.PLAIN, 12);
			SetFont.setFont(font);
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("������ش���:"+e);
		}
		try {
			UserDAObyMySQL.getconn();
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("connsql failed "+e);
		}
		new Server();
		}
}
