- `mitm/`: the codes to generate the MILP model for the MITM preimage attack on keccak-384 and search for solution. To compile, use the command `make`. To run, use the command `java -jar mitmsearch.jar -v -r=2 -so=output/result.json`
- `solutiontotikz/`: the codes to generate tikz picture of the searching result. To compile, use the command `make tikz`. To run, use the command `java -jar solutiontotikz.jar -si=output/result.json`