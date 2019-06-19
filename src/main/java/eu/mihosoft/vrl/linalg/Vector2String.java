package eu.mihosoft.vrl.linalg;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;

import java.text.NumberFormat;
import java.text.DecimalFormat;

@ComponentInfo(name="Vector2String", category="Linear Algebra/Output")
public class Vector2String implements java.io.Serializable {
    private static final long serialVersionUID=1L;

    @OutputInfo(style="default")
    public String print(@ParamInfo(name="Vector", options="serialization=false") double[] vector) {

        DecimalFormat df = new DecimalFormat("0.0000");
        StringBuilder sb = new StringBuilder();


        sb.append("|    ");
        System.out.print("|    ");
        for (int i = 0; i < vector.length; i++) {
            String entry = df.format(vector[i]).replace(",",".") + "\t";
            sb.append(entry);
            System.out.print(entry);
        }
        System.out.println("|");
        sb.append("|\n");


        return sb.toString();
    }

}