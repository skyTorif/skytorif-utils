package edu.shu.nlp.skytorif.image_util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 58图片水印处理
 * @author skytorif
 *
 */
public class WuBaImageUtils {
	private static final Logger logger = LoggerFactory.getLogger(WuBaImageUtils.class);
	private static int logo_width;
	private static int logo_height;
	private static int pixelNumbers; //每个重复区域纵向的像素数
	private static int logo_point_x;
	private static int logo_point_y;
	private static String srcImagePath;
	private static String toImagePath;
	
	static{
		Properties prop = new Properties();
		InputStream in = WuBaImageUtils.class.getResourceAsStream("wuba.properties");
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
		logo_width = Integer.parseInt(prop.getProperty("logo_width"));
		logo_height = Integer.parseInt(prop.getProperty("logo_height"));
		if(logger.isDebugEnabled()){
			logger.debug("pixelNumbers: " + pixelNumbers);
			logger.debug("srcImagePath: " + srcImagePath);
			logger.debug("toImagePath: " + toImagePath);
			logger.debug("logo_width: " + logo_width);
			logger.debug("logo_height: " + logo_height);
		}
	}
	
	public static void removeLogoFromImage(){
		File imageFile = new File(srcImagePath);
		try {
			BufferedImage image = ImageIO.read(imageFile);
			logo_point_x = image.getWidth() - 1;
			logo_point_y = image.getHeight() - 1;
			if(logger.isDebugEnabled()){
				logger.debug("image_width:\t" + image.getWidth());
				logger.debug("image_height:\t" + image.getHeight());
				logger.debug("logo_point_x:\t" + logo_point_x);
				logger.debug("logo_point_y:\t" + logo_point_y);
			}
			mainProcess(image);
			FileOutputStream output = new FileOutputStream(toImagePath);		
			ImageIO.write(image, "jpg", output);
		} catch (FileNotFoundException e) {
			logger.error(srcImagePath + "is error or not exist!");
			e.printStackTrace();
		} catch (IOException e) {
			logger.error("文件操作出错！");
			e.printStackTrace();
		}
	}
	
	private static void mainProcess(BufferedImage image){
		int[] sections = constructingAnArray();
		removeLogoAlgorithm(sections, image);
	}
	
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
	
	private static void removeLogoAlgorithm(int[] array, BufferedImage image){
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
	}
	
	public static void main(String[] args) throws IOException{
		WuBaImageUtils.removeLogoFromImage();
	}
}
