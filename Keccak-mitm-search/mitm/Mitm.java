package mitmsearch.mitm;

import gurobi.*;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.File;
import java.io.FileWriter;
public class Mitm {
  private final int Rounds;
  private final GRBModel model;
  private FileWriter logfile ;
  private final MitmFactory factory;
  private final GRBVar[][][][][] DA;
  private final GRBVar[][][][] DP;
  private final GRBVar[][][] DC1;
  private final GRBVar[][][][] DP2;
  private final GRBVar[][][] DC12;
  private final GRBVar[][][][][] DB; 
  private final GRBVar[][][][] DC2;   
  private final GRBLinExpr   objective;
  private final GRBVar[] obj;
  private final GRBVar[][] dom;

 
  /** * @param env the Gurobi environment
   */
  public Mitm(final GRBEnv env, final int Rounds) throws GRBException {
    model = new GRBModel(env);
    this.Rounds = Rounds;

    factory = new MitmFactory(model);
    DA = new GRBVar[Rounds+1][5][5][64][3];
    DP = new GRBVar[Rounds][5][64][3];
    DP2 = new GRBVar[Rounds][5][64][3];
    DC1 = new GRBVar[Rounds][5][64];
    DC12 = new GRBVar[Rounds][5][64];
    DB = new GRBVar[Rounds][5][5][64][3];
    DC2 = new GRBVar[Rounds][5][5][64];
    // Initialization
    for (int round = 0; round < Rounds+1; round++)
      for (int i = 0; i < 5; i++) 
        for (int j = 0; j < 5; j++) 
	  for (int k = 0; k < 64; k++)         
            for (int l = 0; l < 3; l++) {     
              DA[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_"+round+"_"+i+"_"+j+"_"+k+"_"+l);            
    }

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 5; i++) 
        for (int j = 0; j < 5; j++) 
	  for (int k = 0; k < 64; k++)           
            for (int l = 0; l < 3; l++) {     
              DB[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DB_"+round+"_"+i+"_"+j+"_"+k+"_"+l);    	          
    }

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 5; i++) 
        for (int j = 0; j < 5; j++) 
	  for (int k = 0; k < 64; k++) {    
            DC2[round][i][j][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DC2_"+round+"_"+i+"_"+j+"_"+k); 
    	             
    }
    

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 5; i++) 
	for (int k = 0; k < 64; k++) 
	  for (int l = 0; l < 3; l++)  {         
            DP[round][i][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DP_"+round+"_"+i+"_"+k+"_"+l);               
    }

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 5; i++) 
	for (int k = 0; k < 64; k++) 
	  for (int l = 0; l < 3; l++)  {         
	    DP2[round][i][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DP2_"+round+"_"+i+"_"+k+"_"+l);              
    }
    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 5; i++) 
	for (int k = 0; k < 64; k++) {       
          DC1[round][i][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DC1_"+round+"_"+i+"_"+k);                         
    }

    for (int round = 0; round < Rounds; round++)
      for (int i = 0; i < 5; i++) 
	for (int k = 0; k < 64; k++) {      
	  DC12[round][i][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DC12_"+round+"_"+i+"_"+k);               
    }

    dom = new GRBVar[2][64];
    for (int i = 0; i < 2; i++) 
      for (int k = 0; k < 64; k++){
        dom[i][k]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "dom_"+i+"_"+k);
    }

    //fixed input
    for (int i = 0; i < 5; i++) 
      for (int j = 0; j < 5; j++) 
        for (int k = 0; k < 64; k++) {
	  for (int l = 0; l < 3; l++)  {
            model.addConstr(DA[0][0][0][k][l], GRB.EQUAL, DA[0][1][3][(k+36)%64][l], "");  
 	    model.addConstr(DA[0][0][4][(k+62)%64][l], GRB.EQUAL, DA[0][1][2][(k+6)%64][l], "");
          }
          if ((i==0&j==0) | (i==1&j==3) | (i==0&j==4) | (i==1&j==2) ) {            
            model.addConstr(DA[0][i][j][k][1], GRB.EQUAL, 1, ""); 
            GRBLinExpr known = new GRBLinExpr();
            known.addTerm(1, DA[0][i][j][k][0]);
            known.addTerm(1, DA[0][i][j][k][2]);
            model.addConstr(known, GRB.GREATER_EQUAL, 1, "");      
	  }
          else {
            model.addConstr(DA[0][i][j][k][0], GRB.EQUAL, 1, "");   
            model.addConstr(DA[0][i][j][k][1], GRB.EQUAL, 1, "");  
            model.addConstr(DA[0][i][j][k][2], GRB.EQUAL, 1, ""); 
          }
	  
    }

    article_attack();
    

    // Constraints
    factory.addfivexor_red(DA, DP, DC1);
    factory.addtwoxor_red(DP2, DP, DC12);
    factory.addTheta_red(DA, DP2, DB, DC2);
    factory.addSbox_nc(DB, DA);

    factory.addDoMnew(DA, dom);

    GRBVar[][] beta = new GRBVar[2][64];
    for (int i = 0; i < 2; i++) 
      for (int k = 0; k < 64; k++){
        beta[i][k]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "beta_"+i+"_"+k);
    };
    factory.betaConstraints(DA, beta);



    objective = new GRBLinExpr();  
    GRBLinExpr DoF_red = new GRBLinExpr();
    GRBLinExpr DoF_blue = new GRBLinExpr();
    GRBLinExpr DoM = new GRBLinExpr();
    
    GRBVar Obj = model.addVar(0.0, 128.0, 0.0, GRB.INTEGER, "Obj");
    
    obj = new GRBVar[3];
    obj[0] = model.addVar(0.0, 128.0, 0.0, GRB.INTEGER, "Obj1"); 
    obj[1] = model.addVar(0.0, 128.0, 0.0, GRB.INTEGER, "Obj2"); 
    obj[2] = model.addVar(0.0, 128.0, 0.0, GRB.INTEGER, "Obj3"); 
                
      
    for (int k = 0; k < 64; k++) {    
      DoF_blue.addTerm(1.0, DA[0][0][0][k][0]);
      DoF_red.addTerm(1.0, DA[0][0][0][k][2]);
      DoF_blue.addTerm(-1.0, beta[0][k]);
      DoF_red.addTerm(-1.0, beta[0][k]);
      
      DoF_blue.addTerm(1.0, DA[0][0][4][k][0]);
      DoF_red.addTerm(1.0, DA[0][0][4][k][2]);
      DoF_blue.addTerm(-1.0, beta[1][k]);
      DoF_red.addTerm(-1.0, beta[1][k]);
       
    }

    for (int round = 0; round < Rounds; round ++) 
      for (int i = 0; i < 5; i++) 
        for (int k = 0; k < 64; k++) {
          DoF_red.addTerm(-1.0, DC1[round][i][k]);
          //DoF_blue.addTerm(-1.0, DC1[round][i][k][1]);
          DoF_red.addTerm(-1.0, DC12[round][i][k]);
          //DoF_blue.addTerm(-1.0, DC12[round][i][k][1]);
          for (int j = 0; j < 5; j++) {
            DoF_red.addTerm(-1.0, DC2[round][i][j][k]);
	    //DoF_blue.addTerm(-1.0, DC2[round][i][j][k][1]);        
          }
    }
    


    for (int i = 0; i < 2; i++) 
      for (int k = 0; k < 64; k++) {
        DoM.addTerm(1.0, dom[i][k]);
    }
    
    objective.addTerm(1.0, Obj);

    model.addConstr(DoF_red, GRB.GREATER_EQUAL, 1, "");
    model.addConstr(DoF_blue, GRB.GREATER_EQUAL, 1, "");
    model.addConstr(DoM, GRB.GREATER_EQUAL, 1, "");

    model.addConstr(DoF_red, GRB.EQUAL, obj[0], "");
    model.addConstr(DoF_blue, GRB.EQUAL, obj[1], "");
    model.addConstr(DoM, GRB.EQUAL, obj[2], ""); 
    
    model.addConstr(objective, GRB.LESS_EQUAL, DoF_blue, "");
    model.addConstr(objective, GRB.LESS_EQUAL, DoF_red, "");
    model.addConstr(objective, GRB.LESS_EQUAL, DoM, "");
    //model.addConstr(objective, GRB.LESS_EQUAL, 8, "");
    model.setObjective(objective, GRB.MAXIMIZE);
  }

