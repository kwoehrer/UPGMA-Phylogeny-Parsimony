package phylogenetics;

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

import java.util.ArrayList;

/**
 * This class is responsible for the creation of a rooted tree as well as methods pertaining to the
 * rooted tree such as getting the root of the tree and converting the binary tree data structure of
 * the tree to a user friendly string format.
 * 
 * @author Krischan Woehrer
 *
 */
public class RootedTree {
    private ArrayList<Species> speciesNodeList;
    private ArrayList<Species> initialSpeciesNodeList;
    private ArrayList<ArrayList<Double>> initialDistanceMatrix;
    private boolean showParsimony;
    private int size;
    private int width;
    private int branchLength;

    /**
     * Default Constructor, If used this will return an error.
     */
    public RootedTree() {
        System.err.println("This Constructor requires argument to be in array format");
    }

    /**
     * Constructor for if a user tries to use an arraylist instead of a speciesList.
     * This constructor will return an error.
     * @param speciesList
     */
    public RootedTree(ArrayList<Species> speciesList) {
        System.err.println("This Constructor requires argument to be in array format");
    }

    /**
     * The constructor that should be used for all rooted tree creation. Initializes
     * all member fields and creates the binary tree through a UPGMA-Type algorithm.
     * After constructor is finished, the root of the tree should remain as the single
     * species left in the speciesNodeList.
     * @param speciesArr The species to be used as descendants in the phylogenetic tree
     */
    public RootedTree(Species[] speciesArr) {
        this.initialSpeciesNodeList = new ArrayList<Species>();
        this.speciesNodeList = new ArrayList<Species>();

        // This loop adds initialspecies
        for (int i = 0; i < speciesArr.length; i++) {
            speciesArr[i].setDescendantSpeciesStatus();
            this.speciesNodeList.add(speciesArr[i]);
            this.initialSpeciesNodeList.add(speciesArr[i]);
            this.size++;
        }

        // The following code creates ancestor nodes that correlate to the binary tree
        ArrayList<ArrayList<Double>> distanceArr = this.setDistanceArray();
        while (this.speciesNodeList.size() > 1) {
            int[] twoClosestClusters = findSmallestDistance(distanceArr);
            createAncestorNode(this.speciesNodeList.get(twoClosestClusters[0]),
                this.speciesNodeList.get(twoClosestClusters[1]), distanceArr);
            this.size++;
        }
        this.showParsimony = false;
        this.width = 0;
        this.branchLength = 3;
    }

    /**
     * This method takes two species and creates an ancestor node that contains the
     * two specified parameter species as descendants. The distance array takes the
     * average of the two species distances to the other species on the tree and the ancestor then
     * uses this average as its distance to other species on the tree. The distance array then
     * removes both descendant species from the distance array and adds in the new ancestor node.
     * @param species1 The first descendant (Left descendant in string format)
     * @param species2 The second descendant (Right descendant in string format)
     */
    private void createAncestorNode(Species species1, Species species2,
        ArrayList<ArrayList<Double>> distanceArr) {
        final int INDEX_OF_FIRST_SPECIES = this.speciesNodeList.indexOf(species1);
        final int INITIAL_INDEX_OF_SECOND_SPECIES = this.speciesNodeList.indexOf(species2);
        final int NUMBER_OF_SPECIES = distanceArr.size();
        final String ANCESTOR_NAME = species1.getName() + " " + species2.getName();

        Species ancestorOfSpecies = new Species();
        ancestorOfSpecies.setName(ANCESTOR_NAME);
        ancestorOfSpecies.setAncestorStatus();
        ancestorOfSpecies.setDescendantSpecies(species1, species2);
        this.speciesNodeList.remove(species1);
        this.speciesNodeList.set(speciesNodeList.indexOf(species2), ancestorOfSpecies);

        ArrayList<Double> distanceForAncestor = new ArrayList<Double>();
        // Average of distances of A and B to other nodes and updates distanceArr
        for (int i = 0; i < NUMBER_OF_SPECIES; i++) {
            double avgOfDistances;
            if (i == INITIAL_INDEX_OF_SECOND_SPECIES) {
                avgOfDistances = 0;
            } else {
                avgOfDistances = (((distanceArr.get(INDEX_OF_FIRST_SPECIES).get(i))
                    + distanceArr.get(INITIAL_INDEX_OF_SECOND_SPECIES).get(i))) / 2;
            }
            distanceForAncestor.add(avgOfDistances);
        }
        distanceArr.set(INITIAL_INDEX_OF_SECOND_SPECIES, distanceForAncestor);
        // Removes the first species distance row
        distanceArr.remove(INDEX_OF_FIRST_SPECIES);

        // Updates values for new cluster in distanceArray
        for (int i = 0; i < NUMBER_OF_SPECIES - 1; i++) {
            distanceArr.get(i).set(INITIAL_INDEX_OF_SECOND_SPECIES, distanceForAncestor.get(i));
        }
        // Removes the comparisons to the first species
        for (int i = 0; i < NUMBER_OF_SPECIES - 1; i++) {
            distanceArr.get(i).remove(INDEX_OF_FIRST_SPECIES);
        }
    }

