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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
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

public class CFSecCustLoginPane
extends CFBorderPane
implements ICFForm
{
	protected final String S_FormName = "Login";
	protected final String strDefault = "default";
	protected final String strSystem = "system";
	protected ICFFormManager cfFormManager = null;
	protected ICFSecCustSchema custSchema = null;
	protected CFSecCustFacetPane facetPane = null;
	protected CFLabel labelTitle = null;
	protected CFGridPane fieldGridPane = null;
	protected CFLabel labelServerURL = null;
	protected CFComboBox<String> comboServerURL = null;
	protected CFLabel labelCluster = null;
	protected CFComboBox<String> comboCluster = null;
	protected CFLabel labelTenant = null;
	protected CFTextField textTenant = null;
	protected CFLabel labelSecUser = null;
	protected CFTextField textSecUser = null;
	protected CFLabel labelPassword = null;
	protected CFPasswordField passwordPassword = null;
	protected CFHBox hboxButtons = null;
	protected CFButton buttonOk = null;
	protected CFButton buttonShowPublicKey = null;
	protected CFButton buttonExitApp = null;

	public CFSecCustLoginPane(
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
		labelTitle.setText( "Log In, Please" );
		Font f = labelTitle.getFont();
		Font largeBold = Font.font( f.getFamily(), FontWeight.BOLD, 20 );
		labelTitle.setFont( largeBold );
		labelTitle.setMinHeight( 35 );
		labelTitle.setMaxHeight( 35 );
		labelTitle.setMinWidth( 200 );
		labelTitle.setAlignment( Pos.CENTER );

		fieldGridPane = new CFGridPane();
		fieldGridPane.setPadding( new Insets(5) );
		fieldGridPane.setHgap( 5 );
		fieldGridPane.setVgap( 5 );
		fieldGridPane.setAlignment( Pos.CENTER );
		ColumnConstraints column1 = new ColumnConstraints( 125 );
		ColumnConstraints column2 = new ColumnConstraints( 125, 300, 500 );
		column2.setHgrow( Priority.ALWAYS );
		fieldGridPane.getColumnConstraints().addAll( column1, column2 );

		labelServerURL = new CFLabel();
		labelServerURL.setVisible( true );
		fieldGridPane.setHalignment( labelServerURL, HPos.LEFT );
		fieldGridPane.add( labelServerURL,  0,  0 );
		labelServerURL.setText( "Server URL" );

		ObservableList<String> serverURLOptions =
			FXCollections.observableArrayList();
		comboServerURL = new CFComboBox<String>( serverURLOptions );
		comboServerURL.setEditable( true );
		comboServerURL.valueProperty().addListener( new ChangeListener<String>() {
			@Override public void changed( ObservableValue ov, String t, String t1 ) {
			}
		});
		if( ! serverURLOptions.isEmpty() ) {
			comboServerURL.getSelectionModel().select( 0 );
		}
		fieldGridPane.add( comboServerURL,  1,  0 );

		labelCluster = new CFLabel();
		labelCluster.setVisible( true );
		fieldGridPane.setHalignment( labelCluster, HPos.LEFT );
		fieldGridPane.add( labelCluster,  0,  1 );
		labelCluster.setText( "Cluster" );

		ObservableList<String> clusterOptions =
			FXCollections.observableArrayList(
				strDefault,
				strSystem );
		comboCluster = new CFComboBox<String>( clusterOptions );
		comboCluster.setValue( strDefault );
		comboCluster.valueProperty().addListener( new ChangeListener<String>() {
			@Override public void changed( ObservableValue ov, String t, String t1 ) {
				if( strSystem.equals( t1 ) ) {
					textTenant.setEditable( false );
					textTenant.setText( strSystem );
				}
				else {
					textTenant.setEditable( true );
				}
			}
		});
		fieldGridPane.add( comboCluster,  1,  1 );

		labelTenant = new CFLabel();
		labelTenant.setVisible( true );
		fieldGridPane.setHalignment( labelTenant, HPos.LEFT );
		fieldGridPane.add( labelTenant,  0,  2 );
		labelTenant.setText( "Tenant" );

		textTenant = new CFTextField();
		textTenant.setVisible( true );
		fieldGridPane.setHalignment( textTenant, HPos.LEFT );
		fieldGridPane.add( textTenant,  1,  2 );

		labelSecUser = new CFLabel();
		labelSecUser.setVisible( true );
		fieldGridPane.setHalignment( labelSecUser, HPos.LEFT );
		fieldGridPane.add( labelSecUser,  0,  3 );
		labelSecUser.setText( "User Name" );

		textSecUser = new CFTextField();
		textSecUser.setVisible( true );
		fieldGridPane.setHalignment( textSecUser, HPos.LEFT );
		fieldGridPane.add( textSecUser,  1,  3 );

		labelPassword = new CFLabel();
		labelPassword.setVisible( true );
		fieldGridPane.setHalignment( labelPassword, HPos.LEFT );
		fieldGridPane.add( labelPassword,  0,  4 );
		labelPassword.setText( "Password" );

		passwordPassword = new CFPasswordField();
		passwordPassword.setVisible( true );
		passwordPassword.setMinWidth( 200 );
		passwordPassword.setMaxWidth( 200 );
		passwordPassword.setPrefWidth( 200 );
		fieldGridPane.setHalignment( passwordPassword, HPos.LEFT );
		fieldGridPane.add( passwordPassword,  1,  4 );

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
		buttonOk.setDefaultButton( true );
		hboxButtons.getChildren().add( buttonOk );
		buttonOk.setText( "Ok" );
		buttonOk.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				final String S_ProcName = "handle";
				try {
					ICFSecSchemaObj schemaObj = custSchema.getSchema();
					if( schemaObj.getAuthorization() != null ) {
						CFConsole.message( "Already authorized" );
						facetPane.showSession();
						return;
					}

					ICFSecSchema backingStore = schemaObj.getCFSecBackingStore();
					String serverURL = comboServerURL.getValue();
					String clusterName = comboCluster.getValue();
					String tenantName = textTenant.getText();
					String secUserName = textSecUser.getText();
					String dbPassword = passwordPassword.getText();
					if( ( ( serverURL == null ) || ( serverURL.length() <= 0 ) )
						|| ( ( clusterName == null ) || ( clusterName.length() <= 0 ) )
						|| ( ( tenantName == null ) || ( tenantName.length() <= 0 ) )
						|| ( ( secUserName == null ) || ( secUserName.length() <= 0 ) )
						|| ( ( dbPassword == null ) || ( dbPassword.length() <= 0 ) ) )
					{
						CFConsole.message( "All fields are required" );
						return;
					}

					try {
//						backingStore.setServerURL( serverURL );
//						if( ! schemaObj.connect( clusterName, tenantName, secUserName, dbPassword ) ) {
//							CFConsole.message( "Could not establish authorization" );
//							return;
//						}
						facetPane.loggedIn();
					}
					catch( RuntimeException x ) {
						CFConsole.message( "Could not establish connection: " + x.getMessage() );
						return;
					}
					CFConsole.message( "Login authorized" );
					facetPane.showSession();
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
		hboxButtons.getChildren().add( buttonExitApp );
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

		setTop( labelTitle );
		setCenter( fieldGridPane );
		setBottom( hboxButtons );

		this.setOnMouseMoved( new EventHandler<MouseEvent>() {
			@Override public void handle( MouseEvent event ) {
				if( custSchema != null ) {
					ICFSecSchemaObj schemaObj = custSchema.getSchema();
					if( schemaObj != null ) {
						if( schemaObj.getAuthorization() != null) {
							facetPane.getAuthorizationCallbacks().loggedIn();
							facetPane.showSession();
						}
					}
				}
			}
		} );
	}

	public void forceCancelAndClose() {
		if( cfFormManager != null ) {
			if( cfFormManager.getCurrentForm() == this ) {
				cfFormManager.closeCurrentForm();
			}
		}
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
