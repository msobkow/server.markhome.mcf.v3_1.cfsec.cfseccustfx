// Description: Java 13 JavaFX Display Element Factory for SecGroup.

/*
 *	server.markhome.mcf.CFSec
 *
 *	Copyright (c) 2020-2025 Mark Stephen Sobkow
 *	
 *	Mark's Code Fractal 3.1 CFSec - Security Services
 *	
 *	This file is part of Mark's Code Fractal CFSec.
 *	
 *	Mark's Code Fractal CFSec is available under dual commercial license from
 *	Mark Stephen Sobkow, or under the terms of the GNU Library General Public License,
 *	Version 3 or later.
 *	
 *	Mark's Code Fractal CFSec is free software: you can redistribute it and/or
 *	modify it under the terms of the GNU Library General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *	
 *	Mark's Code Fractal CFSec is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *	
 *	You should have received a copy of the GNU Library General Public License
 *	along with Mark's Code Fractal CFSec.  If not, see <https://www.gnu.org/licenses/>.
 *	
 *	If you wish to modify and use this code without publishing your changes in order to
 *	tie it to proprietary code, please contact Mark Stephen Sobkow
 *	for a commercial license at mark.sobkow@gmail.com
 *	
 */

package server.markhome.mcf.v3_1.cfsec.cfseccustfx;

import server.markhome.mcf.v3_1.cflib.javafx.*;
import server.markhome.mcf.v3_1.cflib.dbutil.*;
import server.markhome.mcf.v3_1.cflib.xml.*;

import server.markhome.mcf.v3_1.cfsec.cfsecobj.*;
import server.markhome.mcf.v3_1.cfsec.cfsecjavafx.*;

/**
 *	CFSecJavaFXSecGroupFactory JavaFX Display Element Factory
 *	for SecGroup.
 */
public class CFSecCustSecGroupFactory
extends CFSecJavaFXSecGroupFactory
{
	public CFSecCustSecGroupFactory( ICFSecJavaFXSchema argSchema ) {
		super( argSchema );
	}

	@Override public CFTabPane newEltTabPane( ICFFormManager formManager, ICFSecSecGroupObj argFocus ) {
		CFSecCustSecGroupEltTabPane retnew = new CFSecCustSecGroupEltTabPane( formManager, javafxSchema, argFocus );
		return( retnew );
	}
}
