package lk.ac.mrt.projectx.buildex;

import org.junit.Test;

import java.io.Serializable;

import static org.junit.Assert.assertEquals;

/**
 * @author Chathura Widanage
 */
public class GeneralUtilsTest implements Serializable {
    @Test
    public void testDeepCopy() throws Exception {

        class Test2 implements Serializable{
            int yh=5;

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Test2)) return false;

                Test2 test2 = (Test2) o;

                return yh == test2.yh;
            }

            @Override
            public int hashCode() {
                return yh;
            }
        }
        class Test1 implements Serializable{
            public Integer b=0;
            public Double y=5.0;
            public long xx=5;
            public Test2 test2=new Test2();

            @Override
            public boolean equals(Object o) {
                if (this == o) return true;
                if (!(o instanceof Test1)) return false;

                Test1 test1 = (Test1) o;

                if (xx != test1.xx) return false;
                if (b != null ? !b.equals(test1.b) : test1.b != null) return false;
                if (y != null ? !y.equals(test1.y) : test1.y != null) return false;
                return test2 != null ? test2.equals(test1.test2) : test1.test2 == null;
            }

            @Override
            public int hashCode() {
                int result = b != null ? b.hashCode() : 0;
                result = 31 * result + (y != null ? y.hashCode() : 0);
                result = 31 * result + (int) (xx ^ (xx >>> 32));
                result = 31 * result + (test2 != null ? test2.hashCode() : 0);
                return result;
            }
        }

        Test1 instance=new Test1();

        Test1 instance2= (Test1) GeneralUtils.deepCopy(instance);

        assertEquals(instance==instance2,false);
        assertEquals(instance.equals(instance2),true);
    }

}