import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.filechooser.FileSystemView;

//This class creates Directory Listings
public class DirectoryListWriter {

	private static int counti = 0;
	private static int count = 0;
	
	public DirectoryListWriter(PrintWriter p1) {}	
	
	//Sends a response listing a directory
	public static void listDir(File[] dirlist, String type, PrintWriter p1) throws IOException {		
		
		//Reset counters
		counti = 0;		
		count = 0;						
		
		//Populate icon folder for this directory
		getIcons(dirlist);			
		
		//Write status line
		p1.write("HTTP/1.1 200 OK\n");
		
		//Content type
		p1.write("Content-Type: " + type + "\n\n");
		
		//Create table and first row/headings
		p1.write("<html><head><title>" + "Directory Listing" + "</title></head><body>");
		p1.write("<table class=\"sortable\"><tr>" +				
				"<td>Icon</td>" +
				"<td><a href=\"" + dirlist[0].getParent() + "/?N/\">" + "Name</a></td>" + //?N for sorting name
				"<td><a href=\"" + dirlist[0].getParent() + "/?S/\">" + "Size</a></td>" + //?S for sorting Size
				"<td><a href=\"" + dirlist[0].getParent() + "/?M/\">" + "Modified</a></td>" + //?M for sorting modified since
				"<td>Description</td></tr>");
		
		//Fill in the table by reading through each file
		for(File f1: dirlist){
													
			//Determine path for each file
			String path = "";
			
			if(f1.isDirectory() == true){							
				path = f1.getPath() + "/";			
			}
			else{
				path = f1.getPath();
			}											
			
			//New row
			p1.write("<tr>");
			//New cell, corresponding to titles above
			p1.write("<td>" + "<img src=\"" + "/icons/" + count + ".png" + "\">" + "</td>");
			p1.write("<td>" +  "<a href=\"" + path + "\">" + f1.getName() + "</a>" + "</td>");		
			p1.write("<td>" + f1.length() + "</td>");
			p1.write("<td>" + new Date(f1.lastModified()) + "</td>");
			p1.write("<td>" + "" + "</td>");			
						
			count++;
		}		
		
		//Close the table
		p1.write("</table></html>");		
		
		//Flush stream
		p1.flush();
		p1.close();			
	}			
	
	//Writes icons for a directory to /src/icons
	public static void getIcons(File[] dir) throws IOException{		
		
		//For extracting icons
		FileSystemView view = FileSystemView.getFileSystemView();	
		
		//Loop through directory
		for(File f1: dir){
		
						
			//Retrieves an icon for the current file
			Icon ico = view.getSystemIcon(f1);		
			ImageIcon icon = (ImageIcon) ico;
			
			//Now Temporarily write the icon into the source folder
			
			//Buffer the icon
			BufferedImage img = new BufferedImage(ico.getIconWidth(), ico.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			
			//Draw the icon
			Graphics g = img.createGraphics();
			g.drawImage(icon.getImage(), 0, 0, null);
			g.dispose();
			
			//Write to a file - populate the icons folder			
			File temp = new File("icons/" + counti + ".png");
			temp.createNewFile();
			ImageIO.write(img, "png", temp);
			counti++;		
		}		
	}	
}
