package FEM;

import javafx.scene.image.Image;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;

import java.util.stream.IntStream;

public class ColorInterpolation {

	private static final float INT_TO_FLOAT_CONST = 1f / 255f;
	private static final int TEXTURE_SIZE = 16;

	public static Image colorPalette() {
		WritableImage img = new WritableImage(TEXTURE_SIZE, TEXTURE_SIZE);
		PixelWriter pw = img.getPixelWriter();

		//right
		IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
				.forEach(y -> IntStream.range((int)(0* TEXTURE_SIZE), (int)(0.25* TEXTURE_SIZE)).boxed()
						.forEach(x -> pw.setColor(x, y, getColor(x-(int)(0* TEXTURE_SIZE), y-(int)(0.25* TEXTURE_SIZE), java.awt.Color.RED, java.awt.Color.RED, java.awt.Color.BLUE, java.awt.Color.BLUE))));
		//top
		IntStream.range((int)(0* TEXTURE_SIZE), (int)(0.25* TEXTURE_SIZE)).boxed()
				.forEach(y -> IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
						.forEach(x -> pw.setColor(x, y, getColor(x-(int)(0.25* TEXTURE_SIZE), y-(int)(0* TEXTURE_SIZE), java.awt.Color.RED, java.awt.Color.RED, java.awt.Color.RED, java.awt.Color.RED))));
		//back
		IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
				.forEach(y -> IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
						.forEach(x -> pw.setColor(x, y, getColor(x-(int)(0.25* TEXTURE_SIZE), y-(int)(0.25* TEXTURE_SIZE), java.awt.Color.RED, java.awt.Color.RED, java.awt.Color.BLUE, java.awt.Color.BLUE))));
		//left
		IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
				.forEach(y -> IntStream.range((int)(0.5* TEXTURE_SIZE), (int)(0.75* TEXTURE_SIZE)).boxed()
						.forEach(x -> pw.setColor(x, y, getColor(x-(int)(0.5* TEXTURE_SIZE), y-(int)(0.25* TEXTURE_SIZE), java.awt.Color.RED, java.awt.Color.RED, java.awt.Color.BLUE, java.awt.Color.BLUE))));
		//front
		IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
				.forEach(y -> IntStream.range((int)(0.75* TEXTURE_SIZE), (int)(1* TEXTURE_SIZE)).boxed()
						.forEach(x -> pw.setColor(x, y, getColor(x-(int)(0.75* TEXTURE_SIZE), y-(int)(0.25* TEXTURE_SIZE), java.awt.Color.RED, java.awt.Color.RED, java.awt.Color.BLUE, java.awt.Color.BLUE))));

		//down
		IntStream.range((int)(0.5* TEXTURE_SIZE), (int)(0.75* TEXTURE_SIZE)).boxed()
				.forEach(y -> IntStream.range((int)(0.25* TEXTURE_SIZE), (int)(0.5* TEXTURE_SIZE)).boxed()
						.forEach(x -> pw.setColor(x, y, getColor(x-(int)(0.25* TEXTURE_SIZE), y-(int)(0.5* TEXTURE_SIZE), java.awt.Color.BLUE, java.awt.Color.BLUE, java.awt.Color.BLUE, java.awt.Color.BLUE))));

		// save for testing purposes
//		try {
//			ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", new File("palette" + ".png"));
//
//		} catch (IOException ex) {
//		}
		return img;
	}

	private static java.awt.Color interpolateColor(final java.awt.Color COLOR1, final java.awt.Color COLOR2, float fraction)
	{
		fraction = Math.min(fraction, 1f);
		fraction = Math.max(fraction, 0f);

		final float RED1 = COLOR1.getRed() * INT_TO_FLOAT_CONST;
		final float GREEN1 = COLOR1.getGreen() * INT_TO_FLOAT_CONST;
		final float BLUE1 = COLOR1.getBlue() * INT_TO_FLOAT_CONST;
		final float ALPHA1 = COLOR1.getAlpha() * INT_TO_FLOAT_CONST;

		final float RED2 = COLOR2.getRed() * INT_TO_FLOAT_CONST;
		final float GREEN2 = COLOR2.getGreen() * INT_TO_FLOAT_CONST;
		final float BLUE2 = COLOR2.getBlue() * INT_TO_FLOAT_CONST;
		final float ALPHA2 = COLOR2.getAlpha() * INT_TO_FLOAT_CONST;

		final float DELTA_RED = RED2 - RED1;
		final float DELTA_GREEN = GREEN2 - GREEN1;
		final float DELTA_BLUE = BLUE2 - BLUE1;
		final float DELTA_ALPHA = ALPHA2 - ALPHA1;

		float red = RED1 + (DELTA_RED * fraction);
		float green = GREEN1 + (DELTA_GREEN * fraction);
		float blue = BLUE1 + (DELTA_BLUE * fraction);
		float alpha = ALPHA1 + (DELTA_ALPHA * fraction);

		red = Math.min(red, 1f);
		red = Math.max(red, 0f);
		green = Math.min(green, 1f);
		green = Math.max(green, 0f);
		blue = Math.min(blue, 1f);
		blue = Math.max(blue, 0f);
		alpha = Math.min(alpha, 1f);
		alpha = Math.max(alpha, 0f);

		return new java.awt.Color(red, green, blue, alpha);
	}

	private static java.awt.Color bilinearInterpolateColor(final java.awt.Color COLOR_00, final java.awt.Color COLOR_10, final java.awt.Color COLOR_01, final java.awt.Color COLOR_11, final float FRACTION_X, final float FRACTION_Y)
	{
		final java.awt.Color INTERPOLATED_COLOR_X1 = interpolateColor(COLOR_00, COLOR_10, FRACTION_X);
		final java.awt.Color INTERPOLATED_COLOR_X2 = interpolateColor(COLOR_01, COLOR_11, FRACTION_X);
		return interpolateColor(INTERPOLATED_COLOR_X1, INTERPOLATED_COLOR_X2, FRACTION_Y);
	}

	private static Color getColor(int x, int y, java.awt.Color color1, java.awt.Color color2, java.awt.Color color3, java.awt.Color color4){
		float xFraction = (float)x/((float) TEXTURE_SIZE /4);
		float yFraction = (float)y/((float) TEXTURE_SIZE /4);

		java.awt.Color awtColor = bilinearInterpolateColor(color1, color2, color3, color4, xFraction, yFraction);
		int r = awtColor.getRed();
		int g = awtColor.getGreen();
		int b = awtColor.getBlue();
		int a = awtColor.getAlpha();
		double opacity = a / 255.0 ;

		javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(r, g, b, opacity);
		return fxColor;
	}

}