    /**
     * This method finds the smallest distance on the distance array and returns the indices of
     * the two species that correspond with the smallest distance.
     * @param distanceArr An array of evolutionary distances between species.
     * @return An array containing the indices of the two closest species based on traits commonality.
     */
    private int[] findSmallestDistance(ArrayList<ArrayList<Double>> distanceArr) {
        final int NUMBER_OF_SPECIES = distanceArr.size();
        final int NUMBER_OF_TRAITS = this.initialSpeciesNodeList.get(0).getTraits().length;
        int[] twoClosestSpecies = new int[2];
        double minimumDistance = NUMBER_OF_TRAITS;

        for (int i = 0; i < NUMBER_OF_SPECIES - 1; i++) {
            for (int j = i + 1; j < NUMBER_OF_SPECIES; j++) {
                if (minimumDistance > distanceArr.get(i).get(j)) {
                    minimumDistance = distanceArr.get(i).get(j);
                    twoClosestSpecies[0] = i;
                    twoClosestSpecies[1] = j;
                }
            }
        }
        return twoClosestSpecies;
    }

    /**
     * This method initializes the distance array based off of the species node list
     * provided at object instantiation. 
     * @see Species.getDistanceValue for more information for how distance is calculated.
     * @return An array list of array lists of doubles that contains distance values.
     *      The distance matrix runs parallel to the species node list.
     */
    private ArrayList<ArrayList<Double>> setDistanceArray() {
        final int NUMBER_OF_SPECIES = this.speciesNodeList.size();

        ArrayList<ArrayList<Double>> distanceMatrix = new ArrayList<ArrayList<Double>>();
        for (int i = 0; i < NUMBER_OF_SPECIES; i++) {
            distanceMatrix.add(new ArrayList<Double>());

            // This loop j = i for effeciency but we are just worrying about getting code to work
            // right now
            for (int j = 0; j < NUMBER_OF_SPECIES; j++) {
                distanceMatrix.get(i).add(
                    (double) this.speciesNodeList.get(i).getDistanceValue(speciesNodeList.get(j)));
            }
        }
        this.initialDistanceMatrix = distanceMatrix;
        return distanceMatrix;
    }

    /**
     * This is an accessor method that returns the root of the tree.
     * The root of the tree can be defined as the most common ancestor of all species provided to
     * the constructor.
     * @return The root of the tree.
     */
    public Species getRoot() {
        return this.speciesNodeList.get(0);
    }

    /**
     * This is an accessor method that returns the entire species node list.
     * This method is mainly used for testing and should only contain the root.
     * @return species
     */
    public ArrayList<Species> getSpeciesList() {
        return this.speciesNodeList;
    }

