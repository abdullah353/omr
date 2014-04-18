import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;

import java.io.File; 
import jxl.*; 
import jxl.write.*; 
import jxl.write.Number;
import jxl.write.biff.RowsExceededException;


public class Omr {
	
	public static void main(String[] args) throws IOException {
		// TODO code application logic here
		
		OmrModel sheet=new OmrModel();
		OmrView view=new OmrView(sheet);
		OmrController control=new OmrController(sheet, view);
		//control.startApp();
		WritableWorkbook workbook = Workbook.createWorkbook(new File("output.xls"));
		WritableSheet sheets = workbook.createSheet("First Sheet", 0);
		Label label = new Label(0, 2, "A label record"); 
		Label label2 = new Label(0, 0, "A label record"); 
		Number number = new Number(3, 4, 3.1459); 
		try {
			sheets.addCell(label);
			sheets.addCell(label2);
			sheets.addCell(number);
			workbook.write(); 
			workbook.close();
		} catch (RowsExceededException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
}
