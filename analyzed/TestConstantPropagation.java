public class TestConstantPropagation {
    
    // ----- test constant -----
    void propagation() {
        int x = 10;
        int y = x;
        int z = y;
        boolean a = true;
        boolean b = a;
        boolean c = b; 
    }

    void intOp() {
        int i10 = 10;
        int x = i10 + 111;
        int y = x * 5;
        int z = y - 12;
    }

    void boolOp() {
        boolean bTrue = true;
        boolean bFalse = false;
        boolean b1 = bTrue || bFalse;
        boolean b2 = bTrue && bFalse;
        int x = 10;
        int y = 20;
        boolean b3 = x > y;
    }

    void branchConstant(boolean b) {
        int x = 2;
        int y = 2;
        int z;
        if (b) {
            z = x + y;
        } else {
            z = x * y;
        }
        int n = z;
    }

    // ----- test NAC -----
    void branchNAC(boolean b) {
        int x;
        if (b) {
            x = 10;
        } else {
            x = 20;
        }
        int y = x;
    }

    void param(int i, boolean b) {
        int x = i;
        int y = i + 10;
        boolean p = b;
        boolean q = b || false;
    }

    void invoke() {
        int x = ten();
        int y = id(10);
    }

    int ten() {
        return 10;
    }

    int id(int x) {
        return x;
    }

    void nonDistributivity(boolean b) {
        int x, y;
        if (b) {
            x = 2;
            y = 3;
        } else {
            x = 3;
            y = 2;
        }
        int z = x + y;
    }

    // ----- test UNDEF -----
    void undefined(boolean b) {
        int undef;
        int x = undef;
        int y;
        if (b) {
            y = 10;
        } else {
            y = undef;
        }
        int z = y;
        x = 20;
        int a = x;
    }
}
