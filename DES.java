/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package homework;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author xintong
 */
public class DES {
	static int[] permTable ={
			58	,	50	,	42	,	34	,	26	,	18	,	10	,	2	,
			60	,	52	,	44	,	36	,	28	,	20	,	12	,	4	,
			62	,	54	,	46	,	38	,	30	,	22	,	14	,	6	,
			64	,	56	,	48	,	40	,	32	,	24	,	16	,	8	,
			57	,	49	,	41	,	33	,	25	,	17	,	9	,	1	,
			59	,	51	,	43	,	35	,	27	,	19	,	11	,	3	,
			61	,	53	,	45	,	37	,	29	,	21	,	13	,	5	,
			63	,	55	,	47	,	39	,	31	,	23	,	15	,	7				
	};

	
	
	static int[] reverIPTable ={
			40	,	8	,	48	,	16	,	56	,	24	,	64	,	32	,
			39	,	7	,	47	,	15	,	55	,	23	,	63	,	31	,
			38	,	6	,	46	,	14	,	54	,	22	,	62	,	30	,
			37	,	5	,	45	,	13	,	53	,	21	,	61	,	29	,
			36	,	4	,	44	,	12	,	52	,	20	,	60	,	28	,
			35	,	3	,	43	,	11	,	51	,	19	,	59	,	27	,
			34	,	2	,	42	,	10	,	50	,	18	,	58	,	26	,
			33	,	1	,	41	,	9	,	49	,	17	,	57	,	25	
		
	};
	int[] IP;
	long block;
	int blockLength;
	
	//construct the class
	DES(int permTable[],long blk,int sizeBlock){
	block=blk;
	IP=permTable;
	blockLength=sizeBlock;
	}
	
	static public long perm(int permTable[],long blk,int sizeBlock){ //sizeblock
		long result=0x0;
		for(int i=0;i<permTable.length;i++){
			int srcPosition=sizeBlock-permTable[i];
			result=(result<<1) | (blk>>>srcPosition & 0x1);
			}

		return result;
		
	}

               
         static public long[] keySchedule(long key){
            int[] PC1table={
				57	,	49	,	41	,	33	,	25	,	17	,	9	,
				1	,	58	,	50	,	42	,	34	,	26	,	18	,
				10	,	2	,	59	,	51	,	43	,	35	,	27	,
				19	,	11	,	3	,	60	,	52	,	44	,	36	,

				63	,	55	,	47	,	39	,	31	,	23	,	15	,
				7	,	62	,	54	,	46	,	38	,	30	,	22	,
				14	,	6	,	61	,	53	,	45	,	37	,	29	,
				21	,	13	,	5	,	28	,	20	,	12	,	4	
		};
		
		int[] PC2table={
				14	,	17	,	11	,	24	,	1	,	5	,
				3	,	28	,	15	,	6	,	21	,	10	,
				23	,	19	,	12	,	4	,	26	,	8	,
				16	,	7	,	27	,	20	,	13	,	2	,
				41	,	52	,	31	,	37	,	47	,	55	,
				30	,	40	,	51	,	45	,	33	,	48	,
				44	,	49	,	39	,	56	,	34	,	53	,
				46	,	42	,	50	,	36	,	29	,	32	

		};
                
                int[] shiftNum={
                                1,1,2,2,2,2,2,2,1,2,2,2,2,2,2,1
                };
		 
             
            long keyResult[]= new long[16]; 
            long pc1_result=perm(PC1table,key,64);
            int cBits=(int)(pc1_result>>>28);
            int dBits=(int)(pc1_result&(0xfffffff));
       
          
            for (int i=0;i<16;i++){
             //rotate the 28 bits
            	if(shiftNum[i]==2){
            		//shifts 2 bits
            	cBits=cBits<<2|(cBits>>>(26));
            	dBits=dBits<<2|(dBits>>>(26));
            	}
            	else {
            		cBits=cBits<<1|(cBits>>>(27));
                	dBits=dBits<<1|(dBits>>>(27));
            	}
            	long mergedCD=(cBits&0xfffffffl)<<28 | (0xfffffffl&dBits);
            	        
            keyResult[i]=perm(PC2table,mergedCD,56);
             }
            return keyResult;             
         }
         
         
    static public long expandBits(int bit){
        //32 bits is expanded to 48 bits by duplication of some bits and permuting
    int[] expandBit={
            32	,	1	,	2	,	3	,	4	,	5	,
            4	,	5	,	6	,	7	,	8	,	9	,
            8	,	9	,	10	,	11	,	12	,	13	,
            12	,	13	,	14	,	15	,	16	,	17	,
            16	,	17	,	18	,	19	,	20	,	21	,
            20	,	21	,	22	,	23	,	24	,	25	,
            24	,	25	,	26	,	27	,	28	,	29	,
            28	,	29	,	30	,	31	,	32	,	1	
        }; 
    
        return perm(expandBit,bit&0xffffffffl,32);
    }
    
    
    static public long byte2long(byte[] mybyte,int bytelength){
        long result=0x0l;
        for(int i=0;i<bytelength;i++){
            result|=(mybyte[i]&0xffl)<<(bytelength*8-i*8-8);      
        }
        return result;
    }
    
