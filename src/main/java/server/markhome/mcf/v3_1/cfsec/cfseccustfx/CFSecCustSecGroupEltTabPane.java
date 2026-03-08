// Description: Java 13 JavaFX View/Edit Pane implementation for SecGroup.

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

import java.util.*;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.util.Callback;
import server.markhome.mcf.v3_1.cflib.*;
import server.markhome.mcf.v3_1.cflib.dbutil.*;
import server.markhome.mcf.v3_1.cflib.inz.Inz;
import server.markhome.mcf.v3_1.cflib.javafx.*;
import server.markhome.mcf.v3_1.cfsec.cfsecobj.*;
import server.markhome.mcf.v3_1.cfsec.cfsecjavafx.*;

/**
 *	CFSecJavaFXSecGroupViewEditPane JavaFX View/Edit Pane implementation
 *	for SecGroup.
 */
public class CFSecCustSecGroupEltTabPane
extends CFTabPane
implements ICFSecJavaFXSecGroupPaneCommon
{
	protected final String S_FormName = "Sec Group Elements";
	protected ICFFormManager cfFormManager = null;
	protected ICFSecJavaFXSchema javafxSchema = null;
	protected boolean javafxIsInitializing = true;
	protected ScrollPane attrScrollPane = null;
	protected CFGridPane attrPane = null;
	protected CFTabPane eltTabPane = null;

	public final String LABEL_TabSubGroup = "Sub-Groups";
	protected CFTab tabSubGroup = null;
	public final String LABEL_TabMember = "Members";
	protected CFTab tabMember = null;
	protected CFSecCustSecGroupSubGroupPane tabViewSubGroupPane = null;
	protected CFSecCustSecGrpMembPane tabViewMemberPane = null;

	public CFSecCustSecGroupEltTabPane( ICFFormManager formManager, ICFSecJavaFXSchema argSchema, ICFSecSecGroupObj argFocus ) {
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
		// argFocus is optional; focus may be set later during execution as
		// conditions of the runtime change.
		javafxSchema = argSchema;
		setJavaFXFocusAsSecGroup( argFocus );
		// Wire the newly constructed Panes/Tabs to this TabPane
		tabSubGroup = new CFTab();
		tabSubGroup.setText( LABEL_TabSubGroup );
		tabSubGroup.setContent( getTabViewSubGroupPane() );
		getTabs().add( tabSubGroup );
		tabMember = new CFTab();
		tabMember.setText( LABEL_TabMember );
		tabMember.setContent( getTabViewMemberPane() );
		getTabs().add( tabMember );
		javafxIsInitializing = false;
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

	public ICFSecJavaFXSchema getJavaFXSchema() {
		return( javafxSchema );
	}

	public void setJavaFXFocus( ICFLibAnyObj value ) {
		final String S_ProcName = "setJavaFXFocus";
		if( ( value == null ) || ( value instanceof ICFSecSecGroupObj ) ) {
			super.setJavaFXFocus( value );
			getTabViewMemberPane().setJavaFXFocus( value );
			getTabViewSubGroupPane().setJavaFXFocus( value );
		}
		else {
			throw new CFLibUnsupportedClassException( getClass(),
				S_ProcName,
				"value",
				value,
				"ICFSecSecGroupObj" );
		}
	}

	public void setJavaFXFocusAsSecGroup( ICFSecSecGroupObj value ) {
		setJavaFXFocus( value );
	}

	public ICFSecSecGroupObj getJavaFXFocusAsSecGroup() {
		return( (ICFSecSecGroupObj)getJavaFXFocus() );
	}

	protected class RefreshSubGroup
	implements ICFRefreshCallback
	{
		public RefreshSubGroup() {
		}

		public void refreshMe() {
			Collection<ICFSecSecGrpIncObj> dataCollection;
			ICFSecSecGroupObj focus = (ICFSecSecGroupObj)getJavaFXFocusAsSecGroup();
			if( focus != null ) {
				dataCollection = focus.getOptionalComponentsInclude( javafxIsInitializing );
			}
			else {
				dataCollection = null;
			}
			CFBorderPane pane = getTabViewSubGroupPane();
			ICFSecJavaFXSecGrpIncPaneList jpList = (ICFSecJavaFXSecGrpIncPaneList)pane;
			jpList.setJavaFXDataCollection( dataCollection );
		}
	}

	public CFBorderPane getTabViewSubGroupPane() {
		if( tabViewSubGroupPane == null ) {
			Collection<ICFSecSecGrpIncObj> dataCollection;
			ICFSecSecGroupObj focus = (ICFSecSecGroupObj)getJavaFXFocusAsSecGroup();
			if( focus != null ) {
				dataCollection = focus.getOptionalComponentsInclude( javafxIsInitializing );
			}
			else {
				dataCollection = null;
			}
			ICFSecSecGroupObj javafxContainer;
			if( ( focus != null ) && ( focus instanceof ICFSecSecGroupObj ) ) {
				javafxContainer = (ICFSecSecGroupObj)focus;
			}
			else {
				javafxContainer = null;
			}
			tabViewSubGroupPane = new CFSecCustSecGroupSubGroupPane( cfFormManager, javafxSchema, javafxContainer, null, dataCollection, new RefreshSubGroup(), false);
		}
		return( tabViewSubGroupPane );
	}

	protected class RefreshMember
	implements ICFRefreshCallback
	{
		public RefreshMember() {
		}

		public void refreshMe() {
			Collection<ICFSecSecGrpMembObj> dataCollection;
			ICFSecSecGroupObj focus = (ICFSecSecGroupObj)getJavaFXFocusAsSecGroup();
			if( focus != null ) {
				dataCollection = focus.getOptionalComponentsMember( javafxIsInitializing );
			}
			else {
				dataCollection = null;
			}
			CFBorderPane pane = getTabViewMemberPane();
			ICFSecJavaFXSecGrpMembPaneList jpList = (ICFSecJavaFXSecGrpMembPaneList)pane;
			jpList.setJavaFXDataCollection( dataCollection );
		}
	}

	public CFBorderPane getTabViewMemberPane() {
		if( tabViewMemberPane == null ) {
			Collection<ICFSecSecGrpMembObj> dataCollection;
			ICFSecSecGroupObj focus = (ICFSecSecGroupObj)getJavaFXFocusAsSecGroup();
			if( focus != null ) {
				dataCollection = focus.getOptionalComponentsMember( javafxIsInitializing );
			}
			else {
				dataCollection = null;
			}
			ICFSecSecGroupObj javafxContainer;
			if( ( focus != null ) && ( focus instanceof ICFSecSecGroupObj ) ) {
				javafxContainer = (ICFSecSecGroupObj)focus;
			}
			else {
				javafxContainer = null;
			}
			tabViewMemberPane = new CFSecCustSecGrpMembPane( cfFormManager, javafxSchema, javafxContainer, null, dataCollection, new RefreshMember(), false);
		}
		return( tabViewMemberPane );
	}

	public void setPaneMode( CFPane.PaneMode value ) {
		super.setPaneMode( value );
		if( tabViewSubGroupPane != null ) {
			tabViewSubGroupPane.setPaneMode( value );
		}
		if( tabViewMemberPane != null ) {
			tabViewMemberPane.setPaneMode( value );
		}
	}

	public class CFSecCustSecGrpMembPane
	extends CFBorderPane
	implements ICFSecJavaFXSecGroupPaneCommon
	{
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected ICFRefreshCallback javafxRefreshCallback = null;
		protected CFSecCustSecGrpMembListPane memberList = null;
		protected CFVBox vboxAddRemove = null;
		protected CFButton buttonAddMember = null;
		protected CFButton buttonRemoveMember = null;
		protected CFSecCustSecUserListPane userList = null;

		public CFSecCustSecGrpMembPane( ICFFormManager formManager,
			ICFSecJavaFXSchema argSchema,
			ICFSecSecGroupObj argContainer,
			ICFSecSecGrpMembObj argFocus,
			Collection<ICFSecSecGrpMembObj> argDataCollection,
			ICFRefreshCallback refreshCallback,
			boolean sortByChain )
		{
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
			// argFocus is optional; focus may be set later during execution as
			// conditions of the runtime change.
			javafxSchema = argSchema;
			javaFXFocus = argFocus;
			javafxRefreshCallback = refreshCallback;
			setLeft( getMemberList() );
			setCenter( getVBoxAddRemove() );
			setRight( getUserList() );
			javafxIsInitializing = false;
			adjustUserFeedback();
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

		public ICFSecJavaFXSchema getJavaFXSchema() {
			return( javafxSchema );
		}

		public void setPaneMode( CFPane.PaneMode value ) {
			super.setPaneMode( value );
		}

		public void setJavaFXFocus( ICFLibAnyObj value ) {
			final String S_ProcName = "setJavaFXFocus";
			if( value == null ) {
				super.setJavaFXFocus( null );
				adjustUserFeedback();
				getMemberList().setJavaFXDataCollection( null );
				getMemberList().setJavaFXFocus( null );
				getUserList().setJavaFXDataCollection( null );
				getUserList().setJavaFXFocus( null );
			}
			else if( value instanceof ICFSecSecGroupObj ) {
				super.setJavaFXFocus( value );
				ICFSecSecGroupObj secGroup = (ICFSecSecGroupObj)value;
				Collection<ICFSecSecGrpMembObj> members = secGroup.getOptionalComponentsMember();
				getMemberList().setJavaFXDataCollection( members );
				ICFSecSecUserObj curSecUser;
				Collection<ICFSecSecUserObj> allSecUser = javafxSchema.getSchema().getSecUserTableObj().readAllSecUser( javafxIsInitializing );
				LinkedList<ICFSecSecUserObj> listOfUnusedSecUser = new LinkedList<ICFSecSecUserObj>();
				Iterator<ICFSecSecUserObj> iterSecUser = allSecUser.iterator();
				while( iterSecUser.hasNext() ) {
					curSecUser = iterSecUser.next();
					if( curSecUser != null ) {
						if( ! memberList.includesSecUser( curSecUser ) ) {
							listOfUnusedSecUser.add( curSecUser );
						}
					}
				}
				getUserList().setJavaFXDataCollection( listOfUnusedSecUser );
			}
			else {
				throw new CFLibUnsupportedClassException( getClass(),
					S_ProcName,
					"value",
					value,
					"ICFSecSecGroupObj" );
			}
			adjustUserFeedback();
		}

		public ICFSecSecGroupObj getJavaFXFocusAsSecGroup() {
			return( (ICFSecSecGroupObj)getJavaFXFocus() );
		}

		public void setJavaFXFocusAsSecGroup( ICFSecSecGroupObj value ) {
			setJavaFXFocus( value );
		}

		public void refreshMe() {
			if( javafxRefreshCallback != null ) {
				javafxRefreshCallback.refreshMe();
			}
		}

		public CFVBox getVBoxAddRemove() {
			if( vboxAddRemove == null ) {
				vboxAddRemove = new CFVBox( 10 );
				buttonAddMember = new CFButton();
				buttonAddMember.setText( "<==" );
				buttonAddMember.setOnAction( new EventHandler<ActionEvent>() {
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
							ICFSecSecGrpMembObj obj = null;
							ICFSecSecGroupObj curSecGroup = getJavaFXFocusAsSecGroup();
							if( curSecGroup != null ) {
								ICFSecSecUserObj selectedSecUser = getUserList().getJavaFXFocusAsSecUser();
								if( selectedSecUser != null ) {
									obj = schemaObj.getSecGrpMembTableObj().newInstance();
									ICFSecSecGrpMembEditObj editObj = obj.beginEdit();
									editObj.setRequiredContainerGroup( curSecGroup );
									editObj.setRequiredParentUser( selectedSecUser );
									obj = editObj.create();
									editObj = null;
								}
							}
							setJavaFXFocusAsSecGroup( curSecGroup );
							if( obj != null ) {
								getMemberList().setJavaFXFocusAsSecGrpMemb( obj );
								getUserList().setJavaFXFocusAsSecUser( null );
							}
							else {
								getMemberList().setJavaFXFocusAsSecGrpMemb( null );
								getUserList().setJavaFXFocusAsSecUser( null );
							}
							adjustUserFeedback();
						}
						catch( Throwable t ) {
							CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
						}
					}
				});
				vboxAddRemove.getChildren().add( buttonAddMember );

				buttonRemoveMember = new CFButton();
				buttonRemoveMember.setText( "==>" );
				buttonRemoveMember.setOnAction( new EventHandler<ActionEvent>() {
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
							ICFSecSecUserObj selectedSecUser = null;
							ICFSecSecGroupObj curSecGroup = getJavaFXFocusAsSecGroup();
							ICFSecSecGrpMembObj selectedInstance = getMemberList().getJavaFXFocusAsSecGrpMemb();
							if( selectedInstance != null ) {
								selectedSecUser = selectedInstance.getRequiredParentUser();
								ICFSecSecGrpMembEditObj editInstance = selectedInstance.beginEdit();
								if( editInstance != null ) {
									editInstance.deleteInstance();
									editInstance = null;
								}
							}
							setJavaFXFocusAsSecGroup( curSecGroup );
							if( selectedSecUser != null ) {
								getMemberList().setJavaFXFocusAsSecGrpMemb( null );
								getUserList().setJavaFXFocusAsSecUser( selectedSecUser );
							}
							else {
								getMemberList().setJavaFXFocusAsSecGrpMemb( null );
								getUserList().setJavaFXFocusAsSecUser( null );
							}
							adjustUserFeedback();
						}
						catch( Throwable t ) {
							CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
						}
					}
				});
				vboxAddRemove.getChildren().add( buttonRemoveMember );
			}
			return( vboxAddRemove );
		}

		public CFSecCustSecGrpMembListPane getMemberList() {
			if( memberList == null ) {
				ICFSecSecGroupObj secGroup = getJavaFXFocusAsSecGroup();
				if( secGroup != null ) {
					memberList = new CFSecCustSecGrpMembListPane( getCFFormManager(),
						javafxSchema,
						secGroup,
						null,
						secGroup.getOptionalComponentsMember(),
						javafxRefreshCallback,
						false );
				}
				else {
					memberList = new CFSecCustSecGrpMembListPane( getCFFormManager(),
						javafxSchema,
						null,
						null,
						null,
						javafxRefreshCallback,
						false );
				}
			}
			return( memberList );
		}

		public CFSecCustSecUserListPane getUserList() {
			if( userList == null ) {
				ICFSecSecUserObj curSecUser;
				Collection<ICFSecSecUserObj> allSecUser = javafxSchema.getSchema().getSecUserTableObj().readAllSecUser( javafxIsInitializing );
				LinkedList<ICFSecSecUserObj> listOfUnusedSecUser = new LinkedList<ICFSecSecUserObj>();
				Iterator<ICFSecSecUserObj> iterSecUser = allSecUser.iterator();
				while( iterSecUser.hasNext() ) {
					curSecUser = iterSecUser.next();
					if( curSecUser != null ) {
						if( ! memberList.includesSecUser( curSecUser ) ) {
							listOfUnusedSecUser.add( curSecUser );
						}
					}
				}
				userList = new CFSecCustSecUserListPane( getCFFormManager(),
						javafxSchema,
						getJavaFXFocusAsSecGroup(),
						null,
						listOfUnusedSecUser,
						javafxRefreshCallback );
			}
			return( userList );
		}

		public void adjustUserFeedback() {
			ICFSecSecUserObj selectedUser = getUserList().getJavaFXFocusAsSecUser();
			if( selectedUser != null ) {
				if( buttonAddMember != null ) {
					buttonAddMember.setDisable( false );
				}
				else {
					buttonAddMember.setDisable( true );
				}
			}
			ICFSecSecGrpMembObj selectedGrpMemb = getMemberList().getJavaFXFocusAsSecGrpMemb();
			if( selectedGrpMemb != null ) {
				if( buttonRemoveMember != null ) {
					buttonRemoveMember.setDisable( false );
				}
				else {
					buttonRemoveMember.setDisable( true );
				}
			}
		}
	}

	public class CFSecCustSecGrpMembListPane
	extends CFBorderPane
	implements ICFSecJavaFXSecGrpMembPaneList
	{
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected Collection<ICFSecSecGrpMembObj> javafxDataCollection = null;
		protected ObservableList<ICFSecSecGrpMembObj> observableListOfSecGrpMemb = null;
		protected TableView<ICFSecSecGrpMembObj> dataTable = null;
		protected ScrollPane dataScrollPane = null;
		protected TableColumn<ICFSecSecGrpMembObj, CFLibDbKeyHash256> tableColumnSecGrpMembId = null;
		protected TableColumn<ICFSecSecGrpMembObj, ICFSecSecUserObj> tableColumnParentUser = null;
		protected HashMap<CFLibDbKeyHash256,ICFSecSecUserObj> containsSecUser = null;

		public final String S_ColumnNames[] = { "Name" };
		protected ICFFormManager cfFormManager = null;
		protected boolean javafxIsInitializing = true;
		protected boolean javafxSortByChain = false;
		protected ICFSecSecGroupObj javafxContainer = null;
		protected ICFRefreshCallback javafxRefreshCallback = null;
		class ViewEditClosedCallback implements ICFFormClosedCallback {
			public ViewEditClosedCallback() {
			}

			public void formClosed( ICFLibAnyObj affectedObject ) {
				if( affectedObject != null ) {
					Parent cont = getParent();
					while( ( cont != null ) && ( ! ( cont instanceof ICFForm ) ) ) {
						cont = cont.getParent();
					}
					if( cont != null ) {
						if( cont instanceof CFSecJavaFXSecGrpMembListPane ) {
							CFSecJavaFXSecGrpMembListPane form = (CFSecJavaFXSecGrpMembListPane)cont;
							form.refreshMe();
						}
					}
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

		public CFSecCustSecGrpMembListPane( ICFFormManager formManager,
			ICFSecJavaFXSchema argSchema,
			ICFSecSecGroupObj argContainer,
			ICFSecSecGrpMembObj argFocus,
			Collection<ICFSecSecGrpMembObj> argDataCollection,
			ICFRefreshCallback refreshCallback,
			boolean sortByChain )
		{
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
			// argFocus is optional; focus may be set later during execution as
			// conditions of the runtime change.
			javafxSchema = argSchema;
			javaFXFocus = argFocus;
			javafxContainer = argContainer;
			javafxRefreshCallback = refreshCallback;
			javafxSortByChain = sortByChain;
			setJavaFXDataCollection( argDataCollection );
			dataTable = new TableView<ICFSecSecGrpMembObj>();
			tableColumnSecGrpMembId = new TableColumn<ICFSecSecGrpMembObj,CFLibDbKeyHash256>( "Sec Group Member Id" );
			tableColumnSecGrpMembId.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecGrpMembObj,CFLibDbKeyHash256>,ObservableValue<CFLibDbKeyHash256> >() {
				public ObservableValue<CFLibDbKeyHash256> call( CellDataFeatures<ICFSecSecGrpMembObj, CFLibDbKeyHash256> p ) {
					ICFSecSecGrpMembObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						CFLibDbKeyHash256 value = obj.getRequiredSecGrpMembId();
						ReadOnlyObjectWrapper<CFLibDbKeyHash256> observable = new ReadOnlyObjectWrapper<CFLibDbKeyHash256>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnSecGrpMembId.setCellFactory( new Callback<TableColumn<ICFSecSecGrpMembObj,CFLibDbKeyHash256>,TableCell<ICFSecSecGrpMembObj,CFLibDbKeyHash256>>() {
				@Override public TableCell<ICFSecSecGrpMembObj,CFLibDbKeyHash256> call(
					TableColumn<ICFSecSecGrpMembObj,CFLibDbKeyHash256> arg)
				{
					return new CFDbKeyHash256TableCell<ICFSecSecGrpMembObj>();
				}
			});
			dataTable.getColumns().add( tableColumnSecGrpMembId );
			tableColumnParentUser = new TableColumn<ICFSecSecGrpMembObj, ICFSecSecUserObj>( "User" );
			tableColumnParentUser.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecGrpMembObj,ICFSecSecUserObj>,ObservableValue<ICFSecSecUserObj> >() {
				public ObservableValue<ICFSecSecUserObj> call( CellDataFeatures<ICFSecSecGrpMembObj, ICFSecSecUserObj> p ) {
					ICFSecSecGrpMembObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						ICFSecSecUserObj ref = obj.getRequiredParentUser();
						ReadOnlyObjectWrapper<ICFSecSecUserObj> observable = new ReadOnlyObjectWrapper<ICFSecSecUserObj>();
						observable.setValue( ref );
						return( observable );
					}
				}
			});
			tableColumnParentUser.setCellFactory( new Callback<TableColumn<ICFSecSecGrpMembObj,ICFSecSecUserObj>,TableCell<ICFSecSecGrpMembObj,ICFSecSecUserObj>>() {
				@Override public TableCell<ICFSecSecGrpMembObj,ICFSecSecUserObj> call(
					TableColumn<ICFSecSecGrpMembObj,ICFSecSecUserObj> arg)
				{
					return new CFReferenceTableCell<ICFSecSecGrpMembObj,ICFSecSecUserObj>();
				}
			});
			dataTable.getColumns().add( tableColumnParentUser );
			dataTable.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<ICFSecSecGrpMembObj>() {
					@Override public void changed( ObservableValue<? extends ICFSecSecGrpMembObj> observable,
						ICFSecSecGrpMembObj oldValue,
						ICFSecSecGrpMembObj newValue )
					{
						setJavaFXFocus( newValue );
						adjustUserFeedback();
					}
				});
			dataScrollPane = new ScrollPane( dataTable );
			setCenter( dataScrollPane );
			javafxIsInitializing = false;
			if( observableListOfSecGrpMemb != null ) {
				dataTable.setItems( observableListOfSecGrpMemb );
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

		public ICFSecJavaFXSchema getJavaFXSchema() {
			return( javafxSchema );
		}

		public void setPaneMode( CFPane.PaneMode value ) {
			super.setPaneMode( value );
		}

		public void setJavaFXFocus( ICFLibAnyObj value ) {
			final String S_ProcName = "setJavaFXFocus";
			if( value == null ) {
				super.setJavaFXFocus( null );
				dataTable.getSelectionModel().clearSelection();
				dataTable.scrollTo( 0 );
			}
			else if( value instanceof ICFSecSecGrpMembObj ) {
				super.setJavaFXFocus( value );
				ICFSecSecGrpMembObj obj = (ICFSecSecGrpMembObj)value;
				if( obj != dataTable.getSelectionModel().getSelectedItem() ) {
					dataTable.getSelectionModel().clearSelection();
					dataTable.scrollTo( obj );
					dataTable.getSelectionModel().select( obj );
				}
			}
			else {
				throw new CFLibUnsupportedClassException( getClass(),
					S_ProcName,
					"value",
					value,
					"ICFSecSecGrpMembObj" );
			}
			adjustUserFeedback();
		}

		public ICFSecSecGrpMembObj getJavaFXFocusAsSecGrpMemb() {
			return( (ICFSecSecGrpMembObj)getJavaFXFocus() );
		}

		public void setJavaFXFocusAsSecGrpMemb( ICFSecSecGrpMembObj value ) {
			setJavaFXFocus( value );
		}

		public class SecGrpMembByQualNameComparator
		implements Comparator<ICFSecSecGrpMembObj>
		{
			public SecGrpMembByQualNameComparator() {
			}

			public int compare( ICFSecSecGrpMembObj lhs, ICFSecSecGrpMembObj rhs ) {
				if( lhs == null ) {
					if( rhs == null ) {
						return( 0 );
					}
					else {
						return( -1 );
					}
				}
				else if( rhs == null ) {
					return( 1 );
				}
				else {
					String lhsValue = lhs.getObjQualifiedName();
					String rhsValue = rhs.getObjQualifiedName();
					if( lhsValue == null ) {
						if( rhsValue == null ) {
							return( 0 );
						}
						else {
							return( -1 );
						}
					}
					else if( rhsValue == null ) {
						return( 1 );
					}
					else {
						return( lhsValue.compareTo( rhsValue ) );
					}
				}
			}
		}

		protected SecGrpMembByQualNameComparator compareSecGrpMembByQualName = new SecGrpMembByQualNameComparator();

		public Collection<ICFSecSecGrpMembObj> getJavaFXDataCollection() {
			return( javafxDataCollection );
		}

		public void setJavaFXDataCollection( Collection<ICFSecSecGrpMembObj> value ) {
			final String S_ProcName = "setJavaFXDataCollection";
			ICFSecSecGrpMembObj curMember;
			ICFSecSecUserObj curSecUser;
			containsSecUser = new HashMap<CFLibDbKeyHash256,ICFSecSecUserObj>();
			javafxDataCollection = value;
			observableListOfSecGrpMemb = FXCollections.observableArrayList();
			if( javafxDataCollection != null ) {
					Iterator<ICFSecSecGrpMembObj> iter = javafxDataCollection.iterator();
					while( iter.hasNext() ) {
						curMember = iter.next();
						if( curMember != null ) {
							observableListOfSecGrpMemb.add( curMember );
							curSecUser = curMember.getRequiredParentUser();
							if( curSecUser != null ) {
								containsSecUser.put( curSecUser.getPKey(), curSecUser );
							}
						}
					}
					observableListOfSecGrpMemb.sort( compareSecGrpMembByQualName );
			}
			if( dataTable != null ) {
				dataTable.setItems( observableListOfSecGrpMemb );
				// Hack from stackoverflow to fix JavaFX TableView refresh issue
				((TableColumn)dataTable.getColumns().get(0)).setVisible( false );
				((TableColumn)dataTable.getColumns().get(0)).setVisible( true );
			}
		}

		public ICFSecSecGroupObj getJavaFXContainer() {
			return( javafxContainer );
		}

		public void setJavaFXContainer( ICFSecSecGroupObj value ) {
			javafxContainer = value;
		}

		public void refreshMe() {
			if( javafxRefreshCallback != null ) {
				javafxRefreshCallback.refreshMe();
			}
		}

		public boolean includesSecUser( ICFSecSecUserObj secUser ) {
			if( secUser == null ) {
				return( false );
			}
			if( containsSecUser == null ) {
				return( false );
			}
			return( containsSecUser.containsKey( secUser.getPKey() ) );
		}

		public void adjustUserFeedback() {
			Node parent = getParent();
			while( ( parent != null ) && ( ! ( parent instanceof CFSecCustSecGrpMembListPane ) ) ) {
				parent = parent.getParent();
			}
			if( parent != null ) {
				CFSecCustSecGrpMembListPane win = (CFSecCustSecGrpMembListPane)parent;
				win.adjustUserFeedback();
			}
		}
	}

	/**
	 *	CFSecCustSecGrpIncListPane JavaFX List of Obj Pane implementation
	 *	for SecGrpInc.
	 */
	public class CFSecCustSecGrpIncListPane
	extends CFBorderPane
	implements ICFSecJavaFXSecGrpIncPaneList
	{
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected Collection<ICFSecSecGrpIncObj> javafxDataCollection = null;
		protected ObservableList<ICFSecSecGrpIncObj> observableListOfSecGrpInc = null;
		protected TableView<ICFSecSecGrpIncObj> dataTable = null;
		protected ScrollPane dataScrollPane = null;
		protected TableColumn<ICFSecSecGrpIncObj, CFLibDbKeyHash256> tableColumnSecGrpIncId = null;
		protected TableColumn<ICFSecSecGrpIncObj, ICFSecSecGroupObj> tableColumnParentSubGroup = null;
		protected HashMap<CFLibDbKeyHash256,ICFSecSecGroupObj> containsSecGroup = null;

		public final String S_ColumnNames[] = { "Name" };
		protected ICFFormManager cfFormManager = null;
		protected boolean javafxIsInitializing = true;
		protected boolean javafxSortByChain = false;
		protected ICFSecSecGroupObj javafxContainer = null;
		protected ICFRefreshCallback javafxRefreshCallback = null;

		public CFSecCustSecGrpIncListPane( ICFFormManager formManager,
			ICFSecJavaFXSchema argSchema,
			ICFSecSecGroupObj argContainer,
			ICFSecSecGrpIncObj argFocus,
			Collection<ICFSecSecGrpIncObj> argDataCollection,
			ICFRefreshCallback refreshCallback,
			boolean sortByChain )
		{
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
			// argFocus is optional; focus may be set later during execution as
			// conditions of the runtime change.
			javafxSchema = argSchema;
			javaFXFocus = argFocus;
			javafxContainer = argContainer;
			javafxRefreshCallback = refreshCallback;
			javafxSortByChain = sortByChain;
			setJavaFXDataCollection( argDataCollection );
			dataTable = new TableView<ICFSecSecGrpIncObj>();
			tableColumnSecGrpIncId = new TableColumn<ICFSecSecGrpIncObj,CFLibDbKeyHash256>( "Sec Group Include Id" );
			tableColumnSecGrpIncId.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecGrpIncObj,CFLibDbKeyHash256>,ObservableValue<CFLibDbKeyHash256> >() {
				public ObservableValue<CFLibDbKeyHash256> call( CellDataFeatures<ICFSecSecGrpIncObj, CFLibDbKeyHash256> p ) {
					ICFSecSecGrpIncObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						CFLibDbKeyHash256 value = obj.getRequiredSecGrpIncId();
						ReadOnlyObjectWrapper<CFLibDbKeyHash256> observable = new ReadOnlyObjectWrapper<CFLibDbKeyHash256>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnSecGrpIncId.setCellFactory( new Callback<TableColumn<ICFSecSecGrpIncObj,CFLibDbKeyHash256>,TableCell<ICFSecSecGrpIncObj,CFLibDbKeyHash256>>() {
				@Override public TableCell<ICFSecSecGrpIncObj,CFLibDbKeyHash256> call(
					TableColumn<ICFSecSecGrpIncObj,CFLibDbKeyHash256> arg)
				{
					return new CFDbKeyHash256TableCell<ICFSecSecGrpIncObj>();
				}
			});
			dataTable.getColumns().add( tableColumnSecGrpIncId );
			tableColumnParentSubGroup = new TableColumn<ICFSecSecGrpIncObj, ICFSecSecGroupObj>( "SubGroup" );
			tableColumnParentSubGroup.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecGrpIncObj,ICFSecSecGroupObj>,ObservableValue<ICFSecSecGroupObj> >() {
				public ObservableValue<ICFSecSecGroupObj> call( CellDataFeatures<ICFSecSecGrpIncObj, ICFSecSecGroupObj> p ) {
					ICFSecSecGrpIncObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						ICFSecSecGroupObj ref = obj.getRequiredParentSubGroup();
						ReadOnlyObjectWrapper<ICFSecSecGroupObj> observable = new ReadOnlyObjectWrapper<ICFSecSecGroupObj>();
						observable.setValue( ref );
						return( observable );
					}
				}
			});
			tableColumnParentSubGroup.setCellFactory( new Callback<TableColumn<ICFSecSecGrpIncObj,ICFSecSecGroupObj>,TableCell<ICFSecSecGrpIncObj,ICFSecSecGroupObj>>() {
				@Override public TableCell<ICFSecSecGrpIncObj,ICFSecSecGroupObj> call(
					TableColumn<ICFSecSecGrpIncObj,ICFSecSecGroupObj> arg)
				{
					return new CFReferenceTableCell<ICFSecSecGrpIncObj,ICFSecSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnParentSubGroup );
			dataTable.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<ICFSecSecGrpIncObj>() {
					@Override public void changed( ObservableValue<? extends ICFSecSecGrpIncObj> observable,
						ICFSecSecGrpIncObj oldValue,
						ICFSecSecGrpIncObj newValue )
					{
						setJavaFXFocus( newValue );
					}
				});
			dataScrollPane = new ScrollPane( dataTable );
			setCenter( dataScrollPane );
			javafxIsInitializing = false;
			if( observableListOfSecGrpInc != null ) {
				dataTable.setItems( observableListOfSecGrpInc );
			}
			adjustUserFeedback();
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

		public ICFSecJavaFXSchema getJavaFXSchema() {
			return( javafxSchema );
		}

		public void setPaneMode( CFPane.PaneMode value ) {
			super.setPaneMode( value );
			adjustUserFeedback();
		}

		public void setJavaFXFocus( ICFLibAnyObj value ) {
			final String S_ProcName = "setJavaFXFocus";
			if( value == null ) {
				super.setJavaFXFocus( null );
				dataTable.getSelectionModel().clearSelection();
				dataTable.scrollTo( 0 );
			}
			else if( value instanceof ICFSecSecGrpIncObj ) {
				super.setJavaFXFocus( value );
				ICFSecSecGrpIncObj obj = (ICFSecSecGrpIncObj)value;
				if( obj != dataTable.getSelectionModel().getSelectedItem() ) {
					dataTable.getSelectionModel().clearSelection();
					dataTable.scrollTo( obj );
					dataTable.getSelectionModel().select( obj );
				}
			}
			else {
				throw new CFLibUnsupportedClassException( getClass(),
					S_ProcName,
					"value",
					value,
					"ICFSecSecGrpMembObj" );
			}
		}

		public ICFSecSecGrpIncObj getJavaFXFocusAsSecGrpInc() {
			return( (ICFSecSecGrpIncObj)getJavaFXFocus() );
		}

		public void setJavaFXFocusAsSecGrpInc( ICFSecSecGrpIncObj value ) {
			setJavaFXFocus( value );
		}

		public class SecGrpIncByQualNameComparator
		implements Comparator<ICFSecSecGrpIncObj>
		{
			public SecGrpIncByQualNameComparator() {
			}

			public int compare( ICFSecSecGrpIncObj lhs, ICFSecSecGrpIncObj rhs ) {
				if( lhs == null ) {
					if( rhs == null ) {
						return( 0 );
					}
					else {
						return( -1 );
					}
				}
				else if( rhs == null ) {
					return( 1 );
				}
				else {
					String lhsValue = lhs.getObjQualifiedName();
					String rhsValue = rhs.getObjQualifiedName();
					if( lhsValue == null ) {
						if( rhsValue == null ) {
							return( 0 );
						}
						else {
							return( -1 );
						}
					}
					else if( rhsValue == null ) {
						return( 1 );
					}
					else {
						return( lhsValue.compareTo( rhsValue ) );
					}
				}
			}
		}

		protected SecGrpIncByQualNameComparator compareSecGrpIncByQualName = new SecGrpIncByQualNameComparator();

		public Collection<ICFSecSecGrpIncObj> getJavaFXDataCollection() {
			return( javafxDataCollection );
		}

		public void setJavaFXDataCollection( Collection<ICFSecSecGrpIncObj> value ) {
			javafxDataCollection = value;
			ICFSecSecGroupObj secGroup;
			ICFSecSecGrpIncObj obj;
			observableListOfSecGrpInc = FXCollections.observableArrayList();
			containsSecGroup = new HashMap<CFLibDbKeyHash256,ICFSecSecGroupObj>();
			if( javafxDataCollection != null ) {
					Iterator<ICFSecSecGrpIncObj> iter = javafxDataCollection.iterator();
					while( iter.hasNext() ) {
						obj = iter.next();
						observableListOfSecGrpInc.add( obj );
						secGroup = obj.getRequiredParentSubGroup();
						if( secGroup != null ) {
							containsSecGroup.put( secGroup.getPKey(), secGroup );
						}
					}
					observableListOfSecGrpInc.sort( compareSecGrpIncByQualName );
			}
			if( dataTable != null ) {
				dataTable.setItems( observableListOfSecGrpInc );
				// Hack from stackoverflow to fix JavaFX TableView refresh issue
				((TableColumn)dataTable.getColumns().get(0)).setVisible( false );
				((TableColumn)dataTable.getColumns().get(0)).setVisible( true );
			}
		}

		public ICFSecSecGroupObj getJavaFXContainer() {
			return( javafxContainer );
		}

		public void setJavaFXContainer( ICFSecSecGroupObj value ) {
			javafxContainer = value;
		}

		public void refreshMe() {
			if( javafxRefreshCallback != null ) {
				javafxRefreshCallback.refreshMe();
			}
		}

		public void adjustUserFeedback() {
			Node parent = getParent();
			while( ( parent != null ) && ( ! ( parent instanceof CFSecCustSecGrpMembListPane ) ) ) {
				parent = parent.getParent();
			}
			if( parent != null ) {
				CFSecCustSecGrpMembListPane win = (CFSecCustSecGrpMembListPane)parent;
				win.adjustUserFeedback();
			}
		}

		public boolean includesSecGroup( ICFSecSecGroupObj secGroup ) {
			if( secGroup == null ) {
				return( false );
			}
			if( containsSecGroup == null ) {
				return( false );
			}
			return( containsSecGroup.containsKey( secGroup.getPKey() ) );
		}
	}

	/**
	 *	CFSecCustSecUserListPane JavaFX List of Obj Pane implementation
	 *	for SecUser.
	 */
	public class CFSecCustSecUserListPane
	extends CFBorderPane
	implements ICFSecJavaFXSecUserPaneList
	{
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected Collection<ICFSecSecUserObj> javafxDataCollection = null;
		protected ObservableList<ICFSecSecUserObj> observableListOfSecUser = null;
		protected TableView<ICFSecSecUserObj> dataTable = null;
		protected ScrollPane dataScrollPane = null;
		protected TableColumn<ICFSecSecUserObj, CFLibDbKeyHash256> tableColumnSecUserId = null;
		protected TableColumn<ICFSecSecUserObj, String> tableColumnLoginId = null;
		protected TableColumn<ICFSecSecUserObj, String> tableColumnEMailAddress = null;
		protected TableColumn<ICFSecSecUserObj, CFLibUuid6> tableColumnEMailConfirmUuid = null;
		protected TableColumn<ICFSecSecUserObj, String> tableColumnPasswordHash = null;
		protected TableColumn<ICFSecSecUserObj, CFLibUuid6> tableColumnPasswordResetUuid = null;
		protected TableColumn<ICFSecSecUserObj, ICFSecSecDeviceObj> tableColumnLookupDefDev = null;

		public final String S_ColumnNames[] = { "Name" };
		protected ICFFormManager cfFormManager = null;
		protected boolean javafxIsInitializing = true;
		protected ICFLibAnyObj javafxContainer = null;
		protected ICFRefreshCallback javafxRefreshCallback = null;
		class ViewEditClosedCallback implements ICFFormClosedCallback {
			public ViewEditClosedCallback() {
			}

			public void formClosed( ICFLibAnyObj affectedObject ) {
				if( affectedObject != null ) {
					Parent cont = getParent();
					while( ( cont != null ) && ( ! ( cont instanceof ICFForm ) ) ) {
						cont = cont.getParent();
					}
					if( cont != null ) {
						if( cont instanceof CFSecJavaFXSecUserListPane ) {
							CFSecJavaFXSecUserListPane form = (CFSecJavaFXSecUserListPane)cont;
							form.refreshMe();
						}
					}
				}
			}
		}

		public CFSecCustSecUserListPane( ICFFormManager formManager,
			ICFSecJavaFXSchema argSchema,
			ICFLibAnyObj argContainer,
			ICFSecSecUserObj argFocus,
			Collection<ICFSecSecUserObj> argDataCollection,
			ICFRefreshCallback refreshCallback )
		{
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
			// argFocus is optional; focus may be set later during execution as
			// conditions of the runtime change.
			javafxSchema = argSchema;
			javaFXFocus = argFocus;
			javafxContainer = argContainer;
			javafxRefreshCallback = refreshCallback;
			setJavaFXDataCollection( argDataCollection );
			dataTable = new TableView<ICFSecSecUserObj>();
			tableColumnSecUserId = new TableColumn<ICFSecSecUserObj,CFLibDbKeyHash256>( "Sec User Id" );
			tableColumnSecUserId.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecUserObj,CFLibDbKeyHash256>,ObservableValue<CFLibDbKeyHash256> >() {
				public ObservableValue<CFLibDbKeyHash256> call( CellDataFeatures<ICFSecSecUserObj, CFLibDbKeyHash256> p ) {
					ICFSecSecUserObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						CFLibDbKeyHash256 value = obj.getRequiredSecUserId();
						ReadOnlyObjectWrapper<CFLibDbKeyHash256> observable = new ReadOnlyObjectWrapper<CFLibDbKeyHash256>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnSecUserId.setCellFactory( new Callback<TableColumn<ICFSecSecUserObj,CFLibDbKeyHash256>,TableCell<ICFSecSecUserObj,CFLibDbKeyHash256>>() {
				@Override public TableCell<ICFSecSecUserObj,CFLibDbKeyHash256> call(
					TableColumn<ICFSecSecUserObj,CFLibDbKeyHash256> arg)
				{
					return new CFDbKeyHash256TableCell<ICFSecSecUserObj>();
				}
			});
			dataTable.getColumns().add( tableColumnSecUserId );
			tableColumnLoginId = new TableColumn<ICFSecSecUserObj,String>( "Login Id" );
			tableColumnLoginId.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecUserObj,String>,ObservableValue<String> >() {
				public ObservableValue<String> call( CellDataFeatures<ICFSecSecUserObj, String> p ) {
					ICFSecSecUserObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						String value = obj.getRequiredLoginId();
						ReadOnlyObjectWrapper<String> observable = new ReadOnlyObjectWrapper<String>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnLoginId.setCellFactory( new Callback<TableColumn<ICFSecSecUserObj,String>,TableCell<ICFSecSecUserObj,String>>() {
				@Override public TableCell<ICFSecSecUserObj,String> call(
					TableColumn<ICFSecSecUserObj,String> arg)
				{
					return new CFStringTableCell<ICFSecSecUserObj>();
				}
			});
			dataTable.getColumns().add( tableColumnLoginId );
			tableColumnEMailAddress = new TableColumn<ICFSecSecUserObj,String>( "EMail Address" );
			tableColumnEMailAddress.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecUserObj,String>,ObservableValue<String> >() {
				public ObservableValue<String> call( CellDataFeatures<ICFSecSecUserObj, String> p ) {
					ICFSecSecUserObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						String value = obj.getRequiredEMailAddress();
						ReadOnlyObjectWrapper<String> observable = new ReadOnlyObjectWrapper<String>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnEMailAddress.setCellFactory( new Callback<TableColumn<ICFSecSecUserObj,String>,TableCell<ICFSecSecUserObj,String>>() {
				@Override public TableCell<ICFSecSecUserObj,String> call(
					TableColumn<ICFSecSecUserObj,String> arg)
				{
					return new CFStringTableCell<ICFSecSecUserObj>();
				}
			});
			dataTable.getColumns().add( tableColumnEMailAddress );
			tableColumnEMailConfirmUuid = new TableColumn<ICFSecSecUserObj,CFLibUuid6>( "EMail Confirmation UUID6" );
			tableColumnEMailConfirmUuid.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecUserObj,CFLibUuid6>,ObservableValue<CFLibUuid6> >() {
				public ObservableValue<CFLibUuid6> call( CellDataFeatures<ICFSecSecUserObj, CFLibUuid6> p ) {
					ICFSecSecUserObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						CFLibUuid6 value = obj.getOptionalEMailConfirmUuid6();
						ReadOnlyObjectWrapper<CFLibUuid6> observable = new ReadOnlyObjectWrapper<CFLibUuid6>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnEMailConfirmUuid.setCellFactory( new Callback<TableColumn<ICFSecSecUserObj,CFLibUuid6>,TableCell<ICFSecSecUserObj,CFLibUuid6>>() {
				@Override public TableCell<ICFSecSecUserObj,CFLibUuid6> call(
					TableColumn<ICFSecSecUserObj,CFLibUuid6> arg)
				{
					return new CFUuid6TableCell<ICFSecSecUserObj>();
				}
			});
			dataTable.getColumns().add( tableColumnEMailConfirmUuid );
			tableColumnPasswordHash = new TableColumn<ICFSecSecUserObj,String>( "Password Hash" );
			tableColumnPasswordHash.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecUserObj,String>,ObservableValue<String> >() {
				public ObservableValue<String> call( CellDataFeatures<ICFSecSecUserObj, String> p ) {
					ICFSecSecUserObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						String value = obj.getRequiredPasswordHash();
						ReadOnlyObjectWrapper<String> observable = new ReadOnlyObjectWrapper<String>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnPasswordHash.setCellFactory( new Callback<TableColumn<ICFSecSecUserObj,String>,TableCell<ICFSecSecUserObj,String>>() {
				@Override public TableCell<ICFSecSecUserObj,String> call(
					TableColumn<ICFSecSecUserObj,String> arg)
				{
					return new CFStringTableCell<ICFSecSecUserObj>();
				}
			});
			dataTable.getColumns().add( tableColumnPasswordHash );
			tableColumnPasswordResetUuid = new TableColumn<ICFSecSecUserObj,CFLibUuid6>( "Password Reset CFLibUuid6" );
			tableColumnPasswordResetUuid.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecUserObj,CFLibUuid6>,ObservableValue<CFLibUuid6> >() {
				public ObservableValue<CFLibUuid6> call( CellDataFeatures<ICFSecSecUserObj, CFLibUuid6> p ) {
					ICFSecSecUserObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						CFLibUuid6 value = obj.getOptionalPasswordResetUuid6();
						ReadOnlyObjectWrapper<CFLibUuid6> observable = new ReadOnlyObjectWrapper<CFLibUuid6>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnPasswordResetUuid.setCellFactory( new Callback<TableColumn<ICFSecSecUserObj,CFLibUuid6>,TableCell<ICFSecSecUserObj,CFLibUuid6>>() {
				@Override public TableCell<ICFSecSecUserObj,CFLibUuid6> call(
					TableColumn<ICFSecSecUserObj,CFLibUuid6> arg)
				{
					return new CFUuid6TableCell<ICFSecSecUserObj>();
				}
			});
			dataTable.getColumns().add( tableColumnPasswordResetUuid );
			tableColumnLookupDefDev = new TableColumn<ICFSecSecUserObj, ICFSecSecDeviceObj>( "Default Sec Device" );
			tableColumnLookupDefDev.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecUserObj,ICFSecSecDeviceObj>,ObservableValue<ICFSecSecDeviceObj> >() {
				public ObservableValue<ICFSecSecDeviceObj> call( CellDataFeatures<ICFSecSecUserObj, ICFSecSecDeviceObj> p ) {
					ICFSecSecUserObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						ICFSecSecDeviceObj ref = obj.getOptionalLookupDefDev();
						ReadOnlyObjectWrapper<ICFSecSecDeviceObj> observable = new ReadOnlyObjectWrapper<ICFSecSecDeviceObj>();
						observable.setValue( ref );
						return( observable );
					}
				}
			});
			tableColumnLookupDefDev.setCellFactory( new Callback<TableColumn<ICFSecSecUserObj,ICFSecSecDeviceObj>,TableCell<ICFSecSecUserObj,ICFSecSecDeviceObj>>() {
				@Override public TableCell<ICFSecSecUserObj,ICFSecSecDeviceObj> call(
					TableColumn<ICFSecSecUserObj,ICFSecSecDeviceObj> arg)
				{
					return new CFReferenceTableCell<ICFSecSecUserObj,ICFSecSecDeviceObj>();
				}
			});
			dataTable.getColumns().add( tableColumnLookupDefDev );
			dataTable.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<ICFSecSecUserObj>() {
					@Override public void changed( ObservableValue<? extends ICFSecSecUserObj> observable,
						ICFSecSecUserObj oldValue,
						ICFSecSecUserObj newValue )
					{
						setJavaFXFocus( newValue );
						adjustUserFeedback();
					}
				});
			dataScrollPane = new ScrollPane( dataTable );
			setCenter( dataScrollPane );
			javafxIsInitializing = false;
			if( observableListOfSecUser != null ) {
				dataTable.setItems( observableListOfSecUser );
			}
			adjustUserFeedback();
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

		public ICFSecJavaFXSchema getJavaFXSchema() {
			return( javafxSchema );
		}

		public void setPaneMode( CFPane.PaneMode value ) {
			super.setPaneMode( value );
		}

		public void setJavaFXFocus( ICFLibAnyObj value ) {
			final String S_ProcName = "setJavaFXFocus";
			if( value == null ) {
				super.setJavaFXFocus( null );
				dataTable.getSelectionModel().clearSelection();
				dataTable.scrollTo( 0 );
			}
			else if( value instanceof ICFSecSecUserObj ) {
				super.setJavaFXFocus( value );
				ICFSecSecUserObj obj = (ICFSecSecUserObj)value;
				if( obj != dataTable.getSelectionModel().getSelectedItem() ) {
					dataTable.getSelectionModel().clearSelection();
					dataTable.scrollTo( obj );
					dataTable.getSelectionModel().select( obj );
				}
			}
			else {
				throw new CFLibUnsupportedClassException( getClass(),
					S_ProcName,
					"value",
					value,
					"ICFSecSecGrpMembObj" );
			}
			adjustUserFeedback();
		}

		public ICFSecSecUserObj getJavaFXFocusAsSecUser() {
			return( (ICFSecSecUserObj)getJavaFXFocus() );
		}

		public void setJavaFXFocusAsSecUser( ICFSecSecUserObj value ) {
			setJavaFXFocus( value );
		}

		public class SecUserByQualNameComparator
		implements Comparator<ICFSecSecUserObj>
		{
			public SecUserByQualNameComparator() {
			}

			public int compare( ICFSecSecUserObj lhs, ICFSecSecUserObj rhs ) {
				if( lhs == null ) {
					if( rhs == null ) {
						return( 0 );
					}
					else {
						return( -1 );
					}
				}
				else if( rhs == null ) {
					return( 1 );
				}
				else {
					String lhsValue = lhs.getObjQualifiedName();
					String rhsValue = rhs.getObjQualifiedName();
					if( lhsValue == null ) {
						if( rhsValue == null ) {
							return( 0 );
						}
						else {
							return( -1 );
						}
					}
					else if( rhsValue == null ) {
						return( 1 );
					}
					else {
						return( lhsValue.compareTo( rhsValue ) );
					}
				}
			}

			public void adjustUserFeedback() {
				Node parent = getParent();
				while( ( parent != null ) && ( ! ( parent instanceof CFSecCustSecGrpMembListPane ) ) ) {
					parent = parent.getParent();
				}
				if( parent != null ) {
					CFSecCustSecGrpMembListPane win = (CFSecCustSecGrpMembListPane)parent;
					win.adjustUserFeedback();
				}
			}
		}

		protected SecUserByQualNameComparator compareSecUserByQualName = new SecUserByQualNameComparator();

		public Collection<ICFSecSecUserObj> getJavaFXDataCollection() {
			return( javafxDataCollection );
		}

		public void setJavaFXDataCollection( Collection<ICFSecSecUserObj> value ) {
			final String S_ProcName = "setJavaFXDataCollection";
			ICFSecSecUserObj cur;
			javafxDataCollection = value;

			observableListOfSecUser = FXCollections.observableArrayList();
			if( javafxDataCollection != null ) {
					Iterator<ICFSecSecUserObj> iter = javafxDataCollection.iterator();
					while( iter.hasNext() ) {
						cur = iter.next();
						observableListOfSecUser.add( cur );
					}
					observableListOfSecUser.sort( compareSecUserByQualName );
			}
			if( dataTable != null ) {
				dataTable.setItems( observableListOfSecUser );
				// Hack from stackoverflow to fix JavaFX TableView refresh issue
				((TableColumn)dataTable.getColumns().get(0)).setVisible( false );
				((TableColumn)dataTable.getColumns().get(0)).setVisible( true );
			}
		}

		public ICFLibAnyObj getJavaFXContainer() {
			return( javafxContainer );
		}

		public void setJavaFXContainer( ICFLibAnyObj value ) {
			javafxContainer = value;
		}

		public void refreshMe() {
			if( javafxRefreshCallback != null ) {
				javafxRefreshCallback.refreshMe();
			}
		}

		public void adjustUserFeedback() {
			Node parent = getParent();
			while( ( parent != null ) && ( ! ( parent instanceof CFSecCustSecGrpMembListPane ) ) ) {
				parent = parent.getParent();
			}
			if( parent != null ) {
				CFSecCustSecGrpMembListPane win = (CFSecCustSecGrpMembListPane)parent;
				win.adjustUserFeedback();
			}
		}
	}

	/**
	 *	CFSecCustSecGroupListAllPane JavaFX List of Obj Pane implementation
	 *	for SecGroup.
	 */
	public class CFSecCustSecGroupListAllPane
	extends CFBorderPane
	implements ICFSecJavaFXSecGroupPaneList
	{
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected Collection<ICFSecSecGroupObj> javafxDataCollection = null;
		protected ObservableList<ICFSecSecGroupObj> observableListOfSecGroup = null;
		protected TableView<ICFSecSecGroupObj> dataTable = null;
		protected ScrollPane dataScrollPane = null;
		protected TableColumn<ICFSecSecGroupObj, CFLibDbKeyHash256> tableColumnSecGroupId = null;
		protected TableColumn<ICFSecSecGroupObj, String> tableColumnName = null;
		protected TableColumn<ICFSecSecGroupObj, Boolean> tableColumnIsVisible = null;

		public final String S_ColumnNames[] = { "Name" };
		protected ICFFormManager cfFormManager = null;
		protected boolean javafxIsInitializing = true;
		protected boolean javafxSortByChain = false;
		protected ICFSecClusterObj javafxContainer = null;
		protected ICFRefreshCallback javafxRefreshCallback = null;

		public CFSecCustSecGroupListAllPane( ICFFormManager formManager,
			ICFSecJavaFXSchema argSchema,
			ICFSecClusterObj argContainer,
			ICFSecSecGroupObj argFocus,
			Collection<ICFSecSecGroupObj> argDataCollection,
			ICFRefreshCallback refreshCallback,
			boolean sortByChain )
		{
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
			// argFocus is optional; focus may be set later during execution as
			// conditions of the runtime change.
			javafxSchema = argSchema;
			javaFXFocus = argFocus;
			javafxContainer = argContainer;
			javafxRefreshCallback = refreshCallback;
			javafxSortByChain = sortByChain;
			setJavaFXDataCollection( argDataCollection );
			dataTable = new TableView<ICFSecSecGroupObj>();
			tableColumnSecGroupId = new TableColumn<ICFSecSecGroupObj,CFLibDbKeyHash256>( "Sec Group Id" );
			tableColumnSecGroupId.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecGroupObj,CFLibDbKeyHash256>,ObservableValue<CFLibDbKeyHash256> >() {
				public ObservableValue<CFLibDbKeyHash256> call( CellDataFeatures<ICFSecSecGroupObj, CFLibDbKeyHash256> p ) {
					ICFSecSecGroupObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						CFLibDbKeyHash256 value = obj.getRequiredSecGroupId();
						ReadOnlyObjectWrapper<CFLibDbKeyHash256> observable = new ReadOnlyObjectWrapper<CFLibDbKeyHash256>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnSecGroupId.setCellFactory( new Callback<TableColumn<ICFSecSecGroupObj,CFLibDbKeyHash256>,TableCell<ICFSecSecGroupObj,CFLibDbKeyHash256>>() {
				@Override public TableCell<ICFSecSecGroupObj,CFLibDbKeyHash256> call(
					TableColumn<ICFSecSecGroupObj,CFLibDbKeyHash256> arg)
				{
					return new CFDbKeyHash256TableCell<ICFSecSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnSecGroupId );
			tableColumnName = new TableColumn<ICFSecSecGroupObj,String>( "Name" );
			tableColumnName.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecGroupObj,String>,ObservableValue<String> >() {
				public ObservableValue<String> call( CellDataFeatures<ICFSecSecGroupObj, String> p ) {
					ICFSecSecGroupObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						String value = obj.getRequiredName();
						ReadOnlyObjectWrapper<String> observable = new ReadOnlyObjectWrapper<String>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnName.setCellFactory( new Callback<TableColumn<ICFSecSecGroupObj,String>,TableCell<ICFSecSecGroupObj,String>>() {
				@Override public TableCell<ICFSecSecGroupObj,String> call(
					TableColumn<ICFSecSecGroupObj,String> arg)
				{
					return new CFStringTableCell<ICFSecSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnName );
			tableColumnIsVisible = new TableColumn<ICFSecSecGroupObj,Boolean>( "IsVisible" );
			tableColumnIsVisible.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecGroupObj,Boolean>,ObservableValue<Boolean> >() {
				public ObservableValue<Boolean> call( CellDataFeatures<ICFSecSecGroupObj, Boolean> p ) {
					ICFSecSecGroupObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						boolean value = obj.getRequiredIsVisible();
						Boolean wrapped = Boolean.valueOf( value );
						ReadOnlyObjectWrapper<Boolean> observable = new ReadOnlyObjectWrapper<Boolean>();
						observable.setValue( wrapped );
						return( observable );
					}
				}
			});
			tableColumnIsVisible.setCellFactory( new Callback<TableColumn<ICFSecSecGroupObj,Boolean>,TableCell<ICFSecSecGroupObj,Boolean>>() {
				@Override public TableCell<ICFSecSecGroupObj,Boolean> call(
					TableColumn<ICFSecSecGroupObj,Boolean> arg)
				{
					return new CFBoolTableCell<ICFSecSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnIsVisible );
			dataTable.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<ICFSecSecGroupObj>() {
					@Override public void changed( ObservableValue<? extends ICFSecSecGroupObj> observable,
						ICFSecSecGroupObj oldValue,
						ICFSecSecGroupObj newValue )
					{
						setJavaFXFocus( newValue );
					}
				});
			dataScrollPane = new ScrollPane( dataTable );
			setCenter( dataScrollPane );
			javafxIsInitializing = false;
			if( observableListOfSecGroup != null ) {
				dataTable.setItems( observableListOfSecGroup );
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

		public ICFSecJavaFXSchema getJavaFXSchema() {
			return( javafxSchema );
		}

		public void setPaneMode( CFPane.PaneMode value ) {
			super.setPaneMode( value );
		}

		public void setJavaFXFocus( ICFLibAnyObj value ) {
			final String S_ProcName = "setJavaFXFocus";
			if( value == null ) {
				super.setJavaFXFocus( null );
				dataTable.getSelectionModel().clearSelection();
				dataTable.scrollTo( 0 );
			}
			else if( value instanceof ICFSecSecGroupObj ) {
				super.setJavaFXFocus( value );
				ICFSecSecGroupObj obj = (ICFSecSecGroupObj)value;
				if( obj != dataTable.getSelectionModel().getSelectedItem() ) {
					dataTable.getSelectionModel().clearSelection();
					dataTable.scrollTo( obj );
					dataTable.getSelectionModel().select( obj );
				}
			}
			else {
				throw new CFLibUnsupportedClassException( getClass(),
					S_ProcName,
					"value",
					value,
					"ICFSecSecGrpMembObj" );
			}
		}

		public ICFSecSecGroupObj getJavaFXFocusAsSecGroup() {
			return( (ICFSecSecGroupObj)getJavaFXFocus() );
		}

		public void setJavaFXFocusAsSecGroup( ICFSecSecGroupObj value ) {
			setJavaFXFocus( value );
		}

		public class SecGroupByQualNameComparator
		implements Comparator<ICFSecSecGroupObj>
		{
			public SecGroupByQualNameComparator() {
			}

			public int compare( ICFSecSecGroupObj lhs, ICFSecSecGroupObj rhs ) {
				if( lhs == null ) {
					if( rhs == null ) {
						return( 0 );
					}
					else {
						return( -1 );
					}
				}
				else if( rhs == null ) {
					return( 1 );
				}
				else {
					String lhsValue = lhs.getObjQualifiedName();
					String rhsValue = rhs.getObjQualifiedName();
					if( lhsValue == null ) {
						if( rhsValue == null ) {
							return( 0 );
						}
						else {
							return( -1 );
						}
					}
					else if( rhsValue == null ) {
						return( 1 );
					}
					else {
						return( lhsValue.compareTo( rhsValue ) );
					}
				}
			}
		}

		protected SecGroupByQualNameComparator compareSecGroupByQualName = new SecGroupByQualNameComparator();

		public Collection<ICFSecSecGroupObj> getJavaFXDataCollection() {
			return( javafxDataCollection );
		}

		public void setJavaFXDataCollection( Collection<ICFSecSecGroupObj> value ) {
			final String S_ProcName = "setJavaFXDataCollection";
			javafxDataCollection = value;
			observableListOfSecGroup = FXCollections.observableArrayList();
			if( javafxDataCollection != null ) {
					Iterator<ICFSecSecGroupObj> iter = javafxDataCollection.iterator();
					while( iter.hasNext() ) {
						observableListOfSecGroup.add( iter.next() );
					}
					observableListOfSecGroup.sort( compareSecGroupByQualName );
			}
			if( dataTable != null ) {
				dataTable.setItems( observableListOfSecGroup );
				// Hack from stackoverflow to fix JavaFX TableView refresh issue
				((TableColumn)dataTable.getColumns().get(0)).setVisible( false );
				((TableColumn)dataTable.getColumns().get(0)).setVisible( true );
			}
		}

		public ICFSecClusterObj getJavaFXContainer() {
			return( javafxContainer );
		}

		public void setJavaFXContainer( ICFSecClusterObj value ) {
			javafxContainer = value;
		}

		public void refreshMe() {
			if( javafxRefreshCallback != null ) {
				javafxRefreshCallback.refreshMe();
			}
		}

		public void adjustUserFeedback() {
			Node parent = getParent();
			while( ( parent != null ) && ( ! ( parent instanceof CFSecCustSecGrpMembListPane ) ) ) {
				parent = parent.getParent();
			}
			if( parent != null ) {
				CFSecCustSecGrpMembListPane win = (CFSecCustSecGrpMembListPane)parent;
				win.adjustUserFeedback();
			}
		}
	}

	/**
	 *	CFSecCustSecGroupListVisiblePane JavaFX List of Obj Pane implementation
	 *	for SecGroup.
	 */
	public class CFSecCustSecGroupListVisiblePane
	extends CFBorderPane
	implements ICFSecJavaFXSecGroupPaneList
	{
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected Collection<ICFSecSecGroupObj> javafxDataCollection = null;
		protected ObservableList<ICFSecSecGroupObj> observableListOfSecGroup = null;
		protected TableView<ICFSecSecGroupObj> dataTable = null;
		protected ScrollPane dataScrollPane = null;
		protected TableColumn<ICFSecSecGroupObj, CFLibDbKeyHash256> tableColumnSecGroupId = null;
		protected TableColumn<ICFSecSecGroupObj, String> tableColumnName = null;
		protected TableColumn<ICFSecSecGroupObj, Boolean> tableColumnIsVisible = null;

		public final String S_ColumnNames[] = { "Name" };
		protected ICFFormManager cfFormManager = null;
		protected boolean javafxIsInitializing = true;
		protected boolean javafxSortByChain = false;
		protected ICFSecClusterObj javafxContainer = null;
		protected ICFRefreshCallback javafxRefreshCallback = null;

		public CFSecCustSecGroupListVisiblePane( ICFFormManager formManager,
			ICFSecJavaFXSchema argSchema,
			ICFSecClusterObj argContainer,
			ICFSecSecGroupObj argFocus,
			Collection<ICFSecSecGroupObj> argDataCollection,
			ICFRefreshCallback refreshCallback,
			boolean sortByChain )
		{
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
			// argFocus is optional; focus may be set later during execution as
			// conditions of the runtime change.
			javafxSchema = argSchema;
			javaFXFocus = argFocus;
			javafxContainer = argContainer;
			javafxRefreshCallback = refreshCallback;
			javafxSortByChain = sortByChain;
			setJavaFXDataCollection( argDataCollection );
			dataTable = new TableView<ICFSecSecGroupObj>();
			tableColumnSecGroupId = new TableColumn<ICFSecSecGroupObj,CFLibDbKeyHash256>( "Sec Group Id" );
			tableColumnSecGroupId.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecGroupObj,CFLibDbKeyHash256>,ObservableValue<CFLibDbKeyHash256> >() {
				public ObservableValue<CFLibDbKeyHash256> call( CellDataFeatures<ICFSecSecGroupObj, CFLibDbKeyHash256> p ) {
					ICFSecSecGroupObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						CFLibDbKeyHash256 value = obj.getRequiredSecGroupId();
						ReadOnlyObjectWrapper<CFLibDbKeyHash256> observable = new ReadOnlyObjectWrapper<CFLibDbKeyHash256>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnSecGroupId.setCellFactory( new Callback<TableColumn<ICFSecSecGroupObj,CFLibDbKeyHash256>,TableCell<ICFSecSecGroupObj,CFLibDbKeyHash256>>() {
				@Override public TableCell<ICFSecSecGroupObj,CFLibDbKeyHash256> call(
					TableColumn<ICFSecSecGroupObj,CFLibDbKeyHash256> arg)
				{
					return new CFDbKeyHash256TableCell<ICFSecSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnSecGroupId );
			tableColumnName = new TableColumn<ICFSecSecGroupObj,String>( "Name" );
			tableColumnName.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecGroupObj,String>,ObservableValue<String> >() {
				public ObservableValue<String> call( CellDataFeatures<ICFSecSecGroupObj, String> p ) {
					ICFSecSecGroupObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						String value = obj.getRequiredName();
						ReadOnlyObjectWrapper<String> observable = new ReadOnlyObjectWrapper<String>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnName.setCellFactory( new Callback<TableColumn<ICFSecSecGroupObj,String>,TableCell<ICFSecSecGroupObj,String>>() {
				@Override public TableCell<ICFSecSecGroupObj,String> call(
					TableColumn<ICFSecSecGroupObj,String> arg)
				{
					return new CFStringTableCell<ICFSecSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnName );
			tableColumnIsVisible = new TableColumn<ICFSecSecGroupObj,Boolean>( "IsVisible" );
			tableColumnIsVisible.setCellValueFactory( new Callback<CellDataFeatures<ICFSecSecGroupObj,Boolean>,ObservableValue<Boolean> >() {
				public ObservableValue<Boolean> call( CellDataFeatures<ICFSecSecGroupObj, Boolean> p ) {
					ICFSecSecGroupObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						boolean value = obj.getRequiredIsVisible();
						Boolean wrapped = Boolean.valueOf( value );
						ReadOnlyObjectWrapper<Boolean> observable = new ReadOnlyObjectWrapper<Boolean>();
						observable.setValue( wrapped );
						return( observable );
					}
				}
			});
			tableColumnIsVisible.setCellFactory( new Callback<TableColumn<ICFSecSecGroupObj,Boolean>,TableCell<ICFSecSecGroupObj,Boolean>>() {
				@Override public TableCell<ICFSecSecGroupObj,Boolean> call(
					TableColumn<ICFSecSecGroupObj,Boolean> arg)
				{
					return new CFBoolTableCell<ICFSecSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnIsVisible );
			dataTable.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<ICFSecSecGroupObj>() {
					@Override public void changed( ObservableValue<? extends ICFSecSecGroupObj> observable,
						ICFSecSecGroupObj oldValue,
						ICFSecSecGroupObj newValue )
					{
						setJavaFXFocus( newValue );
					}
				});
			dataScrollPane = new ScrollPane( dataTable );
			setCenter( dataScrollPane );
			javafxIsInitializing = false;
			if( observableListOfSecGroup != null ) {
				dataTable.setItems( observableListOfSecGroup );
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

		public ICFSecJavaFXSchema getJavaFXSchema() {
			return( javafxSchema );
		}

		public void setPaneMode( CFPane.PaneMode value ) {
			super.setPaneMode( value );
		}

		public void setJavaFXFocus( ICFLibAnyObj value ) {
			final String S_ProcName = "setJavaFXFocus";
			if( value == null ) {
				super.setJavaFXFocus( null );
				dataTable.getSelectionModel().clearSelection();
				dataTable.scrollTo( 0 );
			}
			else if( value instanceof ICFSecSecGroupObj ) {
				super.setJavaFXFocus( value );
				ICFSecSecGroupObj obj = (ICFSecSecGroupObj)value;
				if( obj != dataTable.getSelectionModel().getSelectedItem() ) {
					dataTable.getSelectionModel().clearSelection();
					dataTable.scrollTo( obj );
					dataTable.getSelectionModel().select( obj );
				}
			}
			else {
				throw new CFLibUnsupportedClassException( getClass(),
					S_ProcName,
					"value",
					value,
					"ICFSecSecGrpMembObj" );
			}
		}

		public ICFSecSecGroupObj getJavaFXFocusAsSecGroup() {
			return( (ICFSecSecGroupObj)getJavaFXFocus() );
		}

		public void setJavaFXFocusAsSecGroup( ICFSecSecGroupObj value ) {
			setJavaFXFocus( value );
		}

		public class SecGroupByQualNameComparator
		implements Comparator<ICFSecSecGroupObj>
		{
			public SecGroupByQualNameComparator() {
			}

			public int compare( ICFSecSecGroupObj lhs, ICFSecSecGroupObj rhs ) {
				if( lhs == null ) {
					if( rhs == null ) {
						return( 0 );
					}
					else {
						return( -1 );
					}
				}
				else if( rhs == null ) {
					return( 1 );
				}
				else {
					String lhsValue = lhs.getObjQualifiedName();
					String rhsValue = rhs.getObjQualifiedName();
					if( lhsValue == null ) {
						if( rhsValue == null ) {
							return( 0 );
						}
						else {
							return( -1 );
						}
					}
					else if( rhsValue == null ) {
						return( 1 );
					}
					else {
						return( lhsValue.compareTo( rhsValue ) );
					}
				}
			}

			public void adjustUserFeedback() {
				Node parent = getParent();
				while( ( parent != null ) && ( ! ( parent instanceof CFSecCustSecGrpMembListPane ) ) ) {
					parent = parent.getParent();
				}
				if( parent != null ) {
					CFSecCustSecGrpMembListPane win = (CFSecCustSecGrpMembListPane)parent;
					win.adjustUserFeedback();
				}
			}
		}

		protected SecGroupByQualNameComparator compareSecGroupByQualName = new SecGroupByQualNameComparator();

		public Collection<ICFSecSecGroupObj> getJavaFXDataCollection() {
			return( javafxDataCollection );
		}

		public void setJavaFXDataCollection( Collection<ICFSecSecGroupObj> value ) {
			final String S_ProcName = "setJavaFXDataCollection";
			javafxDataCollection = value;
			observableListOfSecGroup = FXCollections.observableArrayList();
			if( javafxDataCollection != null ) {
					Iterator<ICFSecSecGroupObj> iter = javafxDataCollection.iterator();
					while( iter.hasNext() ) {
						observableListOfSecGroup.add( iter.next() );
					}
					observableListOfSecGroup.sort( compareSecGroupByQualName );
			}
			if( dataTable != null ) {
				dataTable.setItems( observableListOfSecGroup );
				// Hack from stackoverflow to fix JavaFX TableView refresh issue
				((TableColumn)dataTable.getColumns().get(0)).setVisible( false );
				((TableColumn)dataTable.getColumns().get(0)).setVisible( true );
			}
		}

		public ICFSecClusterObj getJavaFXContainer() {
			return( javafxContainer );
		}

		public void setJavaFXContainer( ICFSecClusterObj value ) {
			javafxContainer = value;
		}

		public void refreshMe() {
			if( javafxRefreshCallback != null ) {
				javafxRefreshCallback.refreshMe();
			}
		}
	}

	/**
	 *	CFSecJavaFXSecGroupGroupsPane JavaFX View/Edit Pane implementation
	 *	for SecGroup.
	 */
	public class CFSecCustSecGroupGroupsPane
	extends CFTabPane
	implements ICFSecJavaFXSecGroupPaneList
	{
		protected ICFFormManager cfFormManager = null;
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected boolean javafxIsInitializing = true;
		protected ICFSecClusterObj javafxContainer = null;
		protected Collection<ICFSecSecGroupObj> javafxCollection = null;

		public final String LABEL_TabVisibleGroups = "Visible Groups";
		protected CFTab tabVisibleGroups = null;
		protected CFSecCustSecGroupListVisiblePane tabViewVisibleGroupsPane = null;
		public final String LABEL_TabAllGroups = "All Groups";
		protected CFTab tabAllGroups = null;
		protected CFSecCustSecGroupListAllPane tabViewAllGroupsPane = null;

		public CFSecCustSecGroupGroupsPane( ICFFormManager formManager, ICFSecJavaFXSchema argSchema, ICFSecSecGroupObj argFocus ) {
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
			// argFocus is optional; focus may be set later during execution as
			// conditions of the runtime change.
			javafxSchema = argSchema;
			setJavaFXFocusAsSecGroup( argFocus );
			// Wire the newly constructed Panes/Tabs to this TabPane
			tabVisibleGroups = new CFTab();
			tabVisibleGroups.setText( LABEL_TabVisibleGroups );
			tabVisibleGroups.setContent( getTabViewVisibleGroupsPane() );
			getTabs().add( tabVisibleGroups );
			tabAllGroups = new CFTab();
			tabAllGroups.setText( LABEL_TabAllGroups );
			tabAllGroups.setContent( getTabViewAllGroupsPane() );
			getTabs().add( tabAllGroups );
			javafxIsInitializing = false;
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

		public ICFSecJavaFXSchema getJavaFXSchema() {
			return( javafxSchema );
		}

		@Override public ICFLibAnyObj getJavaFXFocus() {
			final String S_ProcName = "getJavaFXFocus";
			ICFLibAnyObj retval = null;
			SingleSelectionModel<Tab> selectionModel = getSelectionModel();
			if( selectionModel != null ) {
				Tab selected = selectionModel.getSelectedItem();
				if( selected != null ) {
					Node content = selected.getContent();
					if( content != null ) {
						if( content == getTabViewVisibleGroupsPane() ) {
							retval = getTabViewVisibleGroupsPane().getJavaFXFocusAsSecGroup();
						}
						else if( content == getTabViewAllGroupsPane() ) {
							retval = getTabViewAllGroupsPane().getJavaFXFocusAsSecGroup();
						}
						else {
							throw new CFLibRuntimeException(getClass(),
								S_ProcName,
								Inz.x("cflibjavafx.common.CurrentlySelectedTabNotVisible"),
								Inz.s("cflibjavafx.common.CurrentlySelectedTabNotVisible"));
						}
					}
				}
			}
			return( retval );
		}

		@Override public void setJavaFXFocus( ICFLibAnyObj value ) {
			final String S_ProcName = "setJavaFXFocus";
			if( value == null ) {
				super.setJavaFXFocus( null );
			}
			else if( value instanceof ICFSecSecGroupObj ) {
				super.setJavaFXFocus( value );
				ICFSecSecGroupObj secGroup = (ICFSecSecGroupObj)value;
				if( tabViewVisibleGroupsPane != null ) {
					tabViewVisibleGroupsPane.setJavaFXFocusAsSecGroup( secGroup );
				}
				if( tabViewAllGroupsPane != null ) {
					tabViewAllGroupsPane.setJavaFXFocusAsSecGroup( secGroup );
				}
			}
			else {
				throw new CFLibUnsupportedClassException( getClass(),
					S_ProcName,
					"value",
					value,
					"ICFSecSecGroupObj" );
			}
		}

		public void setJavaFXFocusAsSecGroup( ICFSecSecGroupObj value ) {
			setJavaFXFocus( value );
		}

		public ICFSecSecGroupObj getJavaFXFocusAsSecGroup() {
			return( (ICFSecSecGroupObj)getJavaFXFocus() );
		}

		@Override public void setPaneMode( CFPane.PaneMode value ) {
			super.setPaneMode( value );
			if( tabViewVisibleGroupsPane != null ) {
				tabViewVisibleGroupsPane.setPaneMode( CFPane.PaneMode.View );
			}
			if( tabViewAllGroupsPane != null ) {
				tabViewAllGroupsPane.setPaneMode( CFPane.PaneMode.View );
			}
		}

		public CFSecCustSecGroupListVisiblePane getTabViewVisibleGroupsPane() {
			if( tabViewVisibleGroupsPane == null ) {
				tabViewVisibleGroupsPane = new CFSecCustSecGroupListVisiblePane( getCFFormManager(),
					getJavaFXSchema(),
					null,
					null,
					getJavaFXSchema().getSchema().getSecGroupTableObj().readSecGroupByClusterIdx( getJavaFXSchema().getSchema().getAuthorization().getSecClusterId(), javafxIsInitializing ),
					null,
					false );
				tabViewVisibleGroupsPane.setPaneMode( CFPane.PaneMode.View );
			}
			return( tabViewVisibleGroupsPane );
		}

		public CFSecCustSecGroupListAllPane getTabViewAllGroupsPane() {
			if( tabViewAllGroupsPane == null ) {
				tabViewAllGroupsPane = new CFSecCustSecGroupListAllPane( getCFFormManager(),
					getJavaFXSchema(),
					null,
					null,
					getJavaFXSchema().getSchema().getSecGroupTableObj().readSecGroupByClusterIdx( getJavaFXSchema().getSchema().getAuthorization().getSecClusterId(), javafxIsInitializing ),
					null,
					false );
				tabViewAllGroupsPane.setPaneMode( CFPane.PaneMode.View );
			}
			return( tabViewAllGroupsPane );
		}

		@Override public void setJavaFXDataCollection( Collection<ICFSecSecGroupObj> value ) {
			final String S_ProcName = "setJavaFXDataCollection";
			if( tabViewVisibleGroupsPane != null ) {
				LinkedList<ICFSecSecGroupObj> visible = new LinkedList<ICFSecSecGroupObj>();
				ICFSecSecGroupObj obj;
				Iterator<ICFSecSecGroupObj> iter = value.iterator();
				while( iter.hasNext() ) {
					obj = iter.next();
					if( obj.getRequiredIsVisible() ) {
						visible.add( obj );
					}
				}
				tabViewVisibleGroupsPane.setJavaFXDataCollection( visible );
			}
			if( tabViewAllGroupsPane != null ) {
				tabViewAllGroupsPane.setJavaFXDataCollection( value );
			}
			javafxCollection = value;
		}

		@Override public ICFSecClusterObj getJavaFXContainer() {
			return( javafxContainer );
		}

		@Override public void setJavaFXContainer(ICFSecClusterObj value) {
			javafxContainer = value;
		}

		@Override public Collection<ICFSecSecGroupObj> getJavaFXDataCollection() {
			return( javafxCollection );
		}
	}

	public class CFSecCustSecGroupSubGroupPane
	extends CFBorderPane
	implements ICFSecJavaFXSecGrpIncPaneCommon
	{
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected ICFRefreshCallback javafxRefreshCallback = null;
		protected CFSecCustSecGrpIncListPane includeList = null;
		protected CFVBox vboxAddRemove = null;
		protected CFButton buttonAddSubGroup = null;
		protected CFButton buttonRemoveSubGroup = null;
		protected CFSecCustSecGroupGroupsPane groupsPane = null;

		public CFSecCustSecGroupSubGroupPane( ICFFormManager formManager,
			ICFSecJavaFXSchema argSchema,
			ICFSecSecGroupObj argContainer,
			ICFSecSecGrpIncObj argFocus,
			Collection<ICFSecSecGrpIncObj> argDataCollection,
			ICFRefreshCallback refreshCallback,
			boolean sortByChain )
		{
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
			// argFocus is optional; focus may be set later during execution as
			// conditions of the runtime change.
			javafxSchema = argSchema;
			javaFXFocus = argFocus;
			javafxRefreshCallback = refreshCallback;
			setLeft( getIncludeList() );
			setCenter( getVBoxAddRemove() );
			setRight( getGroupsPane() );
			javafxIsInitializing = false;
			adjustUserFeedback();
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

		public ICFSecJavaFXSchema getJavaFXSchema() {
			return( javafxSchema );
		}

		public void setPaneMode( CFPane.PaneMode value ) {
			super.setPaneMode( value );
		}

		public void setJavaFXFocus( ICFLibAnyObj value ) {
			final String S_ProcName = "setJavaFXFocus";
			if( value == null ) {
				super.setJavaFXFocus( null );
				adjustUserFeedback();
				getIncludeList().setJavaFXDataCollection( null );
				getIncludeList().setJavaFXFocus( null );
				getGroupsPane().setJavaFXDataCollection( null );
				getGroupsPane().setJavaFXFocus( null );
			}
			else if( value instanceof ICFSecSecGroupObj ) {
				super.setJavaFXFocus( value );
				ICFSecSecGroupObj secGroup = (ICFSecSecGroupObj)value;
				Collection<ICFSecSecGrpIncObj> includes = secGroup.getOptionalComponentsInclude();
				getIncludeList().setJavaFXDataCollection( includes );
				ICFSecSecGroupObj curSecGroup;
				Collection<ICFSecSecGroupObj> allSecGroup = javafxSchema.getSchema().getSecGroupTableObj().readAllSecGroup( javafxIsInitializing );
				LinkedList<ICFSecSecGroupObj> listOfUnusedSecGroup = new LinkedList<ICFSecSecGroupObj>();
				Iterator<ICFSecSecGroupObj> iterSecGroup = allSecGroup.iterator();
				while( iterSecGroup.hasNext() ) {
					curSecGroup = iterSecGroup.next();
					if( curSecGroup != null ) {
						if( ! includeList.includesSecGroup( curSecGroup ) ) {
							listOfUnusedSecGroup.add( curSecGroup );
						}
					}
				}
				getGroupsPane().setJavaFXDataCollection( listOfUnusedSecGroup );
			}
			else {
				throw new CFLibUnsupportedClassException( getClass(),
					S_ProcName,
					"value",
					value,
					"ICFSecSecGroupObj" );
			}
			adjustUserFeedback();
		}

		public ICFSecSecGrpIncObj getJavaFXFocusAsSecGrpInc() {
			return( (ICFSecSecGrpIncObj)getJavaFXFocus() );
		}

		public void setJavaFXFocusAsSecGrpInc( ICFSecSecGrpIncObj value ) {
			setJavaFXFocus( value );
		}

		public void refreshMe() {
			if( javafxRefreshCallback != null ) {
				javafxRefreshCallback.refreshMe();
			}
			adjustUserFeedback();
		}

		public CFVBox getVBoxAddRemove() {
			if( vboxAddRemove == null ) {
				vboxAddRemove = new CFVBox( 10 );
				buttonAddSubGroup = new CFButton();
				buttonAddSubGroup.setText( "<==" );
				buttonAddSubGroup.setOnAction( new EventHandler<ActionEvent>() {
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
							ICFSecSecGrpIncObj obj = null;
							ICFSecSecGroupObj curSecGroup = getJavaFXFocusAsSecGroup();
							if( curSecGroup != null ) {
								ICFSecSecGroupObj selectedSecGroup = getGroupsPane().getJavaFXFocusAsSecGroup();
								if( selectedSecGroup != null ) {
									obj = schemaObj.getSecGrpIncTableObj().newInstance();
									ICFSecSecGrpIncEditObj editObj = obj.beginEdit();
									editObj.setRequiredContainerGroup( curSecGroup );
									editObj.setRequiredParentSubGroup( selectedSecGroup );
									obj = editObj.create();
									editObj = null;
								}
							}
							setJavaFXFocusAsSecGroup( curSecGroup );
							if( obj != null ) {
								getIncludeList().setJavaFXFocusAsSecGrpInc( obj );
								getGroupsPane().setJavaFXFocusAsSecGroup( null );
							}
							else {
								getIncludeList().setJavaFXFocusAsSecGrpInc( null );
								getGroupsPane().setJavaFXFocusAsSecGroup( null );
							}
							setJavaFXFocus( getJavaFXFocus() );
							adjustUserFeedback();
						}
						catch( Throwable t ) {
							CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
						}
					}
				});
				vboxAddRemove.getChildren().add( buttonAddSubGroup );

				buttonRemoveSubGroup = new CFButton();
				buttonRemoveSubGroup.setText( "==>" );
				buttonRemoveSubGroup.setOnAction( new EventHandler<ActionEvent>() {
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
							ICFSecSecGroupObj linkedSecGroup = null;
							ICFSecSecGroupObj curSecGroup = getJavaFXFocusAsSecGroup();
							if( curSecGroup != null ) {
								ICFSecSecGrpIncObj selectedSecGrpInc = getIncludeList().getJavaFXFocusAsSecGrpInc();
								if( selectedSecGrpInc != null ) {
									linkedSecGroup = selectedSecGrpInc.getRequiredParentSubGroup();
									if( linkedSecGroup != null ) {
										ICFSecSecGrpIncEditObj editInstance = selectedSecGrpInc.beginEdit();
										if( editInstance != null ) {
											editInstance.deleteInstance();
											editInstance = null;
										}
									}
								}
							}
							setJavaFXFocusAsSecGroup( curSecGroup );
							if( linkedSecGroup != null ) {
								getIncludeList().setJavaFXFocusAsSecGrpInc( null );
								getGroupsPane().setJavaFXFocusAsSecGroup( linkedSecGroup );
							}
							else {
								getIncludeList().setJavaFXFocusAsSecGrpInc( null );
								getGroupsPane().setJavaFXFocusAsSecGroup( null );
							}
							adjustUserFeedback();
						}
						catch( Throwable t ) {
							CFConsole.formException( S_FormName, ((CFButton)e.getSource()).getText(), t );
						}
					}
				});
				vboxAddRemove.getChildren().add( buttonRemoveSubGroup );
			}
			return( vboxAddRemove );
		}

		public CFSecCustSecGrpIncListPane getIncludeList() {
			if( includeList == null ) {
				includeList = new CFSecCustSecGrpIncListPane(
						getCFFormManager(),
						javafxSchema,
						getJavaFXFocusAsSecGroup(),
						null,
						getJavaFXFocusAsSecGroup().getOptionalComponentsInclude(),
						javafxRefreshCallback,
						false );
			}
			return( includeList );
		}

		public CFSecCustSecGroupGroupsPane getGroupsPane() {
			if( groupsPane == null ) {
				groupsPane = new CFSecCustSecGroupGroupsPane( getCFFormManager(),
						javafxSchema,
						null );
			}
			return( groupsPane );
		}

		public void adjustUserFeedback() {
			ICFSecSecGroupObj selectedSecGroup = getGroupsPane().getJavaFXFocusAsSecGroup();
			if( selectedSecGroup != null ) {
				if( buttonAddSubGroup != null ) {
					buttonAddSubGroup.setDisable( false );
				}
				else {
					buttonAddSubGroup.setDisable( true );
				}
			}
			ICFSecSecGrpIncObj selectedGrpInc = getIncludeList().getJavaFXFocusAsSecGrpInc();
			if( selectedGrpInc != null ) {
				if( buttonRemoveSubGroup != null ) {
					buttonRemoveSubGroup.setDisable( false );
				}
				else {
					buttonRemoveSubGroup.setDisable( true );
				}
			}
		}
	}
}
