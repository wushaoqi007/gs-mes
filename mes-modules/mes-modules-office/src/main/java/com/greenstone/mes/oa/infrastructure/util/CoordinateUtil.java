package com.greenstone.mes.oa.infrastructure.util;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

public class CoordinateUtil {

    /**
     * 计算两个经纬度之间的距离
     *
     * @param gpsFrom   第一个经纬度
     * @param gpsTo     第二个经纬度
     * @param ellipsoid 计算方式
     * @return 返回的距离，单位m
     */
    public static double getDistanceMeter(GlobalCoordinates gpsFrom, GlobalCoordinates gpsTo, Ellipsoid ellipsoid) {
        //创建GeodeticCalculator，调用计算方法，传入坐标系、经纬度用于计算距离
        GeodeticCurve geoCurve = new GeodeticCalculator().calculateGeodeticCurve(ellipsoid, gpsFrom, gpsTo);

        return geoCurve.getEllipsoidalDistance();
    }

    public static void main(String[] args) {
        double lon1 = 31.517885;
        double lat1 = 120.426505;
        double lon2 = 31.518498;
        double lat2 = 120.427954;

        GlobalCoordinates source = new GlobalCoordinates(lon1, lat1);
        GlobalCoordinates target = new GlobalCoordinates(lon2, lat2);

        double meter1 = getDistanceMeter(source, target, Ellipsoid.Sphere);
        double meter2 = getDistanceMeter(source, target, Ellipsoid.WGS84);

        System.out.println("Sphere坐标系计算结果："+meter1 + "米");
        System.out.println("WGS84坐标系计算结果："+meter2 + "米");
    }

}
