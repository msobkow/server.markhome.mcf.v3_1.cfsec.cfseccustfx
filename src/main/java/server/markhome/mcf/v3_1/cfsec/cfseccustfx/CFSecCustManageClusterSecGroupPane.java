// Description: Java 13 JavaFX Finder Pane implementation for SecGroup.

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
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import server.markhome.mcf.v3_1.cflib.*;
import server.markhome.mcf.v3_1.cflib.dbutil.*;
import server.markhome.mcf.v3_1.cflib.xml.*;
import server.markhome.mcf.v3_1.cflib.javafx.*;
import server.markhome.mcf.v3_1.cfsec.cfsec.*;
import server.markhome.mcf.v3_1.cfsec.cfsecobj.*;
import server.markhome.mcf.v3_1.cfsec.cfsecjavafx.*;
import server.markhome.mcf.v3_1.cfsec.cfseccustfx.CFSecCustManageTenantTSecGroupPane.CFSecCustTenantTSecGroupTabPaneEnum;

/**
 *	CFSecJavaFXSecGroupFinderPane JavaFX Finder Pane implementation
 *	for SecGroup.
 */
public class CFSecCustManageClusterSecGroupPane
extends CFBorderPane
implements ICFRefreshCallback
{
	protected ICFFormManager cfFormManager = null;
	protected ICFSecJavaFXSchema javafxSchema = null;
	protected ICFSecCustSchema custSchema = null;

	protected boolean javafxIsInitializing = true;
	protected ICFSecClusterObj containingCluster = null;
	protected List<ICFSecSecGroupObj> listOfSecGroup = null;
	protected List<ICFSecSecGroupObj> listOfSecGroupFilterByIsVisible = null;
	protected List<ICFSecSecGroupObj> listOfSecGroupFilterBySecUser = null;
	protected ObservableList<ICFSecSecGroupObj> observableListOfSecGroup = null;
	protected ObservableList<ICFSecSecGroupObj> observableListOfSecGroupFilterByIsVisible = null;
	protected ObservableList<ICFSecSecGroupObj> observableListOfSecGroupFilterBySecUser = null;
	protected TableView<ICFSecSecGroupObj> dataTable = null;
	protected TableView<ICFSecSecGroupObj> dataTableFilterByIsVisible = null;
	protected TableView<ICFSecSecGroupObj> dataTableFilterBySecUser = null;
	protected ScrollPane dataScrollPane = null;
	protected ScrollPane dataScrollPaneFilterByIsVisible = null;
	protected ScrollPane dataScrollPaneFilterBySecUser = null;

	public static enum CFSecCustClusterSecGroupTabPaneEnum {
		IS_VISIBLE,
		SEC_USER,
		ALL };

	public class CFSecCustSecGroupEltTabPane
	extends CFTabPane
	implements ICFSecJavaFXSecGroupPaneCommon
	{
		public final String LABEL_TabFilterIsVisibleList = "Visible Cluster Sec Group";
		protected CFTab tabFilterIsVisibleList = null;
		public final String LABEL_TabFilterSecUserList = "Cluster Sec Group for User";
		protected CFTab tabFilterSecUserList = null;
		public final String LABEL_TabAllList = "All Cluster Sec Group";
		protected CFTab tabAllList = null;
		protected CFBorderPane tabViewFilterIsVisibleListPane = null;
		protected CFBorderPane tabViewFilterSecUserListPane = null;
		protected CFBorderPane tabViewAllListPane = null;

		protected CFSecCustClusterSecGroupTabPaneEnum activeTabPane = CFSecCustClusterSecGroupTabPaneEnum.IS_VISIBLE;

		public CFSecCustSecGroupEltTabPane( ICFFormManager formManager, ICFSecCustSchema argSchema, ICFSecSecGroupObj argFocus ) {
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
			setJavaFXFocusAsSecGroup( argFocus );
			// Wire the newly constructed Panes/Tabs to this TabPane
			tabFilterIsVisibleList = new CFTab();
			tabFilterIsVisibleList.setText( LABEL_TabFilterIsVisibleList );
			tabFilterIsVisibleList.setContent( dataScrollPaneFilterByIsVisible );
			getTabs().add( tabFilterIsVisibleList );
			tabFilterSecUserList = new CFTab();
			tabFilterSecUserList.setText( LABEL_TabFilterSecUserList );
			tabFilterSecUserList.setContent( dataScrollPaneFilterBySecUser );
			getTabs().add( tabFilterSecUserList );
			tabAllList = new CFTab();
			tabAllList.setText( LABEL_TabAllList );
			tabAllList.setContent( dataScrollPane );
			getTabs().add( tabAllList );
			setActiveTabPane( CFSecCustClusterSecGroupTabPaneEnum.IS_VISIBLE );
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

		public ICFLibAnyObj getJavaFXFocus() {
			ICFLibAnyObj superretval = super.getJavaFXFocus();
			ICFLibAnyObj retval = superretval;
			CFSecCustClusterSecGroupTabPaneEnum activeTabPaneEnum = getEltTabPane().getActiveTabPaneEnum();
			switch( activeTabPaneEnum ) {
			case IS_VISIBLE:
				retval = getEltTabPane().getTabViewFilterIsVisibleListPane().getJavaFXFocus();
				break;
			case SEC_USER:
				retval = getEltTabPane().getTabViewFilterSecUserListPane().getJavaFXFocus();
				break;
			case ALL:
				retval = getEltTabPane().getTabViewAllListPane().getJavaFXFocus();
				break;
			}
			if( retval != null ) {
				if( ! retval.equals( superretval ) ) {
					setJavaFXFocus( retval );
				}
			}
			return( retval );
		}

		public void setJavaFXFocus( ICFLibAnyObj value ) {
			final String S_ProcName = "setJavaFXFocus";
			if( ( value == null ) || ( value instanceof ICFSecTSecGroupObj ) ) {
				super.setJavaFXFocus( value );
				if( eltTabPane != null ) {
					eltTabPane.setJavaFXFocus( value );
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

		public void setJavaFXFocusAsSecGroup( ICFSecSecGroupObj value ) {
			setJavaFXFocus( value );
		}

		public ICFSecSecGroupObj getJavaFXFocusAsSecGroup() {
			return( (ICFSecSecGroupObj)getJavaFXFocus() );
		}

		protected class RefreshFilterIsVisibleList
		implements ICFRefreshCallback
		{
			public RefreshFilterIsVisibleList() {
			}

			public void refreshMe() {
				loadData( false );

				Collection<ICFSecSecGroupObj> dataCollection = listOfSecGroupFilterByIsVisible;
				ICFSecSecGroupObj focus = (ICFSecSecGroupObj)getJavaFXFocusAsSecGroup();
				CFBorderPane pane = getTabViewFilterIsVisibleListPane();
				ICFSecJavaFXSecGroupPaneList jpList = (ICFSecJavaFXSecGroupPaneList)pane;
				jpList.setJavaFXDataCollection( listOfSecGroupFilterByIsVisible );
			}
		}

		public CFBorderPane getTabViewFilterIsVisibleListPane() {
			if( tabViewFilterIsVisibleListPane == null ) {
				Collection<ICFSecSecGrpIncObj> dataCollection;
				ICFSecSecGroupObj focus = (ICFSecSecGroupObj)getJavaFXFocusAsSecGroup();
				ICFSecClusterObj javafxContainer;
				if( ( focus != null ) && ( focus instanceof ICFSecSecGroupObj ) ) {
					javafxContainer = ((ICFSecSecGroupObj)focus).getRequiredContainerCluster();
				}
				else {
					javafxContainer = null;
				}
				tabViewFilterIsVisibleListPane = javafxSchema.getSecGroupFactory().newListPane( cfFormManager, javafxContainer, null, listOfSecGroupFilterByIsVisible, new RefreshFilterIsVisibleList(), false );
			}
			return( tabViewFilterIsVisibleListPane );
		}

		protected class RefreshFilterSecUserList
		implements ICFRefreshCallback
		{
			public RefreshFilterSecUserList() {
			}

			public void refreshMe() {
				loadData( false );

				Collection<ICFSecSecGroupObj> dataCollection = listOfSecGroupFilterBySecUser;
				ICFSecSecGroupObj focus = (ICFSecSecGroupObj)getJavaFXFocusAsSecGroup();
				CFBorderPane pane = getTabViewFilterSecUserListPane();
				ICFSecJavaFXSecGroupPaneList jpList = (ICFSecJavaFXSecGroupPaneList)pane;
				jpList.setJavaFXDataCollection( listOfSecGroupFilterByIsVisible );
			}
		}

		public CFBorderPane getTabViewFilterSecUserListPane() {
			if( tabViewFilterSecUserListPane == null ) {
				Collection<ICFSecSecGrpIncObj> dataCollection;
				ICFSecSecGroupObj focus = (ICFSecSecGroupObj)getJavaFXFocusAsSecGroup();
				ICFSecClusterObj javafxContainer;
				if( ( focus != null ) && ( focus instanceof ICFSecSecGroupObj ) ) {
					javafxContainer = ((ICFSecSecGroupObj)focus).getRequiredContainerCluster();
				}
				else {
					javafxContainer = null;
				}
				tabViewFilterSecUserListPane = javafxSchema.getSecGroupFactory().newListPane( cfFormManager, javafxContainer, null, listOfSecGroupFilterBySecUser, new RefreshFilterSecUserList(), false );
			}
			return( tabViewFilterSecUserListPane );
		}

		protected class RefreshAllList
		implements ICFRefreshCallback
		{
			public RefreshAllList() {
			}

			public void refreshMe() {
				loadData( false );

				Collection<ICFSecSecGroupObj> dataCollection = listOfSecGroup;
				ICFSecSecGroupObj focus = (ICFSecSecGroupObj)getJavaFXFocusAsSecGroup();
				CFBorderPane pane = getTabViewAllListPane();
				ICFSecJavaFXSecGroupPaneList jpList = (ICFSecJavaFXSecGroupPaneList)pane;
				jpList.setJavaFXDataCollection( listOfSecGroup );
			}
		}

		public CFBorderPane getTabViewAllListPane() {
			if( tabViewAllListPane == null ) {
				Collection<ICFSecSecGrpIncObj> dataCollection;
				ICFSecSecGroupObj focus = (ICFSecSecGroupObj)getJavaFXFocusAsSecGroup();
				ICFSecClusterObj javafxContainer;
				if( ( focus != null ) && ( focus instanceof ICFSecSecGroupObj ) ) {
					javafxContainer = ((ICFSecSecGroupObj)focus).getRequiredContainerCluster();
				}
				else {
					javafxContainer = null;
				}
				tabViewAllListPane = javafxSchema.getSecGroupFactory().newListPane( cfFormManager, javafxContainer, null, listOfSecGroupFilterByIsVisible, new RefreshAllList(), false );
			}
			return( tabViewAllListPane );
		}

		public CFPane.PaneMode getPaneMode() {
			CFPane.PaneMode retval = super.getPaneMode();
			SingleSelectionModel<Tab> selectionModel = eltTabPane.getSelectionModel();
			if( selectionModel != null ) {
				Tab curSelectedTab = selectionModel.getSelectedItem();
				if( curSelectedTab != null ) {
					if( curSelectedTab.equals( tabFilterIsVisibleList ) ) {
						retval = tabViewFilterIsVisibleListPane.getPaneMode();
					}
					else if( curSelectedTab.equals( tabFilterSecUserList ) ) {
						retval = tabViewFilterSecUserListPane.getPaneMode();
					}
					else if( curSelectedTab.equals( tabAllList ) ) {
						retval = tabViewAllListPane.getPaneMode();
					}
				}
			}
			return( retval );
		}

		public void setPaneMode( CFPane.PaneMode value ) {
			CFPane.PaneMode oldMode = getPaneMode();
			super.setPaneMode( value );
			SingleSelectionModel<Tab> selectionModel = eltTabPane.getSelectionModel();
			if( selectionModel != null ) {
				Tab curSelectedTab = selectionModel.getSelectedItem();
				if( curSelectedTab != null ) {
					if( curSelectedTab.equals( tabFilterIsVisibleList ) ) {
						((ICFSecJavaFXSecGrpIncPaneCommon)tabViewFilterIsVisibleListPane).setPaneMode( value );
					}
					else if( curSelectedTab.equals( tabFilterSecUserList ) ) {
						((ICFSecJavaFXSecGrpIncPaneCommon)tabViewFilterSecUserListPane).setPaneMode( value );
					}
					else if( curSelectedTab.equals( tabAllList ) ) {
						((ICFSecJavaFXSecGrpIncPaneCommon)tabViewAllListPane).setPaneMode( value );
					}
				}
			}
		}

		public CFSecCustClusterSecGroupTabPaneEnum getActiveTabPaneEnum() {
			CFSecCustClusterSecGroupTabPaneEnum retval = activeTabPane;
			SingleSelectionModel<Tab> selectionModel = eltTabPane.getSelectionModel();
			if( selectionModel != null ) {
				Tab curSelectedTab = selectionModel.getSelectedItem();
				if( curSelectedTab != null ) {
					if( curSelectedTab.equals( tabFilterIsVisibleList ) ) {
						retval = CFSecCustClusterSecGroupTabPaneEnum.IS_VISIBLE;
					}
					else if( curSelectedTab.equals( tabFilterSecUserList ) ) {
						retval = CFSecCustClusterSecGroupTabPaneEnum.SEC_USER;
					}
					else if( curSelectedTab.equals( tabAllList ) ) {
						retval = CFSecCustClusterSecGroupTabPaneEnum.ALL;
					}
					if( activeTabPane != retval ) {
						activeTabPane = retval;
					}
				}
			}
			return( retval );
		}

		public void setActiveTabPane( CFSecCustClusterSecGroupTabPaneEnum value ) {
			final String S_MyProcName = "setActiveTabPaneEnum";
			SingleSelectionModel<Tab> selectionModel = getSelectionModel();
			switch( value ) {
				case IS_VISIBLE:
					activeTabPane = value.IS_VISIBLE;
					if( tabFilterIsVisibleList != null ) {
						selectionModel.select( tabFilterIsVisibleList );
					}
					break;
				case SEC_USER:
					activeTabPane = value.SEC_USER;
					if( tabFilterSecUserList != null ) {
						selectionModel.select( tabFilterSecUserList );
					}
					break;
				case ALL:
					activeTabPane = value.ALL;
					if( tabAllList != null ) {
						selectionModel.select( tabAllList );
					}
					break;
				default:
					throw new CFLibInvalidArgumentException( getClass(),
						S_MyProcName,
						1,
						"value" );
			}
		}
	}

	protected CFSecCustSecGroupEltTabPane eltTabPane = null;

	public CFSecCustSecGroupEltTabPane getEltTabPane() {
		return( eltTabPane );
	}

	public CFSecCustManageClusterSecGroupPane( ICFFormManager formManager, ICFSecCustSchema argSchema ) {
		super();
		final String S_ProcName = "construct";
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
		TableColumn<ICFSecSecGroupObj, CFLibDbKeyHash256> tableColumnSecGroupId = null;
		TableColumn<ICFSecSecGroupObj, String> tableColumnName = null;
		TableColumn<ICFSecSecGroupObj, Boolean> tableColumnIsVisible = null;
		dataTable = new TableView<ICFSecSecGroupObj>();
		dataTableFilterByIsVisible = new TableView<ICFSecSecGroupObj>();
		dataTableFilterBySecUser = new TableView<ICFSecSecGroupObj>();
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
		dataTableFilterByIsVisible.getColumns().add( tableColumnSecGroupId );
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
		dataTableFilterBySecUser.getColumns().add( tableColumnSecGroupId );
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
		dataTableFilterByIsVisible.getColumns().add( tableColumnName );
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
		dataTableFilterBySecUser.getColumns().add( tableColumnName );

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
		dataTableFilterByIsVisible.getColumns().add( tableColumnIsVisible );
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
		dataTableFilterBySecUser.getColumns().add( tableColumnIsVisible );

		dataTable.getSelectionModel().selectedItemProperty().addListener(
			new ChangeListener<ICFSecSecGroupObj>() {
				@Override public void changed( ObservableValue<? extends ICFSecSecGroupObj> observable,
					ICFSecSecGroupObj oldValue,
					ICFSecSecGroupObj newValue )
				{
					Parent cont = getParent();
					while( ( cont != null ) && ( ! ( cont instanceof ICFForm ) ) ) {
						cont = cont.getParent();
					}
					if( cont != null ) {
						if( cont instanceof ICFSecJavaFXSecGroupPaneCommon ) {
							ICFSecJavaFXSecGroupPaneCommon paneCommon = (ICFSecJavaFXSecGroupPaneCommon)cont;
							paneCommon.setJavaFXFocus( newValue );
						}
					}
				}
			});
		dataTableFilterByIsVisible.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<ICFSecSecGroupObj>() {
					@Override public void changed( ObservableValue<? extends ICFSecSecGroupObj> observable,
						ICFSecSecGroupObj oldValue,
						ICFSecSecGroupObj newValue )
					{
						Parent cont = getParent();
						while( ( cont != null ) && ( ! ( cont instanceof ICFForm ) ) ) {
							cont = cont.getParent();
						}
						if( cont != null ) {
							if( cont instanceof ICFSecJavaFXSecGroupPaneCommon ) {
								ICFSecJavaFXSecGroupPaneCommon paneCommon = (ICFSecJavaFXSecGroupPaneCommon)cont;
								paneCommon.setJavaFXFocus( newValue );
							}
						}
					}
				});
		dataTableFilterBySecUser.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<ICFSecSecGroupObj>() {
					@Override public void changed( ObservableValue<? extends ICFSecSecGroupObj> observable,
						ICFSecSecGroupObj oldValue,
						ICFSecSecGroupObj newValue )
					{
						Parent cont = getParent();
						while( ( cont != null ) && ( ! ( cont instanceof ICFForm ) ) ) {
							cont = cont.getParent();
						}
						if( cont != null ) {
							if( cont instanceof ICFSecJavaFXSecGroupPaneCommon ) {
								ICFSecJavaFXSecGroupPaneCommon paneCommon = (ICFSecJavaFXSecGroupPaneCommon)cont;
								paneCommon.setJavaFXFocus( newValue );
							}
						}
					}
				});

		refreshMe();

		dataScrollPane = new ScrollPane( dataTable );
		dataScrollPaneFilterByIsVisible = new ScrollPane( dataTableFilterByIsVisible );
		dataScrollPaneFilterBySecUser = new ScrollPane( dataTableFilterBySecUser );

		eltTabPane = new CFSecCustSecGroupEltTabPane( formManager, argSchema, null );

		setCenter( eltTabPane );

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

	public void loadData( boolean forceReload ) {
		ICFSecSchemaObj schemaObj = (ICFSecSchemaObj)javafxSchema.getSchema();
		if( ( containingCluster == null ) || forceReload ) {
			ICFSecAuthorization auth = schemaObj.getAuthorization();
			CFLibDbKeyHash256 containingClusterId = auth.getSecClusterId();
			containingCluster = schemaObj.getClusterTableObj().readClusterByIdIdx( containingClusterId );
		}
		if( ( listOfSecGroup == null ) || forceReload ) {
			listOfSecGroupFilterByIsVisible = null;
			listOfSecGroupFilterBySecUser = null;
			observableListOfSecGroup = null;
			observableListOfSecGroupFilterByIsVisible = null;
			observableListOfSecGroupFilterBySecUser = null;
			listOfSecGroup = schemaObj.getSecGroupTableObj().readSecGroupByClusterIdx( containingCluster.getRequiredId(), javafxIsInitializing );
			if( listOfSecGroup != null ) {
				observableListOfSecGroup = FXCollections.observableArrayList( listOfSecGroup );
				observableListOfSecGroup.sort( compareSecGroupByQualName );
				ICFSecSecUserObj secUser = schemaObj.getSecUser();
				ICFSecSecGroupObj secGroupObj = null;
				ICFSecSecGrpMembObj secGroupObjMember = null;
				List<ICFSecSecGrpMembObj> secGroupObjMembers = null;
				Iterator<ICFSecSecGrpMembObj> iterSecGroupObjMembers = null;
				LinkedList<ICFSecSecGroupObj> listOfSecGroupFilterByIsVisible = new LinkedList<ICFSecSecGroupObj>();
				LinkedList<ICFSecSecGroupObj> filterListBySecUser = new LinkedList<ICFSecSecGroupObj>();
				Iterator<ICFSecSecGroupObj> iterSecGroup = listOfSecGroup.iterator();
				while( iterSecGroup.hasNext() ) {
					secGroupObj = iterSecGroup.next();
					if( secGroupObj != null ) {
						if( secGroupObj.getRequiredIsVisible() ) {
							listOfSecGroupFilterByIsVisible.add( secGroupObj );
						}
						secGroupObjMembers = secGroupObj.getOptionalComponentsMember();
						if( secGroupObjMembers != null ) {
							iterSecGroupObjMembers = secGroupObjMembers.iterator();
							while( iterSecGroupObjMembers.hasNext() ) {
								secGroupObjMember = iterSecGroupObjMembers.next();
								if( secGroupObjMember != null ) {
									if( secGroupObjMember.equals( secUser ) ) {
										filterListBySecUser.add( secGroupObj );
									}
								}
							}
						}
					}
				}
				observableListOfSecGroupFilterByIsVisible = FXCollections.observableArrayList( listOfSecGroupFilterByIsVisible ); 
				observableListOfSecGroupFilterBySecUser = FXCollections.observableArrayList( filterListBySecUser ); 
			}
			else {
				observableListOfSecGroup = FXCollections.observableArrayList();
				observableListOfSecGroupFilterByIsVisible = FXCollections.observableArrayList();
				observableListOfSecGroupFilterBySecUser = FXCollections.observableArrayList();
			}
			dataTable.setItems( observableListOfSecGroup );
			// Hack from stackoverflow to fix JavaFX TableView refresh issue
			((TableColumn)dataTable.getColumns().get(0)).setVisible( false );
			((TableColumn)dataTable.getColumns().get(0)).setVisible( true );

			dataTableFilterByIsVisible.setItems( observableListOfSecGroupFilterByIsVisible );
			// Hack from stackoverflow to fix JavaFX TableView refresh issue
			((TableColumn)dataTableFilterByIsVisible.getColumns().get(0)).setVisible( false );
			((TableColumn)dataTableFilterByIsVisible.getColumns().get(0)).setVisible( true );

			dataTableFilterBySecUser.setItems( observableListOfSecGroupFilterByIsVisible );
			// Hack from stackoverflow to fix JavaFX TableView refresh issue
			((TableColumn)dataTableFilterBySecUser.getColumns().get(0)).setVisible( false );
			((TableColumn)dataTableFilterBySecUser.getColumns().get(0)).setVisible( true );
		}
	}

	public void refreshMe() {
		loadData( true );
	}
}
