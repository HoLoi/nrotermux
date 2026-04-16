package com.girlkun.services.func;

import com.girlkun.database.GirlkunDB;
import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.player.Player;
import com.girlkun.server.Client;
import com.girlkun.server.Manager;
import com.girlkun.network.io.Message;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class TopService implements Runnable {

    private static final String QUERY_TOP_POWER = "select player.id, player.name,"
            + "player.power, player.head, player.gender, player.have_tennis_space_ship,"
            + "player.clan_id_sv" + Manager.SERVER + ", player.data_inventory,"
            + "player.data_location, player.data_point, player.data_magic_tree,"
            + "player.items_body, player.items_bag, player.items_box, player.items_box_lucky_round,"
            + "player.friends, player.enemies, player.data_intrinsic,player.data_item_time, player.data_item_time_sieucap,"
            + "player.data_task, player.data_mabu_egg, player.data_dua, player.data_charm, player.skills,"
            + "player.skills_shortcut, player.pet_info, player.pet_power, player.pet_point,"
            + "player.pet_body, player.pet_skill, player.data_black_ball from player join "
            + "account on player.account_id = account.id where account.is_admin = 0 order by "
            + "player.power desc limit 20";

    private static final int TIME_TARGET_GET_TOP_POWER = 1800000;

    private static TopService i;

    private long lastTimeGetTopPower;
    private List<Player> listTopPower;

    private TopService() {
        this.listTopPower = new ArrayList<>();
    }

    public static TopService gI() {
        if (i == null) {
            i = new TopService();
        }
        return i;
    }

    public static String getTopNap() {
        StringBuffer sb = new StringBuffer("");

        String SELECT_TOP_POWER = "SELECT name, vnd FROM player ORDER BY vnd DESC LIMIT 10;";
        PreparedStatement ps;
        ResultSet rs;
        try {
            Connection conn = GirlkunDB.getConnection();
            ps = conn.prepareStatement(SELECT_TOP_POWER);
            conn.setAutoCommit(false);

            rs = ps.executeQuery();
            byte i = 1;
//            while(rs.next()) {
//                sb.append(i).append(".").append(rs.getString("name")).append(": ").append(rs.getString("vnd")).append("\b");
//                i++;
//            }
            conn.close();
        } catch (Exception e) {
            System.out.println("12345");
        }

        return sb.toString();
    }

    public void showTopPower(Player player) {
        if (Util.canDoWithTime(lastTimeGetTopPower, TIME_TARGET_GET_TOP_POWER)) {
            this.lastTimeGetTopPower = System.currentTimeMillis();
            this.listTopPower.clear();
//            this.listTopPower = PlayerDAO.getPlayers(QUERY_TOP_POWER);
        }
        Message msg;
        try {
            msg = new Message(-96);
            msg.writer().writeByte(0);
            msg.writer().writeUTF("Top Sức Mạnh");
            msg.writer().writeByte(this.listTopPower.size());
            for (int i = 0; i < this.listTopPower.size(); i++) {
                Player pl = this.listTopPower.get(i);
                msg.writer().writeInt(i + 1);
                msg.writer().writeInt((int) pl.id);
                msg.writer().writeShort(pl.getHead());
                msg.writer().writeShort(pl.getBody());
                msg.writer().writeShort(pl.getLeg());
                msg.writer().writeUTF(pl.name);
                msg.writer().writeUTF(Client.gI().getPlayer(pl.id) != null ? "Online" : "");
                msg.writer().writeUTF("Sức mạnh: " + Util.numberToMoney(pl.nPoint.power));
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (Manager.timeRealTop + (30 * 60 * 1000) < System.currentTimeMillis()) {
                    Manager.timeRealTop = System.currentTimeMillis();
                    try (Connection con = GirlkunDB.getConnection()) {
                        Manager.topNV = Manager.realTop(Manager.queryTopNV, con);
                        Manager.topSM = Manager.realTop(Manager.queryTopSM, con);
                        Manager.topSB = Manager.realTop(Manager.querytopSB, con);
                        Manager.topSK = Manager.realTop(Manager.querytopSK, con);
//                Manager.topPVP = Manager.realTop(Manager.queryTopPVP, con);
//                Manager.topNHS = Manager.realTop(Manager.queryTopNHS, con);
                        Manager.topVND = Manager.realTop(Manager.queryTopVND, con);
                        Manager.topHP = Manager.realTop(Manager.queryTopHP, con);
                        Manager.topKI = Manager.realTop(Manager.queryTopKI, con);
                        Manager.topSD = Manager.realTop(Manager.queryTopSD, con);
                    } catch (Exception ignored) {
                        Logger.error("Lỗi đọc top");
                    }
                }
                Thread.sleep(1000);
            } catch (Exception ignored) {
            }
        }
    }

//    public void updateTop() {
//        if (Manager.timeRealTop + (30 * 60 * 1000) < System.currentTimeMillis()) {
//            Manager.timeRealTop = System.currentTimeMillis();
//            try (Connection con = GirlkunDB.getConnection()) {
//               Manager.topNV = Manager.realTop(Manager.queryTopNV, con);
//                Manager.topSM = Manager.realTop(Manager.queryTopSM, con);
//                Manager.topSB = Manager.realTop(Manager.querytopSB, con);
//                Manager.topSK = Manager.realTop(Manager.querytopSK, con);
////                Manager.topPVP = Manager.realTop(Manager.queryTopPVP, con);
////                Manager.topNHS = Manager.realTop(Manager.queryTopNHS, con);
//                Manager.topVND = Manager.realTop(Manager.queryTopVND, con);
//                Manager.topHP = Manager.realTop(Manager.queryTopHP, con);
//                Manager.topKI = Manager.realTop(Manager.queryTopKI, con);
//                Manager.topSD = Manager.realTop(Manager.queryTopSD, con);
//            } catch (Exception ignored) {
//                Logger.error("Lỗi đọc top");
//            }
//        }
//    }
}
