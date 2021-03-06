# UPGMA Phylogenetic Tree Generator With Optional Parsimony Capabilities
# About
  This program utilizes a UPGMA (unweighted pair group method with arithmetic mean) clustering method to generate a phylogenetic tree.
  The user can specify if they want the trait states of each ancestor node of the tree to be determined as well as the parsimony value of the tree.
  The goal of this program is to generate the most parsiminious phylogenetic tree while utilizing heuristics to reduce computation time.
  
  In simpler terms, an evolutionary tree is constructed from a user provided list of species and their associated traits.
  These traits can only be either absent(0) or present(1).
  The evolutionary tree constructed is simply a hypothesis of how evolution may have occurred based off of trait presence.
  The tree generated is the tree with the least number of evolutionary steps (aka mutations) between species and their ancestors.
  In other words, the tree generated is the tree with maximum parsimony.
  
  To learn more about UPGMA visit https://en.wikipedia.org/wiki/UPGMA
  
  To learn more about parsimony visit https://en.wikipedia.org/wiki/Maximum_parsimony_(phylogenetics)
  
  To learn more about phylogenetic trees visit https://www.nature.com/scitable/topicpage/reading-a-phylogenetic-tree-the-meaning-of-41956/
  # How to Generate a Phylogenetic Tree
	1. Create an inputFile.txt
		a. Format for input file is as follows
			(integer for number of species) (integer for number of traits being compared (per species))
			(Species 1 String Name) 0 1 0 1 0 1 
			(Species 2 String Name) 1 0 1 0 1 0
			...
			(Species n String name) 1 1 1 1 1 1
		b. Possible common errors creating the input file:
			-Having a space after integer on first line
			-Species not all having the same number of traits
			-Not enough species are present (less than specified amount of species on first line of input file)
			-Too many species are present(more than specified amount of species on first line of input file)
	2. Run the programs CLI(Command line interface)
		a. Open your computers CMD/command prompt
		b. Change your working directory to the a folder containing the -jar file
		c. Use the following command: "java -jar phylogeneticTreeCLI.jar inputFileName.txt"
		d. Follow prompts on the command line interface until the CLI displays that it has completed its work.

		Please note: This program utilizes Java Version 11 and requires you to have Java Version 11 or a more recent version installed on your computer.
		Also note that the program can take a very long time if you enter in a large amount of species with a large amount of traits.
		It is suggested that complex trees only utilize the UPGMA tree generation of this program and elect not to show parsimony.
		If you choose to display parsimony, please wait for the program to finish or your output.txt will not be created.
	3. Access the outputFile.txt
		a. Access the outputFile.txt to see your generated phylogenetic tree.
			-File location will be shown on last line of the CLI, default location is the folder/directory you launched the jar from.
