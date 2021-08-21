
package phylogenetics;

///////////////////////// TOP OF FILE COMMENT BLOCK ////////////////////////////
//
//Title: UPGMA based Phylogenetic Tree Creator
//
//Author: Krischan Woehrer
//Email: kwoehrer@wisc.edu
//
///////////////////////////////// NOTES ////////////////////////////////////////
//
//Reasons for creating this program:
//1. I remember taking microbiology-470 at UW-Madison and one of our big
//projects for the semester was to create the most parsiminious
//phylogenetic tree of 20-25 randomly named species when we were only given
//the presence of various traits (I remember it being close to 10-12).
//Everyone in the course spent hours upon hours manually crafting their
//trees and I remember thinking to myself how nice it would be if a
//computer could help compute the optimal state for parsimony.
//Unfortunately, almost all the programs found online either costed money
//or their algorithms were faulty and yielded trees that were close to
//the most parsiminious tree but not quite there. Now that I am pursuing
//a career in computer science and developement, I have decided to solve
//this problem on my own and offer it as a free resource online.
//2. Gain a greater understanding of binary trees and their algorithms
//3. Practice my skills learned from my coursework in computer science.
//Things to improve/TODO:
//1. Numerous points in the program could be better optimized to run a
//little bit faster. These are noted locally when they occur.
//2. When determining the most parsiminious trait states, I currently use
//a brute-force approach. Although I narrow down the number of computations
//by determining traits of ancestors we know for certain, I believe there
//are additional ways I can cut down on the number of computations and speed
//the program up.
//A brute-force approach will still be required in order construct
//the most parsiminious phylogenetic tree accurately.
//
//
///////////////////////////////// CITATIONS ////////////////////////////////////
//
//Source or Recipient; Description
//UW-Madison Microbiology-470
//This class taught me how to do UPGMA based phylogeny as part of my course work.
//
/////////////////////////////// 80 COLUMNS WIDE /////////////////////////////////**

public class Parsimony {
    
    private static int[] indexOfMinimumParsimonyTree;
    private static int[] indexOfAncestorTracker;
    private static int minParsimonyScore;
    
    /**
     * This method takes a rooted tree with ancestor nodes that have an undetermined trait state
     * and determines the trait states that will result in minimum parsimony of the tree.
     * @param tree
     * @return
     */
    public static int findMostParsimoniousTree(RootedTree tree){
        int numOfAncestors = tree.size() - tree.getInitialSpecies().size();
        Species[] ancestorNodes = new Species[numOfAncestors];
        indexOfMinimumParsimonyTree = new int[numOfAncestors];
        indexOfAncestorTracker = new int[numOfAncestors];
        minParsimonyScore = Integer.MAX_VALUE;
        
        //Inserts all ancestor nodes into the species array ancestorNodes
        findAncestorNodes(tree.getRoot(), ancestorNodes, 0);
        //Finds optimal trait states of ancestor nodes
        findOptimalTraitStates(numOfAncestors,ancestorNodes);
        //Changes the actual trait array in the species object to its optimal configuration
        changeTreeTraitsToMinPars(ancestorNodes);
            
        return minParsimonyScore;
    }

    /**
     * Adds all ancestor species into an array. Iterates from left to right.
     * Index = 0 root
     * @param species
     * @param speciesArr
     */
    private static int findAncestorNodes(Species species, Species[] speciesArr,int currIndex) {
        //Puts current species into array
        speciesArr[currIndex] = species;
        currIndex++;
        //Gets Descendant species
        Species leftDescendant = species.getDescendants()[0];
        Species rightDescendant = species.getDescendants()[1];
        
        //If descendants arent ancestor nodes then stop, if they are ancestor nodes than add them to array
        if (leftDescendant.getHeight() != 1) {
            currIndex = findAncestorNodes(leftDescendant, speciesArr,currIndex);
        }
        
        if (rightDescendant.getHeight() != 1) {
            currIndex = findAncestorNodes(rightDescendant, speciesArr,currIndex);
        }
        
        return currIndex;
    }
    
    /**
     * This method finds the trait states with the minimum parsimony score and stores the index of these states in the appropriate field
     * Iterates from the bottom right to bottom left, then moves up a height value
     * of one and repeats right to left until top.
     * @param speciesIndex
     * @param speciesArr
     * @return
     */
    private static int findOptimalTraitStates(int speciesIndex, Species[] speciesArr) {
        speciesIndex--;
        
        if(speciesIndex < 0) {
            return 0;
        }
        
        //gets species reference from array, starts at bottom right of binary tree/last index in array passed in
        Species currentSpecies = speciesArr[speciesIndex];
        //want to loop through each species trait values and set the
        for(int i = 0; i < currentSpecies.getPossibleTraitStates().length; i++) {
            currentSpecies.setTraits(currentSpecies.getPossibleTraitStates()[i]);
            indexOfAncestorTracker[speciesIndex] = i;
            //Calculate parsimony and work with the field variables
            if(speciesIndex == 0) {
                int currParsimonyScore = getParsimonyScore(speciesArr);
                //Compares to minimum parsimony score, if smaller we change minimum score and store the index of the trait states
                if(currParsimonyScore <= minParsimonyScore) {
                    minParsimonyScore = currParsimonyScore;
                    indexOfMinimumParsimonyTree = indexOfAncestorTracker.clone();
                }
            }
            //System.out.println(i);
            findOptimalTraitStates(speciesIndex,speciesArr);
        }
        
        return speciesIndex;
    }
    
    /**
     * This method calculates the parsimony score of the entire tree. It works by calculating
     * the parsimony score of each ancestor node to its descendants.
     * @see Species.getParsimonyScoreToDescendants for information on how parsimony is calculated.
     * @param speciesArr
     * @return
     */
    private static int getParsimonyScore(Species[] speciesArr) {
        int parsimonyScoreOfTree = 0;
        
        for(int i = 0; i < speciesArr.length; i++) {
            parsimonyScoreOfTree += speciesArr[i].getParsimonyScoreToDescendants();
        }
        
        return parsimonyScoreOfTree;
    }
    
    /**
     * This method changes the actual trait states of the ancestor nodes to the states determined to have minimum parsimony.
     * @param ancestorNodes
     */
    private static void changeTreeTraitsToMinPars(Species[] ancestorNodes) {
        for(int i = 0; i < ancestorNodes.length; i++) {
            int[] optimalTraits = ancestorNodes[i].getPossibleTraitStates()[indexOfMinimumParsimonyTree[i]];
            ancestorNodes[i].setTraits(optimalTraits);
        }
    }
    
    /**
     * This method is used to get the parsimony score of the tree after findMostParsimoniousTree method
     * has been called somewhere else in the program.
     * @return The parsimony score of the most parsimonious tree.
     */
    public static int getParsimonyScoreOfTree() {
        return minParsimonyScore;
    }
    
}