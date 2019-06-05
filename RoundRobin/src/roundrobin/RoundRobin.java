
package roundrobin;

import java.util.Vector;

public class RoundRobin {
    
    public static Vector <Process> data=new Vector<Process>();
    public static int quantum=4;
    public static int []bursts={10,1,2,1,5};
    public static int []priorties={3,1,4,5,2};
    public static int []arrivals={0,5,3,4,2};
    public static void doRoundRobin()
    {
        while(data.size()>0)
        {
            data.elementAt(0).burst-=quantum;
           if(data.elementAt(0).burst>0)
           {
               System.out.println("process number: "+data.elementAt(0).priorty+" not finished yet!" );
               Process obj=data.elementAt(0);
               data.addElement(obj);
           }
           else
           {
               System.out.println("process number: "+data.elementAt(0).priorty+" finished!" );
           }
           data.remove(0);
        }
    }
    public static void sortQueueByArrival (Vector<Process> vec)
    {
        for(int i=0;i<vec.size()-1;i++)
        {
            for(int j=i+1;j<vec.size();j++)
            {
                if(vec.elementAt(i).arrival>vec.elementAt(j).arrival || (vec.elementAt(i).arrival==vec.elementAt(j).arrival) && (vec.elementAt(i).priorty>vec.elementAt(j).priorty) )
                {
                    Process temp=vec.elementAt(j);
                    vec.set(j, vec.elementAt(i));
                    vec.set(i, temp);
                }
            }
        }
    }
    public static void sortQueueByPriorty (Vector<Process> vec)
    {
        for(int i=0;i<vec.size()-1;i++)
        {
            for(int j=i+1;j<vec.size();j++)
            {
                if(vec.elementAt(i).priorty>vec.elementAt(j).priorty || (vec.elementAt(i).priorty==vec.elementAt(j).priorty) && (vec.elementAt(i).priorty>vec.elementAt(j).priorty) )
                {
                    Process temp=vec.elementAt(j);
                    vec.set(j, vec.elementAt(i));
                    vec.set(i, temp);
                }
            }
        }
    }
    public static void shifttingToFirst(Vector<Process> vec,int index)
    {
        System.out.println("target Index into function: "+index);
        Process temp=vec.elementAt(index);
        Process carryTemp;
        System.out.println("process number "+temp.context+" carry");
        for(int i=index;i>0;i--)
        {
            vec.set(i,vec.elementAt(i-1));
        }
        vec.set(0, temp);
    }
    public static void premptivePriorty()
    {
        int strCounter=0;
        boolean flag=true;
        sortQueueByArrival(data);
        //arrival=0 periorty=3 burst=2
        //arrival=0 periorty=1 burst=1
        //arrival=1 periort=0 burst=3
        for(int i=0;i<data.size();i++)
        {
            data.elementAt(i).context=i+1;
            System.out.println(data.elementAt(i).context+"  "+data.elementAt(i).arrival+"  "+data.elementAt(i).burst+"  "+data.elementAt(i).priorty);
        }
        Vector<Process> coData=new Vector<Process>(); 
        while(data.size()>0)
        {
            int currentp=data.elementAt(0).arrival+data.elementAt(0).burst,index=0;
            for(int i=1;i<data.size();i++)
            {
                System.out.print("process number "+data.elementAt(i).context+" arrival: "+data.elementAt(i).arrival+"  ");
                System.out.println("//current process number "+data.elementAt(0).context+" will end at: "+currentp);
                if(data.elementAt(i).arrival<currentp)
                {
                    strCounter++;
                    System.out.print("process number "+data.elementAt(i).context+" periorty: "+data.elementAt(i).priorty+"  ");
                    System.out.println("//process number "+data.elementAt(0).context+" periorty: "+data.elementAt(0).priorty+"  ");
                    if(data.elementAt(i).priorty < data.elementAt(0).priorty)
                    {
                        index=i-1;
                        System.out.println("process number: "+data.elementAt(0).context+" not finished yet!");
                        data.elementAt(0).burst-=(data.elementAt(i).arrival-data.elementAt(0).arrival);
                        System.out.println(data.elementAt(0).burst+" not finished yet!");
                        coData.addElement(data.elementAt(0));
                        System.out.println(coData.lastElement().burst+" ela5ir!");
                        break;
                    }
                    else if(strCounter==4)
                    {
                        
                    }
                }
//                else
//                {
//                    System.out.println("process number: "+data.elementAt(0).context+" finished");
//                }
            }
//            System.out.println("vector size before delete: "+data.size());
//            System.out.println("COvector size before delete: "+coData.size());
            if(data.size()==1 && flag==true)
            {
                System.out.println("process number "+data.elementAt(0).context);
                int newArrival=data.elementAt(0).arrival+data.elementAt(0).burst;
                System.out.println("burst: "+data.elementAt(0).burst+"  arrival:"+data.elementAt(0).arrival);
                System.out.println(newArrival);
                sortQueueByPriorty(coData);
                for(int i=0;i<coData.size();i++)
                {
                    if(i==0)
                    {
                        System.out.println("HERE  process number "+coData.elementAt(i).context);
                        coData.elementAt(i).arrival =newArrival;
                        System.out.println(coData.elementAt(i).arrival+"      "+coData.elementAt(i).burst);
                    }
                        
                    else
                    {
                        System.out.println("process number "+coData.elementAt(i).context);
                        System.out.println(coData.elementAt(i-1).arrival+"      "+coData.elementAt(i-1).burst);
                        coData.elementAt(i).arrival=coData.elementAt(i-1).arrival+coData.elementAt(i-1).burst;
                        System.out.println(coData.elementAt(i).arrival);
                    }
                }
                for(int i=0;i<coData.size();i++)
                    data.addElement(coData.elementAt(i));
                flag= false;
            }
            System.out.println("process number: "+data.elementAt(0).context+" will be deleted");
            data.remove(0);
//            System.out.println("vector size after delete: "+data.size());
//            System.out.println("COvector size after delete: "+coData.size());
            if(data.size()!=0)
            {
                System.out.println("target Index: "+index);
                System.out.println("size before shift: "+data.size());
                shifttingToFirst(data,index);
                System.out.println("size after shift: "+data.size());
            }
        }
    }
    public static void main(String[] args) {
        for(int i=0; i<5 ;i++)
        {
            Process obj=new Process("Ahmed",arrivals[i],bursts[i],priorties[i],i+1);
            data.addElement(obj);
        }
        //doRoundRobin();
        premptivePriorty();
        System.out.println("Done!!");
        
    }
    
}
