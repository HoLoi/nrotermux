package com.girlkun.services.func;

import com.girlkun.database.GirlkunDB;
import com.girlkun.server.Manager;
import com.girlkun.utils.Logger;
import java.sql.Connection;


public class TopService implements Runnable{
    private static TopService i;

    public static TopService gI() {
        if (i == null) {
            i = new TopService();
        }
        return i;
    }
    
    @Override
    public void run() {
        while(true){
            try{
                if (Manager.timeRealTop + (30 * 60 * 1000) < System.currentTimeMillis()) {
                    Manager.timeRealTop = System.currentTimeMillis();
                    try (Connection con = GirlkunDB.getConnection()) {
                        Manager.topNV = Manager.realTop(Manager.queryTopNV, con);
                        Manager.topSM = Manager.realTop(Manager.queryTopSM, con);
                        Manager.topSK = Manager.realTop(Manager.queryTopSK, con);
                        Manager.topPVP = Manager.realTop(Manager.queryTopPVP, con);
                        Manager.topNHS = Manager.realTop(Manager.queryTopNHS, con);
                    } catch (Exception ignored) {
                        Logger.error("Lỗi đọc top");
                    }
                }
                Thread.sleep(1000);
            }catch (Exception ignored) {
            }
        }
    }

}
