package com.example.wonder;

public class Utils {

    /**
     * getDistanceBetweenPoints returns the distance between 2d points p1 and p2
     * @param p1x
     * @param p1y
     * @param p2x
     * @param p2y
     * @return
     */
    public static double getDistanceBetweenPoints(double p1x, double p1y, double p2x, double p2y) {
        return Math.sqrt(
                Math.pow(p2x - p1x, 2) +
                Math.pow(p2y - p1y, 2)
        );
    }
}
