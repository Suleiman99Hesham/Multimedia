
package nonuniformquasulei;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.Raster;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.Vector;
import javax.imageio.ImageIO;

public class NonUniformQuaSulei {

    static int num_levels, width, height;

    static int[][] Matriximage = null;
    static int[][] newMatriximage = null;
    static int[][] qData = null;
    static Vector<Integer> ImageData = new Vector<Integer>();
    static Vector<Integer> AVGS = new Vector<Integer>();
    static Vector<Integer> ranges = new Vector<Integer>();
    static Vector<DataType> rangesData = new Vector<DataType>();

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
    public static void calculateAvg(Vector<Integer> data, Vector<Integer> AVGS, int out) {
        int myAvgerage = 0;
        Vector<AvgData> newData = new Vector<AvgData>();
        Vector<Integer> container = new Vector<Integer>();
        for (int i = 0; i < data.size(); i++) {
            AvgData obj = new AvgData(data.elementAt(i), false);
            newData.addElement(obj);
        }
        for (int i = 0; i < data.size(); i++) {
            myAvgerage += data.elementAt(i);
        }
        myAvgerage /= data.size();

        if (1 >= out) {
            AVGS.add(myAvgerage);
            return;
        }
        int high_Range = myAvgerage + 1;
        for (int i = 0; i < newData.size(); i++) {
            int element = newData.elementAt(i).value;
            if (element >= high_Range) {
                newData.elementAt(i).flag = true;
            }
        }
        for (int i = 0; i < newData.size(); i++) {
            if (newData.elementAt(i).flag == false) {
                int passValue = newData.elementAt(i).value;
                container.add(passValue);
            }
        }
        calculateAvg(container, AVGS, out / 2);
        container = new Vector<Integer>();
        for (int i = 0; i < newData.size(); i++) {
            if (newData.elementAt(i).flag == true) {
                int passValue = newData.elementAt(i).value;
                container.add(passValue);
            }
        }
        calculateAvg(container, AVGS, out / 2);
    }

