package lk.ac.mrt.projectx.buildex.models.common;

import lk.ac.mrt.projectx.buildex.DefinesDotH;
import lk.ac.mrt.projectx.buildex.models.output.MemoryType;
import lk.ac.mrt.projectx.buildex.models.output.Operand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by krv on 1/23/17.
 */
public class FuncInfo {

    private String funcName;
    private String moduleName;
    private List<Operand> parameters = new ArrayList<>();
    private Operand ret;
    private Long start;
    private Long end;

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public List<Operand> getParameters() {
        return parameters;
    }

    public void setParameters(List<Operand> parameters) {
        this.parameters = parameters;
    }

    public Operand getRet() {
        return ret;
    }

    public void setRet(Operand ret) {
        this.ret = ret;
    }

    public Long getStart() {
        return start;
    }

    public void setStart(Long start) {
        this.start = start;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public static void populateStandardFuncs(List<FuncInfo> funcInfoss) {
        FuncInfo func = new FuncInfo();
        func.setModuleName( "" );
        func.setStart( 15792L );
        func.setEnd( 15938L );

        // float passes in the floating point stack
        Operand para = new Operand();
        para.setType( MemoryType.REG_TYPE );
        para.setValue( DefinesDotH.DR_REG.DR_REG_ST9.ordinal() );
        para.setWidth( 10 );
        para.regToMemRange();
        func.getParameters().add( para );

        // flaot returned at the top of the stack
        Operand ret = new Operand();
        ret.setType( MemoryType.REG_TYPE );
        ret.setValue( DefinesDotH.DR_REG.DR_REG_ST9.ordinal() );
        ret.setWidth( 10 );
        ret.regToMemRange();

        func.setRet( ret );

        func.setFuncName( "Halide::floor" );

        funcInfoss.add( func );
    }
}