    /**
    * This is an accessor method that returns an array list containing the initial
    * species used (AKA the descendants) to generate this tree.
    * @return An array list containing the initial species used to generate the tree.
    */
    public ArrayList<Species> getInitialSpecies() {
        return this.initialSpeciesNodeList;
    }

    /**
     * This is an accessor method that returns the size of the tree. The size of the tree
     * can be defined as the total number of species nodes (ancestors and descendants) present
     * in the tree.
     * @return The size of the tree as an integer.
     */
    public int size() {
        return this.size;
    }

    /**
     * This is an accessor method that returns the initial distance matrix generated by the initial
     * species provided at object instantiation.
     * This method is mainly used for testing purposes.
     * @return The initial distance matrix.
     */
    public ArrayList<ArrayList<Double>> getInitialDistanceMatrix() {
        return this.initialDistanceMatrix;
    }

    /**
     * This is a mutator method that is used if a user wishes to show parsimony on the tree.
     * If called, sets showParsimony to true and then calculates the trait values of each ancestor node
     * that would result in the most parsimonious(least number of evolutionary changes) tree.
     * @see RootedTree.setPossibleAncestorTraits for how parsimony is determined
     */
    public void setShowParsimony() {
        this.showParsimony = true;
        setPossibleAncestorTraits(this.getRoot());
    }

    /**
     * This method recursively sets each ancestor node with its possible trait states.
     * 
     * Note: Descendants with a height of 1 are species provided at tree instantiation
     * and should already have defined traits and thus we do not need to set their trait states.
     * @see Species.setPossibleTraitStates for how the algorithm works
     * @param root The species that is the root of the tree.
     */
    private void setPossibleAncestorTraits(Species root) {
        Species leftDescendant = root.getDescendants()[0];
        Species rightDescendant = root.getDescendants()[1];

        if (rightDescendant.getHeight() != 1) {
            setPossibleAncestorTraits(rightDescendant);
            rightDescendant.setPossibleTraitStates();
        }

        if (leftDescendant.getHeight() != 1) {
            setPossibleAncestorTraits(leftDescendant);
            leftDescendant.setPossibleTraitStates();
        }

        root.setPossibleTraitStates();
        return;
    }

    /**
     * This method overrides javas default object toString method to generate a string representation
     * of the phylogenetic tree.
     *      
     */
    @Override
    public String toString() {
        String outputTree = "";
        boolean treeDone = false;
        char[][] treeAsCharArr = null;
        // branchIncreaser is used in case the width of our "String tree" char arr is not big enough
        int branchIncreaser = 0;

        int heightOfRoot = this.getRoot().getHeight();
        int widthOfTree = heightOfRoot * ((this.branchLength) * this.initialSpeciesNodeList.size());
        this.width = widthOfTree;
        // catch java.lang.ArrayIndexOutOfBoundsException
        do {
            treeAsCharArr = new char[heightOfRoot * (this.branchLength + 1)][widthOfTree];
            // Inserts the root onto the tree along with its branchs
            String rootStr = "Root: " + this.getRoot().getName();
            this.getRoot().setLocationOnTree(0, widthOfTree / 2);
            this.insertStringIntoCharArr(rootStr, 0, widthOfTree / 2, treeAsCharArr);
            this.insertHeight(this.getRoot().getHeight(), treeAsCharArr);
            this.insertBranch(this.getRoot(), treeAsCharArr);
            // Inserts all descendants of the root into the char array (AKA all species)
            // Try block used to catch if we need to make width of tree (char array) bigger to fit
            // tree
            try {
                this.insertDescendantsIntoCharArr(this.getRoot(), treeAsCharArr);
            } catch (ArrayIndexOutOfBoundsException e) {
                e.printStackTrace();
                widthOfTree = heightOfRoot * ((this.branchLength + ++branchIncreaser)
                    * this.initialSpeciesNodeList.size());
                this.width = widthOfTree;
                continue;
            }
            // Prints out the height, left aligned, on the line where species are shown
            for (int i = this.getRoot().getHeight() - 2; i >= 0; i--) {
                insertHeight((i + 1), treeAsCharArr);
            }
            treeDone = true;
        } while (!treeDone);
        //Converts null chars in char array into ' ' chars
        replaceNullChar(treeAsCharArr);

        // Converts the charArr(the tree) to a String
        outputTree += Output.multipleCharsToString('-', widthOfTree) + "\n";
        for (int i = 0; i < treeAsCharArr.length; i++) {
            outputTree += new String(treeAsCharArr[i]) + "\n";
        }
        outputTree += Output.multipleCharsToString('-', widthOfTree) + "\n";

        // If user specified to show parsimony, we do some additional method calls to show parsimony
        if (this.showParsimony) {
            // Creates an arraylist of arraylists of species at a height value on the tree.
            // Index 0 = height value of 1 which would correlate with descendant species.
            ArrayList<ArrayList<Species>> speciesByHeight = null;
            speciesByHeight = speciesByHeight(this.getRoot());

            // Prints out probable traitStates for ancestors
            String parsimonyScore = "Parsimony Score Of Tree: " + Parsimony.getParsimonyScoreOfTree();
            outputTree +=
                Output.multipleCharsToString(' ', (widthOfTree - parsimonyScore.length()) / 2)
                    + parsimonyScore + "\n\n";
            String traitStateHeader = "Probable trait states of ancestor nodes:";
            outputTree +=
                Output.multipleCharsToString(' ', (widthOfTree - traitStateHeader.length()) / 2)
                    + traitStateHeader + "\n";

            outputTree += traitStatesToString(speciesByHeight, heightOfRoot);
        }

        return outputTree;
    }

