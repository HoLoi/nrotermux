package com.girlkun.server;

import com.girlkun.database.GirlkunDB;

import java.net.ServerSocket;

import com.girlkun.jdbc.daos.HistoryTransactionDAO;
import com.girlkun.models.boss.BossManager;
import com.girlkun.models.item.Item;
import com.girlkun.models.matches.pvp.DaiHoiVoThuat;
import com.girlkun.models.map.challenge.MartialCongressManager;
import com.girlkun.models.player.Player;
import com.girlkun.network.session.ISession;
import com.girlkun.network.example.MessageSendCollect;
import com.girlkun.network.server.GirlkunServer;
import com.girlkun.network.server.IServerClose;
import com.girlkun.network.server.ISessionAcceptHandler;
import com.girlkun.server.io.MyKeyHandler;
import com.girlkun.server.io.MySession;
import com.girlkun.models.kygui.ShopKyGuiManager;
import com.girlkun.services.ClanService;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.NgocRongNamecService;
import com.girlkun.services.Service;
import com.girlkun.services.func.ChonAiDay;
import com.girlkun.services.func.TaiXiu;
import com.girlkun.services.func.TopService;
import com.girlkun.utils.Logger;
import com.girlkun.utils.TimeUtil;
import com.girlkun.utils.Util;
import java.awt.GraphicsEnvironment;

