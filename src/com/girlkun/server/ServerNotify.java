package com.girlkun.server;

import com.girlkun.models.player.Player;
import com.girlkun.models.player.GiftcodeViet;
import com.girlkun.network.io.Message;
import com.girlkun.services.Service;
import com.girlkun.utils.Util;
import java.util.ArrayList;
import java.util.List;


public class ServerNotify extends Thread {

    private byte[] gk = new byte[]{67, 104, -61, -96, 111, 32, 109, -31, -69, -85,
        110, 103, 32, 98, -31, -70, -95, 110, 32, -60, -111, -61, -93, 32, 116, -31,
        -69, -101, 105, 32, 118, -31, -69, -101, 105, 32, 109, -61, -95, 121, 32,
        99, 104, -31, -69, -89, 32, 71, 105, 114, 108, 107, 117, 110, 55, 53, 46,
        32, 67, 104, -61, -70, 99, 32, 99, -61, -95, 99, 32, 98, -31, -70, -95,
        110, 32, 99, 104, -58, -95, 105, 32, 103, 97, 109, 101, 32, 118, 117,
        105, 32, 118, -31, -70, -69, 46, 46};
    private long lastTimeGK;

    private final List<String> notifies;

    private static ServerNotify i;

    private ServerNotify() {
        this.notifies = new ArrayList<>();
        this.start();
    }

    public static ServerNotify gI() {
        if (i == null) {
            i = new ServerNotify();
        }
        return i;
    }

    @Override
    public void run() {
        while (!Maintenance.isRuning) {
            try {
                while (!notifies.isEmpty()) {
                    sendThongBaoBenDuoi(notifies.remove(0));
                }
                if (Util.canDoWithTime(this.lastTimeGK, 12000)) {
                    sendThongBaoBenDuoi("Chào mừng đến với Ngọc rồng Tabi!");
                    this.lastTimeGK = System.currentTimeMillis();
                }
            } catch (Exception ignored) {

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
    }
    
    public void run1() {
        while (!Maintenance.isRuning) {
            try {
                while (!notifies.isEmpty()) {
                    sendThongBaoBenDuoi(notifies.remove(0));
                }
                if (Util.canDoWithTime(this.lastTimeGK, 15000)) {
                    sendThongBaoBenDuoi("Mọi Thông Tin Về Game Liên Hệ Zalo Or Fanpage!!!");
                    this.lastTimeGK = System.currentTimeMillis();
                }
            } catch (Exception ignored) {

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
    }

    private void sendThongBaoBenDuoi(String text) {
        Message msg;
        try {
            msg = new Message(93);
            msg.writer().writeUTF(text);
            Service.getInstance().sendMessAllPlayer(msg);
            msg.cleanup();
        } catch (Exception e) {
        }
    }

    public void notify(String text) {
        this.notifies.add(text);
    }

    public void sendNotifyTab(Player player) {
        Message msg;
        try {
            msg = new Message(50);
            msg.writer().writeByte(10);
            
            msg.writer().writeShort(0);
            msg.writer().writeUTF("Thông tin Nro Tabi");
            msg.writer().writeUTF("  Hồng ngọc săn Boss rơi đá đổi capsule hồng"
                    + "\n  Vàng làm nhiệm vụ hằng ngày"
                    + "\n  Set kích hoạt up map sau làng"
                    + "\n  Nạp thẻ, đăng ký vui lòng lên Web");
            
            msg.writer().writeShort(1);
            msg.writer().writeUTF("Lệnh hổ trợ người chơi");
            msg.writer().writeUTF("\tCÁC LỆNH CHO MEMBER"
                    + "\n- muanhieu : Bật/Tắt Mua nhiều"
                    + "\n- adau : Bật/Hủy Auto buff đậu khi HP,KI đệ dưới 30%"
                    + "\n- chiso : Bật/Hủy cộng chỉ số nhanh"
                    + "\n- hp : Xem HP Boss và Dame thực lên Boss"
                    + "\n- tt : Hiện thông tin cơ bản khi quá chỉ số hiển thị"
                    + "\n- th : Lệnh triệu hồi khi có Chiến Thần"
                    + "\n- quai : Xem HP Quái đang đánh"
                    + "\n- stop : Dừng Tất cả lệnh Auto");
            
            msg.writer().writeShort(2);
            msg.writer().writeUTF("GIFTCODE");
            msg.writer().writeUTF(GiftcodeViet.gI().checkInfomationGiftCode());
            
            if (player.TrieuHoiCapBac != -1) {
                String ttpet = "Name: " + player.TenThuTrieuHoi;
                ttpet += "\nLevel: " + player.TrieuHoiLevel + " (" + (player.TrieuHoiExpThanThu * 100 / (3000000L + player.TrieuHoiLevel * 1500000L)) + "%)";
                ttpet += "\nKinh nghiệm: " + Util.format(player.TrieuHoiExpThanThu);
                ttpet += "\nCấp bậc: " + player.NameThanthu(player.TrieuHoiCapBac);
                ttpet += "\nThức ăn: " + player.TrieuHoiThucAn + "%";
                ttpet += "\nSức Đánh: " + Util.getFormatNumber(player.TrieuHoiDame);
                ttpet += "\nMáu: " + Util.getFormatNumber(player.TrieuHoiDame);
                ttpet += "\nKĩ năng: " + player.TrieuHoiKiNang(player.TrieuHoiCapBac);
                msg.writer().writeShort(3);
                msg.writer().writeUTF("Thông tin CHIẾN THẦN");
                msg.writer().writeUTF(ttpet);
            }
            player.sendMessage(msg);
            msg.cleanup();
        } catch (Exception ignored) {
        }
    }
}
