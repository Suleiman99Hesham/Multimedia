package jpeg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Vector;

public class JPEG {
    //-------------------------------------------variables----------------------------------------------
    static Vector< TagType> plist = new Vector<TagType>();   //a primary list for every Tags and its code
    static Vector< TagType> slist = new Vector<TagType>();   //a secondery list is used in compression operation
    static int originalsize = 0;                             //to save the size of primary list before modifying
    static Vector<TagType> decVector = new Vector<TagType>();//contains the huffman Table while decompressing
    static Vector<Integer> data = new Vector<Integer>();     //cantains the original input numbers value
    
    
    //--------------------------general fuctions----------------------------------------------
    public static int findbyTag(Vector<TagType> myvec, int zeroc, int val) {
        for (int i = 0; i < myvec.size(); i++) {

            if (myvec.elementAt(i).zeroCounter == zeroc && myvec.elementAt(i).cateValue == val) {
                return i;
            }
        }
        return -1;
    }

    public static int findbykey(Vector<TagType> myvec, String key) {
        for (int i = 0; i < myvec.size(); i++) {

            if (myvec.elementAt(i).key == key) {
                return i;
            }
        }
        return -1;

    }

    public static int findbycode(String scode) {
        for (int i = 0; i < decVector.size(); i++) {
            if (decVector.elementAt(i).code.equals(scode)) {
                return i;
            }
        }
        return -1;
    }

