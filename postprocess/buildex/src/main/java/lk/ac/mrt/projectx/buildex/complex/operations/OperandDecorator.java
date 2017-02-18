package lk.ac.mrt.projectx.buildex.complex.operations;

/**
 * @author Chathura Widanage
 */
public enum OperandDecorator {//should do the inverse of each operator
    NONE("%s"), TAN("atan(%s)"), //ATAN("Math.tan(%s)"),
    SQUARE("sqrt(%s)"),
    SQRT("pow(%s,2)");/*
     SIN("Math.asin(%s)"),
     COS("Math.acos(%s)");*/
    String operation;

    OperandDecorator(String operation) {
        this.operation = operation;
    }

    public double operate(double val) {
        if (this.equals(SQUARE)) {
            return Math.pow(val, 2);
        } else if (this.equals(TAN)) {
            return Math.tan(val);
        }/* else if (this.equals(ATAN)) {
            return Math.atan(val);
        }*/ else if (this.equals(SQRT)) {
            return Math.sqrt(val);
        } /*else if (this.equals(SIN)) {
            return Math.sin(val);
        } else if (this.equals(COS)) {
            return Math.cos(val);
        }*/ else {
            return val;
        }
    }

    public double operateInv(double val) {
        if (this.equals(SQUARE)) {
            return Math.sqrt(val);
        } else if (this.equals(TAN)) {
            return Math.atan(val);
        }/*else if (this.equals(ATAN)) {
            return Math.tan(val);
        }*/ else if (this.equals(SQRT)) {
            return Math.pow(val, 2);
        } /*else if (this.equals(SIN)) {
            return Math.asin(val);
        } else if (this.equals(COS)) {
            return Math.acos(val);
        }*/ else {
            return val;
        }
    }

    @Override
    public String toString() {
        return this.operation;
    }
}
