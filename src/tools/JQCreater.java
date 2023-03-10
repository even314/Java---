
package tools;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.StringTokenizer;


/**
 * �˺��������ࡣ ������������Դ��
 */
public class JQCreater {

	private String[] part = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
			"0" };
	private int minDigit;
	private int maxDigit;
	private String notAllowed;

	private String path = "jid.dat";

	public JQCreater() {
HashMap<String, String>	paramap=new GetParameter().paramap;
		minDigit = Integer.parseInt(paramap.get("MinDigit"));
		maxDigit = Integer.parseInt(paramap.get("MaxDigit"));
		notAllowed = paramap.get("ShieldAc");

	}

	/**
	 * ���ر����˺ŵ��ļ���
	 * 
	 * @return ���ر����˺ŵ��ļ���
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private RandomAccessFile getFile() throws FileNotFoundException,
			IOException {
		RandomAccessFile raf = null;
		File file = new File(path);
		boolean isWrite = false;
		if (!file.exists()) {
			file.createNewFile();
			isWrite = true;
		}
		raf = new RandomAccessFile(file, "rw");
		if (isWrite)
			raf.writeUTF("0\n");
		return raf;
	}

	/**
	 * �����˺�
	 * 
	 * @return ���ز���acnum
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Integer createJQ() throws FileNotFoundException, IOException {
		String s = "";
		int num = 0;
		while (true) {
			int n = RandomUtil.randomInt(minDigit, maxDigit);
			// System.out.println("λ��:"+n);
			for (int i = 0; i < n; i++)
				s += part[RandomUtil.randomInt(part.length)];
			num = Integer.parseInt(s);
			s = num + "";
			// System.out.println("���ɵĺ���"+s);
			if (s.length() >= minDigit && isAllowed(num) && isAllowReged(num))
				break;
		}
		return num;
	}

	/**
	 * �����Ƿ�Ϊ���εĺ��롣
	 * 
	 * @param num
	 *            ����
	 * @return �����Ƿ�Ϊ���εĺ��롣
	 */
	private boolean isAllowed(Integer num) {
		StringTokenizer tokenizer = new StringTokenizer(notAllowed, ";");
		while (tokenizer.hasMoreTokens()) {
			Integer shieldac = Integer.parseInt(tokenizer.nextToken());
			if (num == shieldac)
				return false;
		}
		return true;
	}

	/**
	 * ��������ע���
	 * 
	 * @param num
	 *            ����
	 * @return ��������ע���
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	private boolean isAllowReged(Integer num) throws FileNotFoundException,
			IOException {
		RandomAccessFile raf = getFile();
		String s;
		while ((s = raf.readLine()) != null) {
			if (s.indexOf(num + "") != -1)
				return false;
		}
		return true;
	}

	/**
	 * ����DAOUser�û�ID
	 * 
	 * @return ����id
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public Integer createID() throws FileNotFoundException, IOException {
		int id = -1;
		RandomAccessFile raf = getFile();
		String s = raf.readLine();

		if (s != null) {
			s = s.trim();
			id = Integer.parseInt(s) + 1;
		}

		raf.close();
		return id;
	}

	/**
	 * �����û���id�ͺ���
	 * 
	 * @param id
	 *            �û�id
	 * @param num
	 *            JQ����
	 * @throws IOException
	 */
	public void saveIDJQ(int id, int num) throws IOException {
		RandomAccessFile raf = getFile();
		raf.writeUTF(id + ":" + num + "\n");
		raf.close();
		raf = getFile();
		raf.seek(0);
		raf.writeUTF(id + "\n");
		raf.close();
	}

}
