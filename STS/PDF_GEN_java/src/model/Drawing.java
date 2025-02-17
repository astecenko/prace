package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;

public class Drawing {
  private PDDocument vykresOrigDoc;
  
  public static final int A4 = 1;
  
  public static final int A3 = 2;
  
  public static final int A2 = 3;
  
  public static final int A1 = 4;
  
  public static final int A0 = 5;
  
  public static final int LEFT_TOP_CORNER = 1;
  
  public static final int CENTER_TOP = 2;
  
  public static final int RIGHT_TOP_CORNER = 3;
  
  public static final int LEFT_CENTER = 4;
  
  public static final int RIGHT_CENTER = 5;
  
  public static final int LEFT_BOTTOM_CORNER = 6;
  
  public static final int CENTER_BOTTOM = 7;
  
  public static final int RIGHT_BOTTOM_CORNER = 8;
  
  public static final int CUSTOM = 9;
  
  public static final int CENTER_PAGE = 10;
  
  public int xText;
  public	int yText;
  public int xText_ks;
  
  
  private int Getformat(PDDocument vykresOrigDoc) {
    int format;
    PDPage vykresOrig = vykresOrigDoc.getDocumentCatalog().getPages().get(0);
    float pageWidth = vykresOrig.getMediaBox().getWidth();
    float pageHeight = vykresOrig.getMediaBox().getHeight();
    int pageWidthMM = Math.round(pageWidth * 25.4F / 72.0F);
    int pageHeightMM = Math.round(pageHeight * 25.4F / 72.0F);
    int rotation = vykresOrig.getRotation();
    if (pageWidthMM == 210 && pageHeightMM == 297) {
      format = 11;
    } else if (pageWidthMM == 297 && pageHeightMM == 210) {
      format = 12;
    } else if (pageWidthMM == 420 && pageHeightMM == 297) {
      format = 22;
    } else if (pageWidthMM == 297 && pageHeightMM == 420) {
      format = 21;
    } else if (pageWidthMM == 594 && pageHeightMM == 420) {
      format = 32;
    } else if (pageWidthMM == 420 && pageHeightMM == 594) {
      format = 31;
    } else if (pageWidthMM == 841 && pageHeightMM == 594) {
      format = 42;
    } else if (pageWidthMM == 594 && pageHeightMM == 841) {
      format = 41;
    } else if (pageWidthMM == 1189 && pageHeightMM == 841) {
      format = 52;
    } else if (pageWidthMM == 841 && pageHeightMM == 1189) {
      format = 51;
    } else {
      format = 5;
    } 
    return format;
  }
  
  private PDDocument ChangeFormat(File partFile, String ghostScriptPath, PDDocument vykresOrigDoc) throws IOException, InterruptedException {
    Runtime rt = Runtime.getRuntime();
    File newFile = new File(String.valueOf(partFile.getParent()) + "\\" + partFile.getName().substring(0, partFile.getName().indexOf(".")) + "_r.pdf");
    Process pr = rt.exec("\"" + ghostScriptPath + "\" -o " + newFile.getAbsolutePath() + " -sDEVICE=pdfwrite -sPAPERSIZE=a3 -dFIXEDMEDIA -dPDFFitPage -dCompatibilityLevel=1.4 " + partFile.getAbsolutePath());
    BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));
    String line = null;
    while ((line = input.readLine()) != null)
      System.out.println(line); 
    int exitVal = pr.waitFor();
    System.out.println("Exited with error code " + exitVal);
    PDDocument vykresOrigDoc_new = PDDocument.load(newFile);
    return vykresOrigDoc_new;
  }
  
  private void PasteStamp(int format, int position, float pageHeight, float pageWidth, float coordinatesTop, float coordinatesLeft) {
    int pageOffsetX, pageOffsetY, yText_ks = 0;
    switch (format) {
      case 1:
        pageOffsetX = 35;
        pageOffsetY = 55;
        break;
      case 2:
        pageOffsetX = 50;
        pageOffsetY = 60;
        break;
      default:
        pageOffsetX = 35;
        pageOffsetY = 50;
        break;
    } 
    
	
	switch (position) {
      case 1:
        xText = pageOffsetX;
        yText = (int)pageHeight - pageOffsetY;
        yText_ks = yText - 20;
        xText_ks = xText;
        return;
      case 4:
        xText = pageOffsetX;
        yText = (int)pageHeight / 2;
        yText_ks = yText - 20;
        xText_ks = xText;
        return;
      case 6:
        xText = pageOffsetX;
        yText = pageOffsetY + 20;
        yText_ks = yText - 20;
        xText_ks = xText;
        return;
      case 2:
        xText = (int)pageWidth / 2;
        yText = (int)pageHeight - pageOffsetY;
        yText_ks = yText - 20;
        xText_ks = xText;
        return;
      case 7:
        xText = (int)pageWidth / 2;
        yText = pageOffsetY;
        yText_ks = yText - 20;
        xText_ks = xText;
        return;
      case 3:
        xText = (int)pageWidth - pageOffsetX + 100;
        yText = (int)pageHeight - pageOffsetY;
        yText_ks = yText - 20;
        xText_ks = xText;
        return;
      case 5:
        xText = (int)pageWidth - pageOffsetX + 100;
        yText = (int)pageHeight / 2;
        yText_ks = yText - 20;
        xText_ks = xText;
        return;
      case 8:
        xText = (int)pageWidth - pageOffsetX + 100;
        yText = pageOffsetY;
        yText_ks = yText - 20;
        xText_ks = xText;
        return;
      case 10:
        xText = (int)pageWidth / 2;
        yText = (int)pageHeight / 2 - pageOffsetY;
        yText_ks = yText - 20;
        xText_ks = xText;
        return;
      case 9:
        xText = (int)Math.round((coordinatesLeft * 72.0F) / 25.4D);
        yText = (int)Math.round(pageHeight - (coordinatesTop * 72.0F) / 25.4D);
        yText_ks = yText - 20;
        xText_ks = xText;
        return;
    } 
    int xText = pageOffsetX;
    int yText = (int)pageHeight - pageOffsetY;
    yText_ks = yText - 20;
    int xText_ks = xText;
  }
}
