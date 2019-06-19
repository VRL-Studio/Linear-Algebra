package eu.mihosoft.vrl.linalg;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;

@ComponentInfo(name="VectorInput", category="Linear Algebra/Input")
public class VectorInput implements java.io.Serializable {
    private static final long serialVersionUID=1L;

    // add your code here
    @MethodInfo(name="", valueName="v", valueStyle="default", valueOptions="", hide=false)
    public double[] vector(@ParamInfo(name="", style="array", options="") Double[] v) {
        double[] result = new double[v.length];

        for(int i = 0; i < v.length;i++) {
            result[i] = v[i];
        }

        return result;
    }
}
