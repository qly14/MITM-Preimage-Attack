package mitmsearch;

import mitmsearch.mitm.Mitm;
import mitmsearch.mitm.MitmSolution;

import gurobi.*;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.ArgGroup;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ExecutionException;
import java.io.File;

@Command(name = "mitmsearch", mixinStandardHelpOptions = true, version = "1.0", description = "Find MIMT attacks on JH")
public class MitmSearch implements Callable<Integer> {
  
  @Option(names = {"-verbose","-v"}, description = "Verbose mode. Default is false")
  private boolean verbose;
  @Option(names = {"-Rounds","-r"}, defaultValue = "12", description = "Number of rounds for the MITM attack. Default is ${DEFAULT-VALUE}")
  private int Rounds;
  @Option(names = {"-Threads","-t"}, defaultValue = "0", description = "Number of threads allowed to use. Default takes as many as possible")
  private int Threads;
  

  @Option(names = {"-nonOptimalSols","-nonOpt"}, description = "Search for non optimal solutions (up to obj = 2*bestObj) . Default is false")
  private boolean nonOptimalSols;
  @Option(names = {"-MinObjectiveValue","-obj"}, defaultValue = "-1", description = "Value of objective (may not be optimal). Default search for optimal solutions. Seems to greatly increase the running time, preferably you should also generate the optimal solutions")
  private int MinObjectiveValue;
  @Option(names = {"-nSols","-sol"}, defaultValue = "1", description = "Maximum number to find in step1. Default is ${DEFAULT-VALUE}")
  private int nSols;
  @Option(names = {"-output","-so"}, defaultValue = "output/result.json", description = "Output file of the solutions. Default is ${DEFAULT-VALUE}")
  private File output;

  
  /** The main function instanciates the CLI, get the parameters and then execute the runner (function call()) */
  public static void main(final String... args) throws Exception {
    System.exit(new CommandLine(new MitmSearch()).execute(args));
  }

  /** Main function that runs the models */
  @Override
  public Integer call() {
    
    List<MitmSolution> MitmSolutions;
   
    MitmSolutions = getMitmSolutions();
    MitmSolution.toFile(output, MitmSolutions);
 

    return 0;
  }

  /** Run the Gurobi model to solve the step 1 */
  private List<MitmSolution> getMitmSolutions() {
    try {
      GRBEnv env = new GRBEnv(true);
      env.set(GRB.IntParam.OutputFlag, (verbose) ? 1 : 0);
      env.start();
      Mitm mitm = new Mitm(env, Rounds);
      if (verbose)
        System.out.println("Starting");
      List<MitmSolution> MitmSolutions = mitm.solve(nSols, nonOptimalSols, MinObjectiveValue, Threads);
      if (verbose) {
        System.out.println("The best solution has objective " + MitmSolutions.get(0).objective);
        System.out.println("Found "+ MitmSolutions.size() +" solution(s)");
      }
      mitm.dispose();
      env.dispose();
      return MitmSolutions;
    } catch (GRBException e) {
      System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
      return null; // Can't access
    }
  }
}
