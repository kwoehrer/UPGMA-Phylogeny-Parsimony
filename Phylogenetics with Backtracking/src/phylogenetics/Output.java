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

/**
 * This class is responsible for writing the output file.
 * @author Krischan Woehrer 
 *
 */
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
        
        System.out.println("Complete. Your file is named \"" + fileName
            + ".txt\". Please check for your file at " + outputFile.getAbsolutePath());
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
