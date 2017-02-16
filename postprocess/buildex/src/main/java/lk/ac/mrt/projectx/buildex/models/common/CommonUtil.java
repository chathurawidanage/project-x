package lk.ac.mrt.projectx.buildex.models.common;

import lk.ac.mrt.projectx.buildex.models.memoryinfo.MemoryInfo;

import java.util.ArrayList;
import java.util.List;

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

    public static long getExtents(MemoryInfo memoryInfo, long dim, long totalDims) {
        List<MemoryInfo> localMemoryInfos = new ArrayList<>();
        localMemoryInfos.add( memoryInfo );

        MemoryInfo local = memoryInfo;

        while (!local.getMergedMemoryInfos().isEmpty()) {
            localMemoryInfos.add( local.getMergedMemoryInfos().get( 0 ) );
            local = local.getMergedMemoryInfos().get( 0 );
        }

        MemoryInfo wanted = localMemoryInfos.get( (int) (totalDims - dim) );
        if (!wanted.getMergedMemoryInfos().isEmpty()) {
            return wanted.getMergedMemoryInfos().size();
        } else {
            return (wanted.getEnd() - wanted.getStart()) / wanted.getProbStride();
        }

    }

    public static long getStride(MemoryInfo memoryInfo, long dim, long totalDims) {
        List<MemoryInfo> localMemoryInfos = new ArrayList<>();
        localMemoryInfos.add( memoryInfo );

        MemoryInfo local = memoryInfo;

        while (!local.getMergedMemoryInfos().isEmpty()) {
            localMemoryInfos.add( local.getMergedMemoryInfos().get( 0 ) );
            local = local.getMergedMemoryInfos().get( 0 );
        }

        MemoryInfo wanted = localMemoryInfos.get( (int) (totalDims - dim) );
        if (!wanted.getMergedMemoryInfos().isEmpty()) {
            return wanted.getMergedMemoryInfos().get( 1 ).getStart() - wanted.getMergedMemoryInfos().get( 0 ).getStart();
        } else {
            return wanted.getProbStride();
        }

    }

}
