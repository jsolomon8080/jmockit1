/*
 * Copyright (c) 2006 JMockit developers
 * This file is subject to the terms of the MIT license (see LICENSE.txt).
 */
package mockit.coverage;

import javax.annotation.Nonnegative;
import javax.annotation.Nonnull;

public final class CoveragePercentage {
    private CoveragePercentage() {
    }

    public static int calculate(@Nonnegative int coveredCount, @Nonnegative int totalCount) {
        if (totalCount == 0) {
            return -1;
        }

        // noinspection NumericCastThatLosesPrecision
        return (int) (100.0 * coveredCount / totalCount + 0.5);
    }

    @Nonnull
    public static String percentageColor(@Nonnegative int coveredCount, @Nonnegative int totalCount) {
        if (coveredCount == 0) {
            return "ff0000";
        }
        if (coveredCount == totalCount) {
            return "00ff00";
        }

        double percentage = 100.0 * coveredCount / totalCount;
        // noinspection NumericCastThatLosesPrecision
        int green = (int) (0xFF * percentage / 100.0 + 0.5);
        int red = 0xFF - green;

        StringBuilder color = new StringBuilder(6);
        appendColorInHexadecimal(color, red);
        appendColorInHexadecimal(color, green);
        color.append("00");

        return color.toString();
    }

    private static void appendColorInHexadecimal(@Nonnull StringBuilder colorInHexa, @Nonnegative int rgb) {
        String hex = Integer.toHexString(rgb);

        if (hex.length() == 1) {
            colorInHexa.append('0');
        }

        colorInHexa.append(hex);
    }
}
