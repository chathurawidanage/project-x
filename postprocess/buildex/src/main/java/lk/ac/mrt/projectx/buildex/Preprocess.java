package lk.ac.mrt.projectx.buildex;

import lk.ac.mrt.projectx.buildex.models.Pair;
import lk.ac.mrt.projectx.buildex.models.common.StaticInfo;
import lk.ac.mrt.projectx.buildex.models.output.Output;

import java.util.List;

/**
 * Created by Lasantha on 02-Feb-17.
 */
public class Preprocess {

    public static void filterDisamVector(List<Pair<Output, StaticInfo>> instrs, List<StaticInfo> staticInfo){

        for (int i = 0; i < staticInfo.size(); i++) {

            Long pc = staticInfo.get(i).getPc();

            boolean found = false;
            for (int j = 0; j < instrs.size(); j++) {
                Output instr = instrs.get(j).first;
                if (instr.getPc() == pc){
                    found = true;
                    break;
                }
            }

            if (!found){
                staticInfo.remove(i);
                i--;
            }
        }

    }

}