    public static void createRanges(Vector<Integer> Avgs) {
        ranges.add(0);
        for (int i = 0; i < AVGS.size() - 1; i++) {
            ranges.add((AVGS.elementAt(i) + AVGS.elementAt(i + 1)) / 2);
        }
        ranges.add(255);
        DataType obj;
        for (int i = 0; i < AVGS.size(); i++) {
            if (i == 0) {
                obj = new DataType(ranges.elementAt(i), ranges.elementAt(i + 1), AVGS.elementAt(i));
            } else {
                obj = new DataType(ranges.elementAt(i) + 1, ranges.elementAt(i + 1), AVGS.elementAt(i));
            }
            rangesData.add(obj);
        }
    }
    public static int getQvalue(int value) {
        for (int i = 0; i < rangesData.size(); i++) {
            if (value >= rangesData.elementAt(i).lowRange && value <= rangesData.elementAt(i).highRange) {
                return i;
            }
        }
        return -1;
    }
    public static void quantization(int Num_Of_Levels) {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                ImageData.add(Matriximage[i][j]);
            }
        }
        calculateAvg(ImageData, AVGS, Num_Of_Levels);
        createRanges(AVGS);
        qData = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                qData[i][j] = getQvalue(Matriximage[i][j]);
            }
        }
    }
    
    public static void saveFile() throws IOException {

        //Create File to write ranges
        FileWriter fileWriter = new FileWriter("Image ranges.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (int i = 0; i < rangesData.size(); i++) {
            bufferedWriter.write(rangesData.elementAt(i).lowRange + "   " + rangesData.elementAt(i).highRange + "   " + rangesData.elementAt(i).decValue);
            bufferedWriter.newLine();
        }
        bufferedWriter.close();

        //Create File to write Compressed Matrix
        FileWriter fileWriter2 = new FileWriter("compressedImage_qData.txt");
        BufferedWriter bufferedWriter2 = new BufferedWriter(fileWriter2);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bufferedWriter2.write(qData[i][j] + " ");
            }
            bufferedWriter2.newLine();
        }
        bufferedWriter2.close();

    }

    public static void readFile() throws IOException {
        height = 0;
        DataType newRange;
        String line;
        BufferedReader br = new BufferedReader(new FileReader("Image ranges.txt"));
        // Read line by line and it will be false when there is no new lines

        int i = 0;
        rangesData=new Vector<DataType>();
        while ((line = br.readLine()) != null) {
            // Split my string based on spaces
            String[] parts = line.split("\\s+");
            int low = Integer.parseInt(parts[0]), high = Integer.parseInt(parts[1]), value = Integer.parseInt(parts[2]);
            newRange = new DataType(low, high, value);
            rangesData.add(newRange);
        }
        br.close();

        // Read Data from Image Matrix File
        String[] test = null;
        String testline;
        BufferedReader br2 = new BufferedReader(new FileReader("compressedImage_qData.txt"));
        // Read line by line and it will be false when there is no new lines
        while ((testline = br2.readLine()) != null) {
            test = testline.split("\\s+");
            height++;
        }
        br2.close();
        width = test.length;

        qData = new int[width][height];

        String line2;
        BufferedReader br3 = new BufferedReader(new FileReader("compressedImage_qData.txt"));
        for (int k = 0; (line2 = br3.readLine()) != null; k++) {
            // Split my string based on spaces
            String[] parts = line2.split("\\s+");
            for (int j = 0; j < parts.length; j++) {
                qData[k][j] = Integer.parseInt(parts[j]);
            }
        }
        br3.close();

    }

    public static void dequantization() throws IOException {
        int counter = 0;
        newMatriximage = new int[width][height];
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                newMatriximage[i][j] = rangesData.elementAt(qData[i][j]).decValue;
                counter++;
            }
        }
    }
    public static void writeImage() throws IOException {

        File outFile = new File("Quantized Image.jpg");
        BufferedImage outImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        int rgb = 0;
        Color pixelColor;
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                pixelColor = new Color(newMatriximage[i][j], newMatriximage[i][j], newMatriximage[i][j]);
                rgb = pixelColor.getRGB();
                outImage.setRGB(i, j, rgb);
            }
        }

        ImageIO.write(outImage, "jpg", outFile);

        System.out.println("write done.");

    }
    public static void progressImage(int Num_Of_Levels) throws IOException {
        //compress stage
        readImage();
        quantization(Num_Of_Levels);
        saveFile();
        //decompress stage
        readFile();
        dequantization();
        writeImage();
    }
    public static void main(String args[]) throws IOException {

        System.out.print("Please Enter Number of levels: ");
        Scanner getInput = new Scanner(System.in);
        num_levels = getInput.nextInt();

        if (num_levels == 0) {
            System.out.print("Wrong Input");
            return;
        }
        progressImage(num_levels);

        /*ImageData.add(6);
        ImageData.add(15);
        ImageData.add(17);
        ImageData.add(60);
        ImageData.add(100);
        ImageData.add(90);
        ImageData.add(66);
        ImageData.add(59);
        ImageData.add(18);
        ImageData.add(3);
        ImageData.add(5);
        ImageData.add(16);
        ImageData.add(14);
        ImageData.add(67);
        ImageData.add(63);
        ImageData.add(2);
        ImageData.add(98);
        ImageData.add(92);
        calculateAvg(ImageData,AVGS,num_levels);
        createRanges(AVGS);
        System.out.println(AVGS);
        for(int i=0;i<rangesData.size();i++)
        {
            System.out.println("low :"+rangesData.elementAt(i).lowRange+"  "+"high :"+rangesData.elementAt(i).highRange+"  "+"q-1 value :"+rangesData.elementAt(i).decValue);
        }*/
    }
}
