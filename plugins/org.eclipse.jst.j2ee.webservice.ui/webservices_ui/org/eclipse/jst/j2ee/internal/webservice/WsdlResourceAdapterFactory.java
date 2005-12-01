/*******************************************************************************
 * Copyright (c) 2003, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/
/*
 * Created on Mar 5, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package org.eclipse.jst.j2ee.internal.webservice;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jst.j2ee.internal.webservices.WSDLServiceExtManager;
import org.eclipse.jst.j2ee.internal.webservices.WSDLServiceHelper;
import org.eclipse.wst.common.internal.emfworkbench.EMFAdapterFactory;
import org.eclipse.wst.common.internal.emfworkbench.WorkbenchResourceHelper;

/**
 * @author jlanuti
 * 
 * To change the template for this generated type comment go to Window - Preferences - Java - Code
 * Generation - Code and Comments
 */
public class WsdlResourceAdapterFactory extends EMFAdapterFactory {

	/**
	 * Default constructor
	 */
	public WsdlResourceAdapterFactory() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.runtime.IAdapterFactory#getAdapter(java.lang.Object, java.lang.Class)
	 */
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		Resource res = null;
		WSDLServiceHelper serviceHelper = WSDLServiceExtManager.getServiceHelper();
		if (serviceHelper.isWSDLResource(adaptableObject))
			res = (Resource) adaptableObject;

		if (res != null && adapterType == EMFAdapterFactory.IFILE_CLASS)
			return WorkbenchResourceHelper.getFile(res) != null ? WorkbenchResourceHelper.getFile(res) : null;
		else if (res != null && adapterType == EMFAdapterFactory.IRESOURCE_CLASS)
			return WorkbenchResourceHelper.getFile(res);
		else if (res != null && adapterType == EMFAdapterFactory.IPROJECT_CLASS)
			return ProjectUtilities.getProject(res);
		else
			return super.getAdapter(adaptableObject, adapterType);
	}
}
