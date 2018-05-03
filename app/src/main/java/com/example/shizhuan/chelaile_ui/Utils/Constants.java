package com.example.shizhuan.chelaile_ui.Utils;

/**
 * Created by ShiZhuan on 2018/4/25.
 */

public class Constants {
    public static final String BaoAn1_ToWork = "5a49c02f2376c17f01cddda9";
    public static final String BaoAn1_ToHome = "5ae90ba9afdf52662353a6c5";
    public static final String BaoAn2_ToWork = "5ae90b78afdf5266235398d2";
    public static final String BaoAn2_ToHome = "5ae85b7a2376c14eec1c44cd";
    public static final String ShenNan_ToWork = "5adfedb32376c149478fa972";
    public static final String XiLi_ToWork = "5adfedca2376c149478fb0fe";
    public static final String QianHai_ToWork = "5adfeddbafdf5279c0ee6360";
    public static final String HouHai_ToWork = "5adfeddeafdf5279c0ee640d";
    public static final String BinHe_ToWork = "5adfede2afdf5279c0ee64b6";
    public static final String ShenNan_ToHome = "5ae9540e2376c14eec5344fc";
    public static final String XiLi_ToHome = "5ae954102376c14eec5345d1";
    public static final String QianHai_ToHome = "5ae954132376c14eec53463f";
    public static final String HouHai_ToHome = "5ae954152376c14eec5346b8";
    public static final String BinHe_ToHome = "5ae954192376c14eec53474b";
    public static final String BeiHuan_ToWork = "5ae90ba9afdf52662353a6de";
    public static final String BeiHuan_ToHome = "5ae95401afdf52662367aada";

    public static String[] lineKeys = {BaoAn1_ToWork,BaoAn1_ToHome,BaoAn2_ToWork,BaoAn2_ToHome,
            ShenNan_ToWork,ShenNan_ToHome,BeiHuan_ToWork,BeiHuan_ToHome,BinHe_ToWork,BinHe_ToHome,
            XiLi_ToWork,XiLi_ToHome,HouHai_ToWork,HouHai_ToHome,QianHai_ToWork,QianHai_ToHome};

    //上班路线
    public static String[] ToWorkLines = { "宝安1线上班", "宝安2线上班", "深南线上班", "北环线上班",
    "滨河线上班", "西丽线上班", "后海线上班", "前海线上班" };

    //下班路线
    public static String[] ToHomeLines = { "宝安1线下班", "宝安2线下班", "深南线下班", "北环线下班",
            "滨河线下班", "西丽线下班", "后海线下班", "前海线下班" };

    //BC0002-获取位置
    public static String url_getLocation="http://111.230.148.118:8080/BocbusServer/QueryLocation.do?param=";

    //BC0006-获取最近站点
    public static String url_querystation="http://111.230.148.118:8080/BocbusServer/QueryClosestStation.do?param=";
}
