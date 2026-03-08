// Description: Java 13 Cust JavaFX Schema.

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

import java.math.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.security.KeyStore;

import javafx.application.*;
import javafx.scene.*;
import javafx.stage.*;

import org.apache.commons.codec.binary.Base64;

import server.markhome.mcf.v3_1.cflib.*;
import server.markhome.mcf.v3_1.cflib.dbutil.*;
import server.markhome.mcf.v3_1.cflib.xml.*;
import server.markhome.mcf.v3_1.cflib.javafx.*;
import server.markhome.mcf.v3_1.cfsec.cfsec.*;
import server.markhome.mcf.v3_1.cfsec.cfsecjavafx.*;
import server.markhome.mcf.v3_1.cfsec.cfsecobj.*;

/**
 *	The CFSecCustSchema defines the interface object that is
 *	provided by the cust interface for manipulating the CFSec
 *	facet in the user interface.
 */
public class CFSecCustSchema
extends CFSecJavaFXSchema
implements ICFSecCustSchema
{
	protected ICFSecJavaFXSchema javafxSchema = null;

	public CFSecCustSchema() {
	}

	public ICFSecJavaFXSchema getJavaFXSchema() {
		if( javafxSchema == null ) {
			javafxSchema = this;
		}
		return( javafxSchema );
	}

	public void setJavaFXSchema( ICFSecJavaFXSchema value ) {
		final String S_ProcName = "setJavaFXSchema";
		if( value == null ) {
			throw new CFLibNullArgumentException( getClass(),
				S_ProcName,
				1,
				"value" );
		}
		javafxSchema = value;
	}

	@Override public ICFSecJavaFXSecGroupFactory getSecGroupFactory() {
		if( factorySecGroup == null ) {
			factorySecGroup = new CFSecCustSecGroupFactory( this );
		}
		return( factorySecGroup );
	}

	@Override public ICFSecJavaFXTSecGroupFactory getTSecGroupFactory() {
		if( factoryTSecGroup == null ) {
			factoryTSecGroup = new CFSecCustTSecGroupFactory( this );
		}
		return( factoryTSecGroup );
	}

	protected volatile static CFSecCustFacetPane S_singletonSecFacetPane = null;

	public CFSecCustFacetPane getSingletonSecFacetPane( ICFFormManager formManager ) {
		CFSecCustFacetPane pane = getSingletonSecFacetPane( formManager, this );
		return( pane );
	}

	public CFSecCustFacetPane getSingletonSecFacetPane( ICFFormManager formManager, ICFSecCustSchema argSchema ) {
		if( S_singletonSecFacetPane == null ) {
			S_singletonSecFacetPane = newSecFacetPane( formManager, argSchema );
		}
		return( S_singletonSecFacetPane );
	}

	public CFSecCustFacetPane newSecFacetPane( ICFFormManager formManager ) {
		CFSecCustFacetPane pane = newSecFacetPane( formManager, this );
		return( pane );
	}

	public CFSecCustFacetPane newSecFacetPane( ICFFormManager formManager, ICFSecCustSchema argSchema ) {
		CFSecCustFacetPane pane = new CFSecCustFacetPane( formManager, argSchema );
		return( pane );
	}

	public CFBorderPane newManageClusterSecGroupForm( ICFFormManager formManager ) {
		CFBorderPane pane = new CFSecCustManageClusterSecGroupForm( formManager, this );
		return( pane );
	}

	public CFBorderPane newManageClusterSecGroupForm( ICFFormManager formManager, ICFSecCustSchema argSchema ) {
		CFBorderPane pane = new CFSecCustManageClusterSecGroupForm( formManager, argSchema );
		return( pane );
	}

	public CFBorderPane newManageClusterSecGroupPane( ICFFormManager formManager ) {
		CFBorderPane pane = new CFSecCustManageClusterSecGroupPane( formManager, this );
		return( pane );
	}

	public CFBorderPane newManageClusterSecGroupPane( ICFFormManager formManager, ICFSecCustSchema argSchema ) {
		CFBorderPane pane = new CFSecCustManageClusterSecGroupPane( formManager, argSchema );
		return( pane );
	}

	public CFBorderPane newManageTenantTSecGroupForm( ICFFormManager formManager ) {
		CFBorderPane pane = new CFSecCustManageTenantTSecGroupForm( formManager, this );
		return( pane );
	}

	public CFBorderPane newManageTenantTSecGroupForm( ICFFormManager formManager, ICFSecCustSchema argSchema ) {
		CFBorderPane pane = new CFSecCustManageTenantTSecGroupForm( formManager, argSchema );
		return( pane );
	}

	public CFBorderPane newManageTenantTSecGroupPane( ICFFormManager formManager ) {
		CFBorderPane pane = new CFSecCustManageTenantTSecGroupPane( formManager, this );
		return( pane );
	}

	public CFBorderPane newManageTenantTSecGroupPane( ICFFormManager formManager, ICFSecCustSchema argSchema ) {
		CFBorderPane pane = new CFSecCustManageTenantTSecGroupPane( formManager, argSchema );
		return( pane );
	}
}
