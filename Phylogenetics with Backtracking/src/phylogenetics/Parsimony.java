
package phylogenetics;

import java.util.ArrayList;

///////////////////////// TOP OF FILE COMMENT BLOCK ////////////////////////////
//
// Title: UPGMA based Phylogenetic Tree Creator
//
// Author: Krischan Woehrer
// Email: kwoehrer@wisc.edu
//
///////////////////////////////// NOTES ////////////////////////////////////////
//
//
///////////////////////////////// CITATIONS ////////////////////////////////////
//
// Source or Recipient; Description
// UW-Madison Microbiology-470
// This class taught me how to do UPGMA based phylogeny as part of my course work.
//
/////////////////////////////// 80 COLUMNS WIDE /////////////////////////////////**

/**
 * This class contains the methods responsible for determining the maximum parsimony tree based
 * off of the trees ancestor nodes and their respective possible trait states.
 *
 * @author Krischan Woehrer
 */
public class Parsimony {

    private static int[] indexOfMinimumParsimonyTree;
    private static int[] indexOfAncestorTracker;
    private static int minParsimonyScore;

    /**
     * This method takes a rooted tree with ancestor nodes that have an undetermined trait state and
     * determines the optimal trait states that will result in maximum parsimony of the tree.
     * 
     * @param tree A rooted tree containining species and their ancestors.
     * @return The parsimony score of the maximum parsimony tree.
     */
    public static int findMostParsimoniousTree(RootedTree tree) {
        int numOfAncestors = tree.size() - tree.getInitialSpecies().size();
        Species[] ancestorNodes = new Species[numOfAncestors];
        indexOfMinimumParsimonyTree = new int[numOfAncestors];
        indexOfAncestorTracker = new int[numOfAncestors];
        minParsimonyScore = Integer.MAX_VALUE;

        // Inserts all ancestor nodes into the species array ancestorNodes
        findAncestorNodes(tree.getRoot(), ancestorNodes, 0);
        // Create a hypothesis of what best state could be based off of prevalence of traits of
        // initial descendants
        // Note: This will not find the most parsiminious tree but drastically reduces the time to
        // calculate the most parsimonious tree by accelerating the rate at which the backtracking
        // algorithm performs
        maximumLikelyHoodTraitStates(ancestorNodes);

        // Finds optimal trait states of ancestor nodes
        findOptimalTraitStates(numOfAncestors, ancestorNodes);
        // Changes the actual trait array in the species object to its optimal configuration
        changeTreeTraitsToMinPars(ancestorNodes);

        return minParsimonyScore;
    }

    /**
     * Adds all ancestor species into an array. Iterates from top left to bottom right.
     * Index = 0 is the root of the tree.
     * @param Species Current species to add into the array. This method also takes the descendants
     *      of this species and adds them to the speciesArr array if they are also ancestor nodes.
     * @param speciesArr Array of ancestor species nodes to be populated by this method.
     * @param currIndex Current index of the array.
     */
    private static int findAncestorNodes(Species species, Species[] speciesArr, int currIndex) {
        // Puts current species into array
        speciesArr[currIndex] = species;
        currIndex++;
        // Gets Descendant species
        Species leftDescendant = species.getDescendants()[0];
        Species rightDescendant = species.getDescendants()[1];

        // If descendants aren't ancestor nodes then stop, if they are ancestor nodes than add them
        // to array
        if (leftDescendant.getHeight() != 1) {
            currIndex = findAncestorNodes(leftDescendant, speciesArr, currIndex);
        }

        if (rightDescendant.getHeight() != 1) {
            currIndex = findAncestorNodes(rightDescendant, speciesArr, currIndex);
        }

        return currIndex;
    }

