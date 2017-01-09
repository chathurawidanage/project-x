package lk.ac.mrt.projectx.buildex;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wik2kassa on 12/3/2016.
 */
public class InstructionTraceUnit {
    public static final long REG_TYPE = 0;
    public static final long MEM_STACK_TYPE = 1;
    public static final long MEM_HEAP_TYPE = 2;
    public static final long IMM_FLOAT_TYPE = 3;
    public static final long IMM_INT_TYPE = 4;
    public static final long DEFAULT_TYPE = 5;
    private static final int MAX_SRCS = 4;
    private static final int MAX_DSTS = 4;
    public List<Operand> srcs;
    public List<Operand> dsts;
    private long opcode;
    private long num_srcs;
    private long num_dsts;
    private long eflags;
    private long pc;

    public InstructionTraceUnit() {
        srcs = new ArrayList<>();
        dsts = new ArrayList<>();

    }

    public long getOpcode() {
        return opcode;
    }

    public void setOpcode(long opcode) {
        this.opcode = opcode;
    }

    public long getNum_srcs() {
        return num_srcs;
    }

    public void setNum_srcs(long num_srcs) {
        this.num_srcs = num_srcs;
    }

    public long getNum_dsts() {
        return num_dsts;
    }

    public void setNum_dsts(long num_dsts) {
        this.num_dsts = num_dsts;
    }


    public long getEflags() {
        return eflags;
    }

    public void setEflags(long eflags) {
        this.eflags = eflags;
    }

    public long getPc() {
        return pc;
    }

    public void setPc(long pc) {
        this.pc = pc;
    }

    public InstructionTraceUnit clone() {
        InstructionTraceUnit inst = new InstructionTraceUnit();
        inst.pc = pc;
        inst.eflags = eflags;
        inst.num_dsts = num_dsts;
        inst.num_srcs = num_srcs;
        inst.num_dsts = num_dsts;
        inst.opcode = opcode;
        for (Operand op : srcs)
            inst.srcs.add(op.clone());

        for (Operand op : dsts)
            inst.dsts.add(op.clone());
        return inst;
    }

    public static class Operand {

        public Operand[] addr;
        private long type;
        private long width;
        private Long lvalue;
        private Float fvalue;

        public void setValue(long l) {
            lvalue = l;
        }

        public Object getValue() {
            if (lvalue == null)
                return fvalue;
            else
                return lvalue;
        }

        public void setValue(float f) {
            fvalue = f;
        }

        public long getType() {
            return type;
        }

        public void setType(long type) {
            this.type = type;
        }

        public long getWidth() {
            return width;
        }

        public void setWidth(long width) {
            this.width = width;
        }

        public Operand clone() {
            Operand inst = new Operand();
            inst.type = type;
            inst.width = width;
            inst.lvalue = lvalue;
            inst.fvalue = fvalue;
            if(addr != null) {
                inst.addr = new Operand[addr.length];
                for (int i = 0; i < addr.length; i++) {
                    inst.addr[i] = new InstructionTraceUnit.Operand();
                    inst.addr[i].type = addr[i].type;
                    inst.addr[i].width = addr[i].width;
                    inst.addr[i].lvalue = addr[i].lvalue;
                    inst.addr[i].fvalue = addr[i].fvalue;
                }
            }
            return inst;
        }
    }
}
