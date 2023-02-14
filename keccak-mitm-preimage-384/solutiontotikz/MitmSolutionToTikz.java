package mitmsearch.solutiontotikz;

import mitmsearch.mitm.MitmSolution;

import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.lang.Math;

public class MitmSolutionToTikz {
  private final MitmSolution mitmSolution;
  private static final String WhiteColor  = "white";
  private static final String GrayColor  = "lightgray";
  private static final String BlueColor = "blue";
  private static final String RedColor = "red"; 
  private static final String PurpleColor  = "green!60";
  private static final int[][] rho = new int[][]{{0,36,3,41,18},{1,44,10,45,2},{62,6,43,15,61},{28,55,25,21,56},{27,20,39,8,14}};
  private static final int[][][] cond = new int[][][]{{{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1}},
          {{-1,1,-1,0},{-1,-1,-1,-1},{-1,1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,0},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1}},
          {{-1,-1,-1,-1},{-1,-1,-1,-1},{1,-1,0,-1},{-1,-1,0,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{1,-1,-1,-1},{-1,-1,-1,-1}},
          {{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{1,-1,-1,-1},{-1,-1,-1,-1}},
          {{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,0,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1}},
          {{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,0,-1,0},{-1,-1,-1,0},{-1,0,-1,-1},{-1,-1,-1,-1}},
          {{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1}},
          {{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1},{-1,-1,-1,-1}}};
  public MitmSolutionToTikz(final String filename, final int solutionNumber) {
    this(MitmSolution.fromFile(filename).get(solutionNumber));
  }

  public MitmSolutionToTikz(final MitmSolution mitmSolution) {
    this.mitmSolution = mitmSolution;
  }


  public String generate() {
    String output = "";
    // Header
    output += "\\documentclass{standalone}\n";
    output += "\\usepackage{tikz}\n";
    output += "\\usepackage{calc}\n";
    output += "\\usepackage{pgffor}\n";
    output += "\\usetikzlibrary{patterns}\n";
    output += "\\tikzset{base/.style = {draw=black, minimum width=0.02cm, minimum height=0.02cm, align=center, on chain},}\n";
    output += "\\begin{document}\n";
    output += "\\begin{tikzpicture}[scale = 0.45,every node/.style={scale=0.5}]\n";
    output += "\\makeatletter\n";

    for (int round = 0; round < mitmSolution.Rounds+1; round++) {
      
      output += " \\node[align=center] at ("+(-2)+","+(25*(mitmSolution.Rounds-round)+20.5)+") [scale=1.5]{\\textbf{\\huge $A^{("+(round+1)+")}$}};\n";
      
        for (int k = 0; k < 64; k++) 
        {       
          for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) 
            {
              if (mitmSolution.DA[round][i][j][k][0] == 0 & mitmSolution.DA[round][i][j][k][1] == 0 & mitmSolution.DA[round][i][j][k][2] == 0)
                output += "\\fill[color="+WhiteColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j)+18)+") rectangle ++(1,1);\n";
              if (mitmSolution.DA[round][i][j][k][0] == 0 & mitmSolution.DA[round][i][j][k][1] == 1 & mitmSolution.DA[round][i][j][k][2] == 1)
                output += "\\fill[color="+RedColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j)+18)+") rectangle ++(1,1);\n";
              if (mitmSolution.DA[round][i][j][k][0] == 1 & mitmSolution.DA[round][i][j][k][1] == 1 & mitmSolution.DA[round][i][j][k][2] == 0)
                output += "\\fill[color="+BlueColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j)+18)+") rectangle ++(1,1);\n";
              if (mitmSolution.DA[round][i][j][k][0] == 0 & mitmSolution.DA[round][i][j][k][1] == 1 & mitmSolution.DA[round][i][j][k][2] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j)+18)+") rectangle ++(1,1);\n";
              if (mitmSolution.DA[round][i][j][k][0] == 1 & mitmSolution.DA[round][i][j][k][1] == 1 & mitmSolution.DA[round][i][j][k][2] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j)+18)+") rectangle ++(1,1);\n";
            }
          output += "\\draw("+(6*k)+","+(25*(mitmSolution.Rounds-round)+18)+") grid ++(5,5);\n";
          output += " \\node[align=center] at ("+(6*k+2)+","+(25*(mitmSolution.Rounds-round)+17)+")[scale=2] {{\\Large z="+k+"}};\n";
        }
       //DP
       
      
       if ( round != mitmSolution.Rounds) {
         output += " \\node[align=center] at ("+(-2)+","+(25*(mitmSolution.Rounds-round)+15.5)+") [scale=1.5]{\\textbf{\\huge $C^{("+(round+1)+")}$}};\n";
         int consumered = 0;
         for (int k = 0; k < 64; k++) 
         {       
          for (int i = 0; i < 5; i++)
           {
              if (mitmSolution.DP[round][i][k][0] == 0 & mitmSolution.DP[round][i][k][1] == 0 & mitmSolution.DP[round][i][k][2] == 0)
                output += "\\fill[color="+WhiteColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+15)+") rectangle ++(1,1);\n";
              if (mitmSolution.DP[round][i][k][0] == 0 & mitmSolution.DP[round][i][k][1] == 1 & mitmSolution.DP[round][i][k][2] == 1)
                output += "\\fill[color="+RedColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+15)+") rectangle ++(1,1);\n";
              if (mitmSolution.DP[round][i][k][0] == 1 & mitmSolution.DP[round][i][k][1] == 1 & mitmSolution.DP[round][i][k][2] == 0)
                output += "\\fill[color="+BlueColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+15)+") rectangle ++(1,1);\n";
              if (mitmSolution.DP[round][i][k][0] == 0 & mitmSolution.DP[round][i][k][1] == 1 & mitmSolution.DP[round][i][k][2] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+15)+") rectangle ++(1,1);\n";
              if (mitmSolution.DP[round][i][k][0] == 1 & mitmSolution.DP[round][i][k][1] == 1 & mitmSolution.DP[round][i][k][2] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+15)+") rectangle ++(1,1);\n";
            }
          output += "\\draw("+(6*k)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(5,1);\n";
          //output += " \\node[align=center] at ("+(6*k+2)+","+(21*(mitmSolution.Rounds-round)+13)+") {\\textbf{\\Large z="+k+"}};\n";
          for (int i = 0; i < 5; i++)
          {
              //DC1
              if (mitmSolution.DC1[round][i][k] == 1) {
                consumered ++;
                output += "\\draw[line width=2pt, color=red]("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(1,1);\n";
              }
          }
        }

        output += "\\fill[color="+RedColor+"] ("+(6*63+4+4)+","+(25*(mitmSolution.Rounds-round)+15)+")  rectangle ++(1,1);\n";
        output += "\\draw("+(6*63+4+4)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(1,1);\n";
        output += " \\node[align=center] at ("+(6*63+4+2.5)+","+(25*(mitmSolution.Rounds-round)+15.4)+") {\\textbf{\\huge - "+(consumered)+"}};\n";

   
       
       output += " \\node[align=center] at ("+(-2)+","+(25*(mitmSolution.Rounds-round)+13.5)+")[scale=1.5] {\\textbf{\\huge $D^{("+(round+1)+")}$}};\n";

       consumered = 0;
       for (int k = 0; k < 64; k++) 
       {       
          for (int i = 0; i < 5; i++)
           {
              if (mitmSolution.DP2[round][i][k][0] == 0 & mitmSolution.DP2[round][i][k][1] == 0 & mitmSolution.DP2[round][i][k][2] == 0)
                output += "\\fill[color="+WhiteColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+13)+") rectangle ++(1,1);\n";
              if (mitmSolution.DP2[round][i][k][0] == 0 & mitmSolution.DP2[round][i][k][1] == 1 & mitmSolution.DP2[round][i][k][2] == 1)
                output += "\\fill[color="+RedColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+13)+") rectangle ++(1,1);\n";
              if (mitmSolution.DP2[round][i][k][0] == 1 & mitmSolution.DP2[round][i][k][1] == 1 & mitmSolution.DP2[round][i][k][2] == 0)
                output += "\\fill[color="+BlueColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+13)+") rectangle ++(1,1);\n";
              if (mitmSolution.DP2[round][i][k][0] == 0 & mitmSolution.DP2[round][i][k][1] == 1 & mitmSolution.DP2[round][i][k][2] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+13)+") rectangle ++(1,1);\n";
              if (mitmSolution.DP2[round][i][k][0] == 1 & mitmSolution.DP2[round][i][k][1] == 1 & mitmSolution.DP2[round][i][k][2] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+13)+") rectangle ++(1,1);\n";
            }
          output += "\\draw("+(6*k)+","+(25*(mitmSolution.Rounds-round)+13)+") grid ++(5,1);\n";
          //output += " \\node[align=center] at ("+(6*k+2)+","+(21*(mitmSolution.Rounds-round)+13)+") {\\textbf{\\Large z="+k+"}};\n";
          for (int i = 0; i < 5; i++)
           {
              //DC12
              if (mitmSolution.DC12[round][i][k] == 1){
                  consumered ++;
                  output += "\\draw[line width=2pt, color=red]("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+13)+") grid ++(1,1);\n";
              }

            }
        }
           output += "\\fill[color="+RedColor+"] ("+(6*63+4+4)+","+(25*(mitmSolution.Rounds-round)+13)+")  rectangle ++(1,1);\n";
           output += "\\draw("+(6*63+4+4)+","+(25*(mitmSolution.Rounds-round)+13)+") grid ++(1,1);\n";
           output += " \\node[align=center] at ("+(6*63+4+2.5)+","+(25*(mitmSolution.Rounds-round)+13.4)+") {\\textbf{\\huge - "+(consumered)+"}};\n";
      }
      // DB after theta
      if ( round != mitmSolution.Rounds) {
        output += " \\node[align=center] at ("+(-2)+","+(25*(mitmSolution.Rounds-round)+9.5)+")[scale=1.5] {\\textbf{\\huge ${\\Huge \\theta}^{("+(round+1)+")}$}};\n";
        for (int k = 0; k < 64; k++) 
        {       
          for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) 
            {
              if (mitmSolution.DB[round][i][j][k][0] == 0 & mitmSolution.DB[round][i][j][k][1] == 0 & mitmSolution.DB[round][i][j][k][2] == 0)
                output += "\\fill[color="+WhiteColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.DB[round][i][j][k][0] == 0 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 1)
                output += "\\fill[color="+RedColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.DB[round][i][j][k][0] == 1 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 0)
                output += "\\fill[color="+BlueColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.DB[round][i][j][k][0] == 0 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.DB[round][i][j][k][0] == 1 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+7)+") rectangle ++(1,1);\n";
            }
        }
        for (int k = 0; k < 64; k++) {
          output += "\\draw("+(6*k)+","+(25*(mitmSolution.Rounds-round)+7)+") grid ++(5,5);\n";
          //output += " \\node[align=center] at ("+(6*k+2)+","+(25*(mitmSolution.Rounds-round)+6)+") {\\textbf{\\Large z="+k+"}};\n";
        }
        int consumered = 0;
        for (int k = 0; k < 64; k++) 
        {       
          for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) 
            {
              //DC2
              if (mitmSolution.DC2[round][i][j][k] == 1) {
                consumered ++;
                output += "\\draw[line width=2pt, color=red]("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j)+7)+") grid ++(1,1);\n"; 
              }
            }
        }
        output += "\\fill[color="+RedColor+"] ("+(6*63+4+4)+","+(25*(mitmSolution.Rounds-round)+9)+")  rectangle ++(1,1);\n";
        output += "\\draw("+(6*63+4+4)+","+(25*(mitmSolution.Rounds-round)+9)+")  grid ++(1,1);\n";
        output += " \\node[align=center] at ("+(6*63+4+2.5)+","+(25*(mitmSolution.Rounds-round)+9.4)+") {\\textbf{\\huge - "+(consumered)+"}};\n";
      }

      // DB
      if ( round != mitmSolution.Rounds) {
        output += " \\node[align=center] at ("+(-2)+","+(25*(mitmSolution.Rounds-round)+2.5)+")[scale=1.5] {\\textbf{\\huge $\\pi^{("+(round+1)+")}$}};\n";
        for (int k = 0; k < 64; k++) 
        {       
          for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) 
            {
              if (mitmSolution.DB[round][i][j][k][0] == 0 & mitmSolution.DB[round][i][j][k][1] == 0 & mitmSolution.DB[round][i][j][k][2] == 0)
                output += "\\fill[color="+WhiteColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.DB[round][i][j][k][0] == 0 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 1)
                output += "\\fill[color="+RedColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.DB[round][i][j][k][0] == 1 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 0)
                output += "\\fill[color="+BlueColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.DB[round][i][j][k][0] == 0 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.DB[round][i][j][k][0] == 1 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
            }
          output += "\\draw("+(6*k)+","+(25*(mitmSolution.Rounds-round))+") grid ++(5,5);\n";
          //output += " \\node[align=center] at ("+(6*k+2)+","+(25*(mitmSolution.Rounds-round)-1)+") {\\textbf{\\Large z="+k+"}};\n";
        }
      }
      
      /*for (int k = 0; k < 64; k++) 
      {
	if (mitmSolution.dom[0][k]==1)
        {
	  output += "\\node[align=center] at ("+(6*((k-rho[3][0]+64)%64)+3+0.5)+","+(18+4+0.5)+") {\\textbf{\\Large $m$}};\n";
          output += "\\node[align=center] at ("+(6*((k-rho[3][0]+64)%64)+3+0.5)+","+(18+1+0.5)+") {\\textbf{\\Large $m$}};\n";
          output += "\\node[align=center] at ("+(6*((k-rho[0][2]+64)%64)+0+0.5)+","+(18+2+0.5)+") {\\textbf{\\Large $m$}};\n";
          output += "\\node[align=center] at ("+(6*((k-rho[0][2]+64)%64)+0+0.5)+","+(18+4+0.5)+") {\\textbf{\\Large $m$}};\n";
        }
        if (mitmSolution.dom[1][k]==1)
        {
	  output += "\\node[align=center] at ("+(6*((k-rho[4][1]+64)%64)+4+0.5)+","+(18+3+0.5)+") {\\textbf{\\Large $m$}};\n";
          output += "\\node[align=center] at ("+(6*((k-rho[4][1]+64)%64)+4+0.5)+","+(18+0+0.5)+") {\\textbf{\\Large $m$}};\n";
          output += "\\node[align=center] at ("+(6*((k-rho[1][3]+64)%64)+1+0.5)+","+(18+1+0.5)+") {\\textbf{\\Large $m$}};\n";
          output += "\\node[align=center] at ("+(6*((k-rho[1][3]+64)%64)+1+0.5)+","+(18+3+0.5)+") {\\textbf{\\Large $m$}};\n";
	  //output += "\\draw[line width=2pt, color=green]("+(6*((k-rho[4][1]+64)%64)+4)+","+(18+3)+") grid ++(1,1);\n";
          //output += "\\draw[line width=2pt, color=green]("+(6*((k-rho[4][1]+64)%64)+4)+","+(18+0)+") grid ++(1,1);\n";
          //output += "\\draw[line width=2pt, color=green]("+(6*((k-rho[1][3]+64)%64)+1)+","+(18+1)+") grid ++(1,1);\n";
          //output += "\\draw[line width=2pt, color=green]("+(6*((k-rho[1][3]+64)%64)+1)+","+(18+3)+") grid ++(1,1);\n";
        }
      }
      output += "\n";*/

    }
    
    int round = -1;
    if (round == -1) {
      
      output += " \\node[align=center] at ("+(-2)+","+(25*(mitmSolution.Rounds-round)+20.5)+") [scale=1.5]{\\textbf{\\huge $A^{("+(round+1)+")}$}};\n";
      
        for (int k = 0; k < 64; k++) 
        {       
          for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) 
            {
              if (mitmSolution.Pi_init[i][j][k][0] == 0 & mitmSolution.Pi_init[i][j][k][1] == 0 & mitmSolution.Pi_init[i][j][k][2] == 0)
                output += "\\fill[color="+WhiteColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+18)+") rectangle ++(1,1);\n";
              if (mitmSolution.Pi_init[i][j][k][0] == 0 & mitmSolution.Pi_init[i][j][k][1] == 1 & mitmSolution.Pi_init[i][j][k][2] == 1)
                output += "\\fill[color="+RedColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+18)+") rectangle ++(1,1);\n";
              if (mitmSolution.Pi_init[i][j][k][0] == 1 & mitmSolution.Pi_init[i][j][k][1] == 1 & mitmSolution.Pi_init[i][j][k][2] == 0)
                output += "\\fill[color="+BlueColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+18)+") rectangle ++(1,1);\n";
              if (mitmSolution.Pi_init[i][j][k][0] == 0 & mitmSolution.Pi_init[i][j][k][1] == 1 & mitmSolution.Pi_init[i][j][k][2] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+18)+") rectangle ++(1,1);\n";
              if (mitmSolution.Pi_init[i][j][k][0] == 1 & mitmSolution.Pi_init[i][j][k][1] == 1 & mitmSolution.Pi_init[i][j][k][2] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+18)+") rectangle ++(1,1);\n";
            }
        }
        for (int k = 0; k < 64; k++) 
        { 
          output += "\\draw("+(6*k)+","+(25*(mitmSolution.Rounds-round)+18)+") grid ++(5,5);\n";
          output += " \\node[align=center] at ("+(6*k+2)+","+(25*(mitmSolution.Rounds-round)+17)+")[scale=2] {{\\Large z="+k+"}};\n";
        }
       int allred = 0;
       int thirdred = 0;
       int allblue = 0;
       int thirdblue = 0;
       output += " \\node[align=center] at ("+(-2)+","+(25*(mitmSolution.Rounds-round)+15.5)+")[scale=1.5] {\\textbf{\\huge $C^{("+(round+1)+")}$}};\n";
       for (int k = 0; k < 64; k++) 
       {       
          for (int i = 0; i < 5; i++)
           {
               output += "\\fill[color="+GrayColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+15)+") rectangle ++(1,1);\n";
            }
          output += "\\draw("+(6*k)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(5,1);\n";
       }
       for (int k = 0; k < 64; k++) 
       {       
          //for (int i = 0; i < 5; i++)
               //output += "\\fill[color="+GrayColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+15)+") rectangle ++(1,1);\n";
            
          //output += "\\draw("+(6*k)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(5,1);\n";
          //output += " \\node[align=center] at ("+(6*k+2)+","+(21*(mitmSolution.Rounds-round)+13)+") {\\textbf{\\Large z="+k+"}};\n";
          
          //DC1
          if (mitmSolution.Pi_init[0][0][k][0] == 0 & mitmSolution.Pi_init[0][0][k][1] == 1 & mitmSolution.Pi_init[0][0][k][2] == 1) {
            allred ++;
            thirdred ++;
            output += "\\draw[line width=2pt, color=red]("+(6*k)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(1,1);\n";
          }
          if (mitmSolution.Pi_init[0][0][k][0] == 1 & mitmSolution.Pi_init[0][0][k][1] == 1 & mitmSolution.Pi_init[0][0][k][2] == 0) {
                allblue ++;
                thirdblue ++;
                output += "\\draw[line width=2pt, color=blue]("+(6*k)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(1,1);\n";
              }

           if (mitmSolution.Pi_init[0][1][k][0] == 0 & mitmSolution.Pi_init[0][1][k][1] == 1 & mitmSolution.Pi_init[0][1][k][2] == 1) {
               allred ++;
               output += "\\draw[line width=2pt, color=red]("+(6*((k-rho[3][0]+64)%64)+3)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(1,1);\n";
           }
           if (mitmSolution.Pi_init[0][1][k][0] == 1 & mitmSolution.Pi_init[0][1][k][1] == 1 & mitmSolution.Pi_init[0][1][k][2] == 0) {
               allblue ++;
               output += "\\draw[line width=2pt, color=blue]("+(6*((k-rho[3][0]+64)%64)+3)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(1,1);\n";
           }

           if (mitmSolution.Pi_init[0][2][k][0] == 0 & mitmSolution.Pi_init[0][2][k][1] == 1 & mitmSolution.Pi_init[0][2][k][2] == 1) {
               allred ++;
               thirdred ++;
               output += "\\draw[line width=2pt, color=red]("+(6*((k-rho[1][0]+64)%64)+1)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(1,1);\n";
           }
           if (mitmSolution.Pi_init[0][2][k][0] == 1 & mitmSolution.Pi_init[0][2][k][1] == 1 & mitmSolution.Pi_init[0][2][k][2] == 0) {
               allblue ++;
               thirdblue ++;
               output += "\\draw[line width=2pt, color=blue]("+(6*((k-rho[1][0]+64)%64)+1)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(1,1);\n";
           }

           if (mitmSolution.Pi_init[0][3][k][0] == 0 & mitmSolution.Pi_init[0][3][k][1] == 1 & mitmSolution.Pi_init[0][3][k][2] == 1) {
               allred ++;
               output += "\\draw[line width=2pt, color=red]("+(6*((k-rho[4][0]+64)%64)+4)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(1,1);\n";
           }
           if (mitmSolution.Pi_init[0][3][k][0] == 1 & mitmSolution.Pi_init[0][3][k][1] == 1 & mitmSolution.Pi_init[0][3][k][2] == 0) {
               allblue ++;
               output += "\\draw[line width=2pt, color=blue]("+(6*((k-rho[4][0]+64)%64)+4)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(1,1);\n";
           }

           if (mitmSolution.Pi_init[0][4][k][0] == 0 & mitmSolution.Pi_init[0][4][k][1] == 1 & mitmSolution.Pi_init[0][4][k][2] == 1) {
               allred ++;
               thirdred ++;
               output += "\\draw[line width=2pt, color=red]("+(6*((k-rho[2][0]+64)%64)+2)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(1,1);\n";
           }
           if (mitmSolution.Pi_init[0][4][k][0] == 1 & mitmSolution.Pi_init[0][4][k][1] == 1 & mitmSolution.Pi_init[0][4][k][2] == 0) {
               allblue ++;
               thirdblue ++;
               output += "\\draw[line width=2pt, color=blue]("+(6*((k-rho[2][0]+64)%64)+2)+","+(25*(mitmSolution.Rounds-round)+15)+") grid ++(1,1);\n";
           }

            
        }

      output += "\\fill[color="+RedColor+"] ("+(6*63+4+2)+","+(25*(mitmSolution.Rounds-round)+21)+")  rectangle ++(1,1);\n";
      output += "\\draw("+(6*63+4+2)+","+(25*(mitmSolution.Rounds-round)+21)+")  grid ++(1,1);\n";
      output += " \\node[align=center] at ("+(6*63+4+4.2)+","+(25*(mitmSolution.Rounds-round)+21.4)+") {\\textbf{\\huge = "+(allred*2+thirdred)+"}};\n";
      output += "\\fill[color="+BlueColor+"] ("+(6*63+4+2)+","+(25*(mitmSolution.Rounds-round)+19)+")  rectangle ++(1,1);\n";
      output += "\\draw("+(6*63+4+2)+","+(25*(mitmSolution.Rounds-round)+19)+") grid ++(1,1);\n";
      output += " \\node[align=center] at ("+(6*63+4+4.1)+","+(25*(mitmSolution.Rounds-round)+19.4)+") {\\textbf{\\huge = "+(allblue*2+thirdblue)+"}};\n";
      output += " \\node[align=center] at ("+(-2)+","+(25*(mitmSolution.Rounds-round)+13.5)+") [scale=1.5] {\\textbf{\\huge $D^{("+(round+1)+")}$}};\n";
       for (int k = 0; k < 64; k++) 
       {       
          for (int i = 0; i < 5; i++)
           {
                output += "\\fill[color="+GrayColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+13)+") rectangle ++(1,1);\n";
            }
          output += "\\draw("+(6*k)+","+(25*(mitmSolution.Rounds-round)+13)+") grid ++(5,1);\n";
        }
      
      output += "\\fill[color="+RedColor+"] ("+(6*63+4+4)+","+(25*(mitmSolution.Rounds-round)+16)+")  rectangle ++(1,1);\n";
      output += "\\draw("+(6*63+4+4)+","+(25*(mitmSolution.Rounds-round)+16)+")  grid ++(1,1);\n";
      output += " \\node[align=center] at ("+(6*63+4+2.5)+","+(25*(mitmSolution.Rounds-round)+16.4)+") {\\textbf{\\huge - "+(allred)+"}};\n";
      output += "\\fill[color="+BlueColor+"] ("+(6*63+4+4)+","+(25*(mitmSolution.Rounds-round)+14.5)+")  rectangle ++(1,1);\n";
      output += "\\draw("+(6*63+4+4)+","+(25*(mitmSolution.Rounds-round)+14.5)+") grid ++(1,1);\n";
      output += " \\node[align=center] at ("+(6*63+4+2.7)+","+(25*(mitmSolution.Rounds-round)+14.9)+") {\\textbf{\\huge - "+(allblue)+"}};\n";


      // DB after theta
        
        output += " \\node[align=center] at ("+(-2)+","+(25*(mitmSolution.Rounds-round)+9.5)+")[scale=1.5] {{\\huge $\\theta^{("+(round+1)+")}$}};\n";
        for (int k = 0; k < 64; k++) 
        {       
          for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) 
            {
              if (mitmSolution.Pi_init[i][j][k][0] == 0 & mitmSolution.Pi_init[i][j][k][1] == 0 & mitmSolution.Pi_init[i][j][k][2] == 0)
                output += "\\fill[color="+WhiteColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.Pi_init[i][j][k][0] == 0 & mitmSolution.Pi_init[i][j][k][1] == 1 & mitmSolution.Pi_init[i][j][k][2] == 1)
                output += "\\fill[color="+RedColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.Pi_init[i][j][k][0] == 1 & mitmSolution.Pi_init[i][j][k][1] == 1 & mitmSolution.Pi_init[i][j][k][2] == 0)
                output += "\\fill[color="+BlueColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.Pi_init[i][j][k][0] == 0 & mitmSolution.Pi_init[i][j][k][1] == 1 & mitmSolution.Pi_init[i][j][k][2] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.Pi_init[i][j][k][0] == 1 & mitmSolution.Pi_init[i][j][k][1] == 1 & mitmSolution.Pi_init[i][j][k][2] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(6*((k-rho[(i+3*j)%5][i]+64)%64)+((i+3*j)%5))+","+(25*(mitmSolution.Rounds-round)+(4-i)+7)+") rectangle ++(1,1);\n";
            }

          /*for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) 
            {
              if (mitmSolution.DA[round+1][i][j][k][0] == 0 & mitmSolution.DA[round+1][i][j][k][1] == 1 & mitmSolution.DA[round+1][i][j][k][2] == 1) {
                output += "\\node[align=center] at ("+(6*((k-rho[((i+1)%5+3*j)%5][(i+1)%5]+64)%64)+(((i+1)%5+3*j)%5)+0.5)+","+(25*(mitmSolution.Rounds-round)+(4-(i+1)%5)+7+0.5)+") {\\textbf{\\Large 0}};\n";
		output += "\\node[align=center] at ("+(6*((k-rho[((i+4)%5+3*j)%5][(i+4)%5]+64)%64)+(((i+4)%5+3*j)%5)+0.5)+","+(25*(mitmSolution.Rounds-round)+(4-(i+4)%5)+7+0.5)+") {\\textbf{\\Large 1}};\n";
              }
              if (mitmSolution.DA[round+1][i][j][k][0] == 1 & mitmSolution.DA[round+1][i][j][k][1] == 1 & mitmSolution.DA[round+1][i][j][k][2] == 0) {
                output += "\\node[align=center] at ("+(6*((k-rho[((i+1)%5+3*j)%5][(i+1)%5]+64)%64)+(((i+1)%5+3*j)%5)+0.5)+","+(25*(mitmSolution.Rounds-round)+(4-(i+1)%5)+7+0.5)+") {\\textbf{\\Large 0}};\n";
		output += "\\node[align=center] at ("+(6*((k-rho[((i+4)%5+3*j)%5][(i+4)%5]+64)%64)+(((i+4)%5+3*j)%5)+0.5)+","+(25*(mitmSolution.Rounds-round)+(4-(i+4)%5)+7+0.5)+") {\\textbf{\\Large 1}};\n";
              }
            }*/
        }
        for (int k = 0; k < 64; k++) {
          output += "\\draw("+(6*k)+","+(25*(mitmSolution.Rounds-round)+7)+") grid ++(5,5);\n";
          //output += " \\node[align=center] at ("+(6*k+2)+","+(25*(mitmSolution.Rounds-round)+6)+") {\\textbf{\\Large z="+k+"}};\n";
        }
      

      // DB
        output += " \\node[align=center] at ("+(-2)+","+(25*(mitmSolution.Rounds-round)+2.5)+") [scale=1.5]{\\textbf{\\huge $\\pi^{("+(round+1)+")}$}};\n";
        int CCount = 0;
        for (int k = 0; k < 64; k++) 
        {       
          for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) 
            {
              if (mitmSolution.Pi_init[i][j][k][0] == 0 & mitmSolution.Pi_init[i][j][k][1] == 0 & mitmSolution.Pi_init[i][j][k][2] == 0)
                output += "\\fill[color="+WhiteColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.Pi_init[i][j][k][0] == 0 & mitmSolution.Pi_init[i][j][k][1] == 1 & mitmSolution.Pi_init[i][j][k][2] == 1)
                output += "\\fill[color="+RedColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.Pi_init[i][j][k][0] == 1 & mitmSolution.Pi_init[i][j][k][1] == 1 & mitmSolution.Pi_init[i][j][k][2] == 0)
                output += "\\fill[color="+BlueColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.Pi_init[i][j][k][0] == 0 & mitmSolution.Pi_init[i][j][k][1] == 1 & mitmSolution.Pi_init[i][j][k][2] == 0)
                output += "\\fill[color="+PurpleColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.Pi_init[i][j][k][0] == 1 & mitmSolution.Pi_init[i][j][k][1] == 1 & mitmSolution.Pi_init[i][j][k][2] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(6*k+i)+","+(25*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
            }
          output += "\\draw("+(6*k)+","+(25*(mitmSolution.Rounds-round))+") grid ++(5,5);\n";
          //output += " \\node[align=center] at ("+(6*k+2)+","+(25*(mitmSolution.Rounds-round)-1)+") {\\textbf{\\Large z="+k+"}};\n";

          /*for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) 
            { 
              if (mitmSolution.DA[round+1][i][j][k][0] == 0 & mitmSolution.DA[round+1][i][j][k][1] == 1 & mitmSolution.DA[round+1][i][j][k][2] == 1) {
                output += "\\node[align=center] at ("+(6*k+(i+1)%5+0.5)+","+(25*(mitmSolution.Rounds-round)+(4-j)+0.5)+") {\\textbf{\\Large 0}};\n";
                output += "\\node[align=center] at ("+(6*k+(i+4)%5+0.5)+","+(25*(mitmSolution.Rounds-round)+(4-j)+0.5)+") {\\textbf{\\Large 1}};\n";
              }
              if (mitmSolution.DA[round+1][i][j][k][0] == 1 & mitmSolution.DA[round+1][i][j][k][1] == 1 & mitmSolution.DA[round+1][i][j][k][2] == 0) {
                output += "\\node[align=center] at ("+(6*k+(i+1)%5+0.5)+","+(25*(mitmSolution.Rounds-round)+(4-j)+0.5)+") {\\textbf{\\Large 0}};\n";
                output += "\\node[align=center] at ("+(6*k+(i+4)%5+0.5)+","+(25*(mitmSolution.Rounds-round)+(4-j)+0.5)+") {\\textbf{\\Large 1}};\n";
              }
            }*/
            for (int i = 0; i < 5; i++){
                int []tmpCond;
                tmpCond = getCond(mitmSolution.Pi_init[0][i][k][0],mitmSolution.Pi_init[0][i][k][1],mitmSolution.Pi_init[0][i][k][2],mitmSolution.Pi_init[1][i][k][0],mitmSolution.Pi_init[1][i][k][1],mitmSolution.Pi_init[1][i][k][2],mitmSolution.Pi_init[2][i][k][0],mitmSolution.Pi_init[2][i][k][1],mitmSolution.Pi_init[2][i][k][2],
                              mitmSolution.DA[0][0][i][k][0],mitmSolution.DA[0][0][i][k][1],mitmSolution.DA[0][0][i][k][2],mitmSolution.DA[0][1][i][k][0],mitmSolution.DA[0][1][i][k][1],mitmSolution.DA[0][1][i][k][2],mitmSolution.DA[0][4][i][k][0],mitmSolution.DA[0][4][i][k][1],mitmSolution.DA[0][4][i][k][2]);
/*
		if (k==29){
			System.out.print("tmpcond:");
			System.out.print(tmpCond[0]);
System.out.print(tmpCond[1]);
System.out.print(tmpCond[2]);
System.out.print(tmpCond[3]);
			System.out.print("\n");		
		}*/
                for (int j = 0; j < 4; j++){
                    if (tmpCond[j] == 0) {
                        CCount++;
                        //Pi
                        output += "\\node[align=center] at (" + (6 * k + j % 5 + 0.5) + "," + (25 * (mitmSolution.Rounds - round) + (4 - i) + 0.5) + ") {\\textbf{\\Large 0}};\n";
                        //theta
                        output += "\\node[align=center] at ("+(6*((k-rho[(j%5+3*i)%5][j%5]+64)%64)+((j%5+3*i)%5)+0.5)+","+(25*(mitmSolution.Rounds-round)+(4-j%5)+7+0.5)+") {\\textbf{\\Large 0}};\n";
                    }
                    if (tmpCond[j] == 1){
                        CCount++;
                        output += "\\node[align=center] at ("+(6*k+j%5+0.5)+","+(25*(mitmSolution.Rounds-round)+(4-i)+0.5)+") {\\textbf{\\Large 1}};\n";
                        output += "\\node[align=center] at ("+(6*((k-rho[(j%5+3*i)%5][j%5]+64)%64)+((j%5+3*i)%5)+0.5)+","+(25*(mitmSolution.Rounds-round)+(4-j%5)+7+0.5)+") {\\textbf{\\Large 1}};\n";
                    }

                }
                if (!isGray(mitmSolution.Pi_init[0][i][k][0],mitmSolution.Pi_init[0][i][k][1],mitmSolution.Pi_init[0][i][k][2]) & isGray( mitmSolution.DA[0][3][i][k][0],mitmSolution.DA[0][3][i][k][1],mitmSolution.DA[0][3][i][k][2])){
                    CCount++;
                    output += "\\node[align=center] at ("+(6*k+4%5+0.5)+","+(25*(mitmSolution.Rounds-round)+(4-i)+0.5)+") {\\textbf{\\Large 1}};\n";
                    output += "\\node[align=center] at ("+(6*((k-rho[(4%5+3*i)%5][4%5]+64)%64)+((4%5+3*i)%5)+0.5)+","+(25*(mitmSolution.Rounds-round)+(4-4%5)+7+0.5)+") {\\textbf{\\Large 1}};\n";
                }

            }
        }
        System.out.print("Const count: ");
        System.out.print(CCount);
        System.out.print("\n");
        output += " \\node[align=center] at ("+(6*63+4+3.5)+","+(25*(mitmSolution.Rounds-round)+3)+") {\\textbf{\\huge C = "+(CCount)+"}};\n";

    }



    // Footer
    output += "\\makeatother\n";
    output += "\\end{tikzpicture}\n";
    output += "\\end{document}\n";
    return output;
  }

  public static int[] getCond(int ... vars){
        int in = 0;
        int out = 0;
        in = getIndex(vars[0],vars[1],vars[2],vars[3],vars[4],vars[5],vars[6],vars[7],vars[8]);
        out = getIndex(vars[9],vars[10],vars[11],vars[12],vars[13],vars[14],vars[15],vars[16],vars[17]);
        /*
	    System.out.print(cond[in][out]);
        if (vars[18]==29){
        System.out.print("in-out:");
        System.out.print(in);
        System.out.print(out);
        System.out.print("\n");
        } */
        return cond[in][out];

    }

  public static int getIndex(int... vars){
      int result = 0;
      if (!isGray(vars[0],vars[1],vars[2])){
          result = result + 4;
      }
      if (!isGray(vars[3],vars[4],vars[5])){
          result = result + 2;
      }
      if (!isGray(vars[6],vars[7],vars[8])){
          result = result + 1;
      }
      return result;
  }
  public static boolean isGray(int... vars){
      return vars[0] == 1 & vars[1] == 1 & vars[2] == 1;
  }
}
