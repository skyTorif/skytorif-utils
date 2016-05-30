package edu.shu.nlp.skytorif.image_util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageUtils {
	
	
	public static void main(String[] args) throws IOException{
		//String src = "C:\\Users\\Administrator\\Desktop\\58同城图片\\logo2\\1.jpg";
		//String src = "C:\\Users\\Administrator\\Desktop\\58同城图片\\logo1\\横宽\\3.jpg";
		String src = "C:\\Users\\Administrator\\Desktop\\58同城图片\\qp.jpg";
		File imageFile = new File(src);
		BufferedImage image = ImageIO.read(imageFile);
		System.out.println("width:\t" + image.getWidth());
		System.out.println("height:\t" + image.getHeight());
		
		int logo_height = 40;
		int logo_width = 150;
		int pixelNumbers = 40;
		int logo_point_y = image.getHeight() - 1;
		int logo_point_x = image.getWidth() - 1;
		
		int length = (int)Math.ceil(logo_height / (double)pixelNumbers);
		int array[] = new int[length];
		
		for(int i = 0; i < array.length; i++){
			if(i == 0){
				array[i] = logo_point_y;
			}else{
				array[i] = array[i - 1] - pixelNumbers;
			}
		}
		System.out.println(Arrays.toString(array));
		for(int j = logo_point_x; j >= logo_point_x - logo_width; j--){
			for(int i = 0; i < array.length; i++){
				if(i == array.length - 1){
					for(int k = array[i]; k >= logo_point_y - logo_height; k--){
						image.setRGB(j, k, image.getRGB(j, array[i]));
					}
				}else{
					for(int k = array[i]; k > array[i + 1]; k--){
						image.setRGB(j, k, image.getRGB(j, array[i]));
					}
				}
			}
		}
		String to = "C:\\Users\\Administrator\\Desktop\\ex.jpg";
		FileOutputStream output = new FileOutputStream(to);		
		ImageIO.write(image, "jpg", output);
	}
}
