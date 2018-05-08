import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import javax.swing.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class ColorMatcher extends PApplet {

/*
  Authors: Timmothy Lane, Tyler Rook, Jesse Stewart, Drew Waszak
  ColorMatcher.pde: 
  *Allows the user to select and image file from the local system 
      and find the closest matching color from the color database.
  *The color database can be customized by adding or removing images from 
      the Data/Colors folder stored in the same location as the executable.
  
                          INSTRUCTIONS:
   *Follow the intial prompt to select a file from local storage.
   *Click and drag with the mouse to select the desired area
   *Wait for the program to load
   *The program will display the closest color to the median
       color of the selected area.
*/



PImage selectedImage, rimg, currentImg, img;
String fname, startText;
int startX, endX, startY, endY, numberOfColors = 3;
ArrayList<String> closestColors = new ArrayList<String>();
String[] colorNames;


public void setup() {
  //start up the program with the rainbow as the starting image.
  
  fname = "rainbow.png";
  surface.setResizable(true);
  noFill();
  
  //Create the text to display when starting the program
  startText = "Welcome to PaintMatch!\nTo use this program: \n1. Select an image"
      + " you want to match the color to.\n"
      + "2. Click and drag the mouse to select the color you want to match. \n"
      + "3. Wait for the program to tell you the closest matches available.";
  //Show the startup text
  javax.swing.JOptionPane.showMessageDialog(null, startText);
  //Prompt the user to select a file to use
  selectInput("Select an image to process:", "fileSelected");
  selectedImage = loadImage(fname);
  img = loadImage(fname);
  currentImg = selectedImage;
  rectMode(CORNERS);
}


public void draw(){
    surface.setSize(selectedImage.width, selectedImage.height);
    image(currentImg, 0, 0);
    if (mousePressed) {
      rect(startX, startY, mouseX, mouseY);
    }
}


//Function that allows the user to select a locally stored image
public void fileSelected(File selection) {
  if (selection == null) {
    javax.swing.JOptionPane.showMessageDialog(null, "Window was closed or the user hit cancel.");
  } else {
    fname = selection.getAbsolutePath();
    selectedImage = loadImage(fname);
    currentImg = selectedImage;
    surface.setSize(selectedImage.width, selectedImage.height);
  }
}


/*Take two colors and return the difference of the colors
  imageColor is the color from the user selected image
  pColor is the color from the color table
*/
public float getColorDifference(int imageColor, int pColor){
  float diff;
  //Calculate and return the difference between the colors
  float rmean =(red(imageColor) + red(pColor)) / 2;
  float r = red(imageColor) - red(pColor);
  float g = green(imageColor) - green(pColor);
  float b = blue(imageColor) - blue(pColor);
  diff =  sqrt((PApplet.parseInt(((512+rmean)*r*r))>>8)+(4*g*g)+(PApplet.parseInt(((767-rmean)*b*b))>>8)); 
  return diff;
}

// Scan the available lists and return the correct index
// to find what index it should be placed at.
public int getDiffOrder(float diff, ArrayList<Float> diffs) {
  int index = 0;
  boolean done = false;
  for (int i = 0; i < diffs.size(); i++) {
    if (diffs.get(i) > diff) {
      index = i;
      i = diffs.size();
      done = true;
    }
  }
  // Handle the case where the color is the absolute most different.
  if (!done) {
    index = diffs.size();
  }
  return index;
}

public PImage getColor(PImage img1, int sx, int sy, int ex, int ey) {
  PImage target = img1.get();
  PImage fileImage; 
  float diff;
  String filePath;
  
  // Calculate Median Color
  float rSum = 0;
  float gSum = 0;
  float bSum = 0;
  float total = 0;
  for (int y = sy; y < ey; y++) {
    for (int x = sx; x < ex; x++) {
      //insert code here to get the median of the area
      int c = target.get(x, y);
      rSum = rSum + red(c);
      gSum = gSum + green(c);
      bSum = bSum + blue(c);
      total = total + 1;
    }
  }
  
  // Return Median Color
  int imageColor = color(rSum/total, gSum/total, bSum/total);
  
  java.io.File folder = new java.io.File(dataPath("Colors"));
  String[] colorNames = folder.list();
  
  /*Give the initial values for closestColors
    This is just to let us compare to other colors*/
  ArrayList<Float> diffs = new ArrayList<Float>();
  for (int i = 0; i < numberOfColors; i++){
   closestColors.add(i, colorNames[i]);
  }
  
  // Calculate differences and order colors.
  for (int i = 0; i < colorNames.length; i++) {
    filePath = "Colors/"+colorNames[i];
    fileImage = loadImage(filePath);
    int pColor = fileImage.get(1,1);
    diff = getColorDifference(imageColor, pColor);
    
    // Find the index to put the current color at.
    int index = getDiffOrder(diff, diffs);
    diffs.add(index, diff);
    closestColors.add(index, colorNames[i]);
  }
  
  // Give the user feedback of closest colors.
  String colorText = "The closest colors are:";
  for (int i = 1; i <= numberOfColors; i++){
    //Remove the file extensions before outputting to the user
    String[] arrayUsedToRemoveFileExtension = splitTokens(closestColors.get(i-1), ".");
    colorText = colorText + "\n" + i + ". " + arrayUsedToRemoveFileExtension[0];
  }
  
  //Message box displaying the closest colors
  javax.swing.JOptionPane.showMessageDialog(null, colorText);
  return target;
}


public void mousePressed()
{
  startX = mouseX;
  startY = mouseY;
}


public void mouseReleased() {
  int endX, endY;
  if (mouseX < startX) {
    endX = startX;
    startX = mouseX;
  } else {
    endX = mouseX;
  }
  if (mouseY < startY) {
    endY = startY;
    startY = mouseY;
  } else {
    endY = mouseY;
  }
  rimg = getColor(selectedImage, startX, startY, endX, endY);
  currentImg = rimg;
}
  public void settings() {  size(100,100); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "ColorMatcher" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
