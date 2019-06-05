/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package quantizer;

import java.io.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.JFrame;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.Raster;
import java.util.Vector;

public class Quantizer {

    static int[][] max = null;
    static int[][] arr = null;
    static int[][] Q_Matrix = null;
    
    static int h;
    static int w;

    public static class table {

        public int first;
        public int second;
        public int q;
        public int q1;

        table() {
        }
        public table(int f, int s, int iq,int iq1) {
        first=f;
        second=s;
        q=iq;
        q1=iq1;
        }
    }
      
    public static ArrayList<Integer> Data_List = new ArrayList<Integer>();   // involves the all numbers of image 
    public static Vector<Integer> Avg_List = new Vector<Integer>();         // all averages number
    public static ArrayList<table> content = new ArrayList<table>();
    public static ArrayList<Integer> Q1_List = new ArrayList<Integer>();
    public static ArrayList<Integer> Q_List = new ArrayList<Integer>();

    public static void readImage(String pathImage) {

        try {
            BufferedImage Image = null;
            File originalImage = new File(pathImage);
            Image = ImageIO.read(originalImage);
            BufferedImage gray = new BufferedImage(Image.getWidth(), Image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);
            Graphics g = Image.createGraphics();
            g.drawImage(Image, 0, 0, null);
            g.dispose();
            arr = new int[Image.getWidth()][Image.getHeight()];
            Raster raster = Image.getData();
            for (int i = 0; i < Image.getWidth(); i++) {
                for (int j = 0; j < Image.getHeight(); j++) {
                    arr[i][j] = raster.getSample(i, j, 0);
                    Data_List.add(arr[i][j]);
                } // internal for loop
            } // external for loop
            h = Image.getHeight();
            w = Image.getWidth();
        } catch (IOException e) {
            System.out.println("error ! ");
        }
    }// end of read function
    public static void Compression(ArrayList<Integer> list, int level) {    // recursion function hyb2y level/2
        int first_avg = 0;
        ArrayList<Integer> list1 = new ArrayList<Integer>();
        ArrayList<Integer> list2 = new ArrayList<Integer>();
        for (int b = 0; b < list.size(); b++) {
            first_avg += list.get(b);
        }
        first_avg /= list.size();

        if (1 >= level) {
            Avg_List.add(first_avg);
            return;
        }

        int avg_p = first_avg + 1;
        int avg_m = first_avg;

        for (int z = 0; z < list.size(); z++) {
            int y = list.get(z);
            if (y > avg_m) {
                list2.add(y);
            } else {
                list1.add(y);
            }
        }

        Compression(list1, level / 2);
        Compression(list2, level / 2);
    }

    public static void Create_Table() {
        int size;
        size = Avg_List.size();
        // table []content=new table[size];
        int[] midpoint = new int[size - 1 + 2];
        midpoint[0] = 0;
        int y = midpoint.length - 1;
        midpoint[y] = 256;
        for (int z = 1; z < midpoint.length - 1; z++) {
            midpoint[z] = (Avg_List.get(z) + Avg_List.get(z - 1)) / 2;
        }
        /*for(int z=1;z<midpoint.length-1;z++)
   {      
         System.out.println( midpoint[z]) ;    
    } */

        for (int a = 0; a < Avg_List.size(); a++) {
            table t = new table();
            t.first = midpoint[a];
            t.second = midpoint[a + 1];
            t.q = a;
            t.q1 = Avg_List.get(a);
            content.add(t);

        }
    }
    
