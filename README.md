# Phylogeny
UPGMA Phylogeny Program With Optional Parsimony Capabilities


  This program utilizes a UPGMA (unweighted pair group method with arithmetic mean) clustering method to generate a phylogenetic tree.
	The user can specify if they want the trait states of each ancestor node of the tree to be determined as well as the parsimony value of the tree.
	The goal of this program is to generate the most parsiminious phylogenetic tree while utilizing heuristics to reduce computation time.
  
  In simpler terms, an evolutionary tree is constructed from a user provided list of species and their associated traits.
  These traits can only be either absent(0) or present(1).
  The evolutionary tree constructed is simply a hypothesis of how evolution may have occurred based off of trait presence.
  The tree generated is the tree with the least number of evolutionary steps (aka mutations) between species and their ancestors.
  In other words, the tree generated is the tree with maximum parsimony.
  
