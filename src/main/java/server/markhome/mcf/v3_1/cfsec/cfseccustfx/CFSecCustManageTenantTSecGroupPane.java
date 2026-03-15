// Description: Java 13 JavaFX Finder Pane implementation for TSecGroup.

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

/**
 *	CFSecJavaFXTSecGroupFinderPane JavaFX Finder Pane implementation
 *	for TSecGroup.
 */
public class CFSecCustManageTenantTSecGroupPane
extends CFBorderPane
implements ICFRefreshCallback
{
	protected ICFFormManager cfFormManager = null;
	protected ICFSecJavaFXSchema javafxSchema = null;
	protected ICFSecCustSchema custSchema = null;

	protected boolean javafxIsInitializing = true;
	protected ICFSecClusterObj containingCluster = null;
	protected ICFSecTenantObj containingTenant = null;
	protected List<ICFSecTSecGroupObj> listOfTSecGroup = null;
	protected List<ICFSecTSecGroupObj> listOfTSecGroupFilterByIsVisible = null;
	protected List<ICFSecTSecGroupObj> listOfTSecGroupFilterBySecUser = null;
	protected ObservableList<ICFSecTSecGroupObj> observableListOfTSecGroup = null;
	protected ObservableList<ICFSecTSecGroupObj> observableListOfTSecGroupFilterByIsVisible = null;
	protected ObservableList<ICFSecTSecGroupObj> observableListOfTSecGroupFilterBySecUser = null;
	protected TableView<ICFSecTSecGroupObj> dataTable = null;
	protected TableView<ICFSecTSecGroupObj> dataTableFilterByIsVisible = null;
	protected TableView<ICFSecTSecGroupObj> dataTableFilterBySecUser = null;
	protected ScrollPane dataScrollPane = null;
	protected ScrollPane dataScrollPaneFilterByIsVisible = null;
	protected ScrollPane dataScrollPaneFilterBySecUser = null;

	public static enum CFSecCustTenantTSecGroupTabPaneEnum {
		IS_VISIBLE,
		SEC_USER,
		ALL };

	public class CFSecCustTSecGroupEltTabPane
	extends CFTabPane
	implements ICFSecJavaFXTSecGroupPaneCommon
	{
		public final String LABEL_TabFilterIsVisibleList = "Visible Tenant Sec Group";
		protected CFTab tabFilterIsVisibleList = null;
		public final String LABEL_TabFilterSecUserList = "Tenant Sec Group for User";
		protected CFTab tabFilterSecUserList = null;
		public final String LABEL_TabAllList = "All Tenant Sec Group";
		protected CFTab tabAllList = null;
		protected CFBorderPane tabViewFilterIsVisibleListPane = null;
		protected CFBorderPane tabViewFilterSecUserListPane = null;
		protected CFBorderPane tabViewAllListPane = null;

		protected CFSecCustTenantTSecGroupTabPaneEnum activeTabPane = CFSecCustTenantTSecGroupTabPaneEnum.IS_VISIBLE;

		public CFSecCustTSecGroupEltTabPane( ICFFormManager formManager, ICFSecCustSchema argSchema, ICFSecTSecGroupObj argFocus ) {
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
			setJavaFXFocusAsTSecGroup( argFocus );
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
			setActiveTabPane( CFSecCustTenantTSecGroupTabPaneEnum.IS_VISIBLE );
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
			CFSecCustTenantTSecGroupTabPaneEnum activeTabPaneEnum = getEltTabPane().getActiveTabPaneEnum();
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
				CFSecCustTenantTSecGroupTabPaneEnum activeTabPaneEnum = getEltTabPane().getActiveTabPaneEnum();
				switch( activeTabPaneEnum ) {
				case IS_VISIBLE:
					getEltTabPane().getTabViewFilterIsVisibleListPane().setJavaFXFocus( value );
					break;
				case SEC_USER:
					getEltTabPane().getTabViewFilterSecUserListPane().setJavaFXFocus( value );
					break;
				case ALL:
					getEltTabPane().getTabViewAllListPane().setJavaFXFocus( value );
					break;
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

		protected class RefreshFilterIsVisibleList
		implements ICFRefreshCallback
		{
			public RefreshFilterIsVisibleList() {
			}

			public void refreshMe() {
				loadData( false );

				Collection<ICFSecTSecGroupObj> dataCollection = listOfTSecGroupFilterByIsVisible;
				ICFSecTSecGroupObj focus = (ICFSecTSecGroupObj)getJavaFXFocusAsTSecGroup();
				CFBorderPane pane = getTabViewFilterIsVisibleListPane();
				ICFSecJavaFXTSecGroupPaneList jpList = (ICFSecJavaFXTSecGroupPaneList)pane;
				jpList.setJavaFXDataCollection( listOfTSecGroupFilterByIsVisible );
			}
		}

		public CFBorderPane getTabViewFilterIsVisibleListPane() {
			if( tabViewFilterIsVisibleListPane == null ) {
				Collection<ICFSecTSecGrpIncObj> dataCollection;
				ICFSecTSecGroupObj focus = (ICFSecTSecGroupObj)getJavaFXFocusAsTSecGroup();
				ICFSecTenantObj javafxContainer;
				if( ( focus != null ) && ( focus instanceof ICFSecTSecGroupObj ) ) {
					javafxContainer = ((ICFSecTSecGroupObj)focus).getRequiredContainerTenant();
				}
				else {
					javafxContainer = null;
				}
				tabViewFilterIsVisibleListPane = javafxSchema.getTSecGroupFactory().newListPane( cfFormManager, javafxContainer, null, listOfTSecGroupFilterByIsVisible, new RefreshFilterIsVisibleList(), false );
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

				Collection<ICFSecTSecGroupObj> dataCollection = listOfTSecGroupFilterBySecUser;
				ICFSecTSecGroupObj focus = (ICFSecTSecGroupObj)getJavaFXFocusAsTSecGroup();
				CFBorderPane pane = getTabViewFilterSecUserListPane();
				ICFSecJavaFXTSecGroupPaneList jpList = (ICFSecJavaFXTSecGroupPaneList)pane;
				jpList.setJavaFXDataCollection( listOfTSecGroupFilterByIsVisible );
			}
		}

		public CFBorderPane getTabViewFilterSecUserListPane() {
			if( tabViewFilterSecUserListPane == null ) {
				Collection<ICFSecTSecGrpIncObj> dataCollection;
				ICFSecTSecGroupObj focus = (ICFSecTSecGroupObj)getJavaFXFocusAsTSecGroup();
				ICFSecTenantObj javafxContainer;
				if( ( focus != null ) && ( focus instanceof ICFSecTSecGroupObj ) ) {
					javafxContainer = ((ICFSecTSecGroupObj)focus).getRequiredContainerTenant();
				}
				else {
					javafxContainer = null;
				}
				tabViewFilterSecUserListPane = javafxSchema.getTSecGroupFactory().newListPane( cfFormManager, javafxContainer, null, listOfTSecGroupFilterBySecUser, new RefreshFilterSecUserList(), false );
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

				Collection<ICFSecTSecGroupObj> dataCollection = listOfTSecGroup;
				ICFSecTSecGroupObj focus = (ICFSecTSecGroupObj)getJavaFXFocusAsTSecGroup();
				CFBorderPane pane = getTabViewAllListPane();
				ICFSecJavaFXTSecGroupPaneList jpList = (ICFSecJavaFXTSecGroupPaneList)pane;
				jpList.setJavaFXDataCollection( listOfTSecGroup );
			}
		}

		public CFBorderPane getTabViewAllListPane() {
			if( tabViewAllListPane == null ) {
				Collection<ICFSecTSecGrpIncObj> dataCollection;
				ICFSecTSecGroupObj focus = (ICFSecTSecGroupObj)getJavaFXFocusAsTSecGroup();
				ICFSecTenantObj javafxContainer;
				if( ( focus != null ) && ( focus instanceof ICFSecTSecGroupObj ) ) {
					javafxContainer = ((ICFSecTSecGroupObj)focus).getRequiredContainerTenant();
				}
				else {
					javafxContainer = null;
				}
				tabViewAllListPane = javafxSchema.getTSecGroupFactory().newListPane( cfFormManager, javafxContainer, null, listOfTSecGroupFilterByIsVisible, new RefreshAllList(), false );
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
						((ICFSecJavaFXTSecGrpIncPaneCommon)tabViewFilterIsVisibleListPane).setPaneMode( value );
					}
					else if( curSelectedTab.equals( tabFilterSecUserList ) ) {
						((ICFSecJavaFXTSecGrpIncPaneCommon)tabViewFilterSecUserListPane).setPaneMode( value );
					}
					else if( curSelectedTab.equals( tabAllList ) ) {
						((ICFSecJavaFXTSecGrpIncPaneCommon)tabViewAllListPane).setPaneMode( value );
					}
				}
			}
		}

		public CFSecCustTenantTSecGroupTabPaneEnum getActiveTabPaneEnum() {
			CFSecCustTenantTSecGroupTabPaneEnum retval = activeTabPane;
			SingleSelectionModel<Tab> selectionModel = eltTabPane.getSelectionModel();
			if( selectionModel != null ) {
				Tab curSelectedTab = selectionModel.getSelectedItem();
				if( curSelectedTab != null ) {
					if( curSelectedTab.equals( tabFilterIsVisibleList ) ) {
						retval = CFSecCustTenantTSecGroupTabPaneEnum.IS_VISIBLE;
					}
					else if( curSelectedTab.equals( tabFilterSecUserList ) ) {
						retval = CFSecCustTenantTSecGroupTabPaneEnum.SEC_USER;
					}
					else if( curSelectedTab.equals( tabAllList ) ) {
						retval = CFSecCustTenantTSecGroupTabPaneEnum.ALL;
					}
					if( activeTabPane != retval ) {
						activeTabPane = retval;
					}
				}
			}
			return( retval );
		}

		public void setActiveTabPane( CFSecCustTenantTSecGroupTabPaneEnum value ) {
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
					throw new CFLibInvalidArgumentException(getClass(),
						S_MyProcName,
						1,
						"value" );
			}
		}
	}

	protected CFSecCustTSecGroupEltTabPane eltTabPane = null;

	public CFSecCustTSecGroupEltTabPane getEltTabPane() {
		return( eltTabPane );
	}

	public CFSecCustManageTenantTSecGroupPane( ICFFormManager formManager, ICFSecCustSchema argSchema ) {
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
		TableColumn<ICFSecTSecGroupObj, CFLibDbKeyHash256> tableColumnTSecGroupId = null;
		TableColumn<ICFSecTSecGroupObj, String> tableColumnName = null;
		TableColumn<ICFSecTSecGroupObj, Boolean> tableColumnIsVisible = null;
		dataTable = new TableView<ICFSecTSecGroupObj>();
		dataTableFilterByIsVisible = new TableView<ICFSecTSecGroupObj>();
		dataTableFilterBySecUser = new TableView<ICFSecTSecGroupObj>();
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
		dataTableFilterByIsVisible.getColumns().add( tableColumnTSecGroupId );
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
		dataTableFilterBySecUser.getColumns().add( tableColumnTSecGroupId );
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
		dataTableFilterByIsVisible.getColumns().add( tableColumnName );
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
		dataTableFilterBySecUser.getColumns().add( tableColumnName );

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
		dataTableFilterByIsVisible.getColumns().add( tableColumnIsVisible );
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
		dataTableFilterBySecUser.getColumns().add( tableColumnIsVisible );

		dataTable.getSelectionModel().selectedItemProperty().addListener(
			new ChangeListener<ICFSecTSecGroupObj>() {
				@Override public void changed( ObservableValue<? extends ICFSecTSecGroupObj> observable,
					ICFSecTSecGroupObj oldValue,
					ICFSecTSecGroupObj newValue )
				{
					Parent cont = getParent();
					while( ( cont != null ) && ( ! ( cont instanceof ICFForm ) ) ) {
						cont = cont.getParent();
					}
					if( cont != null ) {
						if( cont instanceof ICFSecJavaFXTSecGroupPaneCommon ) {
							ICFSecJavaFXTSecGroupPaneCommon paneCommon = (ICFSecJavaFXTSecGroupPaneCommon)cont;
							paneCommon.setJavaFXFocus( newValue );
						}
					}
				}
			});
		dataTableFilterByIsVisible.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<ICFSecTSecGroupObj>() {
					@Override public void changed( ObservableValue<? extends ICFSecTSecGroupObj> observable,
						ICFSecTSecGroupObj oldValue,
						ICFSecTSecGroupObj newValue )
					{
						Parent cont = getParent();
						while( ( cont != null ) && ( ! ( cont instanceof ICFForm ) ) ) {
							cont = cont.getParent();
						}
						if( cont != null ) {
							if( cont instanceof ICFSecJavaFXTSecGroupPaneCommon ) {
								ICFSecJavaFXTSecGroupPaneCommon paneCommon = (ICFSecJavaFXTSecGroupPaneCommon)cont;
								paneCommon.setJavaFXFocus( newValue );
							}
						}
					}
				});
		dataTableFilterBySecUser.getSelectionModel().selectedItemProperty().addListener(
				new ChangeListener<ICFSecTSecGroupObj>() {
					@Override public void changed( ObservableValue<? extends ICFSecTSecGroupObj> observable,
						ICFSecTSecGroupObj oldValue,
						ICFSecTSecGroupObj newValue )
					{
						Parent cont = getParent();
						while( ( cont != null ) && ( ! ( cont instanceof ICFForm ) ) ) {
							cont = cont.getParent();
						}
						if( cont != null ) {
							if( cont instanceof ICFSecJavaFXTSecGroupPaneCommon ) {
								ICFSecJavaFXTSecGroupPaneCommon paneCommon = (ICFSecJavaFXTSecGroupPaneCommon)cont;
								paneCommon.setJavaFXFocus( newValue );
							}
						}
					}
				});

		refreshMe();

		dataScrollPane = new ScrollPane( dataTable );
		dataScrollPaneFilterByIsVisible = new ScrollPane( dataTableFilterByIsVisible );
		dataScrollPaneFilterBySecUser = new ScrollPane( dataTableFilterBySecUser );

		eltTabPane = new CFSecCustTSecGroupEltTabPane( formManager, argSchema, null );

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

	public void loadData( boolean forceReload ) {
		ICFSecSchemaObj schemaObj = (ICFSecSchemaObj)javafxSchema.getSchema();
		if( ( containingTenant == null ) || forceReload ) {
			ICFSecAuthorization auth = schemaObj.getAuthorization();
			CFLibDbKeyHash256 containingClusterId = auth.getSecClusterId(); 
			containingCluster = schemaObj.getClusterTableObj().readClusterByIdIdx( containingClusterId );
			CFLibDbKeyHash256 containingTenantId = auth.getSecTenantId(); 
			containingTenant = schemaObj.getTenantTableObj().readTenantByIdIdx( containingTenantId );
		}
		if( ( listOfTSecGroup == null ) || forceReload ) {
			listOfTSecGroupFilterByIsVisible = null;
			listOfTSecGroupFilterBySecUser = null;
			observableListOfTSecGroup = null;
			observableListOfTSecGroupFilterByIsVisible = null;
			observableListOfTSecGroupFilterBySecUser = null;
			listOfTSecGroup = schemaObj.getTSecGroupTableObj().readTSecGroupByTenantIdx( containingTenant.getRequiredId(), javafxIsInitializing );
			if( listOfTSecGroup != null ) {
				observableListOfTSecGroup = FXCollections.observableArrayList( listOfTSecGroup );
				observableListOfTSecGroup.sort( compareTSecGroupByQualName );
				ICFSecSecUserObj secUser = schemaObj.getSecUser();
				ICFSecTSecGroupObj tsecGroupObj = null;
				ICFSecTSecGrpMembObj tsecGroupObjMember = null;
				List<ICFSecTSecGrpMembObj> tsecGroupObjMembers = null;
				Iterator<ICFSecTSecGrpMembObj> iterTSecGroupObjMembers = null;
				LinkedList<ICFSecTSecGroupObj> listOfTSecGroupFilterByIsVisible = new LinkedList<ICFSecTSecGroupObj>();
				LinkedList<ICFSecTSecGroupObj> filterListBySecUser = new LinkedList<ICFSecTSecGroupObj>();
				Iterator<ICFSecTSecGroupObj> iterTSecGroup = listOfTSecGroup.iterator();
				while( iterTSecGroup.hasNext() ) {
					tsecGroupObj = iterTSecGroup.next();
					if( tsecGroupObj != null ) {
						if( tsecGroupObj.getRequiredIsVisible() ) {
							listOfTSecGroupFilterByIsVisible.add( tsecGroupObj );
						}
						tsecGroupObjMembers = tsecGroupObj.getOptionalComponentsMember();
						if( tsecGroupObjMembers != null ) {
							iterTSecGroupObjMembers = tsecGroupObjMembers.iterator();
							while( iterTSecGroupObjMembers.hasNext() ) {
								tsecGroupObjMember = iterTSecGroupObjMembers.next();
								if( tsecGroupObjMember != null ) {
									if( tsecGroupObjMember.equals( secUser ) ) {
										filterListBySecUser.add( tsecGroupObj );
									}
								}
							}
						}
					}
				}
				observableListOfTSecGroupFilterByIsVisible = FXCollections.observableArrayList( listOfTSecGroupFilterByIsVisible ); 
				observableListOfTSecGroupFilterBySecUser = FXCollections.observableArrayList( filterListBySecUser ); 
			}
			else {
				observableListOfTSecGroup = FXCollections.observableArrayList();
				observableListOfTSecGroupFilterByIsVisible = FXCollections.observableArrayList();
				observableListOfTSecGroupFilterBySecUser = FXCollections.observableArrayList();
			}
			dataTable.setItems( observableListOfTSecGroup );
			// Hack from stackoverflow to fix JavaFX TableView refresh issue
			((TableColumn)dataTable.getColumns().get(0)).setVisible( false );
			((TableColumn)dataTable.getColumns().get(0)).setVisible( true );

			dataTableFilterByIsVisible.setItems( observableListOfTSecGroupFilterByIsVisible );
			// Hack from stackoverflow to fix JavaFX TableView refresh issue
			((TableColumn)dataTableFilterByIsVisible.getColumns().get(0)).setVisible( false );
			((TableColumn)dataTableFilterByIsVisible.getColumns().get(0)).setVisible( true );

			dataTableFilterBySecUser.setItems( observableListOfTSecGroupFilterByIsVisible );
			// Hack from stackoverflow to fix JavaFX TableView refresh issue
			((TableColumn)dataTableFilterBySecUser.getColumns().get(0)).setVisible( false );
			((TableColumn)dataTableFilterBySecUser.getColumns().get(0)).setVisible( true );
		}
	}

	public void refreshMe() {
		loadData( true );
	}
}
