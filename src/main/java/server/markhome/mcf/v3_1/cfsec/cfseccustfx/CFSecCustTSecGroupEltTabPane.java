// Description: Java 13 JavaFX View/Edit Pane implementation for TSecGroup.

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
 *	CFSecJavaFXTSecGroupViewEditPane JavaFX View/Edit Pane implementation
 *	for TSecGroup.
 */
public class CFSecCustTSecGroupEltTabPane
extends CFTabPane
implements ICFSecJavaFXTSecGroupPaneCommon
{
	protected final String S_FormName = "Tenant Sec Group Elements";
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
	protected CFSecCustTSecGroupSubGroupPane tabViewSubGroupPane = null;
	protected CFSecCustTSecGrpMembPane tabViewMemberPane = null;

	public CFSecCustTSecGroupEltTabPane( ICFFormManager formManager, ICFSecJavaFXSchema argSchema, ICFSecTSecGroupObj argFocus ) {
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
		setJavaFXFocusAsTSecGroup( argFocus );
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
		if( ( value == null ) || ( value instanceof ICFSecTSecGroupObj ) ) {
			super.setJavaFXFocus( value );
			getTabViewMemberPane().setJavaFXFocus( value );
			getTabViewSubGroupPane().setJavaFXFocus( value );
		}
		else {
			throw new CFLibUnsupportedClassException( getClass(),
				S_ProcName,
				"value",
				value,
				"ICFSecTSecGroupObj" );
		}
	}

	public void setJavaFXFocusAsTSecGroup( ICFSecTSecGroupObj value ) {
		setJavaFXFocus( value );
	}

	public ICFSecTSecGroupObj getJavaFXFocusAsTSecGroup() {
		return( (ICFSecTSecGroupObj)getJavaFXFocus() );
	}

	protected class RefreshSubGroup
	implements ICFRefreshCallback
	{
		public RefreshSubGroup() {
		}

		public void refreshMe() {
			Collection<ICFSecTSecGrpIncObj> dataCollection;
			ICFSecTSecGroupObj focus = (ICFSecTSecGroupObj)getJavaFXFocusAsTSecGroup();
			if( focus != null ) {
				dataCollection = focus.getOptionalComponentsInclude( javafxIsInitializing );
			}
			else {
				dataCollection = null;
			}
			CFBorderPane pane = getTabViewSubGroupPane();
			ICFSecJavaFXTSecGrpIncPaneList jpList = (ICFSecJavaFXTSecGrpIncPaneList)pane;
			jpList.setJavaFXDataCollection( dataCollection );
		}
	}

	public CFBorderPane getTabViewSubGroupPane() {
		if( tabViewSubGroupPane == null ) {
			Collection<ICFSecTSecGrpIncObj> dataCollection;
			ICFSecTSecGroupObj focus = (ICFSecTSecGroupObj)getJavaFXFocusAsTSecGroup();
			if( focus != null ) {
				dataCollection = focus.getOptionalComponentsInclude( javafxIsInitializing );
			}
			else {
				dataCollection = null;
			}
			ICFSecTSecGroupObj javafxContainer;
			if( ( focus != null ) && ( focus instanceof ICFSecTSecGroupObj ) ) {
				javafxContainer = (ICFSecTSecGroupObj)focus;
			}
			else {
				javafxContainer = null;
			}
			tabViewSubGroupPane = new CFSecCustTSecGroupSubGroupPane( cfFormManager, javafxSchema, javafxContainer, null, dataCollection, new RefreshSubGroup(), false);
		}
		return( tabViewSubGroupPane );
	}

	protected class RefreshMember
	implements ICFRefreshCallback
	{
		public RefreshMember() {
		}

		public void refreshMe() {
			Collection<ICFSecTSecGrpMembObj> dataCollection;
			ICFSecTSecGroupObj focus = (ICFSecTSecGroupObj)getJavaFXFocusAsTSecGroup();
			if( focus != null ) {
				dataCollection = focus.getOptionalComponentsMember( javafxIsInitializing );
			}
			else {
				dataCollection = null;
			}
			CFBorderPane pane = getTabViewMemberPane();
			ICFSecJavaFXTSecGrpMembPaneList jpList = (ICFSecJavaFXTSecGrpMembPaneList)pane;
			jpList.setJavaFXDataCollection( dataCollection );
		}
	}

	public CFBorderPane getTabViewMemberPane() {
		if( tabViewMemberPane == null ) {
			Collection<ICFSecTSecGrpMembObj> dataCollection;
			ICFSecTSecGroupObj focus = (ICFSecTSecGroupObj)getJavaFXFocusAsTSecGroup();
			if( focus != null ) {
				dataCollection = focus.getOptionalComponentsMember( javafxIsInitializing );
			}
			else {
				dataCollection = null;
			}
			ICFSecTSecGroupObj javafxContainer;
			if( ( focus != null ) && ( focus instanceof ICFSecTSecGroupObj ) ) {
				javafxContainer = (ICFSecTSecGroupObj)focus;
			}
			else {
				javafxContainer = null;
			}
			tabViewMemberPane = new CFSecCustTSecGrpMembPane( cfFormManager, javafxSchema, javafxContainer, null, dataCollection, new RefreshMember(), false);
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

	public class CFSecCustTSecGrpMembPane
	extends CFBorderPane
	implements ICFSecJavaFXTSecGroupPaneCommon
	{
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected ICFRefreshCallback javafxRefreshCallback = null;
		protected CFSecCustTSecGrpMembListPane memberList = null;
		protected CFVBox vboxAddRemove = null;
		protected CFButton buttonAddMember = null;
		protected CFButton buttonRemoveMember = null;
		protected CFSecCustSecUserListPane userList = null;

		public CFSecCustTSecGrpMembPane( ICFFormManager formManager,
			ICFSecJavaFXSchema argSchema,
			ICFSecTSecGroupObj argContainer,
			ICFSecTSecGrpMembObj argFocus,
			Collection<ICFSecTSecGrpMembObj> argDataCollection,
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
			else if( value instanceof ICFSecTSecGroupObj ) {
				super.setJavaFXFocus( value );
				ICFSecTSecGroupObj tsecGroup = (ICFSecTSecGroupObj)value;
				Collection<ICFSecTSecGrpMembObj> members = tsecGroup.getOptionalComponentsMember();
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
					"ICFSecTSecGroupObj" );
			}
			adjustUserFeedback();
		}

		public ICFSecTSecGroupObj getJavaFXFocusAsTSecGroup() {
			return( (ICFSecTSecGroupObj)getJavaFXFocus() );
		}

		public void setJavaFXFocusAsTSecGroup( ICFSecTSecGroupObj value ) {
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
							ICFSecTSecGrpMembObj obj = null;
							ICFSecTSecGroupObj curTSecGroup = getJavaFXFocusAsTSecGroup();
							if( curTSecGroup != null ) {
								ICFSecSecUserObj selectedSecUser = getUserList().getJavaFXFocusAsSecUser();
								if( selectedSecUser != null ) {
									obj = schemaObj.getTSecGrpMembTableObj().newInstance();
									ICFSecTSecGrpMembEditObj editObj = obj.beginEdit();
									editObj.setRequiredContainerGroup( curTSecGroup );
									editObj.setRequiredParentUser( selectedSecUser );
									obj = editObj.create();
									editObj = null;
								}
							}
							setJavaFXFocusAsTSecGroup( curTSecGroup );
							if( obj != null ) {
								getMemberList().setJavaFXFocusAsTSecGrpMemb( obj );
								getUserList().setJavaFXFocusAsSecUser( null );
							}
							else {
								getMemberList().setJavaFXFocusAsTSecGrpMemb( null );
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
							ICFSecTSecGroupObj curTSecGroup = getJavaFXFocusAsTSecGroup();
							ICFSecTSecGrpMembObj selectedInstance = getMemberList().getJavaFXFocusAsTSecGrpMemb();
							if( selectedInstance != null ) {
								selectedSecUser = selectedInstance.getRequiredParentUser();
								ICFSecTSecGrpMembEditObj editInstance = selectedInstance.beginEdit();
								if( editInstance != null ) {
									editInstance.deleteInstance();
									editInstance = null;
								}
							}
							setJavaFXFocusAsTSecGroup( curTSecGroup );
							if( selectedSecUser != null ) {
								getMemberList().setJavaFXFocusAsTSecGrpMemb( null );
								getUserList().setJavaFXFocusAsSecUser( selectedSecUser );
							}
							else {
								getMemberList().setJavaFXFocusAsTSecGrpMemb( null );
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

		public CFSecCustTSecGrpMembListPane getMemberList() {
			if( memberList == null ) {
				ICFSecTSecGroupObj tsecGroup = getJavaFXFocusAsTSecGroup();
				if( tsecGroup != null ) {
					memberList = new CFSecCustTSecGrpMembListPane( getCFFormManager(),
						javafxSchema,
						tsecGroup,
						null,
						tsecGroup.getOptionalComponentsMember(),
						javafxRefreshCallback,
						false );
				}
				else {
					memberList = new CFSecCustTSecGrpMembListPane( getCFFormManager(),
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
						getJavaFXFocusAsTSecGroup(),
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
			ICFSecTSecGrpMembObj selectedGrpMemb = getMemberList().getJavaFXFocusAsTSecGrpMemb();
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

	public class CFSecCustTSecGrpMembListPane
	extends CFBorderPane
	implements ICFSecJavaFXTSecGrpMembPaneList
	{
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected Collection<ICFSecTSecGrpMembObj> javafxDataCollection = null;
		protected ObservableList<ICFSecTSecGrpMembObj> observableListOfTSecGrpMemb = null;
		protected TableView<ICFSecTSecGrpMembObj> dataTable = null;
		protected ScrollPane dataScrollPane = null;
		protected TableColumn<ICFSecTSecGrpMembObj, CFLibDbKeyHash256> tableColumnTSecGrpMembId = null;
		protected TableColumn<ICFSecTSecGrpMembObj, ICFSecSecUserObj> tableColumnParentUser = null;
		protected HashMap<CFLibDbKeyHash256,ICFSecSecUserObj> containsSecUser = null;

		public final String S_ColumnNames[] = { "Name" };
		protected ICFFormManager cfFormManager = null;
		protected boolean javafxIsInitializing = true;
		protected boolean javafxSortByChain = false;
		protected ICFSecTSecGroupObj javafxContainer = null;
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
						if( cont instanceof CFSecJavaFXTSecGrpMembListPane ) {
							CFSecJavaFXTSecGrpMembListPane form = (CFSecJavaFXTSecGrpMembListPane)cont;
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

		public CFSecCustTSecGrpMembListPane( ICFFormManager formManager,
			ICFSecJavaFXSchema argSchema,
			ICFSecTSecGroupObj argContainer,
			ICFSecTSecGrpMembObj argFocus,
			Collection<ICFSecTSecGrpMembObj> argDataCollection,
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
			dataTable = new TableView<ICFSecTSecGrpMembObj>();
			tableColumnTSecGrpMembId = new TableColumn<ICFSecTSecGrpMembObj,CFLibDbKeyHash256>( "Sec Group Member Id" );
			tableColumnTSecGrpMembId.setCellValueFactory( new Callback<CellDataFeatures<ICFSecTSecGrpMembObj,CFLibDbKeyHash256>,ObservableValue<CFLibDbKeyHash256> >() {
				public ObservableValue<CFLibDbKeyHash256> call( CellDataFeatures<ICFSecTSecGrpMembObj, CFLibDbKeyHash256> p ) {
					ICFSecTSecGrpMembObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						CFLibDbKeyHash256 value = obj.getRequiredTSecGrpMembId();
						ReadOnlyObjectWrapper<CFLibDbKeyHash256> observable = new ReadOnlyObjectWrapper<CFLibDbKeyHash256>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnTSecGrpMembId.setCellFactory( new Callback<TableColumn<ICFSecTSecGrpMembObj,CFLibDbKeyHash256>,TableCell<ICFSecTSecGrpMembObj,CFLibDbKeyHash256>>() {
				@Override public TableCell<ICFSecTSecGrpMembObj,CFLibDbKeyHash256> call(
					TableColumn<ICFSecTSecGrpMembObj,CFLibDbKeyHash256> arg)
				{
					return new CFDbKeyHash256TableCell<ICFSecTSecGrpMembObj>();
				}
			});
			dataTable.getColumns().add( tableColumnTSecGrpMembId );
			tableColumnParentUser = new TableColumn<ICFSecTSecGrpMembObj, ICFSecSecUserObj>( "User" );
			tableColumnParentUser.setCellValueFactory( new Callback<CellDataFeatures<ICFSecTSecGrpMembObj,ICFSecSecUserObj>,ObservableValue<ICFSecSecUserObj> >() {
				public ObservableValue<ICFSecSecUserObj> call( CellDataFeatures<ICFSecTSecGrpMembObj, ICFSecSecUserObj> p ) {
					ICFSecTSecGrpMembObj obj = p.getValue();
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
			tableColumnParentUser.setCellFactory( new Callback<TableColumn<ICFSecTSecGrpMembObj,ICFSecSecUserObj>,TableCell<ICFSecTSecGrpMembObj,ICFSecSecUserObj>>() {
				@Override public TableCell<ICFSecTSecGrpMembObj,ICFSecSecUserObj> call(
					TableColumn<ICFSecTSecGrpMembObj,ICFSecSecUserObj> arg)
				{
					return new CFReferenceTableCell<ICFSecTSecGrpMembObj,ICFSecSecUserObj>();
				}
			});
			dataTable.getColumns().add( tableColumnParentUser );
			dataTable.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<ICFSecTSecGrpMembObj>() {
					@Override public void changed( ObservableValue<? extends ICFSecTSecGrpMembObj> observable,
						ICFSecTSecGrpMembObj oldValue,
						ICFSecTSecGrpMembObj newValue )
					{
						setJavaFXFocus( newValue );
						adjustUserFeedback();
					}
				});
			dataScrollPane = new ScrollPane( dataTable );
			setCenter( dataScrollPane );
			javafxIsInitializing = false;
			if( observableListOfTSecGrpMemb != null ) {
				dataTable.setItems( observableListOfTSecGrpMemb );
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
			else if( value instanceof ICFSecTSecGrpMembObj ) {
				super.setJavaFXFocus( value );
				ICFSecTSecGrpMembObj obj = (ICFSecTSecGrpMembObj)value;
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
					"ICFSecTSecGrpMembObj" );
			}
			adjustUserFeedback();
		}

		public ICFSecTSecGrpMembObj getJavaFXFocusAsTSecGrpMemb() {
			return( (ICFSecTSecGrpMembObj)getJavaFXFocus() );
		}

		public void setJavaFXFocusAsTSecGrpMemb( ICFSecTSecGrpMembObj value ) {
			setJavaFXFocus( value );
		}

		public class TSecGrpMembByQualNameComparator
		implements Comparator<ICFSecTSecGrpMembObj>
		{
			public TSecGrpMembByQualNameComparator() {
			}

			public int compare( ICFSecTSecGrpMembObj lhs, ICFSecTSecGrpMembObj rhs ) {
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

		protected TSecGrpMembByQualNameComparator compareTSecGrpMembByQualName = new TSecGrpMembByQualNameComparator();

		public Collection<ICFSecTSecGrpMembObj> getJavaFXDataCollection() {
			return( javafxDataCollection );
		}

		public void setJavaFXDataCollection( Collection<ICFSecTSecGrpMembObj> value ) {
			final String S_ProcName = "setJavaFXDataCollection";
			ICFSecTSecGrpMembObj curMember;
			ICFSecSecUserObj curSecUser;
			containsSecUser = new HashMap<CFLibDbKeyHash256,ICFSecSecUserObj>();
			javafxDataCollection = value;
			observableListOfTSecGrpMemb = FXCollections.observableArrayList();
			if( javafxDataCollection != null ) {
					Iterator<ICFSecTSecGrpMembObj> iter = javafxDataCollection.iterator();
					while( iter.hasNext() ) {
						curMember = iter.next();
						if( curMember != null ) {
							observableListOfTSecGrpMemb.add( curMember );
							curSecUser = curMember.getRequiredParentUser();
							if( curSecUser != null ) {
								containsSecUser.put( curSecUser.getPKey(), curSecUser );
							}
						}
					}
					observableListOfTSecGrpMemb.sort( compareTSecGrpMembByQualName );
			}
			if( dataTable != null ) {
				dataTable.setItems( observableListOfTSecGrpMemb );
				// Hack from stackoverflow to fix JavaFX TableView refresh issue
				((TableColumn)dataTable.getColumns().get(0)).setVisible( false );
				((TableColumn)dataTable.getColumns().get(0)).setVisible( true );
			}
		}

		public ICFSecTSecGroupObj getJavaFXContainer() {
			return( javafxContainer );
		}

		public void setJavaFXContainer( ICFSecTSecGroupObj value ) {
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
			while( ( parent != null ) && ( ! ( parent instanceof CFSecCustTSecGrpMembListPane ) ) ) {
				parent = parent.getParent();
			}
			if( parent != null ) {
				CFSecCustTSecGrpMembListPane win = (CFSecCustTSecGrpMembListPane)parent;
				win.adjustUserFeedback();
			}
		}
	}

	/**
	 *	CFSecCustTSecGrpIncListPane JavaFX List of Obj Pane implementation
	 *	for TSecGrpInc.
	 */
	public class CFSecCustTSecGrpIncListPane
	extends CFBorderPane
	implements ICFSecJavaFXTSecGrpIncPaneList
	{
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected Collection<ICFSecTSecGrpIncObj> javafxDataCollection = null;
		protected ObservableList<ICFSecTSecGrpIncObj> observableListOfTSecGrpInc = null;
		protected TableView<ICFSecTSecGrpIncObj> dataTable = null;
		protected ScrollPane dataScrollPane = null;
		protected TableColumn<ICFSecTSecGrpIncObj, CFLibDbKeyHash256> tableColumnTSecGrpIncId = null;
		protected TableColumn<ICFSecTSecGrpIncObj, ICFSecTSecGroupObj> tableColumnParentSubGroup = null;
		protected HashMap<CFLibDbKeyHash256,ICFSecTSecGroupObj> containsTSecGroup = null;

		public final String S_ColumnNames[] = { "Name" };
		protected ICFFormManager cfFormManager = null;
		protected boolean javafxIsInitializing = true;
		protected boolean javafxSortByChain = false;
		protected ICFSecTSecGroupObj javafxContainer = null;
		protected ICFRefreshCallback javafxRefreshCallback = null;

		public CFSecCustTSecGrpIncListPane( ICFFormManager formManager,
			ICFSecJavaFXSchema argSchema,
			ICFSecTSecGroupObj argContainer,
			ICFSecTSecGrpIncObj argFocus,
			Collection<ICFSecTSecGrpIncObj> argDataCollection,
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
			dataTable = new TableView<ICFSecTSecGrpIncObj>();
			tableColumnTSecGrpIncId = new TableColumn<ICFSecTSecGrpIncObj,CFLibDbKeyHash256>( "Sec Group Include Id" );
			tableColumnTSecGrpIncId.setCellValueFactory( new Callback<CellDataFeatures<ICFSecTSecGrpIncObj,CFLibDbKeyHash256>,ObservableValue<CFLibDbKeyHash256> >() {
				public ObservableValue<CFLibDbKeyHash256> call( CellDataFeatures<ICFSecTSecGrpIncObj, CFLibDbKeyHash256> p ) {
					ICFSecTSecGrpIncObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						CFLibDbKeyHash256 value = obj.getRequiredTSecGrpIncId();
						ReadOnlyObjectWrapper<CFLibDbKeyHash256> observable = new ReadOnlyObjectWrapper<CFLibDbKeyHash256>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnTSecGrpIncId.setCellFactory( new Callback<TableColumn<ICFSecTSecGrpIncObj,CFLibDbKeyHash256>,TableCell<ICFSecTSecGrpIncObj,CFLibDbKeyHash256>>() {
				@Override public TableCell<ICFSecTSecGrpIncObj,CFLibDbKeyHash256> call(
					TableColumn<ICFSecTSecGrpIncObj,CFLibDbKeyHash256> arg)
				{
					return new CFDbKeyHash256TableCell<ICFSecTSecGrpIncObj>();
				}
			});
			dataTable.getColumns().add( tableColumnTSecGrpIncId );
			tableColumnParentSubGroup = new TableColumn<ICFSecTSecGrpIncObj, ICFSecTSecGroupObj>( "SubGroup" );
			tableColumnParentSubGroup.setCellValueFactory( new Callback<CellDataFeatures<ICFSecTSecGrpIncObj,ICFSecTSecGroupObj>,ObservableValue<ICFSecTSecGroupObj> >() {
				public ObservableValue<ICFSecTSecGroupObj> call( CellDataFeatures<ICFSecTSecGrpIncObj, ICFSecTSecGroupObj> p ) {
					ICFSecTSecGrpIncObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						ICFSecTSecGroupObj ref = obj.getRequiredParentSubGroup();
						ReadOnlyObjectWrapper<ICFSecTSecGroupObj> observable = new ReadOnlyObjectWrapper<ICFSecTSecGroupObj>();
						observable.setValue( ref );
						return( observable );
					}
				}
			});
			tableColumnParentSubGroup.setCellFactory( new Callback<TableColumn<ICFSecTSecGrpIncObj,ICFSecTSecGroupObj>,TableCell<ICFSecTSecGrpIncObj,ICFSecTSecGroupObj>>() {
				@Override public TableCell<ICFSecTSecGrpIncObj,ICFSecTSecGroupObj> call(
					TableColumn<ICFSecTSecGrpIncObj,ICFSecTSecGroupObj> arg)
				{
					return new CFReferenceTableCell<ICFSecTSecGrpIncObj,ICFSecTSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnParentSubGroup );
			dataTable.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<ICFSecTSecGrpIncObj>() {
					@Override public void changed( ObservableValue<? extends ICFSecTSecGrpIncObj> observable,
						ICFSecTSecGrpIncObj oldValue,
						ICFSecTSecGrpIncObj newValue )
					{
						setJavaFXFocus( newValue );
					}
				});
			dataScrollPane = new ScrollPane( dataTable );
			setCenter( dataScrollPane );
			javafxIsInitializing = false;
			if( observableListOfTSecGrpInc != null ) {
				dataTable.setItems( observableListOfTSecGrpInc );
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
			else if( value instanceof ICFSecTSecGrpIncObj ) {
				super.setJavaFXFocus( value );
				ICFSecTSecGrpIncObj obj = (ICFSecTSecGrpIncObj)value;
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
					"ICFSecTSecGrpMembObj" );
			}
		}

		public ICFSecTSecGrpIncObj getJavaFXFocusAsTSecGrpInc() {
			return( (ICFSecTSecGrpIncObj)getJavaFXFocus() );
		}

		public void setJavaFXFocusAsTSecGrpInc( ICFSecTSecGrpIncObj value ) {
			setJavaFXFocus( value );
		}

		public class TSecGrpIncByQualNameComparator
		implements Comparator<ICFSecTSecGrpIncObj>
		{
			public TSecGrpIncByQualNameComparator() {
			}

			public int compare( ICFSecTSecGrpIncObj lhs, ICFSecTSecGrpIncObj rhs ) {
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

		protected TSecGrpIncByQualNameComparator compareTSecGrpIncByQualName = new TSecGrpIncByQualNameComparator();

		public Collection<ICFSecTSecGrpIncObj> getJavaFXDataCollection() {
			return( javafxDataCollection );
		}

		public void setJavaFXDataCollection( Collection<ICFSecTSecGrpIncObj> value ) {
			javafxDataCollection = value;
			ICFSecTSecGroupObj tsecGroup;
			ICFSecTSecGrpIncObj obj;
			observableListOfTSecGrpInc = FXCollections.observableArrayList();
			containsTSecGroup = new HashMap<CFLibDbKeyHash256,ICFSecTSecGroupObj>();
			if( javafxDataCollection != null ) {
					Iterator<ICFSecTSecGrpIncObj> iter = javafxDataCollection.iterator();
					while( iter.hasNext() ) {
						obj = iter.next();
						observableListOfTSecGrpInc.add( obj );
						tsecGroup = obj.getRequiredParentSubGroup();
						if( tsecGroup != null ) {
							containsTSecGroup.put( tsecGroup.getPKey(), tsecGroup );
						}
					}
					observableListOfTSecGrpInc.sort( compareTSecGrpIncByQualName );
			}
			if( dataTable != null ) {
				dataTable.setItems( observableListOfTSecGrpInc );
				// Hack from stackoverflow to fix JavaFX TableView refresh issue
				((TableColumn)dataTable.getColumns().get(0)).setVisible( false );
				((TableColumn)dataTable.getColumns().get(0)).setVisible( true );
			}
		}

		public ICFSecTSecGroupObj getJavaFXContainer() {
			return( javafxContainer );
		}

		public void setJavaFXContainer( ICFSecTSecGroupObj value ) {
			javafxContainer = value;
		}

		public void refreshMe() {
			if( javafxRefreshCallback != null ) {
				javafxRefreshCallback.refreshMe();
			}
		}

		public void adjustUserFeedback() {
			Node parent = getParent();
			while( ( parent != null ) && ( ! ( parent instanceof CFSecCustTSecGrpMembListPane ) ) ) {
				parent = parent.getParent();
			}
			if( parent != null ) {
				CFSecCustTSecGrpMembListPane win = (CFSecCustTSecGrpMembListPane)parent;
				win.adjustUserFeedback();
			}
		}

		public boolean includesTSecGroup( ICFSecTSecGroupObj tsecGroup ) {
			if( tsecGroup == null ) {
				return( false );
			}
			if( containsTSecGroup == null ) {
				return( false );
			}
			return( containsTSecGroup.containsKey( tsecGroup.getPKey() ) );
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
			tableColumnPasswordResetUuid = new TableColumn<ICFSecSecUserObj,CFLibUuid6>( "Password Reset UUID6" );
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
					"ICFSecTSecGrpMembObj" );
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
				while( ( parent != null ) && ( ! ( parent instanceof CFSecCustTSecGrpMembListPane ) ) ) {
					parent = parent.getParent();
				}
				if( parent != null ) {
					CFSecCustTSecGrpMembListPane win = (CFSecCustTSecGrpMembListPane)parent;
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
			while( ( parent != null ) && ( ! ( parent instanceof CFSecCustTSecGrpMembListPane ) ) ) {
				parent = parent.getParent();
			}
			if( parent != null ) {
				CFSecCustTSecGrpMembListPane win = (CFSecCustTSecGrpMembListPane)parent;
				win.adjustUserFeedback();
			}
		}
	}

	/**
	 *	CFSecCustTSecGroupListAllPane JavaFX List of Obj Pane implementation
	 *	for TSecGroup.
	 */
	public class CFSecCustTSecGroupListAllPane
	extends CFBorderPane
	implements ICFSecJavaFXTSecGroupPaneList
	{
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected Collection<ICFSecTSecGroupObj> javafxDataCollection = null;
		protected ObservableList<ICFSecTSecGroupObj> observableListOfTSecGroup = null;
		protected TableView<ICFSecTSecGroupObj> dataTable = null;
		protected ScrollPane dataScrollPane = null;
		protected TableColumn<ICFSecTSecGroupObj, CFLibDbKeyHash256> tableColumnTSecGroupId = null;
		protected TableColumn<ICFSecTSecGroupObj, String> tableColumnName = null;
		protected TableColumn<ICFSecTSecGroupObj, Boolean> tableColumnIsVisible = null;

		public final String S_ColumnNames[] = { "Name" };
		protected ICFFormManager cfFormManager = null;
		protected boolean javafxIsInitializing = true;
		protected boolean javafxSortByChain = false;
		protected ICFSecTenantObj javafxContainer = null;
		protected ICFRefreshCallback javafxRefreshCallback = null;

		public CFSecCustTSecGroupListAllPane( ICFFormManager formManager,
			ICFSecJavaFXSchema argSchema,
			ICFSecTenantObj argContainer,
			ICFSecTSecGroupObj argFocus,
			Collection<ICFSecTSecGroupObj> argDataCollection,
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
			dataTable = new TableView<ICFSecTSecGroupObj>();
			tableColumnTSecGroupId = new TableColumn<ICFSecTSecGroupObj,CFLibDbKeyHash256>( "Sec Group Id" );
			tableColumnTSecGroupId.setCellValueFactory( new Callback<CellDataFeatures<ICFSecTSecGroupObj,CFLibDbKeyHash256>,ObservableValue<CFLibDbKeyHash256> >() {
				public ObservableValue<CFLibDbKeyHash256> call( CellDataFeatures<ICFSecTSecGroupObj, CFLibDbKeyHash256> p ) {
					ICFSecTSecGroupObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						CFLibDbKeyHash256 value = obj.getRequiredTSecGroupId();
						ReadOnlyObjectWrapper<CFLibDbKeyHash256> observable = new ReadOnlyObjectWrapper<CFLibDbKeyHash256>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnTSecGroupId.setCellFactory( new Callback<TableColumn<ICFSecTSecGroupObj,CFLibDbKeyHash256>,TableCell<ICFSecTSecGroupObj,CFLibDbKeyHash256>>() {
				@Override public TableCell<ICFSecTSecGroupObj,CFLibDbKeyHash256> call(
					TableColumn<ICFSecTSecGroupObj,CFLibDbKeyHash256> arg)
				{
					return new CFDbKeyHash256TableCell<ICFSecTSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnTSecGroupId );
			tableColumnName = new TableColumn<ICFSecTSecGroupObj,String>( "Name" );
			tableColumnName.setCellValueFactory( new Callback<CellDataFeatures<ICFSecTSecGroupObj,String>,ObservableValue<String> >() {
				public ObservableValue<String> call( CellDataFeatures<ICFSecTSecGroupObj, String> p ) {
					ICFSecTSecGroupObj obj = p.getValue();
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
			tableColumnName.setCellFactory( new Callback<TableColumn<ICFSecTSecGroupObj,String>,TableCell<ICFSecTSecGroupObj,String>>() {
				@Override public TableCell<ICFSecTSecGroupObj,String> call(
					TableColumn<ICFSecTSecGroupObj,String> arg)
				{
					return new CFStringTableCell<ICFSecTSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnName );
			tableColumnIsVisible = new TableColumn<ICFSecTSecGroupObj,Boolean>( "IsVisible" );
			tableColumnIsVisible.setCellValueFactory( new Callback<CellDataFeatures<ICFSecTSecGroupObj,Boolean>,ObservableValue<Boolean> >() {
				public ObservableValue<Boolean> call( CellDataFeatures<ICFSecTSecGroupObj, Boolean> p ) {
					ICFSecTSecGroupObj obj = p.getValue();
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
			tableColumnIsVisible.setCellFactory( new Callback<TableColumn<ICFSecTSecGroupObj,Boolean>,TableCell<ICFSecTSecGroupObj,Boolean>>() {
				@Override public TableCell<ICFSecTSecGroupObj,Boolean> call(
					TableColumn<ICFSecTSecGroupObj,Boolean> arg)
				{
					return new CFBoolTableCell<ICFSecTSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnIsVisible );
			dataTable.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<ICFSecTSecGroupObj>() {
					@Override public void changed( ObservableValue<? extends ICFSecTSecGroupObj> observable,
						ICFSecTSecGroupObj oldValue,
						ICFSecTSecGroupObj newValue )
					{
						setJavaFXFocus( newValue );
					}
				});
			dataScrollPane = new ScrollPane( dataTable );
			setCenter( dataScrollPane );
			javafxIsInitializing = false;
			if( observableListOfTSecGroup != null ) {
				dataTable.setItems( observableListOfTSecGroup );
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
			else if( value instanceof ICFSecTSecGroupObj ) {
				super.setJavaFXFocus( value );
				ICFSecTSecGroupObj obj = (ICFSecTSecGroupObj)value;
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
					"ICFSecTSecGrpMembObj" );
			}
		}

		public ICFSecTSecGroupObj getJavaFXFocusAsTSecGroup() {
			return( (ICFSecTSecGroupObj)getJavaFXFocus() );
		}

		public void setJavaFXFocusAsTSecGroup( ICFSecTSecGroupObj value ) {
			setJavaFXFocus( value );
		}

		public class TSecGroupByQualNameComparator
		implements Comparator<ICFSecTSecGroupObj>
		{
			public TSecGroupByQualNameComparator() {
			}

			public int compare( ICFSecTSecGroupObj lhs, ICFSecTSecGroupObj rhs ) {
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

		protected TSecGroupByQualNameComparator compareTSecGroupByQualName = new TSecGroupByQualNameComparator();

		public Collection<ICFSecTSecGroupObj> getJavaFXDataCollection() {
			return( javafxDataCollection );
		}

		public void setJavaFXDataCollection( Collection<ICFSecTSecGroupObj> value ) {
			final String S_ProcName = "setJavaFXDataCollection";
			javafxDataCollection = value;
			observableListOfTSecGroup = FXCollections.observableArrayList();
			if( javafxDataCollection != null ) {
					Iterator<ICFSecTSecGroupObj> iter = javafxDataCollection.iterator();
					while( iter.hasNext() ) {
						observableListOfTSecGroup.add( iter.next() );
					}
					observableListOfTSecGroup.sort( compareTSecGroupByQualName );
			}
			if( dataTable != null ) {
				dataTable.setItems( observableListOfTSecGroup );
				// Hack from stackoverflow to fix JavaFX TableView refresh issue
				((TableColumn)dataTable.getColumns().get(0)).setVisible( false );
				((TableColumn)dataTable.getColumns().get(0)).setVisible( true );
			}
		}

		public ICFSecTenantObj getJavaFXContainer() {
			return( javafxContainer );
		}

		public void setJavaFXContainer( ICFSecTenantObj value ) {
			javafxContainer = value;
		}

		public void refreshMe() {
			if( javafxRefreshCallback != null ) {
				javafxRefreshCallback.refreshMe();
			}
		}

		public void adjustUserFeedback() {
			Node parent = getParent();
			while( ( parent != null ) && ( ! ( parent instanceof CFSecCustTSecGrpMembListPane ) ) ) {
				parent = parent.getParent();
			}
			if( parent != null ) {
				CFSecCustTSecGrpMembListPane win = (CFSecCustTSecGrpMembListPane)parent;
				win.adjustUserFeedback();
			}
		}
	}

	/**
	 *	CFSecCustTSecGroupListVisiblePane JavaFX List of Obj Pane implementation
	 *	for TSecGroup.
	 */
	public class CFSecCustTSecGroupListVisiblePane
	extends CFBorderPane
	implements ICFSecJavaFXTSecGroupPaneList
	{
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected Collection<ICFSecTSecGroupObj> javafxDataCollection = null;
		protected ObservableList<ICFSecTSecGroupObj> observableListOfTSecGroup = null;
		protected TableView<ICFSecTSecGroupObj> dataTable = null;
		protected ScrollPane dataScrollPane = null;
		protected TableColumn<ICFSecTSecGroupObj, CFLibDbKeyHash256> tableColumnTSecGroupId = null;
		protected TableColumn<ICFSecTSecGroupObj, String> tableColumnName = null;
		protected TableColumn<ICFSecTSecGroupObj, Boolean> tableColumnIsVisible = null;

		public final String S_ColumnNames[] = { "Name" };
		protected ICFFormManager cfFormManager = null;
		protected boolean javafxIsInitializing = true;
		protected boolean javafxSortByChain = false;
		protected ICFSecTenantObj javafxContainer = null;
		protected ICFRefreshCallback javafxRefreshCallback = null;

		public CFSecCustTSecGroupListVisiblePane( ICFFormManager formManager,
			ICFSecJavaFXSchema argSchema,
			ICFSecTenantObj argContainer,
			ICFSecTSecGroupObj argFocus,
			Collection<ICFSecTSecGroupObj> argDataCollection,
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
			dataTable = new TableView<ICFSecTSecGroupObj>();
			tableColumnTSecGroupId = new TableColumn<ICFSecTSecGroupObj,CFLibDbKeyHash256>( "Sec Group Id" );
			tableColumnTSecGroupId.setCellValueFactory( new Callback<CellDataFeatures<ICFSecTSecGroupObj,CFLibDbKeyHash256>,ObservableValue<CFLibDbKeyHash256> >() {
				public ObservableValue<CFLibDbKeyHash256> call( CellDataFeatures<ICFSecTSecGroupObj, CFLibDbKeyHash256> p ) {
					ICFSecTSecGroupObj obj = p.getValue();
					if( obj == null ) {
						return( null );
					}
					else {
						CFLibDbKeyHash256 value = obj.getRequiredTSecGroupId();
						ReadOnlyObjectWrapper<CFLibDbKeyHash256> observable = new ReadOnlyObjectWrapper<CFLibDbKeyHash256>();
						observable.setValue( value );
						return( observable );
					}
				}
			});
			tableColumnTSecGroupId.setCellFactory( new Callback<TableColumn<ICFSecTSecGroupObj,CFLibDbKeyHash256>,TableCell<ICFSecTSecGroupObj,CFLibDbKeyHash256>>() {
				@Override public TableCell<ICFSecTSecGroupObj,CFLibDbKeyHash256> call(
					TableColumn<ICFSecTSecGroupObj,CFLibDbKeyHash256> arg)
				{
					return new CFDbKeyHash256TableCell<ICFSecTSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnTSecGroupId );
			tableColumnName = new TableColumn<ICFSecTSecGroupObj,String>( "Name" );
			tableColumnName.setCellValueFactory( new Callback<CellDataFeatures<ICFSecTSecGroupObj,String>,ObservableValue<String> >() {
				public ObservableValue<String> call( CellDataFeatures<ICFSecTSecGroupObj, String> p ) {
					ICFSecTSecGroupObj obj = p.getValue();
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
			tableColumnName.setCellFactory( new Callback<TableColumn<ICFSecTSecGroupObj,String>,TableCell<ICFSecTSecGroupObj,String>>() {
				@Override public TableCell<ICFSecTSecGroupObj,String> call(
					TableColumn<ICFSecTSecGroupObj,String> arg)
				{
					return new CFStringTableCell<ICFSecTSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnName );
			tableColumnIsVisible = new TableColumn<ICFSecTSecGroupObj,Boolean>( "IsVisible" );
			tableColumnIsVisible.setCellValueFactory( new Callback<CellDataFeatures<ICFSecTSecGroupObj,Boolean>,ObservableValue<Boolean> >() {
				public ObservableValue<Boolean> call( CellDataFeatures<ICFSecTSecGroupObj, Boolean> p ) {
					ICFSecTSecGroupObj obj = p.getValue();
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
			tableColumnIsVisible.setCellFactory( new Callback<TableColumn<ICFSecTSecGroupObj,Boolean>,TableCell<ICFSecTSecGroupObj,Boolean>>() {
				@Override public TableCell<ICFSecTSecGroupObj,Boolean> call(
					TableColumn<ICFSecTSecGroupObj,Boolean> arg)
				{
					return new CFBoolTableCell<ICFSecTSecGroupObj>();
				}
			});
			dataTable.getColumns().add( tableColumnIsVisible );
			dataTable.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<ICFSecTSecGroupObj>() {
					@Override public void changed( ObservableValue<? extends ICFSecTSecGroupObj> observable,
						ICFSecTSecGroupObj oldValue,
						ICFSecTSecGroupObj newValue )
					{
						setJavaFXFocus( newValue );
					}
				});
			dataScrollPane = new ScrollPane( dataTable );
			setCenter( dataScrollPane );
			javafxIsInitializing = false;
			if( observableListOfTSecGroup != null ) {
				dataTable.setItems( observableListOfTSecGroup );
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
			else if( value instanceof ICFSecTSecGroupObj ) {
				super.setJavaFXFocus( value );
				ICFSecTSecGroupObj obj = (ICFSecTSecGroupObj)value;
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
					"ICFSecTSecGrpMembObj" );
			}
		}

		public ICFSecTSecGroupObj getJavaFXFocusAsTSecGroup() {
			return( (ICFSecTSecGroupObj)getJavaFXFocus() );
		}

		public void setJavaFXFocusAsTSecGroup( ICFSecTSecGroupObj value ) {
			setJavaFXFocus( value );
		}

		public class TSecGroupByQualNameComparator
		implements Comparator<ICFSecTSecGroupObj>
		{
			public TSecGroupByQualNameComparator() {
			}

			public int compare( ICFSecTSecGroupObj lhs, ICFSecTSecGroupObj rhs ) {
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
				while( ( parent != null ) && ( ! ( parent instanceof CFSecCustTSecGrpMembListPane ) ) ) {
					parent = parent.getParent();
				}
				if( parent != null ) {
					CFSecCustTSecGrpMembListPane win = (CFSecCustTSecGrpMembListPane)parent;
					win.adjustUserFeedback();
				}
			}
		}

		protected TSecGroupByQualNameComparator compareTSecGroupByQualName = new TSecGroupByQualNameComparator();

		public Collection<ICFSecTSecGroupObj> getJavaFXDataCollection() {
			return( javafxDataCollection );
		}

		public void setJavaFXDataCollection( Collection<ICFSecTSecGroupObj> value ) {
			final String S_ProcName = "setJavaFXDataCollection";
			javafxDataCollection = value;
			observableListOfTSecGroup = FXCollections.observableArrayList();
			if( javafxDataCollection != null ) {
					Iterator<ICFSecTSecGroupObj> iter = javafxDataCollection.iterator();
					while( iter.hasNext() ) {
						observableListOfTSecGroup.add( iter.next() );
					}
					observableListOfTSecGroup.sort( compareTSecGroupByQualName );
			}
			if( dataTable != null ) {
				dataTable.setItems( observableListOfTSecGroup );
				// Hack from stackoverflow to fix JavaFX TableView refresh issue
				((TableColumn)dataTable.getColumns().get(0)).setVisible( false );
				((TableColumn)dataTable.getColumns().get(0)).setVisible( true );
			}
		}

		public ICFSecTenantObj getJavaFXContainer() {
			return( javafxContainer );
		}

		public void setJavaFXContainer( ICFSecTenantObj value ) {
			javafxContainer = value;
		}

		public void refreshMe() {
			if( javafxRefreshCallback != null ) {
				javafxRefreshCallback.refreshMe();
			}
		}
	}

	/**
	 *	CFSecJavaFXTSecGroupGroupsPane JavaFX View/Edit Pane implementation
	 *	for TSecGroup.
	 */
	public class CFSecCustTSecGroupGroupsPane
	extends CFTabPane
	implements ICFSecJavaFXTSecGroupPaneList
	{
		protected ICFFormManager cfFormManager = null;
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected boolean javafxIsInitializing = true;
		protected ICFSecTenantObj javafxContainer = null;
		protected Collection<ICFSecTSecGroupObj> javafxCollection = null;

		public final String LABEL_TabVisibleGroups = "Visible Groups";
		protected CFTab tabVisibleGroups = null;
		protected CFSecCustTSecGroupListVisiblePane tabViewVisibleGroupsPane = null;
		public final String LABEL_TabAllGroups = "All Groups";
		protected CFTab tabAllGroups = null;
		protected CFSecCustTSecGroupListAllPane tabViewAllGroupsPane = null;

		public CFSecCustTSecGroupGroupsPane( ICFFormManager formManager, ICFSecJavaFXSchema argSchema, ICFSecTSecGroupObj argFocus ) {
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
			setJavaFXFocusAsTSecGroup( argFocus );
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
							retval = getTabViewVisibleGroupsPane().getJavaFXFocusAsTSecGroup();
						}
						else if( content == getTabViewAllGroupsPane() ) {
							retval = getTabViewAllGroupsPane().getJavaFXFocusAsTSecGroup();
						}
						else {
							throw new CFLibRuntimeException( getClass(),
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
			else if( value instanceof ICFSecTSecGroupObj ) {
				super.setJavaFXFocus( value );
				ICFSecTSecGroupObj tsecGroup = (ICFSecTSecGroupObj)value;
				if( tabViewVisibleGroupsPane != null ) {
					tabViewVisibleGroupsPane.setJavaFXFocusAsTSecGroup( tsecGroup );
				}
				if( tabViewAllGroupsPane != null ) {
					tabViewAllGroupsPane.setJavaFXFocusAsTSecGroup( tsecGroup );
				}
			}
			else {
				throw new CFLibUnsupportedClassException( getClass(),
					S_ProcName,
					"value",
					value,
					"ICFSecTSecGroupObj" );
			}
		}

		public void setJavaFXFocusAsTSecGroup( ICFSecTSecGroupObj value ) {
			setJavaFXFocus( value );
		}

		public ICFSecTSecGroupObj getJavaFXFocusAsTSecGroup() {
			return( (ICFSecTSecGroupObj)getJavaFXFocus() );
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

		public CFSecCustTSecGroupListVisiblePane getTabViewVisibleGroupsPane() {
			if( tabViewVisibleGroupsPane == null ) {
				tabViewVisibleGroupsPane = new CFSecCustTSecGroupListVisiblePane( getCFFormManager(),
					getJavaFXSchema(),
					null,
					null,
					getJavaFXSchema().getSchema().getTSecGroupTableObj().readTSecGroupByTenantIdx( getJavaFXSchema().getSchema().getAuthorization().getSecTenantId(), javafxIsInitializing ),
					null,
					false );
				tabViewVisibleGroupsPane.setPaneMode( CFPane.PaneMode.View );
			}
			return( tabViewVisibleGroupsPane );
		}

		public CFSecCustTSecGroupListAllPane getTabViewAllGroupsPane() {
			if( tabViewAllGroupsPane == null ) {
				tabViewAllGroupsPane = new CFSecCustTSecGroupListAllPane( getCFFormManager(),
					getJavaFXSchema(),
					null,
					null,
					getJavaFXSchema().getSchema().getTSecGroupTableObj().readTSecGroupByTenantIdx( getJavaFXSchema().getSchema().getAuthorization().getSecTenantId(), javafxIsInitializing ),
					null,
					false );
				tabViewAllGroupsPane.setPaneMode( CFPane.PaneMode.View );
			}
			return( tabViewAllGroupsPane );
		}

		@Override public void setJavaFXDataCollection( Collection<ICFSecTSecGroupObj> value ) {
			final String S_ProcName = "setJavaFXDataCollection";
			if( tabViewVisibleGroupsPane != null ) {
				LinkedList<ICFSecTSecGroupObj> visible = new LinkedList<ICFSecTSecGroupObj>();
				ICFSecTSecGroupObj obj;
				Iterator<ICFSecTSecGroupObj> iter = value.iterator();
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

		@Override public ICFSecTenantObj getJavaFXContainer() {
			return( javafxContainer );
		}

		@Override public void setJavaFXContainer(ICFSecTenantObj value) {
			javafxContainer = value;
		}

		@Override public Collection<ICFSecTSecGroupObj> getJavaFXDataCollection() {
			return( javafxCollection );
		}
	}

	public class CFSecCustTSecGroupSubGroupPane
	extends CFBorderPane
	implements ICFSecJavaFXTSecGrpIncPaneCommon
	{
		protected ICFSecJavaFXSchema javafxSchema = null;
		protected ICFRefreshCallback javafxRefreshCallback = null;
		protected CFSecCustTSecGrpIncListPane includeList = null;
		protected CFVBox vboxAddRemove = null;
		protected CFButton buttonAddSubGroup = null;
		protected CFButton buttonRemoveSubGroup = null;
		protected CFSecCustTSecGroupGroupsPane groupsPane = null;

		public CFSecCustTSecGroupSubGroupPane( ICFFormManager formManager,
			ICFSecJavaFXSchema argSchema,
			ICFSecTSecGroupObj argContainer,
			ICFSecTSecGrpIncObj argFocus,
			Collection<ICFSecTSecGrpIncObj> argDataCollection,
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
			else if( value instanceof ICFSecTSecGroupObj ) {
				super.setJavaFXFocus( value );
				ICFSecTSecGroupObj tsecGroup = (ICFSecTSecGroupObj)value;
				Collection<ICFSecTSecGrpIncObj> includes = tsecGroup.getOptionalComponentsInclude();
				getIncludeList().setJavaFXDataCollection( includes );
				ICFSecTSecGroupObj curTSecGroup;
				Collection<ICFSecTSecGroupObj> allTSecGroup = javafxSchema.getSchema().getTSecGroupTableObj().readAllTSecGroup( javafxIsInitializing );
				LinkedList<ICFSecTSecGroupObj> listOfUnusedTSecGroup = new LinkedList<ICFSecTSecGroupObj>();
				Iterator<ICFSecTSecGroupObj> iterTSecGroup = allTSecGroup.iterator();
				while( iterTSecGroup.hasNext() ) {
					curTSecGroup = iterTSecGroup.next();
					if( curTSecGroup != null ) {
						if( ! includeList.includesTSecGroup( curTSecGroup ) ) {
							listOfUnusedTSecGroup.add( curTSecGroup );
						}
					}
				}
				getGroupsPane().setJavaFXDataCollection( listOfUnusedTSecGroup );
			}
			else {
				throw new CFLibUnsupportedClassException( getClass(),
					S_ProcName,
					"value",
					value,
					"ICFSecTSecGroupObj" );
			}
			adjustUserFeedback();
		}

		public ICFSecTSecGrpIncObj getJavaFXFocusAsTSecGrpInc() {
			return( (ICFSecTSecGrpIncObj)getJavaFXFocus() );
		}

		public void setJavaFXFocusAsTSecGrpInc( ICFSecTSecGrpIncObj value ) {
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
							ICFSecTSecGrpIncObj obj = null;
							ICFSecTSecGroupObj curTSecGroup = getJavaFXFocusAsTSecGroup();
							if( curTSecGroup != null ) {
								ICFSecTSecGroupObj selectedTSecGroup = getGroupsPane().getJavaFXFocusAsTSecGroup();
								if( selectedTSecGroup != null ) {
									obj = schemaObj.getTSecGrpIncTableObj().newInstance();
									ICFSecTSecGrpIncEditObj editObj = obj.beginEdit();
									editObj.setRequiredContainerGroup( curTSecGroup );
									editObj.setRequiredParentSubGroup( selectedTSecGroup );
									obj = editObj.create();
									editObj = null;
								}
							}
							setJavaFXFocusAsTSecGroup( curTSecGroup );
							if( obj != null ) {
								getIncludeList().setJavaFXFocusAsTSecGrpInc( obj );
								getGroupsPane().setJavaFXFocusAsTSecGroup( null );
							}
							else {
								getIncludeList().setJavaFXFocusAsTSecGrpInc( null );
								getGroupsPane().setJavaFXFocusAsTSecGroup( null );
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
							ICFSecTSecGroupObj linkedTSecGroup = null;
							ICFSecTSecGroupObj curTSecGroup = getJavaFXFocusAsTSecGroup();
							if( curTSecGroup != null ) {
								ICFSecTSecGrpIncObj selectedTSecGrpInc = getIncludeList().getJavaFXFocusAsTSecGrpInc();
								if( selectedTSecGrpInc != null ) {
									linkedTSecGroup = selectedTSecGrpInc.getRequiredParentSubGroup();
									if( linkedTSecGroup != null ) {
										ICFSecTSecGrpIncEditObj editInstance = selectedTSecGrpInc.beginEdit();
										if( editInstance != null ) {
											editInstance.deleteInstance();
											editInstance = null;
										}
									}
								}
							}
							setJavaFXFocusAsTSecGroup( curTSecGroup );
							if( linkedTSecGroup != null ) {
								getIncludeList().setJavaFXFocusAsTSecGrpInc( null );
								getGroupsPane().setJavaFXFocusAsTSecGroup( linkedTSecGroup );
							}
							else {
								getIncludeList().setJavaFXFocusAsTSecGrpInc( null );
								getGroupsPane().setJavaFXFocusAsTSecGroup( null );
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

		public CFSecCustTSecGrpIncListPane getIncludeList() {
			if( includeList == null ) {
				includeList = new CFSecCustTSecGrpIncListPane(
						getCFFormManager(),
						javafxSchema,
						getJavaFXFocusAsTSecGroup(),
						null,
						getJavaFXFocusAsTSecGroup().getOptionalComponentsInclude(),
						javafxRefreshCallback,
						false );
			}
			return( includeList );
		}

		public CFSecCustTSecGroupGroupsPane getGroupsPane() {
			if( groupsPane == null ) {
				groupsPane = new CFSecCustTSecGroupGroupsPane( getCFFormManager(),
						javafxSchema,
						null );
			}
			return( groupsPane );
		}

		public void adjustUserFeedback() {
			ICFSecTSecGroupObj selectedTSecGroup = getGroupsPane().getJavaFXFocusAsTSecGroup();
			if( selectedTSecGroup != null ) {
				if( buttonAddSubGroup != null ) {
					buttonAddSubGroup.setDisable( false );
				}
				else {
					buttonAddSubGroup.setDisable( true );
				}
			}
			ICFSecTSecGrpIncObj selectedGrpInc = getIncludeList().getJavaFXFocusAsTSecGrpInc();
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
