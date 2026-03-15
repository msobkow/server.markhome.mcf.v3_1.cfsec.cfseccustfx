// Description: Java 13 JavaFX Display Element Factory for SecGroup.

/*
 *	server.markhome.mcf.CFSec
 *
 *	Copyright (c) 2016-2026 Mark Stephen Sobkow
 *	
 *	Mark's Code Fractal 3.1 CFSec - Security Services
 *	
 *	Copyright (c) 2016-2026 Mark Stephen Sobkow mark.sobkow@gmail.com
 *	
 *	These files are part of Mark's Code Fractal CFSec.
 *	
 *	Licensed under the Apache License, Version 2.0 (the "License");
 *	you may not use this file except in compliance with the License.
 *	You may obtain a copy of the License at
 *	
 *	http://www.apache.org/licenses/LICENSE-2.0
 *	
 *	Unless required by applicable law or agreed to in writing, software
 *	distributed under the License is distributed on an "AS IS" BASIS,
 *	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *	See the License for the specific language governing permissions and
 *	limitations under the License.
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
