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
public class NapVangService2 {
    
    public static void ChonGiaTien(int chon, Player p) throws Exception{
        switch (chon){
            case 20: {//(20k)
                if (p.getSession().vnd < 20000) {
                    Service.getInstance().sendThongBao(p, "");
                    return;
                }
                if (InventoryServiceNew.gI().getCountEmptyBag(p) == 0) {
                    Service.getInstance().sendThongBao(p, "Hành trang không đủ chỗ trống");
                    return;
                }
                Item thoivang = ItemService.gI().createNewItem((short) 457, 3500);
                if (thoivang != null) {
                    p.getSession().vnd -= 20000;
                    InventoryServiceNew.gI().addItemBag(p, thoivang);
                    InventoryServiceNew.gI().sendItemBags(p);
                    GirlkunDB.executeUpdate("update account set vnd = '" + p.getSession().vnd + "' where id = " + p.getSession().userId);
                    Service.getInstance().sendThongBao(p, "Bạn vừa rút thành công 3500 thỏi vàng");

                }
                break;
            }
            case 50: {
                if (p.getSession().vnd < 50000) {
                    Service.getInstance().sendThongBao(p, "Số tiền tối thiểu: là 50,000vnđ");
                    return;
                }
                if (InventoryServiceNew.gI().getCountEmptyBag(p) == 0) {
                    Service.getInstance().sendThongBao(p, "Hành trang không đủ chỗ trống");
                    return;
                }
                Item thoivang = ItemService.gI().createNewItem((short) 457, 9000);
              if (thoivang != null) {
                    p.getSession().vnd -= 50000;
                    InventoryServiceNew.gI().addItemBag(p, thoivang);
                    InventoryServiceNew.gI().sendItemBags(p);
                    GirlkunDB.executeUpdate("update account set vnd = '" + p.getSession().vnd + "' where id = " + p.getSession().userId);
                    Service.getInstance().sendThongBao(p, "Bạn vừa rút thành công 9000 thỏi vàng");

                }
               break;
            }
            case 100: {
                if (p.getSession().vnd < 100000) {
                    Service.getInstance().sendThongBao(p, "Số tiền tối thiểu: là 100,000vnđ");
                    return;
                }
                if (InventoryServiceNew.gI().getCountEmptyBag(p) == 0) {
                    Service.getInstance().sendThongBao(p, "Hành trang không đủ chỗ trống");
                    return;
                }
                Item thoivang = ItemService.gI().createNewItem((short) 457, 18000);
                if (thoivang != null) {
                    p.getSession().vnd -= 100000;
                    InventoryServiceNew.gI().addItemBag(p, thoivang);
                    InventoryServiceNew.gI().sendItemBags(p);
                    GirlkunDB.executeUpdate("update account set vnd = '" + p.getSession().vnd + "' where id = " + p.getSession().userId);
                    Service.getInstance().sendThongBao(p, "Bạn vừa rút thành công 18000 thỏi vàng");

                }
                 break;
                  }
            case 500: {
                if (p.getSession().vnd < 500000) {
                    Service.getInstance().sendThongBao(p, "Số tiền tối thiểu: là 500,000vnđ");
                    return;
                }
                if (InventoryServiceNew.gI().getCountEmptyBag(p) == 0) {
                    Service.getInstance().sendThongBao(p, "Hành trang không đủ chỗ trống");
                    return;
                }
                Item thoivang = ItemService.gI().createNewItem((short) 457, 100000);
                if (thoivang != null) {
                    p.getSession().vnd -= 500000;
                    InventoryServiceNew.gI().addItemBag(p, thoivang);
                    InventoryServiceNew.gI().sendItemBags(p);
                    GirlkunDB.executeUpdate("update account set vnd = '" + p.getSession().vnd + "' where id = " + p.getSession().userId);
                    Service.getInstance().sendThongBao(p, "Bạn vừa rút thành công 100000 thỏi vàng");
                }
                 break;
            }
        }
    }
}
