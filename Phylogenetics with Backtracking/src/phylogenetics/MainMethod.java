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
// Reasons for creating this program:
// 1. I remember taking microbiology-470 at UW-Madison and one of our big
// projects for the semester was to create the most parsiminious
// phylogenetic tree of 20-25 randomly named species when we were only given
// the presence of various traits (I remember it being close to 10-12).
// Everyone in the course spent hours upon hours manually crafting their
// trees and I remember thinking to myself how nice it would be if a
// computer could help compute the optimal state for parsimony.
// Unfortunately, almost all the programs found online either costed money
// or their algorithms were faulty and yielded trees that were close to
// the most parsiminious tree but not quite there. Now that I am pursuing
// a career in computer science and developement, I have decided to solve
// this problem on my own and offer it as a free resource online.
// 2. Gain a greater understanding of binary trees and their algorithms
// 3. Practice my skills learned from my coursework in computer science.
// Things to improve/TODO:
// 1. Numerous points in the program could be better optimized to run a
// little bit faster. These are noted locally when they occur.
// 2. When determining the most parsiminious trait states, I currently use
// a brute-force approach. Although I narrow down the number of computations
// by determining traits of ancestors we know for certain, I believe there
// are additional ways I can cut down on the number of computations and speed
// the program up.
// A brute-force approach will likely still be required in order construct
// the most parsiminious phylogenetic tree accurately.
//
//
///////////////////////////////// CITATIONS ////////////////////////////////////
//
// Source or Recipient; Description
// UW-Madison Microbiology-470
// This class taught me how to do UPGMA based phylogeny as part of my course work.
//
/////////////////////////////// 80 COLUMNS WIDE /////////////////////////////////**

import java.util.Scanner;

/**
 * This class contains the main method for the command line interface.
 * 
 * @author Krischan Woehrer
 *
 */
public class MainMethod {

    public static void main(String[] args) {
        // Get Input from user.
        if (args.length != 1) {
            System.out.println("Proper usage: MainMethod fileContainingTraitValues.txt");
            return;
        }

        Scanner scnr = new Scanner(System.in);

        Species[] descendantArr = Input.transposeFile(args);

        if (descendantArr == null) {
            System.out.println("Failure to transpose input file. Exiting program now.");
            scnr.close();
            return;
        }

        // Working with the array
        // Creates codenames for species and a glossary to determine what is what.
        String[] speciesGlossary = new String[descendantArr.length];
        for (int i = 0; i < descendantArr.length; i++) {
            String speciesCode = "" + (char) (i + 65);
            speciesGlossary[i] = speciesCode + ": " + descendantArr[i].getName();
            descendantArr[i].setName(speciesCode);
        }

        // Create tree
        RootedTree tree = new RootedTree(descendantArr);
        // Ask user if they would like to have trait values displayed

        System.out.println(
            "Would you like to display traits of each ancestor" + " as well as parsimony? Y or N");
        String showParsimony = scnr.next();
        if (showParsimony.equalsIgnoreCase("Y")) {
            System.out.println(
                "Calculating the optimal trait states for parsimony. This may take a few minutes to a few hours.\n"
                    + "Please do not exit as nothing will be saved to output.\nNot displaying parsimony drastically"
                    + " reduces runtime.\n"
                    + "If there are an excessive number of species and/or traits then the program could take"
                    + " an extraordinary amount of time. (Perhaps try running the program overnight in that case)\n");
            tree.setShowParsimony();
            Parsimony.findMostParsimoniousTree(tree);
        }
        
     // Output methods
        boolean isOutputComplete = false;
        String fileName = "";
        do {
            System.out.println(
                "What would you like to name the output file?\n\t DO NOT enter file extension type!");
            fileName = scnr.next();
            if (!Output.writeTreeToFile(fileName, tree, speciesGlossary)) {
                System.out.println("Would you like to enter a different output file name?"
                    + "If you would like to quit, enter \"q\".");
                if(scnr.next().equalsIgnoreCase("q")) {
                   isOutputComplete = true; 
                }
            } else {
                isOutputComplete = true;
            }
        } while (!isOutputComplete);

        scnr.close();
    }

}
