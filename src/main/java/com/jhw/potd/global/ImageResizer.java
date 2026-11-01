package com.jhw.potd.global;

import java.awt.*;
import java.awt.image.BufferedImage;

import org.springframework.stereotype.Component;

@Component
public class ImageResizer {

	public static final int MAX_WIDTH = 1080;
	public static final int MAX_HEIGHT = 1350;

	public BufferedImage resize(BufferedImage image) {
		int width = image.getWidth();
		int height = image.getHeight();
		// 최대 크기보다 큰 경우에만 축소
		if (width > MAX_WIDTH || height > MAX_HEIGHT) {
			double scale = Math.min((double)MAX_WIDTH / width, (double)MAX_HEIGHT / height);

			int newWidth = (int)(MAX_WIDTH * scale);
			int newHeight = (int)(MAX_HEIGHT * scale);

			BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, image.getType());
			Graphics2D g2d = resizedImage.createGraphics();
			// 축소 화질 개선 — 보간을 BILINEAR(또는 더 부드러운 BICUBIC)로
			g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
			g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY);

			g2d.drawImage(image, 0, 0, newWidth, newHeight, null);
			g2d.dispose(); // 해제 안할 시 그래픽 리소스 누수
			return resizedImage;
		}
		return image;
	}

}
