import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.Vector;

import org.apache.bcel.classfile.Attribute;
import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;

public class DisplayDetails {
    /**
     * @param args
     * @throws IOException 
     * @throws ClassFormatException 
     */
	static Vector<Integer> OriginalFileVals=new Vector(); 
	static Vector<Integer> ObfFileVals=new Vector(); 
	static Vector<Integer> Diffmetrics=new Vector(); 
    static String CmdArg="";
    static boolean isFirstRun=true;
    
	public static void main(String[] args) {

        Scanner scanner=null;
		if(args.length!=0)
			CmdArg=args[0];
        try {
        	System.out.println("*******Commmand Options*********");
        	System.out.println();
        	System.out.println("Enter 'm' to compare difference in methods ");
        	System.out.println("Enter 's' to compare difference in size ");
        	System.out.println("Enter 'f' to compare difference in fields ");
        	System.out.println("Enter 'a' to compare difference in attributes ");
        	System.out.println("Enter 'c' to compare difference in constat pool ");
        	System.out.println("Default is overall obfuscation rating");
        	System.out.println();
        	System.out.println();
            scanner = new Scanner(System.in);
            String check="y";
            String newArg="";
            while (!check.equalsIgnoreCase("N")) {
            	
                System.out.print("Enter path to file : ");
                String input = scanner.next();
                System.out.println("-----------------------\n");

		        System.out.println("*******Analyzing*********");
                getFiles(input);
                System.out.println();
                System.out.println();
                System.out.print("Do you wish to analyze another obfuscator directory? : Y/N ");
                check=scanner.next();
                System.out.println();
                if(!check.equalsIgnoreCase("n"))
                	{	
                	System.out.print("Please enter a command option; press any other key for char key for default  ");
                	newArg=scanner.next();
                	}
                System.out.println();
                if(newArg!=null){
                	if(newArg.equalsIgnoreCase("a"))
                
                	CmdArg="a";
                else if(newArg.equalsIgnoreCase("m"))
                	CmdArg="m";
                else if(newArg.equalsIgnoreCase("s"))
                	CmdArg="s";
                else if(newArg.equalsIgnoreCase("f"))
                	CmdArg="f";
                else if(newArg.equalsIgnoreCase("c"))
                	CmdArg="c";
                //else if(newArg.equals(scanner.))
                	//CmdArg="";
                else 
                	CmdArg="";
                }
                OriginalFileVals.removeAllElements();
            	ObfFileVals.removeAllElements();
            	Diffmetrics.removeAllElements();
            	isFirstRun=true;

                }
            System.out.println();
            System.out.println("*******Done*********");
            System.exit(0);
        
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                
                    scanner.close();
                
            }
        }

    }
	
	private static void getFiles(String input) throws IOException {
		
		//Create a file pointing to the string input
		File file = new File(input);
//		List <File> inputFiles = new ArrayList<File>();
//		if (file.isDirectory()) {
//			//get all the files, and loop x times
//			for (File temp:file.listFiles()) {
//				if(!temp.isFile())
//					continue;
//				
//				inputFiles.add(temp); //get all files of type class within this directory
//			}
//		} else {
//			//we do it once for the single file we point to.
//			inputFiles.add(file);
//		}
//		for (File f: inputFiles) {
//			readFile(f);
//		}
		FolderTraverse fw = new FolderTraverse();
        fw.walk(input);
        if(fw.foundFiles.isEmpty()){
        	System.out.println("No Files Found!");
        	return;
        }
        
        //Now let's collect the files together
        for (Map.Entry<String, File> entry:fw.foundFiles.entrySet()) {
        	if (!entry.getKey().contains("obf")) {
        		File f1 = entry.getValue();
        		File f2 = fw.foundFiles.get(entry.getKey() + "obf");
        		
        		readFile(f1);
        		readFile(f2);
        		//clear vectors here cause we just compared the two
        		OriginalFileVals.removeAllElements(); 
        		ObfFileVals.removeAllElements(); 
        		Diffmetrics.removeAllElements(); 
        		isFirstRun = true;
        	}
        }
	}
	// private Set mVisitors = new HashSet(); 
 
    
    
	private static void readFile(File file) {
		
		int TMACount=0;//Total attributes in class
		int MCount=0;
		int FCount=0;
		int CPCount=0;
		int AttCount=0;
		int BCount=0;
		int Alen=0;
		
		
		FileInputStream fis;
		try {
			fis = new FileInputStream(file);

		    /*Parse the class */
	        ClassParser parser=new ClassParser(fis, "DirExplorer.class");
	        JavaClass javaClass=parser.parse();
	        
	       for(Constant CP:javaClass.getConstantPool().getConstantPool()){
	        	
	        	CPCount++;
	        	
	        }   
	        
	        for(Attribute A:javaClass.getAttributes()){
	        	
	        	AttCount++;
	            
	        }
	        
	        for(Field field:javaClass.getFields()){
	        
	        	FCount++;
	        
	        }
	       
	        for(byte Byte:javaClass.getBytes()){
	        	
	        	BCount++;

	        }
	        
	        for(Method method:javaClass.getMethods()){
	     
	        	for(Attribute MA:method.getAttributes()){
		        	Alen+=MA.getLength();
		        
		            TMACount++;
		            
		        }
	        	
	        	MCount++;
		    
	        }
	        
	        String Cname=javaClass.getClassName();
	    
	        
	        System.out.println();
	        System.out.println();
	        String fileNameOnly = file.getName().replaceFirst("[.][^.]+$", "");
	        if(fileNameOnly.endsWith("obf".toLowerCase())){
	        	
	        	ObfFileVals.add(BCount);
	        	ObfFileVals.add(MCount);
	        	ObfFileVals.add(FCount);
	        	ObfFileVals.add(TMACount);
	        	ObfFileVals.add(CPCount);
	        	ObfFileVals.add(Alen);
	      
	        }
	        else{
	        	OriginalFileVals.add(BCount);
	        	OriginalFileVals.add(MCount);
	        	OriginalFileVals.add(FCount);
	        	OriginalFileVals.add(TMACount);
	        	OriginalFileVals.add(CPCount);
	        	OriginalFileVals.add(Alen);
	      
	        }
	        if(isFirstRun){
	        	isFirstRun=false;
	        	if(fileNameOnly.endsWith("obf".toLowerCase())){
        			fileNameOnly = fileNameOnly.substring(0, fileNameOnly.length() - 3);
        		}
        		System.out.println("Now Analyzing: " + fileNameOnly);

	        }
	        else
	        {	int diff=0;
	        	for(int i=0;i<6;i++){
	        		diff=Math.abs(OriginalFileVals.elementAt(i)-ObfFileVals.elementAt(i));
	        		Diffmetrics.add(diff);
	        	}
	        }
	        
	        if(Diffmetrics.size()!=0 && Diffmetrics!=null){
	        	
	        	if(CmdArg.equalsIgnoreCase("m")){
		        	
		        	System.out.println("Number of Methods modified in: " +Cname+".class  == "+ Diffmetrics.elementAt(1));
			        
		        	
		        }
		        
	        	else if(CmdArg.equalsIgnoreCase("s")){
		        	
		        	System.out.println("Difference in size between obfuscated and original " +Cname+".class== "+ Diffmetrics.elementAt(0)+ " bytes");
			
		        }
		        
		        else if(CmdArg.equalsIgnoreCase("f")){
		        	
		        	System.out.println("Number of fields modified by obfuscation in: " +Cname+".class  == "+ Diffmetrics.elementAt(2));
			        
		        	
		        }
		        
		        else if(CmdArg.equalsIgnoreCase("a")){
		        	
		        	System.out.println("Total Attributes modified by obfuscation in: " +Cname+".class  == "+ Diffmetrics.elementAt(3));
			        
		        	
		        }
		        
		        else if(CmdArg.equalsIgnoreCase("c")){
		        	
		        	System.out.println("Number of Constants modified by obfuscation in: " +Cname+".class  == "+ Diffmetrics.elementAt(4));
			        
		        	
		        }
		        else{
		        double rating;
		        double size=0,mets=0,fld=0,atts=0,cp=0,al=0;
		        
		        //File Size
		        if(OriginalFileVals.elementAt(0)>=(3*ObfFileVals.elementAt(0)))
		        	size=10;
		        else if (OriginalFileVals.elementAt(0)>=(2*ObfFileVals.elementAt(0)))
		        	size=7.5;

		        else if (OriginalFileVals.elementAt(0)>=(1.5*ObfFileVals.elementAt(0)))
		        	size=5;

		        else if (OriginalFileVals.elementAt(0)>ObfFileVals.elementAt(0))
		        	size=2;
		        
		        // Methods Difference
		        if(OriginalFileVals.elementAt(1)>=(3*ObfFileVals.elementAt(1)))
		        	mets=10;
		        else if (OriginalFileVals.elementAt(1)>=(2*ObfFileVals.elementAt(1)))
		        	mets=7.5;

		        else if (OriginalFileVals.elementAt(1)>=(1.5*ObfFileVals.elementAt(1)))
		        	mets=5;

		        else if (OriginalFileVals.elementAt(1)>ObfFileVals.elementAt(1))
		        	mets=2;

		        // Fields Difference
		        if(OriginalFileVals.elementAt(2)>=(3*ObfFileVals.elementAt(2)))
		        	fld=10;
		        else if (OriginalFileVals.elementAt(2)>=(2*ObfFileVals.elementAt(2)))
		        	fld=7.5;

		        else if (OriginalFileVals.elementAt(2)>=(1.5*ObfFileVals.elementAt(2)))
		        	fld=5;

		        else if (OriginalFileVals.elementAt(2)>ObfFileVals.elementAt(2))
		        	fld=2;
		        
		        // Method Attributes Difference
		        if(OriginalFileVals.elementAt(3)>=(3*ObfFileVals.elementAt(3)))
		        	atts=10;
		        else if (OriginalFileVals.elementAt(3)>=(2*ObfFileVals.elementAt(3)))
		        	atts=7.5;

		        else if (OriginalFileVals.elementAt(3)>=(1.5*ObfFileVals.elementAt(3)))
		        	atts=5;

		        else if (OriginalFileVals.elementAt(3)>ObfFileVals.elementAt(3))
		        	atts=2;
		        
		        // Constant Pool difference
		        if(OriginalFileVals.elementAt(4)>=(3*ObfFileVals.elementAt(4)))
		        	cp=10;
		        else if (OriginalFileVals.elementAt(4)>=(2*ObfFileVals.elementAt(4)))
		        	cp=7.5;

		        else if (OriginalFileVals.elementAt(4)>=(1.5*ObfFileVals.elementAt(4)))
		        	cp=5;

		        else if (OriginalFileVals.elementAt(4)>ObfFileVals.elementAt(4))
		        	cp=2;
		        
		        //Attributes length difference
		        if(OriginalFileVals.elementAt(5)>=(3*ObfFileVals.elementAt(5)))
		        	al=10;
		        else if (OriginalFileVals.elementAt(5)>=(2*ObfFileVals.elementAt(5)))
		        	al=7.5;

		        else if (OriginalFileVals.elementAt(5)>=(1.5*ObfFileVals.elementAt(5)))
		        	al=5;

		        else if (OriginalFileVals.elementAt(5)>ObfFileVals.elementAt(5))
		        	al=2;
		        
		        rating=((cp+al+fld+mets+atts+size)/6);
		        	
		        System.out.println("Obfuscation Rating  == "+ rating);
		        }
		        
		        }
	        else{
	        	/*
					Do Nothing
	        	*/
	        }
		        
		} catch (FileNotFoundException e) {
		    e.printStackTrace();
		} catch (IOException e) {
		    e.printStackTrace();
		}
	}
}
