import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;


public class Omr {
	
	public static void main(String[] args) throws IOException {
		// TODO code application logic here
		
		OmrModel sheet=new OmrModel();
		OmrView view=new OmrView(sheet);
		OmrController control=new OmrController(sheet, view);
		//control.startApp();
		try {
			genExcel();
		} catch (WriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		/*
		File file1 = new File("/home/sam/OMRlayout.png");
		BufferedImage image = ImageIO.read(file1);
		File outputfile = new File("/home/sam/aaaaaaaaa2.png");
		
		
		ImageIO.write(drawcircle(500, 500, 250,image), "png", outputfile);
		*/
	}
	private static BufferedImage drawCircleBorder(final int centerX, final int centerY, final int radius,BufferedImage image) {
	    int d = 3 - (2 * radius);
	    int x = 0;
	    int y = radius;
	    
	    do {
	    	Color black = new Color(0,0, 0); // Color white
	    	int rgb=black.getRGB();
	        image.setRGB(centerX + x, centerY + y, rgb);
	        image.setRGB(centerX + x, centerY - y, rgb);
	        image.setRGB(centerX - x, centerY + y, rgb);
	        image.setRGB(centerX - x, centerY - y, rgb);
	        image.setRGB(centerX + y, centerY + x, rgb);
	        image.setRGB(centerX + y, centerY - x, rgb);
	        image.setRGB(centerX - y, centerY + x, rgb);
	        image.setRGB(centerX - y, centerY - x, rgb);
	        if (d < 0) {
	            d = d + (4 * x) + 6;
	        } else {
	            d = d + 4 * (x - y) + 10;
	            y--;
	        }
	        x++;
	    } while (x <= y);
	    return image;
	}
	
	private static BufferedImage drawcircle(int x,int y,int radius,BufferedImage image){
		Color black = new Color(0,0, 0); // Color white
    	int rgb=black.getRGB();
		for(int i=x;i<radius*2;i++)
	        for(int j=x;j<radius*2;j++){
	            int d= (int) Math.sqrt((i-radius)*(i-radius)+(j-radius)*(j-radius));
	                if(d<radius)
	                    image.setRGB(i, j, rgb);
	        }
	    return image;

	}
	public static void genExcel() throws IOException, WriteException{
		String 	excelname = "output.xls",
				sheetName = "Sheet1";
		int totQuest = 6;
		int[] selecOpt = {1,2,3,4,5,6};
		
		WritableWorkbook workbook = Workbook.createWorkbook(new File( excelname));
		WritableSheet sheets = workbook.createSheet(sheetName, 0);
		for (int i = 0; i < totQuest; i++) {
			Label label = new Label(i, 0, "Question "+i); 
			Number number = new Number(i, 1, selecOpt[i]); 
			sheets.addCell(label);
			sheets.addCell(number);
		}
		workbook.write(); 
		workbook.close();
	}
	
}