    /**
     * This method starts the process of classifying every species with the same height on the tree
     * into an array list. An arrayList is created of these array lists and the index of this array list
     * correlates with the height - 1 of the species it contains.
     * TODO: Possible way to better implement, change to array of arraylists as this allows for better memory management
     * @param root The species that is the root of the tree. 
     * @return An array list of array lists of species that have species separated based on their height.
     *      Ex. index<0> = array list containing species with a height of 1.
     */
    private ArrayList<ArrayList<Species>> speciesByHeight(Species root) {
        int height = root.getHeight();
        // Initialize the array list of array lists
        ArrayList<ArrayList<Species>> speciesAtAllHeights =
            new ArrayList<ArrayList<Species>>(height);
        // Initialize the array lists of species
        for (int i = 0; i < height; i++) {
            ArrayList<Species> speciesAtThisHeight = new ArrayList<Species>();
            speciesAtAllHeights.add(speciesAtThisHeight);
        }
        // Adds the root to its appropriate array list
        speciesAtAllHeights.get(height - 1).add(root);

        Species[] descendants = root.getDescendants();
        // This calls the overloaded, recursive function for each descendant and populates the
        // array lists with species of the appopriate height.
        for (Species descendant : descendants) {
            speciesByHeight(descendant, speciesAtAllHeights);
        }

        return speciesAtAllHeights;
    }

    /**
     * A recursive method that classifies an ancestor, and all its descendants, by their height 
     * and appends them onto the appropriate array list (index of height - 1).
     * @param ancestor An ancestor species.
     * @param ArrayList<ArrayList<Species>> An array list of array lists of species that have species
     *      separated based on their height.
     *      Ex. index<0> = array list containing species with a height of 1
     */
    private void speciesByHeight(Species ancestor,
        ArrayList<ArrayList<Species>> speciesAtAllHeights) {
        int height = ancestor.getHeight();
        speciesAtAllHeights.get(height - 1).add(ancestor);

        Species[] descendants = ancestor.getDescendants();
        if (height == 1) {
            return;
        } else {
            for (Species descendant : descendants) {
                speciesByHeight(descendant, speciesAtAllHeights);
            }
        }
    }

