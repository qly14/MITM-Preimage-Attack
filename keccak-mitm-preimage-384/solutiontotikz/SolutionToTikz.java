package mitmsearch.solutiontotikz;

import mitmsearch.mitm.MitmSolution;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ArgGroup;

import java.util.Arrays;
import java.util.concurrent.Callable;
import java.io.File;
import java.io.*;

@Command(name = "mitmsearch", mixinStandardHelpOptions = true, version = "1.0", description = "")
public class SolutionToTikz implements Callable<Integer> {
  
  @Option(names = {"-solNumber","-sol"}, defaultValue = "0", description = "The solution number in the array. Default is ${DEFAULT-VALUE}")
  private int solNumber;

  @Option(names = {"-input","-si"}, description = "Output file of solutions to convert to tikz.")
  private String input;

  /** The main function instanciates the CLI, get the parameters and then execute the runner (function call()) */
  public static void main(final String... args) throws Exception {
    System.exit(new CommandLine(new SolutionToTikz()).execute(args));
  }

  /** Main function that runs the models */
  @Override
  public Integer call() {
    String tikz = "";
    if (input != null)
      tikz = new MitmSolutionToTikz(input, solNumber).generate();
    FileWriter fw = null;
    try {
      fw = new FileWriter("outputimm/tikzpic5.tex");
      fw.write(tikz);
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      try {
        fw.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
    //System.out.println(tikz);
    return 0;
  }

}
