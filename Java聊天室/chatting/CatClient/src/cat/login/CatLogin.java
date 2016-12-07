package cat.login;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Properties;


import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

import cat.client.CatChatroom;
import cat.dao.HibernateDao;
import cat.function.CatBean;
import cat.function.ClientBean;
import cat.function.UserBean;
import cat.util.CatUtil;

public class CatLogin extends JFrame {

    private JPanel contentPane;
    private JTextField textField;
    private JPasswordField passwordField;
    public static HashMap<String, ClientBean> onlines;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // 启动登陆界面
                    CatLogin frame = new CatLogin();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public CatLogin() {
        setTitle("登陆\n");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(350, 250, 450, 300);



        contentPane=new JPanel();
        contentPane.setBackground(new Color(64,65,76));
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);


        ImageIcon icon=new ImageIcon("images/icon.png");
        icon=new ImageIcon(icon.getImage().getScaledInstance(700, 60, Image.SCALE_DEFAULT));
        JLabel label=new JLabel();
        label.setIcon(icon);
        label.setBounds(140, 30,220,40);
        contentPane.add(label);

        textField = new JTextField();
        Font font = new Font("黑体", Font.BOLD, 20);
        textField.setFont(font);
        textField.setForeground(Color.DARK_GRAY);
        textField.setBounds(150, 100, 200,30);
        textField.setToolTipText("请输入账号");
        MatteBorder border = new MatteBorder(0, 0, 2, 0, new Color(192, 192,
                192));
        textField.setBorder(border);
        contentPane.add(textField);
        textField.setColumns(10);
        JLabel jLabel=new JLabel(new ImageIcon("images/user.png"));
        jLabel.setBounds(120,100,30,30);
        contentPane.add(jLabel);

        passwordField = new JPasswordField();
        passwordField.setForeground(Color.DARK_GRAY);
        passwordField.setEchoChar('*');
        passwordField.setFont(font);
        passwordField.setBounds(150, 130, 200, 30);
        passwordField.setToolTipText("请输入密码");
        passwordField.setBorder(new EmptyBorder(0, 0, 0, 0));
        contentPane.add(passwordField);
        JLabel jLabe2=new JLabel(new ImageIcon("images/password.png"));
        jLabe2.setBounds(120,130,30,30);
        contentPane.add(jLabe2);


        final JButton btnNewButton = new JButton("登录");
        Font font2 = new Font("华文行楷", Font.BOLD, 20);
        btnNewButton.setBackground(new Color(122,183,67));
        btnNewButton.setBounds(120, 180, 230, 30);
        btnNewButton.setForeground(Color.white);
        btnNewButton.setFont(font2);
        btnNewButton.setBorder(new EmptyBorder(0,0,0,0));
        getRootPane().setDefaultButton(btnNewButton);
        contentPane.add(btnNewButton);

        final JButton btnNewButton_1 = new JButton("注册");
        btnNewButton_1.setBackground(new Color(56,58,66));
        btnNewButton_1.setBounds(120, 220, 230, 30);
        btnNewButton_1.setForeground(Color.white);
        btnNewButton_1.setFont(font2);
        btnNewButton_1.setBorder(new EmptyBorder(0,0,0,0));
        contentPane.add(btnNewButton_1);

        // 提示信息
        final JLabel lblNewLabel = new JLabel();
        lblNewLabel.setBounds(150, 80, 151, 21);
        lblNewLabel.setForeground(Color.white);
        getContentPane().add(lblNewLabel);

        // 监听登陆按钮
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String u_name = textField.getText();
                UserBean userBean = (UserBean) HibernateDao.get(UserBean.class, u_name);
                if (userBean != null) {
                    if (userBean.getIsOnline()==0){
                        String u_pwd = new String(passwordField.getPassword());
                        if (u_pwd.equals(userBean.getPassword())) {
                            try {
                                Socket client = new Socket("localhost", 8520);

                                btnNewButton.setEnabled(false);
                                CatChatroom frame = new CatChatroom(u_name,
                                        client);
                                frame.setVisible(true);// 显示聊天界面
                                setVisible(false);// 隐藏掉登陆界面

                            } catch (UnknownHostException e1) {
                                // TODO Auto-generated catch block
//								errorTip("The connection with the server is interrupted, please login again");
                                errorTip("连接服务器失败,请检查后重新连接");
                            } catch (IOException e1) {
                                // TODO Auto-generated catch block
//								errorTip("The connection with the server is interrupted, please login again");
                                errorTip("连接服务器失败,请检查后重新连接");
                            }
                        } else {
                            lblNewLabel.setText("您输入的密码有误！");
                            textField.setText("");
                            passwordField.setText("");
                            textField.requestFocus();
                        }
                    }else {
                        lblNewLabel.setText("该账号已经登录！");
                        textField.setText("");
                        passwordField.setText("");
                        textField.requestFocus();
                    }
                } else {
                    lblNewLabel.setText("您输入昵称不存在！");
                    textField.setText("");
                    passwordField.setText("");
                    textField.requestFocus();
                }
//				Properties userPro = new Properties();
//				File file = new File("Users.properties");
//				CatUtil.loadPro(userPro, file);
//				if (file.length() != 0) {
//
//					if (userPro.containsKey(u_name)) {
//						String u_pwd = new String(passwordField.getPassword());
//						if (u_pwd.equals(userPro.getProperty(u_name))) {
//
//							try {
//								Socket client = new Socket("localhost", 8520);
//
//								btnNewButton.setEnabled(false);
//								CatChatroom frame = new CatChatroom(u_name,
//										client);
//								frame.setVisible(true);// 显示聊天界面
//								setVisible(false);// 隐藏掉登陆界面
//
//							} catch (UnknownHostException e1) {
//								// TODO Auto-generated catch block
////								errorTip("The connection with the server is interrupted, please login again");
//								errorTip("连接服务器失败,请检查后重新连接");
//							} catch (IOException e1) {
//								// TODO Auto-generated catch block
////								errorTip("The connection with the server is interrupted, please login again");
//								errorTip("连接服务器失败,请检查后重新连接");
//							}
//
//						} else {
//							lblNewLabel.setText("您输入的密码有误！");
//							textField.setText("");
//							passwordField.setText("");
//							textField.requestFocus();
//						}
//					} else {
//						lblNewLabel.setText("您输入昵称不存在！");
//						textField.setText("");
//						passwordField.setText("");
//						textField.requestFocus();
//					}
//				} else {
//					lblNewLabel.setText("您输入昵称不存在！");
//					textField.setText("");
//					passwordField.setText("");
//					textField.requestFocus();
//				}
            }
        });

        //注册按钮监听
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                btnNewButton_1.setEnabled(false);
                CatResign frame = new CatResign();
                frame.setVisible(true);// 显示注册界面
                setVisible(false);// 隐藏掉登陆界面
            }
        });
    }

    protected void errorTip(String str) {
        // TODO Auto-generated method stub
        JOptionPane.showMessageDialog(contentPane, str, "Error Message",
                JOptionPane.ERROR_MESSAGE);
        textField.setText("");
        passwordField.setText("");
        textField.requestFocus();
    }
}