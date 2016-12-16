package lk.ac.mrt.projectx.buildex;

/**
 * Created by krv on 12/5/2016.
 */
public class X86Analysis {
    //canonicalized operations
    public enum Operation {
        op_assign,
        op_add,
        op_sub,
        op_mul,
        op_div,
        op_mod,
        op_lsh,
        op_rsh,
        op_not,
        op_xor,
        op_and,
        op_or,

        /*support operations*/
        op_split_h,
        op_split_l,
        op_concat,
        op_signex,

        /* to cater to different widths */
        op_partial_overlap,
        op_full_overlap,

        /* logical operations */
        op_ge,
        op_gt,
        op_le,
        op_lt,
        op_eq,
        op_neq,

        /*address dependancy*/
        op_indirect,

        /*call*/
        op_call,
        op_unknown
    }
}
