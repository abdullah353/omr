import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.IOException;


public class Omr {
	
	public static void main(String[] args) throws IOException {
		// TODO code application logic here
		
		OmrModel sheet=new OmrModel();
		OmrView view=new OmrView(sheet);
		OmrController control=new OmrController(sheet, view);
		control.startApp();
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