    /**
     * This method inserts a string into a char array and centers it in at a specified index in a charArr.
     * TODO: Possible solution for when names get too long/tree gets too big, throw exception in this method
     *      and implement some dynamic programming (maybe back tracking??) to re-adjust how far apart species are on the charArr
     * @param stringToInsert A string to insert into the char array.
     * @param currentRow The row to insert the string.
     * @param centerOfWordLocation The column to center the string around.
     * @param charArr The char array to be inserted into.
     */
    private void insertStringIntoCharArr(String stringToInsert, int currentRow,
        int centerOfWordLocation, char[][] charArr) {
        int lengthOfString = stringToInsert.length();
        int actualLocationOfStrInsert = centerOfWordLocation - (lengthOfString / 2);

        if (actualLocationOfStrInsert < 0) {
            actualLocationOfStrInsert = 0;
        }

        for (int i = 0; i < lengthOfString; i++) {
            // This is where we would detect and throw an overlap exception
            int locationForCharInsert = actualLocationOfStrInsert + i;
            charArr[currentRow][locationForCharInsert] = stringToInsert.charAt(i);
        }

        return;
    }

    /**
     * This method inserts a species object into a char array.
     * The location of the species on the char array will have already been determined
     * and stored as a field in the species object.
     * @param speciesToInsert The species to insert.
     * @param charArr The char array to insert into.
     */
    private void insertSpeciesIntoCharArr(Species species, char[][] charArr) {
        int rowLocation = species.getLocationOnTree()[0];
        int columnLocation = species.getLocationOnTree()[1];

        insertStringIntoCharArr(species.getName(), rowLocation, columnLocation, charArr);
        return;
    }

    /**
     * This is a recursive method that inserts all descendants of a species into a specified char array.
     * Algorithm:
     *      First, we get the references to each descendant, as well as their height.
     *      Second, the location of each descendant is determined by a series of mathematical formulas and
     *      stored as an instance member variable in each species object.
     *      Third, the species are inserted into the character array.
     *      Fourth, branches are created that link the ancestor to the descendant
     *      Fifth, if the descendants have a height value not equal to 1 (height valuesare always 1 or higher)
     *      then the method calls upon itself.
     *      
     * @param ancestor The ancestor whose descendants should be placed onto the tree
     * @param charArr The char array that descendants are inserted into
     */
    private void insertDescendantsIntoCharArr(Species ancestor, char[][] charArr) {
        Species leftDescendant = ancestor.getDescendants()[0];
        Species rightDescendant = ancestor.getDescendants()[1];

        int heightOfLeft = leftDescendant.getHeight();
        int heightOfRight = rightDescendant.getHeight();

        int heightDifferenceOfL = ancestor.getHeight() - heightOfLeft;
        int heightDifferenceOfR = ancestor.getHeight() - heightOfRight;
        // Accounts for any empty height ancestor nodes, these move column by 1/2 ancestor name
        // length
        int columnAmountOfEmptyHeightsL =
            (heightDifferenceOfL) * (int) (.5 * ancestor.getName().length());
        int columnAmountOfEmptyHeightsR =
            (heightDifferenceOfR) * (int) (.5 * ancestor.getName().length());

        // Branch length + 1 is used as species are present every branch length +1 rows
        int rowLocationOfLeft = charArr.length - ((this.branchLength + 1) * heightOfLeft);
        // Subtract the column displacement caused by branch insertion from the ancestor column
        // location to get descendant location
        int columnLocationOfLeft = ancestor.getLocationOnTree()[1]
            - (columnAmountOfEmptyHeightsL + (heightDifferenceOfL * this.branchLength));
        // Fine adjustment
        for (int i = 0; i < heightDifferenceOfL / 2; i++) {
            columnLocationOfLeft--;
        }
        // Branch length + 1 is used as species are present every branch length +1 rows
        int rowLocationOfRight = charArr.length - ((this.branchLength + 1) * heightOfRight);
        // Add the column displacement caused by branch insertion from the ancestor column location
        // to get descendant location
        int columnLocationOfRight = ancestor.getLocationOnTree()[1]
            + (columnAmountOfEmptyHeightsR + (heightDifferenceOfR * this.branchLength));
        // Fine adjustments
        for (int i = 0; i < heightDifferenceOfR / 2; i++) {
            columnLocationOfRight++;
        }

        leftDescendant.setLocationOnTree(rowLocationOfLeft, columnLocationOfLeft);
        rightDescendant.setLocationOnTree(rowLocationOfRight, columnLocationOfRight);


        insertSpeciesIntoCharArr(leftDescendant, charArr);
        insertSpeciesIntoCharArr(rightDescendant, charArr);

        if (this.showParsimony) {
            this.insertParsimonyBranch(ancestor, leftDescendant, charArr);
            this.insertParsimonyBranch(ancestor, rightDescendant, charArr);
        } else {
            this.insertBranch(ancestor, leftDescendant, charArr);
            this.insertBranch(ancestor, rightDescendant, charArr);
        }
        if (leftDescendant.getHeight() != 1) {
            this.insertDescendantsIntoCharArr(leftDescendant, charArr);
        }

        if (rightDescendant.getHeight() != 1) {
            this.insertDescendantsIntoCharArr(rightDescendant, charArr);
        }
    }

