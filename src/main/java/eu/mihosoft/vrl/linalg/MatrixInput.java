package eu.mihosoft.vrl.linalg;


import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.MethodInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;
import groovy.lang.GroovyShell;
import groovy.lang.Script;

import java.io.Serializable;
import java.util.List;

@ComponentInfo(name="MatrixInput", category="Linear Algebra/Input")
public class MatrixInput implements Serializable {

    private static final long serialVersionUID = 1L;
    private transient Script script;

    @MethodInfo(name="", valueName="Matrix", valueStyle="default", valueOptions="serialization=false", hide=false)
    public double[][] matrix(
            @ParamInfo(name="<html><b>Matrix m</b></hmtl>", style="code", options="") String expression) {
        GroovyShell shell = new GroovyShell();
        String scriptText = "import static java.lang.Math.*;"
                + "import eu.mihosoft.vrl.types.*;"
                + "def m = [];"
                + expression+";\n _m_cast = m as double[][];";
        script = shell.parse(scriptText);
        script.run();

        System.out.println("scriptText");

        return (double[][]) script.getProperty("_m_cast");
    }
}