package eu.mihosoft.vrl.linalg;

import eu.mihosoft.vrl.annotation.ComponentInfo;
import eu.mihosoft.vrl.annotation.OutputInfo;
import eu.mihosoft.vrl.annotation.ParamInfo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A simple 2d file reader for VRL-Studio.
 * 
 * # File format:
 * # - lines starting with '#' are ignored (just like this comment)
 * # - first line contains two entries:
 * #     the number of vertices and the number of triangles
 * # - then follow the vertices and the triangles
 * #
 * # Now follows the actual content with three vertices and one triangle:
 * 3 1
 * 0.0 0.0
 * 5.0 0.0
 * 0.0 5.0
 * 0 1 2
 * 
 * @author Michael Hoffer <info@michaelhoffer.de>
 */
@ComponentInfo(name="File2DReader", category="Linear Algebra/IO/", description = "reads *.2df files")
public class File2DReader implements java.io.Serializable {
  private static final long serialVersionUID=1L;
  
  // ---- add your code here -----
  
  @OutputInfo(name="2df File Content", style="multi-out", options="", 
    elemTypes={double[][].class, int[][].class},
    elemNames={"Vertices", "Triangles"},
    elemOptions={"serialization=false","serialization=false"})
  public Object[] load(
  @ParamInfo(name="2df File", style="load-dialog", options="endings=[\".2df\"]; description=\"*.2df-Files\"") File input) throws IOException {
    
    // read lines and strip comments as well as empty lines
    List<String> lines = Files.readAllLines(input.toPath()).
    stream().map(l->l.trim()).
    filter(l->!l.startsWith("#")&&!l.isEmpty()).
    collect(Collectors.toList());
    
    // check that we have content
    if(lines.size() < 3) {
      throw new RuntimeException("File '$input' is invalid: it contains less than 3 lines.");
    }
    
    // read num-verts and num-tris
    String[] numEntries = lines.get(0).split("\\s");
    
    if(numEntries.length < 2) {
      throw new RuntimeException("File '$input' is invalid: number of verts and/or number of tris unspecified");
    }

    // read number of vertices
    int numVerts = 0;
    try{
      numVerts = Integer.parseInt(numEntries[0]);
    } catch(Exception ex) {
      throw new RuntimeException("File '$input' is invalid: number of verts does not specify an integer value");
    }

    // read number of triangles
    int numTris = 0;
    try{
      numTris = Integer.parseInt(numEntries[1]);
    } catch(Exception ex) {
      throw new RuntimeException("File '$input' is invalid: number of tris does not specify an integer value");
    }

    // read vertices (x,y)
    double[][] vertices = new double[numVerts][];
    for(int i = 1; i < numVerts+1; i++) {
      String[] numEntriesVerts = lines.get(i).split("\\s");
      if(numEntriesVerts.length < 2) {
        throw new RuntimeException("File '$input' is invalid: number of vert coordinates in vertex $i");
      }
      double x = 0;
      try{
        x = Double.parseDouble(numEntriesVerts[0]);
      } catch(Exception ex) {
        throw new RuntimeException("File '$input' is invalid: vertex x coord is invalid in vertex $i");
      }
      double y = 0;
      try{
        y = Double.parseDouble(numEntriesVerts[1]);
      } catch(Exception ex) {
        throw new RuntimeException("File '$input' is invalid: vertex y coord is invalid in vertex $i");
      }
      vertices[i-1] = new double[2];
      vertices[i-1][0] = x;
      vertices[i-1][1] = y;
    }

    // read triangles (v0,v1,v2)
    int[][] triangles = new int[numTris][];
    for(int i = numVerts+1; i < numVerts+numTris+1; i++) {
      String[] numEntriesTris = lines.get(i).split("\\s");
      if(numEntriesTris.length < 3) {
        throw new RuntimeException("File '$input' is invalid: wrong number of indices in triangle " + (i-numVerts-1));
      }
      triangles[i-numVerts-1] = new int[3];
      for(int j = 0; j < 3; j++) {
        try{
          triangles[i-numVerts-1][j] = Integer.parseInt(numEntriesTris[j]);
        } catch(Exception ex) {
          throw new RuntimeException("File '$input' is invalid: index $j in triangle " + (i-numVerts-1) +" is not valid");
        }
      }
    }

    return new Object[]{vertices,triangles};
  }
}