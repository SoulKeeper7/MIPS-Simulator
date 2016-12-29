

	import java.io.File;
	import java.io.FileNotFoundException;
	import java.io.FileOutputStream;
	import java.nio.file.Files;
	import java.nio.file.Paths;
	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.LinkedHashMap;
	import java.util.List;
	import java.util.Map;
	import java.io.PrintStream;
	
	public class Mipssim 
	{
		//List of reorder
		public static List<ROB> rob = new ArrayList<ROB>();
		//List of Reservation station
		public static List<ReservationStation> RS = new ArrayList<ReservationStation>();

		///To store the instruction
		public static List<Que> queue = new ArrayList<Que>();
		///To fetch
		public static List<Que> queue1 = new ArrayList<Que>();
		//List of instruction queue
		public static Map<Integer,String> IQ = new LinkedHashMap<Integer,String>();
		//queue
		public static Map<Integer,Que> instrucque = new LinkedHashMap<Integer,Que>();
		//register collection
		public static  List<REG> Register =new ArrayList<REG>();
		//data segment collection
		public static List<Integer> DS = new ArrayList<Integer>();
		//Starting Counter
		public static int PrgCnt =600;
		public static int clockCycle=1;
		public static int d =0;
		public static  List<BTB> Bt = new ArrayList<BTB>();
		public static int loadcount =0;
		//public static int stall =0;
		public static int count =1;		
		public static boolean BranchNT =false;
		public static int startindex =0;
		public static int endindex =0;
		public static boolean StopFetch =false;
		public static String thirdgument;
		public static  List<RSLookUP> lookup = new ArrayList<RSLookUP>();
		public  static int initializer =-1;
		public static void main(String[] args) 
		{	
			if(!Files.exists(Paths.get(args[0])))
			{
				System.out.print("File not available"+args[0]);
				
				System.exit(0);
			}
		
			 byte[] data = null;
			 String opcodebits = "";
	        String firstfivebits = "";
	        String secondfivebits = "";
	        String thirdfivebits = "";
	        String fourthfivebits = "";
	        String lastsixbits = "";
	        List<String> opcodekey = new ArrayList<String>();;
	     
	        Map<String, String[]> opcode= new HashMap<String, String[]>();
	        
			 try
			 {         
	                try
	                {
	                	data= Files.readAllBytes(Paths.get(args[0]));           
	                }
	                catch(Exception e)
	                {
	                	System.out.println(e.getMessage());
	                }   
	                
		          try
		          {          	
		        	  opcodeKeyCollectionBuilding(opcodekey);	
		        	 // Object location = Dissassembler.class.getClassLoader().getResource("Resource/opcodekey.txt");
		        	  //System.out.println((Paths.get(location.toString())));  
		        	  //opcodekey = Files.readAllLines((Paths.get(location.toString()))); 
		          }
		          catch (Exception e)
		 		  {
		        	  System.out.println(e.getMessage());  
		 		  }

	          
	          for(int i =0;i<opcodekey.size();i++)
	          {
	          	String[] splited = opcodekey.get(i).split("\\s+");
	          	String[] ListString = new String[]{splited[1],splited[2],splited[3]};          	
	          	opcode.put(splited[0], ListString);          	
	          }
	        
	          StringBuilder sb = new StringBuilder();
	          
	          for(byte b:data)
	          {
	          	
	          String s1 = String.format("%1s", Integer.toBinaryString((b+256)%256));
	          String S2 = String.format("%8s", s1).replace(' ', '0');
	          sb.append(S2);           
	          }
	          
	          int i =0;
	          StringBuilder sb1 = new StringBuilder();          
	          boolean breakreached = false;        
	          int z = 600;
	          for (int j = 0; j < sb.length(); j++)
	          {
	              char c = sb.charAt(j);  
	              if (i ==36  && i!=0 && breakreached ==false)
	              { 
	                  sb1.append(c).append(" ");                  
	                  lastsixbits = sb1.toString().substring((sb1.length() - 7), sb1.length()-1);                 
	                  sb1.append(z).append(" ");                 
	                  for (Map.Entry<String, String[]> items : opcode.entrySet())
	                  {
	                  	String Key = items.getKey();
	                  	String[] values = items.getValue();
	                  	
	                      if (values[1].equals("000000")  && (values[2].equals(lastsixbits) && (!lastsixbits.equals("001101") ))) 
	                      {
	                      	
	                      	 iType(firstfivebits, secondfivebits, thirdfivebits, fourthfivebits, sb1, Key,lastsixbits,opcodebits);                       
	                           break;
	                      }
	                      else if (values[1].equals(opcodebits) && (!opcodebits.equals("000000")) && (!opcodebits.equals("000010")) && (!lastsixbits.equals("001101")))
	                      {
	                          if ((Key.equals("BGEZ") && (!secondfivebits.equals("00000"))) || (((Key.equals("BLTZ")) && (!(secondfivebits.equals("00001"))))))
	                          {
	                          	continue;
	                          }
	                          	rType(firstfivebits, secondfivebits, thirdfivebits, fourthfivebits, lastsixbits, sb1, Key);
	                          	break;
	                  		}
	                      else if (opcodebits.equals("000010")&& values[1].equals(opcodebits))
	                      {
	                      	int x = Integer.parseInt((firstfivebits+ secondfivebits+ thirdfivebits+ fourthfivebits+ lastsixbits),2) *4;
	                        sb1.append(Key).append(" ").append("#").append(x);
	                        queue.add(new Que("",Key,"","#"+x));
	                        break;
	                      }
	                      else if (values[1].equals("000000")&& (lastsixbits.equals("001101") && (Key.equals("BREAK"))))
	                      {                      	
	                          sb1.append(Key);
	                          queue.add(new Que("",Key,"",""));
	                          breakreached = true;
	                          break;
	                      }                         
	                  }
	                      
		                sb1.append(System.getProperty("line.separator"));
		                i = 0;
		                z += 4;	               
		            }  
	          	 else if ( i == 5&&breakreached ==false) 
	              {
	              	 sb1.append(c).append(" ");              	
	              	 try
	              	 {              		
	              		 opcodebits = sb1.toString().substring((sb1.length() - 7), sb1.length()-1);
	              	 }
	              	 catch(Exception e)
	              	 {
	              		System.out.println(e.getMessage());  
	              	 }
	              	 
	                 i += 2;
	              }
	              else if ((i == 11)&& i!=0&&breakreached ==false) 
	              {
	              	 sb1.append(c).append(" "); 
	                  firstfivebits = sb1.toString().substring((sb1.length() - 6), sb1.length()-1);                 
	                  i += 2;
	              }
	              else if ((i == 17)&& i!=0 && breakreached ==false) 
	              {
	              	 sb1.append(c).append(" ");
	                  secondfivebits = sb1.toString().substring((sb1.length() - 6), sb1.length()-1);
	                  i += 2;
	              }
	              else if ((i == 23)&& i!=0&& breakreached ==false) 
	              {
	              	 sb1.append(c).append(" ");
	                  thirdfivebits = sb1.toString().substring((sb1.length() - 6), sb1.length()-1);
	                  i += 2;
	              }
	              else if ((i == 29)&& i!=0&& breakreached ==false)
	              {
	              	 sb1.append(c).append(" ");
	                  fourthfivebits = sb1.toString().substring((sb1.length() - 6),sb1.length()-1);
	                  i += 2;
	              } 
	              else if (breakreached&& i!=0)
	              {
	                  sb1.append(c);
	                  if ((i == 31)) 
	                  {
	                	  int dat = Integer.parseInt(sb1.toString().substring(sb1.length()-32,sb1.length()), 2) ;                	  
	                      sb1.append(c).append(" ").append(z).append(" ").append(dat).append(System.getProperty("line.separator"));
	                	  
	                      z += 4;
	                      i = 0;
	                  }
	                  else
	                  {
	                      i++;
	                  }                  
	              }
	              else
	              {              	
	                  sb1.append(c);                   
	                  i++;
	              }              
	      }  
	          
	        	  
	        		
	          
		           thirdgument = args[2];
	          
		          Simulator(args[1]);
	                   
			 }
			 catch (Exception e)
			 {
				 
				 System.out.println(e.getMessage());	
			 }
	                
			 }		
		
		///
		public static void Simulator(String s)
		{
			
			String[] arg = thirdgument.split(":");
			File file = new File(s);
			FileOutputStream fis;
			try {
					fis = new FileOutputStream(file);
					PrintStream out = new PrintStream(fis);
				
			
			//System.setOut(out);     
       	
			
			String[] first =arg[0].split("T");
			startindex = Integer.parseInt(first[1]);
			String[] last =arg[1].split("]");
			endindex = Integer.parseInt(last[0]);
			
			
			initializeComponent();
			
			while(PrgCnt != 716 || StopFetch == false)
			{
				
				if(clockCycle!=0&&clockCycle == endindex+1&& endindex+1 !=1)
				{
					break;
		
				}
				if(BranchNT ==true)
				{	
					if(queue1.isEmpty())
					{
						queue1.add(instrucque.get(PrgCnt));
						if(!rob.isEmpty())
						{
							rob.remove(0);
						}
						PrgCnt= PrgCnt+4;
					
						printOutpput(out,s);
						clockCycle++;
						continue;
						
					}
					else 
					{						
						String Inst = "["+queue1.get(0).Operation + "]";
						ROB r =(new ROB(-1,"NO",Inst,"Commited",queue1.get(0).DetinationR,-1));
						rob.add(r);
						ReservationStation  rs =new ReservationStation(queue1.get(0).Operation,"NO",queue1.get(0).Operation,-1,-1,-1,-1,queue1.get(0).DetinationR,"aj","ak");
						
						if(queue1.get(0).Operation.equals("BREAK"))
						{
							
							
							queue1.remove(0);
							//RS.add(rs); 
							rob.remove(0);
							printOutpput(out, s);
							RS.clear();
							rob.clear();
							clockCycle++;
							StopFetch =true;
							printOutpput(out, s);
							
							return;
						}
						
						RS.add(rs); 
						
						queue1.add(instrucque.get(PrgCnt));
						queue1.remove(0);
						printOutpput(out, s);							
						clockCycle++;
						continue;
					}
					
				}
				
				IFStage(PrgCnt);
			
				Issue(PrgCnt);
				
			
				for(int i=0;i<1;i++)
				{
				
				if(rob.size() > 0 &&rob.get(i).Busy.equals("NO") && rob.get(i).State.equals("Commited"))
				{
					
					if(RS.get(i).Name.equals("J"))
					{
						if(Bt.get(1).set== -1)
						{
							Bt.get(1).set=1;
							RS.clear();
							rob.clear();						
							PrgCnt =Bt.get(1).observed-4;
							count=0;
							break;
						}	
						else if(Bt.get(1).set== 1)
						{
							rob.remove(i);
							RS.remove(i);
							break;
						}
					}
					
					
					if(RS.get(i).Name.equals("BEQ")|RS.get(i).Name.equals("BNE"))
					{
						rob.remove(i);
						RS.remove(i);
						if(Bt.get(0).set ==1)
						{
							rob.clear();
							RS.clear();
							queue1.clear();
							BranchNT=true;
							PrgCnt =Bt.get(0).observed-4;
						}
					
						break;
					}
					
					if(RS.get(i).Name.equals("NOP")&&Bt.get(0).set == 0)
					{	
						rob.remove(i+1);
						RS.remove(i+1);
						rob.remove(i);
						RS.remove(i);
						queue1.clear();						
						break;
					}
					 
					if(RS.get(i).Name.equals("BREAK"))
					{
						///Bt.get(1).set=1;;
						if(BranchNT==false)
						{
							rob.remove(i);
							RS.remove(i);
						}
						//StopFetch =true;
					}
					String[] s1 = rob.get(i).Destination.split("R");
					
					if(!RS.get(i).Name.equals("SW"))
					{
						if(clockCycle==43)
						{
							continue;
						}
						for(int m =0;m<rob.size();m++)
						{
							if( BranchNT == false)
							{
								if(RS.get(m).Vj == Integer.parseInt(s1[1]))
								{
								RS.get(m).Vj=rob.get(i).value;
								RS.get(m).Qj=0;
								break;
								}
							}
							else
							{
								///int j =0;
							}
						}
						if(BranchNT ==false)
						{
							Register.get(Integer.parseInt(s1[1])).Value =	rob.get(i).value;
							Register.get(Integer.parseInt(s1[1])).available = true;
						}
						else
						{
							//to do
						}
					
					}
					else if(RS.get(i).Name.equals("SW"))
					{
					for(int j =i+1; j<RS.size();j++)
					{
						if(RS.get(i).Name.equals("SW"))
						{
							RS.get(i).Vk =rob.get(i).value;
							RS.get(i).Qk=0;
						}
					}
					DS.set(d, Register.get(Integer.parseInt(s1[1])).Value);
					Register.get(Integer.parseInt(s1[1])).available = true;
					d++;
					}
					else
					{
						
					}
					rob.remove(i);
					RS.remove(i);					
				}				
			}
				
			printOutpput(out,s);
			
			PrgCnt= PrgCnt+4;
			clockCycle++;
			count++;
			}
			
			}catch (FileNotFoundException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}

		/**
		 * 
		 */
		private static void initializeComponent() {
			for( int i =0;i<32;i++)
			{
				Register.add(new REG(i));
			}
			
			for(int i=0; i< queue.size() ;i++)
			{
				
				String  sr = ("["+queue.get(i).Operation+ " "  +queue.get(i).DetinationR+ ","+ queue.get(i).SourceR + ","+queue.get(i).Sourceorimmediate +"]");
				IQ.put(600+i*4, sr);
				instrucque.put(600+i*4,queue.get(i));
			}
			
			for(int i=0;i<10;i++)
			{
				DS.add(0);
			}
		}



		/**
		 * 
		 * @param out
		 * @param s
		 */
		private static void printOutpput(PrintStream out,String s)
		{	if(startindex ==0 && endindex !=0)
			{
				
			}
			else if(clockCycle<startindex)
			{
				return;
			}
			else if(endindex == 0)
			{
				
				if(BranchNT == true &&StopFetch ==true )
				{
					
				}
				else
				{
					return;
				}
			}
			
			////output to a file
			System.setOut(out);
			
			
			System.out.println("Cycle:"+"<"+clockCycle+">" );
			System.out.println("Instruction Queue:" );
			for(int i= 0;i<queue1.size();i++)
			{
				if(queue1.get(i).Operation.equals("BREAK"))
				{
					System.out.println("["+queue1.get(i).Operation + "]");	
				}
				else if(queue1.get(i).Operation.equals("NOP"))
				{
					System.out.println("["+queue1.get(i).Operation + "]");
				}
				else if(queue1.get(i).Operation.equals("BGEZ")||queue1.get(i).Operation.equals("BLTZ ")||queue1.get(i).Operation.equals("BLEZ")||queue1.get(i).Operation.equals("BGTZ")||queue1.get(i).Operation.equals("J"))
				{
					System.out.println("["+queue1.get(i).Operation+" " +queue1.get(i).Sourceorimmediate+ "]");
				}
				else
				{
				System.out.println("["+queue1.get(i).Operation + "  "+queue1.get(i).DetinationR+","+queue1.get(i).SourceR +","+queue1.get(i).Sourceorimmediate+"]");
			
				}	
			}
			
			System.out.println("Reservation Station:" );
			{
				for(int i= 0;i<rob.size();i++)
				{
					if(Bt.size()>0&& Bt.get(0).set ==1&& (RS.get(i).Name.equals("NOP")))
					{
						
					
					}
					else
					{
					System.out.println(rob.get(i).Instruction);
					}
				}	
			}		

			System.out.println("ROB:" );
			
			{
				for(int i= 0;i<rob.size();i++)
				{
					System.out.println(rob.get(i).Instruction);
				}	
			}

			System.out.println("BTB:");
			for(int i = 0;i< Bt.size() ;i++)
			{
				if(((Bt.get(0).set ==0 ||Bt.get(0).set ==1))&&i==0)
				{
				System.out.println("["+"Entry" + " "+(i+1)+"]"+"<"+Bt.get(i).predicted+","+Bt.get(i).observed+","+Bt.get(i).set+">");
				}
				else if(Bt.size()>1&&(Bt.get(1).set ==1)&& i==1)
				{
					System.out.println("["+"Entry" + " "+(i+1)+"]"+"<"+Bt.get(i).predicted+","+Bt.get(i).observed+","+Bt.get(i).set+">");
					
				}
			}
			int m = 0;
			for( int j =0;j<32;j++)
			{
				if( m == 0)
				{
					if(j<10)
					{
						System.out.print("Registers" + "0"+j + ":" + "   " +Register.get(j).Value);
					}
					else
					{
						System.out.print("Registers" +j + ":" + "   " +Register.get(j).Value);
					}
				}
				else if( m <8 && m>0)
				{
					
				System.out.print("   " + Register.get(j).Value + "   ");
					
				}
				else 
				{
					m =0;
					System.out.println();
					System.out.print("Registers" + j + ":" + "   " +Register.get(j).Value);
				}
			m++;
			}
			
			System.out.println();
			System.out.print("716:");
			for(int i = 0;i < 10;i++)
			{
				System.out.print("  "+DS.get(i));
			}			
			System.out.println();
			 
			
		}
		
		
		private static void IFStage(int prgCounter) 
		{	
			Que x =instrucque.get(prgCounter);
			if(x!=null)
			{
				queue1.add(x);
		
				if(instrucque.get(prgCounter).Operation.equals("BEQ")||instrucque.get(prgCounter).Operation.equals("BNE"))
				{
					if(Bt.size()>0 && Bt.get(0).observed !=prgCounter+8)
					{
						Bt.clear();
						Bt.add(new BTB(prgCounter,prgCounter+8,-1));
					}
					else
					{
						Bt.add(new BTB(prgCounter,prgCounter+8,-1));
					}
				}
				if(instrucque.get(prgCounter).Operation.equals("J"))
				{
					if(Bt.size()>1)
					{
						if(Bt.get(0).set == 0 && Bt.get(1).set ==1)						
						{
							PrgCnt = Bt.get(1).observed-4;
						}
						else if(Bt.get(0).set ==1 && Bt.get(1).set ==1)
						{
							PrgCnt = Bt.get(0).observed-4;
						}
						else
						{
							PrgCnt = Bt.get(0).observed-4;
						}
							
					}
					else
					{
					String[] sp = x.Sourceorimmediate.split("#");
						Bt.add(new BTB(prgCounter,Integer.parseInt(sp[1]),-1));
					}
				}
			}
		else
		{
			queue1.remove(x);
		}
			
		}


		/**
		 * 
		 * @param prgCounter
		 */
		private static void Issue(int prgCounter) 
		{
		
			if(clockCycle<2)
			{
				return;
			}
			
			if(count<2)
			{
				return;
			}
			
			
			////////do othersstages of pipeline
			if(BranchNT == false)
			{
				for(int i =rob.size()-1;i>-1;i--)
				{
					if(rob.get(i).State.equals("Issued"))
					{
						Execute(i);
					}
					else if(rob.get(i).State.equals("Executed"))
					{
						Writeresult(i);
					}
					else if(rob.get(i).State.equals("Written"))
					{
						Commit(i);
					}				
				}			
			}
			
			
			if(queue1.size() == 0)
			{				
				return;
			}
			
			if(rob.size()<6)
			{
				Que x = queue1.get(0);
				
				
			if(x.Operation.equals("NOP") || x.Operation.equals("BREAK")||x.Operation.equals("J"))
				{
					if(x.Operation.equals("J"))
					{
						ReservationStation rs =new ReservationStation(queue1.get(0).Operation,"NO",queue1.get(0).Operation,-1,-1,-1,-1,queue1.get(0).DetinationR,"aj","ak");
						
						RS.add(rs);
		
						String Inst = "["+queue1.get(0).Operation + " "+ queue1.get(0).Sourceorimmediate+"]" ;
						ROB r =(new ROB(-1,"NO",Inst,"Commited",queue1.get(0).DetinationR,-1));
						rob.add(r);
						queue1.remove((0));
						
					}
					else
					{
					ReservationStation rs =new ReservationStation(queue1.get(0).Operation,"NO",queue1.get(0).Operation,-1,-1,-1,-1,queue1.get(0).DetinationR,"aj","ak");
						
					RS.add(rs);
					String Inst = "["+queue1.get(0).Operation + "]" ;
					ROB r =(new ROB(-1,"NO",Inst,"Commited",queue1.get(0).DetinationR,-1));
					rob.add(r);
					
					queue1.remove((0));
					
						if(x.Operation.equals("SW"))
						{
							
						}
					}
				}	
				else  if(x !=null)
				{
					
				String[] Dest = queue1.get(0).DetinationR.split("R");
				String[] Source= queue1.get(0).SourceR.split("R");
				String[] Source1= queue1.get(0).Sourceorimmediate.split("#");
				String[] Sourcimm= queue1.get(0).Sourceorimmediate.split("R");
				
				
				IntializeROBRS(x, Source, Source1, Sourcimm);
			
							
			
			if(!(x.Operation.equals("NOP")||x.Operation.equals("BREAK")||(x.Operation.equals("J")||(x.Operation.equals("BEQ")||x.Operation.equals("BNE")))))
			{
				Register.get(Integer.parseInt(Dest[1])).available  = false;
			}
			
			if((Dest.length>1 &&Register.get(Integer.parseInt(Dest[1])).available == true) )
			{
				///to do///
			}
			queue1.remove((0));
			}
		}
	}

		/**
		 * @param x
		 * @param Source
		 * @param Source1
		 * @param Sourcimm
		 * @throws NumberFormatException
		 */
		private static void IntializeROBRS(Que x, String[] Source, String[] Source1, String[] Sourcimm)
				throws NumberFormatException {
			ReservationStation rs =new ReservationStation(queue1.get(0).Operation,"Yes",queue1.get(0).Operation,-1,-1,-1,-1,queue1.get(0).DetinationR,"aj","ak");
			RS.add(rs);
			if(queue1.get(0).Operation.equals("LW")||queue1.get(0).Operation.equals("SW"))
			{
				String Inst = "["+queue1.get(0).Operation + "   " +queue1.get(0).DetinationR+","+queue1.get(0).SourceR +"("+queue1.get(0).Sourceorimmediate+")"+"]";
				ROB r =(new ROB(-1,"Yes",Inst,"Issued",queue1.get(0).DetinationR,-1));
				rob.add(r);
			}
			else
			{
				String Inst = "["+queue1.get(0).Operation + "   " +queue1.get(0).DetinationR+","+queue1.get(0).SourceR +","+queue1.get(0).Sourceorimmediate+"]";

			
			ROB r =(new ROB(-1,"Yes",Inst,"Issued",queue1.get(0).DetinationR,-1));
			rob.add(r);
			}
			



			IinitialOperation(x, Source, Source1, Sourcimm, rs);
		}

		/**
		 * @param x
		 * @param Source
		 * @param Source1
		 * @param Sourcimm
		 * @param rs
		 * @throws NumberFormatException
		 */
		private static void IinitialOperation(Que x, String[] Source, String[] Source1, String[] Sourcimm,
				ReservationStation rs) throws NumberFormatException {
			if(x.Sourceorimmediate.contains("R") && Register.get(Integer.parseInt(Sourcimm[1])).available == true)
			{
				rs.Vk=Register.get(Integer.parseInt(Sourcimm[1])).Value;
				rs.Qk =0;
				
			}			
			else if(x.Sourceorimmediate.contains("R") && (Register.get(Integer.parseInt(Sourcimm[1])).available == false))
			{
				rs.Vk=Integer.parseInt(Sourcimm[1]);
				rs.Qk =-1;
				
			}
			else if(x.Sourceorimmediate.contains("#"))
			{
				rs.Vk=Integer.parseInt(Source1[1]);
				rs.Qk =0;
				
			}


			if(x.SourceR.contains("R") && Register.get(Integer.parseInt(Source[1])).available == true)
			{
						rs.Vj=Register.get(Integer.parseInt(Source[1])).Value;
						rs.Qj =0;
						
			}
			else if(x.SourceR.contains("R") && Register.get(Integer.parseInt(Source[1])).available == false)
			{	
						rs.Locj =Source[1];
						rs.Vj=Integer.parseInt(Source[1]);
						rs.Qj =-1;
			}
			else
			{
						rs.Vj=-11;
						rs.Qj =-1;
			}
		}


		/**
		 * 
		 * @param i
		 */
		private static void Execute(int i)
		{			
			if(RS.get(i).Name.equals("BEQ")||RS.get(i).Name.equals("J"))
			{
				rob.get(i).Busy = "NO";
				RS.get(i).Busy = "NO";
				rob.get(i).State = "Commited";
				if(RS.get(i).Name.equals("BEQ"))
				{
				String[] Dest = RS.get(i).DEst.split("R");
				int val1 = Register.get(Integer.parseInt(Dest[1])).Value + RS.get(i).Vk;
				int val2= RS.get(i).Vj;
				if(val1==val2)
				{
					Bt.get(0).set =1;
				}
				if(Bt.get(0).set == -1)
				{
					Bt.get(0).set =0;
				}
				return;
				}
			}
			
			
				StoreOperation(i);
				Floatingpoint_operation(i);
				Loadoperation(i);
		}

		/**
		 * @param i
		 */
		private static void Loadoperation(int i) 
		{
			if(RS.get(i).Name.equals("LW"))
			{				
				if(RS.get(i).Qk ==-1)
				{
					int c = 716+Register.get(RS.get(i).Vk).Value-716;
					if((Register.get(RS.get(i).Vk)).available == true)
					{
						
						RS.get(i).Vk = DS.get((c/4));
						RS.get(i).Vj =0;
						RS.get(i).Qk=0;
						RS.get(i).Qj =0;
						rob.get(i).State="Executed";
						rob.get(i).value = DS.get((c/4));
					}
					else if((Register.get(RS.get(i).Vk)).available == false && clockCycle<34 )
					{
						for(int j =0;j<i;j++)
						{
							if(RS.get(j).Name.equals("J"))
							{
								continue;
							}
							String dest[] = RS.get(j).DEst.split("R");
							if((dest[1].equals(Integer.toString(RS.get(i).Vk)) && rob.get(j).State.equals("Commited")))
							{
								 c = 716+rob.get(j).value -716;
								RS.get(i).Vk = DS.get((c/4));
								RS.get(i).Vj =0;
								RS.get(i).Qk=0;
								RS.get(i).Qj =0;
								rob.get(i).State="Executed";								
								rob.get(i).value = DS.get((c/4));
							}
						}	
					}
					else 
					{
						for(int j =0;j<i;j++)
						{
							if(RS.get(j).Name.equals("J"))
							{
								continue;
							}
							String dest[] = RS.get(j).DEst.split("R");
							if((dest[1].equals(Integer.toString(RS.get(i).Vk))&&rob.get(j).State.equals("Executed")))
							{
								 c = 716+rob.get(j).value -716;
								RS.get(i).Vk = DS.get((c/4));
								RS.get(i).Vj =0;
								RS.get(i).Qk=0;
								RS.get(i).Qj =0;
								rob.get(i).State="Executed";								
								rob.get(i).value = DS.get((c/4));
							}
						}
					}						
				}
			}
		}

		/**
		 * @param i
		 */
		private static void StoreOperation(int i)
		{
			if(RS.get(i).Name.equals("SW"))
			{
				if(RS.get(i).Qj ==-1)
				{
					if(Register.get(RS.get(i).Vk).available == true &&(!rob.get(i).State.endsWith("Executed")))
					{
						int c = RS.get(i).Vk;
						RS.get(i).Vk = DS.get((c/4));
						rob.get(i).value = DS.get((c/4));
						RS.get(i).Vj =0;
						RS.get(i).Qk=0;
						RS.get(i).Qj =0;
						rob.get(i).State="Commited";
						rob.get(i).Busy = "NO";
						RS.get(i).Busy = "NO";
		
						
					}
			
					else if(Register.get(RS.get(i).Vk).available == false && RS.get(i).Qk==0 &&(!rob.get(i).State.endsWith("Executed")))
					{
						int c =RS.get(i).Vk;
						RS.get(i).Vk = DS.get((c/4));
						rob.get(i).value = DS.get((c/4));
						RS.get(i).Vj =0;
						RS.get(i).Qk=0;
						RS.get(i).Qj =0;
						rob.get(i).State="Commited";
						rob.get(i).Busy = "NO";
						RS.get(i).Busy = "NO";
		
						
					}
									
					}
				}
		}

		/**
		 * @param i
		 */
		private static void Floatingpoint_operation(int i) 
		{
			if(RS.get(i).Qj == 0 &&RS.get(i).Qk == 0  &&(!RS.get(i).Name.equals("LW"))&&(!RS.get(i).Name.equals("SW")))				
			{
				rob.get(i).value =(RS.get(i).Vj + RS.get(i).Vk);				
				rob.get(i).State = "Executed";									
			}	
			else if(RS.get(i).Name.equals("J")||RS.get(i).Name.equals("BREAK"))
			{
				rob.get(i).Busy = "NO";
				RS.get(i).Busy = "NO";
				rob.get(i).State = "Commited";				
			}
			else if(RS.get(i).Qj ==-1 && (!RS.get(i).Name.equals("LW"))&&(!RS.get(i).Name.equals("SW")))
			{
				
				for( int j=0;j<i;j++)
				{ 
					if(RS.get(j).Name.equals("J") ||RS.get(j).Name.equals("BEQ"))
					{
						continue;
					}
					
					 if(RS.get(i).Qj !=0 &&Register.get(RS.get(i).Vj).available == true)
					{
						RS.get(i).Vj =Register.get(RS.get(i).Vj).Value;
						RS.get(i).Qj =0;
						if(RS.get(i).Qk ==0)
						{
							rob.get(i).value =(RS.get(i).Vj + RS.get(i).Vk);
							
							rob.get(i).State = "Executed";
						}
						
					}
				}
			}
			else if(RS.get(i).Qk ==-1 && (!RS.get(i).Name.equals("LW"))&&(!RS.get(i).Name.equals("SW")))
			{
				for( int j=0;j<i;j++)
				{ 
					if(RS.get(j).Name.equals("J"))
					{
						continue;
					}
					
					String[] dest = RS.get(j).DEst.split("R");
					if(dest[1].equals(Integer.toString(RS.get(i).Vk))&&rob.get(j).value!=-1&&(rob.get(j).State.equals("Written")||rob.get(j).State.equals("Commited")))
					{
						RS.get(i).Vk =rob.get(j).value;
						RS.get(i).Qk =0;
						if(RS.get(i).Qj ==0)
						{
							rob.get(i).value =(RS.get(i).Vj + RS.get(i).Vk);
							
							rob.get(i).State = "Executed";
						}
					}
					else if(Register.get(RS.get(i).Vk).available == true)
					{
						RS.get(i).Vk =Register.get(RS.get(i).Vk).Value;
						RS.get(i).Qk =0;
						if(RS.get(i).Qj ==0)
						{
							rob.get(i).value =(RS.get(i).Vj + RS.get(i).Vk);							
							rob.get(i).State = "Executed";
						}
						
					}
				}
			}
		}
		
		/**
		 * 
		 * @param i
		 */
		private static void Writeresult(int i) 
		{
			if(clockCycle<4)
			{
				return;
			}
			
			if(RS.size() > i+1)
			{
			
			for(int j =i+1; j<RS.size();j++)			
			{				
				String [] split = rob.get(i).Destination.split("R");
							
				if(!(RS.get(j).Name.equals("BEQ")||RS.get(j).Name.equals("J")))
				{
				if(RS.get(j).Qj !=0 &&RS.get(j).Locj.equals(split[1])&& (!RS.get(i).Name.equals("SW")&& rob.get(i).value !=-1))
					{
						RS.get(j).Vj = rob.get(i).value;
						RS.get(j).Qj=0;
						
					}				
				}
			}	 
				 if(RS.get(i).Name.equals("LW"))
				 {
					 String[] d = RS.get(i+1).DEst.split("R");
						String [] split = rob.get(i).Destination.split("R");
					
						if(RS.get(i+1).Qj!=0 &&d[1].equals(split[1]))
						{
							RS.get(i+1).Vj = rob.get(i).value;
							RS.get(i+1).Qj=0;							
						}
						 if(RS.get(i+1).Qk!=0 &&d[1].equals(split[1]))
						{
							RS.get(i+1).Vk = rob.get(i).value;
							RS.get(i+1).Qk=0;
							
						}
					
				 }
					
			}
			
				rob.get(i).State = "Written";
			}
		
		/**
		 * 
		 * @param i
		 */
		private static void Commit(int i) 
		{
			
			if(clockCycle<5)
			{
				return;
			}
			
					if(rob.get(i).value > -1  || RS.get(0).Name.equals("J"))
				{
					rob.get(i).Busy = "NO";
					RS.get(i).Busy = "NO";
					rob.get(i).State = "Commited";			
					
				}
					for(int j =i+1; j<RS.size();j++)
					{
						if(RS.get(i).Name.equals("SW"))
						{
							RS.get(i).Vk =rob.get(i).value;
							RS.get(i).Qk=0;
						}
					}
		}


		/**
		 * 
		 * @param opcodekey
		 */
		private static void opcodeKeyCollectionBuilding(List<String> opcodekey)
		{
				opcodekey.add(0,"SW I 101011 222222");
				opcodekey.add(1,"J N 000010 011110");
				opcodekey.add(2,"BREAK R 000000 001101");			
				opcodekey.add(3,"XOR R 000000 100110");
				opcodekey.add(4,"OR R 000000 100101");
				opcodekey.add(5,"AND R 000000 100100");
				opcodekey.add(6,"SRA R 000000 000011");
				opcodekey.add(7,"SRL R 000000 000010");
				opcodekey.add(8,"SLL R 000000 000000");
				opcodekey.add(9,"SLTU R 000000 101011");
				opcodekey.add(10,"SLT R 000000 101010");
				opcodekey.add(11,"SUB R 000000 100010");
				opcodekey.add(12,"SUBU R 000000 100011");
				opcodekey.add(13,"ADD R 000000 100000");
				opcodekey.add(14,"ADDU R 000000 100001");
				opcodekey.add(15,"NOR R 000000 100111");
				opcodekey.add(16,"NOP R 000000 000000");
				opcodekey.add(17,"SLTI I 001010 222222");
				opcodekey.add(18,"SLTI I 001010 222222");
				opcodekey.add(19,"ADDIU I 001001 222222");
				opcodekey.add(20,"ADDI I 001000 222222");
				opcodekey.add(21,"BGEZ I 000001 222222");
				opcodekey.add(22,"BGTZ I 000111 222222");
				opcodekey.add(23,"BLEZ I 000110 222222");
				opcodekey.add(24,"BLTZ I 000001 222222");
				opcodekey.add(25,"BNE I 000101 222222");          		
				opcodekey.add(26,"BEQ I 000100 222222");
				opcodekey.add(27,"LW I 100011 222222");
		}

		/**
		 * 
		 * @param firstfivebits
		 * @param secondfivebits
		 * @param thirdfivebits
		 * @param fourthfivebits
		 * @param sb1
		 * @param Key
		 * @param lastsixbits
		 * @param opcodebits
		 */
		private static void iType(String firstfivebits, String secondfivebits, String thirdfivebits, String fourthfivebits,
				StringBuilder sb1, String Key,String lastsixbits, String opcodebits) 
		{
			if(Integer.parseInt(opcodebits+ firstfivebits+secondfivebits+thirdfivebits+fourthfivebits+lastsixbits, 2) ==0)
			{
				Key = "NOP";
			}
			switch (Key) 
			 {
			     case "ADD":
			         sb1.append((Key + (" " + ("R" 
			                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
			                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
			         queue.add(new Que("R"+ (Integer.parseInt(thirdfivebits, 2)),Key,("R"  + (Integer.parseInt(firstfivebits, 2))),"R" + Integer.parseInt(secondfivebits, 2)));
			         break;
			     case "ADDU":
			         sb1.append((Key + (" " + ("R" 
			                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
			                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
			         queue.add(new Que("R"+ (Integer.parseInt(thirdfivebits, 2)),Key,("R"  + (Integer.parseInt(firstfivebits, 2))),"R" + Integer.parseInt(secondfivebits, 2)));
			         break;
			     case "SUB":
			         sb1.append((Key + (" " + ("R" 
			                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
			                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
			         queue.add(new Que("R"+ (Integer.parseInt(thirdfivebits, 2)),Key,("R"  + (Integer.parseInt(firstfivebits, 2))),"R" + Integer.parseInt(secondfivebits, 2)));
			         break;
			     case "SUBU":
			         sb1.append((Key + (" " + ("R" 
			                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
			                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
			         queue.add(new Que("R"+ (Integer.parseInt(thirdfivebits, 2)),Key,("R"  + (Integer.parseInt(firstfivebits, 2))),"R" + Integer.parseInt(secondfivebits, 2)));
			         break;
			     case "SLT":
			         sb1.append((Key + (" " + ("R" 
			                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
			                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
			         queue.add(new Que("R"+ (Integer.parseInt(thirdfivebits, 2)),Key,("R"  + (Integer.parseInt(firstfivebits, 2))),"R" + Integer.parseInt(secondfivebits, 2)));
			         break;
			     case "SLTU":
			         sb1.append((Key + (" " + ("R" 
			                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
			                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
			         queue.add(new Que("R"+ (Integer.parseInt(thirdfivebits, 2)),Key,("R"  + (Integer.parseInt(firstfivebits, 2))),"R" + Integer.parseInt(secondfivebits, 2)));
			         break;
			     case "SLL":
			         sb1.append((Key + (" " + ("R" 
			                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
			                         + (Integer.parseInt(secondfivebits, 2) + ("," + ("H" + Integer.parseInt(fourthfivebits, 2)))))))))));
			         queue.add(new Que("R"+ (Integer.parseInt(thirdfivebits, 2)),Key,("R"  + (Integer.parseInt(firstfivebits, 2))),"R" + Integer.parseInt(fourthfivebits, 2)));
			         break;
			     case "SRL":
			         sb1.append((Key + (" " + ("R" 
			                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
			                         + (Integer.parseInt(secondfivebits, 2) + ("," + Integer.parseInt(fourthfivebits, 2))))))))));
			         queue.add(new Que("R"+ (Integer.parseInt(thirdfivebits, 2)),Key,("R"  + (Integer.parseInt(firstfivebits, 2))),"R" + Integer.parseInt(fourthfivebits, 2)));
			         break;
			     case "SRA":
			         sb1.append((Key + (" " + ("R" 
			                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
			                         + (Integer.parseInt(secondfivebits, 2) + (","+ Integer.parseInt(fourthfivebits, 2))))))))));
			         queue.add(new Que("R"+ (Integer.parseInt(thirdfivebits, 2)),Key,("R"  + (Integer.parseInt(firstfivebits, 2))),"R" + Integer.parseInt(fourthfivebits, 2)));
			         break;
			     case "AND":
			         sb1.append((Key + (" " + ("R" 
			                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
			                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
			         queue.add(new Que("R"+ (Integer.parseInt(thirdfivebits, 2)),Key,("R"  + (Integer.parseInt(firstfivebits, 2))),"R" + Integer.parseInt(secondfivebits, 2)));
			         break;
			     case "OR":
			         sb1.append((Key + (" " + ("R" 
			                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
			                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
			         queue.add(new Que("R"+ (Integer.parseInt(thirdfivebits, 2)),Key,("R"  + (Integer.parseInt(firstfivebits, 2))),"R" + Integer.parseInt(secondfivebits, 2)));
			         break;
			     case "XOR":
			         sb1.append((Key + (" " + ("R" 
			                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
			                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
			         queue.add(new Que("R"+ (Integer.parseInt(thirdfivebits, 2)),Key,("R"  + (Integer.parseInt(firstfivebits, 2))),"R" + Integer.parseInt(secondfivebits, 2)));
			         break;
			     case "NOR":
			         sb1.append((Key + (" " + ("R" 
			                         + (Integer.parseInt(thirdfivebits, 2) + ("," + ("R" 
			                         + (Integer.parseInt(firstfivebits, 2) + ("," + ("R" + Integer.parseInt(secondfivebits, 2)))))))))));
			         queue.add(new Que("R"+ (Integer.parseInt(thirdfivebits, 2)),Key,("R"  + (Integer.parseInt(firstfivebits, 2))),"R" + Integer.parseInt(secondfivebits, 2)));
			         break;
			     case "NOP":
			         sb1.append("NOP");
			         queue.add(new Que("",Key,"",""));
			         break;
			 }
		}
		
		
		/**
		 * 
		 * @param firstfivebits
		 * @param secondfivebits
		 * @param thirdfivebits
		 * @param fourthfivebits
		 * @param lastsixbits
		 * @param sb1
		 * @param Key
		 */
		private static void rType(String firstfivebits, String secondfivebits, String thirdfivebits, String fourthfivebits,
				String lastsixbits, StringBuilder sb1, String Key)
		{
			String val = thirdfivebits.toString().substring((thirdfivebits.length() - 5),1);
			int immed;
			
			int offset = Integer.parseInt((thirdfivebits 
	                + fourthfivebits + lastsixbits), 2);
	    	String shiftedi = Integer.toBinaryString(offset<<2);
		
			
			if( val.equals("1"))
			{
				immed = Integer.parseInt(thirdfivebits 
	                  + fourthfivebits + lastsixbits, 2)-65536;
			}
			else
			{
				immed = Integer.parseInt(thirdfivebits 
	                  + fourthfivebits + lastsixbits, 2);
			}
			
			
			switch (Key) 
			{
			        case "LW":
			            sb1.append((Key + (" " + ("R" 
			                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " 
			                            + (Integer.parseInt((thirdfivebits 
			                                + (fourthfivebits + lastsixbits)), 2) + ("(" + ("R" 
			                            + (Integer.parseInt(firstfivebits, 2) + ")")))))))))));
			            queue.add(new Que(("R"+ (Integer.parseInt(secondfivebits, 2))),Key,(Integer.toString(Integer.parseInt((thirdfivebits 
	                            + (fourthfivebits + lastsixbits)), 2))),("R" 
			                            + (Integer.parseInt(firstfivebits, 2)))));	
			            break;
			        case "SW":
			            sb1.append((Key + (" " + ("R" 
			                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " 
			                            + (Integer.parseInt((thirdfivebits 
			                                + (fourthfivebits + lastsixbits)), 2) + ("(" + ("R" 
			                            + (Integer.parseInt(firstfivebits, 2) + ")")))))))))));
			            queue.add(new Que(("R"+ (Integer.parseInt(secondfivebits, 2))),Key,(Integer.toString(Integer.parseInt((thirdfivebits 
	                            + (fourthfivebits + lastsixbits)), 2))),("R" 
			                            + (Integer.parseInt(firstfivebits, 2)))));	
			            
			          
			            break;
			        case "ADDI":
			        	
			            sb1.append((Key + " " + "R" 
			                            + Integer.parseInt(secondfivebits, 2) + "," + " " + "R" 
			                            + Integer.parseInt(firstfivebits, 2) + "," + " " + "#" + immed));
			            queue.add(new Que("R"+ (Integer.parseInt(secondfivebits, 2)),Key,("R"+Integer.parseInt(firstfivebits, 2)),"#" +immed));	
			            break;
			        case "ADDIU":
			        	
			            sb1.append((Key + (" " + ("R" 
			                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("R" 
			                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " + ("#" +immed ))))))))))));
			            queue.add(new Que("R"+ (Integer.parseInt(secondfivebits, 2)),Key,("R"+Integer.parseInt(firstfivebits, 2)),"#" +immed));	
			            break;
			        case "SLTI":
			            sb1.append((Key + (" " + ("R" 
			                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("R" 
			                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " + ("#" + Integer.parseInt((thirdfivebits 
			                                + (fourthfivebits + lastsixbits)), 2)))))))))))));
			            queue.add(new Que("R"+ (Integer.parseInt(secondfivebits, 2)),Key,("R"+Integer.parseInt(firstfivebits, 2)),"#" +Integer.parseInt((thirdfivebits 
                                + (fourthfivebits + lastsixbits)), 2)));	
			            break;
			        case "BEQ":
			        	
			            sb1.append((Key + (" " + ("R" 
			                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " + ("R" 
			                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("#" + Integer.parseInt(shiftedi, 2)))))))))))));
			           
			            queue.add(new Que("R"+ (Integer.parseInt(firstfivebits, 2)),Key,("R"+Integer.parseInt(secondfivebits, 2)),"#" + Integer.parseInt(shiftedi, 2)));
			            break;
			        case "BNE":
			        	
			            sb1.append((Key + (" " + ("R" 
			                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " + ("R" 
			                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("#" +  Integer.parseInt(shiftedi, 2)))))))))))));
			            queue.add(new Que("R"+ (Integer.parseInt(firstfivebits, 2)),Key,("R"+Integer.parseInt(secondfivebits, 2)),"#" + Integer.parseInt(shiftedi, 2)));
			            break;
			        case "BGEZ":
			            sb1.append((Key + (" " + ("R" 
			                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " 
			                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("#" + Integer.parseInt((shiftedi), 2))))))))))));
			            queue.add(new Que("R"+ (Integer.parseInt(firstfivebits, 2)),Key,("R"+Integer.parseInt(secondfivebits, 2)),"#" + Integer.parseInt(shiftedi, 2)));
			            break;
			        case "BGTZ":
			            sb1.append((Key + (" " + ("R" 
			                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " 
			                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("#" + Integer.parseInt((shiftedi), 2))))))))))));
			            queue.add(new Que("R"+ (Integer.parseInt(firstfivebits, 2)),Key,("R"+Integer.parseInt(secondfivebits, 2)),"#" + Integer.parseInt(shiftedi, 2)));
			            break;
			        case "BLEZ":
			            sb1.append((Key + (" " + ("R" 
			                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " 
			                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("#" + Integer.parseInt(shiftedi, 2))))))))))));
			            queue.add(new Que("R"+ (Integer.parseInt(firstfivebits, 2)),Key,("R"+Integer.parseInt(secondfivebits, 2)),"#" + Integer.parseInt(shiftedi, 2)));
			            break;
			        case "BLTZ":
			            sb1.append((Key + (" " + ("R" 
			                            + (Integer.parseInt(firstfivebits, 2) + ("," + (" " 
			                            + (Integer.parseInt(secondfivebits, 2) + ("," + (" " + ("#" + Integer.parseInt(shiftedi, 2))))))))))));
			            queue.add(new Que("R"+ (Integer.parseInt(firstfivebits, 2)),Key,("R"+Integer.parseInt(secondfivebits, 2)),"#" + Integer.parseInt(shiftedi, 2)));
			            break;
			    }
		}
		
	
	
	
	}