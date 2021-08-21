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
// Reasons for creating this program:
//      1. I remember taking microbiology-470 at UW-Madison and one of our big
//      projects for the semester was to create the most parsiminious
//      phylogenetic tree of 20-25 randomly named species when we were only given
//      the presence of various traits (I remember it being close to 10-12).
//      Everyone in the course spent hours upon hours manually crafting their
//      trees and I remember thinking to myself how nice it would be if a
//      computer could help compute the optimal state for parsimony.
//      Unfortunately, almost all the programs found online either costed money
//      or their algorithms were faulty and yielded trees that were close to
//      the most parsiminious tree but not quite there. Now that I am pursuing
//      a career in computer science and developement, I have decided to solve
//      this problem on my own and offer it as a free resource online.
//      2. Gain a greater understanding of binary trees and their algorithms
//      3. Practice my skills learned from my coursework in computer science.
//      4. Improve my ability to perform OOP
//Things to improve/TODO:
//      1. Numerous points in the program could be better optimized to run a
//      little bit faster. These are noted locally when they occur.
//      2. When determining the most parsiminious trait states, I currently use
//      a brute-force approach. Although I narrow down the number of computations
//      by determining traits of ancestors we know for certain, I believe there
//      are additional ways I can cut down on the number of computations and speed
//      the program up.
//      A brute-force approach will likely still be required in order construct
//      the most parsiminious phylogenetic tree accurately.
//      3. Create a GUI
//      4. Pipeline this as a backend to a website/server and make this more
//      accessible to people who aren't tech savvy.
// 
//
///////////////////////////////// CITATIONS ////////////////////////////////////
//
//Source or Recipient; Description
// UW-Madison Microbiology-470
// This class taught me how to do UPGMA based phylogeny as part of my course work.
//
/////////////////////////////// 80 COLUMNS WIDE /////////////////////////////////**

import java.lang.IndexOutOfBoundsException;
import java.util.ArrayList;


/**
 * This class is an instance based class that contains various methods for generating
 * and manipulating species objects and their appropriate fields.
 * @author Krischan Woehrer
 *
 */
public class Species {
    private int[] traits;
    private int[][] possibleTraitStates;
    private boolean isDescendantSpecies = true;
    private boolean isAncestor = false;
    private Species ancestor;
    private Species[] descendants;
    private String name;
    private int height;
    private int[] locationOnTree;

    /**
     * Constructor method that creates species object.
     * @param inputTraitsStr String containing traits. Trait values should be 1 
     *      for present and 0 for not present. All traits seperated by a space.
     * @param numberOfTraits Number of traits 
     */
    public Species(String name, String inputTraitsStr, int numberOfTraits) {
        this.traits = new int[numberOfTraits];
        this.name = name;
        for (int i = 0; i < numberOfTraits; i++) {
            String[] inputTraits = inputTraitsStr.split(" ");
            this.traits[i] = Integer.parseInt(inputTraits[i]);
        }
    }

    /**
     * Constructor method that creates a species object of subtype descendant species.
     * This constructor initializes the trait array.
     * @param traits
     * @param isDescendantSpecies
     */
    public Species(int[] traits, boolean isDescendantSpecies) {
        this.traits = traits;
        this.setDescendantSpeciesStatus();
    }

    /**
     * Constructor method that creates a species object of subtype descendant species.
     * This constructor initializes the trait array and the name of the species;
     * @param traits
     * @param isDescendantSpecies
     */
    public Species(String name, int[] traits, boolean isDescendantSpecies) {
        this.name = name;
        this.traits = traits;
        this.setDescendantSpeciesStatus();
    }

    /**
     * Default constructor method
     */
    public Species() {
        this.traits = null;
    }

