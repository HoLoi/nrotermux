/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.girlkun.services;

import com.girlkun.database.GirlkunDB;
import com.girlkun.models.item.Item;
import com.girlkun.models.player.Player;

/**
 *
 * @author Administrator
 */
public class NapVangService {
    
    public static void ChonGiaTien(int chon, Player p) throws Exception{
        switch (chon){
            case 20: {//(20k)
                if (p.getSession().vnd < 20000) {
                    Service.gI().sendThongBao(p, "Số tiền tối thiểu: là 20,000 vnđ");
                    return;
                }
                if (InventoryServiceNew.gI().getCountEmptyBag(p) == 0) {
                    Service.gI().sendThongBao(p, "Hành trang không đủ chỗ trống");
                    return;
                }
                Item thoivang = ItemService.gI().createNewItem((short) 457, 60);
                if (thoivang != null) {
                    p.getSession().vnd -= 20000;
                    InventoryServiceNew.gI().addItemBag(p, thoivang);
                    InventoryServiceNew.gI().sendItemBags(p);
                    GirlkunDB.executeUpdate("update account set vnd = '" + p.getSession().vnd + "' where id = " + p.getSession().userId);
                    Service.gI().sendThongBao(p, "Bạn vừa rút thành công 60 thỏi vàng");

                }
                break;
            }
            case 50: {
                if (p.getSession().vnd < 50000) {
                    Service.gI().sendThongBao(p, "Số tiền tối thiểu: là 50,000vnđ");
                    return;
                }
                if (InventoryServiceNew.gI().getCountEmptyBag(p) == 0) {
                    Service.gI().sendThongBao(p, "Hành trang không đủ chỗ trống");
                    return;
                }
                Item thoivang = ItemService.gI().createNewItem((short) 457, 150);
              if (thoivang != null) {
                    p.getSession().vnd -= 50000;
                    InventoryServiceNew.gI().addItemBag(p, thoivang);
                    InventoryServiceNew.gI().sendItemBags(p);
                    GirlkunDB.executeUpdate("update account set vnd = '" + p.getSession().vnd + "' where id = " + p.getSession().userId);
                    Service.gI().sendThongBao(p, "Bạn vừa rút thành công 150 thỏi vàng");

                }
               break;
            }
            case 100: {
                if (p.getSession().vnd < 100000) {
                    Service.gI().sendThongBao(p, "Số tiền tối thiểu: là 100,000vnđ");
                    return;
                }
                if (InventoryServiceNew.gI().getCountEmptyBag(p) == 0) {
                    Service.gI().sendThongBao(p, "Hành trang không đủ chỗ trống");
                    return;
                }
                Item thoivang = ItemService.gI().createNewItem((short) 457, 300);
                if (thoivang != null) {
                    p.getSession().vnd -= 100000;
                    InventoryServiceNew.gI().addItemBag(p, thoivang);
                    InventoryServiceNew.gI().sendItemBags(p);
                    GirlkunDB.executeUpdate("update account set vnd = '" + p.getSession().vnd + "' where id = " + p.getSession().userId);
                    Service.gI().sendThongBao(p, "Bạn vừa rút thành công 300 thỏi vàng");

                }
                 break;
                  }
            case 500: {
                if (p.getSession().vnd < 500000) {
                    Service.gI().sendThongBao(p, "Số tiền tối thiểu: là 500,000vnđ");
                    return;
                }
                if (InventoryServiceNew.gI().getCountEmptyBag(p) == 0) {
                    Service.gI().sendThongBao(p, "Hành trang không đủ chỗ trống");
                    return;
                }
                Item thoivang = ItemService.gI().createNewItem((short) 457, 1500);
                if (thoivang != null) {
                    p.getSession().vnd -= 500000;
                    InventoryServiceNew.gI().addItemBag(p, thoivang);
                    InventoryServiceNew.gI().sendItemBags(p);
                    GirlkunDB.executeUpdate("update account set vnd = '" + p.getSession().vnd + "' where id = " + p.getSession().userId);
                    Service.gI().sendThongBao(p, "Bạn vừa rút thành công 1500 thỏi vàng");

                }
                 break;
            }
        }
    }
}
