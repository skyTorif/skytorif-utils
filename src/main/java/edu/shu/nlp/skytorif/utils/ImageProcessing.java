package edu.shu.nlp.skytorif.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageProcessing {
	
	private static final Logger logger = LoggerFactory.getLogger(ImageProcessing.class);
	//图片纵向的像素分割数量，越大图片表现出的纵向区别越大，应小于图片的高度
	private static int pixelNumbers;
	private static String srcImagePath;
	private static String toImagePath;
	private static int logo_point_x;
	private static int logo_point_y;
	private static int logo_width;
	private static int logo_height;
	
	static{
		Properties prop = new Properties();
		InputStream in = ImageProcessing.class.getResourceAsStream("para.properties");
		try {
			prop.load(in);
		} catch (IOException e) {
			logger.error("加载配置文件出错！");
			e.printStackTrace();
		}
		pixelNumbers = Integer.parseInt(prop.getProperty("pixelNumbers"));
		srcImagePath = prop.getProperty("srcImagePath");
		toImagePath = prop.getProperty("toImagePath");
		logo_point_x = Integer.parseInt(prop.getProperty("logo_point_x"));
		logo_point_y = Integer.parseInt(prop.getProperty("logo_point_y"));
		logo_width = Integer.parseInt(prop.getProperty("logo_width"));
		logo_height = Integer.parseInt(prop.getProperty("logo_height"));
		if(logger.isDebugEnabled()){
			logger.debug("pixelNumbers: " + pixelNumbers);
			logger.debug("srcImagePath: " + srcImagePath);
			logger.debug("toImagePath: " + toImagePath);
			logger.debug("logo_point_x: " + logo_point_x);
			logger.debug("logo_point_y: " + logo_point_y);
			logger.debug("logo_width: " + logo_width);
			logger.debug("logo_height: " + logo_height);
		}
	}
	
	public static void removeLogoFromImage() throws IOException{
		
		File imageFile = new File(srcImagePath);
		BufferedImage image = ImageIO.read(imageFile);
		
		verticalAlgorithm(image);
		
		FileOutputStream output = new FileOutputStream(toImagePath);		
		ImageIO.write(image, "jpg", output);
	} 
	/**
	 * 去水印的算法
	 * @param image
	 */
	private static void verticalAlgorithm(BufferedImage image){
		
		int height = logo_height; 
		int point_y = 1; //需要重复的像素点y轴坐标的偏移量
		
		int s = (int)Math.ceil(height / (double)pixelNumbers); //数组大小
		int[] sections = new int[s]; //数组中保存每段需要重复的像素点y轴坐标偏移量
		int cur = 0; //数组游标
		//构建数组
		do{
			sections[cur++] = point_y;
			point_y += pixelNumbers;
		}while(point_y <= height && cur < s);
		//core
		for(int i = 0; i < s; i++){
			for(int j = logo_point_x; j < logo_point_x + logo_width; j++){
				for(int k = 0; k < pixelNumbers; k++){
					if(logo_point_y + sections[i] + k <= logo_point_y + logo_height){
						image.setRGB(j, logo_point_y + sections[i] + k, image.getRGB(j, logo_point_y + sections[i]));
					}
				}
			}
		}
		
	}
	
	public static void main(String[] args) throws IOException{
		ImageProcessing.removeLogoFromImage();
	}
}
