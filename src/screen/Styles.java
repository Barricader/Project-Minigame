package screen;

import java.awt.Font;
import java.awt.GradientPaint;

/**
 * Collection of predefined gradient styles
 * @author David Kramer
 *
 */
public class Styles {
	// Gradient Styles
	public static final GradientPaint VERTICAL_BLUE = new GradientPaint(400.0f, 0.0f,
														  GameUtils.colorFromHex("#12AFE3"),
														  400.0f, 40.0f, GameUtils.colorFromHex("#0E6FC4"));
	
	public static final GradientPaint HORIZONTAL_BLUE = new GradientPaint(0.0f, 0.0f,
			  												GameUtils.colorFromHex("#12AFE3"),
			  												400.0f, 0.0f, GameUtils.colorFromHex("#0E6FC4"));

	
}
