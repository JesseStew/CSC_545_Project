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

public class Project1 extends PApplet {

/*
  
*/



PImage selectedImage, rimg, currentImg, img;
//PImage img;
String fname, startText;
int startX, endX, startY, endY, numberOfColors = 3;
String[] closestColors = new String[numberOfColors], colorNames;


public void setup() {
  //start up the program with the mona as the starting image.
  
  fname = "mona.jpg";
  surface.setResizable(true);
  noFill();
  
  //Create the text to display when starting the program
  startText = "Welcome to PaintMatch!\nTo use this program: \n1. Select an image"
      + " you want to match the color to.\n"
      + "2. Click and drag the mouse to select the color you want to match. \n"
      + "3. Wait for the program to tell you the 5 closest matches available.";
  //Show the startup text
  javax.swing.JOptionPane.showMessageDialog(null, startText);
  //Prompt the user to select a file to use
  selectInput("Select a file to process:", "fileSelected");
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


public void fileSelected(File selection) {
  if (selection == null) {
    println("Window was closed or the user hit cancel.");
  } else {
    println("User selected " + selection.getAbsolutePath());
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



public PImage getColor(PImage img1, int sx, int sy, int ex, int ey){
  PImage target = img1.get();
  PImage fileImage; 
  float diff;
  String filePath;
  /*
  
  //loop trough the selected area
  for (int y = sy; y < ey; y++) {
    for (int x = sx; x < ex; x++) {
      //insert code here to get the median of the area
      target.set(x, y, color(0,0,0));
    }
  }*/
  
  int imageColor = img1.get(sx,sy);
  
  java.io.File folder = new java.io.File(dataPath("Colors"));
  String[] colorNames = folder.list();
  
  /*Give the initial values for closestColors
    This is just to let us compare to other colors*/
  for (int i = 0; i < numberOfColors; i++){
   closestColors[i] = colorNames[i];
  }
  
  for (int i = 0; i < colorNames.length; i++) {
    println(colorNames[i]);
    filePath = "Colors/"+colorNames[i];
    fileImage = loadImage(filePath);
    int pColor = fileImage.get(1,1);
    diff = getColorDifference(imageColor, pColor);
    println(diff);
    
  }
  
  String colorText = "The closest colors are:";
  for (int i = 1; i <= numberOfColors; i++){
    colorText = colorText + "\n" + i + ". " + closestColors[i-1];
  }
  //println(colorText);
  javax.swing.JOptionPane.showMessageDialog(null, colorText);
  return target;
}



public void mousePressed()
{
  //rimg = loadImage(fname);
  startX = mouseX;
  startY = mouseY;
  //currentImg = img;
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



  /*Section for reference
  // we'll have a look in the data folder
java.io.File folder = new java.io.File(dataPath("Colors"));
 
// list the files in the data folder
String[] colorNames = folder.list();
 
// get and display the number of jpg files
println(colorNames.length + " jpg files in specified directory");
 
// display the filenames
for (int i = 0; i < colorNames.length; i++) {
  println(colorNames[i]);
}*/
  public void settings() {  size(100,100); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Project1" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
