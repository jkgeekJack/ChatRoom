package cat.server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


import cat.dao.HibernateDao;
import cat.function.*;

public class CatServer {
    private static ServerSocket ss;
    public static HashMap<String, ClientBean> onlines;

    static {
        try {
            ss = new ServerSocket(8520);
            onlines = new HashMap<String, ClientBean>();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class CatClientThread extends Thread {
        private Socket client;
        private CatBean bean;
        private ObjectInputStream ois;
        private ObjectOutputStream oos;

        public CatClientThread(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            try {
                // 不停的从客户端接收信息
                while (true) {
                    // 读取从客户端接收到的catbean信息
                    ois = new ObjectInputStream(client.getInputStream());
                    bean = (CatBean) ois.readObject();

                    // 分析catbean中，type是那样一种类型
                    switch (bean.getType()) {
                        // 上下线更新
                        case 0: { // 上线
                            // 记录上线客户的用户名和端口在clientbean中
                            ClientBean cbean = new ClientBean();
                            cbean.setName(bean.getName());
                            cbean.setSocket(client);
                            // 添加在线用户
                            onlines.put(bean.getName(), cbean);
                            // 创建服务器的catbean，并发送给客户端
                            CatBean serverBean = new CatBean();
                            serverBean.setType(0);
                            serverBean.setInfo(bean.getTimer() + "  "
                                    + bean.getName() + "上线了");
                            // 通知所有客户有人上线
                            HashSet<String> set = new HashSet<String>();
                            // 客户昵称
                            set.addAll(onlines.keySet());
                            serverBean.setClients(set);
                            sendAll(serverBean);

                            System.out.println();
                            UserBean userBean= (UserBean) HibernateDao.get(UserBean.class,bean.getName());
                            userBean.setIsOnline(1);
                            HibernateDao.update(userBean);
                            break;
                        }
                        case -1: { // 下线
                            // 创建服务器的catbean，并发送给客户端
                            CatBean serverBean = new CatBean();
                            serverBean.setType(-1);

                            try {
                                oos = new ObjectOutputStream(
                                        client.getOutputStream());
                                oos.writeObject(serverBean);
                                oos.flush();
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                            onlines.remove(bean.getName());

                            // 向剩下的在线用户发送有人离开的通知
                            CatBean serverBean2 = new CatBean();
                            serverBean2.setInfo(bean.getTimer() + "  "
                                    + bean.getName() + " " + " 下线了");
                            serverBean2.setType(0);
                            HashSet<String> set = new HashSet<String>();
                            set.addAll(onlines.keySet());
                            serverBean2.setClients(set);

                            sendAll(serverBean2);
                            UserBean userBean= (UserBean) HibernateDao.get(UserBean.class,bean.getName());
                            userBean.setIsOnline(0);
                            HibernateDao.update(userBean);
                            return;
                        }
                        case 1: { // 聊天

//						 创建服务器的catbean，并发送给客户端
                            CatBean serverBean = new CatBean();

                            serverBean.setType(1);
                            serverBean.setClients(bean.getClients());
                            serverBean.setInfo(bean.getInfo());
                            serverBean.setName(bean.getName());
                            serverBean.setTimer(bean.getTimer());
                            // 向选中的客户发送数据
                            sendMessage(serverBean);
                            //在数据库中保存两条,to一条,from一条
                            ChatHistoryBean chatHistoryBean = new ChatHistoryBean();
                            chatHistoryBean.setMsg_to((String) bean.getClients().toArray()[0]);
                            chatHistoryBean.setMsg_from(bean.getName());
                            chatHistoryBean.setTimer(bean.getTimer());
                            chatHistoryBean.setContent(bean.getInfo());
                            chatHistoryBean.setUser_name(bean.getName());
                            HibernateDao.add(chatHistoryBean);
                            chatHistoryBean.setUser_name((String) bean.getClients().toArray()[0]);
                            HibernateDao.add(chatHistoryBean);
                            break;
                        }
                        case 2: { // 请求接受文件
                            // 创建服务器的catbean，并发送给客户端
                            CatBean serverBean = new CatBean();
                            String info = bean.getTimer() + "  " + bean.getName()
                                    + "向你传送文件,是否需要接受";

                            serverBean.setType(2);
                            serverBean.setClients(bean.getClients()); // 这是发送的目的地
                            serverBean.setFileName(bean.getFileName()); // 文件名称
                            serverBean.setSize(bean.getSize()); // 文件大小
                            serverBean.setInfo(info);
                            serverBean.setName(bean.getName()); // 来源
                            serverBean.setTimer(bean.getTimer());
                            // 向选中的客户发送数据
                            sendMessage(serverBean);

                            break;
                        }
                        case 3: { // 确定接收文件
                            CatBean serverBean = new CatBean();

                            serverBean.setType(3);
                            serverBean.setClients(bean.getClients()); // 文件来源
                            serverBean.setTo(bean.getTo()); // 文件目的地
                            serverBean.setFileName(bean.getFileName()); // 文件名称
                            serverBean.setIp(bean.getIp());
                            serverBean.setPort(bean.getPort());
                            serverBean.setName(bean.getName()); // 接收的客户名称
                            serverBean.setTimer(bean.getTimer());
                            // 通知文件来源的客户，对方确定接收文件
                            sendMessage(serverBean);
                            break;
                        }
                        case 4: {
                            CatBean serverBean = new CatBean();

                            serverBean.setType(4);
                            serverBean.setClients(bean.getClients()); // 文件来源
                            serverBean.setTo(bean.getTo()); // 文件目的地
                            serverBean.setFileName(bean.getFileName());
                            serverBean.setInfo(bean.getInfo());
                            serverBean.setName(bean.getName());// 接收的客户名称
                            serverBean.setTimer(bean.getTimer());
                            sendMessage(serverBean);

                            break;
                        }
                        case 6: { // 请求好友
                            // 创建服务器的catbean，并发送给客户端
                            //先查找是否有此人,有则返回发送成功,没有则返回查无此人,不在线则返回不在线,type为4
//                            CatBean serverBean2 = new CatBean();
//                            String info2 = "发送好友请求成功";
//                            HashSet<String>name=new HashSet<String>();
//                            name.add(bean.getName());
//                            serverBean2.setClients(name); // 这是发送的目的地
//                            serverBean2.setType(4);
//                            sendMessage(serverBean2);

                            CatBean serverBean = new CatBean();
                            serverBean.setType(6);
                            // 创建服务器的catbean，并发送给客户端
                            String info = bean.getName()
                                    + "向你发出好友请求,是否需要接受";
                            serverBean.setClients(bean.getClients()); // 这是发送的目的地
                            serverBean.setInfo(info);
                            serverBean.setName(bean.getName()); // 来源
                            serverBean.setTimer(bean.getTimer());
                            // 向选中的客户发送数据
                            sendMessage(serverBean);

                            break;
                        }
                        case 7: { // 确定成为好友
//                            CatBean serverBean = new CatBean();
//                            serverBean.setType(7);
//                            serverBean.setName(bean);
//                            serverBean.setClients(bean.getClients()); // 文件来源
//                            serverBean.setTo(bean.getTo()); // 文件目的地
                            // 通知文件来源的客户，对方确定接收文件
                            sendMessage(bean);

                            FriendBean friendBean=new FriendBean();
                            friendBean.setUser_name(bean.getName());
                            String friendName= (String) bean.getClients().toArray()[0];
                            friendBean.setFriend_name(friendName);
                            friendBean.setNick_name(friendName);
                            HibernateDao.add(friendBean);

                            FriendBean friendBean2=new FriendBean();
                            String friendName2= (String) bean.getClients().toArray()[0];
                            friendBean2.setUser_name(friendName2);
                            friendBean2.setFriend_name(bean.getName());
                            friendBean2.setNick_name(bean.getName());
                            HibernateDao.add(friendBean2);
                            //在数据库中添加两条好友记录
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
                close();
            }
        }

        // 向选中的用户发送数据
        private void sendMessage(CatBean serverBean) {
            // 首先取得所有的values
            Set<String> cbs = onlines.keySet();
            Iterator<String> it = cbs.iterator();
            // 选中客户
            HashSet<String> clients = serverBean.getClients();
            //是否有发送
            boolean is_send = false;
            while (it.hasNext()) {
                // 在线客户
                String client = it.next();
                // 选中的客户中若是在线的，就发送serverbean
                if (clients.contains(client)) {
                    is_send = true;
                    Socket c = onlines.get(client).getSocket();
                    ObjectOutputStream oos;
                    try {
                        oos = new ObjectOutputStream(c.getOutputStream());
                        oos.writeObject(serverBean);
                        oos.flush();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            }
            if (!is_send) {
                System.out.println(clients + " has offline");
                CatBean catBean = new CatBean();
                HashSet<String> set = new HashSet<String>();
                System.out.println("from :" + serverBean.getName() + " to:" + serverBean.getClients());
                set.add(serverBean.getName());
                catBean.setClients(set);
                catBean.setType(4);
                String name = (String) serverBean.getClients().toArray()[0];
                catBean.setInfo(name + "不在线或不存在");
                sendMessage(catBean);

            }
        }

        // 向所有的用户发送数据
        public void sendAll(CatBean serverBean) {
            Collection<ClientBean> clients = onlines.values();
            Iterator<ClientBean> it = clients.iterator();
            ObjectOutputStream oos;
            while (it.hasNext()) {
                Socket c = it.next().getSocket();
                try {
                    oos = new ObjectOutputStream(c.getOutputStream());
                    oos.writeObject(serverBean);
                    oos.flush();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

        private void close() {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    public void start() {
        try {
            while (true) {
                Socket client = ss.accept();
                new CatClientThread(client).start();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        new CatServer().start();
    }

}
