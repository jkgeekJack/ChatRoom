package cat.client;

import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import cat.dao.HibernateDao;
import cat.function.CatBean;
import cat.function.FriendBean;
import cat.function.UserBean;
import cat.history.ChatHistory;
import cat.util.CatUtil;

class CellRenderer extends JLabel implements ListCellRenderer {
    CellRenderer() {
        setOpaque(true);
    }

    public Component getListCellRendererComponent(JList list, Object value,
                                                  int index, boolean isSelected, boolean cellHasFocus) {

        setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));// 加入宽度为5的空白边框

        if (value != null) {
            setText(value.toString());
            //是否要加入图片
//            setIcon(new ImageIcon("images//1.jpg"));
        }
        if (isSelected) {
            setBackground(new Color(255, 255, 153));// 设置背景色
            setForeground(Color.black);
        } else {
            // 设置选取与取消选取的前景与背景颜色.
            setBackground(Color.white); // 设置背景色
            setForeground(Color.black);
        }
        setEnabled(list.isEnabled());
        setFont(new Font("sdf", Font.ROMAN_BASELINE, 13));
        setOpaque(true);
        return this;
    }
}

class UUListModel extends AbstractListModel {

    private Vector vs;

    public UUListModel(Vector vs) {
        this.vs = vs;
    }

    @Override
    public Object getElementAt(int index) {
        // TODO Auto-generated method stub
        return vs.get(index);
    }

    @Override
    public int getSize() {
        // TODO Auto-generated method stub
        return vs.size();
    }

}

public class CatChatroom extends JFrame {

    private static final long serialVersionUID = 6129126482250125466L;

    private static JPanel contentPane;
    private static Socket clientSocket;
    private static ObjectOutputStream oos;
    private static ObjectInputStream ois;
    private static String name;
    private static JTextArea textArea;
    private static JTextField textField;
    private static AbstractListModel listmodel;
    private static JList list;
    private static String filePath;
    private static JLabel lblNewLabel;
    private static JProgressBar progressBar;
    private static Vector onlines;
    private static Vector friends;
    private static boolean isSendFile = false;
    private static boolean isReceiveFile = false;

    /**
     * Create the frame.
     */

    public CatChatroom(String u_name, Socket client) {
        // 赋值
        name = u_name;
        clientSocket = client;
        onlines = new Vector();

        friends = new Vector();
        SwingUtilities.updateComponentTreeUI(this);


        setTitle(name);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setBounds(200, 100, 688, 550);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        // 聊天信息显示区域
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 10, 410, 375);
        getContentPane().add(scrollPane);

        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);// 激活自动换行功能
        textArea.setWrapStyleWord(true);// 激活断行不断字功能
        textArea.setFont(new Font("sdf", Font.BOLD, 13));
        scrollPane.setViewportView(textArea);

        // 打字区域
        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(10, 390, 411, 97);
        getContentPane().add(scrollPane_1);

        final JTextArea textArea_1 = new JTextArea();
        textArea_1.setLineWrap(true);// 激活自动换行功能
        textArea_1.setWrapStyleWord(true);// 激活断行不断字功能
        scrollPane_1.setViewportView(textArea_1);


        // 关闭按钮
//		final JButton btnNewButton = new JButton("\u5173\u95ED");
//		final JButton btnNewButton = new JButton("关闭");
//		btnNewButton.setBounds(200, 448, 60, 30);
//		getContentPane().add(btnNewButton);

        // 发送按钮
//		JButton btnNewButton_1 = new JButton("\u53D1\u9001");
        JButton btnNewButton_1 = new JButton("发送");
        btnNewButton_1.setBounds(100, 500, 60, 30);
        getRootPane().setDefaultButton(btnNewButton_1);
        getContentPane().add(btnNewButton_1);

        // 发送文件按钮
//		JButton btnNewButton_1 = new JButton("\u53D1\u9001");
        JButton btnFile = new JButton("发送文件");
        btnFile.setBounds(170, 500, 100, 30);
        getContentPane().add(btnFile);

        // 历史消息按钮
//		JButton btnNewButton_1 = new JButton("\u53D1\u9001");
        JButton btnHistory = new JButton("历史消息");
        btnHistory.setBounds(280, 500, 100, 30);
        getContentPane().add(btnHistory);

        // 在线客户列表
//        listmodel = new UUListModel(onlines);
//        list = new JList(listmodel);
        list = new JList();
        list.setCellRenderer(new CellRenderer());
        list.setOpaque(false);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        //从数据库获取好友列表
        updateFirends();

