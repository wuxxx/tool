/*
 * Created on Mar 18, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.jst.j2ee.internal.wizard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.jst.j2ee.datamodel.properties.IEarComponentCreationDataModelProperties;
import org.eclipse.jst.j2ee.datamodel.properties.IJ2EEComponentCreationDataModelProperties;
import org.eclipse.jst.j2ee.internal.actions.IJ2EEUIContextIds;
import org.eclipse.jst.j2ee.internal.earcreation.DefaultJ2EEComponentCreationDataModelProvider;
import org.eclipse.jst.j2ee.internal.earcreation.IDefaultJ2EEComponentCreationDataModelProperties;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIPlugin;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIPluginIcons;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.wst.common.componentcore.resources.ComponentHandle;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

/**
 * @author jialin
 *
 * TODO To change the template for this generated type comment go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
public class EARComponentCreationSecondPage extends DataModelWizardPage implements IEarComponentCreationDataModelProperties{
	private Button selectAllButton;
	private Button deselectAllButton;
	private Button newModuleButton;
	private CheckboxTableViewer moduleProjectsViewer;
	private boolean ignoreCheckedState = false;
	/**
	 * @param model
	 * @param pageName
	 */
	public EARComponentCreationSecondPage(IDataModel  model, String pageName) {
		super(model, pageName);
		setTitle(J2EEUIMessages.getResourceString(J2EEUIMessages.EAR_COMPONENT_SECOND_PG_TITLE));
		setDescription(J2EEUIMessages.getResourceString(J2EEUIMessages.EAR_COMPONENT_SECOND_PG_DESC));
		setImageDescriptor(J2EEUIPlugin.getDefault().getImageDescriptor(J2EEUIPluginIcons.EAR_WIZ_BANNER));
	}

	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.frameworks.ui.WTPWizardPage#getValidationPropertyNames()
	 */
	protected String[] getValidationPropertyNames() {
		return new String[] {IEarComponentCreationDataModelProperties.J2EE_COMPONENT_LIST};
	}
	/* (non-Javadoc)
	 * @see org.eclipse.wst.common.frameworks.ui.WTPWizardPage#createTopLevelComposite(org.eclipse.swt.widgets.Composite)
	 */
	protected Composite createTopLevelComposite(Composite parent) {
		Composite modulesGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		modulesGroup.setLayout(layout);
		setInfopopID(IJ2EEUIContextIds.NEW_EAR_ADD_MODULES_PAGE);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		modulesGroup.setLayoutData(gridData);
		createModuleProjectOptions(modulesGroup);
		createButtonsGroup(modulesGroup);
		return modulesGroup;
	}

	/**
	 * @param modulesGroup
	 */
	private void createModuleProjectOptions(Composite modulesGroup) {
		moduleProjectsViewer = CheckboxTableViewer.newCheckList(modulesGroup, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		GridData gData = new GridData(GridData.FILL_BOTH);
		gData.widthHint = 200;
		gData.heightHint = 80;
		moduleProjectsViewer.getControl().setLayoutData(gData);
		int j2eeVersion = getDataModel().getIntProperty(COMPONENT_VERSION);
		AvailableJ2EEComponentsContentProvider provider = new AvailableJ2EEComponentsContentProvider(j2eeVersion);
		moduleProjectsViewer.setContentProvider(provider);
		moduleProjectsViewer.setLabelProvider(new J2EEComponentLabelProvider());
		setCheckedItemsFromModel();
		
		moduleProjectsViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				if (!ignoreCheckedState) {
					getDataModel().setProperty(J2EE_COMPONENT_LIST, getCheckedJ2EEElementsAsList());
					getDataModel().setProperty(JAVA_PROJECT_LIST, getCheckedJavaProjectsAsList());
                }
			}
		});
		TableLayout tableLayout = new TableLayout();
		moduleProjectsViewer.getTable().setLayout(tableLayout);
		moduleProjectsViewer.getTable().setHeaderVisible(false);
		moduleProjectsViewer.getTable().setLinesVisible(false);
		moduleProjectsViewer.setSorter(null);
	}

	/**
	 *  
	 */
	private void setCheckedItemsFromModel() {
		List components = (List) getDataModel().getProperty(IEarComponentCreationDataModelProperties.J2EE_COMPONENT_LIST);
		moduleProjectsViewer.setCheckedElements(components.toArray());
	}

	private void refreshModules() {
		moduleProjectsViewer.refresh();
		setCheckedItemsFromModel();
	}

	protected List getCheckedJ2EEElementsAsList() {
		Object[] elements = moduleProjectsViewer.getCheckedElements();
		List list;
		if (elements == null || elements.length == 0)
			list = Collections.EMPTY_LIST;
		else{
			list = new ArrayList(); 
			for( int i=0; i< elements.length; i++){
				if( elements[i] instanceof ComponentHandle ) {
					list.add(elements[i]);
				}
			}
		}	
		return list;
	}
	
	protected List getCheckedJavaProjectsAsList() {
		Object[] elements = moduleProjectsViewer.getCheckedElements();
		List list;
		if (elements == null || elements.length == 0)
			list = Collections.EMPTY_LIST;
		else{
			list = new ArrayList(); 
			for( int i=0; i< elements.length; i++){
				if( elements[i] instanceof IProject ) {
					list.add(elements[i]);
				}
			}
		}	
		return list;
	}
	
	
	protected void createButtonsGroup(org.eclipse.swt.widgets.Composite parent) {
		Composite buttonGroup = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		buttonGroup.setLayout(layout);
		buttonGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		selectAllButton = new Button(buttonGroup, SWT.PUSH);
		selectAllButton.setText(J2EEUIMessages.getResourceString(J2EEUIMessages.APP_PROJECT_MODULES_PG_SELECT));
		selectAllButton.addListener(SWT.Selection, this);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.widthHint = 120;
		selectAllButton.setLayoutData(gd);
		deselectAllButton = new Button(buttonGroup, SWT.PUSH);
		deselectAllButton.setText(J2EEUIMessages.getResourceString(J2EEUIMessages.APP_PROJECT_MODULES_PG_DESELECT));
		deselectAllButton.addListener(SWT.Selection, this);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.widthHint = 120;
		deselectAllButton.setLayoutData(gd);
		newModuleButton = new Button(buttonGroup, SWT.PUSH);
		newModuleButton.setText(J2EEUIMessages.getResourceString(J2EEUIMessages.APP_PROJECT_MODULES_PG_NEW));
		newModuleButton.addListener(SWT.Selection, this);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		gd.widthHint = 120;
		newModuleButton.setLayoutData(gd);
	}

	/**
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(Event)
	 */
	public void handleEvent(Event evt) {
		if (evt.widget == selectAllButton)
			handleSelectAllButtonPressed();
		else if (evt.widget == deselectAllButton)
			handleDeselectAllButtonPressed();
		else if (evt.widget == newModuleButton)
			handleNewModuleButtonPressed();
		else
			super.handleEvent(evt);
	}

	/**
	 *  
	 */
	private void handleNewModuleButtonPressed() {
		IDataModel aModel = createNewModuleModel();
		DefaultJ2EEComponentCreationWizard wizard = new DefaultJ2EEComponentCreationWizard(aModel);
		WizardDialog dialog = new WizardDialog(getShell(), wizard);
		dialog.create();
		if (dialog.open() != IDialogConstants.CANCEL_ID) {
			IWorkspaceRoot input = ResourcesPlugin.getWorkspace().getRoot();
			moduleProjectsViewer.setInput(input);
            setNewModules(aModel);
            refreshModules();
		}
	}
    /**
     * @param model
     */
    private void setNewModules(IDataModel defaultModel) {
        List newComponents = new ArrayList();
        collectNewComponents(defaultModel, newComponents);
        List oldComponents = (List) getDataModel().getProperty(IEarComponentCreationDataModelProperties.J2EE_COMPONENT_LIST);
        newComponents.addAll(oldComponents);
        getDataModel().setProperty(IEarComponentCreationDataModelProperties.J2EE_COMPONENT_LIST, newComponents);
    }
    
    private void collectNewComponents(IDataModel defaultModel, List newProjects) {
        collectComponents(defaultModel.getNestedModel(IDefaultJ2EEComponentCreationDataModelProperties.NESTED_MODEL_EJB), newProjects);
        collectComponents(defaultModel.getNestedModel(IDefaultJ2EEComponentCreationDataModelProperties.NESTED_MODEL_WEB), newProjects);
        collectComponents(defaultModel.getNestedModel(IDefaultJ2EEComponentCreationDataModelProperties.NESTED_MODEL_CLIENT), newProjects);
        collectComponents(defaultModel.getNestedModel(IDefaultJ2EEComponentCreationDataModelProperties.NESTED_MODEL_JCA), newProjects);
    }
    private void collectComponents(IDataModel compDM, List newProjects) {
        if (compDM != null) {
            String projectName = compDM.getStringProperty(IJ2EEComponentCreationDataModelProperties.PROJECT_NAME);
            if(projectName == null) return;
            IProject project = ProjectUtilities.getProject(projectName);
            String compName = compDM.getStringProperty(IJ2EEComponentCreationDataModelProperties.COMPONENT_NAME);
            if (project != null && project.exists())
                newProjects.add(ComponentHandle.create(project, compName));
        }
    }
    
	private IDataModel createNewModuleModel() {
		IDataModel defaultModel = DataModelFactory.createDataModel(new DefaultJ2EEComponentCreationDataModelProvider());
		// transfer properties, project name
		String projectName = model.getStringProperty(PROJECT_NAME);
		defaultModel.setProperty(IDefaultJ2EEComponentCreationDataModelProperties.PROJECT_NAME, projectName);
		// ear component name
		String earName = model.getStringProperty(COMPONENT_NAME);
		defaultModel.setProperty(IDefaultJ2EEComponentCreationDataModelProperties.EAR_COMPONENT_NAME, earName);
		// ear j2ee version
		int j2eeVersion = model.getIntProperty(COMPONENT_VERSION);
		defaultModel.setProperty(IDefaultJ2EEComponentCreationDataModelProperties.J2EE_VERSION, new Integer(j2eeVersion));
		return defaultModel;
	}

	/**
	 *  
	 */
	private void handleDeselectAllButtonPressed() {
		ignoreCheckedState = true;
		try {
			moduleProjectsViewer.setAllChecked(false);
			//getDataModel().setProperty(J2EE_COMPONENT_LIST, null);
			//IDataModel nestedModel = (IDataModel)getDataModel().getProperty(NESTED_ADD_COMPONENT_TO_EAR_DM);	
			//(nestedModel).setProperty(AddComponentToEnterpriseApplicationDataModelProvider., getCheckedJ2EEElementsAsList());
			getDataModel().setProperty(J2EE_COMPONENT_LIST, null);
			getDataModel().setProperty(JAVA_PROJECT_LIST, null);			
		} finally {
			ignoreCheckedState = false;
		}
	}

	/**
	 *  
	 */
	private void handleSelectAllButtonPressed() {
		ignoreCheckedState = true;
		try {
			moduleProjectsViewer.setAllChecked(true);
			//getDataModel().setProperty(J2EE_COMPONENT_LIST, getCheckedElementsAsList());
			//IDataModel nestedModel = (IDataModel)getDataModel().getProperty(NESTED_ADD_COMPONENT_TO_EAR_DM);
			//(nestedModel).setProperty(AddComponentToEnterpriseApplicationDataModelProvider., getCheckedJ2EEElementsAsList());
			
			getDataModel().setProperty(J2EE_COMPONENT_LIST, getCheckedJ2EEElementsAsList());
			getDataModel().setProperty(JAVA_PROJECT_LIST, getCheckedJavaProjectsAsList());
			
		} finally {
			ignoreCheckedState = false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.wst.common.frameworks.internal.ui.wizard.J2EEWizardPage#enter()
	 */
	protected void enter() {
		IWorkspaceRoot input = ResourcesPlugin.getWorkspace().getRoot();
		moduleProjectsViewer.setInput(input);
		super.enter();
	}

}
