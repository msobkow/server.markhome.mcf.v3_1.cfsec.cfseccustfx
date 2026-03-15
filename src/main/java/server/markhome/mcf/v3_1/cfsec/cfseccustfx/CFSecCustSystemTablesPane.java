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
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
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

public class CFSecCustSystemTablesPane
extends CFBorderPane
implements ICFForm
{
	protected final String S_FormName = "System Tables";
	protected ICFFormManager cfFormManager = null;
	protected ICFSecCustSchema custSchema = null;
	protected CFSecCustFacetPane facetPane = null;
	protected CFLabel labelTitle = null;
	protected ScrollPane scrollButtons = null;
	protected CFVBox vboxButtons = null;

	protected CFButton buttonISOCtry = null;
	protected CFButton buttonISOCcy = null;
	protected CFButton buttonISOLang = null;
	protected CFButton buttonISOTZone = null;
	protected CFButton buttonServiceType = null;
	protected CFButton buttonCluster = null;
	protected CFButton buttonBack = null;

	public CFSecCustSystemTablesPane(
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
		labelTitle.setText( "Maintain System Tables" );
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

		buttonCluster = new CFButton();
		buttonCluster.setVisible( true );
		buttonCluster.setMinWidth( 200 );
		buttonCluster.setMaxWidth( 200 );
		buttonCluster.setPrefWidth( 200 );
		buttonCluster.setMinHeight( 25 );
		buttonCluster.setMaxHeight( 25 );
		buttonCluster.setPrefHeight( 25 );
		vboxButtons.getChildren().add( buttonCluster );
		buttonCluster.setText( "Cluster..." );
		buttonCluster.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					CFBorderPane finderForm = custSchema.getJavaFXSchema().getClusterFactory().newFinderForm( cfFormManager );
					cfFormManager.pushForm( finderForm );
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonISOCtry = new CFButton();
		buttonISOCtry.setVisible( true );
		buttonISOCtry.setMinWidth( 200 );
		buttonISOCtry.setMaxWidth( 200 );
		buttonISOCtry.setPrefWidth( 200 );
		buttonISOCtry.setMinHeight( 25 );
		buttonISOCtry.setMaxHeight( 25 );
		buttonISOCtry.setPrefHeight( 25 );
		vboxButtons.getChildren().add( buttonISOCtry );
		buttonISOCtry.setText( "ISO Ctry..." );
		buttonISOCtry.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					CFBorderPane finderForm = custSchema.getJavaFXSchema().getISOCtryFactory().newFinderForm( cfFormManager );
					cfFormManager.pushForm( finderForm );
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonISOCcy = new CFButton();
		buttonISOCcy.setVisible( true );
		buttonISOCcy.setMinWidth( 200 );
		buttonISOCcy.setMaxWidth( 200 );
		buttonISOCcy.setPrefWidth( 200 );
		buttonISOCcy.setMinHeight( 25 );
		buttonISOCcy.setMaxHeight( 25 );
		buttonISOCcy.setPrefHeight( 25 );
		vboxButtons.getChildren().add( buttonISOCcy );
		buttonISOCcy.setText( "ISO Ccy..." );
		buttonISOCcy.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					CFBorderPane finderForm = custSchema.getJavaFXSchema().getISOCcyFactory().newFinderForm( cfFormManager );
					cfFormManager.pushForm( finderForm );
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonISOLang = new CFButton();
		buttonISOLang.setVisible( true );
		buttonISOLang.setMinWidth( 200 );
		buttonISOLang.setMaxWidth( 200 );
		buttonISOLang.setPrefWidth( 200 );
		buttonISOLang.setMinHeight( 25 );
		buttonISOLang.setMaxHeight( 25 );
		buttonISOLang.setPrefHeight( 25 );
		vboxButtons.getChildren().add( buttonISOLang );
		buttonISOLang.setText( "ISO Lang..." );
		buttonISOLang.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					CFBorderPane finderForm = custSchema.getJavaFXSchema().getISOLangFactory().newFinderForm( cfFormManager );
					cfFormManager.pushForm( finderForm );
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonISOTZone = new CFButton();
		buttonISOTZone.setVisible( true );
		buttonISOTZone.setMinWidth( 200 );
		buttonISOTZone.setMaxWidth( 200 );
		buttonISOTZone.setPrefWidth( 200 );
		buttonISOTZone.setMinHeight( 25 );
		buttonISOTZone.setMaxHeight( 25 );
		buttonISOTZone.setPrefHeight( 25 );
		vboxButtons.getChildren().add( buttonISOTZone );
		buttonISOTZone.setText( "ISO Timezone..." );
		buttonISOTZone.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					CFBorderPane finderForm = custSchema.getJavaFXSchema().getISOTZoneFactory().newFinderForm( cfFormManager );
					cfFormManager.pushForm( finderForm );
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonServiceType = new CFButton();
		buttonServiceType.setVisible( true );
		buttonServiceType.setMinWidth( 200 );
		buttonServiceType.setMaxWidth( 200 );
		buttonServiceType.setPrefWidth( 200 );
		buttonServiceType.setMinHeight( 25 );
		buttonServiceType.setMaxHeight( 25 );
		buttonServiceType.setPrefHeight( 25 );
		vboxButtons.getChildren().add( buttonServiceType );
		buttonServiceType.setText( "Service Type..." );
		buttonServiceType.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					CFBorderPane finderForm = custSchema.getJavaFXSchema().getServiceTypeFactory().newFinderForm( cfFormManager );
					cfFormManager.pushForm( finderForm );
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonBack = new CFButton();
		buttonBack.setVisible( true );
		buttonBack.setMinWidth( 200 );
		buttonBack.setMaxWidth( 200 );
		buttonBack.setPrefWidth( 200 );
		buttonBack.setMinHeight( 25 );
		buttonBack.setMaxHeight( 25 );
		buttonBack.setPrefHeight( 25 );
		vboxButtons.getChildren().add( buttonBack );
		buttonBack.setText( "Back" );
		buttonBack.setOnAction( new EventHandler<ActionEvent>() {
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
