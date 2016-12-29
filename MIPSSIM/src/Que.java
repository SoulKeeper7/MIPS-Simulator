/**
 * 
 * @author agarw
 *
 */
public class Que {
	public String Operation;
	public String DetinationR;
	public String SourceR;
	public String Sourceorimmediate;
	/**
	 * 
	 * @param d
	 * @param op
	 * @param SO
	 * @param imm
	 */
	public Que(String d,String op, String SO, String imm)
	{
		this.DetinationR=d;
		this.Operation=op;
		this.Sourceorimmediate=imm;
		this.SourceR=SO;
		
	}
}
