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
package org.eclipse.jst.j2ee.internal.webservice.provider;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.edit.provider.ChangeNotifier;
import org.eclipse.emf.edit.provider.ComposeableAdapterFactory;
import org.eclipse.emf.edit.provider.ComposedAdapterFactory;
import org.eclipse.emf.edit.provider.IChangeNotifier;
import org.eclipse.emf.edit.provider.IEditingDomainItemProvider;
import org.eclipse.emf.edit.provider.IItemLabelProvider;
import org.eclipse.emf.edit.provider.IItemPropertySource;
import org.eclipse.emf.edit.provider.INotifyChangedListener;
import org.eclipse.emf.edit.provider.IStructuredItemContentProvider;
import org.eclipse.emf.edit.provider.ITreeItemContentProvider;
import org.eclipse.jst.j2ee.internal.wsdd.provider.HandlerItemProvider;
import org.eclipse.jst.j2ee.webservice.wsdd.internal.util.WsddAdapterFactory;


/**
 * This is the factory that is used to provide the interfaces needed to support Viewers. The
 * adapters generated by this factory convert EMF adapter notifications into calls to
 * {@link #fireNotifyChanged fireNotifyChanged}. The adapters also support Eclipse property sheets.
 * Note that most of the adapters are shared among multiple instances. <!-- begin-user-doc --> <!--
 * end-user-doc -->
 * 
 * @generated
 */
public class WsddItemProviderAdapterFactory extends WsddAdapterFactory implements ComposeableAdapterFactory, IChangeNotifier {
	/**
	 * This keeps track of the root adapter factory that delegates to this adapter factory. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ComposedAdapterFactory parentAdapterFactory;

	/**
	 * This is used to implement {@link org.eclipse.emf.edit.provider.IChangeNotifier}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected IChangeNotifier changeNotifier = new ChangeNotifier();

	/**
	 * This keeps track of all the supported types checked by
	 * {@link #isFactoryForType isFactoryForType}. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected Collection supportedTypes = new ArrayList();

	/**
	 * This constructs an instance. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public WsddItemProviderAdapterFactory() {
		supportedTypes.add(IStructuredItemContentProvider.class);
		supportedTypes.add(ITreeItemContentProvider.class);
		supportedTypes.add(IItemPropertySource.class);
		supportedTypes.add(IEditingDomainItemProvider.class);
		supportedTypes.add(IItemLabelProvider.class);
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.WebServices}instances. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected WebServicesItemProvider webServicesItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.WebServices}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Adapter createWebServicesAdapter() {
		if (webServicesItemProvider == null) {
			webServicesItemProvider = new WebServicesItemProvider(this);
		}

		return webServicesItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.WebServiceDescription}instances. <!-- begin-user-doc
	 * --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected WebServiceDescriptionItemProvider webServiceDescriptionItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.WebServiceDescription}.
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Adapter createWebServiceDescriptionAdapter() {
		if (webServiceDescriptionItemProvider == null) {
			webServiceDescriptionItemProvider = new WebServiceDescriptionItemProvider(this);
		}

		return webServiceDescriptionItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.PortComponent}instances. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected PortComponentItemProvider portComponentItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.PortComponent}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Adapter createPortComponentAdapter() {
		if (portComponentItemProvider == null) {
			portComponentItemProvider = new PortComponentItemProvider(this);
		}

		return portComponentItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.WSDLPort}instances. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected WSDLPortItemProvider wsdlPortItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.WSDLPort}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Adapter createWSDLPortAdapter() {
		if (wsdlPortItemProvider == null) {
			wsdlPortItemProvider = new WSDLPortItemProvider(this);
		}

		return wsdlPortItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.ServiceImplBean}instances. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	protected ServiceImplBeanItemProvider serviceImplBeanItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.ServiceImplBean}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Adapter createServiceImplBeanAdapter() {
		if (serviceImplBeanItemProvider == null) {
			serviceImplBeanItemProvider = new ServiceImplBeanItemProvider(this);
		}

		return serviceImplBeanItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.ServletLink}instances. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected ServletLinkItemProvider servletLinkItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.ServletLink}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Adapter createServletLinkAdapter() {
		if (servletLinkItemProvider == null) {
			servletLinkItemProvider = new ServletLinkItemProvider(this);
		}

		return servletLinkItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.EJBLink}instances. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected EJBLinkItemProvider ejbLinkItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.EJBLink}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Adapter createEJBLinkAdapter() {
		if (ejbLinkItemProvider == null) {
			ejbLinkItemProvider = new EJBLinkItemProvider(this);
		}

		return ejbLinkItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.Handler}instances. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected HandlerItemProvider handlerItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.Handler}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Adapter createHandlerAdapter() {
		if (handlerItemProvider == null) {
			handlerItemProvider = new HandlerItemProvider(this);
		}

		return handlerItemProvider;
	}

	/**
	 * This keeps track of the one adapter used for all
	 * {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.BeanLink}instances. <!-- begin-user-doc --> <!--
	 * end-user-doc -->
	 * 
	 * @generated
	 */
	protected BeanLinkItemProvider beanLinkItemProvider;

	/**
	 * This creates an adapter for a {@link org.eclipse.jst.j2ee.internal.internal.webservice.wsdd.BeanLink}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Adapter createBeanLinkAdapter() {
		if (beanLinkItemProvider == null) {
			beanLinkItemProvider = new BeanLinkItemProvider(this);
		}

		return beanLinkItemProvider;
	}

	/**
	 * This returns the root adapter factory that contains this factory. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public ComposeableAdapterFactory getRootAdapterFactory() {
		return parentAdapterFactory == null ? this : parentAdapterFactory.getRootAdapterFactory();
	}

	/**
	 * This sets the composed adapter factory that contains this factory. <!-- begin-user-doc -->
	 * <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void setParentAdapterFactory(ComposedAdapterFactory parentAdapterFactory) {
		this.parentAdapterFactory = parentAdapterFactory;
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public boolean isFactoryForType(Object type) {
		return supportedTypes.contains(type) || super.isFactoryForType(type);
	}

	/**
	 * This implementation substitutes the factory itself as the key for the adapter. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Adapter adapt(Notifier notifier, Object type) {
		return super.adapt(notifier, this);
	}

	/**
	 * <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public Object adapt(Object object, Object type) {
		if (isFactoryForType(type)) {
			Object adapter = super.adapt(object, type);
			if (!(type instanceof Class) || (((Class) type).isInstance(adapter))) {
				return adapter;
			}
		}

		return null;
	}

	/**
	 * This adds a listener. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void addListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.addListener(notifyChangedListener);
	}

	/**
	 * This removes a listener. <!-- begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void removeListener(INotifyChangedListener notifyChangedListener) {
		changeNotifier.removeListener(notifyChangedListener);
	}

	/**
	 * This delegates to {@link #changeNotifier}and to {@link #parentAdapterFactory}. <!--
	 * begin-user-doc --> <!-- end-user-doc -->
	 * 
	 * @generated
	 */
	public void fireNotifyChanged(Notification notification) {
		changeNotifier.fireNotifyChanged(notification);

		if (parentAdapterFactory != null) {
			parentAdapterFactory.fireNotifyChanged(notification);
		}
	}

}