import java.util.*;
import java.util.logging.Level;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class ServerManager {

    public static String timeStart;

    public static final Map CLIENTS = new HashMap();

    public static String NAME = "Girlkun75";
    public static int PORT = 14445;

    private static ServerManager instance;

    public static ServerSocket listenSocket;
    public static boolean isRunning;
    public static long delaylogin;
    private volatile boolean waitingMenuCommand;

    public void init() {
        Manager.gI();
        try {
            if (Manager.LOCAL) {
                return;
            }
            GirlkunDB.executeUpdate("update account set last_time_login = '2000-01-01', "
                    + "last_time_logout = '2001-01-01'");
        } catch (Exception e) {
        }
        HistoryTransactionDAO.deleteHistory();
    }

    public static ServerManager gI() {
        if (instance == null) {
            instance = new ServerManager();
            instance.init();
        }
        return instance;
    }

    public static void main(String[] args) {
        timeStart = TimeUtil.getTimeNow("dd/MM/yyyy HH:mm:ss");
        ServerManager.gI().run();
    }

    public void run() {
        long delay = 500;
        delaylogin = System.currentTimeMillis();
        isRunning = true;
        initControlPanel();
        activeCommandLine();
        activeGame();
        activeServerSocket();
        Logger.log(Logger.PURPLE_BOLD_BRIGHT,"░░░░░░░░░░░░▄▄\n░░░░░░░░░░░█░░█\n░░░░░░░░░░░█░░█\n░░░░░░░░░░█░░░█\n░░░░░░░░░█░░░░█\n███████▄▄█░░░░░██████▄\n▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n▓▓▓▓▓▓█░░░░░░░░░░░░░░█\n▓▓▓▓▓▓█████░░░░░░░░░█\n██████▀░░░░▀▀██████▀");
//        MaQuaTangManager.gI().init();
        new Thread(DaiHoiVoThuat.gI() , "Thread DHVT").start();
        
//        ChonAiDay.gI().lastTimeEnd = System.currentTimeMillis() + 300000;
//        new Thread(ChonAiDay.gI() , "Thread ChonAiDay").start();
        
        TaiXiu.gI().lastTimeEnd = System.currentTimeMillis() + 50000;
        new Thread(TaiXiu.gI() , "Thread TaiXiu").start();
        
        NgocRongNamecService.gI().initNgocRongNamec((byte)0);
        
        new Thread(NgocRongNamecService.gI() , "Thread NRNM").start();
        
        new Thread(TopService.gI() , "Thread TOP").start();

        new Thread(() -> {
            while (isRunning) {
                try {
                    long start = System.currentTimeMillis();
                    MartialCongressManager.gI().update();
                    ShopKyGuiManager.gI().save();
                    long timeUpdate = System.currentTimeMillis() - start;
                    if (timeUpdate < delay) {
                        Thread.sleep(delay - timeUpdate);
                    }
                } catch (Exception e) {
                    System.out.println("qwert");
                }
            }
        }, "Update dai hoi vo thuat").start();
        try {
            Thread.sleep(1000);
            BossManager.gI().loadBoss();
            Manager.MAPS.forEach(com.girlkun.models.map.Map::initBoss);
        } catch (InterruptedException ex) {
            java.util.logging.Logger.getLogger(BossManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void initControlPanel() {
        try {
            if (GraphicsEnvironment.isHeadless()) {
                Logger.warning("Moi truong headless - bo qua giao dien quan ly server\n");
                Logger.warning("Go 'menu' de mo bang dieu khien terminal\n");
                return;
            }
            JFrame frame = new JFrame("Ngọc rồng Tabi");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ImageIcon icon = new ImageIcon("data/girlkun/icon/icon.png");
            frame.setIconImage(icon.getImage());
            JPanel panel = new panel();
            frame.add(panel);
            frame.pack();
            frame.setVisible(true);
        } catch (Exception e) {
            Logger.warning("Khong the khoi tao giao dien server, tiep tuc che do headless\n");
        }
    }

    private void act() throws Exception {
        GirlkunServer.gI().init().setAcceptHandler(new ISessionAcceptHandler() {
            @Override
            public void sessionInit(ISession is) {
//                antiddos girlkun
                if (!canConnectWithIp(is.getIP())) {
                    is.disconnect();
                    return;
                }

                is = is.setMessageHandler(Controller.getInstance())
                        .setSendCollect(new MessageSendCollect())
                        .setKeyHandler(new MyKeyHandler())
                        .startCollect();
            }

            @Override
            public void sessionDisconnect(ISession session) {
                Client.gI().kickSession((MySession) session);
            }
        }).setTypeSessioClone(MySession.class)
                .setDoSomeThingWhenClose(new IServerClose() {
                    @Override
                    public void serverClose() {
                        System.out.println("server close");
                        System.exit(0);
                    }
                })
                .start(PORT);

    }

    private void activeServerSocket() {
        Logger.log(Logger.PURPLE, "Start server......... Current thread: " + Thread.activeCount() + "\n");
        if (true) {
            try {
                this.act();
            } catch (Exception e) {
            }
            return;
        }
//        try {
//            Logger.log(Logger.PURPLE, "Start server......... Current thread: " + Thread.activeCount() + "\n");
//            listenSocket = new ServerSocket(PORT);
//            while (isRunning) {
//                try {
//                    Socket sc = listenSocket.accept();
//                    String ip = (((InetSocketAddress) sc.getRemoteSocketAddress()).getAddress()).toString().replace("/", "");
//                    if (canConnectWithIp(ip)) {
//                        Session session = new Session(sc, ip);
//                        session.ipAddress = ip;
//                    } else {
//                        sc.close();
//                    }
//                } catch (Exception e) {
////                        Logger.logException(ServerManager.class, e);
//                }
//            }
//            listenSocket.close();
//        } catch (Exception e) {
//            Logger.logException(ServerManager.class, e, "Lỗi mở port");
//            System.exit(0);
//        }
    }

    private boolean canConnectWithIp(String ipAddress) {
        Object o = CLIENTS.get(ipAddress);
        if (o == null) {
            CLIENTS.put(ipAddress, 1);
            return true;
        } else {
            int n = Integer.parseInt(String.valueOf(o));
            if (n < Manager.MAX_PER_IP) {
                n++;
                CLIENTS.put(ipAddress, n);
                return true;
            } else {
                return false;
            }
        }
    }

    public void disconnect(MySession session) {
        Object o = CLIENTS.get(session.getIP());
        if (o != null) {
            int n = Integer.parseInt(String.valueOf(o));
            n--;
            if (n < 0) {
                n = 0;
            }
            CLIENTS.put(session.getIP(), n);
        }
    }

    private void activeCommandLine() {
        new Thread(() -> {
            Scanner sc = new Scanner(System.in);
            if (GraphicsEnvironment.isHeadless()) {
                showControlMenu();
                waitingMenuCommand = true;
            }
            while (true) {
                String line = sc.nextLine();
                if (line == null) {
                    continue;
                }
                line = line.trim();
                if (line.isEmpty()) {
                    continue;
                }
                if (line.equalsIgnoreCase("menu")) {
                    showControlMenu();
                    waitingMenuCommand = true;
                    continue;
                }
                if (waitingMenuCommand) {
                    if (handleMenuSelection(sc, line)) {
                        continue;
                    }
                }
                if (line.equals("baotri")) {
                    Maintenance.gI().start(60 * 2);
                } else if (line.equals("athread")) {
                    ServerNotify.gI().notify("Tabi debug server: " + Thread.activeCount());
                } else if (line.equals("nplayer")) {
                    Logger.error("Player in game: " + Client.gI().getPlayers().size() + "\n");
                } else if (line.equals("admin")) {
                    new Thread(() -> {
                        Client.gI().close();
                    }).start();
                } else if (line.startsWith("bang")) {
                    new Thread(() -> {
                        try {
                            ClanService.gI().close();
                            Logger.error("Save " + Manager.CLANS.size() + " bang");
                        } catch (Exception e) {
                            Logger.error("Lỗi save clan!...................................\n");
                        }
                    }).start();
                } else if (line.startsWith("a")) {
                    String a = line.replace("a ", "");
                    Service.getInstance().sendThongBaoAllPlayer(a);
                } else if (line.startsWith("qua")) {
//                    qua=1-1-1-1=1-1-1-1=
//                     qua=playerId - soluong - itemId - so_saophale = optioneId - param=
                    try {
                        List<Item.ItemOption> ios = new ArrayList<>();
                        String[] pagram1 = line.split("=")[1].split("-");
                        String[] pagram2 = line.split("=")[2].split("-");
                        if (pagram1.length == 4 && pagram2.length % 2 == 0) {
                            Player p = Client.gI().getPlayer(Integer.parseInt(pagram1[0]));
                            if (p != null) {
                                for (int i = 0; i < pagram2.length; i += 2) {
                                    ios.add(new Item.ItemOption(Integer.parseInt(pagram2[i]), Integer.parseInt(pagram2[i + 1])));
                                }
                                Item i = Util.sendDo(Integer.parseInt(pagram1[2]), Integer.parseInt(pagram1[3]), ios);
                                i.quantity = Integer.parseInt(pagram1[1]);
                                InventoryServiceNew.gI().addItemBag(p, i);
                                InventoryServiceNew.gI().sendItemBags(p);
                                Service.getInstance().sendThongBao(p, "Admin trả đồ. anh em thông cảm nhé...");
                            } else {
                                System.out.println("Người chơi không online");
                            }
                        }
                    } catch (Exception e) {
                        System.out.println("Lỗi quà");
                    }
                } else if (line.equals("clean")) {
                    System.gc();
                    System.err.println("Clean.........");
                }
            }
        }, "Active line").start();
    }

    private void showControlMenu() {
        Logger.warning("=========== MENU DIEU KHIEN SERVER ===========\n");
        Logger.warning("1. Bao tri may chu (30 phut)\n");
        Logger.warning("2. Doi EXP server\n");
        Logger.warning("3. Chon su kien server\n");
        Logger.warning("4. Thong bao server\n");
        Logger.warning("5. Da all player\n");
        Logger.warning("6. Khuyen mai nap\n");
        Logger.warning("0. Thoat menu\n");
        Logger.warning("Nhap so de thuc hien: \n");
    }

    private boolean handleMenuSelection(Scanner sc, String line) {
        int option;
        try {
            option = Integer.parseInt(line);
        } catch (NumberFormatException e) {
            waitingMenuCommand = false;
            return false;
        }
        if (option == 0) {
            waitingMenuCommand = false;
            Logger.warning("Da thoat menu dieu khien\n");
            return true;
        }
        executeControlAction(option, sc);
        showControlMenu();
        waitingMenuCommand = true;
        return true;
    }

    private void executeControlAction(int option, Scanner sc) {
        try {
            switch (option) {
                case 1:
                    Maintenance.gI().start(30);
                    Logger.error("Tien hanh bao tri 30 phut\n");
                    break;
                case 2:
                    Logger.warning("Nhap ti le EXP server moi: \n");
                    Manager.RATE_EXP_SERVER = Byte.parseByte(sc.nextLine().trim());
                    Logger.error("EXP server hien tai: " + Manager.RATE_EXP_SERVER + "\n");
                    break;
                case 3:
                    Logger.warning("Nhap su kien (1:TrungThu, 2:He, 3:Tet, 4:Valentine, 5:GioTo, 6:GiangSinh, 7:Halloween): \n");
                    Manager.SUKIEN = Byte.parseByte(sc.nextLine().trim());
                    Logger.error("Su kien hien tai: " + Manager.SUKIEN + "\n");
                    if (Manager.SUKIEN == 1) {
                        Service.getInstance().sendThongBaoAllPlayer("|7|Sự kiện Trung thu đang được diễn ra"
                                + "\n|5|Thông tin chi tiết Sự kiện vui lòng xem tại NPC Trung thu tại Làng Aru");
                    }
                    break;
                case 4:
                    Logger.warning("Nhap noi dung thong bao server: \n");
                    String chat = sc.nextLine();
                    Service.getInstance().sendThongBaoAllPlayer(chat);
                    Logger.error("Thong bao: " + chat + "\n");
                    break;
                case 5:
                    new Thread(() -> Client.gI().close()).start();
                    Logger.error("Dang thuc hien da all player\n");
                    break;
                case 6:
                    Logger.warning("Nhap he so khuyen mai nap moi: \n");
                    Manager.KHUYEN_MAI_NAP = Byte.parseByte(sc.nextLine().trim());
                    Logger.error("Khuyen mai nap hien tai: x" + Manager.KHUYEN_MAI_NAP + "\n");
                    break;
                default:
                    Logger.warning("Lua chon khong hop le\n");
                    break;
            }
        } catch (NumberFormatException e) {
            Logger.warning("Gia tri nhap vao khong hop le\n");
        } catch (Exception e) {
            Logger.warning("Loi thuc thi menu dieu khien\n");
        }
    }

    private void activeGame() {
    }

    public void close(long delay) {
        GirlkunServer.gI().stopConnect();

        isRunning = false;
        try {
            ClanService.gI().close();
        } catch (Exception e) {
            Logger.error("Lỗi save clan!...................................\n");
        }
        ShopKyGuiManager.gI().save();
        Client.gI().close();
        Logger.success("SUCCESSFULLY MAINTENANCE!...................................\n");
        System.exit(0);
    }
}
