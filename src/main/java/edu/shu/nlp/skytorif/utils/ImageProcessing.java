package edu.shu.nlp.skytorif.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 图片水印处理（水印位置确定）
 * @author Administrator
 *
 */
public class ImageProcessing {
	
	private static final Logger logger = LoggerFactory.getLogger(ImageProcessing.class);
	//图片纵向的像素分割数量，越大图片表现出的纵向区别越大，即图片越模糊不能超过图片水印的高度
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
		toImagePath = MessageFormat.format(toImagePath, pixelNumbers);
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
		algorithm(image);
		FileOutputStream output = new FileOutputStream(toImagePath);		
		ImageIO.write(image, "jpg", output);
	} 
	/**
	 * 模糊水印的算法，将水印所在长方形区域，按y轴横向分成n块长方形区域，每一块长方形区域的像素都重复最上一边的像素
	 * @param image
	 */
	private static void algorithm(BufferedImage image){
		int[] sections = constructingAnArray();
		mainPartOfAlgorithm(sections, image);
	}
	/**
	 * 构造数组，数组中保存需要重复的像素点的y轴坐标
	 * @param length
	 * @return
	 */
	private static int[] constructingAnArray(){
		int length = (int)Math.ceil(logo_height / (double)pixelNumbers);
		int array[] = new int[length];
		
		for(int i = 0; i < array.length; i++){
			if(i == 0){
				array[i] = logo_point_y;
			}else{
				array[i] = pixelNumbers + array[i - 1];
			}
		}
		
		return array;
	}
	/**
	 * 算法的主要处理部分
	 * @param sections
	 * @param image
	 */
	private static void mainPartOfAlgorithm(int[] sections, BufferedImage image){
		for(int j = logo_point_x; j <= logo_point_x + logo_width; j++){
			for(int i = 0; i < sections.length; i++){
				if(i == sections.length - 1){
					for(int k = sections[i]; k <= logo_point_y + logo_height; k++){
						image.setRGB(j, k, image.getRGB(j, sections[i]));
					}
				}else{
					for(int k = sections[i]; k < sections[i + 1]; k++){
						image.setRGB(j, k, image.getRGB(j, sections[i]));
					}
				}
			}
		}
	}
	
	public static void main(String[] args) throws IOException{
		ImageProcessing.removeLogoFromImage();
	}
}