    static public byte[] long2byte(long input,int bytelength){
        byte[] bits = new byte[bytelength];
        for (int i=0;i<bytelength;i++){
            bits[i]=(byte) ((input>>(bytelength*8-i*8-8))&0xffl);
        }
        return bits;
    }
    
    
    
    static public byte substitution(byte bits, int boxNumber){
    	byte[][] S = { {
    	        14, 4,  13, 1,  2,  15, 11, 8,  3,  10, 6,  12, 5,  9,  0,  7,
    	        0,  15, 7,  4,  14, 2,  13, 1,  10, 6,  12, 11, 9,  5,  3,  8,
    	        4,  1,  14, 8,  13, 6,  2,  11, 15, 12, 9,  7,  3,  10, 5,  0,
    	        15, 12, 8,  2,  4,  9,  1,  7,  5,  11, 3,  14, 10, 0,  6,  13
    	    }, {
    	        15, 1,  8,  14, 6,  11, 3,  4,  9,  7,  2,  13, 12, 0,  5,  10,
    	        3,  13, 4,  7,  15, 2,  8,  14, 12, 0,  1,  10, 6,  9,  11, 5,
    	        0,  14, 7,  11, 10, 4,  13, 1,  5,  8,  12, 6,  9,  3,  2,  15,
    	        13, 8,  10, 1,  3,  15, 4,  2,  11, 6,  7,  12, 0,  5,  14, 9
    	    }, {
    	        10, 0,  9,  14, 6,  3,  15, 5,  1,  13, 12, 7,  11, 4,  2,  8,
    	        13, 7,  0,  9,  3,  4,  6,  10, 2,  8,  5,  14, 12, 11, 15, 1,
    	        13, 6,  4,  9,  8,  15, 3,  0,  11, 1,  2,  12, 5,  10, 14, 7,
    	        1,  10, 13, 0,  6,  9,  8,  7,  4,  15, 14, 3,  11, 5,  2,  12
    	    }, {
    	        7,  13, 14, 3,  0,  6,  9,  10, 1,  2,  8,  5,  11, 12, 4,  15,
    	        13, 8,  11, 5,  6,  15, 0,  3,  4,  7,  2,  12, 1,  10, 14, 9,
    	        10, 6,  9,  0,  12, 11, 7,  13, 15, 1,  3,  14, 5,  2,  8,  4,
    	        3,  15, 0,  6,  10, 1,  13, 8,  9,  4,  5,  11, 12, 7,  2,  14
    	    }, {
    	        2,  12, 4,  1,  7,  10, 11, 6,  8,  5,  3,  15, 13, 0,  14, 9,
    	        14, 11, 2,  12, 4,  7,  13, 1,  5,  0,  15, 10, 3,  9,  8,  6,
    	        4,  2,  1,  11, 10, 13, 7,  8,  15, 9,  12, 5,  6,  3,  0,  14,
    	        11, 8,  12, 7,  1,  14, 2,  13, 6,  15, 0,  9,  10, 4,  5,  3
    	    }, {
    	        12, 1,  10, 15, 9,  2,  6,  8,  0,  13, 3,  4,  14, 7,  5,  11,
    	        10, 15, 4,  2,  7,  12, 9,  5,  6,  1,  13, 14, 0,  11, 3,  8,
    	        9,  14, 15, 5,  2,  8,  12, 3,  7,  0,  4,  10, 1,  13, 11, 6,
    	        4,  3,  2,  12, 9,  5,  15, 10, 11, 14, 1,  7,  6,  0,  8,  13
    	    }, {
    	        4,  11, 2,  14, 15, 0,  8,  13, 3,  12, 9,  7,  5,  10, 6,  1,
    	        13, 0,  11, 7,  4,  9,  1,  10, 14, 3,  5,  12, 2,  15, 8,  6,
    	        1,  4,  11, 13, 12, 3,  7,  14, 10, 15, 6,  8,  0,  5,  9,  2,
    	        6,  11, 13, 8,  1,  4,  10, 7,  9,  5,  0,  15, 14, 2,  3,  12
    	    }, {
    	        13, 2,  8,  4,  6,  15, 11, 1,  10, 9,  3,  14, 5,  0,  12, 7,
    	        1,  15, 13, 8,  10, 3,  7,  4,  12, 5,  6,  11, 0,  14, 9,  2,
    	        7,  11, 4,  1,  9,  12, 14, 2,  0,  6,  10, 13, 15, 3,  5,  8,
    	        2,  1,  14, 7,  4,  10, 8,  13, 15, 12, 9,  0,  3,  5,  6,  11
    	    } }; 
    	
    	
    	
    	int SPosition=(0x20&bits | ((0x1&bits)<<4)) | ((bits&0x1e)>>1) ;
    			
    	return  S[boxNumber][SPosition];         
    }
    