    /**
     * Determines the number of similar traits between this species and another species
     * @param species2 Another species object that will be compared to this species
     * @return A similarity score.
     */
    public int getSimilarityValue(Species species2) {
        int similarityScore = 0;
        for (int i = 0; i < this.traits.length; i++) {
            try {
                if (this.traits[i] == species2.getTraits()[i]) {
                    similarityScore++;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Not all species have the same number of traits");
            }
        }
        return similarityScore;
    }

    /**
     * This method calculates the parsimony value of this species to its direct descendants.
     * To clarify, it calculates the right and left descendants parsimony scores.
     * @return
     */
    public int getParsimonyScoreToDescendants() {
        int parsimonyScore = 0;
        Species leftDescendant = this.getDescendants()[0];
        Species rightDescendant = this.getDescendants()[1];

        for (int i = 0; i < this.getTraits().length; i++) {
            if (leftDescendant.getTraits()[i] != this.getTraits()[i]) {
                parsimonyScore++;
            }
            if (rightDescendant.getTraits()[i] != this.getTraits()[i]) {
                parsimonyScore++;
            }
        }

        return parsimonyScore;
    }
    
    /**
     * This method calculates the parsimony score between this species and the species
     * passed in as a parameter.
     * @param species The species being compared to
     * @return int the parsimony scores between these species
     */
    public int getParsimonyScore(Species species) {
        int parsimonyScore = 0;
        for (int i = 0; i < this.getTraits().length; i++) {
            if (species.getTraits()[i] != this.getTraits()[i]) {
                parsimonyScore++;
            }
        }
        return parsimonyScore;
    }

    /**
     * Determines the number of similar traits between this species and another species.
     * @param species2 Another species object that will be compared to this species
     * @return The distance value(number of dissimilar traits) between species
     */
    public int getDistanceValue(Species species2) {
        int distanceValue = 0;
        for (int i = 0; i < this.traits.length; i++) {
            try {
                if (this.traits[i] != species2.getTraits()[i]) {
                    distanceValue++;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println("Not all species have the same number of traits");
            }
        }
        return distanceValue;
    }

    /**
     * Compares two species and returns an integer array parallel to the trait arrays of both species.
     * The integer array consists of 1's and 0's. If the traits at an index are the same then the array
     * element at that index will contain a 1, and if different, a 0.
     * @return A integer array containing the comparison array
     */
    public int[] compareWith(Species species2) {
        int[] traitComparison = new int[this.traits.length];
        for (int i = 0; i < traitComparison.length; i++) {
            if (this.getTraits()[i] == species2.getTraits()[i]) {
                traitComparison[i] = 1;
            } else {
                traitComparison[i] = 0;
            }
        }

        return traitComparison;
    }

    /**
     * This is an accessor method that returns the trait array of this species.
     * @return
     */
    public int[] getTraits() {
        return this.traits;
    }

    /**
     * This is a mutator method that changes the trait array of the species.
     * @param traits The new trait array for the species
     */
    public void setTraits(int[] traits) {
        this.traits = traits;
    }

    /**
     * This is an accessor method that returns the height value, in the tree, for this species.
     * @return
     */
    public int getHeight() {
        return this.height;
    }

    /**
     * This is a mutator method that changes the height, in the tree, for this species.
     * @param height The new height value for the species.
     */
    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * This method links two existing species to this species as descendants.
     * It does so by setting both descendant array elements of this species to reference the
     * species provided in the parameters. Then the descendants ancestor field is
     * set to reference this species as an ancestor.
     * Additionally, the height of this species is also determined.
     * First, the heights of both descendants are compared and the larger one is found.
     * The height of this species, the ancestor, is set to 1 greater than the larger descendant height.
     * @param speciesLeft
     * @param speciesRight
     */
    public void setDescendantSpecies(Species speciesLeft, Species speciesRight) {
        this.descendants = new Species[2];
        speciesLeft.ancestor = this;
        speciesRight.ancestor = this;
        this.descendants[0] = speciesLeft;
        this.descendants[1] = speciesRight;
        this.setAncestorStatus();
        // The following code determines height for this species based off of its immediate
        // descendants
        if (speciesLeft.getHeight() > speciesRight.getHeight()) {
            this.setHeight(speciesLeft.getHeight() + 1);
        } else if (speciesLeft.getHeight() < speciesRight.getHeight()) {
            this.setHeight(speciesRight.getHeight() + 1);
        } else {
            this.setHeight(speciesLeft.getHeight() + 1);
        }
    }

    /**
     * This is a mutator method that allows one to change the ancestor of a species.
     * Please Note: This method DOES NOT change the descendant array of this species.
     * @param ancestor A Species to be set as the ancestor of this species.
     */
    public void setAncestor(Species ancestor) {
        if (ancestor.isDescendantSpecies) {
            System.err.println("Error: Cannot set a descendantSpecies as ancestor");
        }
        this.ancestor = ancestor;
    }

    /**
     * This is an accessor method that allows one to get the reference to the ancestor of this species.
     * @return The reference to the ancestor of this species.
     */
    public Species getAncestor() {
        return this.ancestor;
    }

    /**
     * This is a mutator method that allows one to directly change the descendants of this species.
     * @param descendants An array containing the two species to be set as the ancestor
     */
    public void setDescendants(Species[] descendants) {
        // Direct references to the descendants are used to make a deep copy instead of simply
        // referencing the descendant array parameter
        this.descendants[0] = descendants[0];
        this.descendants[1] = descendants[1];
    }

    /**
     * This is an accessor method that allows one to retrieve an array containing the references
     * to both descendants.
     * @return
     */
    public Species[] getDescendants() {
        return this.descendants;
    }

    /**
     * This is a mutator method that sets the status of this species to be an ancestor.
     */
    public void setAncestorStatus() {
        this.isAncestor = true;
        this.isDescendantSpecies = false;
    }

    /**
     * This is an accessor method that returns if this species is an ancestor.
     * @return True if this species is an ancestor, false if it is a descendant.
     */
    public boolean getAncestorStatus() {
        return this.isAncestor;
    }

    /**
     * This is a mutator method that that sets the status of this species to be a descendant.
     */
    public void setDescendantSpeciesStatus() {
        this.isAncestor = false;
        this.isDescendantSpecies = true;
        this.height = 1;
    }

    /**
     * This is an accessor method that returns if this species is a descendant.
     * @return True if this species is an descendant, false if it is a ancestor.
     */
    public boolean getDescendantSpeciesStatus() {
        return this.isDescendantSpecies;
    }

    /**
     * This method returns a string representation of this species status as a
     *
     * Currently unused but might be used in future implementations of this project.
     * @return
     */
    public String ancestorOrSpecies() {
        if (this.isAncestor) {
            return "ancestor";
        } else if (this.isDescendantSpecies) {
            return "descendant species";
        }
        return "error";
    }
    
    /**
     * This is a mutator method that allows for one to set the name of this species.
     * @param name The desired name for this species.
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * This is an accessor method that allows for one to get the name of this species.
     * @return A string representation of the name of this species
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * This is a mutator method that allows for one to set the location of this species.
     * The location of this species correlates with a char array found in the rooted tree object.
     * @see RootedTree.toString() for more information on how this is used
     * @param row The "x" coordinate on the char array
     * @param column The "y" coordinate on the char array
     */
    public void setLocationOnTree(int row, int column) {
        this.locationOnTree = new int[2];
        this.locationOnTree[0] = row;
        this.locationOnTree[1] = column;
    }   
    
    /**
     * This is an accessor method that allows for one to get the location of this species.
     * The location of this species correlates with a char array found in the rooted tree object.
     * @see RootedTreee.toString() for more information on how this is used
     * @return An array containing the coordinates for the location on the rooted tree.
     *      Index[0] is the "x" coordinate. Index[1] is the "y" coordinate.
     */
    public int[] getLocationOnTree() {
        return this.locationOnTree;
    }

    /**
     * This method returns the possible trait states of this species. Only applicable to
     * species that are of subtype ancestors.
     * @return An array of integer arrays that correlate to possible trait states of this species.
     */
    public int[][] getPossibleTraitStates() {
        return this.possibleTraitStates;
    }

    /**
     * This method sets the possible trait states of this species based on the possible state instance method.
     * @See this.possibleStates() for how the possible trait states are determined.
     */
    public void setPossibleTraitStates() {
        this.possibleTraitStates = this.possibleStates();
    }

    /**
     * This method determines the possible trait states through the following algorithm.
     * Algorithm:
     *      First we obtain the traits we know will be true for this species. Any traits
     *      that share the same value (both 0 or both 1) in the descendants will have the
     *      same value in the ancestor as this results in a net parsimony score change of 0.
     *      If the descendants have different trait values for a trait, then the trait value
     *      for the ancestor is stored as a -1 as we cannot infer the trait status from the 
     *      descendants. Once every element has a value in the traits array, then the 
     *      possible trait states are determined by using the determineAllPossibleStates method.
     *      @see Species.determineAllPossibleStates
     *      
     *      
     * @return
     */
    public int[][] possibleStates() {
        final int NUM_OF_TRAITS = this.descendants[0].getTraits().length;
        int[][] possibleTraitStates = null;

        // Find traits that you know will be true in ancestor
        int[] templateTraits = new int[NUM_OF_TRAITS];

        int i = 0;
        // TODO: Could optimize the compareWith method to return -1 for differing
        // traits and putting in actual shared trait values. This would skip this loop
        for (int trait : this.descendants[0].compareWith(descendants[1])) {
            if (trait == 1) {
                templateTraits[i] = this.descendants[0].traits[i];
            } else {
                templateTraits[i] = -1;
            }
            i++;
        }
        this.setTraits(templateTraits);
        
        //furtherOptimizeTemplate();
        // if a trait is prevalent in all species except one the set this ancestor trait to thatthe majority

        int numOfPossibleTraitStates = countOfPossibleTraitStates(templateTraits);
        possibleTraitStates = new int[numOfPossibleTraitStates][NUM_OF_TRAITS];

        // Initialize each traitState with copy of template
        for (int j = 0; j < numOfPossibleTraitStates; j++) {
            possibleTraitStates[j] = copyIntArr(templateTraits);
        }
        determineAllPossibleStates(possibleTraitStates, numOfPossibleTraitStates, 1);

        return possibleTraitStates;
    }
    
    /**
     * This method helps further optimize tree by placing extremely likely trait states.
     * Currently leads to an extreme lose in accuracy for parsimony and will have to rethink
     * the implementation of this method
     */
    private void furtherOptimizeTemplate() {
        ArrayList<Species> initSpeciesThatAreDescendants = new ArrayList<Species>();
        this.getInitialSpeciesThatAreDescendants(initSpeciesThatAreDescendants);
        
        int numOfInitSpeciesDescendants = initSpeciesThatAreDescendants.size();
        int traitArrLength = this.traits.length;
        
        //Parallel array that correlates with trait prevalance
        double[] traitPrevalence = new double[traitArrLength];
        //Iterates through the array list of descendants 
        for(int i = 0; i < initSpeciesThatAreDescendants.size(); i++) {
            Species currDescendant = initSpeciesThatAreDescendants.get(i);
            // Iterates through the currDescendants trait array and adds it to parallel index
            // in trait prevalence array
            for(int j = 0; j < traitArrLength; j++) {
                traitPrevalence[j] += currDescendant.getTraits()[j];
            }
        }
        
        for(int i = 0; i < traitArrLength; i++) {
            traitPrevalence[i] /= numOfInitSpeciesDescendants;
            //If trait is present in 80% of species
            if(traitPrevalence[i]>= 1) {
                this.traits[i] = 1;
            } //If trait is absent in 80% of species, set the value in th
            else if(traitPrevalence[i] <= 0) {
                this.traits[i] = 0;
            }
        }
    
    }
    
    /**
     * This method recursive method returns all descendants of this species that were initial species entered in
     * at rooted tree creation. It does so by looking at all direct and indirect descendants of this species
     * and placing them in a provided array list if their height is equal to 1.
     * @param initSpeciesThatAreDescendants An empty array list that will contain all initial species that
     *      are descendants of this species.
     */
    private void getInitialSpeciesThatAreDescendants(ArrayList<Species> initSpeciesThatAreDescendants) {
        if(this.height == 1) {
            return;
        }
        
        Species leftDescendant = this.getDescendants()[0];
        Species rightDescendant = this.getDescendants()[1];
        
        if(leftDescendant.height != 1) {
            leftDescendant.getInitialSpeciesThatAreDescendants(initSpeciesThatAreDescendants);
        } else {
            initSpeciesThatAreDescendants.add(leftDescendant);
        }
        
        if(rightDescendant.height != 1) {
            leftDescendant.getInitialSpeciesThatAreDescendants(initSpeciesThatAreDescendants);
        } else {
            initSpeciesThatAreDescendants.add(rightDescendant);
        }
    }

    /**
     * This method creates a deep copy of an array of integers.
     * @param templateTraits The integer array to be copied
     * @return A deep copy of the array.
     */
    private int[] copyIntArr(int[] templateTraits) {
        int[] copy = new int[templateTraits.length];
        for (int i = 0; i < templateTraits.length; i++) {
            copy[i] = templateTraits[i];
        }
        return copy;
    }

    /**
     * This method counts the number of variable traits(trait value = -1) in an incomplete trait array
     * @param traitArr
     * @return
     */
    private int countNumberOfVariableTraits(int[] traitArr) {
        int variableTraitCount = 0;
        for (int trait : traitArr) {
            if (trait == -1) {
                variableTraitCount++;
            }
        }
        return variableTraitCount;
    }

    /**
     * This method returns the number of possible trait states based on the number of traits with a value of -1
     * in a trait array.
     * @param traitArr
     * @return
     */
    private int countOfPossibleTraitStates(int[] traitArr) {
        final int NUM_VARIABLE_TRAITS = countNumberOfVariableTraits(traitArr);
        int numOfPossibleTraitStates = 1;
        // Executes 2^numOfPossibleTraitStates without importing java.lang.Math
        for (int i = 0; i < NUM_VARIABLE_TRAITS; i++) {
            numOfPossibleTraitStates *= 2;
        }
        return numOfPossibleTraitStates;
    }

    /**
     * This is a recursive method that populates the possibleTraitStates member variable array.
     * Algorithm:
     *      First we find the index for the first -1 (variable/undefined trait) in the possible
     *      trait state array for the ancestor species. If a -1 is present, the index is stored,
     *      if not present a -2 is stored so the program may exit as all trait states have been
     *      generated.
     *      Second, a loop for populates each the of possible trait states with either a 0 or a 1
     *      at the first index determined to contain a -1.
     *      Third, the method recursively calls itself if our current number of trait states is less
     *      than the calculated number of possible states for this species. If our current number of
     *      trait states is equal to (or greater than which theoretically should never happen) the
     *      number of possible trait states then the method returns.
     * 
     *     
     * @param possibleTraitStates An array of various trait states to be populated. Trait states
     *      should contain both defined traits (0 or 1) or undefined/variable traits (-1)
     * @param numOfPossibleTraitStates The number of possible trait states.
     * @param currentNumOfTraitStates The current number of trait states that we have generated
     */
    private void determineAllPossibleStates(int[][] possibleTraitStates,
        int numOfPossibleTraitStates, int currentNumOfTraitStates) {
        int indexOfFirstVariableTrait = -2;
        // Finds first index of -1, in other words it finds the index of the first variable trait
        for (int i = 0; i < possibleTraitStates[0].length; i++) {
            if (possibleTraitStates[0][i] == -1) {
                indexOfFirstVariableTrait = i;
                break;
            }
        }
        // Exit condition 1, this should only occur when the method is called on an ancestor whose
        // descendants share the exact same trait states
        if (indexOfFirstVariableTrait == -2) {
            return;
        }

        // The *2 here allows us to later determine if we have generated all possible trait states
        // This loop populates all the variable trait with either 0 or 1 in all possible trait state
        // elements
        for (int i = (currentNumOfTraitStates *= 2) - 1; i > (currentNumOfTraitStates / 2)
            - 1; i--) {
            possibleTraitStates[i] =
                copyIntArr(possibleTraitStates[i - ((currentNumOfTraitStates / 2))]);
            possibleTraitStates[i][indexOfFirstVariableTrait] = 1;
            possibleTraitStates[i - ((currentNumOfTraitStates / 2))][indexOfFirstVariableTrait] = 0;
        }
        // Exit condition 2, Exit recursive method call when all possible trait states have been
        // generated
        if (currentNumOfTraitStates < numOfPossibleTraitStates) {
            determineAllPossibleStates(possibleTraitStates, numOfPossibleTraitStates,
                currentNumOfTraitStates);
        } else {
            return;
        }
        return;
    }
}
