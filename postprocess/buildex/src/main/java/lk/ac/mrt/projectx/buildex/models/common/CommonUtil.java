package lk.ac.mrt.projectx.buildex.models.common;

/**
 * Created by krv on 1/23/17.
 */
public class CommonUtil {


    public static boolean isOverlapped(Long start1, Long end1, Long start2, Long end2) {
        boolean oneInTwo = (start1 >= start2) && (end1 <= end2);
        boolean twoInOne = (start1 <= start2) && (end1 >= end2);

        boolean partialOverlap = ((start1 >= start2) && (start1 <= end2))
                || ((end1 >= start2) && (end1 <= end2));

        return oneInTwo || twoInOne || partialOverlap;
    }

}
