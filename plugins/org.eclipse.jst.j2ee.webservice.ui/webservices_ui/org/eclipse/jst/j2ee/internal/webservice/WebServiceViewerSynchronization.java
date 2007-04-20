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
package org.eclipse.jst.j2ee.internal.webservice;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jst.j2ee.internal.webservice.helper.WebServiceEvent;
import org.eclipse.jst.j2ee.internal.webservice.helper.WebServiceManagerListener;
import org.eclipse.jst.j2ee.internal.webservice.helper.WebServicesManager;
import org.eclipse.jst.j2ee.internal.webservice.plugin.WebServiceUIPlugin;
import org.eclipse.jst.j2ee.navigator.internal.IJ2EENavigatorConstants;
import org.eclipse.jst.j2ee.navigator.internal.NonConflictingRule;
import org.eclipse.ui.progress.UIJob;
import org.eclipse.wst.common.internal.emfworkbench.integration.DynamicAdapterFactory;
import org.eclipse.wst.common.project.facet.core.internal.FacetedProjectPropertyTester;

public class WebServiceViewerSynchronization implements WebServiceManagerListener{
	

	private WebServicesManager webServicesManager = null; 
	
    private static final String PROJECT_FACET = "projectFacet"; //$NON-NLS-1$     
    
    /* The facets are duplicated here to avoid loading plugins just for constants */
    /* package */ static final String EAR_FACET = "jst.ear"; //$NON-NLS-1$ 
    /* package */ static final String APPCLIENT_FACET = "jst.appclient"; //$NON-NLS-1$ 
    /* package */ static final String WEB_FACET = "jst.web"; //$NON-NLS-1$ 
    /* package */ static final String EJB_FACET = "jst.ejb"; //$NON-NLS-1$ 
    /* package */ static final String UTILITY_FACET = "jst.utility"; //$NON-NLS-1$ 
    /* package */ static final String CONNECTOR_FACET = "jst.connector"; //$NON-NLS-1$ 
    /* package */ static final String STATIC_WEB_FACET = "wst.web"; //$NON-NLS-1$ 
    
    private static final FacetedProjectPropertyTester facetPropertyTester = new FacetedProjectPropertyTester();

    
    private WebServicesNavigatorContentProvider contentProvider;

	private Job indexJob = new WebServiceIndexJob();
	private Job updateJob = new UpdateWebServicesNodeUIJob(); 
	private Job removeJob = new RemoveWebServicesNodeUIJob(); 

	private boolean navigatorGroupAdded = false;
	private boolean indexJobScheduled = false;
	
	private boolean initialized = false;
	
	public WebServiceViewerSynchronization(WebServicesNavigatorContentProvider provider) {
		contentProvider = provider; 
	}
	

	public void start() {

		try { 
			
			getWebServicesManager().addListener(this);
			
			// create the default synchronizer for any web service editor to use with view due
			// to the usage of seperate edit models.
			WebServicesNavigatorSynchronizer.createInstance(
							new DynamicAdapterFactory(IJ2EENavigatorConstants.VIEWER_ID), contentProvider);
		} finally { 
			initialized = true;
		} 
	}
	 
	public void stop() { 
		if(initialized) {
			getWebServicesManager().removeListener(this);
			// dispose current instance of web service editor/explorer synchronizer
			WebServicesNavigatorSynchronizer.disposeInstance();
		}
	}
	
	public void webServiceManagerChanged(WebServiceEvent anEvent) {
		
		switch (anEvent.getEventType()) {
			case WebServiceEvent.REFRESH:

				if(!hasNavigatorGroupBeenAdded()) {
					if(!hasIndexJobBeenScheduled()){
						indexJob.schedule();
					}
					if(!hasNavigatorGroupBeenAdded())
						new AddWebServicesNodeUIJob().schedule();
				} else {
					updateJob.schedule();
				}
				break;
			case WebServiceEvent.REMOVE:
				removeJob.schedule();
		}
	}


	public void startIndexJob() {
		indexJob.schedule();
	} 
	
	/**
	 * Employ a Test-And-Set (TST) primitive to ensure the Job is only scheduled once per load.
	 * 
	 * @return True if the the index job has been scheduled. The value of indexJobSchedule will
	 *         _always_ be true after this method executes, so if false is returned, the job must be
	 *         scheduled by the caller.
	 */
	/* package */ synchronized boolean hasIndexJobBeenScheduled() {
		if (!indexJobScheduled) {
			indexJobScheduled = true;
			return false;
		}
		return true;
	}

	/**
	 * Multiple threads access this boolean flag, so we synchronize it to ensure that its value is
	 * consistent across different threads.
	 * 
	 * @return True if the WebServicesNavigatorGroup has already been processed and added to the
	 *         tree.
	 */
	/* package */ synchronized boolean hasNavigatorGroupBeenAdded() {
		return navigatorGroupAdded;
	} 
	
	/* package */ synchronized void setNavigatorGroupAdded(boolean hasBeenAdded) {
		navigatorGroupAdded = hasBeenAdded;
	}

