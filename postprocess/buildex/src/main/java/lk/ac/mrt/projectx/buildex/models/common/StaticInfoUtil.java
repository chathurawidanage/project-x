package lk.ac.mrt.projectx.buildex.models.common;

import java.util.List;

/**
 * Created by krv on 2/7/17.
 */
public class StaticInfoUtil {

    public static StaticInfo getStaticInfo(List<StaticInfo> instruction, long programCounter) {
        for (StaticInfo staticInfo :
                instruction) {
            if (staticInfo.getPc() == programCounter)
                return staticInfo;
        }
        return null;
    }

    public static StaticInfo getStaticInfo(List<StaticInfo> instruction, JumpInfo jumpInfo) {
        for (StaticInfo staticInfo :
                instruction) {
            if (staticInfo.getPc() == jumpInfo.jump_pc)
                return staticInfo;
        }
        return null;
    }
}
