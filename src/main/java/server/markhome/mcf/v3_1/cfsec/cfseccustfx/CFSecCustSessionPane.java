// Description: Java 13 Cust JavaFX Schema.

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

import java.math.*;
import java.sql.*;
import java.text.*;
import java.util.*;
import java.security.KeyStore;

import javafx.application.*;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
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

public class CFSecCustSessionPane
extends CFBorderPane
implements ICFForm
{
	protected final String S_FormName = "Session";
	protected ICFFormManager cfFormManager = null;
	protected ICFSecCustSchema custSchema = null;
	protected CFSecCustFacetPane facetPane = null;
	protected CFLabel labelTitle = null;
	protected ScrollPane scrollButtons = null;
	protected CFVBox vboxButtons = null;

	protected CFButton buttonImportFile = null;
	protected CFButton buttonSystemTables = null;
	protected CFButton buttonClusterTables = null;
	protected CFButton buttonTenantTables = null;
	protected CFButton buttonLogout = null;
	protected CFButton buttonExitApp = null;

	public CFSecCustSessionPane(
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

		setMinWidth( 300.0 );
		setMinHeight( 200.0 );

		labelTitle = new CFLabel();
		labelTitle.setText( "You are currently logged in" );
		Font f = labelTitle.getFont();
		Font largeBold = Font.font( f.getFamily(), FontWeight.BOLD, 20 );
		labelTitle.setFont( largeBold );
		labelTitle.setMinHeight( 35 );
		labelTitle.setMaxHeight( 35 );
		labelTitle.setMinWidth( 200 );
		labelTitle.setAlignment( Pos.CENTER );

		vboxButtons = new CFVBox( 10 );
		vboxButtons.setMinWidth( 220 );
		vboxButtons.setAlignment( Pos.TOP_CENTER );

		buttonImportFile = new CFButton();
		buttonImportFile.setVisible( true );
		buttonImportFile.setMinWidth( 200 );
		buttonImportFile.setMaxWidth( 200 );
		buttonImportFile.setPrefWidth( 200 );
		buttonImportFile.setMinHeight( 25 );
		buttonImportFile.setMaxHeight( 25 );
		buttonImportFile.setPrefHeight( 25 );
		vboxButtons.getChildren().add( buttonImportFile );
		buttonImportFile.setText( "Import File..." );
		buttonImportFile.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					if( facetPane != null ) {
						facetPane.showFileImport();
					}
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonSystemTables = new CFButton();
		buttonSystemTables.setVisible( true );
		buttonSystemTables.setMinWidth( 200 );
		buttonSystemTables.setMaxWidth( 200 );
		buttonSystemTables.setPrefWidth( 200 );
		buttonSystemTables.setMinHeight( 25 );
		buttonSystemTables.setMaxHeight( 25 );
		buttonSystemTables.setPrefHeight( 25 );
		vboxButtons.getChildren().add( buttonSystemTables );
		buttonSystemTables.setText( "Maintain System Tables..." );
		buttonSystemTables.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					if( facetPane != null ) {
						facetPane.showSystemTables();
					}
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonClusterTables = new CFButton();
		buttonClusterTables.setVisible( true );
		buttonClusterTables.setMinWidth( 200 );
		buttonClusterTables.setMaxWidth( 200 );
		buttonClusterTables.setPrefWidth( 200 );
		buttonClusterTables.setMinHeight( 25 );
		buttonClusterTables.setMaxHeight( 25 );
		buttonClusterTables.setPrefHeight( 25 );
		vboxButtons.getChildren().add( buttonClusterTables );
		buttonClusterTables.setText( "Maintain Cluster Tables..." );
		buttonClusterTables.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					if( facetPane != null ) {
						facetPane.showClusterTables();
					}
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonTenantTables = new CFButton();
		buttonTenantTables.setVisible( true );
		buttonTenantTables.setMinWidth( 200 );
		buttonTenantTables.setMaxWidth( 200 );
		buttonTenantTables.setPrefWidth( 200 );
		buttonTenantTables.setMinHeight( 25 );
		buttonTenantTables.setMaxHeight( 25 );
		buttonTenantTables.setPrefHeight( 25 );
		vboxButtons.getChildren().add( buttonTenantTables );
		buttonTenantTables.setText( "Maintain Tenant Tables..." );
		buttonTenantTables.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					if( facetPane != null ) {
						facetPane.showTenantTables();
					}
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonLogout = new CFButton();
		buttonLogout.setVisible( true );
		buttonLogout.setMinWidth( 200 );
		buttonLogout.setMaxWidth( 200 );
		buttonLogout.setPrefWidth( 200 );
		buttonLogout.setMinHeight( 25 );
		buttonLogout.setMaxHeight( 25 );
		buttonLogout.setPrefHeight( 25 );
		vboxButtons.getChildren().add( buttonLogout );
		buttonLogout.setText( "Logout" );
		buttonLogout.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					if( facetPane != null ) {
						facetPane.showConfirmLogout();
					}
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonExitApp = new CFButton();
		buttonExitApp.setVisible( true );
		buttonExitApp.setMinWidth( 200 );
		buttonExitApp.setMaxWidth( 200 );
		buttonExitApp.setPrefWidth( 200 );
		buttonExitApp.setMinHeight( 25 );
		buttonExitApp.setMaxHeight( 25 );
		buttonExitApp.setPrefHeight( 25 );
		vboxButtons.getChildren().add( buttonExitApp );
		buttonExitApp.setText( "Exit Application" );
		buttonExitApp.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					if( facetPane != null ) {
						facetPane.showConfirmExitApp();
					}
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		scrollButtons = new ScrollPane();
		scrollButtons.setMinWidth( 240 );
		scrollButtons.setMaxWidth( 240 );
		scrollButtons.setHbarPolicy( ScrollBarPolicy.NEVER );
		scrollButtons.setVbarPolicy( ScrollBarPolicy.AS_NEEDED );
		scrollButtons.setFitToWidth( true );
		scrollButtons.setContent( vboxButtons );

		setTop( labelTitle );
		setCenter( scrollButtons );
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
