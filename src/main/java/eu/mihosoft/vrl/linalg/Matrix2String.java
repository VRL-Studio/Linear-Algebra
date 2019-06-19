package eu.mihosoft.vrl.linalg;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;

import java.text.NumberFormat;
import java.text.DecimalFormat;

@ComponentInfo(name="Matrix2String", category="Linear Algebra/Output")
public class Matrix2String implements java.io.Serializable {
    private static final long serialVersionUID=1L;

    @OutputInfo(style="editor")
    public String print(@ParamInfo(name="Matrix", options="serialization=false") double[][] matrix) {

        DecimalFormat df = new DecimalFormat("0.0000");
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < matrix.length; i++) {
            sb.append("|    ");
            System.out.print("|    ");
            for (int j = 0; j < matrix[i].length; j++) {
                String entry = df.format(matrix[i][j]).replace(",",".") + "\t";
                sb.append(entry);
                System.out.print(entry);
            }
            System.out.println("|");
            sb.append("|\n");
        }
        return sb.toString();
    }
}