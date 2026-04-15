package com.girlkun.services;

import com.girlkun.jdbc.daos.PlayerDAO;
import com.girlkun.models.player.Player;
import com.girlkun.utils.Logger;
import com.girlkun.utils.Util;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class NapThe {

    private static NapThe I;
    
    public static NapThe gI(){
        if(NapThe.I == null){
            NapThe.I = new NapThe();
        }
        return NapThe.I;
    }
    
    public void napThe(Player pl, String maThe, String seri){
        System.out.println(maThe);
        System.out.println(seri);
    }
    public static final void SendCard(Player p, String loaiThe, String menhGia, String soSeri, String maPin) {
       String partnerId = "72461046463"; //0086879143
            String partnerKey = "16502d49bf5e949c3f27238c2a762115"; //edc3a8086e2db06925438495b0cf88df
           String api = MD5Hash(partnerKey+maPin+soSeri);
            int requestID = Util.nextInt(100000000, 999999999);
               String t = String.valueOf(requestID);
           try{   
OkHttpClient client = new OkHttpClient().newBuilder()
  .build();
MediaType mediaType = MediaType.parse("application/json");
RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
  .addFormDataPart("telco",loaiThe)
  .addFormDataPart("code",maPin)
  .addFormDataPart("serial",soSeri)
  .addFormDataPart("amount",menhGia)
  .addFormDataPart("request_id",t)
  .addFormDataPart("partner_id",partnerId)
  .addFormDataPart("sign",api)
  .addFormDataPart("command","charging")
  .build();

Request request = new Request.Builder()
  .url("https://thesieure.com/chargingws/v2")
  .post(body)
  .addHeader("Content-Type", "application/json")
  .build();
 
       okhttp3.Response response = client.newCall(request).execute();
       
      String jsonString= response.body().string();

        Object obj = JSONValue.parse(jsonString);
       JSONObject jsonObject = (JSONObject) obj;
       long name = (long) jsonObject.get("status");
//        
        if(name==99){
             PlayerDAO.LogNapTIen(p.getSession().uu, menhGia, soSeri, maPin, t);
            Service.gI().sendThongBaoOK(p, "Gửi thẻ thành công \n"
                    + "Seri :"+ soSeri +"\n Mã thẻ :"+maPin +"\n Mệnh giá : "+menhGia+"\n"
                            + "Thời gia : "+java.time.LocalDate.now() +" "+java.time.LocalTime.now()+"\n"
                                    + "Vui lòng thoát game để update lại số tiền");
        }
        if(name==1){
             PlayerDAO.LogNapTIen(p.getSession().uu, menhGia, soSeri, maPin, t);
  Service.gI().sendThongBaoOK(p, "Gửi thẻ thành công \n"
                    + "Seri :"+ soSeri +"\n Mã thẻ :"+maPin +"\n Mệnh giá : "+menhGia+"\n"
                            + "Thời gia : "+java.time.LocalDate.now() +" "+java.time.LocalTime.now()+"\n"
                                    + "Vui lòng thoát game để update lại số tiền");        }
        else if(name==2){
            Service.gI().sendThongBao(p,"nạp thành công nhưng sai mệnh giá.con sẽ ko dc cộng tiền \n lần sau ông khóa mẹ acc con cho chừa nhé");
        }
        else  if(name==3){
            Service.gI().sendThongBao(p,"Bạn đã nhập sai giá trị, hãy nhập đúng nhóe :3");
        }
        else if(name==4){
            Service.gI().sendThongBao(p,"Hệ thống nạp bảo trì rồi con");
        }
       else if(name==100){
            Service.gI().sendThongBao(p,"Sai seri và mã ping ồi con ơi");
           
        }
       
System.out.println(name+"\n"+menhGia+soSeri+"\n"+maPin);

           }catch(Exception e){
               e.printStackTrace();
               //Logger.error("lỗi ở nạp thẻ mất ồi");
           }
    }
   
    public static String MD5Hash(String input){
        try {
        java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
        byte[] array = md.digest(input.getBytes());
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
          sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1,3));
       }
        return sb.toString();
    } catch (Exception e) {
        e.printStackTrace();
    }
    return null;
    }
    
}





















