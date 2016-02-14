package com.project.syz.randomidnum.Utiles;

import java.util.Random;

/**
 * Created by syz on 2016/2/1.
 */
public class CalSex {
    
    public static String getSexNumber(String sex){
        Random r = new Random();
        int res = r.nextInt(10);
        if(sex.equals("male")){
            if((res & 1) == 0){
                res = (res + 1) % 10;
            }
        }
        else if(sex.equals("female")){
            if((res & 1) != 0){
                res = (res + 1) % 10;
            }
        }
        return String.valueOf(res);
    }
}

