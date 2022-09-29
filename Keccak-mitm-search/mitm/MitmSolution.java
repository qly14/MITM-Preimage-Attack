package mitmsearch.mitm;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.List;
import java.io.File;
import java.io.IOException;

public class MitmSolution {
  public int Rounds;
  public int objective;
  public int[][][][][] DA;
  public int[][][][] DP;
  public int[][][][] DP2;
  public int[][][] DC1;
  public int[][][] DC12;
  public int[][][][][] DB;
  public int[][][][] DC2;
  public int[][] dom;
  public int[] obj;

  public MitmSolution() {}

  public MitmSolution(int Rounds, int objective, int[][][][][] DA, int[][][][][]DB, int[][][][]DC2, int[][][][] DP, int[][][][] DP2, int[][][] DC1, int[][][] DC12, int[][] dom, int[] obj) {
    this.Rounds = Rounds;
    this.objective = objective;
    this.DA = DA;
    this.DP = DP;
    this.DP2 = DP2;
    this.DC1 = DC1;
    this.DC12 = DC12;
    this.DB = DB;
    this.DC2 = DC2;   
    this.dom = dom;
    this.obj = obj;
  }

  public void toFile(String fileName) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File(fileName), this);
    }
    catch (JsonParseException e) { e.printStackTrace(); System.exit(1); }
    catch (JsonMappingException e) { e.printStackTrace(); System.exit(1); }
    catch (IOException e) { e.printStackTrace(); System.exit(1); }
  }

  public static void toFile(String fileName, List<MitmSolution> solutions) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File(fileName), solutions);
    }
    catch (JsonParseException e) { e.printStackTrace(); System.exit(1); }
    catch (JsonMappingException e) { e.printStackTrace(); System.exit(1); }
    catch (IOException e) { e.printStackTrace(); System.exit(1); }
  }

  public static void toFile(String fileName, MitmSolution solutions) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(new File(fileName), solutions);
    }
    catch (JsonParseException e) { e.printStackTrace(); System.exit(1); }
    catch (JsonMappingException e) { e.printStackTrace(); System.exit(1); }
    catch (IOException e) { e.printStackTrace(); System.exit(1); }
  }

  public static void toFile(File file, List<MitmSolution> solutions) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      mapper.writeValue(file, solutions);
    }
    catch (JsonParseException e) { e.printStackTrace(); System.exit(1); }
    catch (JsonMappingException e) { e.printStackTrace(); System.exit(1); }
    catch (IOException e) { e.printStackTrace(); System.exit(1); }
  }

  public static List<MitmSolution> fromFile(String fileName) {
    return fromFile(new File(fileName));
  }

  public static List<MitmSolution> fromFile(File file) {
    try {
      return new ObjectMapper().readValue(file, new TypeReference<List<MitmSolution>>(){});
    }
    catch (JsonParseException e) { e.printStackTrace(); System.exit(1); }
    catch (JsonMappingException e) { e.printStackTrace(); System.exit(1); }
    catch (IOException e) { e.printStackTrace(); System.exit(1); }
    return null; // Can't reach
  }
}
