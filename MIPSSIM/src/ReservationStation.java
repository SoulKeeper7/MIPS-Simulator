/**
 * 
 * @author agarw
 *
 */
public class ReservationStation {
	public String Name;
	public String Busy;
	public String Op;
	public int Vj;
	public int Vk;
	public int Qj;
	public int Qk;
	public String DEst;
	public String Locj;
	public String Lock;
	/**
	 * 
	 * @param n
	 * @param b
	 * @param o
	 * @param vj
	 * @param vk
	 * @param qj
	 * @param qk
	 * @param d
	 * @param Locj
	 * @param Lock
	 */
	public ReservationStation(String n,String b,String o,int vj,int vk,int qj,int qk,String d,String Locj,String Lock) 
	{
       this.Name =n;
       this.Locj=Locj;
       this.Lock=Lock;
       this.Busy=b;
       this.Op=o;
       this.Qj=qj;
       this.Qk=qk;
       this.Vj=vj;
       this.Vk=vk;
       this.DEst=d;       
	}
}