    /**
     * This method determines the most likely trait states of an array of ancestor nodes based
     * off of the initial descendants (at tree instantation) trait values.
     * @param ancestorNodes An array containing all ancestor nodes of the tree.
     */
    private static void maximumLikelyHoodTraitStates(Species[] ancestorNodes) {
        // Can only determine the traits in this way for an ancestor node with a height of greater
        // than 3 as these ancestor nodes have at least 3 or more initial descendant species.
        for (int i = 0; i < ancestorNodes.length; i++) {
            if (ancestorNodes[i].getHeight() > 2) {
                // Finds likely trait state
                int[] likelyTraitState = findLikelyTraitState(ancestorNodes[i]);
                ancestorNodes[i].setTraits(likelyTraitState);
                // Matches trait state to a possible trait state and returns index of that possible
                // trait state.
                // Adds this index to its appropriate index in indexOfMinimumParsimonyTree.
                indexOfMinimumParsimonyTree[i] =
                    getTraitStateIndex(likelyTraitState, ancestorNodes[i]);
            }
        }

        // Determines most likely traits for ancestor nodes with a height of 2. This method uses the
        // ancestor of this ancestor node to make this inference as we cannot infer anything from an
        // ancestor node with only two descendants.
        // We cannot do so as the ancestor node trait states are both equally likely and either
        // option would increase parsimony score of the tree by 1. By keeping the trait value the same as the trait value
        // of the ancestor of this ancestor node, we avoid adding another an extra mutation/parsimony score.
        for (int i = 0; i < ancestorNodes.length; i++) {
            if (ancestorNodes[i].getHeight() == 2) {
                int[] currAncestorNodeTraits = ancestorNodes[i].getTraits();
                for (int j = 0; j < currAncestorNodeTraits.length; j++) {
                    // Replaces any -1 traits with the ancestors (of this node) trait value at that
                    // index
                    if (currAncestorNodeTraits[j] == -1) {
                        currAncestorNodeTraits[j] = ancestorNodes[i].getAncestor().getTraits()[j];
                    }
                }
                // Matches trait state to a possible trait state and returns index of that possible
                // trait state
                // Adds this index to its appropriate index in indexOfMinimumParsimonyTree
                indexOfMinimumParsimonyTree[i] =
                    getTraitStateIndex(currAncestorNodeTraits, ancestorNodes[i]);
            }
        }
        // Store minimum parsimony score
        minParsimonyScore = getParsimonyScore(ancestorNodes, 0);
    }

    /**
     * This method finds the most likely trait state of an ancestor node based off of its descendants that
     * were initial species entered at tree instantiation.
     * @param ancestor An ancestor node species.
     * @return An int array that contains the most likely trait state.
     */
    private static int[] findLikelyTraitState(Species ancestor) {
        ArrayList<Species> initialSpeciesThatAreDescendants = new ArrayList<Species>();
        getInitialSpeciesThatAreDescendants(ancestor, initialSpeciesThatAreDescendants);

        double[] maximumLikelyhoodTraitStateDbl = new double[ancestor.getTraits().length];
        int[] maximumLikelyhoodTraitStateInt = new int[ancestor.getTraits().length];

        // Adds the value of all traits of all initial species that are descendants of the ancestor
        for (int i = 0; i < initialSpeciesThatAreDescendants.size(); i++) {
            Species currentDescendant = initialSpeciesThatAreDescendants.get(i);
            int[] currentDescendantTraits = currentDescendant.getTraits();

            for (int j = 0; j < currentDescendantTraits.length; j++) {
                maximumLikelyhoodTraitStateDbl[j] += currentDescendantTraits[j];
            }
        }

        // Calculates if the traits are more likely to be present(1) or absent (0) in ancestor
        for (int i = 0; i < maximumLikelyhoodTraitStateDbl.length; i++) {
            maximumLikelyhoodTraitStateDbl[i] /= initialSpeciesThatAreDescendants.size();

            // If most descendants have trait present
            if (maximumLikelyhoodTraitStateDbl[i] > 0.5) {
                maximumLikelyhoodTraitStateInt[i] = 1;
            } // If most descendants have trait absent or absence/presence is equal.
            else if (maximumLikelyhoodTraitStateDbl[i] <= 0.5) {
                maximumLikelyhoodTraitStateInt[i] = 0;
            }
        }

        return maximumLikelyhoodTraitStateInt;
    }



    /**
     * This recursive method determines all descendants, of the provided ancestor node, that were initial species entered
     * at rooted tree instantiation. It does so by looking at all direct and indirect descendants of this species
     * and placing them in the provided array list if their height is equal to 1.
     * @param ancestor An ancestor node(species).
     * @param initSpeciesThatAreDescendants An empty array list that will contain all initial species that
     *      are descendants of this species.
     */
    private static void getInitialSpeciesThatAreDescendants(Species ancestor,
        ArrayList<Species> initialSpeciesThatAreDescendants) {
        // gets descendants of each ancestor node
        Species leftDescendant = ancestor.getDescendants()[0];
        Species rightDescendant = ancestor.getDescendants()[1];

        // If left descendant is an ancestor node then recursively call this method to find its
        // descendants
        if (leftDescendant.getHeight() != 1) {
            getInitialSpeciesThatAreDescendants(leftDescendant, initialSpeciesThatAreDescendants);
        } else {
            initialSpeciesThatAreDescendants.add(leftDescendant);
        }

        // If right descendant is an ancestor node then recursively call this method to find its
        // descendants
        if (rightDescendant.getHeight() != 1) {
            getInitialSpeciesThatAreDescendants(rightDescendant, initialSpeciesThatAreDescendants);
        } else {
            initialSpeciesThatAreDescendants.add(rightDescendant);
        }
    }

