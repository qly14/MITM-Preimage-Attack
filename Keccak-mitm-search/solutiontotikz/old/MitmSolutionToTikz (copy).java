package mitmsearch.solutiontotikz;

import mitmsearch.mitm.MitmSolution;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MitmSolutionToTikz {
  private final MitmSolution mitmSolution;
  private static final String WhiteColor  = "white";
  private static final String GrayColor  = "lightgray";
  private static final String BlueColor = "blue";
  private static final String RedColor = "red"; 
  private static final String PurpleColor  = "purple";

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
    output += "\\tikzset{base/.style = {draw=black, minimum width=0.02cm, minimum height=0.02cm, align=center, on chain},}\n";
    output += "\\begin{document}\n";
    output += "\\begin{tikzpicture}[scale = 0.45,every node/.style={scale=0.5}]\n";
    output += "\\makeatletter\n";

    for (int round = 0; round <= mitmSolution.Rounds; round++) {
      
      output += " \\node[align=center] at ("+(-2)+","+(14*(mitmSolution.Rounds-round)+8.5)+") {\\textbf{\\Large R"+round+"}};\n";
      if (round == 0) { //DAin
        for (int k = 0; k < 64; k++) 
        {       
          for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) 
            {
              if (mitmSolution.DAin[i][j][k][0] == 0 & mitmSolution.DAin[i][j][k][1] == 0)
                output += "\\fill[color="+WhiteColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.DAin[i][j][k][0] == 0 & mitmSolution.DAin[i][j][k][1] == 1)
                output += "\\fill[color="+RedColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.DAin[i][j][k][0] == 1 & mitmSolution.DAin[i][j][k][1] == 0)
                output += "\\fill[color="+BlueColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.DAin[i][j][k][0] == 1 & mitmSolution.DAin[i][j][k][1] == 1)
                output += "\\fill[color="+GrayColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j)+7)+") rectangle ++(1,1);\n";
            }
          output += "\\draw("+(6*k)+","+(14*(mitmSolution.Rounds-round)+7)+") grid ++(5,5);\n";
          output += " \\node[align=center] at ("+(6*k+2)+","+(14*(mitmSolution.Rounds-round)+6)+") {\\textbf{\\Large z="+k+"}};\n";
        }
      }
      else { //DA
        for (int k = 0; k < 64; k++) 
        {       
          for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) 
            {
              if (mitmSolution.DA[round-1][i][j][k][0] == 0 & mitmSolution.DA[round-1][i][j][k][1] == 0 & mitmSolution.DA[round-1][i][j][k][2] == 0)
                output += "\\fill[color="+WhiteColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.DA[round-1][i][j][k][0] == 0 & mitmSolution.DA[round-1][i][j][k][1] == 0 & mitmSolution.DA[round-1][i][j][k][2] == 1)
                output += "\\fill[color="+RedColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.DA[round-1][i][j][k][0] == 0 & mitmSolution.DA[round-1][i][j][k][1] == 1 & mitmSolution.DA[round-1][i][j][k][2] == 0)
                output += "\\fill[color="+BlueColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.DA[round-1][i][j][k][0] == 0 & mitmSolution.DA[round-1][i][j][k][1] == 1 & mitmSolution.DA[round-1][i][j][k][2] == 1)
                output += "\\fill[color="+PurpleColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j)+7)+") rectangle ++(1,1);\n";
              if (mitmSolution.DA[round-1][i][j][k][0] == 1 & mitmSolution.DA[round-1][i][j][k][1] == 0 & mitmSolution.DA[round-1][i][j][k][2] == 0)
                output += "\\fill[color="+GrayColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j)+7)+") rectangle ++(1,1);\n";
            }
          output += "\\draw("+(6*k)+","+(14*(mitmSolution.Rounds-round)+7)+") grid ++(5,5);\n";
          output += " \\node[align=center] at ("+(6*k+2)+","+(14*(mitmSolution.Rounds-round)+6)+") {\\textbf{\\Large z="+k+"}};\n";
        }
      }
      // DB
      if ( round != mitmSolution.Rounds) {
        for (int k = 0; k < 64; k++) 
        {       
          for (int i = 0; i < 5; i++)
            for (int j = 0; j < 5; j++) 
            {
              if (mitmSolution.DB[round][i][j][k][0] == 0 & mitmSolution.DB[round][i][j][k][1] == 0 & mitmSolution.DB[round][i][j][k][2] == 0)
                output += "\\fill[color="+WhiteColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.DB[round][i][j][k][0] == 0 & mitmSolution.DB[round][i][j][k][1] == 0 & mitmSolution.DB[round][i][j][k][2] == 1)
                output += "\\fill[color="+RedColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.DB[round][i][j][k][0] == 0 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 0)
                output += "\\fill[color="+BlueColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.DB[round][i][j][k][0] == 0 & mitmSolution.DB[round][i][j][k][1] == 1 & mitmSolution.DB[round][i][j][k][2] == 1)
                output += "\\fill[color="+PurpleColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
              if (mitmSolution.DB[round][i][j][k][0] == 1 & mitmSolution.DB[round][i][j][k][1] == 0 & mitmSolution.DB[round][i][j][k][2] == 0)
                output += "\\fill[color="+GrayColor+"] ("+(6*k+i)+","+(14*(mitmSolution.Rounds-round)+(4-j))+") rectangle ++(1,1);\n";
            }
          output += "\\draw("+(6*k)+","+(14*(mitmSolution.Rounds-round))+") grid ++(5,5);\n";
          output += " \\node[align=center] at ("+(6*k+2)+","+(14*(mitmSolution.Rounds-round)-1)+") {\\textbf{\\Large z="+k+"}};\n";
        }
      }
      output += "\n";
    }
    
    



    // Footer
    output += "\\makeatother\n";
    output += "\\end{tikzpicture}\n";
    output += "\\end{document}\n";
    return output;
  }
}