    /**TODO Update this method header
     * This method inserts a branch between an ancestor species and a descendant species.
     * It does so in a top down perspective. If the difference in heights of the ancestor and
     * descendant is greater than 1 then a series of '_' characters are added to the char array
     * at heights where an ancestor node could have been. The reasoning for this is to prevent
     * overlap of the various tree characters (such as overlapping ancestor species names).
     * @param ancestor The ancestor species where the branch will start.
     * @param descendant The descendant species where the branch will end.
     * @param charArr The char array that the branch is inserted into.
     */

    private void insertParsimonyBranch(Species ancestor, Species descendant, char[][] charArr) {
        int rowLocationOfAncestor = ancestor.getLocationOnTree()[0];
        int columnLocationOfAncestor = ancestor.getLocationOnTree()[1];
        int rowLocationOfDescendant = descendant.getLocationOnTree()[0];
        int columnLocationOfDescendant = descendant.getLocationOnTree()[1];

        int differenceOfColumns = columnLocationOfAncestor - columnLocationOfDescendant;
        int midPointOfRow = (int) ((rowLocationOfDescendant - rowLocationOfAncestor) / 2);

        int counter = 0;
        // Right Descendant
        if (differenceOfColumns < 0) {
            int j = columnLocationOfAncestor;
            for (int i = rowLocationOfAncestor + 1; i < rowLocationOfDescendant; i++) {
                charArr[i][j + 1] = '\\';
                j++;
                // Inserts parsimony score
                if (i - rowLocationOfAncestor == midPointOfRow
                    && ancestor.getParsimonyScore(descendant) > 0) {
                    // Need to do something different if midPoint falls upon a series of '_'

                    if ((counter + 1) % (this.branchLength + 1) == 0) {
                        charArr[i][j + 1 + (ancestor.getName().length() / 2)] = ' ';
                        charArr[i][j + 2 + (ancestor.getName().length() / 2)] = 'P';
                        charArr[i][j + 3 + (ancestor.getName().length() / 2)] = '+';
                        charArr[i][j + 4 + (ancestor.getName().length() / 2)] = '=';
                        charArr[i][j + 5 + (ancestor.getName().length() / 2)] = Character.forDigit(
                            ancestor.getParsimonyScore(descendant), ancestor.getTraits().length);
                    } else {
                        charArr[i][j + 1] = ' ';
                        charArr[i][j + 2] = 'P';
                        charArr[i][j + 3] = '+';
                        charArr[i][j + 4] = '=';
                        charArr[i][j + 5] = Character.forDigit(
                            ancestor.getParsimonyScore(descendant), ancestor.getTraits().length);
                    }
                }
                counter++;
                if (counter % (this.branchLength + 1) == 0) {
                    for (int k = 0; k < (ancestor.getName().length() / 2); k++) {
                        charArr[i][j + 1] = '_';
                        j++;
                    }
                    charArr[i][j + 1] = '_';
                    j++;
                }
            }
        // Left Descendant
        } else if (differenceOfColumns > 0) {
            int j = columnLocationOfAncestor;
            for (int i = rowLocationOfAncestor + 1; i < rowLocationOfDescendant; i++) {
                charArr[i][j - 1] = '/';
                j--;
                // Inserts parsimony score changes on branches
                if (i - rowLocationOfAncestor == midPointOfRow
                    && ancestor.getParsimonyScore(descendant) > 0) {
                    // Need to do something different if midPoint falls upon a series of '_'

                    if ((counter + 1) % (this.branchLength + 1) == 0) {
                        charArr[i][j - 1 - (ancestor.getName().length() / 2)] = ' ';
                        charArr[i][j - 2 - (ancestor.getName().length() / 2)] = Character.forDigit(
                            ancestor.getParsimonyScore(descendant), ancestor.getTraits().length);
                        charArr[i][j - 3 - (ancestor.getName().length() / 2)] = '=';
                        charArr[i][j - 4 - (ancestor.getName().length() / 2)] = '+';
                        charArr[i][j - 5 - (ancestor.getName().length() / 2)] = 'P';
                    } else {
                        charArr[i][j - 1] = ' ';
                        charArr[i][j - 2] = Character.forDigit(
                            ancestor.getParsimonyScore(descendant), ancestor.getTraits().length);
                        charArr[i][j - 3] = '=';
                        charArr[i][j - 4] = '+';
                        charArr[i][j - 5] = 'P';
                    }
                }
                counter++;
                if (counter % (this.branchLength + 1) == 0) {
                    for (int k = 0; k < (ancestor.getName().length() / 2); k++) {
                        charArr[i][j - 1] = '_';
                        j--;
                    }
                    charArr[i][j - 1] = '_';
                    j--;
                }
            }
        }
    }

