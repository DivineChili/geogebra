/* 
GeoGebra - Dynamic Mathematics for Everyone
http://www.geogebra.org

This file is part of GeoGebra.

This program is free software; you can redistribute it and/or modify it 
under the terms of the GNU General Public License as published by 
the Free Software Foundation.

 */

package org.geogebra.common.kernel.statistics;

import org.apache.commons.math3.distribution.TDistribution;
import org.geogebra.common.kernel.Construction;
import org.geogebra.common.kernel.commands.Commands;
import org.geogebra.common.kernel.geos.GeoBoolean;
import org.geogebra.common.kernel.geos.GeoNumberValue;

/**
 * 
 * @author Michael Borcherds
 */

public class AlgoTDistribution extends AlgoDistribution {

	/**
	 * @param cons
	 *            construction
	 * @param label
	 *            lavel
	 * @param a
	 *            degrees of freadom
	 * @param b
	 *            variable value
	 */
	public AlgoTDistribution(Construction cons, GeoNumberValue a,
			GeoNumberValue b, GeoBoolean cumulative) {
		super(cons, a, b, null, cumulative);
	}

	@Override
	public Commands getClassName() {
		return Commands.TDistribution;
	}

	@Override
	public final void compute() {

		if (input[0].isDefined() && input[1].isDefined()) {
			double param = a.getDouble();
			try {
				TDistribution t = getTDistribution(param);
				setFromRealDist(t, b); // P(T <= val)
			} catch (Exception e) {
				num.setUndefined();
			}
		} else {
			num.setUndefined();
		}
	}

}
