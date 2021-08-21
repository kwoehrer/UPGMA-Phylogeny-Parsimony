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
//A brute-force approach will likely still be required in order construct
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

import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.IOException;

public class Input {

    /**
     * This method creates an array of species object from a user.txt file. 
     * @param args
     * @return
     */
    public static Species[] transposeFile(String[] args) {
        System.out.println("Transposing Input File...");
        Species[] descendants = null;
        Scanner scnr = null;
        File file = new File(args[0]);
        int numberOfSpecies = 0;
        int numberOfTraits = 0;
        Scanner bufferScnr = null;

        try {
            scnr = new Scanner(file);

            if (scnr.hasNextLine()) {
                String firstLineOfInput = scnr.nextLine();

                bufferScnr = new Scanner(firstLineOfInput);

                if (bufferScnr.hasNextInt()) {
                    numberOfSpecies = bufferScnr.nextInt();
                    System.out.println("\tAcquired number of species.");
                } else {
                    throw new Exception(
                        "The first line should contain the following: \" numberOfSpecies numberOfTraitsInSpecies\""
                            + "\n The program detected that either a non-integer was entered or no tokens were present");
                }

                if (bufferScnr.hasNextInt()) {
                    numberOfTraits = bufferScnr.nextInt();
                    System.out.println("\tAcquired number of comparable traits.");
                } else {
                    throw new Exception(
                        "The first line should contain the following: \" numberOfSpecies numberOfTraitsInSpecies\""
                            + "\n The program detected that either a non-integer was entered or no integer was present for the number"
                            + "of traits.");
                }

                if (bufferScnr.hasNext()) {
                    throw new Exception(
                        "First line should only contain the following: \"numberOfSpecies numberOfTraits\"");
                }
                bufferScnr.close();
                // Initializes the descendant array
                descendants = new Species[numberOfSpecies];
            } else {
                System.out.println("File is empty");
            }


            int indexOfDescendantArr = 0;
            System.out.println("\tTransposing species names and traits...");
            while (scnr.hasNextLine()) {
                int[] traitArr = new int[numberOfTraits];
                String name;

                if (indexOfDescendantArr >= descendants.length) {
                    throw new Exception(
                        "There are more species, or more lines entered, on the text document than specified"
                            + " by the user.");
                }

                if (scnr.hasNextInt()) {
                    throw new Exception(
                        "The first token entered on each line should be the species name. Cannot be an integer.");
                }
                name = scnr.next();

                try {
                    // Used this in case extra traits are entered or not enough traits are entered
                    // for each species. Could have used String.split and Integer.parseInt() methods
                    // but that wouldn't allow for as precise error generation.
                    for (int i = 0; i < numberOfTraits; i++) {
                        if (scnr.hasNextInt()) {
                            traitArr[i] = scnr.nextInt();
                            // Check to make sure its a 1 or 2
                            if (traitArr[i] != 1 && traitArr[i] != 0) {
                                throw new Exception(
                                    "Only 0's (trait not present) or 1's (trait is present) should"
                                        + "be entered in for trait values.\n This error was generated by species \""
                                        + name + "\".");
                            }
                        } else {
                            throw new Exception(
                                "Only 0's (trait not present) or 1's (trait is present) should"
                                    + "be entered in for trait values.\n Either a non-integer was found entered in species \""
                                    + name + "\", or there were not enough traits in species \""
                                    + name + "\". This species had " + i + " traits entered when this error occured (should have"
                                        + numberOfTraits + " traits present.");
                        }
                    }
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return null;
                }
                // Create new species from the information we parsed and add it to array of
                // descendants
                Species currSpecies = new Species(name, traitArr, true);
                descendants[indexOfDescendantArr] = currSpecies;
                indexOfDescendantArr++;
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found, please check to see if file is in correct location "
                + "and it is spelled correctly.");
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
            return null;
        } finally {
            if (scnr != null) {
                scnr.close();
            }
            if (bufferScnr != null) {
                bufferScnr.close();
            }
        }
        System.out.println("\tDone Transposing Input File!");
        return descendants;
    }

}
