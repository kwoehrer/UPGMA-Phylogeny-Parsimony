package phylogenetics;

import java.util.ArrayList;

public class TestMethods {
    // Will I be able to
    public static void main(String[] args) {
         //testSpecies();
        // testSpeciesNode();
        // testAncestorNode();
        testRootedTree();
    }

    public static void testSpecies() {
        // testCases for possibleStates()
        // testCase 1;
        {
            Species descendant1 = new Species();
            Species descendant2 = new Species();

            int[] traits1 = {1, 0, 0, 0, 1};
            int[] traits2 = {1, 1, 1, 0, 0};

            descendant1.setTraits(traits1);
            descendant2.setTraits(traits2);

            Species ancestor = new Species();
            ancestor.setDescendantSpecies(descendant1, descendant2);

            ancestor.setPossibleTraitStates();
            int[][] possibleTraitStates = ancestor.getPossibleTraitStates();
            System.out.println(
                "Expected:\n1)1 0 0 0 0 \n2)1 1 0 0 0 \n3)1 0 1 0 0 \n4)1 1 1 0 0 \n5)1 0 0 0 1 \n6)1 1 0 0 1 \n7)1 0 1 0 1 \n8)1 1 1 0 1 ");

            System.out.println("Actual:");
            for (int i = 0; i < possibleTraitStates.length; i++) {
                System.out.print(i + 1 + ")");
                for (int j = 0; j < possibleTraitStates[i].length; j++) {
                    System.out.print(possibleTraitStates[i][j] + " ");
                }
                System.out.println("");
            }
        }
    }

    public static void testRootedTree() {
        //testCase1 check to see if code works at all
        //Will need to break this up into multiple sections for readability
        {   
            int[] traits1 = new int[]{1, 0, 0, 0, 1};
            Species species1 = new Species(traits1,true);
            species1.setName("A");
            
            int[] traits2 = new int[]{1, 1, 1, 0, 0};
            Species species2 = new Species(traits2,true);
            species2.setName("B");
            
            int[] traits3 = new int[]{1, 0, 1, 0, 1};
            Species species3 = new Species(traits3,true);
            species3.setName("C");
            
            int[] traits4 = new int[]{1, 1, 0, 0, 1};
            Species species4 = new Species(traits4,true);
            species4.setName("D");
            
            
            int[] traits5 = new int[]{1, 1, 0, 1, 1};
            Species species5 = new Species(traits5,true);
            species5.setName("E");
            
            int[] traits6 = new int[]{1, 0, 0, 1, 1};
            Species species6 = new Species(traits6,true);
            species6.setName("F");
            
            int[] traits7 = new int[]{0, 0, 0, 0, 1};
            Species species7 = new Species(traits7,true);
            species7.setName("G");
        
            Species[] speciesArr = new Species[7];
            speciesArr[0] = species1;
            speciesArr[1] = species2;
            speciesArr[2] = species3;
            speciesArr[3] = species4;
            speciesArr[4] = species5;
            speciesArr[5] = species6;
            speciesArr[6] = species7;
            
            
            
            
            RootedTree tree = new RootedTree(speciesArr);
            System.out.println(speciesArr[0].getAncestor());
            
            System.out.println("\nTest to ensure proper passage of data from entry array to tree\nExpected Size:" + speciesArr.length);
            ArrayList<Species> speciesList = tree.getInitialSpecies();
            System.out.println("Actual Size:" + speciesList.size() + "\n");
            
            System.out.println("Test to ensure proper passage of species data from entry array to tree\nExpected species data:");
            
            for(int i = 0; i < speciesArr.length;i++) {
                System.out.print("Species " + speciesArr[i].getName() + " traits:");
                for(int j = 0; j < speciesArr[i].getTraits().length;j++) {
                    System.out.print(speciesArr[i].getTraits()[j] + " ");
                }
                System.out.println("");
            }
            
            System.out.println("Actual species data:");
            for(int i = 0; i < speciesList.size();i++) {
                System.out.print("Species " + speciesList.get(i).getName() + " traits:");
                for(int j = 0; j < speciesList.get(i).getTraits().length;j++) {
                    System.out.print(speciesList.get(i).getTraits()[j] + " ");
                }
                System.out.println("");
            }
            System.out.println("");
            
            System.out.println("Expected Height of Tree: 5");
            System.out.println("Actual Height of Tree: " + tree.getRoot().getHeight() + "\n");
            
            System.out.println("Size:" + tree.getSpeciesList().size());
            
            System.out.println(tree.getRoot().getName());
            
            System.out.println(tree);
            
            
            System.out.println(tree.size());
            
            tree.setShowParsimony();
            
            System.out.println("\nTest to check possible traitStates");
            Species rightDescendantOfRoot = tree.getRoot().getDescendants()[1];
            
            System.out.println("Species Name: "+ rightDescendantOfRoot.getName());
            
            int[][] possTraitStates = rightDescendantOfRoot.getPossibleTraitStates();
            
            System.out.println("Template traits for this species:");
            for(int traits: rightDescendantOfRoot.getTraits()) {
                System.out.print(traits+  " ,");
            }
            
            System.out.println("");
            
            for(int i = 0; i < possTraitStates.length; i++) {
                System.out.print("Possible Traitstate #" + i + "{");
                for(int j = 0; j < possTraitStates[i].length; j++) {
                    System.out.print(possTraitStates[i][j]);
                }
                System.out.println("}");
            }
            
            
            System.out.println("\n\n\n Tests for showing trait values in tree");
            
            System.out.println(tree);
            
            System.out.println("\n\n\n Tests for parsimony functions");
            //Test to see if binary iterator is working
            
            System.out.println(Parsimony.findMostParsimoniousTree(tree));
          
            System.out.println(tree);
        }
        
        //testCase2 test to see if distance matrix is functioning (print out distance matrix for each step)
        {
            
        }
    }

}
