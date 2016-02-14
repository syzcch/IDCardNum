package com.project.syz.randomidnum;

import com.project.syz.randomidnum.Utiles.CalId18;
import com.project.syz.randomidnum.Utiles.CalRandom;
import com.project.syz.randomidnum.Utiles.CalSex;

/**
 * Created by syz on 2016/2/2.
 */
public class CalIdInfo {
    private String birthday = "";
    private char id18;
    private String locInfo = "";
    private String random = "";
    private String sex = "";

    private CalIdInfo() {}
    private static CalIdInfo singleCalInfo = null;

    private CalRandom calRandom;

    //静态工厂方法
    public synchronized  static CalIdInfo getInstance() {
        if (singleCalInfo == null) {
            singleCalInfo = new CalIdInfo();
        }
        return singleCalInfo;
    }

    private void setId18(String id17){
        id18 = CalId18.getValidateCode(id17);
    }

    private char getId18(){
        return id18;
    }

    private void setBirthday(String year,String month, String day){
        birthday = year + month + day;
    }

    private String getBirthday(){
        return birthday;
    }

    private void setSex(String sex){
        sex = CalSex.getSexNumber(sex);
    }

    private String getSex(){
        return sex;
    }

    private void setRandom(){
        calRandom = new CalRandom();
        random = calRandom.getRandomInfo();
    }

    private String getRandom(){
        return random;
    }

    private String getLocInfo(){
        return locInfo;
    }

    private void setLocInfo(int numLoc){
        locInfo = String.valueOf(numLoc);
    }

    public String calIdInfoAll(int locNum, String year, String month, String day, String sex){
        String locRes = "";
        setBirthday(year, month, day);
        setLocInfo(locNum);
        setSex(sex);
        setRandom();
        locRes = getLocInfo() + getBirthday() + getRandom() + getSex();
        setId18(locRes);
        locRes += getId18();
        return locRes;
    }

}