  public List<MitmSolution> solve() throws GRBException  
  {
    model.write("model.lp");
    model.optimize();
    model.write("output.sol");
    //model.computeIIS();
    //model.write("model1.ilp");
    return getAllFoundSolutions();
  }

  public void dispose() throws GRBException {
    model.dispose();
  }

  public List<MitmSolution> getAllFoundSolutions() throws GRBException {
    return IntStream.range(0, model.get(GRB.IntAttr.SolCount)).boxed()
      .map(solNb -> getSolution(solNb))
      .collect(Collectors.toList());
  }

  private MitmSolution getSolution(final int solutionNumber) {
    try {
      model.set(GRB.IntParam.SolutionNumber, solutionNumber);
      int[][][][][] DAValue     = new int[Rounds+1][5][5][64][3];
      int[][][][] DPValue  = new int[Rounds][5][64][3];
      int[][][][] DP2Value  = new int[Rounds][5][64][3];
      int[][][] DC1Value = new int[Rounds][5][64];
      int[][][] DC12Value = new int[Rounds][5][64];
      int[][][][][] DBValue  = new int[Rounds][5][5][64][3];
      int[][][][] DC2Value  = new int[Rounds][5][5][64];
      int[][] domValue  = new int[2][64];
      int[] objValue  = new int[3];

      for (int round = 0; round < Rounds; round++)
        for (int i = 0; i < 5; i++) 
          for (int j = 0; j < 5; j++)
	    for (int k = 0; k < 64; k++) {
              DC2Value[round][i][j][k]  = (int) Math.round(DC2[round][i][j][k].get(GRB.DoubleAttr.Xn));
              
	      for (int l = 0; l < 3; l++)  { 
                DAValue[round][i][j][k][l]  = (int) Math.round(DA[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
                DBValue[round][i][j][k][l]  = (int) Math.round(DB[round][i][j][k][l].get(GRB.DoubleAttr.Xn));
              }
      }
      for (int i = 0; i < 5; i++) 
        for (int j = 0; j < 5; j++)
	  for (int k = 0; k < 64; k++) 
            for (int l = 0; l < 3; l++)  { 
              DAValue[Rounds][i][j][k][l]  = (int) Math.round(DA[Rounds][i][j][k][l].get(GRB.DoubleAttr.Xn));
            }
      for (int round = 0; round < Rounds; round++)
        for (int i = 0; i < 5; i++) 
	  for (int k = 0; k < 64; k++) {
            DC1Value[round][i][k]  = (int) Math.round(DC1[round][i][k].get(GRB.DoubleAttr.Xn));
            DC12Value[round][i][k]  = (int) Math.round(DC12[round][i][k].get(GRB.DoubleAttr.Xn));
	    
	    for (int l = 0; l < 3; l++)  { 
              DPValue[round][i][k][l]  = (int) Math.round(DP[round][i][k][l].get(GRB.DoubleAttr.Xn));
	      DP2Value[round][i][k][l]  = (int) Math.round(DP2[round][i][k][l].get(GRB.DoubleAttr.Xn));
	    }
      }
      
      for (int i = 0; i < 2; i++) 
	for (int k = 0; k < 64; k++) 
          domValue[i][k]  = (int) Math.round(dom[i][k].get(GRB.DoubleAttr.Xn));

      for (int i = 0; i < 3; i++) 
        objValue[i]  = (int) Math.round(obj[i].get(GRB.DoubleAttr.Xn));
	   
      return new MitmSolution(Rounds, (int) Math.round(model.get(GRB.DoubleAttr.PoolObjVal)), DAValue,DBValue, DC2Value, DPValue, DP2Value, DC1Value, DC12Value, domValue, objValue);
    } catch (GRBException e) {
      System.out.println("Error code: " + e.getErrorCode() + ". " + e.getMessage());
      e.printStackTrace();
      System.exit(1);
      return null; // Can't access
    }
  }

  private void article_attack() throws GRBException 
  {
     int[][][][] DA_init = new int[][][][] 
  {{{{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1}},{{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1}},{{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1}},{{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1}},{{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1}}},{{{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1}},{{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1}},{{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{1,1,0},{0,1,1}},{{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,0},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{0,1,1},{1,1,0},{0,1,1}},{{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1},{1,1,1}}}};

    for (int i = 0; i < 2; i++) 
      for (int j = 0; j < 5; j++) 
        for (int k = 0; k < 64; k++) 
          for (int l = 0; l < 3; l++)  {
              model.addConstr(DA[0][i][j][k][l], GRB.EQUAL, DA_init[i][j][k][l], "");  
          } 
	    
	  
  }

}
