
package vectorquantizer;

import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;
import javax.imageio.ImageIO;

public class VectorQuantizer {

    static String vecSize;
    static int vecRow,vecCol ,width, height,codeBookSize;
    static int[][] Matriximage = null;  
    static Vector<int [][]> vectors=new Vector<int [][]>();
    static Vector<int [][]> codeBook=new Vector<int [][]>();
    public static void readImage() throws IOException {

        BufferedImage image = null;

        File input_file = new File("Original Image.jpg"); //image file path 
        image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);  // assignment object values 

        // Reading input file 
        image = ImageIO.read(input_file);
        width = image.getWidth();
        height = image.getHeight();
        Matriximage = new int[width][height];

        Raster raster = image.getData();
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Matriximage[i][j] = raster.getSample(i, j, 0);
            }
        }

        System.out.println("Read done.");

    }
    public static void saveFile() throws IOException {

        //Create File to write Compressed Matrix
        FileWriter fileWriter2 = new FileWriter("image.txt");
        BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter2);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferedWriter2.write(Matriximage[i][j] + " ");
            }
            bufferedWriter2.newLine();
        }
        bufferedWriter2.close();

    }
    public static void readFile() throws IOException {
        height = 0;
        // Read Data from Image Matrix File
        String[] test = null;
        String testline;
        BufferedReader br2 = new BufferedReader(new FileReader("image.txt"));
        // Read line by line and it will be false when there is no new lines
        while ((testline = br2.readLine()) != null) {
            test = testline.split("\\s+");
            height++;
        }
        br2.close();
        width = test.length;

        Matriximage = new int[width][height];

        String line2;
        BufferedReader br3 = new BufferedReader(new FileReader("image.txt"));
        for (int k = 0; (line2 = br3.readLine()) != null; k++) {
            // Split my string based on spaces
            String[] parts = line2.split("\\s+");
            for (int j = 0; j < parts.length; j++) {
                Matriximage[k][j] = Integer.parseInt(parts[j]);
            }
        }
        br3.close();

    }
    public static void reShape(int rpointer,int cpointer)
    {
        while(true)
        {
            int[][] container=new int[vecRow][vecCol];
            if(rpointer>=height)
                break;
            int i=rpointer,j=cpointer;
            for (int a=0;a<vecRow;a++)
            {
                j=cpointer;
                for(int b=0;b<vecCol;b++)
                {
                    container[a][b]=Matriximage[i][j];
                    j++;
                }
                i++;
            }
            vectors.add(container);
            cpointer+=vecCol;
            if(cpointer>=width)
            {
                rpointer+=vecRow;
                cpointer=0;
            }
        }
    }
    
    public static boolean euclideanDistance(int[][] test, int[][] low,int[][] high)
    {
        int lowValue=0,highValue=0;
        for(int i=0;i<vecRow;i++)
            for(int j=0;j<vecCol;j++)
            {
                lowValue+=(test[i][j]-low[i][j])*(test[i][j]-low[i][j]);
                highValue+=(test[i][j]-high[i][j])*(test[i][j]-high[i][j]);
            }
        if(highValue>lowValue)
            return true;
        else
            return false;
    }
    
    public static void fillCodebook(Vector<int[][] > data,Vector<int[][] > cBook,int cbSize) {
        
        int [][] avg=new int [vecRow][vecCol];
        
        
        for(int i=0;i<vecRow;i++)
            for(int j=0;j<vecCol;j++)
                avg[i][j]=0;
        
        
        for(int i=0;i<data.size();i++)
        {
            for(int j=0;j<vecRow;j++)
            {
                for(int k=0;k<vecCol;k++)
                    avg[j][k]+=data.elementAt(i)[j][k];
            }
        }
        for(int i=0;i<vecRow;i++)
            for(int j=0;j<vecCol;j++)
                avg[i][j]/=data.size();
        
        if(1>cbSize)
        {
            cBook.addElement(avg);
            return ;
        }
        
        int [][]lowAVG=new int [vecRow][vecCol];
        int [][]highAVG=new int [vecRow][vecCol];
        
        
        for(int i=0;i<vecRow;i++)
            for(int j=0;j<vecCol;j++)
            {
                lowAVG[i][j]=avg[i][j];
                highAVG[i][j]=avg[i][j]+1;
            }
        
        Vector<int[][]> lowVectors=new Vector<int [][]>();
        Vector<int[][]> highVectors=new Vector<int [][]>();
        
        for(int i=0;i<data.size();i++)
        {
            if(euclideanDistance(data.elementAt(i),lowAVG,highAVG))
                highVectors.addElement(data.elementAt(i));
            else
                lowVectors.addElement(data.elementAt(i));
        }
        
        fillCodebook(lowVectors,cBook,cbSize/2);
        fillCodebook(highVectors,cBook,cbSize/2);
    }
    
    public static void finalcheck()
    {
        int [][] avg;
        Vector<int [][]> container=new Vector<int [][]>();
        for(int i=0;i<codeBook.size();i++)
        {
            for(int j=0;j<vectors.size();j++)
            {
                int valid=euclideanDistanceFinal(vectors.elementAt(j),codeBook);
                if(valid==i)
                    container.addElement(vectors.elementAt(j));
            }
        
            avg=new int [vecRow][vecCol];

            for(int a=0;a<vecRow;a++)
                for(int b=0;b<vecCol;b++)
                    avg[a][b]=0;


            for(int a=0;a<container.size();a++)
            {
                for(int b=0;b<vecRow;b++)
                {
                    for(int c=0;c<vecCol;c++)
                        avg[b][c]+=container.elementAt(a)[b][c];
                }
            }
            for(int a=0;a<vecRow;a++)
                for(int b=0;b<vecCol;b++)
                    avg[a][b]/=container.size();
            codeBook.set(i,avg);
        }
    }
    
    public static int euclideanDistanceFinal(int[][] test,Vector<int[][] >myVectors)
    {
        int lowestValue=0,index=0;
        for(int x=0;x<myVectors.size();x++)
        {
            if (x==0)
            {
                for(int i=0;i<vecRow;i++)
                {
                    for(int j=0;j<vecCol;j++)
                    {
                        lowestValue+=(test[i][j]-myVectors.elementAt(x)[i][j])*(test[i][j]-myVectors.elementAt(x)[i][j]);
                    }
                }
            }
            else
            {
                int check=0;
                for(int i=0;i<vecRow;i++)
                {
                    for(int j=0;j<vecCol;j++)
                    {
                        check+=(test[i][j]-myVectors.elementAt(x)[i][j])*(test[i][j]-myVectors.elementAt(x)[i][j]);
                    }
                }
                if(check<lowestValue)
                {
                    lowestValue=check;
                    index=x;
                }
            }
        }
        return index;
    }
    public static void main(String[] args) throws IOException {
        //readImage();
        width=6;height=6;
        Matriximage=new int [width][height];
        Matriximage[0][0]=1;
        Matriximage[0][1]=2;
        Matriximage[0][2]=7;
        Matriximage[0][3]=9;
        Matriximage[0][4]=4;
        Matriximage[0][5]=11;
        Matriximage[1][0]=3;
        Matriximage[1][1]=4;
        Matriximage[1][2]=6;
        Matriximage[1][3]=6;
        Matriximage[1][4]=12;
        Matriximage[1][5]=12;
        Matriximage[2][0]=4;
        Matriximage[2][1]=9;
        Matriximage[2][2]=15;
        Matriximage[2][3]=14;
        Matriximage[2][4]=9;
        Matriximage[2][5]=9;
        Matriximage[3][0]=10;
        Matriximage[3][1]=10;
        Matriximage[3][2]=20;
        Matriximage[3][3]=18;
        Matriximage[3][4]=8;
        Matriximage[3][5]=8;
        Matriximage[4][0]=4;
        Matriximage[4][1]=3;
        Matriximage[4][2]=17;
        Matriximage[4][3]=16;
        Matriximage[4][4]=1;
        Matriximage[4][5]=4;
        Matriximage[5][0]=4;
        Matriximage[5][1]=5;
        Matriximage[5][2]=18;
        Matriximage[5][3]=18;
        Matriximage[5][4]=5;
        Matriximage[5][5]=6;
        System.out.println("Please Enter Size of Vector: ");
        Scanner getInput = new Scanner(System.in);
        vecSize = getInput.next();
        String[] parts=vecSize.split("\\*");
        vecRow=Integer.parseInt(parts[0]);
        vecCol=Integer.parseInt(parts[1]);
        while (vecRow!=vecCol || (width%vecRow)!=0 )
        {
            System.out.println("Error");
            System.out.println("Please Enter Size of Vector again: ");
            getInput = new Scanner(System.in);
            vecSize = getInput.next();
            parts=vecSize.split("\\*");
            vecRow=Integer.parseInt(parts[0]);
            vecCol=Integer.parseInt(parts[1]);
        }
        System.out.println("Please Enter Size of codebook(in vectors): ");
        getInput = new Scanner(System.in);
        codeBookSize = getInput.nextInt();
        while(codeBookSize >(width /vecRow)*(width /vecRow))
        {
            System.out.println("Error");
            System.out.println("Please Enter Size of codebook(in vectors) again: ");
            getInput = new Scanner(System.in);
            codeBookSize = getInput.nextInt();
        }
        reShape(0,0);
        fillCodebook(vectors,codeBook,codeBookSize/2);
        for(int i=0;i<codeBook.size();i++)
        {
            for(int j=0;j<vecRow;j++)
            {
                for(int k=0;k<vecCol;k++)
                {
                    System.out.print(codeBook.elementAt(i)[j][k]+" ");
                }
                System.out.println();
            }
            System.out.println("----------------------");
        }
        finalcheck();
        for(int i=0;i<codeBook.size();i++)
        {
            for(int j=0;j<vecRow;j++)
            {
                for(int k=0;k<vecCol;k++)
                {
                    System.out.print(codeBook.elementAt(i)[j][k]+" ");
                }
                System.out.println();
            }
            System.out.println("----------------------");
        }
    }
    
}
