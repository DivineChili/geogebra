package org.geogebra.common.kernel.stepbystep.steptree;

import org.geogebra.common.kernel.CASException;
import org.geogebra.common.kernel.Kernel;
import org.geogebra.common.kernel.parser.Parser;
import org.geogebra.common.kernel.stepbystep.CASConflictException;
import org.geogebra.common.kernel.stepbystep.StepHelper;
import org.geogebra.common.kernel.stepbystep.solution.SolutionBuilder;
import org.geogebra.common.kernel.stepbystep.solution.SolutionStepType;
import org.geogebra.common.kernel.stepbystep.steps.SolveTracker;
import org.geogebra.common.kernel.stepbystep.steps.StepStrategies;
import org.geogebra.common.main.Localization;
import org.geogebra.common.util.debug.Log;

public class StepEquation extends StepSolvable {

	public StepEquation(StepExpression LHS, StepExpression RHS) {
		this.LHS = LHS.deepCopy();
		this.RHS = RHS.deepCopy();
	}

	public StepEquation(String str, Parser parser) {
		String[] sides = str.split("=");

		this.LHS = (StepExpression) getStepTree(sides[0], parser);
		this.RHS = (StepExpression) getStepTree(sides[1], parser);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((LHS == null) ? 0 : LHS.hashCode());
		result = prime * result + ((RHS == null) ? 0 : RHS.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object sn) {
		if (sn instanceof StepEquation) {
			StepEquation se = (StepEquation) sn;
			return LHS.equals(se.LHS) && RHS.equals(se.RHS);
		}

		return false;
	}

	@Override
	public StepEquation deepCopy() {
		StepEquation newEq = new StepEquation(LHS.deepCopy(), RHS.deepCopy());
		newEq.swapped = swapped;

		return newEq;
	}

	@Override
	public String toString() {
		return LHS + " = " + RHS;
	}

	@Override
	public String toLaTeXString(Localization loc, boolean colored) {
		if (colored && color != 0) {
			return "\\fgcolor{" + getColorHex() + "}{" + toLaTeXString(loc, false) + "}";
		}

		if (swapped) {
			return RHS.toLaTeXString(loc, colored) + " = " + LHS.toLaTeXString(loc, colored);
		}
		return LHS.toLaTeXString(loc, colored) + " = " + RHS.toLaTeXString(loc, colored);
	}

	@Override
	public StepSet trivialSolution(StepVariable variable, SolveTracker tracker) {
		if (LHS.equals(RHS)) {
			return new StepSet(tracker.getRestriction());
		}

		if (LHS.equals(variable)) {
			return new StepSet(RHS);
		}

		if (RHS.equals(variable)) {
			return new StepSet(LHS);
		}

		return new StepSet();
	}

	public StepEquation regroup() {
		return regroup(null, new SolveTracker());
	}

	@Override
	public StepEquation regroup(SolutionBuilder sb, SolveTracker tracker) {
		return (StepEquation) super.regroup(sb, tracker);
	}

	public boolean isValid(StepVariable var, StepExpression val) {
		StepEquation copy = deepCopy();
		copy.replace(var, val);

		copy.expand();

		if (!copy.getLHS().equals(copy.getRHS())) {
			if (isEqual(LHS.getValueAt(var, val.getValue()), RHS.getValueAt(var, val.getValue()))) {
				Log.error("Regroup failed at: " + this);
				Log.error("For solution: " + val);
				Log.error("Result: " + copy);
				Log.error("Whereas numeric evaluation gives equality");

				return true;
			}

			return false;
		}

		return true;
	}

	public StepSet solve(StepVariable sv, SolutionBuilder sb, SolveTracker tracker) {
		return (StepSet) StepStrategies.defaultSolve(this, sv, sb, tracker);
	}

	@Override
	public StepSet solveAndCompareToCAS(Kernel kernel, StepVariable sv, SolutionBuilder sb) throws CASException {
		SolveTracker tracker = new SolveTracker();
		StepSet solutions = solve(sv, sb, tracker);

		for (StepNode solution : solutions) {
			if (solution instanceof StepExpression) {
				String casCommand;
				if (tracker.isApproximate() != null && tracker.isApproximate()) {
					Log.error("approximating");
					casCommand = "ApproximateSolution(" + LHS + ", " + RHS + ", " + sv + " = " + solution + ")";
				} else {
					casCommand = "CorrectSolution(" + LHS + ", " + RHS + ", " + sv + " = " + solution + ")";
				}

				String withAssumptions = StepHelper.getAssumptions(add((StepExpression) solution, add(LHS, RHS)),
						casCommand);

				String result = kernel.evaluateCachedGeoGebraCAS(withAssumptions, null);

				if (!"true".equals(result)) {
					StepSet CASSolutions = StepHelper.getCASSolutions(this, sv, kernel);
					throw new CASConflictException(sb.getSteps(), solutions, CASSolutions);
				}
			}
		}

		return solutions;
	}

	@Override
	public boolean checkSolution(StepExpression solution, StepVariable variable, SolutionBuilder steps,
								 SolveTracker tracker) {

		if (!tracker.getRestriction().contains(solution)) {
			return false;
		}

		if (isValid(variable, solution)) {
			steps.add(SolutionStepType.VALID_SOLUTION, new StepEquation(variable, solution));
		} else {
			steps.add(SolutionStepType.INVALID_SOLUTION, new StepEquation(variable, solution));
			return false;
		}

		return true;
	}
}
