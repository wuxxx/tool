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

import java.io.File;

import org.eclipse.jst.j2ee.internal.plugin.J2EEUIMessages;
import org.eclipse.jst.j2ee.project.datamodel.properties.IFlexibleJavaProjectCreationDataModelProperties;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelSynchHelper;

public class NewFlexibleProjectGroup implements IFlexibleJavaProjectCreationDataModelProperties{
	private IDataModel model;
	public Text projectNameField = null;
	protected Text locationPathField = null;
	protected Button browseButton = null;
	//	constants
	private static final int SIZING_TEXT_FIELD_WIDTH = 305;
	//	default values
	private String defProjectNameLabel = J2EEUIMessages.getResourceString(J2EEUIMessages.NAME_LABEL); //$NON-NLS-1$
	private String defBrowseButtonLabel = J2EEUIMessages.getResourceString(J2EEUIMessages.BROWSE_LABEL); //$NON-NLS-1$
	private static final String defDirDialogLabel = "Directory"; //$NON-NLS-1$

	private DataModelSynchHelper synchHelper;

	/**
	 * @param parent
	 * @param style
	 */
	public NewFlexibleProjectGroup(Composite parent, int style, IDataModel model, DataModelSynchHelper aHelper) {
		this.model = model;
		synchHelper = aHelper;
		buildComposites(parent);
	}

	/**
	 * Create the controls within this composite
	 */
	public void buildComposites(Composite parent) {
		createProjectNameGroup(parent);
		createProjectLocationGroup(parent);
		projectNameField.setFocus();
	}

	/**
	 *  
	 */
	private void createProjectNameGroup(Composite parent) {
		// set up project name label
		Label projectNameLabel = new Label(parent, SWT.NONE);
		projectNameLabel.setText(defProjectNameLabel);
		GridData data = new GridData();
		projectNameLabel.setLayoutData(data);
		// set up project name entry field
		projectNameField = new Text(parent, SWT.BORDER);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		projectNameField.setLayoutData(data);
		new Label(parent, SWT.NONE); // pad
		synchHelper.synchText(projectNameField, PROJECT_NAME, new Control[]{projectNameLabel});
	}

	/**
	 *  
	 */
	private void createProjectLocationGroup(Composite parent) {
		//		set up location path label
		Label locationPathLabel = new Label(parent, SWT.NONE);
		locationPathLabel.setText(J2EEUIMessages.getResourceString(J2EEUIMessages.PROJECT_LOC_LBL));
		GridData data = new GridData();
		locationPathLabel.setLayoutData(data);
		// set up location path entry field
		locationPathField = new Text(parent, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = SIZING_TEXT_FIELD_WIDTH;
		locationPathField.setLayoutData(data);
		// set up browse button
		browseButton = new Button(parent, SWT.PUSH);
		browseButton.setText(defBrowseButtonLabel);
		browseButton.setLayoutData((new GridData(GridData.FILL_HORIZONTAL)));
		browseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleLocationBrowseButtonPressed();
			}
		});
		browseButton.setEnabled(true);
		synchHelper.synchText(locationPathField, PROJECT_LOCATION, null);
	}

	/**
	 * Open an appropriate directory browser
	 */
	protected void handleLocationBrowseButtonPressed() {
		DirectoryDialog dialog = new DirectoryDialog(locationPathField.getShell());
		dialog.setMessage(defDirDialogLabel);
		String dirName = model.getStringProperty(PROJECT_LOCATION);
		if ((dirName != null) && (dirName.length() != 0)) {
			File path = new File(dirName);
			if (path.exists()) {
				dialog.setFilterPath(dirName);
			}
		}
		String selectedDirectory = dialog.open();
		if (selectedDirectory != null) {
			model.setProperty(PROJECT_LOCATION, selectedDirectory);
		}
	}

	public void dispose() {
		model.removeListener(synchHelper);
		model = null;
	}
}