    static public int substitutionBox(long bits){
    	int[] P = {
    	        16, 7,  20, 21,
    	        29, 12, 28, 17,
    	        1,  15, 23, 26,
    	        5,  18, 31, 10,
    	        2,  8,  24, 14,
    	        32, 27, 3,  9,
    	        19, 13, 30, 6,
    	        22, 11, 4,  25
    	    };
    	
    	long output = 0;
    	for (int i=0;i<8;i++){
    		
    		byte input=(byte) (bits&0x3f);;
    		output|=(0xffl&substitution(input,7-i))<<(i*4);
    		bits>>>=6;
    	}  	
    	return (int) perm(P,output,32);	
    			
    }
    
    static public int fFunction(int bits,long subkey){
    	
    	long expandedBits=expandBits(bits);
    	long input=expandedBits ^ subkey;
    	return substitutionBox(input);
    	
    }
    
    
	public static byte[] encryptBlockECB(byte[] blk,long key){
		long[] subkey = keySchedule(key);

		int length = blk.length,start=0,end=8;
		byte[] block=new byte[(length/8)*8+8];
		byte[] temp = new byte[8];
		for(int i=0;i<blk.length/8;i++){
			temp=Arrays.copyOfRange(blk, start,end);
			start+=8;
			end+=8;
			length-=8;
			temp=encryptBlock(temp,subkey);

			for(int j=0;j<8;j++){
				block[8*i+j]=temp[j];
			}
			
		}
		
		//check padding
		
		//padding other values
		int pad=8-length%8;
		temp=Arrays.copyOfRange(blk, start,end);
		for(int i=0;i<pad;i++){
			temp[7-i]=(byte)pad;
		}
		
		//encrypt
		temp=encryptBlock(temp,subkey);
		for(int j=0;j<8;j++){
			block[block.length-j-1]=temp[7-j];
		}
			
				
		return block;
	
	} 
	
	
	public static byte[] decryptBlockECB(byte[] block,long key){
		long[] subkey = keySchedule(key);
		int start=0,end=8;

		byte[] temp = new byte[8];
		for(int i=0;i<block.length/8;i++){
			temp=Arrays.copyOfRange(block, start,end);
			start+=8;
			end+=8;
			temp=decryptBlock(temp,subkey);
			
			for(int j=0;j<8;j++){
				block[8*i+j]=temp[j];
			}
			
		}
		
				//check padding by examing last byte
		int pad=block[block.length-1];		
		return 	Arrays.copyOf(block, block.length-pad);
	
	}

    
    
    
    static public byte[] encryptBlock(byte[] blk, long[] subkey){

    	long bits=byte2long(blk,8);
    	int temp,left,right=0;
    	long IPresult=perm(permTable,bits,64);
    	left=(int) (IPresult>>>32);
    	right=(int) (IPresult&0xffffffff);
    	
    	for(int i=0;i<16;i++){
    		temp=right;
    		right=left ^ fFunction(right,subkey[i]);
    		left=temp; 	
    	}
    	
    	long beforeRIP=(right&0xffffffffl)<<32 | (left&0xffffffffl);
    	long interim=perm(reverIPTable,beforeRIP,64);
    	
    	return long2byte(interim,8);
    	 	  	
    }
    
    

    
    static public byte[] decryptBlock(byte[] blk, long[] subkey){
    	long bits=byte2long(blk,8);
    	int temp,left,right=0;
    	long IPresult=perm(permTable,bits,64);
    	
    	right=(int) (IPresult>>>32);
    	left=(int) (IPresult&0xffffffff);
    	
    	for(int i=0;i<16;i++){
    		temp=left;
    		left=right ^ fFunction(left,subkey[15-i]);
    		right=temp; 	
    	}
    	
    	long beforeRIP=((left&0xffffffffl)<<32) | (right&0xffffffffl);
    	
    	long interim=perm(reverIPTable,beforeRIP,64);
    	
    	return long2byte(interim,8);
    	
    }
    
    
    public static void encryptFile(String inputFile,String outputFile,long key) throws IOException{
		String currentFolder= System.getProperty("user.dir");
		FileInputStream fileIn=new FileInputStream(new File(currentFolder+"/"+inputFile));
    	FileOutputStream fileOut=new FileOutputStream(currentFolder+"/"+outputFile);
    	
    	int lengthFile=(int) fileIn.getChannel().size(); //file couldn't exceed 4G
    	byte[] bits= new byte[lengthFile];
    	fileIn.read(bits);

    	byte[] outputECB=encryptBlockECB(bits,key);
    	fileOut.write(outputECB);
        fileOut.close();
	    fileIn.close();
    	
    	}

