package net.elicodes.clearplot.utils;

import net.elicodes.clearplot.Main;

import java.text.DecimalFormat;

public class ProgressUtil {

    public static String getProgressPorcentage(double current, double max) {
        float percentage = ((float)current * (float)100) / (float)max;

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        return df.format(percentage).replace(",", ".") + "%";
    }
}
