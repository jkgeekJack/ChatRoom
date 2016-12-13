package cat.login;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import cat.dao.HibernateDao;
import cat.function.UserBean;
import cat.util.CatUtil;

public class CatResign extends JFrame {

	private JPanel contentPane;
	private JTextField textField;
	private JPasswordField passwordField;
	private JPasswordField passwordField_1;
	private JLabel lblNewLabel;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					// 启动登陆界面
					CatResign frame = new CatResign();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public CatResign() {
		setTitle("注册\n");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(350, 250, 450, 300);
		contentPane = new JPanel();
		contentPane.setBackground(new Color(64,65,76));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		JLabel icon=new JLabel();

		icon.setBounds(120,80,60,100);
		contentPane.add(icon);

		textField = new JTextField();
		textField.setBounds(150, 80, 200,30);
		Font font = new Font("黑体", Font.BOLD, 20);
		MatteBorder border = new MatteBorder(0, 0, 2, 0, new Color(192, 192,
				192));
		textField.setBorder(border);
		textField.setFont(font);
		contentPane.add(textField);
		textField.setToolTipText("请输入账号");
		textField.setColumns(10);
		JLabel jLabel=new JLabel(new ImageIcon("images/user.png"));
		jLabel.setBounds(120,80,30,30);
		contentPane.add(jLabel);

		passwordField = new JPasswordField();
		passwordField.setEchoChar('*');
		passwordField.setFont(font);
		passwordField.setBorder(border);
		passwordField.setToolTipText("请输入密码");
		passwordField.setBounds(150, 110, 200, 30);
		contentPane.add(passwordField);
		JLabel jLabe2=new JLabel(new ImageIcon("images/password.png"));
		jLabe2.setBounds(120,110,30,30);
		contentPane.add(jLabe2);

		passwordField_1 = new JPasswordField();
		passwordField_1.setBounds(150, 140, 200, 30);
		passwordField_1.setFont(font);
		passwordField_1.setBorder(border);
		passwordField_1.setEchoChar('*');
		passwordField_1.setToolTipText("确认密码");
		contentPane.add(passwordField_1);
		JLabel jLabe3=new JLabel(new ImageIcon("images/password.png"));
		jLabe3.setBounds(120,140,30,30);
		contentPane.add(jLabe3);

		//注册按钮
		final JButton btnNewButton_1 = new JButton("注册");
		Font font2 = new Font("华文行楷", Font.BOLD, 20);
		btnNewButton_1.setFont(font2);
		btnNewButton_1.setBorder(new EmptyBorder(0,0,0,0));
		btnNewButton_1.setBounds(120, 200, 230, 30);
		getRootPane().setDefaultButton(btnNewButton_1);
		btnNewButton_1.setBackground(new Color(122,183,67));
		btnNewButton_1.setForeground(Color.white);
		contentPane.add(btnNewButton_1);

		//返回按钮
		final JButton btnNewButton = new JButton("返回登录");
		btnNewButton.setBorder(new EmptyBorder(0,0,0,0));
		btnNewButton.setFont(font2);
		btnNewButton.setBounds(120, 240, 230, 30);
		btnNewButton.setForeground(Color.white);
		btnNewButton.setBackground(new Color(56,58,66));
		contentPane.add(btnNewButton);

		//提示信息
		lblNewLabel = new JLabel();
		lblNewLabel.setBounds(150, 50, 185, 20);
		lblNewLabel.setForeground(Color.WHITE);
		contentPane.add(lblNewLabel);
		
		//返回按钮监听
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				btnNewButton.setEnabled(false);
				//返回登陆界面
				CatLogin frame = new CatLogin();
				frame.setVisible(true);
				setVisible(false);
			}
		});
		
		//注册按钮监听
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String u_name = textField.getText();
				String u_pwd = new String(passwordField.getPassword());
				String u_pwd_ag = new String(passwordField_1.getPassword());
				if (u_name.length() != 0){
					UserBean userBean = (UserBean) HibernateDao.get(UserBean.class, u_name);
					if (userBean==null){
						if (u_pwd.length()!=0&&u_pwd_ag.length()!=0){
							if (u_pwd.equals(u_pwd_ag)) {
								userBean=new UserBean();
								userBean.setPassword(u_pwd);
								userBean.setName(u_name);
								HibernateDao.add(userBean);
								CatLogin frame = new CatLogin();
								frame.setVisible(true);
								setVisible(false);
							}else {
								lblNewLabel.setText("密码不一致！");
							}
						}else {
							lblNewLabel.setText("密码为空！");
						}

					}else {
						lblNewLabel.setText("用户名已存在!");
					}
				}else {
					lblNewLabel.setText("用户名不能为空！");
				}

//				Properties userPro = new Properties();
//				File file = new File("Users.properties");
//				CatUtil.loadPro(userPro, file);
//
//
//				// 判断用户名是否在普通用户中已存在
//				if (u_name.length() != 0) {
//
//					if (userPro.containsKey(u_name)) {
//						lblNewLabel.setText("用户名已存在!");
//					} else {
//						isPassword(userPro, file, u_name, u_pwd, u_pwd_ag);
//					}
//				} else {
//					lblNewLabel.setText("用户名不能为空！");
//				}
			}

//			private void isPassword(Properties userPro,
//					File file, String u_name, String u_pwd, String u_pwd_ag) {
//				if (u_pwd.equals(u_pwd_ag)) {
//					if (u_pwd.length() != 0) {
//						userPro.setProperty(u_name, u_pwd_ag);
//						try {
//							userPro.store(new FileOutputStream(file),
//									"Copyright (c) Boxcode Studio");
//						} catch (FileNotFoundException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						} catch (IOException e1) {
//							// TODO Auto-generated catch block
//							e1.printStackTrace();
//						}
//						btnNewButton_1.setEnabled(false);
//						//返回登陆界面
//						CatLogin frame = new CatLogin();
//						frame.setVisible(true);
//						setVisible(false);
//					} else {
//						lblNewLabel.setText("密码为空！");
//					}
//				} else {
//					lblNewLabel.setText("密码不一致！");
//				}
//			}
		});
	}
}