    public static void generate_Q() {
        for(int i=0;i<content.size();i++)
        {
            System.out.println(content.get(i).first+" "+content.get(i).second+" "+content.get(i).q+" "+content.get(i).q1);
        }
         int counter=0;
        Q_List=new ArrayList<Integer>();
        for (int r = 0; r < Data_List.size(); r++) {
            int i = Data_List.get(r);
            for (int p = 0; p < content.size(); p++) {
                int j = content.get(p).first;
                int k = content.get(p).second;
                if (i >= j && i < k) {
                    Q_List.add(content.get(p).q);
                    System.out.println("value: "+i+"  q:"+content.get(p).q);
                    break;
                }//end of if condition
            }// end of internal for loop
            System.out.println("size: "+Q_List.size());
        }// end of  external for loop
        Q_Matrix=new int [w][h];
        for(int wloop=0;wloop<w;wloop++)
        {
            for(int hloop=0; hloop<h;hloop++)
            {
                Q_Matrix[wloop][hloop]=Q_List.get(counter);
                counter++;
            }
        }
    }// end of function
    public static void saveFile() throws IOException {

        //Create File to write ranges
        FileWriter fileWriter = new FileWriter("Image ranges.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (int i = 0; i < content.size(); i++) {
            bufferedWriter.write(content.get(i).first + "   " + content.get(i).second + "   " + content.get(i).q+"   "+content.get(i).q1);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

        //Create File to write Compressed Matrix
        FileWriter fileWriter2 = new FileWriter("compressedImage_qData.txt");
        BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter2);
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                bufferedWriter2.write(Q_Matrix[i][j] + " ");
            }
            bufferedWriter2.newLine();
        }
        bufferedWriter2.close();

    }
    public static void readFile() throws IOException {
        h = 0;
        table newRange;
        String line;
        BufferedReader br = new BufferedReader(new FileReader("Image ranges.txt"));
        // Read line by line and it will be false when there is no new lines

        int i = 0;
        content=new ArrayList<table>();
        while ((line = br.readLine()) != null) {
            // Split my string based on spaces
            String[] parts = line.split("\\s+");
            int low = Integer.parseInt(parts[0]), high = Integer.parseInt(parts[1]), value = Integer.parseInt(parts[2]);
            newRange = new table(low, high,i++, value);
            content.add(newRange);
        }
        br.close();

        // Read Data from Image Matrix File
        String[] test = null;
        String testline;
        BufferedReader br2 = new BufferedReader(new FileReader("compressedImage_qData.txt"));
        // Read line by line and it will be false when there is no new lines
        while ((testline = br2.readLine()) != null) {
            test = testline.split("\\s+");
            h++;
        }
        br2.close();
        w = test.length;

        Q_Matrix = new int[w][h];

        String line2;
        BufferedReader br3 = new BufferedReader(new FileReader("compressedImage_qData.txt"));
        for (int k = 0; (line2 = br3.readLine()) != null; k++) {
            // Split my string based on spaces
            String[] parts = line2.split("\\s+");
            for (int j = 0; j < parts.length; j++) {
                Q_Matrix[k][j] = Integer.parseInt(parts[j]);
            }
        }
        br3.close();

    }
    
    public static void Create_matrix() {
        max = new int[w][h];
        int k = 0;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                max[i][j] = content.get(Q_Matrix[i][j]).q1;
                k++;
            }
        }
    }
    public static void writeImage() throws IOException {

        File outFile = new File("Quantized Image.jpg");
        BufferedImage outImage = new BufferedImage(w,h, BufferedImage.TYPE_INT_RGB);
        int rgb = 0;
        Color pixelColor;
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {
                pixelColor = new Color(max[i][j], max[i][j], max[i][j]);
                rgb = pixelColor.getRGB();
                outImage.setRGB(i, j, rgb);
            }
        }

        ImageIO.write(outImage, "jpg", outFile);

    }
    public static void main(String[] args) throws IOException {
        /*Data_List.add(6);
            Data_List.add(15);
            Data_List.add(17);
            Data_List.add(60);
            Data_List.add(100);
            Data_List.add(90);
            Data_List.add(66);
            Data_List.add(59);
            Data_List.add(18);
            Data_List.add(3);
            Data_List.add(5);
            Data_List.add(16);
            Data_List.add(14);
            Data_List.add(67);
            Data_List.add(63);
            Data_List.add(2);
            Data_List.add(98);
            Data_List.add(92); */

        int level;
        Scanner input = new Scanner(System.in);
        System.out.println("Enter number of levels:");
        level = input.nextInt();
        /* user enter the image */
        readImage("Original Image.jpg");
        Compression(Data_List, level);
        Create_Table();
        generate_Q();
        saveFile();
        readFile();
        Create_matrix();
        writeImage();
    }

}
