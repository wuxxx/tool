/*******************************************************************************
 * Copyright (c) 2005, 2007 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.eclipse.jst.j2ee.internal.archive;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ToolFactory;
import org.eclipse.jdt.core.util.IAnnotation;
import org.eclipse.jdt.core.util.IClassFileAttribute;
import org.eclipse.jdt.core.util.IClassFileReader;
import org.eclipse.jdt.core.util.IRuntimeVisibleAnnotationsAttribute;
import org.eclipse.jst.j2ee.internal.J2EEConstants;
import org.eclipse.jst.j2ee.internal.J2EEVersionConstants;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.jst.jee.archive.ArchiveModelLoadException;
import org.eclipse.jst.jee.archive.ArchiveOpenFailureException;
import org.eclipse.jst.jee.archive.ArchiveOptions;
import org.eclipse.jst.jee.archive.IArchive;
import org.eclipse.jst.jee.archive.IArchiveFactory;
import org.eclipse.jst.jee.archive.IArchiveLoadAdapter;
import org.eclipse.jst.jee.archive.IArchiveResource;
import org.eclipse.jst.jee.archive.internal.ArchiveFactoryImpl;
import org.eclipse.jst.jee.archive.internal.ArchiveImpl;
import org.eclipse.jst.jee.archive.internal.ArchiveUtil;
import org.eclipse.jst.jee.util.internal.JavaEEQuickPeek;
import org.eclipse.wst.common.componentcore.internal.resources.VirtualArchiveComponent;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

public class JavaEEArchiveUtilities extends ArchiveFactoryImpl implements IArchiveFactory {

	/**
	 * Default value = Boolean.TRUE Valid values = Boolean.TRUE or Boolean.FALSE
	 * 
	 * An ArchiveOption used to specify whether
	 * {@link #openArchive(ArchiveOptions)} should attempt to discriminate
	 * between different Java EE archive types. The default behavior is to
	 * always discriminate fully for all types except EJB 3.0 archives
	 * {@link #DISCRIMINATE_EJB_ANNOTATIONS}. In order to fully discriminate
	 * EJB 3.0 archives, it is necessary to set both this flag and
	 * {@link #DISCRIMINATE_EJB_ANNOTATIONS} to true.
	 */
	public static final String DISCRIMINATE_JAVA_EE = "DISCRIMINATE_EJB"; //$NON-NLS-1$

	/**
	 * Default value = Boolean.TRUE Valid values = Boolean.TRUE or Boolean.FALSE
	 * 
	 * An ArchiveOption used to specify whether
	 * {@link #openArchive(ArchiveOptions)} should attempt to fully discriminate
	 * a JAR file from an EJB JAR file. This option is only relevant if the
	 * {@link #DISCRIMINATE_JAVA_EE} option is also set to Boolean.TRUE. If both
	 * options are set to true then as a last resort all .class files byte codes
	 * will be analyzed for EJB annotations in order to discriminate whether the
	 * specified IArchive is an EJB 3.0 jar.
	 */
	public static final String DISCRIMINATE_EJB_ANNOTATIONS = "DISCRIMINATE_EJB_ANNOTATIONS"; //$NON-NLS-1$


	private JavaEEArchiveUtilities() {
	}

	public static JavaEEArchiveUtilities INSTANCE = new JavaEEArchiveUtilities();

	public static final String DOT_JAVA = ".java"; //$NON-NLS-1$

	public static final String DOT_CLASS = ".class"; //$NON-NLS-1$

	public static boolean isJava(IFile iFile) {
		return hasExtension(iFile, DOT_JAVA);
	}

	public static boolean isClass(IFile iFile) {
		return hasExtension(iFile, DOT_CLASS);
	}

	public static boolean hasExtension(IFile iFile, String ext) {
		String name = iFile.getName();
		return hasExtension(name, ext);
	}

	public static boolean hasExtension(String name, String ext) {
		int offset = ext.length();
		return name.regionMatches(true, name.length() - offset, ext, 0, offset);
	}

	public IArchive openArchive(IVirtualComponent virtualComponent) throws ArchiveOpenFailureException {
		if (virtualComponent.isBinary()) {
			VirtualArchiveComponent archiveComponent = (VirtualArchiveComponent) virtualComponent;
			java.io.File diskFile = null;
			diskFile = archiveComponent.getUnderlyingDiskFile();
			if (!diskFile.exists()) {
				IFile wbFile = archiveComponent.getUnderlyingWorkbenchFile();
				diskFile = new File(wbFile.getLocation().toOSString());
			}
			IPath path = new Path(diskFile.getAbsolutePath());
			return openArchive(path);
		}
		int type = J2EEVersionConstants.UNKNOWN;
		IArchiveLoadAdapter archiveLoadAdapter = null;
		if (J2EEProjectUtilities.isEARProject(virtualComponent.getProject())) {
			archiveLoadAdapter = new EARComponentArchiveLoadAdapter(virtualComponent);
			type = J2EEVersionConstants.APPLICATION_TYPE;
		} else if (J2EEProjectUtilities.isEJBComponent(virtualComponent)) {
			archiveLoadAdapter = new EJBComponentArchiveLoadAdapter(virtualComponent);
			type = J2EEVersionConstants.EJB_TYPE;
		} else if (J2EEProjectUtilities.isApplicationClientComponent(virtualComponent)) {
			archiveLoadAdapter = new AppClientComponentArchiveLoadAdapter(virtualComponent);
			type = J2EEVersionConstants.APPLICATION_CLIENT_TYPE;
		} else if (J2EEProjectUtilities.isJCAComponent(virtualComponent)) {
			archiveLoadAdapter = new ConnectorComponentArchiveLoadAdapter(virtualComponent);
			type = J2EEVersionConstants.CONNECTOR_TYPE;
		} else if (J2EEProjectUtilities.isDynamicWebComponent(virtualComponent)) {
			archiveLoadAdapter = new WebComponentArchiveLoadAdapter(virtualComponent);
			type = J2EEVersionConstants.WEB_TYPE;
		} else if (J2EEProjectUtilities.isUtilityProject(virtualComponent.getProject())) {
			archiveLoadAdapter = new JavaComponentArchiveLoadAdapter(virtualComponent);
		}

		if (archiveLoadAdapter != null) {
			ArchiveOptions options = new ArchiveOptions();
			options.setOption(ArchiveOptions.LOAD_ADAPTER, archiveLoadAdapter);
			IArchive archive = super.openArchive(options);
			if (type != J2EEVersionConstants.UNKNOWN) {
				int version = J2EEVersionConstants.UNKNOWN;
				String versionStr = J2EEProjectUtilities.getJ2EEProjectVersion(virtualComponent.getProject());
				switch (type) {
				case J2EEVersionConstants.APPLICATION_CLIENT_TYPE:
				case J2EEVersionConstants.APPLICATION_TYPE:
					if (versionStr.equals(J2EEVersionConstants.VERSION_1_2_TEXT)) {
						version = J2EEVersionConstants.J2EE_1_2_ID;
					} else if (versionStr.equals(J2EEVersionConstants.VERSION_1_3_TEXT)) {
						version = J2EEVersionConstants.J2EE_1_3_ID;
					} else if (versionStr.equals(J2EEVersionConstants.VERSION_1_4_TEXT)) {
						version = J2EEVersionConstants.J2EE_1_4_ID;
					} else if (versionStr.equals(J2EEVersionConstants.VERSION_5_0_TEXT)) {
						version = J2EEVersionConstants.JEE_5_0_ID;
					}
					break;
				case J2EEVersionConstants.CONNECTOR_TYPE:
					if (versionStr.equals(J2EEVersionConstants.VERSION_1_0_TEXT)) {
						version = J2EEVersionConstants.JCA_1_0_ID;
					} else if (versionStr.equals(J2EEVersionConstants.VERSION_1_5_TEXT)) {
						version = J2EEVersionConstants.JCA_1_5_ID;
					}
					break;
				case J2EEVersionConstants.EJB_TYPE:
					if (versionStr.equals(J2EEVersionConstants.VERSION_1_1_TEXT)) {
						version = J2EEVersionConstants.EJB_1_1_ID;
					} else if (versionStr.equals(J2EEVersionConstants.VERSION_2_0_TEXT)) {
						version = J2EEVersionConstants.EJB_2_0_ID;
					} else if (versionStr.equals(J2EEVersionConstants.VERSION_2_1_TEXT)) {
						version = J2EEVersionConstants.EJB_2_1_ID;
					} else if (versionStr.equals(J2EEVersionConstants.VERSION_3_0_TEXT)) {
						version = J2EEVersionConstants.EJB_3_0_ID;
					}
					break;
				case J2EEVersionConstants.WEB_TYPE:
					if (versionStr.equals(J2EEVersionConstants.VERSION_2_2_TEXT)) {
						version = J2EEVersionConstants.WEB_2_2_ID;
					} else if (versionStr.equals(J2EEVersionConstants.VERSION_2_3_TEXT)) {
						version = J2EEVersionConstants.WEB_2_3_ID;
					} else if (versionStr.equals(J2EEVersionConstants.VERSION_2_4_TEXT)) {
						version = J2EEVersionConstants.WEB_2_4_ID;
					} else if (versionStr.equals(J2EEVersionConstants.VERSION_2_5_TEXT)) {
						version = J2EEVersionConstants.WEB_2_5_ID;
					}
					break;
				}
				if (version != J2EEVersionConstants.UNKNOWN) {
					archiveToJavaEEQuickPeek.put(archive, new JavaEEQuickPeek(type, version));
				}
			}
			return archive;
		}
		return null;
	}

	private Map<IArchive, JavaEEQuickPeek> archiveToJavaEEQuickPeek = new WeakHashMap<IArchive, JavaEEQuickPeek>();

	/**
	 * Returns a utility for getting the type of Java EE archive, the Java EE version, and the
	 * Module version
	 * 
	 * @param archive
	 * @return
	 */
	public JavaEEQuickPeek getJavaEEQuickPeek(IArchive archive) {
		if (archiveToJavaEEQuickPeek.containsKey(archive)) {
			return archiveToJavaEEQuickPeek.get(archive);
		} else {
			String[] deploymentDescriptorsToCheck = new String[] { J2EEConstants.APPLICATION_DD_URI, J2EEConstants.APP_CLIENT_DD_URI, J2EEConstants.EJBJAR_DD_URI, J2EEConstants.WEBAPP_DD_URI,
					J2EEConstants.RAR_DD_URI };
			for (int i = 0; i < deploymentDescriptorsToCheck.length; i++) {
				final IPath deploymentDescriptorPath = new Path(deploymentDescriptorsToCheck[i]);
				if (archive.containsArchiveResource(deploymentDescriptorPath)) {
					InputStream in = null;
					IArchiveResource dd;
					try {
						dd = archive.getArchiveResource(deploymentDescriptorPath);
						in = dd.getInputStream();
						JavaEEQuickPeek quickPeek = new JavaEEQuickPeek(in);
						archiveToJavaEEQuickPeek.put(archive, quickPeek);
						return quickPeek;
					} catch (FileNotFoundException e) {
						ArchiveUtil.warn(e);
					} catch (IOException e) {
						ArchiveUtil.warn(e);
					}
				}
			}
			JavaEEQuickPeek quickPeek = new JavaEEQuickPeek(null);
			archiveToJavaEEQuickPeek.put(archive, quickPeek);
			return quickPeek;
		}
	}

	/**
	 * Returns an IArchive. This method will attempt to discriminate the
	 * specific Java EE archive type based on the following simple rules. Please
	 * note that these rules do not adhere exactly to the Java EE specification
	 * because they are written for a tooling environment rather than a runtime
	 * environment. Thus these rules attempt to compensate for user error with
	 * the understanding that other areas of the tooling environment will help
	 * detect and correct these errors.
	 * 
	 * <ol>
	 * <li> An archive containing a deployment descriptor is considered to be of
	 * that type </li>
	 * <li> An archive whose name ends with '.ear' is considered an EAR </li>
	 * <li> An archive whose name ends with '.war' is considered a WAR </li>
	 * <li> An archive whose name ends with '.jar' and which contains a
	 * META-INF/MANIFEST.MF file containing a Main-class attribute is considered
	 * an Application Client </li>
	 * <li> If the ArchiveOptions specify the
	 * {@link #DISCRIMINATE_EJB_ANNOTATIONS} as Boolean.TRUE then if the archive
	 * contains any .class file with EJB annotations it is considered an EJB
	 * JAR. Be warned that this full check does have performance implications
	 * and is not done by default.</li>
	 * An archive whose name ends with '.jar' is considered a Utility </li>
	 * </ol>
	 */
	public IArchive openArchive(ArchiveOptions archiveOptions) throws ArchiveOpenFailureException {
		IArchive simpleArchive = super.openArchive(archiveOptions);
		Object discriminateJavaEE = archiveOptions.getOption(DISCRIMINATE_JAVA_EE);
		if(discriminateJavaEE != null && !((Boolean)discriminateJavaEE).booleanValue()){
			return simpleArchive;
		}
		return refineForJavaEE(simpleArchive);
	}

	private static final String DOT_EAR = ".ear";//$NON-NLS-1$
	
	private static final String DOT_WAR = ".war";//$NON-NLS-1$
	
	private static final String DOT_JAR = ".jar";//$NON-NLS-1$
	
	private IArchive refineForJavaEE(final IArchive simpleArchive) {
		String[] deploymentDescriptorsToCheck = new String[] { J2EEConstants.APPLICATION_DD_URI, J2EEConstants.APP_CLIENT_DD_URI, J2EEConstants.EJBJAR_DD_URI, J2EEConstants.WEBAPP_DD_URI,
				J2EEConstants.RAR_DD_URI };
		int[] typeToVerify = new int[] { J2EEVersionConstants.APPLICATION_TYPE, J2EEVersionConstants.APPLICATION_CLIENT_TYPE, J2EEVersionConstants.EJB_TYPE, J2EEVersionConstants.WEB_TYPE,
				J2EEConstants.CONNECTOR_TYPE };
		for (int i = 0; i < deploymentDescriptorsToCheck.length; i++) {
			final IPath deploymentDescriptorPath = new Path(deploymentDescriptorsToCheck[i]);
			if (simpleArchive.containsArchiveResource(deploymentDescriptorPath)) {
				InputStream in = null;
				IArchiveResource dd;
				try {
					dd = simpleArchive.getArchiveResource(deploymentDescriptorPath);
					in = dd.getInputStream();
					JavaEEQuickPeek quickPeek = new JavaEEQuickPeek(in);
					if (quickPeek.getType() == typeToVerify[i] && quickPeek.getVersion() != JavaEEQuickPeek.UNKNOWN && !simpleArchive.containsModelObject(deploymentDescriptorPath)) {
						archiveToJavaEEQuickPeek.put(simpleArchive, quickPeek);
						wrapArchive(simpleArchive, deploymentDescriptorPath);
						return simpleArchive;
					}
				} catch (FileNotFoundException e) {
					ArchiveUtil.warn(e);
				} catch (IOException e) {
					ArchiveUtil.warn(e);
				} finally {
					if (in != null) {
						try {
							in.close();
						} catch (IOException e) {
							ArchiveUtil.warn(e);
						}
					}
				}
			}
		}
		IPath archivePath = simpleArchive.getPath();
		if(archivePath == null){
			Object obj = simpleArchive.getArchiveOptions().getOption(ArchiveOptions.ARCHIVE_PATH);
			if(null != obj){
				archivePath = (IPath)obj;
			}
		}
		
		if(archivePath != null){
			String lastSegment = archivePath.lastSegment().toLowerCase();
			if(lastSegment.endsWith(DOT_EAR)){
				JavaEEQuickPeek quickPeek = new JavaEEQuickPeek(JavaEEQuickPeek.APPLICATION_TYPE, JavaEEQuickPeek.JEE_5_0_ID, JavaEEQuickPeek.JEE_5_0_ID);
				archiveToJavaEEQuickPeek.put(simpleArchive, quickPeek);
				wrapArchive(simpleArchive, new Path(J2EEConstants.APPLICATION_DD_URI));
				return simpleArchive;
			} else if(lastSegment.endsWith(DOT_WAR)){
				JavaEEQuickPeek quickPeek = new JavaEEQuickPeek(JavaEEQuickPeek.WEB_TYPE, JavaEEQuickPeek.WEB_2_5_ID, JavaEEQuickPeek.JEE_5_0_ID);
				archiveToJavaEEQuickPeek.put(simpleArchive, quickPeek);
				wrapArchive(simpleArchive, new Path(J2EEConstants.WEBAPP_DD_URI));
				return simpleArchive;
			} else if(lastSegment.endsWith(DOT_JAR)){
				IPath manifestPath = new Path(J2EEConstants.MANIFEST_URI);
				if(simpleArchive.containsArchiveResource(manifestPath)){
					InputStream in = null;
					try{
						IArchiveResource manifestResource = simpleArchive.getArchiveResource(manifestPath);
						in = manifestResource.getInputStream();
						Manifest manifest = new Manifest(in);
						Attributes attributes = manifest.getMainAttributes();
						String mainClassName = attributes.getValue("Main-Class");
						if(mainClassName != null){
							JavaEEQuickPeek quickPeek = new JavaEEQuickPeek(JavaEEQuickPeek.APPLICATION_CLIENT_TYPE, JavaEEQuickPeek.JEE_5_0_ID, JavaEEQuickPeek.JEE_5_0_ID);
							archiveToJavaEEQuickPeek.put(simpleArchive, quickPeek);
							wrapArchive(simpleArchive, new Path(J2EEConstants.APPLICATION_DD_URI));
							return simpleArchive;
						}
					} catch (FileNotFoundException e) {
						ArchiveUtil.warn(e);
					} catch (IOException e) {
						ArchiveUtil.warn(e);
					} finally {
						if (in != null) {
							try {
								in.close();
							} catch (IOException e) {
								ArchiveUtil.warn(e);
							}
						}
					}
				}
				Object discriminateEJB30 = simpleArchive.getArchiveOptions().getOption(DISCRIMINATE_EJB_ANNOTATIONS);
				if(null == discriminateEJB30 || ((Boolean)discriminateEJB30).booleanValue()){
					if(isEJBArchive(simpleArchive)){
						JavaEEQuickPeek quickPeek = new JavaEEQuickPeek(JavaEEQuickPeek.EJB_TYPE, JavaEEQuickPeek.EJB_3_0_ID, JavaEEQuickPeek.JEE_5_0_ID);
						archiveToJavaEEQuickPeek.put(simpleArchive, quickPeek);
						wrapArchive(simpleArchive, new Path(J2EEConstants.EJBJAR_DD_URI));
						return simpleArchive;
					}
				}
				
			}
		}
		
		return simpleArchive;
	}

	private static void wrapArchive(final IArchive simpleArchive, final IPath deploymentDescriptorPath) {
		final IArchiveLoadAdapter simpleLoadAdapter = simpleArchive.getLoadAdapter();

		IArchiveLoadAdapter wrappingEMFLoadAdapter = new IArchiveLoadAdapter() {
			private JavaEEEMFArchiveAdapterHelper emfHelper = new JavaEEEMFArchiveAdapterHelper(simpleArchive);

			public void close() {
				simpleLoadAdapter.close();
			}

			public boolean containsArchiveResource(IPath resourcePath) {
				return simpleLoadAdapter.containsArchiveResource(resourcePath);
			}

			public boolean containsModelObject(IPath modelObjectPath) {
				if (simpleLoadAdapter.containsArchiveResource(modelObjectPath)) {
					return true;
				}
				if (IArchive.EMPTY_MODEL_PATH == modelObjectPath) {
					modelObjectPath = deploymentDescriptorPath;
				}
				return emfHelper.containsModelObject(modelObjectPath);
			}

			public IArchiveResource getArchiveResource(IPath resourcePath) throws FileNotFoundException {
				return simpleLoadAdapter.getArchiveResource(resourcePath);
			}

			public List<IArchiveResource> getArchiveResources() {
				return simpleLoadAdapter.getArchiveResources();
			}

			public InputStream getInputStream(IArchiveResource archiveResource) throws IOException, FileNotFoundException {
				return simpleLoadAdapter.getInputStream(archiveResource);
			}

			public Object getModelObject(IPath modelObjectPath) throws ArchiveModelLoadException {
				if (simpleLoadAdapter.containsModelObject(modelObjectPath)) {
					return simpleLoadAdapter.getModelObject(modelObjectPath);
				}
				if (IArchive.EMPTY_MODEL_PATH == modelObjectPath) {
					modelObjectPath = deploymentDescriptorPath;
				}
				return emfHelper.getModelObject(modelObjectPath);
			}

			public IArchive getArchive() {
				return simpleLoadAdapter.getArchive();
			}

			public void setArchive(IArchive archive) {
				simpleLoadAdapter.setArchive(archive);
			}

			public String toString() {
				StringBuffer buffer = new StringBuffer(JavaEEArchiveUtilities.class.getName());
				buffer.append(" wrapping: ");
				buffer.append(simpleLoadAdapter.toString());
				return buffer.toString();
			}
		};
		simpleArchive.getArchiveOptions().setOption(ArchiveOptions.LOAD_ADAPTER, wrappingEMFLoadAdapter);
		((ArchiveImpl) simpleArchive).setLoadAdapter(wrappingEMFLoadAdapter);
	}

	private static final char[] RUNTIME_VISIBLE = "RuntimeVisibleAnnotations".toCharArray(); //$NON-NLS-1$

	private static final char[] STATELESS = "Ljavax/ejb/Stateless;".toCharArray();//$NON-NLS-1$

	private static final char[] STATEFUL = "Ljavax/ejb/Stateful;".toCharArray();//$NON-NLS-1$

	private static final char[] MESSAGEDRIVEN = "Ljavax/ejb/MessageDriven;".toCharArray();//$NON-NLS-1$
	
	public boolean isEJBArchive(IArchive archive) {
		// first check for the deployment descriptor
		if (archiveToJavaEEQuickPeek.containsKey(archive)) {
			JavaEEQuickPeek qp = JavaEEArchiveUtilities.INSTANCE.getJavaEEQuickPeek(archive);
			if (qp.getType() == JavaEEQuickPeek.EJB_TYPE) {
				return true;
			}
		} 

		List<IArchiveResource> archiveResources = archive.getArchiveResources();
		for (IArchiveResource archiveResource : archiveResources) {
			if (archiveResource.getType() == IArchiveResource.FILE_TYPE) {
				if (archiveResource.getPath().lastSegment().endsWith(DOT_CLASS)) {
					InputStream ioStream = null;
					try {
						ioStream = archiveResource.getInputStream();
						IClassFileReader classFileReader = ToolFactory.createDefaultClassFileReader(ioStream, IClassFileReader.CLASSFILE_ATTRIBUTES);
						IClassFileAttribute[] attributes = classFileReader.getAttributes();
						for (IClassFileAttribute attribute : attributes) {
							char[] attributeName = attribute.getAttributeName();
							if (Arrays.equals(attributeName, RUNTIME_VISIBLE)) {
								IRuntimeVisibleAnnotationsAttribute annotationsAttribute = (IRuntimeVisibleAnnotationsAttribute) attribute;
								IAnnotation[] annotations = annotationsAttribute.getAnnotations();
								for (IAnnotation annotation : annotations) {
									char[] typedName = annotation.getTypeName();
									if (Arrays.equals(typedName, STATELESS) || Arrays.equals(typedName, STATEFUL) || Arrays.equals(typedName, MESSAGEDRIVEN)) {
										return true;
									}
								}
							}
						}
					} catch (FileNotFoundException e) {
						ArchiveUtil.warn(e);
					} catch (IOException e) {
						ArchiveUtil.warn(e);
					} finally {
						if (null != ioStream) {
							try {
								ioStream.close();
							} catch (IOException e) {
								ArchiveUtil.warn(e);
							}
						}
						ioStream = null;
					}
				}
			}
		}
		return false;
	}
}