all: jar

jar: mitmsearch
	jar cfm mitmsearch.jar META-INF/MitmManifest.txt mitmsearch/

tikz: solutiontotikz
	jar cfm solutiontotikz.jar META-INF/solutionToTikzManifest.txt mitmsearch/


mitm:
	javac -cp ../newlibs/*:. -d . mitm/*.java

solutiontotikz: mitm
	javac -cp ../newlibs/*:. -d . solutiontotikz/*.java

mitmsearch: mitm solutiontotikz
	javac -cp ../newlibs/*:. -d . *.java

clean:
	rm -rf mitmsearch
	rm -f mitmsearch.jar
	rm -f solutiontotikz.jar
	rm -f model.mps

.PHONY: mitm mitmsearch
