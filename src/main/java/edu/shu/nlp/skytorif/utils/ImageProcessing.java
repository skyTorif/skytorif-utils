package edu.shu.nlp.skytorif.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

public class ImageProcessing {

	/**
	 * 图片去水印（logo位置固定，图片处理后的默认格式.jpg）
	 * @param srcImagePath 待处理图片的路径
	 * @param toImagePath 处理后图片的保存路径
	 * @param pixelNumbers 待更新的像素数
	 * @param x 图片中水印所在x轴位置（左上角的x坐标）
	 * @param y 图片中水印所在y轴位置（左上角的y坐标）
	 * @param logo_width 图片中水印的水平长度
	 * @param logo_height 图片中水印的垂直高度
	 * @throws IOException
	 */
	public static void removeLogoFromImage(String srcImagePath, String toImagePath, int pixelNumbers, int x, int y, int logo_width, int logo_height) throws IOException{
		
		File imageFile = new File(srcImagePath);
		BufferedImage image = ImageIO.read(imageFile);
		
		verticalAlgorithm(image, pixelNumbers, x, y, logo_width, logo_height);
		
		FileOutputStream output = new FileOutputStream(toImagePath);		
		ImageIO.write(image, "jpg", output);
	} 
	
	/**
	 * 图片去水印的算法：垂直处理
	 * @param image 待处理的图片
	 * @param pixelNumbers 待更新的像素数
	 * @param x 图片中水印所在x轴位置（左上角的x坐标）
	 * @param y 图片中水印所在y轴位置（左上角的y坐标）
	 * @param logo_width 图片中水印的水平长度
	 * @param logo_height 图片中水印的垂直高度
	 */
	private static void verticalAlgorithm(BufferedImage image, int pixelNumbers, int x, int y, int logo_width, int logo_height){
		
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
			for(int j = x; j < x + logo_width; j++){
				for(int k = 0; k < pixelNumbers; k++){
					if(y + sections[i] + k <= y + logo_height){
						image.setRGB(j, y + sections[i] + k, image.getRGB(j, y + sections[i]));
					}
				}
			}
		}
		
	}
}
