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

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

public class Output {
    
    public static boolean writeTreeToFile(String fileName, RootedTree tree, String[] descendantGlossary) {
        boolean success = false;
        File outputFile = new File(fileName + ".txt");
        PrintWriter writer = null;
       
        try{
            writer = new PrintWriter(outputFile);
            writer.println(tree);
            writer.println(multipleCharsToString('-',tree.getWidth()));
            String speciesGlossary = "Species Glossary";
            writer.println(multipleCharsToString(' ',(tree.getWidth()-speciesGlossary.length())/2) + speciesGlossary);
            writer.print(strArrtoStr(descendantGlossary));
            
            success = true;
        } catch (IOException e){
            System.out.println("Unable to write file to designated file name.");
        } finally {
            if(writer != null) {
                writer.close();
            }
        }
        
        return success;
    }
    /**
     * This method concatenates an array of strings into a single string.
     * The elements of the string array will be separated by a new line character.
     * @param strArr
     * @return
     */
    private static String strArrtoStr(String[] strArr) {
        String combinedStr = "";
        for(int i = 0; i < strArr.length; i++) {
            combinedStr += strArr[i] + "\n";
        }
        
        return combinedStr;
    }
    
    /**
     * This method creates a string that contains a specified char a specified number of times;
     * @param charToConcetenate
     * @param numberToConcetenate
     * @return
     */
    public static String multipleCharsToString(char charToConcetenate, int numberToConcetenate) {
        String multipleCharLine = "";
        for (int i = 0; i < numberToConcetenate; i++) {
            multipleCharLine += charToConcetenate;
        }
        return multipleCharLine;
    }
}
