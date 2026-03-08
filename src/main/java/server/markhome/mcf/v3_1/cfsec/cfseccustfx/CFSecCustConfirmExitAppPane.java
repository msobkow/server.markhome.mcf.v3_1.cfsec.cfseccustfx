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
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.*;

import org.apache.commons.codec.binary.Base64;

import server.markhome.mcf.v3_1.cflib.*;
import server.markhome.mcf.v3_1.cflib.dbutil.*;
import server.markhome.mcf.v3_1.cflib.xml.*;
import server.markhome.mcf.v3_1.cflib.javafx.*;
import server.markhome.mcf.v3_1.cfsec.cfsec.*;
import server.markhome.mcf.v3_1.cfsec.cfsecjavafx.*;
import server.markhome.mcf.v3_1.cfsec.cfsecobj.*;

public class CFSecCustConfirmExitAppPane
extends CFBorderPane
implements ICFForm
{
	protected final String S_FormName = "Exit Application";
	protected ICFFormManager cfFormManager = null;
	protected ICFSecCustSchema custSchema = null;
	protected CFSecCustFacetPane facetPane = null;
	protected CFLabel labelTitle = null;
	protected CFHBox hboxButtons = null;
	protected CFButton buttonOk = null;
	protected CFButton buttonCancel = null;

	public CFSecCustConfirmExitAppPane(
		ICFFormManager formManager, 
		ICFSecCustSchema argSchema,
		CFSecCustFacetPane argFacet )
	{
		super();
		final String S_ProcName = "construct";
		if( formManager == null ) {
			throw new CFLibNullArgumentException( getClass(),
				S_ProcName,
				1,
				"formManager" );
		}
		cfFormManager = formManager;
		custSchema = argSchema;
		facetPane = argFacet;

		labelTitle = new CFLabel();
		labelTitle.setText( "Are you sure you want to exit the application?" );
		Font f = labelTitle.getFont();
		Font largeBold = Font.font( f.getFamily(), FontWeight.BOLD, 20 );
		labelTitle.setFont( largeBold );
		labelTitle.setMinHeight( 35 );
		labelTitle.setMaxHeight( 35 );
		labelTitle.setMinWidth( 200 );
		labelTitle.setAlignment( Pos.CENTER );

		hboxButtons = new CFHBox( 10 );
		hboxButtons.setMinHeight( 35 );
		hboxButtons.setMaxHeight( 35 );
		hboxButtons.setPrefHeight( 35 );
		hboxButtons.setAlignment( Pos.CENTER );

		buttonOk = new CFButton();
		buttonOk.setVisible( true );
		buttonOk.setMinWidth( 200 );
		buttonOk.setMaxWidth( 200 );
		buttonOk.setPrefWidth( 200 );
		buttonOk.setMinHeight( 25 );
		buttonOk.setMaxHeight( 25 );
		buttonOk.setPrefHeight( 25 );
		hboxButtons.getChildren().add( buttonOk );
		buttonOk.setText( "Yes, exit application" );
		buttonOk.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					ICFSecSchemaObj schemaObj = custSchema.getSchema();
					if( ( schemaObj != null ) && ( schemaObj.getAuthorization() != null ) ) {
						try {
							schemaObj.logout();
						}
						catch( Exception x ) {
							CFConsole.message( "Logout error: " + x.getMessage() );
							return;
						}
						finally {
							schemaObj.setAuthorization( null );
						}
					}
					facetPane.exitApplication();
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonCancel = new CFButton();
		buttonCancel.setVisible( true );
		buttonCancel.setMinWidth( 200 );
		buttonCancel.setMaxWidth( 200 );
		buttonCancel.setPrefWidth( 200 );
		buttonCancel.setMinHeight( 25 );
		buttonCancel.setMaxHeight( 25 );
		buttonCancel.setPrefHeight( 25 );
		buttonCancel.setDefaultButton( true );
		hboxButtons.getChildren().add( buttonCancel );
		buttonCancel.setText( "Cancel" );
		buttonCancel.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					if( facetPane != null ) {
						cfFormManager.closeCurrentForm();
					}
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		setTop( labelTitle );
		setBottom( hboxButtons );
	}

	public ICFFormManager getCFFormManager() {
		return( cfFormManager );
	}

	public void setCFFormManager( ICFFormManager value ) {
		final String S_ProcName = "setCFFormManager";
		if( value == null ) {
			throw new CFLibNullArgumentException( getClass(),
				S_ProcName,
				1,
				"value" );
		}
		cfFormManager = value;
	}

	public void forceCancelAndClose() {
		if( cfFormManager != null ) {
			if( cfFormManager.getCurrentForm() == this ) {
				cfFormManager.closeCurrentForm();
			}
		}
	}

	public ICFSecCustSchema getCustSchema() {
		return( custSchema );
	}

	public void setCustSchema( ICFSecCustSchema argSchema ) {
		final String S_ProcName = "setCustSchema";
		final String S_ArgNameSchema = "argSchema";
		if( argSchema == null ) {
			throw new CFLibNullArgumentException( getClass(),
				S_ProcName,
				1,
				S_ArgNameSchema );
		}
		custSchema = argSchema;
	}
}
