# Phylogeny
UPGMA Phylogeny Program With Optional Parsimony Capabilities
# About
  This program utilizes a UPGMA (unweighted pair group method with arithmetic mean) clustering method to generate a phylogenetic tree.
	The user can specify if they want the trait states of each ancestor node of the tree to be determined as well as the parsimony value of the tree.
	The goal of this program is to generate the most parsiminious phylogenetic tree while utilizing heuristics to reduce computation time.
  
  In simpler terms, an evolutionary tree is constructed from a user provided list of species and their associated traits.
  These traits can only be either absent(0) or present(1).
  The evolutionary tree constructed is simply a hypothesis of how evolution may have occurred based off of trait presence.
  The tree generated is the tree with the least number of evolutionary steps (aka mutations) between species and their ancestors.
  In other words, the tree generated is the tree with maximum parsimony.
  
  # How To Run
	1. Create an inputFile.txt
		a. Format for input file is as follows
			(integer for number of species) (integer for number of traits in all species)
			(Species 1 String Name) 0 1 0 1 0 1 
			(Species 2 String Name) 1 0 1 0 1 0
			...
			(Species n String name) 1 1 1 1 1 1
		b. Possible common errors creating the input file:
			-Having a space after integer on first line
			-Species not all having the same number of traits
			-Not enough species are present (less than specified amount of species on first line of input file)
			-Too many species are present(more than specified amount of species on first line of input file)
	2. Run the MainMethod CLI(Command line interface)
		a. Open CMD/command prompt
		b. Change directory to the phylogeny folder
		c. Use the command "java -jar phylogeneticTreeCLI.jar <inputFileName.txt>"
		d. Follow prompts on the command line.
		e. If there are a large number of traits/species, then it is recommended to just perform UPGMA tree creation and not to show parsimony.
			This only occurs with large number of species and/or traits.
	3. Access the outputFile.txt
		a. Output file location will be shown on the last line of the CMI. You can access your file here.
			-Default location is the Phylogeny folder
  
