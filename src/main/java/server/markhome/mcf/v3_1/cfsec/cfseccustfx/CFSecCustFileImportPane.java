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

import java.io.File;
import java.io.IOException;
import java.math.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
import javafx.geometry.VPos;
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

public class CFSecCustFileImportPane
extends CFBorderPane
implements ICFForm
{
	protected final String S_FormName = "File Import";
	protected ICFFormManager cfFormManager = null;
	protected ICFSecCustSchema custSchema = null;
	protected CFSecCustFacetPane facetPane = null;
	protected CFLabel labelTitle = null;
	protected CFGridPane fieldGridPane = null;
	protected CFLabel labelFileName = null;
	protected CFTextField textFileName = null;
	protected CFLabel labelImportResults = null;
	protected CFTextArea areaImportResults = null;
	protected CFHBox hboxButtons = null;
	protected CFButton buttonChooseFile = null;
	protected CFButton buttonImportFile = null;
	protected CFButton buttonClose = null;
	protected FileChooser fileChooser = null;
	protected CFBorderPane myself = null;

	public CFSecCustFileImportPane(
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
		myself = this;
		custSchema = argSchema;
		facetPane = argFacet;

		labelTitle = new CFLabel();
		labelTitle.setText( "File Import" );
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

		labelFileName = new CFLabel();
		labelFileName.setVisible( true );
		fieldGridPane.setHalignment( labelFileName, HPos.LEFT );
		fieldGridPane.add( labelFileName,  0,  0 );
		labelFileName.setText( "File Name" );

		textFileName = new CFTextField();
		textFileName.setVisible( true );
		fieldGridPane.setHalignment( textFileName, HPos.LEFT );
		fieldGridPane.add( textFileName,  1,  0 );
		textFileName.setEditable( false );
		textFileName.setText( "" );

		labelImportResults = new CFLabel();
		labelImportResults.setVisible( true );
		fieldGridPane.setHalignment( labelImportResults, HPos.LEFT );
		fieldGridPane.setValignment( labelImportResults, VPos.TOP );
		fieldGridPane.add( labelImportResults,  0,  1 );
		labelImportResults.setText( "Import Results" );

		areaImportResults = new CFTextArea();
		areaImportResults.setVisible( true );
		areaImportResults.setMinHeight( 80 );
		areaImportResults.setPrefHeight( 80 );
		fieldGridPane.setHalignment( areaImportResults, HPos.LEFT );
		fieldGridPane.add( areaImportResults,  1,  1 );

		hboxButtons = new CFHBox( 10 );
		hboxButtons.setMinHeight( 35 );
		hboxButtons.setMaxHeight( 35 );
		hboxButtons.setPrefHeight( 35 );
		hboxButtons.setAlignment( Pos.CENTER );

		buttonChooseFile = new CFButton();
		buttonChooseFile.setVisible( true );
		buttonChooseFile.setMinWidth( 200 );
		buttonChooseFile.setMaxWidth( 200 );
		buttonChooseFile.setPrefWidth( 200 );
		buttonChooseFile.setMinHeight( 25 );
		buttonChooseFile.setMaxHeight( 25 );
		buttonChooseFile.setPrefHeight( 25 );
		hboxButtons.getChildren().add( buttonChooseFile );
		buttonChooseFile.setText( "Choose File..." );
		buttonChooseFile.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					areaImportResults.setText( "" );
					if( fileChooser == null ) {
						fileChooser = new FileChooser();
					}
					Window window = textFileName.getScene().getWindow();
					File fileChosen = fileChooser.showOpenDialog( window );
					if( fileChosen == null ) {
						textFileName.setText( "" );
					}
					else {
						String fullFileName = fileChosen.getAbsolutePath();
						textFileName.setText( fullFileName );
					}
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonImportFile = new CFButton();
		buttonImportFile.setVisible( true );
		buttonImportFile.setMinWidth( 200 );
		buttonImportFile.setMaxWidth( 200 );
		buttonImportFile.setPrefWidth( 200 );
		buttonImportFile.setMinHeight( 25 );
		buttonImportFile.setMaxHeight( 25 );
		buttonImportFile.setPrefHeight( 25 );
		hboxButtons.getChildren().add( buttonImportFile );
		buttonImportFile.setText( "Import File" );
		buttonImportFile.setOnAction( new EventHandler<ActionEvent>() {
			@Override public void handle( ActionEvent e ) {
				try {
					areaImportResults.setText( "" );
					String fileName = textFileName.getText();
					if( ( fileName == null ) || ( fileName.length() <= 0 ) ) {
						CFConsole.message( "File name must be specified" );
						return;
					}
					File file = new File( fileName );
					if( ! file.exists() ) {
						CFConsole.message( "Could not find file \"" + fileName + "\"" );
						return;
					}
					if( ! file.isFile() ) {
						CFConsole.message( "Not a file: \"" + fileName + "\"" );
						return;
					}
					if( ! file.canRead() ) {
						CFConsole.message( "Read permission denied for file \"" + fileName + "\"" );
						return;
					}
					areaImportResults.setText( "Reading file..." );
					String fileContents;
					try {
						byte[] rawFileContents = Files.readAllBytes( file.toPath() );
						fileContents = new String( rawFileContents, StandardCharsets.UTF_8 );
					}
					catch( IOException x ) {
						CFConsole.message( "Error reading from file \"" + fileName + "\"" );
						return;
					}
					areaImportResults.setText( "Processing..." );
					// Open the file and read the contents
					String logContents = null; // MSS TODO WORKING custSchema.getSchema().fileImport( fileName, fileContents );
					if( ( logContents == null ) || ( logContents.length() <= 0 ) ) {
						areaImportResults.setText( "Import complete." );
					}
					else {
						areaImportResults.setText( logContents );
					}
				}
				catch( Throwable t ) {
					CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
				}
			}
		});

		buttonClose = new CFButton();
		buttonClose.setVisible( true );
		buttonClose.setMinWidth( 200 );
		buttonClose.setMaxWidth( 200 );
		buttonClose.setPrefWidth( 200 );
		buttonClose.setMinHeight( 25 );
		buttonClose.setMaxHeight( 25 );
		buttonClose.setPrefHeight( 25 );
		hboxButtons.getChildren().add( buttonClose );
		buttonClose.setText( "Close" );
		buttonClose.setOnAction( new EventHandler<ActionEvent>() {
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
		setCenter( fieldGridPane );
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