//		Border etch = BorderFactory.createEtchedBorder();
//		list.setBorder(BorderFactory.createTitledBorder(etch, "在线客户:", TitledBorder.LEADING, TitledBorder.TOP,
//				new Font("sdf", Font.BOLD, 18), Color.red));

        JScrollPane scrollPane_2 = new JScrollPane(list);
        scrollPane_2.setBounds(430, 10, 245, 375);
        scrollPane_2.setOpaque(false);
        scrollPane_2.getViewport().setOpaque(false);
        getContentPane().add(scrollPane_2);


        // 文件传输栏
        progressBar = new JProgressBar();
        progressBar.setBounds(430, 390, 245, 15);
        progressBar.setMinimum(1);
        progressBar.setMaximum(100);
        getContentPane().add(progressBar);

        // 文件传输提示
        lblNewLabel = new JLabel("");
        lblNewLabel.setFont(new Font("SimSun", Font.PLAIN, 12));
        lblNewLabel.setBackground(Color.WHITE);
        lblNewLabel.setBounds(430, 410, 245, 15);
        getContentPane().add(lblNewLabel);

        textField = new JTextField();
        textField.setBounds(430, 430, 245, 30);
        getContentPane().add(textField);

        JButton btnAdd = new JButton("添加好友");
        btnAdd.setBounds(430, 460, 100, 30);
        getContentPane().add(btnAdd);

        JButton btnDelete = new JButton("删除好友");
        btnDelete.setBounds(540, 460, 100, 30);
        getContentPane().add(btnDelete);

        JButton btnEdit = new JButton("修改昵称");
        btnEdit.setBounds(430, 500, 100, 30);
        getContentPane().add(btnEdit);

        try {
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            // 记录上线客户的信息在catbean中，并发送给服务器
            CatBean bean = new CatBean();
            bean.setType(0);
            bean.setName(name);
            bean.setTimer(CatUtil.getTimer());
            oos.writeObject(bean);
            oos.flush();

            // 启动客户接收线程
            new ClientInputThread().start();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // 发送按钮
        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String info = textArea_1.getText();
                List to = list.getSelectedValuesList();

                if (to.size() < 1) {
                    JOptionPane.showMessageDialog(getContentPane(), "请选择聊天对象");
                    return;
                }
                if (to.toString().contains(name + "(我)")) {
                    JOptionPane.showMessageDialog(getContentPane(),
                            "不能向自己发送消息");
                    return;
                }
                if (to.toString().contains("离线")) {
                    JOptionPane.showMessageDialog(getContentPane(),
                            "不能向离线用户发送消息");
                    return;
                }
                if (info.equals("")) {
                    JOptionPane.showMessageDialog(getContentPane(), "不能发送空信息");
                    return;
                }

                if (!to.toString().contains(name + "(我)")) {
                    CatBean clientBean = new CatBean();
                    clientBean.setType(1);
                    clientBean.setName(name);
                    String time = CatUtil.getTimer();
                    clientBean.setTimer(time);
                    clientBean.setInfo(info);
                    HashSet set = new HashSet();
                    set.addAll(to);
                    clientBean.setClients(set);

                    // 自己发的内容也要现实在自己的屏幕上面
                    textArea.append(time + " 我对" + to + "说:\r\n" + info
                            + "\r\n");

                    sendMessage(clientBean);
                    textArea_1.setText(null);
                    textArea_1.requestFocus();
                }

            }
        });

        btnFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List to = list.getSelectedValuesList();
                if (to==null||to.size()<1){
                    JOptionPane.showMessageDialog(getContentPane(),
                            "请选择发送文件的对象");
                    return;
                }
                if (to.toString().contains(name + "(我)")) {
                    JOptionPane.showMessageDialog(getContentPane(),
                            "不能向自己发送文件");
                    return;
                }
                if (to.toString().contains("离线")) {
                    JOptionPane.showMessageDialog(getContentPane(),
                            "不能向离线用户发送文件");
                    return;
                }

                JFileChooser chooser = new JFileChooser();
                chooser.setDialogTitle("选择文件框"); // 标题哦...
                chooser.showDialog(getContentPane(), "选择"); // 这是按钮的名字..

                // 判定是否选择了文件
                if (chooser.getSelectedFile() != null) {
                    // 获取路径
                    filePath = chooser.getSelectedFile().getPath();
                    File file = new File(filePath);
                    // 文件为空
                    if (file.length() == 0) {
                        JOptionPane.showMessageDialog(getContentPane(),
                                filePath + "文件为空,不允许发送.");
                        return;
                    }

                    CatBean clientBean = new CatBean();
                    clientBean.setType(2);// 请求发送文件
                    clientBean.setSize(new Long(file.length()).intValue());
                    clientBean.setName(name);
                    clientBean.setTimer(CatUtil.getTimer());
                    clientBean.setFileName(file.getName()); // 记录文件的名称
                    clientBean.setInfo("请求发送文件");

                    // 判断要发送给谁
                    HashSet<String> set = new HashSet<String>();
                    int index=list.getSelectedIndex();
                    System.out.println(name+" send file to "+friends.get(index));
                    FriendBean friendBean = (FriendBean) HibernateDao.queryOne("select f from FriendBean f where f.user_name =? and f.nick_name=?", new String[]{name, (String) friends.get(index)});
                    set.add(friendBean.getFriend_name());
                    clientBean.setClients(set);
                    sendMessage(clientBean);
                }
            }
        });

        btnHistory.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ChatHistory chatHistoryframe=new ChatHistory(name);
                chatHistoryframe.setVisible(true);
            }
        });
        btnAdd.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String friendName = textField.getText();
                if (friendName.equals("")) {
                    JOptionPane.showMessageDialog(getContentPane(), "添加好友名字不能为空");
                    return;
                }
                if (friendName.equals(name)) {
                    JOptionPane.showMessageDialog(getContentPane(), "不能添加自己");
                    return;
                }
                if (friends.contains(friendName)) {
                    JOptionPane.showMessageDialog(getContentPane(), "好友已在列表中");
                    return;
                }

                CatBean catBean = new CatBean();
                catBean.setType(6);
                catBean.setName(name);
                HashSet<String> set = new HashSet<String>();
                set.add(friendName);
                catBean.setClients(set);
                sendMessage(catBean);
                textField.setText("");
            }
        });


        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = list.getSelectedIndex();
                if (index != -1) {
                    int result = JOptionPane.showConfirmDialog(
                            getContentPane(), "您确定要删除好友"+friends.get(index));
                    if (result==0){
                        FriendBean friendBean = (FriendBean) HibernateDao.queryOne("select f from FriendBean f where f.user_name =? and f.nick_name=?", new String[]{name, (String) friends.get(index)});
                        HibernateDao.delete(friendBean);
                        friends.remove(index);
                        onlines.remove(index);
                        listmodel = new UUListModel(onlines);
                        list.setModel(listmodel);
                    }
                } else {
                    JOptionPane.showMessageDialog(getContentPane(), "请选择要删除的好友");
                    return;
                }
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int index = list.getSelectedIndex();
                if (index != -1) {
                    String nickName= (String) JOptionPane.showInputDialog(null,"请输入昵称：\n","修改昵称",JOptionPane.PLAIN_MESSAGE,null,null,friends.get(index));
                    if (nickName.equals("")||nickName==null){
                        JOptionPane.showMessageDialog(getContentPane(), "昵称不能为空");
                        return;
                    }

                    if (friends.contains(nickName)){
                        JOptionPane.showMessageDialog(getContentPane(), "列表中已经有相同的昵称了");
                        return;
                    }

                    System.out.println(nickName);
                    if (!nickName.equals(friends.get(index))){
                        FriendBean friendBean = (FriendBean) HibernateDao.queryOne("select f from FriendBean f where f.user_name =? and f.nick_name=?", new String[]{name, (String) friends.get(index)});
                        System.out.println(friendBean.getUser_name());
                        friendBean.setNick_name(nickName);
                        HibernateDao.update(friendBean);
                        friends.remove(index);
                        friends.add(index,nickName);
                        String onlineName= (String) onlines.get(index);
                        onlines.remove(index);
                        if (onlineName.contains("离线")){
                            onlines.add(index,nickName+"(离线)");
                        }else {
                            onlines.add(index,nickName);
                        }
                        listmodel = new UUListModel(onlines);
                        list.setModel(listmodel);
                    }

                } else {
                    JOptionPane.showMessageDialog(getContentPane(), "请选择要修改昵称的好友");
                    return;
                }

            }
        });

        // 离开
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                // TODO Auto-generated method stub
                if (isSendFile || isReceiveFile) {
                    JOptionPane.showMessageDialog(contentPane,
                            "正在传输文件中，您不能离开...", "Error Message",
                            JOptionPane.ERROR_MESSAGE);
                } else {
                    int result = JOptionPane.showConfirmDialog(
                            getContentPane(), "您确定要离开聊天室");
                    if (result == 0) {
                        CatBean clientBean = new CatBean();
                        clientBean.setType(-1);
                        clientBean.setName(name);
                        clientBean.setTimer(CatUtil.getTimer());
                        sendMessage(clientBean);
                    }
                }
            }
        });

    }

    private void updateFirends() {
        friends.clear();
        List<FriendBean> friendBeanList = HibernateDao.query("select f from FriendBean f where f.user_name=?", new String[]{name});
        if (friendBeanList != null || friendBeanList.size() > 0) {
            for (FriendBean friendBean : friendBeanList) {
                friends.add(friendBean.getNick_name());
            }
        }

    }

    class ClientInputThread extends Thread {

        @Override
        public void run() {
            try {
                // 不停的从服务器接收信息
                while (true) {
                    ois = new ObjectInputStream(clientSocket.getInputStream());
                    final CatBean bean = (CatBean) ois.readObject();
                    switch (bean.getType()) {
                        case 0: {
                            //查询数据库还是说获取
                            // 更新列表
                            onlines.clear();
                            HashSet<String> clients = bean.getClients();
                            System.out.println(clients);
                            for (Object item : friends) {
                                if (clients.contains(item)) {
                                    onlines.add(item);
                                } else {
                                    onlines.add(item + "(离线)");
                                }
                            }
                            listmodel = new UUListModel(onlines);
                            list.setModel(listmodel);

//                            onlines.add(name + "(我)");
//						Iterator<String> it = clients.iterator();
//						while (it.hasNext()) {
//							String ele = it.next();
//							//要增加好友功能,判断是好友才显示
//							if (name.equals(ele)) {
//								onlines.add(ele + "(我)");
//							} else {
//								onlines.add(ele);
//							}
//						}


                            //如果是好友才显示上线信息
                            if (friends.contains(bean.getName())) {
                                textArea.append(bean.getInfo() + "\r\n");
                                textArea.selectAll();
                            }
                            break;
                        }
                        case -1: {

                            return;
                        }
                        case 1: {

                            String info = bean.getTimer() + "  " + bean.getName()
                                    + " 对 " + bean.getClients() + "说:\r\n";
                            if (info.contains(name)) {
                                info = info.replace(name, "我");
                            }
                            textArea.append(info + bean.getInfo() + "\r\n");
                            textArea.selectAll();
                            break;
                        }
//                        case 5: {
//
//                            String info = bean.getTimer() + "  " + bean.getName()
//                                    + " 对 " + bean.getClients() + "说:\r\n";
//                            if (info.contains(name)) {
//                                info = info.replace(name, "我");
//                            }
//                            textArea.append(info + bean.getInfo() + "\r\n");
//                            textArea.selectAll();
//                            break;
//                        }
                        case 2: {
                            // 由于等待目标客户确认是否接收文件是个阻塞状态，所以这里用线程处理
                            new Thread() {
                                public void run() {
                                    // 显示是否接收文件对话框
                                    int result = JOptionPane.showConfirmDialog(
                                            getContentPane(), bean.getInfo());
                                    switch (result) {
                                        case 0: { // 接收文件
                                            JFileChooser chooser = new JFileChooser();
                                            chooser.setDialogTitle("保存文件框"); // 标题哦...
                                            // 默认文件名称还有放在当前目录下
                                            chooser.setSelectedFile(new File(bean
                                                    .getFileName()));
                                            chooser.showDialog(getContentPane(), "保存"); // 这是按钮的名字..
                                            // 保存路径
                                            String saveFilePath = chooser
                                                    .getSelectedFile().toString();

                                            // 创建客户CatBean
                                            CatBean clientBean = new CatBean();
                                            clientBean.setType(3);
                                            clientBean.setName(name); // 接收文件的客户名字
                                            clientBean.setTimer(CatUtil.getTimer());
                                            clientBean.setFileName(saveFilePath);
                                            clientBean.setInfo("确定接收文件");

                                            // 判断要发送给谁
                                            HashSet<String> set = new HashSet<String>();
                                            set.add(bean.getName());
                                            clientBean.setClients(set); // 文件来源
                                            clientBean.setTo(bean.getClients());// 给这些客户发送文件

                                            // 创建新的tcp socket 接收数据, 这是额外增加的功能, 大家请留意...
                                            try {
                                                ServerSocket ss = new ServerSocket(0); // 0可以获取空闲的端口号

                                                clientBean.setIp(clientSocket
                                                        .getInetAddress()
                                                        .getHostAddress());
                                                clientBean.setPort(ss.getLocalPort());
                                                sendMessage(clientBean); // 先通过服务器告诉发送方,
                                                // 你可以直接发送文件到我这里了...

                                                isReceiveFile = true;
                                                // 等待文件来源的客户，输送文件....目标客户从网络上读取文件，并写在本地上
                                                Socket sk = ss.accept();
                                                textArea.append(CatUtil.getTimer()
                                                        + "  " + bean.getFileName()
                                                        + "文件保存中.\r\n");
                                                DataInputStream dis = new DataInputStream( // 从网络上读取文件
                                                        new BufferedInputStream(
                                                                sk.getInputStream()));
                                                DataOutputStream dos = new DataOutputStream( // 写在本地上
                                                        new BufferedOutputStream(
                                                                new FileOutputStream(
                                                                        saveFilePath)));

                                                int count = 0;
                                                int num = bean.getSize() / 100;
                                                int index = 0;
                                                while (count < bean.getSize()) {
                                                    int t = dis.read();
                                                    dos.write(t);
                                                    count++;

                                                    if (num > 0) {
                                                        if (count % num == 0
                                                                && index < 100) {
                                                            progressBar
                                                                    .setValue(++index);
                                                        }
                                                        lblNewLabel.setText("下载进度:"
                                                                + count + "/"
                                                                + bean.getSize()
                                                                + "  整体" + index + "%");
                                                    } else {
                                                        lblNewLabel
                                                                .setText("下载进度:"
                                                                        + count
                                                                        + "/"
                                                                        + bean.getSize()
                                                                        + "  整体:"
                                                                        + new Double(
                                                                        new Double(
                                                                                count)
                                                                                .doubleValue()
                                                                                / new Double(
                                                                                bean.getSize())
                                                                                .doubleValue()
                                                                                * 100)
                                                                        .intValue()
                                                                        + "%");
                                                        if (count == bean.getSize()) {
                                                            progressBar.setValue(100);
                                                        }
                                                    }

                                                }

                                                // 给文件来源客户发条提示，文件保存完毕
                                                PrintWriter out = new PrintWriter(
                                                        sk.getOutputStream(), true);
                                                out.println(CatUtil.getTimer() + " 发送给"
                                                        + name + "的文件["
                                                        + bean.getFileName() + "]"
                                                        + "文件保存完毕.\r\n");
                                                out.flush();
                                                dos.flush();
                                                dos.close();
                                                out.close();
                                                dis.close();
                                                sk.close();
                                                ss.close();
                                                textArea.append(CatUtil.getTimer()
                                                        + "  " + bean.getFileName()
                                                        + "文件保存完毕.存放位置为:"
                                                        + saveFilePath + "\r\n");
                                                isReceiveFile = false;
                                            } catch (Exception e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                            break;
                                        }
                                        default: {
                                            CatBean clientBean = new CatBean();
                                            clientBean.setType(4);
                                            clientBean.setName(name); // 接收文件的客户名字
                                            clientBean.setTimer(CatUtil.getTimer());
                                            clientBean.setFileName(bean.getFileName());
                                            clientBean.setInfo(CatUtil.getTimer()
                                                    + "  " + name + "取消接收文件["
                                                    + bean.getFileName() + "]");

                                            // 判断要发送给谁
                                            HashSet<String> set = new HashSet<String>();
                                            set.add(bean.getName());
                                            clientBean.setClients(set); // 文件来源
                                            clientBean.setTo(bean.getClients());// 给这些客户发送文件

                                            sendMessage(clientBean);

                                            break;

                                        }
                                    }
                                }
                            }.start();
                            break;
                        }
                        case 3: { // 目标客户愿意接收文件，源客户开始读取本地文件并发送到网络上
                            textArea.append(bean.getTimer() + "  " + bean.getName()
                                    + "确定接收文件" + ",文件传送中..\r\n");
                            new Thread() {
                                public void run() {

                                    try {
                                        isSendFile = true;
                                        // 创建要接收文件的客户套接字
                                        Socket s = new Socket(bean.getIp(),
                                                bean.getPort());
                                        DataInputStream dis = new DataInputStream(
                                                new FileInputStream(filePath)); // 本地读取该客户刚才选中的文件
                                        DataOutputStream dos = new DataOutputStream(
                                                new BufferedOutputStream(
                                                        s.getOutputStream())); // 网络写出文件

                                        int size = dis.available();

                                        int count = 0; // 读取次数
                                        int num = size / 100;
                                        int index = 0;
                                        while (count < size) {

                                            int t = dis.read();
                                            dos.write(t);
                                            count++; // 每次只读取一个字节

                                            if (num > 0) {
                                                if (count % num == 0 && index < 100) {
                                                    progressBar.setValue(++index);

                                                }
                                                lblNewLabel.setText("上传进度:" + count
                                                        + "/" + size + "  整体"
                                                        + index + "%");
                                            } else {
                                                lblNewLabel
                                                        .setText("上传进度:"
                                                                + count
                                                                + "/"
                                                                + size
                                                                + "  整体:"
                                                                + new Double(
                                                                new Double(
                                                                        count)
                                                                        .doubleValue()
                                                                        / new Double(
                                                                        size)
                                                                        .doubleValue()
                                                                        * 100)
                                                                .intValue()
                                                                + "%");
                                                if (count == size) {
                                                    progressBar.setValue(100);
                                                }
                                            }
                                        }
                                        dos.flush();
                                        dis.close();
                                        // 读取目标客户的提示保存完毕的信息...
                                        BufferedReader br = new BufferedReader(
                                                new InputStreamReader(
                                                        s.getInputStream()));
                                        textArea.append(br.readLine() + "\r\n");
                                        isSendFile = false;
                                        br.close();
                                        s.close();
                                    } catch (Exception ex) {
                                        ex.printStackTrace();
                                    }

                                }
                            }.start();
                            break;
                        }
                        case 4: {
                            textArea.append(bean.getInfo() + "\r\n");
                            break;
                        }
                        case 6: { // 请求好友
                            // 显示是否成为好友对话框
                            int result = JOptionPane.showConfirmDialog(
                                    getContentPane(), bean.getInfo());
                            switch (result) {
                                case 0: {//接受好友请求
                                    System.out.println("from " + bean.getName() + ",to " + bean.getClients());
                                    // 创建客户CatBean
                                    CatBean clientBean = new CatBean();
                                    clientBean.setType(7);
                                    clientBean.setName(name);
                                    clientBean.setInfo("确定成为好友");
                                    // 判断要发送给谁
                                    HashSet<String> set = new HashSet<String>();
                                    set.add(bean.getName());
                                    clientBean.setClients(set); // 文件来源
                                    clientBean.setTo(bean.getClients());// 给这些客户发送文件
                                    sendMessage(clientBean);
                                    textArea.append(bean.getName()
                                            + "已成为你的好友,开始聊天吧\r\n");
                                    friends.add(bean.getName());
                                    onlines.add(bean.getName());
                                    listmodel = new UUListModel(onlines);
                                    list.setModel(listmodel);

                                    break;
                                }
                                default: {//拒接好友请求
                                    CatBean clientBean = new CatBean();
                                    clientBean.setType(4);
                                    clientBean.setName(name); // 接收文件的客户名字
                                    clientBean.setTimer(CatUtil.getTimer());
                                    clientBean.setInfo(CatUtil.getTimer()
                                            + "  " + name + "拒绝好友请求");
                                    // 判断要发送给谁
                                    HashSet<String> set = new HashSet<String>();
                                    set.add(bean.getName());
                                    clientBean.setClients(set); // 文件来源
                                    clientBean.setTo(bean.getClients());// 给这些客户发送文件
                                    sendMessage(clientBean);
                                }
                            }

                            break;
                        }
                        case 7: { // 确定成为好友
                            textArea.append(bean.getName()
                                    + "已成为你的好友,开始聊天吧\r\n");

                            friends.add(bean.getName());
                            onlines.add(bean.getName());
                            listmodel = new UUListModel(onlines);
                            list.setModel(listmodel);

                            break;
                        }
                        default: {
                            break;
                        }
                    }

                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } finally {
                if (clientSocket != null) {
                    try {
                        clientSocket.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                System.exit(0);
            }
        }
    }

    private void sendMessage(CatBean clientBean) {
        try {
            oos = new ObjectOutputStream(clientSocket.getOutputStream());
            oos.writeObject(clientBean);
            oos.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
