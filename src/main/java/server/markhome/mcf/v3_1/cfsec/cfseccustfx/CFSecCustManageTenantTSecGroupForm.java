// Description: Java 13 JavaFX Finder Form implementation for TSecGroup.

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

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import server.markhome.mcf.v3_1.cflib.*;
import server.markhome.mcf.v3_1.cflib.dbutil.*;
import server.markhome.mcf.v3_1.cflib.xml.*;
import server.markhome.mcf.v3_1.cflib.javafx.*;

import org.apache.commons.codec.binary.Base64;

import server.markhome.mcf.v3_1.cfsec.cfsec.*;
import server.markhome.mcf.v3_1.cfsec.cfsecobj.*;
import server.markhome.mcf.v3_1.cfsec.cfsecjavafx.*;
import server.markhome.mcf.v3_1.cfsec.cfseccustfx.CFSecCustManageTenantTSecGroupPane.CFSecCustTenantTSecGroupTabPaneEnum;

/**
 *	CFSecCustManageTSecGroupPane JavaFX Form implementation
 *	for TSecGroup.
 */
public class CFSecCustManageTenantTSecGroupForm
extends CFBorderPane
implements ICFSecJavaFXTSecGroupPaneCommon,
	ICFForm
{
	protected final String S_FormName = "Tenant Sec Group";
	protected ICFFormManager cfFormManager = null;
	protected ICFSecJavaFXSchema javafxSchema = null;
	protected ICFSecCustSchema custSchema = null;
	protected CFSecCustManageTenantTSecGroupPane javafxManagePane = null;
	protected CFHBox hboxMenu = null;
	protected CFVBox vboxMenuAdd = null;
	protected CFButton buttonAdd = null;
	protected CFButton buttonCancelAdd = null;
	protected CFButton buttonAddTSecGroup = null;
	protected CFButton buttonViewSelected = null;
	protected CFButton buttonEditSelected = null;
	protected CFButton buttonClose = null;
	protected CFButton buttonDeleteSelected = null;

	class ViewEditClosedCallback implements ICFFormClosedCallback {
		public ViewEditClosedCallback() {
		}

		public void formClosed( ICFLibAnyObj affectedObject ) {
			if( affectedObject != null ) {
				((ICFRefreshCallback)javafxManagePane).refreshMe();
			}
		}
	}

	protected ViewEditClosedCallback viewEditClosedCallback = null;

	public ICFFormClosedCallback getViewEditClosedCallback() {
		if( viewEditClosedCallback == null ) {
			viewEditClosedCallback = new ViewEditClosedCallback();
		}
		return( viewEditClosedCallback );
	}

	class DeleteCallback implements ICFDeleteCallback {
		public DeleteCallback() {
		}

		public void deleted( ICFLibAnyObj deletedObject ) {
			if( deletedObject != null ) {
				((ICFRefreshCallback)javafxManagePane).refreshMe();
			}
		}

		public void formClosed( ICFLibAnyObj affectedObject ) {
			if( affectedObject != null ) {
				((ICFRefreshCallback)javafxManagePane).refreshMe();
			}
		}
	}

	protected DeleteCallback deleteCallback = null;

	public ICFDeleteCallback getDeleteCallback() {
		if( deleteCallback == null ) {
			deleteCallback = new DeleteCallback();
		}
		return( deleteCallback );
	}

	public CFSecCustManageTenantTSecGroupForm( ICFFormManager formManager, ICFSecCustSchema argSchema ) {
		super();
		final String S_ProcName = "construct-schema-focus";
		if( formManager == null ) {
			throw new CFLibNullArgumentException( getClass(),
				S_ProcName,
				1,
				"formManager" );
		}
		cfFormManager = formManager;
		if( argSchema == null ) {
			throw new CFLibNullArgumentException( getClass(),
				S_ProcName,
				2,
				"argSchema" );
		}
		custSchema = argSchema;
		if( custSchema instanceof ICFSecJavaFXSchema ) {
			javafxSchema = (ICFSecJavaFXSchema)custSchema;
		}
		else {
			throw new CFLibUnsupportedClassException( getClass(),
				S_ProcName,
				"argSchema",
				argSchema,
				"ICFSecJavaFXSchema and ICFSecCustSchema" );
		}

		javafxManagePane = (CFSecCustManageTenantTSecGroupPane) custSchema.newManageTenantTSecGroupPane( cfFormManager );
		ICFSecTSecGroupObj javafxFocus = (ICFSecTSecGroupObj)javafxManagePane.getJavaFXFocus();
		setJavaFXFocus( javafxFocus );
		setTop( getHBoxMenu() );
		setCenter( javafxManagePane );
		adjustFinderButtons();
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

	public ICFSecJavaFXSchema getJavaFXSchema() {
		return( javafxSchema );
	}

	public CFHBox getHBoxMenu() {
		if( hboxMenu == null ) {
			hboxMenu = new CFHBox( 10 );

			buttonAddTSecGroup = new CFButton();
			buttonAddTSecGroup.setMinWidth( 200 );
			buttonAddTSecGroup.setText( "Add Tenant Sec Group..." );
			buttonAddTSecGroup.setOnAction( new EventHandler<ActionEvent>() {
				@Override public void handle( ActionEvent e ) {
					final String S_ProcName = "handle";
					try {
						ICFSecSchemaObj schemaObj = (ICFSecSchemaObj)javafxSchema.getSchema();
						if( schemaObj == null ) {
							throw new CFLibNullArgumentException( getClass(),
								S_ProcName,
								0,
								"schemaObj" );
						}
						ICFSecTSecGroupObj obj = (ICFSecTSecGroupObj)schemaObj.getTSecGroupTableObj().newInstance();
						ICFSecTSecGroupEditObj edit = (ICFSecTSecGroupEditObj)( obj.beginEdit() );
						if( edit == null ) {
							throw new CFLibNullArgumentException( getClass(),
								S_ProcName,
								0,
								"edit" );
						}
						ICFSecTenantObj secTenant = schemaObj.getSecTenant();
						edit.setRequiredContainerTenant( secTenant );
						CFBorderPane frame = javafxSchema.getTSecGroupFactory().newAddForm( cfFormManager, obj, getViewEditClosedCallback(), false );
						ICFSecJavaFXTSecGroupPaneCommon jpanelCommon = (ICFSecJavaFXTSecGroupPaneCommon)frame;
						jpanelCommon.setPaneMode( CFPane.PaneMode.Add );
						cfFormManager.pushForm( frame );
					}
					catch( Throwable t ) {
						CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
					}
				}
			});
			hboxMenu.getChildren().add( buttonAddTSecGroup );

			buttonViewSelected = new CFButton();
			buttonViewSelected.setMinWidth( 200 );
			buttonViewSelected.setText( "View Selected Tenant Sec Group" );
			buttonViewSelected.setOnAction( new EventHandler<ActionEvent>() {
				@Override public void handle( ActionEvent e ) {
					final String S_ProcName = "handle";
					try {
						ICFSecSchemaObj schemaObj = (ICFSecSchemaObj)javafxSchema.getSchema();
						if( schemaObj == null ) {
							throw new CFLibNullArgumentException( getClass(),
								S_ProcName,
								0,
								"schemaObj" );
						}
						ICFSecTSecGroupObj selectedInstance = getJavaFXFocusAsTSecGroup();
						if( selectedInstance != null ) {
							int classCode = selectedInstance.getClassCode();
							ICFSecSchema.ClassMapEntry entry = ICFSecSchema.getClassMapByRuntimeClassCode(classCode);
							if( entry.getSchemaName().equals("CFSec") && ICFSecTSecGroup.CLASS_CODE == entry.getBackingClassCode() ) {
								CFBorderPane frame = javafxSchema.getTSecGroupFactory().newViewEditForm( cfFormManager, selectedInstance, getViewEditClosedCallback(), false );
								((ICFSecJavaFXTSecGroupPaneCommon)frame).setPaneMode( CFPane.PaneMode.View );
								cfFormManager.pushForm( frame );
							}
							else {
								throw new CFLibUnsupportedClassException( getClass(),
									S_ProcName,
									"selectedInstance",
									selectedInstance,
									"ICFSecTSecGroupObj" );
							}
						}
					}
					catch( Throwable t ) {
						CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
					}
				}
			});
			hboxMenu.getChildren().add( buttonViewSelected );

			buttonEditSelected = new CFButton();
			buttonEditSelected.setMinWidth( 200 );
			buttonEditSelected.setText( "Edit Selected Tenant Sec Group" );
			buttonEditSelected.setOnAction( new EventHandler<ActionEvent>() {
				@Override public void handle( ActionEvent e ) {
					final String S_ProcName = "handle";
					try {
						ICFSecSchemaObj schemaObj = (ICFSecSchemaObj)javafxSchema.getSchema();
						if( schemaObj == null ) {
							throw new CFLibNullArgumentException( getClass(),
								S_ProcName,
								0,
								"schemaObj" );
						}
						ICFSecTSecGroupObj selectedInstance = getJavaFXFocusAsTSecGroup();
						if( selectedInstance != null ) {
							int classCode = selectedInstance.getClassCode();
							ICFSecSchema.ClassMapEntry entry = ICFSecSchema.getClassMapByRuntimeClassCode(classCode);
							if( entry.getSchemaName().equals("CFSec") && ICFSecTSecGroup.CLASS_CODE == entry.getBackingClassCode() ) {
								CFBorderPane frame = javafxSchema.getTSecGroupFactory().newViewEditForm( cfFormManager, selectedInstance, getViewEditClosedCallback(), false );
								((ICFSecJavaFXTSecGroupPaneCommon)frame).setPaneMode( CFPane.PaneMode.Edit );
								cfFormManager.pushForm( frame );
							}
							else {
								throw new CFLibUnsupportedClassException( getClass(),
									S_ProcName,
									"selectedInstance",
									selectedInstance,
									"ICFSecTSecGroupObj" );
							}
						}
					}
					catch( Throwable t ) {
						CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
					}
				}
			});
			hboxMenu.getChildren().add( buttonEditSelected );

			buttonDeleteSelected = new CFButton();
			buttonDeleteSelected.setMinWidth( 200 );
			buttonDeleteSelected.setText( "Delete Selected Tenant Sec Group" );
			buttonDeleteSelected.setOnAction( new EventHandler<ActionEvent>() {
				@Override public void handle( ActionEvent e ) {
					final String S_ProcName = "handle";
					try {
						ICFSecSchemaObj schemaObj = (ICFSecSchemaObj)javafxSchema.getSchema();
						if( schemaObj == null ) {
							throw new CFLibNullArgumentException( getClass(),
								S_ProcName,
								0,
								"schemaObj" );
						}
						ICFSecTSecGroupObj selectedInstance = getJavaFXFocusAsTSecGroup();
						if( selectedInstance != null ) {
							int classCode = selectedInstance.getClassCode();
							ICFSecSchema.ClassMapEntry entry = ICFSecSchema.getClassMapByRuntimeClassCode(classCode);
							if( entry.getSchemaName().equals("CFSec") && ICFSecTSecGroup.CLASS_CODE == entry.getBackingClassCode() ) {
								CFBorderPane frame = javafxSchema.getTSecGroupFactory().newAskDeleteForm( cfFormManager, selectedInstance, getDeleteCallback() );
								cfFormManager.pushForm( frame );
							}
							else {
								throw new CFLibUnsupportedClassException( getClass(),
									S_ProcName,
									"selectedInstance",
									selectedInstance,
									"ICFSecTSecGroupObj" );
							}
						}
						else {
							throw new CFLibUnsupportedClassException( getClass(),
								S_ProcName,
								"selectedInstance",
								selectedInstance,
								"ICFSecTSecGroupObj" );
						}
					}
					catch( Throwable t ) {
						CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
					}
				}
			});
			hboxMenu.getChildren().add( buttonDeleteSelected );

			buttonClose = new CFButton();
			buttonClose.setMinWidth( 200 );
			buttonClose.setText( "Close" );
			buttonClose.setOnAction( new EventHandler<ActionEvent>() {
				@Override public void handle( ActionEvent e ) {
					final String S_ProcName = "handle";
					try {
						cfFormManager.closeCurrentForm();
					}
					catch( Throwable t ) {
						CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
					}
				}
			});
			hboxMenu.getChildren().add( buttonClose );
		}
		return( hboxMenu );
	}

	public ICFLibAnyObj getJavaFXFocus() {
		ICFLibAnyObj superretval = super.getJavaFXFocus();
		ICFLibAnyObj retval = superretval;
		if( javafxManagePane != null ) {
			retval = javafxManagePane.getJavaFXFocus();
			if( retval != null ) {
				if( ! retval.equals( superretval ) ) {
					setJavaFXFocus( retval );
				}
			}
		}
		return( retval );
	}

	public void setJavaFXFocus( ICFLibAnyObj value ) {
		final String S_ProcName = "setJavaFXFocus";
		if( ( value == null ) || ( value instanceof ICFSecSecGroupObj ) ) {
			super.setJavaFXFocus( value );
			if( javafxManagePane != null ) {
				javafxManagePane.setJavaFXFocus( value );
			}
		}
		else {
			throw new CFLibUnsupportedClassException( getClass(),
				S_ProcName,
				"value",
				value,
				"ICFSecSecGroupObj" );
		}
		adjustFinderButtons();
	}

	public ICFSecTSecGroupObj getJavaFXFocusAsTSecGroup() {
		ICFSecTSecGroupObj javafxFocus = (ICFSecTSecGroupObj)getJavaFXFocus();

		return( javafxFocus );
	}

	public void setJavaFXFocusAsTSecGroup( ICFSecTSecGroupObj value ) {
		setJavaFXFocus( value );
	}

	public void adjustFinderButtons() {
		ICFSecTSecGroupObj selectedObj = getJavaFXFocusAsTSecGroup();
		boolean disableState;
		if( selectedObj == null ) {
			disableState = true;
		}
		else {
			disableState = false;
		}

		if( buttonViewSelected != null ) {
			buttonViewSelected.setDisable( disableState );
		}
		if( buttonEditSelected != null ) {
			buttonEditSelected.setDisable( disableState );
		}
		if( buttonDeleteSelected != null ) {
			buttonDeleteSelected.setDisable( disableState );
		}
		if( buttonAddTSecGroup != null ) {
			buttonAddTSecGroup.setDisable( false );
		}
	}
}
