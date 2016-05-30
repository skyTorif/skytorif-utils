package edu.shu.nlp.skytorif.image_util;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Shape;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;
import java.util.Random;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

public class QRcodeUtils {
		//编码格式
		private static String CHARSET;
		//图片格式
		private static String IMAGE_FORMAT;
		// 二维码尺寸
		private static int QRCODE_SIZE;
		// LOGO宽度
		private static int LOGO_WIDTH;
		// LOGO高度
		private static int LOGO_HEIGHT;
		
		static{
			//加载properties文件
			ResourceBundle rb = ResourceBundle.getBundle("qrcode");
			CHARSET = rb.getString("charset");
			IMAGE_FORMAT = rb.getString("image_format");
			QRCODE_SIZE = Integer.parseInt(rb.getString("qrcode_size"));
			LOGO_WIDTH = Integer.parseInt(rb.getString("logo_width"));
			LOGO_HEIGHT = Integer.parseInt(rb.getString("logo_height"));
			
		}
		
		/**
		 * 生成二维码图片。若logoPath=null，二维码无logo。
		 * @param content 二维码内容
		 * @param logoPath logo路径
		 * @param needCompress logo是否压缩
		 * @return
		 * @throws Exception
		 */
		private static BufferedImage createImage(String content, String logoPath,
				boolean needCompress) throws Exception{
			Hashtable<EncodeHintType, Object> hints = new Hashtable<EncodeHintType, Object>();
			hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H); //误差校正
			hints.put(EncodeHintType.CHARACTER_SET, CHARSET); //字符集
			hints.put(EncodeHintType.MARGIN, 1); //边缘
			BitMatrix bitMatrix = new MultiFormatWriter().encode(content,
					BarcodeFormat.QR_CODE, QRCODE_SIZE, QRCODE_SIZE, hints);
			int width = bitMatrix.getWidth();
			int height = bitMatrix.getHeight();
			BufferedImage image = new BufferedImage(width, height,
					BufferedImage.TYPE_INT_RGB);
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000
							: 0xFFFFFFFF);
				}
			}
			if (logoPath == null || "".equals(logoPath)) {
				return image;
			}
			// 插入图片
			insertImage(image, logoPath, needCompress);
			return image;
		}
		
		/**
		 * 向二维码图片中插入logo
		 * @param source 二维码图片
		 * @param logoPath logo路径
		 * @param needCompress logo是否压缩
		 * @throws Exception
		 */
		private static void insertImage(BufferedImage source, String logoPath,
				boolean needCompress) throws Exception {
			File file = new File(logoPath);
			if (!file.exists()) {
				System.err.println(""+logoPath+"   该文件不存在！");
				return;
			}
			Image src = ImageIO.read(new File(logoPath));
			int width = src.getWidth(null);
			int height = src.getHeight(null);
			// 压缩LOGO
			if (needCompress) {
				if (width > LOGO_WIDTH) {
					width = LOGO_WIDTH;
				}
				if (height > LOGO_HEIGHT) {
					height = LOGO_HEIGHT;
				}
				Image image = src.getScaledInstance(width, height,
						Image.SCALE_SMOOTH);
				BufferedImage tag = new BufferedImage(width, height,
						BufferedImage.TYPE_INT_RGB);
				Graphics g = tag.getGraphics();
				g.drawImage(image, 0, 0, null); // 绘制缩小后的logo
				g.dispose();
				src = image;
			}
			// 插入LOGO
			Graphics2D graph = source.createGraphics();
			int x = (QRCODE_SIZE - width) / 2;
			int y = (QRCODE_SIZE - height) / 2;
			graph.drawImage(src, x, y, width, height, null);
			Shape shape = new RoundRectangle2D.Float(x, y, width, width, 6, 6);
			graph.setStroke(new BasicStroke(3f));
			graph.draw(shape);
			graph.dispose();
		}
		
		/**
		 * 将生成的二维码图片输出到指定位置
		 * @param content 二维码内容
		 * @param logoPath logo路径
		 * @param destPath 目标路径
		 * @param needCompress logo是否压缩
		 * @throws Exception
		 */
		public static void createQrcode(String content, String logoPath, String destPath,
				boolean needCompress) throws Exception {
			BufferedImage image = createImage(content, logoPath,
					needCompress);
			
			String file = new Random().nextInt(99999999)+".jpg";
			ImageIO.write(image, IMAGE_FORMAT, new File(destPath+"/"+file));
		}
		public static void main(String[] args) {
			
			String content = "http://sh.anjuke.com/?pi=PZ-baidu-pc-sh-biaoti";
			String logoPath = "image/anjuke.jpg";
			String destPath = "C:\\Users\\Administrator\\Desktop";
			boolean needCompress = false;
			
			try {
				createQrcode(content, logoPath, destPath, needCompress);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
