package com.girlkun.models.boss.list_boss.doanh_trai;

import com.girlkun.models.boss.Boss;
import com.girlkun.models.boss.BossData;
import com.girlkun.models.map.ItemMap;
import com.girlkun.models.map.doanhtrai.DoanhTrai;
import com.girlkun.models.player.Player;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;


public class BossDoanhTrai extends Boss {

    private DoanhTrai doanhTrai;
    private int xHp;
    private int xDame;

    public BossDoanhTrai(DoanhTrai doanhTrai, int xHp, int xDame, int id, BossData... data) throws Exception {
        super(id, data);
        this.doanhTrai = doanhTrai;
    }
    @Override
    public void reward(Player plKill) {
        if (Util.isTrue(100, 100)) {
            ItemMap it = new ItemMap(this.zone, 19, 1, this.location.x, this.zone.map.yPhysicInTop(this.location.x,
                    this.location.y - 24), plKill.id);
        Service.gI().dropItemMap(this.zone, it);
        }
    }

    @Override
    public void initBase() {
        if (this.doanhTrai.getClan() == null) {
            return;
        }
        BossData data = this.data[this.currentLevel];
        this.name = String.format(data.getName(), Util.nextInt(0, 100));
        this.gender = data.getGender();
        this.nPoint.mpg = 7_5_2002;
        long totalDame = 0;
        long totalHp = 0;
        for (Player pl : this.doanhTrai.getClan().membersInGame) {
            totalDame += pl.nPoint.dame;
            totalHp += pl.nPoint.hpMax;
        }
        this.nPoint.hpg = (int) (totalDame * xHp);
        this.nPoint.dameg = (int) (totalHp / xDame);
        this.nPoint.calPoint();
        this.initSkill();
        this.resetBase();
    }

}

/**
 * Vui lòng không sao chép mã nguồn này dưới mọi hình thức. Hãy tôn trọng tác
 * giả của mã nguồn này. Xin cảm ơn! - Duy Péo Đẹp Zai
 */