    public static void sortlist(Vector<TagType> myvec) {
        int n = myvec.size();
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - i - 1; j++) {
                if (myvec.elementAt(j).prob < myvec.elementAt(j + 1).prob) {
                    TagType obj = myvec.elementAt(j);
                    myvec.set(j, myvec.elementAt(j + 1));
                    myvec.set(j + 1, obj);
                }
            }
        }
    }

    public static String finalValue(String value) {
        String output = value;
        boolean flag = false;
        if (value.charAt(0) == '0') {
            output = ReverseBinary(output);
            flag = true;
        }
        int x = Integer.parseInt(output, 2);
        if (flag == true) {
            x = Math.negateExact(x);
        }
        String y = Integer.toString(x);
        return y;
    }

    public static int getCTValue(int x) {
        String y = Integer.toBinaryString(x);
        return y.length();
    }
    
    public static String ReverseBinary(String oldBinary) {
        String newBinary = "";
        for (int i = 0; i < oldBinary.length(); i++) {
            if (oldBinary.charAt(i) == '1') {
                newBinary += '0';
            } else if (oldBinary.charAt(i) == '0') {
                newBinary += '1';
            }
        }
        return newBinary;
    }
    
    
    //--------------------------------perform compress Operation--------------------------------------
    public static void compress() throws IOException {
        getInput();                     //reading input stream from file
        fillWithTags();                 //calculating the probability for tags
        buildTagsTree();                //build the tree of Tags
        giveCode();                     //give code to every Tag
        saveCompressedCode();           //save the compress encodded code to file
        saveNodes();                    //Save Nodes(Tag-code ) in File (Huffman Table)
    }
    //reading input stream from file
    public static void getInput() throws IOException {
        String comp = "";
        // Open code File to read 
        BufferedReader br = new BufferedReader(new FileReader("statement.txt"));
        // Read message in string
        comp = br.readLine();
        br.close();
        String[] parts = comp.split("\\s+");
        for (int i = 0; i < parts.length; i++) {
            int x = Integer.parseInt(parts[i]);
            data.addElement(x);
        }
    }
    //calculating the probability for tags
    public static void fillWithTags() {
        int zeroCounter = 0;
        int value = -1;
        //filling my vectory with tags
        for (int i = 0; i < data.size(); i++) {
            if (data.elementAt(i) != 0) {
                int x = Math.abs(data.elementAt(i));
                value = getCTValue(x);
                int place = findbyTag(plist, zeroCounter, value);
                // If this is first occurrence of Tag
                // Insert the Tag
                if (place == -1) {
                    //putting the Tag data in object
                    Character ch = (char) (65 + i);
                    TagType obj = new TagType(zeroCounter, value, 1, ch + "", -1, -1, "", 0);
                    plist.addElement(obj);
                } // If elements already exists in Tag Vector
                // Increment the count of Tag by 1 
                else if (place != -1) {
                    plist.elementAt(place).prob++;
                }
                zeroCounter = 0;
                value = -1;
            } else if (i == data.size() - 1) {
                Character ch = (char) (65 + i);
                TagType obj = new TagType(-1, -1, 1, ch + "", -1, -1, "EOB", 0);
                plist.addElement(obj);
            } else {
                zeroCounter++;
            }
        }
        for (int i = 0; i < plist.size(); i++) {
            plist.elementAt(i).prob /= plist.size();
        }
    }
    //build the tree of Tags
    public static void buildTagsTree() {
        originalsize = plist.size();    //size of original characters
        while (true) {

            slist = new Vector<TagType>();
            for (int i = 0; i < plist.size(); i++) {
                if (plist.elementAt(i).added == 0) {
                    slist.add(plist.elementAt(i));
                }
            }
            int listsize = slist.size();
            sortlist(slist);
            if (listsize > 2) {
                String newkey = slist.elementAt(listsize - 2).key + slist.elementAt(listsize - 1).key;
                double newprob = slist.elementAt(listsize - 2).prob + slist.elementAt(listsize - 1).prob;
                String fkey = slist.elementAt(listsize - 2).key;
                String skey = slist.elementAt(listsize - 1).key;
                int fplace = findbykey(plist, fkey);
                int splace = findbykey(plist, skey);
                plist.elementAt(fplace).added = 1;
                plist.elementAt(splace).added = 1;
                TagType obj = new TagType(-1, -1, newprob, newkey, fplace, splace, "", 0);
                plist.addElement(obj);
            } else if (listsize == 1) {
                String fkey = slist.elementAt(0).key;
                int fplace = findbykey(plist, fkey);
                plist.elementAt(fplace).code = "0";
                break;
            } else {
                String fkey = slist.elementAt(0).key;
                String skey = slist.elementAt(1).key;
                int fplace = findbykey(plist, fkey);
                int splace = findbykey(plist, skey);
                plist.elementAt(fplace).code = "0";
                plist.elementAt(splace).code = "1";
                break;
            }
        }
    }
    //give code to every Tag
    public static void giveCode() {
        int counter = 0;
        while (counter < originalsize) {
            for (int i = 0; i < plist.size(); i++) {
                if (plist.elementAt(i).added == 0 && plist.elementAt(i).findex != -1 && plist.elementAt(i).sindex != -1) {
                    plist.elementAt(plist.elementAt(i).findex).code = plist.elementAt(i).code + "0";
                    plist.elementAt(plist.elementAt(i).sindex).code = plist.elementAt(i).code + "1";
                    plist.elementAt(plist.elementAt(i).findex).added = 0;
                    plist.elementAt(plist.elementAt(i).sindex).added = 0;
                }
            }
            counter = 0;
            for (int i = 0; i < originalsize; i++) {
                if (plist.elementAt(i).added == 0 && plist.elementAt(i).findex == -1 && plist.elementAt(i).sindex == -1) {
                    counter++;
                }
            }
        }
    }
    //save the compress encodded code to file
    public static void saveCompressedCode() throws IOException {
        //Create File to write in
        FileWriter fileWriter = new FileWriter("code.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        // Write code of every symbol in the file
        int zeroCounter = 0;
        for (int i = 0; i < data.size(); i++) {
            if (data.elementAt(i) != 0) {
                int unsignedValue = Math.abs(data.elementAt(i));
                int tagPos = findbyTag(plist, zeroCounter, getCTValue(unsignedValue));
                String binarycode = Integer.toBinaryString(unsignedValue);
                if (data.elementAt(i) < 0) {
                    binarycode = ReverseBinary(binarycode);
                }
                bufferedWriter.write(plist.elementAt(tagPos).code + binarycode);//appends the string to the file
                zeroCounter = 0;
            } else {
                zeroCounter++;
            }
        }
        bufferedWriter.write("EOB");//end of encoding operation
        bufferedWriter.close();
    }
    //Save Nodes(Tag-code ) in File (Huffman Table)
    public static void saveNodes() throws IOException {
        TagType newNode;
        String line;
        //Create File to write in
        FileWriter fileWriter = new FileWriter("list.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        for (int i = 0; i < originalsize; i++) {
            if (i == originalsize - 1) {
                bufferedWriter.write("EOB     " + plist.elementAt(i).code);
            } else {
                bufferedWriter.write(plist.elementAt(i).zeroCounter + "/" + plist.elementAt(i).cateValue + "     " + plist.elementAt(i).code);
            }
            bufferedWriter.newLine();
        }
        bufferedWriter.close();
    }
    

    //-----------------------------------perform decompress Operation-----------------------------------------------
    public static void decompress() throws IOException {
        readNodes();                                //Read Nodes(Tag-code ) from File (Huffman Table)
        String originalOutput=informOutput();       //get final out but by converting tags int original values
        saveDecompressedCode(originalOutput);       //save the decompress decodded codded code to file 
        System.out.println(originalOutput);
    }
    //Read Nodes(Tag-code ) from File (Huffman Table)
    public static void readNodes() throws IOException {
        TagType newNode;
        String line;
        BufferedReader br = new BufferedReader(new FileReader("list.txt"));
        // Read line by line and it will be false when there is no new lines
        while ((line = br.readLine()) != null) {
            // Split my string based on spaces
            String[] parts = line.split("\\s+");
            String key = parts[0], code = parts[1];
            Integer zeroCounter = -1, categoryValue = -1;
            if (!parts[0].equals("EOB")) {
                key = "";
                String symbol = parts[0];
                String[] newparts = symbol.split("\\/");
                zeroCounter = Integer.parseInt(newparts[0]);
                categoryValue = Integer.parseInt(newparts[1]);
            }
            newNode = new TagType(zeroCounter, categoryValue, 0, key, 0, 0, code, 0);
            decVector.add(newNode);
        }
    }
    //get final out but by converting tags int original values
    public static String informOutput() throws IOException {
        String codedStatement = "", originalWord = "";
        // Open code File to read 
        BufferedReader br = new BufferedReader(new FileReader("code.txt"));
        // Read code in string
        codedStatement = br.readLine();
        br.close();
        boolean flag = false;
        for (int i = 0; i < codedStatement.length(); i++) {
            String holdIt = "" + codedStatement.charAt(i);
            while (true) {
                int index = findbycode(holdIt);
                if (holdIt.contains("E")) {
                    originalWord += "EOB";
                    flag = true;
                    break;
                }
                if (index != -1) {
                    int zeroRepeatTime = decVector.elementAt(index).zeroCounter;
                    String zeros = new String(new char[zeroRepeatTime]).replace("\0", "0,");
                    int numOfBits = decVector.elementAt(index).cateValue;
                    String Value = "";
                    int x = i + numOfBits;
                    while (i < x) {
                        i++;
                        Value += codedStatement.charAt(i);
                    }
                    Value = finalValue(Value);
                    originalWord += zeros + Value + ',';
                    break;
                } else if (i == codedStatement.length() - 3) {
                    break;
                } else {
                    holdIt += codedStatement.charAt(++i);
                }
            }
            if (flag == true) {
                break;
            }
        }
        return originalWord;
    }
    //save the decompress decodded codded code to file
    public static void saveDecompressedCode(String printed) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("code.txt", true)));
        out.println();
        out.println(printed);
        out.close();
    }
    
    
    //-----------------------------------------------Runing main------------------------------------------------------
    public static void main(String[] args) throws IOException {

        compress();                     //perform compress Operation
        decompress();                   //perform decompress Operation

    }

}
