/*******************************************************************************
 * Copyright (c) 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 14, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.eclipse.jst.j2ee.internal.wizard;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jst.j2ee.application.internal.operations.FlexibleJavaProjectCreationDataModelProvider;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIPlugin;
import org.eclipse.jst.j2ee.internal.plugin.J2EEUIPluginIcons;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFlexibleProjectCreationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;

public class FlexibleProjectCreationWizard extends DataModelWizard implements INewWizard, IExecutableExtension, IFlexibleProjectCreationDataModelProperties { 

	/**
	 * <p>
	 * Constant used to identify the key of the main page of the Wizard.
	 * </p>
	 */
	protected static final String MAIN_PG = "main"; //$NON-NLS-1$
	private IStructuredSelection selection;
	private IConfigurationElement configurationElement;
	public static final String WIZARD_ID = FlexibleProjectCreationWizard.class.getName();

	/**
	 * <p>
	 * Creates a default instance of the wizard with no configuration data, no selection, and no
	 * operation data model.
	 * </p>
	 */
	public FlexibleProjectCreationWizard() {
		super();
	}

	/**
	 * <p>
	 * The model is used to prepopulate wizard controls and to collect data from the user. The model
	 * will eventually be used to run the operation, if the user does not cancel the Wizard.
	 * </p>
	 * 
	 * @param model
	 *            used to collect information and interface with the WTP Operation
	 */
	public FlexibleProjectCreationWizard(IDataModel model) {
		super(model);
        setWindowTitle(J2EEUIMessages.getResourceString(J2EEUIMessages.FLEXIBLE_PROJECT_WIZ_TITLE));
        setDefaultPageImageDescriptor(J2EEUIPlugin.getDefault().getImageDescriptor(J2EEUIPluginIcons.EAR_WIZ_BANNER));
        setHelpAvailable(false);
	}

	/**
	 * <p>
	 * The selection is used to pre-populate values in the Wizard dialog controls.
	 * </p>
	 * 
	 * @see org.eclipse.ui.IWorkbenchWizard#init(org.eclipse.ui.IWorkbench,
	 *      org.eclipse.jface.viewers.IStructuredSelection)
	 * 
	 * @param workbench
	 *            the current workbench parent of the wizard
	 * @param aSelection
	 *            the selection from the view used to start the wizard (if any)
	 */
	public final void init(IWorkbench workbench, IStructuredSelection aSelection) {
		setNeedsProgressMonitor(true);
		this.selection = aSelection;
	}

	/**
	 * {@inheritDoc}
	 * 
	 * <p>
	 * The configuration element is saved to use when the wizard completes in order to change the
	 * current perspective using either (1) the value specified by {@link #getFinalPerspectiveID()}
	 * or (2) the value specified by the finalPerspective attribute in the Wizard's configuration
	 * element.
	 * </p>
	 * 
	 * @see org.eclipse.core.runtime.IExecutableExtension#setInitializationData(org.eclipse.core.runtime.IConfigurationElement,
	 *      java.lang.String, java.lang.Object)
	 */
	public final void setInitializationData(IConfigurationElement aConfigurationElement, String aPropertyName, Object theData) throws CoreException {
		configurationElement = aConfigurationElement;
//		doSetInitializeData(aConfigurationElement, aPropertyName, theData);

	}

	/**
	 * <p>
	 * Returns the value specified by {@link #getWizardId()}
	 * </p>
	 * 
	 * @return Returns the an id component used for Activity filtering.
	 */
	public final String getLocalId() {
		return getWizardID();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @see org.eclipse.jface.wizard.Wizard#addPages()
	 */
	protected void doAddPages() {
		addPage(new FlexibleProjectCreationWizardPage(getDataModel(), MAIN_PG));
	}

    protected IDataModelProvider getDefaultProvider() {
        return new FlexibleJavaProjectCreationDataModelProvider();
    }
}