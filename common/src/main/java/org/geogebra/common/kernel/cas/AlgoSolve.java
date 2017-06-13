package org.geogebra.common.kernel.cas;

import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.StringTemplate;
import org.geogebra.common.kernel.algos.AlgoElement;
import org.geogebra.common.kernel.algos.GetCommand;
import org.geogebra.common.kernel.arithmetic.MyArbitraryConstant;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoElement;
import org.geogebra.common.kernel.geos.GeoLine;
import org.geogebra.common.kernel.geos.GeoList;

/**
 * Use Solve cas command from AV
 */
public class AlgoSolve extends AlgoElement implements UsesCAS {

	private GeoList solutions;
	private GeoElement equations;
	private MyArbitraryConstant arbconst = new MyArbitraryConstant(this);
	private boolean numeric;

	/**
	 * @param c
	 *            construction
	 * @param eq
	 *            equation or list thereof
	 * @param numeric
	 *            whether to use NSolve
	 */
	public AlgoSolve(Construction c, GeoElement eq, boolean numeric) {
		super(c);
		this.numeric = numeric;
		this.equations = eq;
		this.solutions = new GeoList(cons);
		setInputOutput();
		compute();
		solutions.setEuclidianVisible(false);
		solutions.setDrawable(false);
	}

	@Override
	protected void setInputOutput() {
		input = equations.asArray();
		setOnlyOutput(solutions);
		setDependencies();

	}

	@Override
	public void compute() {
		StringBuilder sb = new StringBuilder(numeric ? "NSolve[" : "Solve[");
		if (equations instanceof GeoList) {
			sb.append("{");
			for (int i = 0; i < ((GeoList) equations).size(); i++) {
				if (i != 0) {
					sb.append(',');
				}
				printCAS(((GeoList) equations).get(i), sb);
			}
			sb.append("}");
		} else {
			printCAS(equations, sb);
		}
		sb.append("]");
		try {
			String solns = kernel.evaluateCachedGeoGebraCAS(sb.toString(),
					arbconst);
			GeoList raw = kernel.getAlgebraProcessor().evaluateToList(solns);
			solutions.set(raw);
			showUserForm(solutions);
		} catch (Throwable e) {
			solutions.setUndefined();
			e.printStackTrace();
		}
		solutions.setDrawable(false);
	}

	private void showUserForm(GeoList solutions2) {
		for (int i = 0; i < solutions2.size(); i++) {
			if (solutions2.get(i) instanceof GeoLine) {
				((GeoLine) solutions2.get(i)).setMode(GeoLine.EQUATION_USER);
			}
			if (solutions2.get(i) instanceof GeoList) {
				showUserForm((GeoList) solutions2.get(i));
			}
		}

	}

	private static void printCAS(GeoElement equations2, StringBuilder sb) {
		if (equations2.getDefinition() != null) {
			sb.append(equations2.getDefinition()
					.toValueString(StringTemplate.prefixedDefault));
		} else {
			sb.append(equations2.toValueString(StringTemplate.prefixedDefault));
		}
	}

	@Override
	public GetCommand getClassName() {
		return numeric ? Commands.NSolve : Commands.Solve;
	}

	/**
	 * Switch between Solve and NSolve and run the update cascade
	 * 
	 * @return whether this is numeric after the toggle
	 */
	public boolean toggleNumeric() {
		numeric = !numeric;
		compute();
		solutions.updateCascade();
		return numeric;
	}

}