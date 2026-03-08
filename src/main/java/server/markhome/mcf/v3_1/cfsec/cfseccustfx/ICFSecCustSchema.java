// Description: Java 13 Cust JavaFX Schema Interface.

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

import java.security.KeyStore;

import server.markhome.mcf.v3_1.cflib.javafx.*;
import server.markhome.mcf.v3_1.cfsec.cfsecjavafx.*;
import server.markhome.mcf.v3_1.cfsec.cfsecobj.*;

/**
 *	The ICFSecSwingSchema defines the interface for the shared schema
 *	object that is bound to a client.  The various implementations of
 *	facets cast the shared CFSecSchemaObj as required to access their
 *	data.
 *	<p>
 *	The formats of the configuration files don't change between applications,
 *	so only the CFSec-specified implementation is used.  It has all the
 *	attributes needed to set up client-server database connections (not that I
 *	code to that style any more.)
 */
public interface ICFSecCustSchema extends ICFSecJavaFXSchema
{
	ICFSecSchemaObj getSchema();
	void setSchema( ICFSecSchemaObj argSchema );

	String getClusterName();
	void setClusterName( String argClusterName );
	ICFSecClusterObj getClusterObj();

	String getTenantName();
	void setTenantName( String argTenantName );
	ICFSecTenantObj getTenantObj();

	String getSecUserName();
	void setSecUserName( String argSecUserName );
	ICFSecSecUserObj getSecUserObj();

	ICFSecSecSessionObj getSecSessionObj();

	ICFSecJavaFXSchema getJavaFXSchema();
	void setJavaFXSchema( ICFSecJavaFXSchema value );

	CFSecCustFacetPane getSingletonSecFacetPane( ICFFormManager formManager );
	CFSecCustFacetPane getSingletonSecFacetPane( ICFFormManager formManager, ICFSecCustSchema argSchema );
	CFSecCustFacetPane newSecFacetPane( ICFFormManager formManager );
	CFSecCustFacetPane newSecFacetPane( ICFFormManager formManager, ICFSecCustSchema argSchema );
	CFBorderPane newManageClusterSecGroupPane( ICFFormManager formManager );
	CFBorderPane newManageClusterSecGroupPane( ICFFormManager formManager, ICFSecCustSchema argSchema );
	CFBorderPane newManageClusterSecGroupForm( ICFFormManager formManager );
	CFBorderPane newManageClusterSecGroupForm( ICFFormManager formManager, ICFSecCustSchema argSchema );
	CFBorderPane newManageTenantTSecGroupPane( ICFFormManager formManager );
	CFBorderPane newManageTenantTSecGroupPane( ICFFormManager formManager, ICFSecCustSchema argSchema );
	CFBorderPane newManageTenantTSecGroupForm( ICFFormManager formManager );
	CFBorderPane newManageTenantTSecGroupForm( ICFFormManager formManager, ICFSecCustSchema argSchema );
}