    /**
     * This method finds the index of a specified trait state in a species possible trait state array.
     * @param traitState The specified trait state.
     * @param species The species which contains its associated trait states.
     * @return Returns the index of the possible trait state that is identical to the specified trait state.
     */
    private static int getTraitStateIndex(int[] traitState, Species species) {

        for (int i = 0; i < species.getPossibleTraitStates().length; i++) {
            if (traitState.equals(species.getPossibleTraitStates()[i])) {
                return i;
            }
        }

        return -1;
    }


    /**
     * This recursive method finds the trait states with the minimum parsimony score and stores the index of these states in the appropriate field
     * Iterates from the bottom right to bottom left, then moves up a height value and repeats
     * right to left until root is reached. Algorithm can be best understood by walking through
     * the comments below and tracking the code.
     * @param speciesIndex The index of the current species.
     * @param speciesArr The array of ancestor nodes.
     * @return The species index.
     */
    private static void findOptimalTraitStates(int speciesIndex, Species[] speciesArr) {
        speciesIndex--;

        // Exit condition to the recursive function.
        // When we have gone through each ancestor node in the speciesArr, then we are done
        // calculating for this tree and can start calculating the next one until all
        // possible combinations have been solved/disproved to be the tree with minimum parsimony values.
        if (speciesIndex < 0) {
            return;
        }

        // Gets the species reference value from the speciesArr array
        Species currentSpecies = speciesArr[speciesIndex];
        // Loops through each possible trait for the current species in the speciesArr at
        // speciesIndex
        for (int i = 0; i < currentSpecies.getPossibleTraitStates().length; i++) {
            currentSpecies.setTraits(currentSpecies.getPossibleTraitStates()[i]);
            indexOfAncestorTracker[speciesIndex] = i;

            int currParsimonyScore = getParsimonyScore(speciesArr, speciesIndex);
            // Calculate parsimony and store the parsimony and its related data into the method
            // fields if and only if this is the most parsiminious tree seen so far.
            // Only occurs when the method reaches the root of the tree and rest of ancestor nodes
            // in the ancestor node array have had their trait state determined for this tree.
            if (speciesIndex == 0) {
                // Compares to minimum parsimony score, if smaller we change minimum score and store
                // the index of the trait states.
                if (currParsimonyScore <= minParsimonyScore) {
                    minParsimonyScore = currParsimonyScore;
                    indexOfMinimumParsimonyTree = indexOfAncestorTracker.clone();
                    System.out.println("~~!!~~!!~~!!~~!!New Minimum Parsimony Tree found at index " + i + " of the root species!!~~!!~~!!~~!!~~");
                }
            }
            // Allows for backtracking, if at any point the partial tree has a parsimony score
            // greater than the current minimum parsimony score of a full tree, stop iterating this path and
            // go to the next one.
            if (currParsimonyScore >= minParsimonyScore) {
                continue;
            }
            // Displays program finding optimal trait states
            for (int j = (speciesArr.length - 1 - speciesIndex); j > 0; j--) {
                System.out.print("\t");
            }
            System.out.println("Calculating Parsimony for Trait State " + i + " of species index "
                + speciesIndex + " and all subsequent trees");

            // Recursive method call to determine the next ancestor nodes traits in the current tree
            findOptimalTraitStates(speciesIndex, speciesArr);
        }

        return;
    }

    /**
     * This method calculates the parsimony score of the speciesArr. It works by calculating
     * the parsimony score of each ancestor node to its descendants. If the indexToIterateFrom is
     * 0, then the parsimony score is calculated for the entire rooted tree. If the indexToIterateFrom
     * is greater than 0, then a partial parsimony score is calculated based on the species in the
     * species array.
     * @see Species.getParsimonyScoreToDescendants for information on how parsimony is calculated.
     * @param speciesArr An array of species (ancestor nodes in scope of this program)
     * @param indexToIterateFrom The index in speciesArr to start calculating parsimony from.
     * @return int parsimony score from indexToIterate from until end of speciesArr
     */
    private static int getParsimonyScore(Species[] speciesArr, int indexToIterateFrom) {
        int parsimonyScoreOfTree = 0;

        for (int i = indexToIterateFrom; i < speciesArr.length; i++) {
            parsimonyScoreOfTree += speciesArr[i].getParsimonyScoreToDescendants();
        }

        return parsimonyScoreOfTree;
    }

    /**
     * This method changes the actual trait states of the ancestor nodes to the states determined to have minimum parsimony.
     * @param ancestorNodes
     */
    private static void changeTreeTraitsToMinPars(Species[] ancestorNodes) {
        for (int i = 0; i < ancestorNodes.length; i++) {
            int[] optimalTraits =
                ancestorNodes[i].getPossibleTraitStates()[indexOfMinimumParsimonyTree[i]];
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