	private WebServicesManager getWebServicesManager() {
		if (webServicesManager == null)
			webServicesManager = WebServicesManager.getInstance();
		return webServicesManager;
	}
	
	private boolean indexWebServices(IProgressMonitor monitor) {
		boolean hasChildren = false;
		if (!monitor.isCanceled()) {
			try {
				hasChildren |= getWebServicesManager().getWorkspace13ServiceRefs().size() > 0;
			} catch (RuntimeException e) { 
				WebServiceUIPlugin.logError(0, e.getMessage(), e);
			}
		} else {
			return hasChildren;
		}
		monitor.worked(1);

		if (!monitor.isCanceled()) {
			try {
				hasChildren |= getWebServicesManager().getWorkspace14ServiceRefs().size() > 0;
			} catch (RuntimeException e) { 
				WebServiceUIPlugin.logError(0, e.getMessage(), e);
			}
		} else {
			return hasChildren;
		}
		monitor.worked(1);

		if (!monitor.isCanceled()) {
			try {
				hasChildren |= getWebServicesManager().getInternalWSDLServices().size() > 0;
			} catch (RuntimeException e) { 
				WebServiceUIPlugin.logError(0, e.getMessage(), e);
			}
		} else {
			return hasChildren;
		}
		monitor.worked(1);

		if (!monitor.isCanceled()) {
			try {
				hasChildren |= getWebServicesManager().getExternalWSDLServices().size() > 0;
			} catch (RuntimeException e) { 
				WebServiceUIPlugin.logError(0, e.getMessage(), e);
			}
		} else {
			return hasChildren;
		}
		monitor.worked(1);
		return hasChildren;
	}
	
	/* package */ static boolean hasFacet(Object element, String facet) {
		return facetPropertyTester.test(element, PROJECT_FACET, new Object[] {}, facet);
	}
	
	/* package */ boolean webServiceProjectsExist(IProgressMonitor monitor) { 	
		boolean ret = false;
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		
		monitor.beginTask("Searching for web service capable projects...", projects.length);
		for (int i = 0; i < projects.length; i++) {
			 if(isInteresting(projects[i])){
				 ret = true;
				 break;
			 }	 
		}
		monitor.worked(1);
		return ret;
	}

	/* package */ static boolean isInteresting(IProject project) {
		return hasFacet(project, WEB_FACET) || 
			hasFacet(project, EJB_FACET) || 
			hasFacet(project, APPCLIENT_FACET);
	}

	public class WebServiceIndexJob extends Job {
	
		public WebServiceIndexJob() {
			super(WebServiceUIResourceHandler.WS_NAV_JOB0);
			setRule(new NonConflictingRule());
		}

		protected IStatus run(IProgressMonitor monitor) {
			monitor.beginTask(WebServiceUIResourceHandler.WS_NAV_JOB1, 5);
  
			if (webServiceProjectsExist(monitor))
					indexWebServices(monitor);
			
			monitor.done();
			
			return Status.OK_STATUS;
		}
	}

	public class AddWebServicesNodeUIJob extends UIJob {


		public AddWebServicesNodeUIJob() {
			super(WebServiceUIResourceHandler.WS_NAV_JOB2);
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {

			TreeViewer viewer = contentProvider.getViewer();
			if(!viewer.getControl().isDisposed()) {
				viewer.add(viewer.getInput(), contentProvider.getNavigatorGroup());
			}
			setNavigatorGroupAdded(true);
			return Status.OK_STATUS;
		} 
	}

	public class UpdateWebServicesNodeUIJob extends UIJob {
 
		public UpdateWebServicesNodeUIJob () {
			super(WebServiceUIResourceHandler.WS_NAV_JOB3);
		}

		public IStatus runInUIThread(IProgressMonitor monitor) {
			TreeViewer viewer = contentProvider.getViewer();

			if(!viewer.getControl().isDisposed()) {
				if(hasNavigatorGroupBeenAdded())
					contentProvider.getViewer().refresh(contentProvider.getNavigatorGroup());
				else {
					viewer.add(viewer.getInput(), contentProvider.getNavigatorGroup());
					setNavigatorGroupAdded(true);
				}
			}
			return Status.OK_STATUS;
		} 
	}
	 
	public class RemoveWebServicesNodeUIJob extends UIJob { 

		public RemoveWebServicesNodeUIJob() {
			super(WebServiceUIResourceHandler.WS_NAV_JOB4);
		}

		public IStatus runInUIThread(IProgressMonitor monitor) { 

			monitor.beginTask(WebServiceUIResourceHandler.WS_NAV_JOB5, 4);

			TreeViewer viewer = contentProvider.getViewer();

			if(!viewer.getControl().isDisposed()) {
				if (indexWebServices(monitor)) {
					viewer.refresh(contentProvider.getNavigatorGroup());
				} else {
					viewer.remove(contentProvider.getNavigatorGroup());
					setNavigatorGroupAdded(false);
				}
			}
			return Status.OK_STATUS;
		} 
	}


}