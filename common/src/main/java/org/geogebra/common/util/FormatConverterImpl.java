package org.geogebra.common.util;

import org.geogebra.common.io.MathMLParser;
import org.geogebra.common.io.latex.TeXAtomSerializer;
import org.geogebra.common.kernel.Kernel;

import com.himamis.retex.editor.share.editor.FormatConverter;
import com.himamis.retex.renderer.share.TeXFormula;
import com.himamis.retex.renderer.share.TeXParser;

/**
 * Convert expressions from Presentation MathML / LaTeX to simple ggb syntax
 * when pasting into the editor eg \sqrt{\frac{x}{2}} -> sqrt(x/2)
 * 
 * <mrow><mi> x</mi><mo> +</mo><mrow><mi> 1</mi><mo>/</mo>
 * <mi> 2</mi></mrow></mrow> -> x+1/2
 * 
 * @author michael
 *
 */
public class FormatConverterImpl implements FormatConverter {

	private Kernel kernel;

	/**
	 * @param kernel
	 *            Kernel
	 */
	public FormatConverterImpl(Kernel kernel) {
		this.kernel = kernel;
	}

	private boolean mightBeLaTeXSyntax(String expression) {
		try {
			kernel.getAlgebraProcessor()
					.getValidExpressionNoExceptionHandling(expression);
			// parses OK as GGB, not LaTeX
			return false;
		} catch (Throwable e) {
			// fall through
		}

		if (StringUtil.containsLaTeX(expression)) {
			return true;
		}

		return false;
	}

	private String convertLaTeXtoGGB(String latexExpression) {
		kernel.getApplication().getDrawEquation()
				.checkFirstCall(kernel.getApplication());
		TeXFormula tf = new TeXFormula();
		TeXParser tp = new TeXParser(latexExpression, tf);
		tp.parse();
		return new TeXAtomSerializer(null).serialize(tf.root);
	}

	private String convertMathMLoGGB(String mathmlExpression) {
		MathMLParser mathmlParserGGB = new MathMLParser(true);
		return mathmlParserGGB.parse(mathmlExpression, false, true);
	}

	@Override
	public String convert(String exp) {
		// might start <math> or <mrow> etc
		if (exp.startsWith("<")) {
			return convertMathMLoGGB(exp);

		} else if (mightBeLaTeXSyntax(exp)) {
			return convertLaTeXtoGGB(exp);
		}

		return exp;
	}

}
