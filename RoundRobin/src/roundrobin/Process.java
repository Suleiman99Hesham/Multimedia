
package roundrobin;


public class Process {
    String name;
    int arrival;
    int burst,priorty,context;
    public Process(){}
    public Process(String n, int a,int b, int p, int c)
    {
        name=n;
        arrival=a;
        burst=b;
        priorty=p;
        context=c;
    }
}
