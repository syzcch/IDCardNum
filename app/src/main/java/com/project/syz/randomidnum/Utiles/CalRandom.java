package com.project.syz.randomidnum.Utiles;

import java.util.Random;

/**
 * Created by syz on 2016/2/1.
 */
public class CalRandom {

    private int num1;
    private int num2;
    private int num3;
    public CalRandom(){
        num1 = 0;
        num2 = 0;
        num3 = 0;
    }
    private int generateInt(){
        Random r = new Random();
        int res = r.nextInt(10);
        return res;
    }

    public String getRandomInfo(){
        num1 = generateInt();
        num2 = generateInt();
        num3 = generateInt();
        return String.valueOf(num1) + String.valueOf(num2) + String.valueOf(num3);
    }
}