	public static void decryptFile(String inputFile,String outputFile,long key) throws IOException{
		String currentFolder= System.getProperty("user.dir");
		FileInputStream fileIn=new FileInputStream(new File(currentFolder+"/"+inputFile));
    	FileOutputStream fileOut=new FileOutputStream(currentFolder+"/"+outputFile);
    	
    	int lengthFile=(int) fileIn.getChannel().size(); //file couldn't exceed 4G
    	byte[] bits= new byte[lengthFile];
    	fileIn.read(bits);	
    	byte[] outputECB=decryptBlockECB(bits,key);  				    
    	fileOut.write(outputECB);
        fileOut.close();
        fileIn.close();	
	}
	
	public static byte[] hexStringToByteArray(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));
	    }
	    return data;
	}
    
    
    
    static public void main(String[] args) throws Exception{
    	
    	if(args.length!=4){
    		System.out.println("error: input format is inputfile outputfile key encrypt/decrypt ");
    		System.exit(0);
    	}
    	String inputFile=args[0];
		String outputFile=args[1];
		byte[] masterkey=hexStringToByteArray(args[2]);
		long key=byte2long(masterkey,8);
    	String Mode=args[3];
    	
    	 long startTime = System.nanoTime();
     	switch(Mode){ 
         case "encrypt":
         	encryptFile( inputFile, outputFile,key);
         	long difference1 = System.nanoTime() - startTime;
         	System.out.println("Total execution time: " +
             String.format(" %d sec",
                          TimeUnit.NANOSECONDS.toSeconds(difference1)));
         	System.out.println("encrypted!");
         	break;
         case "decrypt":
         	decryptFile( inputFile, outputFile,key);
         	long difference2 = System.nanoTime() - startTime;
         	System.out.println("Total execution time: " +
                     String.format("%d sec",
                                  TimeUnit.NANOSECONDS.toSeconds(difference2)));
             
         	System.out.println("decrypted!");
         	break;
         default:
             break;        
     	}
   
    }

}
