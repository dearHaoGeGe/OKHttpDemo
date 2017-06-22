package com.my;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionSupport;

public class UserAction extends ActionSupport {

	private String username;
	private String password;

	public File mPhoto;
	public String mPhotoFileName;

	/**
	 * �ϴ��ļ��ӱ���Ϣ�������
	 * 
	 * @return
	 */
	public String uploadInfo() {
		if (mPhoto == null) {
			System.out.println(mPhotoFileName + "mPhotoΪ�գ�");
		}
		// ������ļ��д���
		File file = new File("D:/temp/", mPhotoFileName);
		try {
			FileUtils.copyFile(mPhoto, file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("username = " + username + "��password = " + password + "  ͼƬ�ϴ���ɣ�");
		
		HttpServletRequest request = ServletActionContext.getRequest();
		System.out.println("sessionId = " + request.getSession().getId());
		return null;
	}

	/**
	 * post����
	 * 
	 * @return
	 */
	public String login() {
		System.out.println(username + "��" + password);
		
		HttpServletRequest request = ServletActionContext.getRequest();
		System.out.println("sessionId = " + request.getSession().getId());
		
		HttpServletResponse response = ServletActionContext.getResponse();
		PrintWriter writer = null;
		try {
			writer = response.getWriter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		writer.write("login success!");
		writer.flush();
		
		return null;
	}

	/**
	 * post������string(json)
	 * 
	 * @return
	 * @throws IOException
	 */
	public String postString() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		ServletInputStream is = request.getInputStream();

		StringBuilder sb = new StringBuilder();
		int len = 0;
		byte[] buf = new byte[1024];

		while ((len = is.read(buf)) != -1) {
			sb.append(new String(buf, 0, len));
		}

		System.out.println(sb.toString());
		
		System.out.println("sessionId = " + request.getSession().getId());
		return null;
	}

	/**
	 * post�ϴ������ļ�
	 * 
	 * @return
	 * @throws IOException
	 */
	public String postFile() throws IOException {
		HttpServletRequest request = ServletActionContext.getRequest();
		ServletInputStream is = request.getInputStream();

		File file = new File("D:/temp/", "pic.jpg");

		FileOutputStream fos = new FileOutputStream(file);

		int len = 0;
		byte[] buf = new byte[1024];

		while ((len = is.read(buf)) != -1) {
			fos.write(buf, 0, len);
		}
		fos.flush();
		fos.close();
		System.out.println("ͼƬ�ϴ���ɣ�");
		System.out.println("sessionId = " + request.getSession().getId());
		return null;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
