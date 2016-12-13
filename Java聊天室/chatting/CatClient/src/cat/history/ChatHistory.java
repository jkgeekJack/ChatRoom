package cat.history;


import cat.dao.HibernateDao;
import cat.function.ChatHistoryBean;
import cat.function.FriendBean;
import cat.login.CatLogin;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

/**
 * Created by xjk on 16-12-6.
 */
public class ChatHistory extends JFrame {

    private String name;
    private static JPanel contentPane;
    private JList list;
    private static AbstractListModel listmodel;
    List<ChatHistoryBean> chatHistoryList;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    // 启动登陆界面
                    ChatHistory frame = new ChatHistory("jack");
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ChatHistory(String u_name) {
        name = u_name;
        list = new JList();

        updateHistory();

        setTitle("聊天记录");
        setResizable(false);
//        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setBounds(200, 100, 450, 510);
        contentPane = new JPanel();
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JScrollPane scrollPane_2 = new JScrollPane(list);
        scrollPane_2.setBounds(10, 10, 430, 430);
        scrollPane_2.setOpaque(false);
        scrollPane_2.getViewport().setOpaque(false);
        getContentPane().add(scrollPane_2);

        JButton btnDelete = new JButton("删除记录");

        btnDelete.setBounds(180, 448, 100, 30);
        getRootPane().setDefaultButton(btnDelete);
        getContentPane().add(btnDelete);


        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List selected = list.getSelectedValuesList();
                if (selected.size() < 1) {
                    JOptionPane.showMessageDialog(getContentPane(), "请选择要删除的聊天记录对象");
                    return;
                }
                int result = JOptionPane.showConfirmDialog(
                        getContentPane(), "您确定要删除所选"+selected.size()+"条聊天记录");
                if (result==0){
                    delete(list.getSelectedIndices());
                    updateHistory();
                }

            }
        });
    }

    private void delete(int[] selectedIndices) {
        for (int i : selectedIndices) {
             HibernateDao.delete(chatHistoryList.get(i));
        }
        updateHistory();
    }

    private void updateHistory() {
        chatHistoryList = HibernateDao.query("select c from ChatHistoryBean c where c.user_name=?", new String[]{name});
        System.out.println(chatHistoryList.size());
        listmodel = new HistoryListModel(chatHistoryList);
        list.setModel(listmodel);
    }

    class HistoryListModel extends AbstractListModel {

        private List<ChatHistoryBean> vs;

        public HistoryListModel(List vs) {
            this.vs = vs;
        }

        @Override
        public Object getElementAt(int index) {
            // TODO Auto-generated method stub
            String info = vs.get(index).getTimer() + "  " + vs.get(index).getMsg_from()
                    + " 对 " + vs.get(index).getMsg_to() + "说:\r\n";
            if (info.contains(name)) {
                info = info.replace(name, "我");
            }
            info = info + vs.get(index).getContent();
            return info;
        }

        @Override
        public int getSize() {
            // TODO Auto-generated method stub
            return vs.size();
        }

    }


}