    /**
     * This method inserts a branch between an ancestor species and a descendant species.
     * It does so in a top down perspective. If the difference in heights of the ancestor and
     * descendant is greater than 1 then a series of '_' characters are added to the char array
     * at heights where an ancestor node could have been. The reasoning for this is to prevent
     * overlap of the various tree characters (such as overlapping ancestor species names).
     * @param ancestor The ancestor species where the branch will start.
     * @param descendant The descendant species where the branch will end.
     * @param charArr The char array that the branch is inserted into.
     */

    private void insertBranch(Species ancestor, Species descendant, char[][] charArr) {
        int rowLocationOfAncestor = ancestor.getLocationOnTree()[0];
        int columnLocationOfAncestor = ancestor.getLocationOnTree()[1];
        int rowLocationOfDescendant = descendant.getLocationOnTree()[0];
        int columnLocationOfDescendant = descendant.getLocationOnTree()[1];

        int differenceOfColumns = columnLocationOfAncestor - columnLocationOfDescendant;

        int counter = 0;
        if (differenceOfColumns < 0) {
            int j = columnLocationOfAncestor;
            for (int i = rowLocationOfAncestor + 1; i < rowLocationOfDescendant; i++) {
                charArr[i][j + 1] = '\\';
                j++;
                counter++;
                if (counter % (this.branchLength + 1) == 0) {
                    for (int k = 0; k < (ancestor.getName().length() / 2); k++) {
                        charArr[i][j + 1] = '_';
                        j++;
                    }
                    charArr[i][j + 1] = '_';
                    j++;
                }
            }
        } else if (differenceOfColumns > 0) {
            int j = columnLocationOfAncestor;
            for (int i = rowLocationOfAncestor + 1; i < rowLocationOfDescendant; i++) {
                charArr[i][j - 1] = '/';
                j--;
                counter++;
                if (counter % (this.branchLength + 1) == 0) {
                    for (int k = 0; k < (ancestor.getName().length() / 2); k++) {
                        charArr[i][j - 1] = '_';
                        j--;
                    }
                    charArr[i][j - 1] = '_';
                    j--;
                }
            }
        }
    }

