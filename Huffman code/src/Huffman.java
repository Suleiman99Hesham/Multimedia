//aaaaaaaaaaaaaaaaabbbbbbbbbbbbbbbbbbbbbbcccccccccccccccddddddddddddddeeeeeeeeeeeeeeeeeeeeeeeeeeeeeeff
import java.util.Vector;
import java.io.*;
public class Huffman {
    // Creates an empty list for saving characters and their indexes in Second list
    static Vector< PrimaryType> plist = new Vector<PrimaryType>();        
    //create an empty list for saving character occurance time
    static Vector< PrimaryType> slist = new Vector<PrimaryType>();
    static int originalsize=0;
    static Vector<PrimaryType> decVector=new Vector<PrimaryType>();
    public static int findbykey (Vector<PrimaryType> myvec,String schar)
    {
        for (int i=0;i<myvec.size();i++)
        {
            if(myvec.elementAt(i).key.equals(schar))
            {
                return i;
            }
        }
        return -1;
    }
    public static int findbycode (String scode)
    {
        for (int i=0;i<decVector.size();i++)
        {
            if(decVector.elementAt(i).code.equals(scode))
            {
                return i;
            }
        }
        return -1;
    }
    public static void sortlist(Vector<PrimaryType> myvec)
    {
        int n = myvec.size();
        for(int i=0;i<n-1;i++)
        {
            for(int j=0;j<n-i-1;j++)
            {
                if(myvec.elementAt(j).prob < myvec.elementAt(j+1).prob)
                {
                    PrimaryType obj=myvec.elementAt(j);
                    myvec.set(j, myvec.elementAt(j+1));
                    myvec.set(j+1, obj);
                }
            }
        }
    }
    
    
    public static void decompress() throws IOException{
        readNodes();
		
        String codedStatement = "",originalWord="";
        // Open code File to read 
        BufferedReader br = new BufferedReader(new FileReader("code.txt"));
        // Read code in string
        codedStatement = br.readLine();
        br.close();
        
        for (int i = 0 ; i<codedStatement.length();i++){
            String holdIt = "" + codedStatement.charAt(i);
            while(true)
            {
                int index=findbycode(holdIt);
                if(index!=-1)
                {
                    originalWord+=decVector.elementAt(index).key;
                    break;
                }
                else
                {
                    holdIt += codedStatement.charAt(++i);
                }
            }
        }
        
                
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter("code.txt", true)));
        out.println();
        out.println(originalWord);
	out.close();
        
        System.out.println(originalWord);
    }
    
   
    public static void readNodes() throws IOException{
		PrimaryType newNode;
		String line;
		BufferedReader br = new BufferedReader(new FileReader("list.txt"));
		// Read line by line and it will be false when there is no new lines
		while ((line = br.readLine()) != null) {
			// Split my string based on spaces
			String [] parts = line.split("\\s+");
			String symbol=parts[0], code=parts[1];
			newNode = new PrimaryType(symbol,0,0,0,code,0);
			decVector.add(newNode);
		}
	}
	
	// Write nodes in the file
	public static void saveNodes() throws IOException{
		PrimaryType newNode;
		String line;
		//Create File to write in
	    FileWriter fileWriter = new FileWriter("list.txt");
	    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);	 
	    for(int i=0;i<originalsize;i++) {    
			bufferedWriter.write(plist.elementAt(i).key + "   " + plist.elementAt(i).code);
			bufferedWriter.newLine();
		}
	    bufferedWriter.close();
	}
    
    
    public static void main(String[] args) throws IOException {

   
       
    
        String comp = "";
        // Open code File to read 
        BufferedReader br = new BufferedReader(new FileReader("statement.txt"));
        // Read message in string
        comp = br.readLine();
        br.close();
         

        for (int i = 0; i < comp.length(); i++) {
            int place=findbykey(plist,comp.charAt(i)+"");
            // If this is first occurrence of Character
            // Insert the Character
            if (place==-1)
            {
                //putting the character in object
                PrimaryType obj=new PrimaryType (comp.charAt(i)+"",1,-1,-1,"",0);
                plist.addElement(obj);
            }
            // If elements already exists in hash map 
            // Increment the count of element by 1 
            else if (place!=-1)
            {
                plist.elementAt(place).prob++;
            }
        }
        
        
        //calculating the probability for chars
        for(int i=0;i<plist.size();i++)
        {
            plist.elementAt(i).prob/=comp.length();
        }
        
        originalsize=plist.size();
        while(true)
        {
            
            slist=new Vector<PrimaryType>();
            for(int i=0;i<plist.size();i++)
            {
                if(plist.elementAt(i).added==0)
                {
                    slist.add(plist.elementAt(i));
                }
            }
            int listsize=slist.size();
            sortlist(slist);
            if(listsize>2)
            {
                String newkey=slist.elementAt(listsize-2).key+slist.elementAt(listsize-1).key;
                double newprob=slist.elementAt(listsize-2).prob+slist.elementAt(listsize-1).prob;
                String fkey=slist.elementAt(listsize-2).key;
                String skey=slist.elementAt(listsize-1).key;
                int fplace=findbykey(plist,fkey);
                int splace=findbykey(plist,skey);
                plist.elementAt(fplace).added=1;
                plist.elementAt(splace).added=1;
                PrimaryType obj=new PrimaryType(newkey,newprob,fplace,splace,"",0);
                plist.addElement(obj);
            }
            else if(listsize==1)
            {
                String fkey=slist.elementAt(0).key;
                int fplace=findbykey(plist,fkey);
                plist.elementAt(fplace).code="0";
                break;
            }
            else
            {
                String fkey=slist.elementAt(0).key;
                String skey=slist.elementAt(1).key;
                int fplace=findbykey(plist,fkey);
                int splace=findbykey(plist,skey);
                plist.elementAt(fplace).code="0";
                plist.elementAt(splace).code="1";
                break;
            }
        }
        int counter=0;
        while(counter < originalsize)
        {
            for(int i=0;i<plist.size();i++)
            {
                if(plist.elementAt(i).added==0&&plist.elementAt(i).findex!=-1&&plist.elementAt(i).sindex!=-1)
                {
                    plist.elementAt(plist.elementAt(i).findex).code=plist.elementAt(i).code+"0";
                    plist.elementAt(plist.elementAt(i).sindex).code=plist.elementAt(i).code+"1";
                    plist.elementAt(plist.elementAt(i).findex).added=0;
                    plist.elementAt(plist.elementAt(i).sindex).added=0;
                }
            }
            counter=0;
            for(int i=0;i<originalsize;i++)
            {
                if(plist.elementAt(i).added==0&&plist.elementAt(i).findex==-1&&plist.elementAt(i).sindex==-1)
                {
                    counter++;
                }
            }
        }
        //Create File to write in
        FileWriter fileWriter = new FileWriter("code.txt");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);	 

        // Write code of every symbol in the file
        for(int i=0;i<comp.length();i++)
        {
            int charIndex=findbykey(plist,comp.charAt(i)+"");
            bufferedWriter.write(plist.elementAt(charIndex).code);//appends the string to the file

        }
        bufferedWriter.close();

        //Save Nodes in File
        saveNodes();
        
        decompress();
        /*for(int i=0;i<plist.size();i++)
        {
            System.out.println("key: "+plist.elementAt(i).key + "\tCode : "+plist.elementAt(i).code);
        }*/
        
    }
    
}
