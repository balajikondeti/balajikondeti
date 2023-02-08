import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class SortDir {

	private static int reverse = 0;
	
	public SortDir() {}
	
	//Sorts directory depending on query made (N/S/M - Name / Size / Modified)
	public static File[] sortDir(File[] dirlist, String sortdir){
		
		//First read in the query
		String q = sortdir;
		File[] sort = dirlist;		
		
		//If no query, return list	
		if(q == "Z"){
			return dirlist;
		}
		//If N then sort by name
		if(q.equals("N")){			
			
			Arrays.sort(sort, new Comparator<File>(){
				
				//Compare files
				public int compare(File f0, File f1) {
					
					if(f0.getName() == f1.getName()){
						return 0;
					}
					if(f0.getName().compareTo(f1.getName()) == -1){
						return -1;
					}
					else{
						return 1;
					}					
				}
			});								
		}
		
		//S = sort by size
		if(q.equals("S")){			
			
			Arrays.sort(sort, new Comparator<File>(){
				public int compare(File f0, File f1){
					
					if(f0.length() == f1.length()){
						return 0;
					}
					
					if(f0.length() < f1.length()){
						return -1;
					}
					else{					
						return 1;
					}
				}				
			});			
		}
		
		//Sort by modified since
		if(q.equals("M")){
			
			Arrays.sort(sort, new Comparator<File>(){
				public int compare(File f0, File f1){
					
					if(f0.lastModified() == f1.lastModified()){
						return 0;
					}
					
					if(f0.lastModified() < f1.lastModified()){
						return -1;
					}
					else{					
						return 1;
					}
				}				
			});					
		}		
		
		//If reverse = 0 then return as normal
		if(reverse == 0){
			
			reverse++;						
			return sort;			
		}
		
		//Else reverse the array
		if(reverse == 1){
			
			reverse--;					
			return reverse(sort);			
		}		
		
		return null;
	}
	
	//Reverse and returns a passed in array
	public static File[] reverse(File[] swap){
		
		List<File> listswap = Arrays.asList(swap);
		Collections.reverse(listswap);		
		return (File[]) (listswap.toArray());		
	}	
}