    /**
     * This method inserts the branches pertaining to the root species
     * @param root The root species of the tree
     * @param charArr The char array that the root species is inserted into
     */
    private void insertBranch(Species root, char[][] charArr) {
        int rowLocation = root.getLocationOnTree()[0];
        int columnLocation = root.getLocationOnTree()[1];

        for (int i = 1; i < 4; i++) {
            charArr[rowLocation + i][columnLocation + i] = '\\';
            charArr[rowLocation + i][columnLocation - i] = '/';
        }
    }

    /**
     * This method inserts the height parameter, left aligned, on the string representation of the tree
     * @param height The height of species at this line
     * @param charArr The char array that the height is being inserted into
     */
    private void insertHeight(int height, char[][] charArr) {
        int insertRow = charArr.length - (height * 4);
        String heightAsStr = "Height: " + height;
        insertStringIntoCharArr(heightAsStr, insertRow, 0, charArr);

    }

    /**
     * This method converts an array list of array lists of species to a formatted string.
     * The format for the string is:
     *      Height n:
     *          speciesName: {traits}
     *          speciesName: {traits}
     *      Height n - 1:
     *          speciesName: {traits}
     *      ...
     *      Height 1:
     *          speciesName: {traits}
     *          speciesName: {traits}
     * Traits are the trait states present in the most parsimonious phylogenetic tree.
     * @param speciesByHeight
     * @param heightOfRoot
     * @return
     */
    private String traitStatesToString(ArrayList<ArrayList<Species>> speciesByHeight,
        int heightOfRoot) {
        String outputStr = "";

        for (int i = heightOfRoot - 1; i >= 0; i--) {
            outputStr += "Height " + (i + 1) + ":\n";
            outputStr += traitStatesAtHeightToStr(speciesByHeight.get(i));
        }

        return outputStr;
    }

    /**
     * Takes an array list correlating to the species at a height on the tree and converts them to a string.
     *      Could have used string writer for this but decided that would complicate things too much.
     * @param speciesAtHeight An array list containing the number of species at a certain height in the tree
     * @return String containing the species and their optimized(for parsimony) trait states
     */
    private String traitStatesAtHeightToStr(ArrayList<Species> speciesAtHeight) {
        String outputStr = "";
        for (Species species : speciesAtHeight) {
            outputStr += "\t" + species.getName() + ": " + intArrToStr(species.getTraits()) + "\n";
        }

        return outputStr;
    }

    /**
     * Prints out a string representation of an array of ints
     * @param intArr An integer array
     * @return String representation of an int array
     */
    private static String intArrToStr(int[] intArr) {
        String intArrAsStr = "{";
        for (int i = 0; i < intArr.length - 1; i++) {
            intArrAsStr += intArr[i] + ",";
        }
        intArrAsStr += intArr[intArr.length - 1] + "}";

        return intArrAsStr;
    }

    /**
     * This accessor method returns the width of the tree AFTER it has been converted to a string.
     * @return int width of the tree
     */
    public int getWidth() {
        return this.width;
    }

    /**
     * This accessor method returns if we have decided to show parsimony.
     * @return boolean true if parsimony is shown, false if parsiomony is not shown.
     */
    public boolean getShowParsimony() {
        return this.showParsimony;
    }
    
    /**
     * This method converts all null characters into ' ' characters so that text editors properly
     * display the charArr when converted to a string.
     * @param charArr An array of an array of chars
     */
    private static void replaceNullChar(char[][] charArr) {
        for(int i = 0; i < charArr.length; i++) {
            for(int j = 0; j < charArr[i].length; j++) {
                if(charArr[i][j] == 0) {
                    charArr[i][j] = ' ';
                }
            }
        }
    }

}
