/*******************************************************************************
 * Copyright (c) 2003, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * IBM Corporation - initial API and implementation
 *******************************************************************************/ 
package org.eclipse.jst.j2ee.navigator.internal;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.jst.j2ee.internal.navigator.ui.Messages;

public class ClearPlaceHolderJob extends UIJob {

	private AbstractTreeViewer viewer;
	private LoadingDDNode placeHolder;
	private Object[] children;
	private Object parent;

	public ClearPlaceHolderJob(AbstractTreeViewer viewer, LoadingDDNode placeHolder, Object parent, Object[] children) {
		super(Messages.ClearPlaceHolderJob_Removing_place_holder_);
		this.viewer = viewer;
		this.placeHolder = placeHolder; 
		this.parent = parent;
		this.children = children;
		setRule(new NonConflictingRule());
	}
	
	public IStatus runInUIThread(IProgressMonitor monitor) {

		viewer.remove(placeHolder);
		viewer.add(parent, children);
		return Status.OK_STATUS;
	}

}
