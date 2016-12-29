/**
 * 
 * @author agarw
 *
 */
public class REG {
	  // Register Pieces
    public int  Number;
    public Integer  Value;
    public boolean  Ready;
    public boolean available;

    public REG(int num, int val,boolean red) {
        this.Number = num;
        this.Value  = val;
        this.Ready  = red;
      
    }
    /**
     * 
     * @param j
     */
    public REG(int j) {
        this.Number = j;
        this.Value  = 0;
        this.Ready  = true;
        this.available =true;
      
    }

}
