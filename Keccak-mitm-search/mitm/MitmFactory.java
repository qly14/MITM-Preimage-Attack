package mitmsearch.mitm;

import gurobi.*;

public class MitmFactory {
  private GRBModel model;
  private static final int[][] rho = new int[][]{{0,36,3,41,18},{1,44,10,45,2},{62,6,43,15,61},{28,55,25,21,56},{27,20,39,8,14}};


  public MitmFactory(final GRBModel model) {
    this.model = model;
  }

  public void addfivexor_red(GRBVar[][][][][] DA, GRBVar[][][][] DP, GRBVar[][][] DC1) throws GRBException {
    GRBVar[][][][] DA_Allone = new GRBVar[DP.length][5][64][3];

    GRBVar[][] DA_Allzero = new GRBVar[5][64];
    double[] t1 = {1.0, 1.0, -1.0};

    for (int round = 0; round < DP.length; round ++) 
      for (int i = 0; i < 5; i ++) 
	for (int k = 0; k < 64; k ++) {
          for (int l = 0; l < 3; l ++) {
            DA_Allone[round][i][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_Allone_"+round+"_"+i+"_"+k+"_"+l); 
            if (round == 0) {
              if (i==0)
                Determine_AllOne(DA_Allone[round][i][k][l],DA[round][i][0][k][l],DA[round][i][4][k][l]);
              if (i==1)
                Determine_AllOne(DA_Allone[round][i][k][l],DA[round][i][2][k][l],DA[round][i][3][k][l]);
              if (i>=2) 
                model.addConstr(DA_Allone[round][i][k][l], GRB.EQUAL, 1, "");
            }
            else {
              Determine_AllOne(DA_Allone[round][i][k][l],DA[round][i][0][k][l],DA[round][i][1][k][l],DA[round][i][2][k][l],DA[round][i][3][k][l],DA[round][i][4][k][l]);
            }
          }
	  if (round==0) {
	    DA_Allzero[i][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DA_Allzero_"+i+"_"+k); 
            if (i==0)
              Determine_Allzero(DA_Allzero[i][k],DA[round][i][0][k][0],DA[round][i][4][k][0]);
            if (i==1)
              Determine_Allzero(DA_Allzero[i][k],DA[round][i][2][k][0],DA[round][i][3][k][0]);
            if (i>=2) 
              model.addConstr(DA_Allzero[i][k], GRB.EQUAL, 0, "");
          }

          model.addConstr(DA_Allone[round][i][k][1], GRB.EQUAL, DP[round][i][k][1], "");
          model.addConstr(DA_Allone[round][i][k][1], GRB.GREATER_EQUAL, DP[round][i][k][0], "");
          model.addConstr(DA_Allone[round][i][k][0], GRB.LESS_EQUAL, DP[round][i][k][0], "");
          model.addConstr(linExprOf(t1, DC1[round][i][k], DA_Allone[round][i][k][0], DP[round][i][k][0]), GRB.EQUAL, 0, "");
          model.addConstr(DA_Allone[round][i][k][2], GRB.EQUAL, DP[round][i][k][2], "");

          if (round==0)
            model.addConstr(DC1[round][i][k], GRB.LESS_EQUAL, DA_Allzero[i][k], "");
        }  
  }

  public void addtwoxor_red(GRBVar[][][][] DP2, GRBVar[][][][] DP, GRBVar[][][] DC12) throws GRBException {
    GRBVar[][][][] DP_Allone = new GRBVar[DP.length][5][64][3];

    GRBVar[][][] DP_Allzero = new GRBVar[DP.length][5][64];
    for (int round = 0; round < DP.length; round ++)
      for (int i = 0; i < 5; i ++) 
	for (int k = 0; k < 64; k ++) {
          for (int l = 0; l < 3; l ++) {
            DP_Allone[round][i][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DP_Allone_"+round+"_"+i+"_"+k+"_"+l); 
            Determine_AllOne(DP_Allone[round][i][k][l],DP[round][(i+4)%5][k][l],DP[round][(i+1)%5][(k+63)%64][l]);
          }

          DP_Allzero[round][i][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DP_Allzero_"+round+"_"+i+"_"+k); 
          Determine_Allzero(DP_Allzero[round][i][k],DP[round][(i+4)%5][k][0],DP[round][(i+1)%5][(k+63)%64][1]);

          double[] t1 = {1.0, 1.0, -1.0};
          model.addConstr(DP_Allone[round][i][k][1], GRB.EQUAL, DP2[round][i][k][1], "");
          model.addConstr(DP_Allone[round][i][k][1], GRB.GREATER_EQUAL, DP2[round][i][k][0], "");
          model.addConstr(DP_Allone[round][i][k][0], GRB.LESS_EQUAL, DP2[round][i][k][0], "");
          model.addConstr(linExprOf(t1, DC12[round][i][k], DP_Allone[round][i][k][0], DP2[round][i][k][0]), GRB.EQUAL, 0, "");
          model.addConstr(DP_Allone[round][i][k][2], GRB.EQUAL, DP2[round][i][k][2], "");

          model.addConstr(DC12[round][i][k], GRB.LESS_EQUAL, DP_Allzero[round][i][k], "");
        }      
  }
  
  public void addTheta_red(GRBVar[][][][][] DA, GRBVar[][][][] DP2, GRBVar[][][][][] DB, GRBVar[][][][] DC2) throws GRBException {
    GRBVar[][][][][] DP2_Allone = new GRBVar[DP2.length][5][5][64][3];
    
    GRBVar[][][][] DP2_Allzero = new GRBVar[DP2.length][5][5][64];
    for (int round = 0; round < DP2.length; round ++)
      for (int i = 0; i < 5; i ++) 
        for (int j = 0; j < 5; j ++)
	  for (int k = 0; k < 64; k ++) {
            for (int l = 0; l < 3; l ++) {
              DP2_Allone[round][i][j][k][l] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DP2_Allone_"+round+"_"+i+"_"+j+"_"+k+"_"+l); 
              Determine_AllOne(DP2_Allone[round][i][j][k][l],DA[round][i][j][k][l], DP2[round][i][k][l]);
            }
          DP2_Allzero[round][i][j][k] = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "DP2_Allzero_"+round+"_"+i+"_"+j+"_"+k); 
          Determine_Allzero(DP2_Allzero[round][i][j][k],DA[round][i][j][k][0], DP2[round][i][k][0]);


          double[] t1 = {1.0, 1.0, -1.0};
          model.addConstr(DP2_Allone[round][i][j][k][1], GRB.EQUAL, DB[round][j][(2*i+3*j)%5][(k+rho[i][j])%64][1], "");
          model.addConstr(DP2_Allone[round][i][j][k][1], GRB.GREATER_EQUAL, DB[round][j][(2*i+3*j)%5][(k+rho[i][j])%64][0], "");
          model.addConstr(DP2_Allone[round][i][j][k][0], GRB.LESS_EQUAL, DB[round][j][(2*i+3*j)%5][(k+rho[i][j])%64][0], "");
          model.addConstr(linExprOf(t1, DC2[round][i][j][k], DP2_Allone[round][i][j][k][0], DB[round][j][(2*i+3*j)%5][(k+rho[i][j])%64][0]), GRB.EQUAL, 0, "");
          model.addConstr(DP2_Allone[round][i][j][k][2], GRB.EQUAL, DB[round][j][(2*i+3*j)%5][(k+rho[i][j])%64][2], "");

          
          model.addConstr(DC2[round][i][j][k], GRB.LESS_EQUAL, DP2_Allzero[round][i][j][k], "");
        } 
  }
  


  public void addSbox_nc(GRBVar[][][][][] DB, GRBVar[][][][][] DA) throws GRBException {
    double[][] t = {{1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0},
	 	    {0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, -1},
	 	    {0, 1, -1, 1, 0, 0, 1, 0, 0, -1, -1, 1},
	 	    {0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, -1},
	 	    {0, -1, 0, 0, 0, -1, 0, 0, -1, 0, 1, 0},
	 	    {0, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0},
	 	    {0, 0, 0, 0, 0, 1, 1, 0, 0, 0, -1, 0},
	 	    {0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 1, 0},
	 	    {0, 0, 0, 1, 0, 0, 0, 0, 1, 0, -1, 0},
	 	    {0, 1, -1, 0, 1, -1, 0, 1, -1, 0, -1, 1},
	 	    {0, -1, 0, -1, 0, 0, -1, 0, 0, 0, 1, 0},
	 	    {0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, -1},
	 	    {-1, 1, 0, -1, 1, 0, -1, 1, 0, 1, -1, 0},
	 	    {0, -1, 0, 0, -1, 0, -1, 1, -1, 0, 1, 0},
	 	    {0, -1, 0, -1, 1, -1, 0, -1, 0, 0, 1, 0},
	 	    {0, 0, 0, 1, 0, 0, 0, 0, 0, -1, 0, 0, 0},
	 	    {0, 0, 0, 0, 1, 0, 0, 0, 0, 0, -1, 0, 0},
	 	    {0, 0, 0, 0, 0, 0, 0, 1, 0, 0, -1, 0, 0},
	 	    {0, 0, 0, 0, 0, 0, 1, 0, 0, -1, 0, 0, 0}};

    double[] c = {0, 0, 0, 0, -2, 0, 0, 0, 0, 0, -2, 0, 0, -2, -2, 0, 0, 0, 0};
    for (int round = 0; round < DB.length; round ++)
      for (int i = 0; i < 5; i ++) 
        for (int j = 0; j < 5; j ++) 
          for (int k = 0; k < 64; k ++) {
            for (int q = 0; q < 19; q++) {
              model.addConstr(linExprOf(t[q], DB[round][i][j][k][0], DB[round][i][j][k][1], DB[round][i][j][k][2], DB[round][(i+1)%5][j][k][0], DB[round][(i+1)%5][j][k][1], DB[round][(i+1)%5][j][k][2], DB[round][(i+2)%5][j][k][0], DB[round][(i+2)%5][j][k][1], DB[round][(i+2)%5][j][k][2], DA[round+1][i][j][k][0], DA[round+1][i][j][k][1], DA[round+1][i][j][k][2]), GRB.GREATER_EQUAL, c[q], "");
            }
     }
  }


  public void addDoMnew(GRBVar[][][][][] DA, GRBVar[][] dom) throws GRBException {    
    GRBVar[][] Nowhite = new GRBVar[2][64];
    GRBVar[][] Existred = new GRBVar[2][64];
    GRBVar[][] Existblue = new GRBVar[2][64];
    for (int i = 0; i < 2; i++) 
      for (int k = 0; k < 64; k++) {
        Nowhite[i][k]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Nowhite_"+i+"_"+k);
        Existred[i][k]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Existred_"+i+"_"+k);
        Existblue[i][k]   = model.addVar(0.0, 1.0, 0.0, GRB.BINARY, "Existblue_"+i+"_"+k);

    }
      
    int r=DA.length-1;
    for (int k = 0; k < 64; k ++) {
      Determine_AllOne(Nowhite[0][k],DA[r][3][0][(k-rho[3][0]+64)%64][1],DA[r][3][3][(k-rho[3][0]+64)%64][1],DA[r][0][2][(k-rho[0][2]+64)%64][1],DA[r][0][0][(k-rho[0][2]+64)%64][1]);
      Determine_AllOne(Nowhite[1][k],DA[r][4][1][(k-rho[4][1]+64)%64][1],DA[r][4][4][(k-rho[4][1]+64)%64][1],DA[r][1][3][(k-rho[1][3]+64)%64][1],DA[r][1][1][(k-rho[1][3]+64)%64][1]);
      Determine_Existzero(Existred[0][k],DA[r][3][0][(k-rho[3][0]+64)%64][0],DA[r][3][3][(k-rho[3][0]+64)%64][0]);
      Determine_Existzero(Existred[1][k],DA[r][4][1][(k-rho[4][1]+64)%64][0],DA[r][4][4][(k-rho[4][1]+64)%64][0]);
      Determine_Existzero(Existblue[0][k],DA[r][3][0][(k-rho[3][0]+64)%64][2],DA[r][3][3][(k-rho[3][0]+64)%64][2]);
      Determine_Existzero(Existblue[1][k],DA[r][4][1][(k-rho[4][1]+64)%64][2],DA[r][4][4][(k-rho[4][1]+64)%64][2]);
      
      Determine_AllOne(dom[0][k],Nowhite[0][k],Existred[0][k],Existblue[0][k]);
      Determine_AllOne(dom[1][k],Nowhite[1][k],Existred[1][k],Existblue[1][k]);
    } 


  }

  public void betaConstraints(GRBVar[][][][][] DA, GRBVar[][] beta) throws GRBException {
    double[] t = {1, 1, -2};
    for (int k = 0; k < 64; k ++) {
      model.addConstr(DA[0][0][0][k][0], GRB.GREATER_EQUAL, beta[0][k], "");
      model.addConstr(DA[0][0][0][k][2], GRB.GREATER_EQUAL, beta[0][k], "");     
      model.addConstr(linExprOf(t, DA[0][0][0][k][0], DA[0][0][0][k][2], beta[0][k]), GRB.LESS_EQUAL, 1, "");

      model.addConstr(DA[0][0][4][k][0], GRB.GREATER_EQUAL, beta[1][k], "");
      model.addConstr(DA[0][0][4][k][2], GRB.GREATER_EQUAL, beta[1][k], "");     
      model.addConstr(linExprOf(t, DA[0][0][4][k][0], DA[0][0][4][k][2], beta[1][k]), GRB.LESS_EQUAL, 1, "");
    }
  }

  public void Determine_Allzero(GRBVar mainVar, GRBVar ... vars) throws GRBException {
        GRBLinExpr expr = new GRBLinExpr();
	expr.addTerm(1.0, mainVar);
        for (int i = 0; i < vars.length; i++) {
          expr.addTerm(1.0, vars[i]);
	  model.addConstr(linExprOf(mainVar,vars[i]), GRB.LESS_EQUAL, 1, "");
        }
        model.addConstr(expr, GRB.GREATER_EQUAL, 1, "");
  }

  public void Determine_AllOne(GRBVar mainVar, GRBVar ... vars) throws GRBException {
        GRBLinExpr exprm = new GRBLinExpr();
	exprm.addTerm(1.0, mainVar);
	GRBLinExpr exprp = new GRBLinExpr();
	exprp.addTerm(-1.0*vars.length, mainVar);
        for (int i = 0; i < vars.length; i++) {
          exprm.addTerm(-1.0, vars[i]);
	  exprp.addTerm(1.0, vars[i]);
        }
        model.addConstr(exprm, GRB.GREATER_EQUAL, 1-vars.length, "");
	model.addConstr(exprp, GRB.GREATER_EQUAL, 0, "");
  }
  
  public void Determine_ExistOne(GRBVar mainVar, GRBVar ... vars) throws GRBException {
        GRBLinExpr expr1 = new GRBLinExpr();
	expr1.addTerm(1.0, mainVar);
        GRBLinExpr exprm = new GRBLinExpr();
        exprm.addTerm(vars.length, mainVar);
        for (int i = 0; i < vars.length; i++) {
          expr1.addTerm(-1.0, vars[i]);
          exprm.addTerm(-1.0, vars[i]);
        }
        model.addConstr(exprm, GRB.GREATER_EQUAL, 0, "");
	model.addConstr(expr1, GRB.LESS_EQUAL, 0, "");
  }

  public void Determine_Existzero(GRBVar mainVar, GRBVar ... vars) throws GRBException {
        GRBLinExpr expr = new GRBLinExpr();
	expr.addTerm(1.0, mainVar);
        for (int i = 0; i < vars.length; i++) {
          expr.addTerm(1.0, vars[i]);
          model.addConstr(linExprOf(mainVar,vars[i]), GRB.GREATER_EQUAL, 1, "");
        }
	model.addConstr(expr, GRB.LESS_EQUAL, vars.length, "");
  }

  public GRBLinExpr linExprOf(double[] coeffs, GRBVar ... vars) throws GRBException {
    GRBLinExpr ofVars = new GRBLinExpr();
    ofVars.addTerms(coeffs, vars);
    return ofVars;
  }

  public GRBLinExpr linExprOf(double constant, GRBVar ... vars) {
    GRBLinExpr ofVars = linExprOf(vars);
    ofVars.addConstant(constant);
    return ofVars;
  }

  public GRBLinExpr linExprOf(GRBVar ... vars) {
    GRBLinExpr expr = new GRBLinExpr();
    for (GRBVar var : vars)
      expr.addTerm(1.0, var);
    return expr;
  }
 
}
