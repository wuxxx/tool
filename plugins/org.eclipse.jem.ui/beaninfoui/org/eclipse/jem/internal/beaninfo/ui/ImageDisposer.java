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
 *  $RCSfile: ImageDisposer.java,v $
 *  $Revision: 1.1 $  $Date: 2005/09/26 20:26:59 $ 
 */
package org.eclipse.jem.internal.beaninfo.ui;

import org.eclipse.jface.util.Assert;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
 
/**
 * 
 * 
 * @since 1.2.0
 */
public class ImageDisposer implements DisposeListener {
	
	private Image[] fImages;
		
	public ImageDisposer(Image image) {
		this(new Image[] { image });
	}
	
	public ImageDisposer(Image[] images) {
		Assert.isNotNull(images);
		fImages= images;		
	}
	
	/*
	 * @see WidgetListener#widgetDisposed
	 */
	public void widgetDisposed(DisposeEvent e) {
		if (fImages != null) {
			for (int i= 0; i < fImages.length; i++) {
				fImages[i].dispose();
			}
		}
	}
}